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
- **Kafka 3.9 KRaft** — message broker bez Zookeeper
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

### Faza 2 — API Gateway + Auth
- [ ] `api-gateway` — Spring Cloud Gateway, JWT filter, rate limiting
- [ ] `auth-service` — rejestracja, login, 2FA email, Google OAuth2, reset hasła
- [ ] Flyway migracje dla auth-service
- [ ] Maven parent POM

### Faza 3 — Core domain
- [ ] `user-service` — profile, adresy dostawy
- [ ] `restaurant-service` — CRUD restauracji, menu, kategorie, godziny otwarcia
- [ ] Flyway migracje

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
