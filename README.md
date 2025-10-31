# Portfolio Backend
A Java Spring Boot app with JWT auth, CRUD, WebSockets, and batch processing.

## Setup
1. Clone repo.
2. Run `mvn clean install`.
3. Configure PostgreSQL in application.yml.
4. Run `mvn spring-boot:run`.
5. Test with Postman (e.g., POST /api/auth/login).

## Features
- JWT Authentication
- Inventory with file uploads
- Real-time chat
- Finance with batch CSV imports