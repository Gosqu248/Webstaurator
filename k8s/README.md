# Kubernetes dla Webstaurator — przewodnik od zera

Ten plik tłumaczy **co** leży w katalogu `k8s/` i **dlaczego** jest tak, a nie inaczej. Zakładam, że
nie znasz Kubernetesa — każda sekcja zaczyna się od wyjaśnienia pojęcia, zanim odniesie się do
konkretnych plików w tym repo.

---

## 1. Mapa katalogów

```
k8s/
├── namespaces/
│   └── webstaurator.yaml          — definicje 3 namespace'ów (patrz sekcja 2)
├── infrastructure/                — stanowa infrastruktura współdzielona (bazy danych, kolejka, wyszukiwarka)
│   ├── postgres/
│   ├── mongodb/
│   ├── redis/
│   ├── kafka/
│   ├── elasticsearch/
│   ├── minio/                     — obecny, ale NIEUŻYWANY (patrz sekcja 4, tabela zależności)
│   └── network-policies/
│       └── default-deny-ingress.yaml
├── services/                      — 9 mikroserwisów aplikacji + wspólne polityki
│   ├── api-gateway/
│   ├── auth-service/
│   ├── user-service/
│   ├── restaurant-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── notification-service/
│   ├── delivery-service/
│   ├── review-service/
│   └── network-policies/
│       └── default-deny-ingress.yaml
├── ingress/
│   └── webstaurator-ingress.yaml  — jedyny punkt wejścia z zewnątrz klastra
└── helm/
    └── webstaurator/              — Helm chart pakujący 9 serwisów z services/ (patrz sekcja 7)
```

Każdy katalog serwisu/infrastruktury ma ten sam wzorzec plików: `deployment.yaml` (lub
`statefulset.yaml`), `service.yaml`, `configmap.yaml`, `secret.yaml`/`secret.example.yaml`,
`pdb.yaml`, `hpa.yaml` (gdzie dotyczy), `network-policy.yaml`. Jak już rozumiesz jeden serwis,
rozumiesz wszystkie — struktura jest celowo powtarzalna.

**`secret.yaml` vs `secret.example.yaml`**: `secret.example.yaml` to commitowany szablon z
placeholderami (`CHANGE_ME` itp.) — dokumentuje, jakich kluczy oczekuje dany serwis, bez ujawniania
wartości. `secret.yaml` to jego wypełniona kopia z realnymi (na razie dev-owymi) wartościami —
wpisana do `k8s/.gitignore` (`k8s/**/secret.yaml`), więc nigdy nie trafia do repo. Przed pierwszym
`kubectl apply` na danym serwisie kopiuje się `secret.example.yaml` → `secret.yaml` i uzupełnia
realne wartości (albo w produkcji: zarządza nimi zewnętrzny sekret-manager, a nie plik na dysku).

**Uwaga — Helm to inna ścieżka niż surowe manifesty.** Podcharty w `helm/webstaurator/charts/<serwis>/values.yaml`
też mają blok `secret.stringData` z placeholderami `CHANGE_ME`, ale te pliki **nie są objęte** regułą
`k8s/**/secret.yaml` z `.gitignore` — są commitowane jak każdy inny plik Helm chart (to jego szablon, analogiczny
do `secret.example.yaml`). Realnych wartości nigdy nie wpisuje się do tych plików. Zamiast tego, przy
`helm install`/`helm upgrade` podaje się osobny plik z prawdziwymi sekretami przez `-f prod-values.yaml`
(plik spoza repo, albo zarządzany przez zewnętrzny sekret-manager) — Helm scala go z domyślnymi wartościami
z podchartu, nadpisując tylko `secret.stringData`.

---

## 2. Namespace'y — po co trzy, a nie jeden

**Namespace** to logiczna "przegroda" w klastrze — osobna przestrzeń nazw dla obiektów (Deploymentów,
Service'ów, Secretów...). Dwa obiekty w różnych namespace'ach mogą nazywać się tak samo i nie
kolidują. Namespace to *nie* jest izolacja bezpieczeństwa sama w sobie — to zapewnia dopiero
`NetworkPolicy` (sekcja 4).

Trzy namespace'y w `namespaces/webstaurator.yaml`:

| Namespace | Co w nim żyje | Dlaczego osobno |
|---|---|---|
| `webstaurator` | 9 serwisów aplikacji (`services/`) | To jest kod, który zmienia się najczęściej — deploye, rollouty, skalowanie |
| `webstaurator-infra` | Postgres, MongoDB, Redis, Kafka, Elasticsearch, MinIO | Infrastruktura stanowa, inny cykl życia (rzadkie zmiany, ostrożne upgrade'y) |
| `webstaurator-monitoring` | (zarezerwowany, jeszcze nie wdrożony — temat `06-prometheus-grafana-mid-senior.md`) | Prometheus/Grafana będą tu, gdy ten temat wyląduje |

**Jak NetworkPolicy widzi namespace inny niż swój:** Kubernetes automatycznie nadaje każdemu
namespace'owi etykietę `kubernetes.io/metadata.name: <nazwa>`. Dzięki temu polityka w
`webstaurator-infra` może napisać:

```yaml
- namespaceSelector:
    matchLabels:
      kubernetes.io/metadata.name: webstaurator
  podSelector:
    matchLabels:
      app: auth-service
```

To znaczy: "wpuść ruch tylko z podów o etykiecie `app: auth-service`, które siedzą w namespace
`webstaurator`". Bez `namespaceSelector` sam `podSelector` szukałby `app: auth-service` **w tym samym
namespace**, w którym jest polityka — a auth-service jest gdzie indziej, więc reguła byłaby martwa.
To jest dokładnie ten mechanizm, który spina cały graf zależności w sekcji 4.

---

## 3. Deployment vs StatefulSet — dlaczego cała infrastruktura to teraz StatefulSet

### Deployment
`Deployment` zarządza podami bez tożsamości — każdy pod ma losową nazwę (`api-gateway-7f9c8d-xk2p1`)
i losowy IP. Jeśli pod umrze, nowy dostaje nową nazwę/IP. To jest w porządku dla usług
bezstanowych — 9 serwisów aplikacji w `services/` to wszystko `Deployment`, bo nie trzymają danych
lokalnie i nie zależy im, który konkretnie pod obsłuży request.

### StatefulSet
`StatefulSet` daje każdemu podowi **stabilną tożsamość**: nazwę `<nazwa>-0`, `<nazwa>-1`, `<nazwa>-2`
(zamiast losowego hasha) i stabilny adres DNS przez **headless Service** (`clusterIP: None`) —
`kafka-0.kafka-headless.webstaurator-infra.svc.cluster.local`. Po restarcie pod wraca z tą samą
nazwą i tym samym wolumenem danych. To jest wymagane, gdy:
- proces musi wiedzieć "kim jest" (Kafka: każdy broker ma numeryczne `node.id`)
- inne repliki muszą móc go znaleźć po stałym adresie (Kafka: kworum kontrolerów, Elasticsearch: discovery klastra)
- każda replika ma **własne, osobne dane** — nie jeden współdzielony dysk (`volumeClaimTemplates`
  zamiast jednego `PersistentVolumeClaim` wskazywanego przez wszystkie repliki)

Dlatego `postgres`, `mongodb`, `redis`, `minio`, `kafka`, `elasticsearch` w `infrastructure/` to
teraz wszystko `StatefulSet` (wcześniej część z nich była błędnie `Deployment` z jednym współdzielonym
PVC — patrz sekcja "Co było naprawione" niżej).

### Kafka jako najbardziej pouczający przykład

`infrastructure/kafka/statefulset.yaml` to 3-brokerowy klaster w trybie **KRaft** (Kafka Raft — nowy
mechanizm konsensusu, zastępuje ZooKeepera od Kafki 4.x; ten obraz nie ma i nie potrzebuje
ZooKeepera). Trzy rzeczy, które sprawiają, że to nie jest zwykły StatefulSet:

1. **`KAFKA_NODE_ID` musi być liczbą całkowitą**, a Kubernetes-owy `fieldRef: metadata.name` dałby
   string `"kafka-0"`. Rozwiązanie — nadpisany `command`/`args` kontenera, które przed
   uruchomieniem właściwego procesu Kafki wyciągają numer z nazwy poda przez shell:
   ```bash
   export KAFKA_NODE_ID=${POD_NAME##*-}   # "kafka-0" -> "0"
   exec /etc/kafka/docker/run              # oryginalny entrypoint obrazu apache/kafka
   ```
   (`POD_NAME` to zmienna środowiskowa wstrzyknięta przez `fieldRef: metadata.name` — patrz env
   niżej w tym samym pliku).
2. **`KAFKA_CONTROLLER_QUORUM_VOTERS`** — trzej brokerzy muszą się wzajemnie znać z góry, żeby
   uzgodnić kworum: `0@kafka-0.kafka-headless...:9093,1@kafka-1...,2@kafka-2...`. To działa tylko
   dzięki stabilnym nazwom DNS ze StatefulSet + headless Service.
3. **`KAFKA_ADVERTISED_LISTENERS`** musi wskazywać na adres *tego konkretnego* brokera (nie na
   wspólny load-balancer `kafka:9092`), inaczej klienci łączący się przez zwykły Service dostaną
   błędne instrukcje przekierowania. Użyta jest tu natywna **k8s "dependent env var" substitution**
   — składnia `$(POD_NAME)` w wartości innej zmiennej env, rozwiązywana przez kubelet przy starcie
   poda (działa tylko wewnątrz jednej listy `env:`, nie przez `envFrom`).

Elasticsearch (`infrastructure/elasticsearch/statefulset.yaml`) ma analogiczny problem uproszczony —
`discovery.seed_hosts` + `cluster.initial_master_nodes` wskazują na te same 3 stabilne nazwy
(`elasticsearch-0.elasticsearch-headless`, ...), żeby węzły odnalazły się i sformowały klaster
zamiast działać jako 3 osobne, nieświadome siebie instancje (`discovery.type: single-node` zostało
usunięte — było poprawne tylko dla starej wersji z 1 replikaą).

**Wzorzec headless + zwykły Service, powtórzony w każdym StatefulSet:** headless Service
(`clusterIP: None`) daje stabilne DNS per-pod (do komunikacji między-replikami), a zwykły Service
(`ClusterIP`) daje jeden adres z load-balancingiem (do połączeń klienckich z `services/`). Oba
istnieją równolegle dla Kafki, Elasticsearch, Postgresa, MongoDB, Redisa i MinIO.

---

## 4. NetworkPolicy — model default-deny + jawne zezwolenia

### Model

Domyślnie w Kubernetesie **każdy pod może rozmawiać z każdym** — brak `NetworkPolicy` to brak
jakichkolwiek ograniczeń. `NetworkPolicy` z pustym `podSelector: {}` i bez reguł `ingress` znaczy
"zablokuj cały przychodzący ruch do wszystkich podów w tym namespace" — to jest
`default-deny-ingress.yaml`, obecny **osobno w obu namespace'ach aplikacyjnych**:
`infrastructure/network-policies/default-deny-ingress.yaml` (namespace `webstaurator-infra`) i
`services/network-policies/default-deny-ingress.yaml` (namespace `webstaurator`).

Od tego punktu **każdy** ruch musi być jawnie dozwolony osobną `NetworkPolicy` per cel (per serwis
lub per komponent infrastruktury). To jest odwrócenie domyślnej otwartości Kubernetesa — model
"zero trust": pod nie widzi niczego, dopóki ktoś jawnie nie napisze reguły.

### Pełny graf zależności

**REST (serwis → serwis)**, zweryfikowany w kodzie (`*_SERVICE_URL` w configmapach + faktyczne wołania w Javie):

| Wołający | Woła (REST) |
|---|---|
| `api-gateway` | wszystkie 8 pozostałych (trasy gatewaya do backendu) |
| `order-service` | `user-service`, `restaurant-service` |
| `review-service` | `restaurant-service` |
| `notification-service` | `auth-service` (nie user-service — `UserClient.java` faktycznie woła `services.auth-service.url`, mimo że wcześniejszy audyt sugerował inaczej) |
| pozostałe (auth, user, restaurant, payment, delivery) | nic — nie wołają żadnego innego serwisu po REST |

**Infrastruktura** (z `application.properties` każdego serwisu):

| Infra | Port | Kto się łączy |
|---|---|---|
| PostgreSQL | 5432 | auth, user, restaurant, order, payment |
| MongoDB | 27017 | review, notification, delivery |
| Redis | 6379 | api-gateway, auth, user, restaurant, order, payment, notification |
| Kafka | 9092 | restaurant, order, payment, notification, delivery, review |
| Elasticsearch | 9200 | restaurant (tylko) |
| MinIO | 9000/9001 | **nikt** — świadomie bez `NetworkPolicy`, pozostaje całkowicie zablokowany przez `default-deny`. Brak `MinioClient`/`S3Client` gdziekolwiek w repo — martwa infrastruktura, nieużywana |

**Dodatkowe reguły self-to-self** (ruch między replikami tej samej infrastruktury, wymagany żeby
klaster w ogóle się sformował):
- Kafka: port 9092 (replikacja inter-broker) + 9093 (kworum kontrolerów KRaft) między podami `app: kafka`
- Elasticsearch: port 9300 (transport, discovery klastra) między podami `app: elasticsearch`

**Reguła metryk (dla przyszłego Prometheusa)**: każdy serwis aplikacji ma drugą regułę `ingress`
zezwalającą na port metryk (909x, `/actuator/prometheus`) z `namespaceSelector:
{kubernetes.io/metadata.name: webstaurator-monitoring}`. Prometheus tam jeszcze nie stoi (temat 06,
poza zakresem tego zlecenia), ale polityka jest gotowa, żeby scraping nie został po cichu
zablokowany, gdy Prometheus wyląduje.

**`api-gateway`** dostaje wyjątek: `ingress: - {}` (zezwól na cały ruch przychodzący), bo wchodzi z
zewnątrz przez Ingress Controller (`ingress/webstaurator-ingress.yaml`), którego etykiet podów nie
znamy z góry. To jedyne świadome odejście od modelu default-deny — bo to jest właśnie ta brama, którą
reszta polityk wymusza jako jedyną drogę do serwisów wewnętrznych.

### Co jest zweryfikowane statycznie, a co wymaga żywego klastra

To jest najważniejsza sekcja tego dokumentu dla uczciwej oceny, na ile można ufać temu, co tu jest.

**Zweryfikowane bez klastra (w tej sesji):**
- Składnia YAML wszystkich 95 plików (`kubectl apply --dry-run=client --validate=false` — parsuje
  się poprawnie, żaden plik nie ma błędu składniowego)
- Spójność grafu REST z `NetworkPolicy` (skrypt statyczny, patrz sekcja 9) — każdy `*_SERVICE_URL` z
  configmapy ma odpowiadającą regułę `podSelector` w polityce celu
- Spójność namespace'ów między `pdb.yaml`/`network-policy.yaml`/`hpa.yaml` a sąsiednim
  `deployment.yaml`/`statefulset.yaml`
- `helm lint` i `helm template` (patrz sekcja 7) — poprawność szablonów Helm i zgodność
  wygenerowanego outputu z surowymi manifestami

**NIE zweryfikowane — wymaga żywego klastra z CNI egzekwującym NetworkPolicy** (Calico lub Cilium —
**NIE** `kindnet`, domyślny CNI w `kind`, i **NIE** Docker Desktop Kubernetes w trybie standardowym —
oba ignorują `NetworkPolicy` po cichu, czyli polityki "działają" składniowo, ale nic realnie nie
blokują):
- Czy Kafka faktycznie formuje kworum KRaft (3 brokery widzą się nawzajem przez DNS + reguła
  self-to-self na 9092/9093 rzeczywiście przepuszcza ruch)
- Czy Elasticsearch faktycznie tworzy klaster 3-węzłowy (discovery przez port 9300)
- Czy sondy `livenessProbe`/`readinessProbe` (`kubelet` → pod, ruch **z węzła**, nie z innego poda)
  przeżywają `default-deny` — teoretycznie tak (NetworkPolicy nie blokuje ruchu od kubeletu), ale
  nie zweryfikowane empirycznie na tym klastrze
- Zachowanie `PodDisruptionBudget` przy `kubectl drain` (sekcja 5)
- Czy `kubectl apply --dry-run=client --validate=false` przepuściłoby błąd **schematu** (zła nazwa
  pola, zły typ) — flaga `--validate=false` wyłącza właśnie tę walidację przeciw schematowi API,
  sprawdza tylko, czy YAML się parsuje. Literówka w nazwie pola (np. `voluemClaimTemplates`)
  **przeszłaby tę kontrolę bez błędu**. Realna walidacja schematu wymaga albo żywego klastra, albo
  narzędzia typu `kubeconform`/`yamllint` (żadne nie jest zainstalowane w tym środowisku — sprawdzone
  i potwierdzone jako brakujące)
- Obraz `apache/kafka:4.3.1` — mechanizm entrypointa (`/etc/kafka/docker/run`,
  `/__cacert_entrypoint.sh`) zweryfikowany empirycznie na tagu `apache/kafka:latest` (`docker
  inspect`/`docker run`), **nie** na `4.3.1` konkretnie — `docker pull apache/kafka:4.3.1` nie
  dokończył się w tej sesji (bardzo wolne pobieranie, przerwane po przekroczeniu rozsądnego czasu
  oczekiwania). Przed pierwszym realnym deployem warto dociągnąć obraz i powtórzyć
  `docker inspect`/`docker run --entrypoint sh` na dokładnie tym tagu, żeby potwierdzić, że struktura
  plików się nie zmieniła

---

## 5. PodDisruptionBudget (PDB) — różnica względem probes

**Probes** (`livenessProbe`/`readinessProbe`) odpowiadają na pytanie "czy ten jeden pod jest zdrowy
*teraz*" — Kubernetes restartuje niezdrowy pod albo przestaje kierować do niego ruch.

**PodDisruptionBudget** odpowiada na inne pytanie: "ile podów tego serwisu **wolno jednocześnie
usunąć** podczas *dobrowolnej* przerwy" (aktualizacja węzła, `kubectl drain`, autoskalowanie klastra
w dół) — **nie** chroni przed awarią (crash, OOM), tylko przed samym Kubernetesem/administratorem
usuwającym za dużo podów naraz.

Wszystkie 9 serwisów w `services/*/pdb.yaml` ma `minAvailable: 1`, co jest bezpieczne, bo każdy
serwis ma `replicas: 2` w swoim Deploymencie (zweryfikowane) — nigdy nie ma ryzyka, że drenaż węzła
zawiesi się w nieskończoność czekając na PDB, którego nie da się spełnić.

**Jak przetestować na żywym klastrze:**
```bash
kubectl get pdb -n webstaurator
# Wymuś drenaż węzła, na którym stoi replika serwisu:
kubectl drain <node-name> --ignore-daemonsets --delete-emptydir-data
# Obserwuj: Kubernetes usunie co najwyżej 1 replikę na raz z serwisu z minAvailable: 1,
# czekając aż nowa wstanie na innym węźle, zanim usunie kolejną.
```

---

## 6. HorizontalPodAutoscaler (HPA) — automatyczne skalowanie

`HorizontalPodAutoscaler` zwiększa/zmniejsza liczbę replik Deploymentu na podstawie metryki —
tutaj: **CPU**. Wszystkie 9 serwisów aplikacji ma identyczny wzorzec
(`services/*/hpa.yaml`):

```yaml
minReplicas: 2
maxReplicas: 10
metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 70
```

Znaczenie: jeśli średnie zużycie CPU replik przekroczy 70% zadeklarowanego `requests.cpu` (z
`deployment.yaml`), Kubernetes dokłada repliki (do max 10); jeśli spadnie znacząco poniżej — zabiera
(do min 2). `minReplicas: 2` jest spójne z `PodDisruptionBudget minAvailable: 1` z sekcji 5 — zawsze
zostaje przynajmniej 1 pod nawet przy najniższym skalowaniu i jednoczesnym drenażu.

**Co dalej (poza zakresem tego zlecenia):** dla `notification-service`, który konsumuje z Kafki,
metryka CPU jest słabym proxy dla realnego obciążenia — lepsza byłaby metryka custom (lag konsumenta
Kafki, przez np. KEDA albo Prometheus Adapter). To zostało odnotowane jako "stretch" w oryginalnym
todo i świadomie pominięte tutaj.

---

## 7. Helm — po co i jak

**Helm** to menedżer pakietów dla Kubernetesa — zamiast pisać 8 plików YAML per serwis ręcznie,
pisze się **szablon** (`templates/*.yaml` z placeholderami `{{ .Values.xxx }}`) raz, a konkretne
wartości (nazwa obrazu, liczba replik, porty...) trzyma się osobno w `values.yaml`. `helm template`
generuje z tego gotowe manifesty — identyczne w kształcie do tych w `services/`, ale sparametryzowane.

**Wybrana struktura — umbrella chart z podchartami per serwis**
(`helm/webstaurator/Chart.yaml` deklaruje 9 zależności `file://charts/<serwis>`), a nie jeden
uniwersalny szablon z pętlą `range` po liście serwisów. Przy 9 serwisach obie struktury dają podobny
nakład pracy — przepisanie na "jeden szablon + range" oznaczałoby dodatkowo przepisanie już
istniejących `Chart.yaml`/`values.yaml`, bez wyraźnej korzyści na tej skali. To odzwierciedla też
inny cykl życia: infrastruktura (`infrastructure/`) zmienia się rzadko i ostrożnie, więc zostaje
surowymi manifestami — `values.yaml` referuje ją tylko jako `global.*.host` (DNS), nie jako
deployowalny komponent chartu — a serwisy aplikacji zmieniają się często, więc dostają Helm.

**Struktura każdego z 9 podchartów** (`helm/webstaurator/charts/<serwis>/`):
- `Chart.yaml` — minimalny opis (`name`, `version`)
- `values.yaml` — wartości domyślne: porty, `resources`, klucze `configMap.data` / `secret.stringData`
  (sekrety jako placeholdery `CHANGE_ME` — realne wartości nadpisuje się osobnym, niecommitowanym
  plikiem values przy `helm install -f`, tak jak `services/<serwis>/secret.example.yaml` dziś)
  i `networkPolicy.ingress` (dokładnie ten sam graf `from`/`ports`, co w surowym
  `services/<serwis>/network-policy.yaml`)
- `templates/{deployment,service,configmap,secret,pdb,hpa,networkpolicy}.yaml` — 7 identycznych w
  kształcie szablonów Go, sparametryzowanych przez `.Values.*` i `.Values.global.*` z `values.yaml`
  umbrelli

**Zweryfikowane:** `helm dependency update` buduje 9 podchartów z lokalnych katalogów (`file://charts/...`),
`helm lint` przechodzi bez błędów, `helm template` renderuje 63 zasoby (9 × 7 typów) bez błędów
wykonania szablonów. Wyrenderowany output porównano programowo z surowymi manifestami z `services/`
dla wszystkich 9 serwisów — **zero rozbieżności** w kluczach `configMap`/`secret` ani w grafie
`NetworkPolicy` (`from`/`ports`). Warto posłużyć się tym samym porównaniem (`helm template` + diff)
przy każdej zmianie w `charts/` lub `values.yaml`, żeby złapać rozjazd zanim trafi na klaster.

`helm dependency update` tworzy `charts/*.tgz` i `Chart.lock` — to zbudowane artefakty (jak `target/`
czy `node_modules/`), nie źródło; `helm/webstaurator/.gitignore` je pomija.

**Komendy do wypróbowania:**
```bash
helm dependency update helm/webstaurator  # zbuduj podcharty z file://charts/<serwis> (raz, po każdej zmianie w charts/)
helm lint helm/webstaurator               # statyczna walidacja szablonów, bez klastra
helm template helm/webstaurator           # wygeneruj finalne manifesty na stdout
helm template helm/webstaurator | diff - <(cat services/api-gateway/*.yaml)  # porównanie ręczne
helm install webstaurator helm/webstaurator --namespace webstaurator --dry-run  # wymaga klastra
```

---

## 8. Wymagania zasobowe — uwaga przed pierwszym uruchomieniem

3 brokery Kafki + 3 węzły Elasticsearch to **6 dodatkowych podów infrastruktury**, zanim jakikolwiek
pod aplikacji w ogóle wystartuje. Każdy węzeł Elasticsearch ma `ES_JAVA_OPTS: "-Xms512m -Xmx512m"`
(sam heap JVM, nie licząc narzutu kontenera) — w praktyce to kilka GB RAM tylko na samą
infrastrukturę stanową, zanim doliczysz Postgres/MongoDB/Redis/MinIO i 9× serwis aplikacji (każdy
`requests.memory: 256Mi` w typowym przypadku, razy min. 2 repliki = 18 podów bazowych).

Jeśli testujesz lokalnie na Docker Desktop Kubernetes albo `kind` — **podnieś limit RAM w ustawieniach
Dockera** (Docker Desktop → Settings → Resources) przed `kubectl apply -f .` na całym katalogu, albo
świadomie aplikuj tylko część na raz (np. bez 3-replikowego Elasticsearch, zamień chwilowo na 1
replikę do testów lokalnych).

---

## 9. Jak sprawdzić, że to działa

### Bez klastra (statycznie, już wykonane w tej sesji)

```bash
cd k8s
# Składnia YAML surowych manifestów (BEZ helm/ — szablony Go z {{ }} nie są czystym YAML,
# kubectl by je odrzucił mimo że są poprawne dla Helm):
for f in $(find infrastructure services namespaces ingress -name "*.yaml"); do
  kubectl apply --dry-run=client --validate=false -f "$f" || echo "BŁĄD: $f"
done

# Helm (osobna walidacja — helm rozumie {{ }}, kubectl nie):
helm dependency update helm/webstaurator
helm lint helm/webstaurator
helm template helm/webstaurator > /tmp/helm-output.yaml

# Spójność namespace'ów (PDB/NetworkPolicy/HPA vs Deployment/StatefulSet) i grafu REST-NetworkPolicy —
# skrypt ad-hoc użyty w tej sesji (nie wchodzi do repo, ale wzorzec jest odtwarzalny):
# 1. dla każdego <caller> z <TARGET>_SERVICE_URL w services/<caller>/configmap.yaml,
#    sprawdź że services/<target>/network-policy.yaml zawiera `app: <caller>` w podSelector
# 2. sprawdź że namespace w każdym pdb.yaml/network-policy.yaml/hpa.yaml zgadza się z namespace
#    w sąsiednim deployment.yaml/statefulset.yaml
```

### Z żywym klastrem (Calico/Cilium jako CNI — nie kindnet, nie Docker Desktop standardowo)

```bash
kubectl apply -f namespaces/webstaurator.yaml
kubectl apply -f infrastructure/network-policies/default-deny-ingress.yaml
kubectl apply -f infrastructure/ --recursive
kubectl apply -f services/network-policies/default-deny-ingress.yaml
kubectl apply -f services/ --recursive
kubectl apply -f ingress/

# Czy wszystko wstało:
kubectl get pods -n webstaurator -w
kubectl get pods -n webstaurator-infra -w

# Czy Kafka sformowała kworum (3/3 gotowe):
kubectl get pods -n webstaurator-infra -l app=kafka

# Czy Elasticsearch sformował klaster:
kubectl exec -n webstaurator-infra elasticsearch-0 -- curl -s localhost:9200/_cluster/health

# Czy NetworkPolicy faktycznie coś blokuje (spróbuj połączyć się skądś nieuprawnionego):
kubectl run test-pod --rm -it --image=busybox -n webstaurator -- wget -qO- --timeout=3 postgres.webstaurator-infra:5432
# (powinno się zawiesić/nie połączyć — test-pod nie ma etykiety app: auth-service itp.)

# PDB pod obciążeniem:
kubectl get pdb -n webstaurator
kubectl drain <node> --ignore-daemonsets --delete-emptydir-data

# HPA:
kubectl get hpa -n webstaurator -w
```

---

## 10. Co dalej / czego świadomie nie zrobiono w tym zleceniu

- **Custom-metric HPA** dla `notification-service` (lag konsumenta Kafki zamiast CPU) — wymaga KEDA
  albo Prometheus Adapter, poza zakresem
- **mTLS między serwisami** (np. service mesh: Istio/Linkerd) — dziś ruch wewnątrz klastra jest
  plaintext, ograniczony tylko przez `NetworkPolicy` (L3/L4, nie L7/kryptografia)
- **Postgres HA** — dziś `replicas: 1` w StatefulSet; prawdziwa wysoka dostępność (streaming
  replication, automatyczny failover) wymaga operatora typu Patroni albo CloudNativePG
- **`webstaurator-monitoring`** — namespace istnieje, ale Prometheus/Grafana jeszcze tam nie stoją
  (temat `06-prometheus-grafana-mid-senior.md`); reguły `NetworkPolicy` na port metryk są już gotowe
  na to wdrożenie
- **Egress NetworkPolicy** — ten zestaw polityk kontroluje tylko `Ingress` (kto może *wejść* do poda).
  Egress (dokąd pod może *wyjść*) nie jest ograniczony — wymagałoby to dodatkowo jawnej reguły dla
  DNS (`kube-dns`, port 53), inaczej żaden pod nie rozwiąże żadnej nazwy. Poza zakresem tego zlecenia.
- **Realny test pod CNI egzekwującym polityki** — patrz sekcja 4, "NIE zweryfikowane"
- **Potwierdzenie obrazu `apache/kafka:4.3.1`** — pull nie dokończył się w tej sesji, patrz sekcja 4
