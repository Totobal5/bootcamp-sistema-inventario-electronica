# Sistema de Inventario de Componentes Electrónicos - AI Agent Instructions

## Architecture Overview

**Stack**: Spring Boot 3.2.0, Java 21, PostgreSQL 15, JWT Authentication, Docker
**Pattern**: Layered MVC (Controller → Service → Repository → Entity)

### Core Domain Model
- **ComponenteElectronico**: Electronics inventory with stock tracking
- **Placa**: PCB designs with ManyToMany relationship to components via `PlacaComponente` (join entity with `cantidadNecesaria`)
- **SolicitudMecanizado**: Machining requests with Gerber file uploads + versioning
- **SolicitudArmado**: Assembly requests with automatic stock validation and deduction on confirm
- **Tarea**: Task assignments to operators (PENDIENTE → EN_PROCESO → PAUSADO/COMPLETADO)
- **ArchivoCertificacion**: Quality certifications (PDF/images, max 10MB)

### Critical Business Logic Flows

**Assembly Request Stock Management** (`SolicitudArmadoServiceImpl`):
1. Creation validates stock via `verificarStockDisponible()` - throws `InsufficientStockException` if insufficient
2. State transitions: SOLICITADO → EN_PROCESO → COMPLETADO
3. `confirmarArmado()` (line 134+) **automatically deducts stock** from all components when transitioning to COMPLETADO
4. Stock calculation: `cantidadNecesaria * solicitud.getCantidad()` per component

**File Upload Pattern** (max 10MB, see `application.properties`):
- Gerber files: Auto-versioned on re-upload (stored in `uploads/gerber/`)
- Certifications: Unique filename generation (stored in `uploads/certificaciones/`)
- Constants in `util/Constants.java`: `MAX_FILE_SIZE`, `ALLOWED_FILE_EXTENSIONS`

## Security & Authorization

**JWT**: Stateless auth with 24h expiration (`jwt.expiration=86400000`)
- Filter chain: `JwtAuthenticationFilter` → extracts token → validates → sets SecurityContext
- Token format: `Authorization: Bearer <token>`

**Role-Based Access** (via `@PreAuthorize`):
- **ADMIN**: Full CRUD, can delete resources
- **OPERADOR**: Manage requests/tasks (change states), upload certifications, view assigned tasks via `/mis-tareas`
- **CLIENTE**: Create requests (SolicitudMecanizado, SolicitudArmado), view own data

Example patterns (see controllers):
```java
@PreAuthorize("hasRole('CLIENTE')") // Only clients create solicitudes
@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')") // Manage states
@PreAuthorize("hasRole('ADMIN')") // Delete operations
```

**Permission Validation**: Services implement `isOwner()` checks for ownership validation

## Developer Workflows

### Local Development
**Startup**: Use `start.ps1` (PowerShell) or `start.sh` (Bash) - compiles with `mvn clean install -DskipTests` then runs `mvn spring-boot:run`
**Database**: PostgreSQL must be running on `localhost:5432` with database `inventario_electronica`
**Initial User**: `DatabaseInitializer` auto-creates admin user (username: `admin`, password: `password123`)

**Ports**:
- App: `8081`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- API Docs: `http://localhost:8081/api-docs`

### Docker Deployment
```bash
docker-compose up -d  # Starts postgres + app containers
```
- DB container: `inventario-db` (port 5432)
- App container: `inventario-app` (port 8081)
- Healthcheck ensures DB ready before app starts
- Volume mount: `./uploads:/app/uploads` for file persistence

### Environment Profiles
- `application.properties`: Default (local dev)
- `application-dev.properties`: Development profile
- `application-prod.properties`: Production (Railway/Render)
- Set via `SPRING_PROFILES_ACTIVE=prod`

## Code Conventions

### Transaction Management
- **Read operations**: `@Transactional(readOnly = true)` - consistently used across all service GET methods
- **Write operations**: `@Transactional` - used for create/update/delete, enables automatic rollback

### Repository Pattern
- **Custom queries**: Named methods like `findByIdWithRelations()`, `findByEstado()`, `findByClienteIdWithPlaca()`
- **Eager loading**: Use `@EntityGraph` or JOIN FETCH in JPQL to avoid N+1 queries
- Example: `PlacaRepository.findByIdWithComponentes()` fetches components eagerly

### DTO Pattern
Strict separation: `dto/request/` and `dto/response/` packages
- Request DTOs: Validation annotations (`@NotNull`, `@Min`, `@Size`)
- Response DTOs: Mapped via service layer `mapToResponse()` methods
- **Never expose entities directly** in controllers

### Exception Handling
Centralized in `GlobalExceptionHandler` with **13 handlers**:
- `ResourceNotFoundException` → 404
- `InsufficientStockException` → 409 (Conflict)
- `DuplicateResourceException` → 409
- `BadRequestException` → 400
- `UnauthorizedException` → 401
- `AccessDeniedException` → 403
- `MaxUploadSizeExceededException` → 413
- All return standardized `ErrorResponse` with timestamp, status, message, path

### Logging
- Use `@Slf4j` (Lombok) for logging
- Debug level for entry/exit: `log.debug("Buscando solicitud de armado con ID: {}", id)`
- Info level for state changes: `log.info("Armado confirmado exitosamente para solicitud {}", id)`
- Pattern: See `application.properties` for console format

## Key Files & Integration Points

**Configuration**:
- `SecurityConfig.java`: FilterChain, password encoder, auth provider
- `OpenApiConfig.java`: Swagger/OpenAPI setup with JWT bearer auth
- `DatabaseInitializer.java`: Auto-creates admin user on startup (CommandLineRunner)

**State Machine Enums** (`model/enums/`):
- `EstadoSolicitud`: SOLICITADO, EN_PROCESO, COMPLETADO, CANCELADO
- `EstadoTarea`: PENDIENTE, EN_PROCESO, PAUSADO, COMPLETADO
- `Rol`: ADMIN, OPERADOR, CLIENTE
- `TipoArchivo`: CERTIFICACION, INSPECCION

**Frontend Integration**: Thymeleaf templates in `templates/` with static resources (`css/styles.css`, `js/api-client.js`)
- Login page: `/login`
- Dashboard: `/dashboard`
- Components/Placas views with API client in `api-client.js`

## Testing & Validation

**Test database**: H2 in-memory (see `pom.xml` test scope)
**Test config**: `src/test/resources/application.properties`
**Build**: `mvn clean install` runs tests; use `-DskipTests` to skip

**Business validations to preserve**:
1. Stock validation before creating SolicitudArmado
2. Stock deduction only on COMPLETADO state transition
3. Unique constraints: component names, usernames, emails
4. File size limits (10MB max)
5. State transition rules (can't skip states)
6. Ownership checks before state changes

## Common Patterns

**Fetching with relationships**:
```java
// Use custom repository methods to avoid N+1
Placa placa = placaRepository.findByIdWithComponentes(id)
    .orElseThrow(() -> new ResourceNotFoundException("Placa", "id", id));
```

**Stock verification**:
```java
// Always verify before operations that require stock
boolean stockDisponible = verificarStockDisponible(placa, cantidad);
if (!stockDisponible) throw new InsufficientStockException(message);
```

**Username extraction from SecurityContext**:
```java
// In controllers, pass username from Principal to service layer
@PostMapping
public ResponseEntity<?> create(@RequestBody Request request, Principal principal) {
    return service.create(request, principal.getName());
}
```

## Deployment Targets

**Railway** (recommended): Auto-detects Dockerfile, PostgreSQL addon available
**Render**: See `RENDER-DEPLOYMENT.md` for specific config
**Required env vars**: `SPRING_DATASOURCE_URL`, `SPRING_PROFILES_ACTIVE`, `JWT_SECRET`, `JWT_EXPIRATION`

See `QUICK-START-DEPLOY.md` for 5-minute Railway deployment guide.
