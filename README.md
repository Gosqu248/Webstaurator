# 🍽️ Webstaurator
Webstaurator is a comprehensive platform that streamlines online food ordering and restaurant management, offering customers an intuitive way to browse menus, place orders, and make payments, while providing restaurants with powerful tools for order processing, menu management, and real-time administration.

## 🚀 Key Features
- Registration and login with strong password verification and 2FA
- Restaurant search by address or current location (geolocation with OpenStreetMap)
- Menu browsing with dish details, add-ons, and category filtering
- Order placement with delivery or pickup options
- Payment via PayU or cash, with secure redirection to electronic payment system
- Order history, reviews, and restaurant ratings
- Real-time order management for administrators
- Restaurant and menu management
- Offers and promotions management
- Monitoring complaints and system logs

## 👥 User Roles
- Customer: Browse menus, place orders, make payments, and leave reviews
- Restaurant Administrator: Manage restaurant details, menus, orders, and promotions
- System Administrator: Oversee system operations, handle complaints, and monitor logs

## 🎯 Project Goal
- Automate the process of accepting and fulfilling online orders
- Optimize restaurant operational processes
- Provide an intuitive interface for both users and administrators



## 🔒 Privacy & Security
- All user data is protected using industry-standard encryption and secure authentication (including 2FA)
- Sensitive information such as passwords and payment details are never stored in plain text
- Regular security audits and updates are performed to ensure data integrity
- Compliance with GDPR and other relevant data protection regulations

## 🛠️ Technology Stack

### Frontend
- Framework: Angular 19
- UI Components: Angular Material
- Mapping Library: Leaflet.js
- HTTP Client: Angular's built-in HTTP client
- Reactive Programming: RxJS

### Backend
- Framework: Spring Boot 3.3.2
- Language: Java 21
- Database Access: Spring Data JPA
- API: RESTful web services
- Email Service: Spring Mail

### Database
- DBMS: PostgreSQL 17 (Alpine)
- Migration: Flyway (based on the project structure)

### DevOps & Infrastructure
- Containerization: Docker
- Container Orchestration: Docker Compose
- Reverse Proxy: Nginx
- Environment Variables: .env file for configuration

## 🚦 Getting Started

### Prerequisites
- Docker and Docker Compose

### Installation
Clone the repository:

```bash
git clone https://github.com/Gosqu248/Webstaurator.git
cd Webstaurator
```

### First run
To change configuration edit .env file.

#### Run the Application
Run entire application using Docker Compose:

```bash
docker-compose up -d
```

Run docker with rebuilding:

```bash
docker-compose up --build -d
```

The application will be available at http://localhost.

## 📁 Project Structure

```
├── backend/                 # Spring Boot backend application
│   ├── src/                # Source code
│   ├── Dockerfile          # Backend container configuration
│   └── pom.xml             # Maven dependencies and build configuration
├── frontend/               # Angular frontend application
│   ├── src/                # Source code
│   ├── Dockerfile          # Frontend container configuration
│   └── package.json        # NPM dependencies and scripts
├── gateway/                # Nginx gateway configuration
│   └── nginx.conf          # Nginx routing rules
├── .env                    # Environment variables
└── docker-compose.yml      # Multi-container Docker configuration
```
## 📱 Mobile Version
A mobile version of Webstaurator is available, built with React:
[WebstauratorApp (React Native)](https://github.com/Gosqu248/WebstauratorApp)

## 🚀 Deployment
The application is configured for containerized deployment using Docker Compose. The Nginx gateway routes traffic to the appropriate service based on the request path:
- `/api/*` routes to the backend service
- `/img/*` routes to the backend service for static images
- All other routes go to the frontend service

## 🧪 Backend Tests
The backend project contains unit and integration tests:
- Unit tests: 43 tests
- Integration tests: 13 tests


## 📄 License
This project is licensed under the terms specified in the project documentation.

## 👤 Author
Grzegorz Urban (Gosqu)