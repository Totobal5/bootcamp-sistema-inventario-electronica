# 🚀 Guía de Deployment - Railway

Esta guía te ayudará a desplegar el Sistema de Inventario Electrónica en Railway.

## 📋 Prerrequisitos

1. ✅ Cuenta de GitHub con el repositorio del proyecto
2. ✅ Cuenta en Railway (https://railway.app)
3. ✅ Código pusheado a GitHub

## 🎯 Paso a Paso - Deployment en Railway

### 1️⃣ Crear Cuenta en Railway

1. Ve a https://railway.app
2. Haz clic en "Start a New Project"
3. Inicia sesión con GitHub
4. Autoriza Railway para acceder a tus repositorios

### 2️⃣ Crear Nuevo Proyecto

1. Click en "New Project"
2. Selecciona "Deploy from GitHub repo"
3. Busca y selecciona: `bootcamp-sistema-inventario-electronica`
4. Railway detectará automáticamente que es un proyecto Maven/Spring Boot

### 3️⃣ Provisionar PostgreSQL

1. En el dashboard del proyecto, click en "+ New"
2. Selecciona "Database" → "PostgreSQL"
3. Railway creará automáticamente la base de datos
4. Espera a que el servicio esté "Active"

### 4️⃣ Conectar PostgreSQL a la Aplicación

Railway genera automáticamente las variables de entorno de conexión. 

**En el servicio de la aplicación:**

1. Ve a "Variables"
2. Click en "+ New Variable" → "Add Reference"
3. Selecciona el servicio PostgreSQL
4. Agrega:
   - `DATABASE_URL` → Reference: `postgres.DATABASE_URL`
   - O Railway ya las crea automáticamente

### 5️⃣ Configurar Variables de Entorno

En el servicio de la aplicación, agrega las siguientes variables:

```env
# Datasource (Railway genera automáticamente DATABASE_URL)
SPRING_DATASOURCE_URL=${DATABASE_URL}

# O usa las variables separadas si prefieres:
SPRING_DATASOURCE_URL=jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}
SPRING_DATASOURCE_USERNAME=${PGUSER}
SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# JWT (IMPORTANTE: Generar clave segura única)
JWT_SECRET=<GENERAR_CON_openssl_rand_base64_32>
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# Upload Files (Railway usa filesystem efímero, considerar AWS S3 para producción)
FILE_UPLOAD_DIR=/app/uploads
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB

# Logging
LOGGING_LEVEL_COM_BOOTCAMP_INVENTARIO=INFO
```

### 6️⃣ Generar JWT Secret Seguro

**Opción 1: OpenSSL (recomendado)**
```bash
openssl rand -base64 32
```

**Opción 2: Online**
```
https://generate-random.org/api-token-generator?count=1&length=64&type=mixed-numbers-symbols
```

**Copiar el resultado** y pegarlo en la variable `JWT_SECRET` en Railway.

### 7️⃣ Configurar Build Settings (Opcional)

Railway detecta automáticamente Maven, pero puedes personalizar:

1. Ve a "Settings" del servicio
2. En "Build":
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/*.jar`

### 8️⃣ Deploy Automático

1. Railway detectará cambios en `main` branch automáticamente
2. Cada push a GitHub dispara un nuevo deploy
3. Puedes ver logs en tiempo real en la pestaña "Deployments"

### 9️⃣ Obtener URL Pública

1. Ve a "Settings" del servicio de la aplicación
2. En "Networking" → "Public Networking"
3. Click en "Generate Domain"
4. Railway asignará un dominio: `tu-app.up.railway.app`

### 🔟 Verificar Deployment

1. **Health Check**:
   ```
   https://tu-app.up.railway.app/actuator/health
   ```
   Respuesta esperada: `{"status":"UP"}`

2. **Swagger UI**:
   ```
   https://tu-app.up.railway.app/swagger-ui.html
   ```

3. **Probar Login**:
   ```bash
   curl -X POST https://tu-app.up.railway.app/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{
       "username": "admin",
       "email": "admin@empresa.com",
       "password": "admin123",
       "rol": "ADMIN"
     }'
   ```

---

## 🐛 Troubleshooting

### ❌ Error: "Application failed to start"

**Revisar logs:**
1. Ve a "Deployments" → Click en el deployment fallido
2. Revisa los logs para errores específicos

**Causas comunes:**
- Variables de entorno faltantes (JWT_SECRET, DATABASE_URL)
- Puerto incorrecto (Railway usa PORT dinámico)
- Credenciales de BD incorrectas

**Solución:**
```java
// En application.properties, usar:
server.port=${PORT:8080}
```

### ❌ Error: "Connection refused to PostgreSQL"

**Verificar:**
1. PostgreSQL está "Active" en Railway
2. Variables de entorno están correctas
3. `SPRING_DATASOURCE_URL` apunta a `postgres:5432` (interno en Railway)

### ❌ Error: "JWT Secret too short"

El JWT_SECRET debe ser de **al menos 256 bits** (32 caracteres en Base64).

**Generar nuevo:**
```bash
openssl rand -base64 32
```

### ❌ Error: "Out of Memory"

Railway Free tier tiene límites de memoria.

**Optimizar JVM:**
```env
JAVA_TOOL_OPTIONS=-Xmx512m -Xms256m -XX:MaxMetaspaceSize=128m
```

---

## 🎨 Personalización de Dominio (Opcional)

### Usar Dominio Propio

1. Ve a "Settings" → "Networking"
2. En "Custom Domains", click "Add Domain"
3. Ingresa tu dominio (ej: `api.tuempresa.com`)
4. Configura el DNS según las instrucciones de Railway:
   - Tipo: `CNAME`
   - Nombre: `api` (o subdomain deseado)
   - Valor: `tu-app.up.railway.app`

---

## 📊 Monitoreo

### Ver Logs en Tiempo Real
```bash
# Desde Railway CLI (instalar primero)
railway logs
```

### Métricas
Railway proporciona:
- CPU Usage
- Memory Usage
- Network Traffic
- Request Rate

Accesibles en el dashboard del servicio.

---

## 🔄 CI/CD - Deploy Automático

Railway despliega automáticamente cada push a `main`:

1. Haces cambios en código local
2. Commit y push a GitHub:
   ```bash
   git add .
   git commit -m "feat: nueva funcionalidad"
   git push origin main
   ```
3. Railway detecta el cambio y redespliega
4. Puedes ver el progreso en "Deployments"

### Rollback a Versión Anterior

1. Ve a "Deployments"
2. Selecciona un deployment anterior exitoso
3. Click en "..." → "Redeploy"

---

## 💰 Costos

### Railway Free Tier
- **$5 USD de crédito gratis/mes**
- Suficiente para desarrollo y demos
- PostgreSQL incluido

### Plan Hobby ($5/mes)
- **$5 USD de crédito + $5 adicionales**
- Mejor para proyectos pequeños en producción

### Estimación de Uso
- **Aplicación Spring Boot**: ~$3-5/mes
- **PostgreSQL**: ~$2-3/mes
- **Total**: ~$5-8/mes

**💡 Tip**: Railway pausará servicios inactivos automáticamente para ahorrar créditos.

---

## 🔐 Seguridad en Producción

### ✅ Checklist de Seguridad

- [ ] JWT_SECRET único y seguro (256 bits)
- [ ] POSTGRES_PASSWORD complejo
- [ ] `SPRING_JPA_SHOW_SQL=false` en producción
- [ ] HTTPS habilitado (Railway lo hace automáticamente)
- [ ] Logs no exponen información sensible
- [ ] Rate limiting configurado (Spring Security)
- [ ] CORS configurado correctamente
- [ ] Variables de entorno NUNCA en código

### Rotar JWT Secret

Si necesitas cambiar el JWT_SECRET:

1. Genera nuevo secret: `openssl rand -base64 32`
2. Actualiza variable en Railway
3. Railway redesplegará automáticamente
4. **Todos los usuarios** deberán volver a loguearse

---

## 📚 Recursos Adicionales

- **Railway Docs**: https://docs.railway.app
- **Railway CLI**: https://docs.railway.app/develop/cli
- **Railway Status**: https://status.railway.app
- **Community**: https://discord.gg/railway

---

## ✅ Checklist Final

Antes de considerar el deployment completo:

- [ ] Aplicación desplegada y accesible vía HTTPS
- [ ] PostgreSQL conectado y funcionando
- [ ] Todas las variables de entorno configuradas
- [ ] Swagger UI accesible
- [ ] Endpoints de autenticación funcionando
- [ ] Crear al menos 1 usuario ADMIN de prueba
- [ ] Probar upload de archivos (Gerber, certificaciones)
- [ ] Verificar logs sin errores
- [ ] Health check respondiendo correctamente
- [ ] Actualizar README con URL de producción

---

## 🎉 ¡Deployment Exitoso!

Tu aplicación está ahora desplegada en:
- **URL**: https://tu-app.up.railway.app
- **Swagger**: https://tu-app.up.railway.app/swagger-ui.html
- **Health**: https://tu-app.up.railway.app/actuator/health

**Próximos pasos:**
1. Compartir URL con tu equipo o bootcamp
2. Documentar en README.md
3. Preparar demo para video tutorial
4. ¡Celebrar! 🎊
