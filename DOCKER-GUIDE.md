# Guía de Desarrollo - Sistema de Inventario Electrónica

## 📦 Archivos de Dockerización Creados

### 1. Dockerfile
- **Multi-stage build** optimizado
- Etapa 1: Compilación con Maven
- Etapa 2: Runtime con JRE (imagen más pequeña)
- Usuario no-root para seguridad
- Health check configurado
- Variables de entorno optimizadas

### 2. docker-compose.yml
- **Servicio PostgreSQL 15** con health check
- **Servicio Spring Boot** con restart automático
- **Volúmenes persistentes** para datos y uploads
- **Network aislada** para comunicación entre servicios
- **Variables de entorno** configurables desde .env
- Init scripts para datos iniciales

### 3. Archivos de Configuración
- `.env.example` - Plantilla de variables de entorno
- `.dockerignore` - Optimización del build context
- `setup-env.ps1` / `setup-env.sh` - Scripts de configuración inicial
- `start.ps1` / `start.sh` - Scripts de inicio rápido
- `init-scripts/01-init-data.sql` - Datos iniciales (opcional)

## 🚀 Flujo de Trabajo con Docker

### Desarrollo Local
```bash
# 1. Configurar entorno
./setup-env.ps1  # Windows
./setup-env.sh   # Linux/Mac

# 2. Iniciar todo
./start.ps1      # Windows
./start.sh       # Linux/Mac
```

### Testing
```bash
# Acceder a Swagger UI
http://localhost:8081/swagger-ui.html

# Probar endpoint de salud
curl http://localhost:8081/actuator/health

# Ver logs en tiempo real
docker-compose logs -f app
```

### Debugging
```bash
# Acceder al contenedor
docker-compose exec app sh

# Ver variables de entorno
docker-compose exec app env

# Revisar base de datos
docker-compose exec postgres psql -U postgres -d inventario_electronica

# Ver tablas creadas
\dt

# Consultar usuarios
SELECT * FROM usuario;
```

## 📁 Estructura de Volúmenes

```
volumes:
  postgres_data/     # Datos persistentes de PostgreSQL
  app_uploads/       # Archivos subidos (Gerber, certificaciones)
```

Los volúmenes persisten incluso al detener los contenedores con `docker-compose down`.

Para **eliminar todos los datos** (útil para testing):
```bash
docker-compose down -v
```

## 🔐 Seguridad

### Variables de Entorno Sensibles

**NUNCA** commitear archivos con:
- Contraseñas reales
- JWT_SECRET de producción
- Credenciales de servicios externos

✅ Usar `.env.example` como plantilla
✅ Crear `.env` local (ya en .gitignore)
✅ En producción: usar secrets manager (Railway/Render)

### JWT Secret

Generar clave segura:
```bash
openssl rand -base64 32
```

Resultado de ejemplo:
```
XT5k7h9jKmP3qRsU8vWxYz2aBcDeFgHi
```

Agregar a `.env`:
```env
JWT_SECRET=XT5k7h9jKmP3qRsU8vWxYz2aBcDeFgHi
```

## 🧪 Testing con Docker

### Smoke Test Completo
```bash
# 1. Iniciar servicios
docker-compose up -d

# 2. Esperar 30 segundos
sleep 30

# 3. Registrar usuario ADMIN
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@empresa.com",
    "password": "admin123",
    "rol": "ADMIN"
  }'

# 4. Login
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# 5. Guardar token y usar en requests subsecuentes
# Authorization: Bearer <token>
```

## 🐛 Troubleshooting

### Problema: Contenedor app se reinicia constantemente

**Revisar logs:**
```bash
docker-compose logs app | tail -100
```

**Causas comunes:**
1. PostgreSQL no está listo → Aumentar `start_period` en healthcheck
2. Credenciales incorrectas → Verificar .env
3. Lombok no generó código → Verificar pom.xml

### Problema: No compila en Docker

**Forzar rebuild sin cache:**
```bash
docker-compose build --no-cache app
```

### Problema: Base de datos corrupta

**Reset completo:**
```bash
docker-compose down -v
docker-compose up -d
```

## 📊 Monitoreo

### Ver uso de recursos
```bash
docker stats

# Salida ejemplo:
CONTAINER ID   NAME              CPU %     MEM USAGE / LIMIT
abc123         inventario-app    0.50%     512MiB / 1GiB
def456         inventario-db     0.10%     128MiB / 512MiB
```

### Health Checks
```bash
# App
curl http://localhost:8081/actuator/health

# PostgreSQL (desde host)
docker-compose exec postgres pg_isready -U postgres
```

## 🔄 Actualizar después de cambios de código

```bash
# Opción 1: Rebuild automático
docker-compose up -d --build app

# Opción 2: Rebuild manual
docker-compose build app
docker-compose up -d app

# Ver logs nuevos
docker-compose logs -f app
```

## 📝 Notas Importantes

### Lombok en Docker
El Dockerfile compila con Maven dentro del contenedor, que **incluye correctamente el procesador de anotaciones de Lombok**. Esto resuelve el problema de compilación local.

### Persistencia de Datos
- Los datos de PostgreSQL se guardan en un volumen Docker
- Los uploads se guardan en otro volumen separado
- Ambos persisten entre reinicios
- Solo se eliminan con `docker-compose down -v`

### Networking
Los contenedores usan nombres de servicio para comunicarse:
- `postgres:5432` (NO `localhost:5432` dentro de app)
- `app:8081` (para comunicación entre servicios)

Desde el host:
- `localhost:8081` → Aplicación
- `localhost:5432` → PostgreSQL

## 🎯 Próximos Pasos

1. ✅ Dockerización completada
2. ⏳ Deploy en Railway/Render (Tarea 14)
3. ⏳ Video tutorial YouTube (Tarea 15)

---

**Progreso: 13/15 tareas completadas (87%)** 🎉
