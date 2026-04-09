# Sistema de Gestión de Activos y Bienes Empresariales

## Ejecución rápida con Docker Compose

```bash
docker compose up --build
```

Servicios:
- Frontend: http://localhost:4200
- Backend API: http://localhost:8080/api
- Swagger: http://localhost:8080/api/swagger-ui.html
- H2 Console: http://localhost:8080/api/h2-console

Credenciales demo (HTTP Basic):
- usuario: `admin`
- password: `admin123`

## Ejecución local (sin Docker)

### Backend
```bash
cd gestion-activos-backend/src
mvn spring-boot:run
```

### Frontend
```bash
cd gestion-activos-frontend
npm install
npm start
```

## Datos iniciales
Al iniciar, el backend crea usuarios demo, un proveedor y una partida presupuestaria para que puedas probar el flujo completo.
