# Webstaurator v2 — Backend Architecture

Platforma food delivery klasy Uber Eats / Bolt Food zbudowana jako **architektura mikroserwisowa** na Spring Boot 4 / Java 25, wdrożona na Kubernetes.

---

## Mapa mikroserwisów

| Serwis | Port | Baza danych | Odpowiedzialność |
|---|---|---|---|
| `api-gateway` | 8080 | Redis (rate limiting) | Routing, JWT validation, rate limiting |
| `auth-service` | 8081 | PostgreSQL | JWT, 2FA, Google OAuth2, reset hasła |
| `user-service` | 8082 | PostgreSQL | Profile użytkowników, adresy |
| `restaurant-service` | 8083 | PostgreSQL | Restauracje, menu, kategorie, godziny |
| `order-service` | 8084 | PostgreSQL | Cykl życia zamówień, Kafka producer |
| `payment-service` | 8085 | PostgreSQL | PayU, historia płatności |
| `notification-service` | 8086 | MongoDB | Email, push, historia powiadomień |
| `delivery-service` | 8087 | MongoDB | Tracking kurierów, geolokalizacja |
| `review-service` | 8088 | MongoDB | Oceny, recenzje restauracji/dań |

### Dlaczego polyglot persistence?

- **PostgreSQL** — serwisy wymagające ACID i relacji (zamówienia, płatności, autoryzacja)
- **MongoDB** — elastyczny schemat (powiadomienia), dane geospatialne (delivery tracking), zagnieżdżone dokumenty (reviews z odpowiedziami)

---

## Kafka Topics

| Topic | Producer | Consumers |
|---|---|---|
| `order.created` | order-service | payment-service, notification-service |
| `order.confirmed` | order-service | delivery-service, notification-service |
| `order.prepared` | order-service | delivery-service, notification-service |
| `order.picked-up` | order-service | notification-service |
| `order.delivered` | order-service | review-service, notification-service |
| `order.cancelled` | order-service | payment-service, notification-service |
| `payment.completed` | payment-service | order-service |
| `payment.failed` | payment-service | order-service, notification-service |
| `delivery.location` | delivery-service | order-service (real-time tracking) |
| `review.restaurant-rated` | review-service | restaurant-service (aktualizacja avgRating) |

---

## Stack technologiczny

### Core
- **Spring Boot 4** / **Java 25** — virtual threads, records, sealed classes, pattern matching
- **Spring Cloud Gateway** — API Gateway z circuit breaker
- **Spring Security** — JWT + OAuth2 (Google)
- **Spring Data JPA + Flyway** — PostgreSQL (auth, user, restaurant, order, payment)
- **Spring Data MongoDB** — MongoDB (notification, delivery, review)
- **Spring Kafka** — komunikacja async przez Kafka

### Infrastruktura
- **Kafka KRaft** (latest) — message broker bez Zookeeper
- **PostgreSQL 17** — główna baza relacyjna
- **MongoDB 8** — baza dokumentowa
- **Redis 7** — cache + distributed locks + rate limiting
- **Elasticsearch 8** — wyszukiwanie restauracji i dań full-text
- **MinIO** — object storage dla zdjęć (S3-compatible)

### Observability
- **OpenTelemetry + Jaeger** — distributed tracing (śledzenie requestu przez N serwisów)
- **Prometheus + Grafana** — metryki i dashboardy
- **Spring Boot Actuator** — health checks, `/actuator/prometheus`

### Resilience
- **Resilience4j** — Circuit Breaker, Retry, Rate Limiter między serwisami
- **WebSocket** — real-time tracking zamówień w order-service

### Deployment
- **Kubernetes** — orchestracja z Helm charts
- **Docker Compose** — lokalne środowisko deweloperskie

---

## Struktura repozytorium (docelowa)

```
backend/
  api-gateway/          Spring Boot — routing, auth filter
  auth-service/         Spring Boot — JWT, OAuth2, 2FA
  user-service/         Spring Boot — profile, adresy
  restaurant-service/   Spring Boot — restauracje, menu
  order-service/        Spring Boot — zamówienia, Kafka producer
  payment-service/      Spring Boot — PayU
  notification-service/ Spring Boot — email, MongoDB
  delivery-service/     Spring Boot — tracking, MongoDB geospatial
  review-service/       Spring Boot — oceny, MongoDB
  pom.xml               Maven parent POM (wspólne zależności)
frontend/               Angular 22 + Tailwind CSS
k8s/
  namespaces/
  infrastructure/       K8s manifests dla Postgres, Mongo, Kafka, Redis
  services/             K8s manifests per serwis
  helm/webstaurator/    Umbrella Helm chart
monitoring/
  prometheus.yml
old/                    v1 (legacy — do porównania)
docker-compose.yml      Dev environment (wszystkie infrastrukturalne serwisy)
```

---

## Lokalne środowisko deweloperskie

### Start całej infrastruktury
```bash
cp .env.example .env      # uzupełnij hasła
docker-compose up -d
```

### Dostępne UI po starcie
| Serwis | URL |
|---|---|
| Kafka UI | http://localhost:8090 |
| Kibana (Elasticsearch) | http://localhost:5601 |
| Jaeger (tracing) | http://localhost:16686 |
| Grafana | http://localhost:3000 |
| MinIO Console | http://localhost:9001 |
| MailDev | http://localhost:1080 |
| Prometheus | http://localhost:9090 |

### Kubernetes (minikube / k3d)
```bash
kubectl apply -f k8s/namespaces/
helm install webstaurator k8s/helm/webstaurator/
```

---

## Fazy implementacji

### Faza 1 — Infrastruktura ✅
- [x] `docker-compose.yml` z pełną infrastrukturą deweloperską
- [x] Kubernetes namespaces + przykładowe manifesty
- [x] Helm umbrella chart skeleton
- [x] Prometheus config
- [x] Ta dokumentacja

### Faza 2 — API Gateway + Auth ✅
- [x] `api-gateway` — Spring Cloud Gateway, JWT filter (+X-User-Id header), header stripping
- [x] `auth-service` — rejestracja, login, 2FA email, Google OAuth2 (one-time code), reset hasła
- [x] Flyway migracje dla auth-service
- [x] Maven parent POM
- [x] Security: token nie w URL, nagłówki zaufania czyszczone, user enumeration fix, actuator na porcie 9090

### Faza 3 — Core domain ✅
- [x] `user-service` — profile, adresy dostawy (port 8082)
- [x] `restaurant-service` — CRUD restauracji, menu, kategorie, godziny otwarcia (port 8083)
- [x] Flyway migracje dla obu serwisów

### Faza 4 — Order flow + Kafka
- [ ] `order-service` — tworzenie zamówień, statusy, Kafka producer
- [ ] `payment-service` — PayU, Kafka consumer
- [ ] `notification-service` — Kafka consumer → email + MongoDB logi

### Faza 5 — Delivery + Reviews
- [ ] `delivery-service` — geospatial tracking na MongoDB, WebSocket
- [ ] `review-service` — oceny i recenzje, MongoDB

### Faza 6 — Observability + Resilience
- [ ] OpenTelemetry w każdym serwisie
- [ ] Resilience4j Circuit Breaker
- [ ] Elasticsearch — serwis wyszukiwania
- [ ] Grafana dashboardy
- [ ] Kubernetes HPA (autoscaling)

---

## Konwencje kodu

- DTOs jako Java `record`
- Konstruktor injection (nie `@Autowired` na polach)
- Jeden serwis = jeden kontekst domenowy (bounded context)
- Komunikacja sync: REST (zewnętrzna), gRPC (wewnętrzna — planowane)
- Komunikacja async: Kafka events
- Każdy serwis ma własne migracje Flyway / własną kolekcję MongoDB
- Testy: JUnit 5 + Testcontainers (integration), Mockito (unit)

---

## Domain-Driven Design (DDD)

Webstaurator stosuje taktyczne wzorce DDD w obrębie każdego serwisu. Architektura mikroserwisowa naturalnie pokrywa się z podziałem na **Bounded Contexts** — każdy serwis jest odrębnym kontekstem domenowym z własnym językiem ubiquitous language.

### Bounded Contexts

| Serwis | Bounded Context | Rdzeń domenowy |
|---|---|---|
| `auth-service` | Identity & Access | użytkownicy, sesje, tokeny |
| `user-service` | User Profile | profil, adresy dostawy |
| `restaurant-service` | Restaurant Catalog | restauracje, menu, kategorie |
| `order-service` | Order Management | cykl życia zamówienia |
| `payment-service` | Payments | płatności, historia |
| `notification-service` | Notifications | powiadomienia async |
| `delivery-service` | Delivery & Tracking | kurierzy, geolokalizacja |
| `review-service` | Reviews | oceny, recenzje |

Konteksty komunikują się przez zdarzenia domenowe (Kafka) — nie współdzielą bazy danych.

### Agregaty

Każdy serwis definiuje własne agregaty. Reguła: **repozytorium tylko dla korzenia agregatu** (Aggregate Root).

Przykłady:

| Serwis | Aggregate Root | Encje wewnątrz agregatu |
|---|---|---|
| `restaurant-service` | `Restaurant` | `OpeningHours` (ściśle związane z restauracją, brak sensu bez kontekstu) |
| `restaurant-service` | `Category` | `MenuItem` (kategoria zarządza swoimi pozycjami) |
| `order-service` | `Order` | `OrderItem` |
| `payment-service` | `Payment` | `PaymentEvent` |

**Decyzja projektowa**: `Category` i `MenuItem` są *oddzielnymi agregatami* (nie zagnieżdżonymi w `Restaurant`), bo:
- `MenuItem` może mieć setki rekordów — ładowanie całego menu przy każdej zmianie restauracji to antywzorzec
- Operacje na pozycjach menu (toggle dostępności, zmiana ceny) są niezależne od operacji na restauracji
- Niezmiennik agregatu jest lokalny: `MenuItem.restaurantId` musi wskazywać na restaurację właściciela — sprawdzamy to w warstwie serwisowej przed każdą mutacją

### Value Objects

Preferujemy `record` do enkapsulowania pojęć domenowych bez tożsamości:

```java
// Zamiast rozproszonych pól BigDecimal w encjach
public record Money(BigDecimal amount, String currency) {
    public Money {
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Amount cannot be negative");
    }
    public Money add(Money other) { return new Money(amount.add(other.amount), currency); }
}

// Adres jako spójny Value Object
public record Address(String street, String city, String postalCode) {}
```

Docelowe Value Objects do wydzielenia:
- `Money` — ceny (deliveryFee, minOrderAmount, menuItem.price)
- `Address` — adres restauracji
- `OpeningTimeSlot` — (dayOfWeek, openTime, closeTime)
- `GeoPoint` — (latitude, longitude) dla delivery-service

### Zdarzenia domenowe (Domain Events)

Kafka topics = zdarzenia domenowe między kontekstami. Każde zdarzenie ma: typ, identyfikator agregatu, payload, timestamp.

```java
// Przykładowe zdarzenie — OrderCreatedEvent (order-service → payment-service)
public record OrderCreatedEvent(
    String eventId,
    Long orderId,
    Long customerId,
    Long restaurantId,
    BigDecimal totalAmount,
    Instant occurredAt
) {}
```

Aktualne zdarzenia (patrz sekcja Kafka Topics):

| Zdarzenie | Producent | Konsumenci |
|---|---|---|
| `order.created` | order-service | payment-service, notification-service |
| `order.delivered` | order-service | review-service, notification-service |
| `payment.completed` | payment-service | order-service |

Zasada: zdarzenie opisuje **co się stało** (past tense), nie polecenie. `OrderCreated` — tak. `CreateOrder` — nie.

### Struktura pakietów wewnątrz serwisu

Stosujemy podział na warstwy zgodny z DDD, unikając pakietów technicznych (`model/`, `repository/`) jako jedynego poziomu organizacji:

```
com.gosqu.<service>/
  domain/
    model/          — encje, Value Objects, Aggregate Roots
    event/          — zdarzenia domenowe (rekordy publikowane przez Kafka)
    exception/      — wyjątki domenowe (RestaurantNotFoundException, itp.)
    repository/     — interfejsy repozytoriów (kontrakt, nie implementacja)
  application/
    service/        — Application Services (orkiestracja use-case'ów)
    dto/
      request/      — DTO wejściowe (rekordy z Bean Validation)
      response/     — DTO wyjściowe (rekordy z fabryką from())
  infrastructure/
    persistence/    — implementacje JPA repozytoriów (jeśli oddzielone od interfejsu)
    messaging/      — Kafka producers / consumers
    config/         — SecurityConfig, konfiguracja Spring
    controller/     — REST kontrolery (cienka warstwa adaptacyjna)
```

Obecne serwisy (auth, restaurant) mają płaski podział — **migracja do tej struktury będzie incremental**, przy dodawaniu nowych serwisów (order, payment) stosujemy już ten layout od razu.

### Application Service vs Domain Service

| Rodzaj | Co robi | Przykład |
|---|---|---|
| **Application Service** | Orkiestruje: wczytaj agregat, wykonaj domenową operację, zapisz, opublikuj zdarzenie | `RestaurantService.create()` |
| **Domain Service** | Logika domenowa nieprzynależąca do jednego agregatu, bez infrastruktury | `PricingService.applyDeliveryDiscount(order, restaurant)` |

W obecnych serwisach klasy `*Service` pełnią rolę Application Services. Domain Services pojawią się gdy logika biznesowa przekroczy granicę jednego agregatu (np. walidacja minimów zamówienia łącząca dane z restaurant-service i order-service).

### Anti-Corruption Layer

Każdy serwis konsumujący dane z innego serwisu (przez REST lub Kafka) definiuje własne DTO — nie współdzieli modelu domenowego. Przykład: `order-service` nie importuje encji z `restaurant-service`; pobiera dane przez REST i mapuje na swój wewnętrzny `RestaurantSnapshot`.

### Priorytety DDD na kolejne fazy

- **Faza 4 (order-service)**: stosujemy pełną strukturę pakietów DDD od początku; `Order` jako bogaty agregat z metodami domenowymi zamiast anemic model
- **Faza 5 (review-service)**: zdarzenie `order.delivered` → domain event consumer → tworzenie `Review` (Event-Driven DDD)
- **Refaktor restaurant-service**: wydzielenie Value Objects (`Money`, `Address`), testy przed refaktorem

---

## Media Storage (S3 / floci)

Upload avatara (`user-service`) i loga restauracji (`restaurant-service`) idzie bezpośrednio pod
`software.amazon.awssdk:s3`, bez pośredniej warstwy (MinIO SDK celowo pominięty — patrz
`backend/md/todo-v2/08-floci-eks-s3.md`).

- **Dev**: `floci` (kontener w `docker-compose.yml`, port 4566) emuluje S3 lokalnie, bez konta AWS.
- **Prod/EKS**: `aws.s3.endpoint` puste → SDK używa prawdziwego AWS i domyślnego łańcucha
  poświadczeń (docelowo IRSA — ServiceAccount z adnotacją `eks.amazonaws.com/role-arn`).
- W bazie danych (`Restaurant.logoUrl`, `UserProfile.avatarUrl`) przechowywany jest **klucz obiektu
  S3**, nie publiczny URL. Wyświetlany link to zawsze świeżo wygenerowany presigned URL
  (`GET /restaurants/{id}/logo/url`, `GET /users/me/avatar/url`), ważny domyślnie 15 minut
  (`aws.s3.presign-ttl-minutes`) — bucket pozostaje prywatny.
- Terraform (`infra/terraform/local/main.tf`) tworzy bucket `webstaurator-media` na floci; skrypt
  `k8s/scripts/floci-eks-bootstrap.sh` stawia klaster EKS na floci i wdraża na nim manifesty z
  `k8s/`.
