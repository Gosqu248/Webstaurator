# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Structure

```
old/        — v1 of the project (legacy code for comparison)
  backend/
  frontend/
  gateway/
  docker-compose.yml
```

The new v2 modules will be added at the root level as they are created.

## Project Overview

Webstaurator is an online food ordering and restaurant management platform. Users can browse menus, place orders, and pay via PayU or cash. Restaurant administrators manage menus and orders in real time. A system administrator oversees the whole platform.

**Three user roles:** Customer, Restaurant Administrator, System Administrator.

## Tech Stack

- **Frontend:** Angular 19, Angular Material, Leaflet.js, RxJS — located in `frontend/`
- **Backend:** Spring Boot 3.3.2 / Java 21, Spring Data JPA, Spring Security (JWT + OAuth2) — located in `backend/`
- **Database:** PostgreSQL 17 with Flyway migrations
- **Gateway:** Nginx reverse proxy — config in `gateway/nginx.conf`
- **Dev mail:** MailDev container (SMTP :1025, web UI :1080)

## Development Commands

### Full stack (Docker)
```bash
docker-compose up -d              # start all services
docker-compose up --build -d      # rebuild and start
```
App available at `http://localhost`.

### Frontend (standalone)
```bash
cd frontend
npm install
npm start         # ng serve — dev server on :4200
npm run build     # production build
npm test          # Karma/Jasmine unit tests
```

### Backend (standalone)
```bash
cd backend
./mvnw spring-boot:run            # run with embedded Tomcat on :8080
./mvnw clean package              # build JAR
./mvnw test                       # run all tests
./mvnw test -Dtest=AuthTests      # run a single test class
```
Requires a running PostgreSQL instance (use `docker-compose up db` to start only the DB).

## Architecture

### Request routing (Nginx)
- `/api/*` → strips `/api` prefix, proxies to `backend:8080`
- `/img/*` → strips `/img` prefix, proxies to `backend:8080` (static images)
- `/*` → proxied to `frontend:4200`

The Angular `environment.api` is set to `'/api'` so all HTTP calls go through the gateway.

### Backend layers (`pl.urban.backend`)
```
config/       — SecurityConfig (JWT filter + OAuth2), FlywayConfig, MailConfig, AppConfig
controller/   — REST endpoints (thin; delegate everything to services)
service/      — business logic
repository/   — Spring Data JPA interfaces
model/        — JPA entities
dto/          — request/response DTOs
enums/        — shared enumerations
```

### Authentication flow
1. Email/password login → email 2FA code → `POST /api/auth/verify-2fa` → JWT stored in `localStorage`
2. Google OAuth2 → redirects through `/oauth2/authorization/google` → success handler returns JWT as query param
3. JWT sent as `Authorization: Bearer <token>` header on protected calls
4. Tokens: access (24 h) + short-lived (15 min) — see `jwt.*` properties in `application.properties`

### Frontend structure (`frontend/src/app`)
```
components/
  admin-components/       — order monitoring, restaurant management (guarded by adminGuard)
  home/ + home-components/
  menu-components/        — auth dialogs, Google callback, password reset
  order-components/       — checkout
  restaurant-menu-components/
  resturants-components/  — restaurant search/list
  user-orders-components/ — order history
  nav/
services/
  api/    — HTTP services, one per domain (auth, order, menu, restaurant, …)
  state/  — shared state (cart, language, map, searched-restaurant, option)
guards/   — adminGuard (protects admin routes)
interfaces/, pipes/, directives/, validator/
```

All routes use lazy `loadComponent` except the root `HomeComponent`.

### Database migrations
Flyway scripts live in `backend/src/main/resources/db/migration/`:
- `V1__create_tables.sql` — schema
- `V2__insert_sample_data.sql` — seed data

### Static images
Stored in `backend/src/main/resources/static/img/` and served at `/img/<filename>`.

## Environment configuration

`.env` (root) contains DB credentials consumed by `docker-compose.yml`:
```
SURVEY_DB_HOST, SURVEY_DB_PORT, SURVEY_DB_NAME, SURVEY_DB_USERNAME, SURVEY_DB_PASSWORD
```

Backend `application.properties` holds JWT secret, PayU credentials, Google OAuth2 client ID/secret, and mail config. These are checked in for development — rotate before any production deployment.

## Testing

Backend tests are split into:
- `src/test/java/.../tests/unit/` — Mockito-based unit tests (JUnit 5)
- `src/test/java/.../tests/integration/` — integration tests hitting real services

Frontend uses Karma + Jasmine (`*.spec.ts` alongside each component).
