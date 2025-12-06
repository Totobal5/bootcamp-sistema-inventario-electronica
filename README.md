# Sistema de Inventario de Componentes Electrónicos

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

**Proyecto final del bootcamp** - Sistema para gestionar inventario de componentes electrónicos con control de revisiones de PCBs, producción y stock.

[📺 Video](https://youtube.com/tu-video) • [🚀 Demo](https://tu-app.railway.app) • [📖 Guía PCB](PCB-REVISION-GUIDE.md)

---

## ¿Qué es esto?

Mi proyecto final del bootcamp. Simula el inventario de una empresa de electrónica que fabrica PCBs.

El problema que resuelve: cuando una empresa diseña placas electrónicas, necesita controlar versiones de los diseños, mantener un BOM (lista de componentes) actualizado, gestionar el stock, y llevar trazabilidad de quién aprobó qué. Todo esto mientras múltiples operadores trabajan en paralelo.

**Lo más interesante**: El módulo de revisiones PCB que armé permite importar BOMs desde CSV y matchea automáticamente los componentes con el inventario usando el MPN. Además, una vez aprobada una revisión, queda bloqueada (inmutable) para evitar cagadas en producción.

---

## Qué hace

**Inventario de componentes** - Lo básico: resistencias, capacitores, ICs, etc. Cada componente tiene su MPN (Manufacturer Part Number), fabricante, footprint y stock.

**Módulo de PCBs** - La parte más trabajada del proyecto:
- Versionado de diseños (Rev 1.0, 1.1, 2.0...)
- Specs técnicas reales: dimensiones, capas, espesor, acabado (HASL/ENIG/OSP), color de máscara
- **Importar BOM desde CSV**: subes un archivo y matchea los componentes automáticamente por MPN
- **Sistema de aprobación**: una vez aprobada una revisión, queda locked (no se puede editar más)
- Lifecycle states: DRAFT → PROTOTYPE → PRODUCTION → DEPRECATED

El matching de componentes fue lo más interesante de resolver. Tiene 3 niveles:
1. Match exacto (1.0) - MPN idéntico
2. Match parcial (0.5-0.99) - calcula similitud con Levenshtein  
3. No match (0.0) - componente no encontrado

Cuando el match es bajo, marca el componente para revisión manual.

**Workflow de producción**:
- Solicitudes de mecanizado (subes archivos Gerber, se versiona automático)
- Solicitudes de armado (valida stock antes de crear, deduce stock automático al confirmar)
- Sistema de tareas para operadores
- Upload de certificaciones de calidad (PDFs, fotos, máx 10MB)

**Auth & permisos**:
- JWT con 3 roles: ADMIN (hace todo), OPERADOR (gestiona producción), CLIENTE (solo crea solicitudes)
- Cada endpoint tiene su `@PreAuthorize` para controlar quién puede hacer qué
- Los clientes solo ven sus propias solicitudes

---

## Stack

**Backend**: Spring Boot 3.2 + Java 21, PostgreSQL, JPA/Hibernate, JWT auth, Swagger

**Frontend**: Thymeleaf + JavaScript vanilla (sin React ni nada pesado), CSS3

**DevOps**: Docker, Maven, Railway para deploy

Usé Java 21 porque quería probar records y pattern matching. El frontend es simple a propósito - quería enfocarme en el backend y no complicarme con webpack y toda esa bola.

---

## Cómo correrlo

**Necesitas**: Java 21, PostgreSQL 15, Maven

```bash
# Clonar
git clone https://github.com/Totobal5/bootcamp-sistema-inventario-electronica.git
cd bootcamp-sistema-inventario-electronica

# Crear DB
psql -U postgres
CREATE DATABASE inventario_electronica;
\q

# Correr (hay scripts para facilitar)
# Windows
.\start.ps1

# Linux/Mac  
chmod +x start.sh && ./start.sh

# O con Maven directo
mvn clean install -DskipTests
mvn spring-boot:run
```

Abre http://localhost:8081 y entra con `admin / password123`

Swagger: http://localhost:8081/swagger-ui.html

---

## Documentación

- **[PCB-REVISION-GUIDE.md](PCB-REVISION-GUIDE.md)** - Cómo usar el módulo PCB completo
- **[TESTING-CHECKLIST.md](TESTING-CHECKLIST.md)** - 24 tests manuales paso a paso
- **[PCB-MODULE-COMPLETED.md](PCB-MODULE-COMPLETED.md)** - Resumen técnico de la implementación
- **[bom-example.csv](bom-example.csv)** - Ejemplo de BOM para probar el import

---

## API Highlights

Los endpoints más interesantes:

```bash
# Auth
POST /api/auth/login         # Login con JWT
POST /api/auth/register      # Registro

# PCB Revisions (lo nuevo)
GET    /api/pcb-designs/{id}/revisions              # Ver historial de versiones
POST   /api/pcb-designs/{id}/revisions              # Crear nueva rev
POST   /api/pcb-revisions/{id}/import-bom           # Importar BOM CSV
POST   /api/pcb-revisions/{id}/approve              # Aprobar y bloquear
POST   /api/pcb-revisions/{id}/promote-to-production

# Componentes
GET    /api/componentes/stock-bajo    # Alertas de stock
PUT    /api/componentes/{id}/stock    # Ajustar stock

# Armado
POST   /api/solicitudes-armado/{id}/confirmar  # Deduce stock automático
```

Doc completa: http://localhost:8081/swagger-ui.html

---

## Desafíos que me rompieron la cabeza

### 1. Matching de MPNs con tolerancia

El problema: los MPNs en los CSVs a veces tienen espacios extra, guiones, mayúsculas random. No podía hacer solo `mpn.equals()` porque fallarían matches obvios.

La solución: algoritmo de 3 niveles:
```java
// 1. Match exacto
Optional<ComponenteElectronico> exact = componenteRepo.findByMpn(mpn);

// 2. Match parcial (calcula distancia Levenshtein)
double confidence = calculateMatchConfidence(mpn, candidate.getMpn());
if (confidence >= 0.7) { /* usar este */ }

// 3. No match - marcar para revisión manual
entry.markAsNotFound("MPN no encontrado");
```

Funciona piola. Si la confianza es < 0.7, lo marca en rojo para que alguien lo revise.

### 2. Inmutabilidad de revisiones aprobadas

Una vez que ingeniería aprueba una revisión, NO se puede editar más (imaginate aprobar Rev 2.0 y que alguien cambie los componentes después = desastre en producción).

```java
// En el método approve()
if (this.locked) {
    throw new IllegalStateException("Ya está aprobada, hacé nueva rev");
}
this.locked = true;
this.approvedBy = approver;
this.approvedAt = LocalDateTime.now();
```

El `locked` se valida en TODOS los updates. Si intentás editar una revisión locked, tira excepción.

### 3. Transacciones y rollback

Al confirmar un armado, deduce stock de todos los componentes. Si uno falla (stock insuficiente), tiene que rollbackear todo.

```java
@Transactional  // <-- esto es clave
public void confirmarArmado(Long id) {
    // Valida stock primero
    if (!stockDisponible) {
        throw new InsufficientStockException(); // auto-rollback
    }
    
    // Deduce stock de todos
    componentes.forEach(c -> deducirStock(c));
    
    solicitud.setEstado(COMPLETADO);
}
```

Con `@Transactional`, si algo falla, vuelve todo atrás automáticamente.

### 4. Frontend sin React

Decidí no usar React/Angular para no complicarme con node_modules, webpack, etc. Solo Thymeleaf + JS vanilla.

Para drag & drop del CSV:
```javascript
dropArea.addEventListener('drop', (e) => {
    e.preventDefault();
    const files = e.dataTransfer.files;
    handleCsvFile(files[0]);
});
```

Todo fetch API, nada de axios ni librerías extras. Medio old school pero funciona.

### 5. JWT y permisos granulares

Cada endpoint tiene su `@PreAuthorize`:
```java
@PreAuthorize("hasRole('ADMIN')")  // Solo admin
@DeleteMapping("/{id}")
public ResponseEntity<?> delete(@PathVariable Long id) { }

@PreAuthorize("hasAnyRole('ADMIN', 'OPERADOR')")  // Admin u operador
@PostMapping("/{id}/approve")
public ResponseEntity<?> approve(@PathVariable Long id) { }
```

Los clientes solo ven sus propias solicitudes (filtro en service layer por username).

---

## Deploy en Railway

Para deployment usé Railway (es gratis y detecta Docker automáticamente):

```bash
# 1. Conectar repo con Railway
# 2. Agregar PostgreSQL database
# 3. Variables de entorno:
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=tu_secreto_largo_aqui
# Railway auto-configura DATABASE_URL
```

Levanta en ~5 min. Railway maneja SSL y todo automático.

---

## Estructura del proyecto

```
src/main/java/com/bootcamp/inventario/
├── config/         # Security, Swagger, DB init
├── controller/     # REST endpoints (13 controllers)
├── dto/            # Request/Response DTOs separados
├── exception/      # Custom exceptions + GlobalExceptionHandler
├── model/          # Entidades JPA (10 entities)
│   └── enums/      # Estados, roles, tipos
├── repository/     # Spring Data JPA
├── security/       # JWT filter, UserDetails, etc
├── service/        # Business logic
└── util/           # Constants, helpers

src/main/resources/
├── templates/      # Thymeleaf HTML
│   ├── pcb-designs.html    # <-- Lo nuevo
│   ├── componentes.html
│   └── ...
├── static/
│   ├── js/
│   │   └── pcb-revisions.js  # <-- 600 lines de logic
│   └── css/styles.css
└── application*.properties
```

---

## Testing

Manual testing checklist en [TESTING-CHECKLIST.md](TESTING-CHECKLIST.md) (24 casos).

Para tests unitarios (cuando los agregue):
```bash
mvn test
```

---

## Permisos por rol

| Acción | CLIENTE | OPERADOR | ADMIN |
|--------|---------|----------|-------|
| Ver componentes | ✅ | ✅ | ✅ |
| Crear componente | ❌ | ✅ | ✅ |
| Borrar componente | ❌ | ❌ | ✅ |
| Crear revisión PCB | ✅ | ✅ | ✅ |
| Aprobar revisión | ❌ | ✅ | ✅ |
| Promover a producción | ❌ | ❌ | ✅ |
| Crear solicitud | ✅ | ✅ | ✅ |
| Gestionar tareas | ❌ | ✅ | ✅ |

---

## Aprendizajes

**Técnico**:
- JPA es piola pero hay que cuidar el N+1 (usar `@EntityGraph` y JOIN FETCH)
- DTOs separados para request/response hacen la API más limpia
- `@Transactional` es tu amigo para atomicidad
- Spring Security con JWT es medio verboso pero funciona bien

**Arquitectura**:
- Separación de capas (controller → service → repository) mantiene todo más ordenado
- Validaciones en DTOs con Jakarta Validation ahorra código
- GlobalExceptionHandler centralizado > try-catch everywhere

**DevOps**:
- Docker hace el deploy mucho más simple
- Perfiles de Spring (dev/prod) son clave
- Railway es piola para prototipos, gratis y rápido

---

## TODO / Ideas futuras

- [ ] Tests unitarios (coverage >80%)
- [ ] Caché con Redis para queries frecuentes
- [ ] WebSockets para updates en tiempo real de tareas
- [ ] Integración con APIs de proveedores (Mouser, Digikey) para actualizar precios
- [ ] Reportes PDF de BOMs
- [ ] Comparación visual entre revisiones (diff)
- [ ] Frontend más pulido (o migrar a React si me da ganas)

---

## Licencia

MIT

---

**Hecho con ☕ para el bootcamp**

*Si encontrás bugs o tenés ideas, abrí un issue!*
