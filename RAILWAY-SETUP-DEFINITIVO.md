# 🚀 Railway Deployment - Configuración Definitiva

## ✅ PASO 1: Agregar PostgreSQL en Railway

1. **Dashboard Railway** → Click en tu proyecto
2. **"New"** → **"Database"** → **"Add PostgreSQL"**
3. Railway automáticamente crea las siguientes variables en el servicio PostgreSQL:
   - `DATABASE_URL` (formato: `postgresql://user:password@host:port/database`)
   - `PGHOST`
   - `PGPORT`
   - `PGDATABASE`
   - `PGUSER`
   - `PGPASSWORD`

## ✅ PASO 2: Configurar Variables en el Servicio de Aplicación

**Ve a tu servicio de aplicación (NO PostgreSQL)** → **Variables** → Agrega:

### Opción A: Usar DATABASE_URL (MÁS SIMPLE) ⭐ RECOMENDADO

```bash
# NO NECESITAS configurar nada manualmente
# La app usará automáticamente DATABASE_URL que Railway inyecta
```

La aplicación está configurada para leer `DATABASE_URL` automáticamente de las variables de entorno que Railway inyecta desde el servicio PostgreSQL.

### Opción B: Usar Referencias de Railway (Si prefieres control manual)

```bash
SPRING_DATASOURCE_URL=${{Postgres.DATABASE_URL}}
```

**⚠️ SINTAXIS CORRECTA DE REFERENCIAS:**
- ✅ `${{Postgres.DATABASE_URL}}` - Railway expande esto
- ❌ `${DATABASE_URL}` - NO funciona, Railway no expande esto
- ❌ `${PGHOST}:${PGPORT}` - NO funciona

### Variables OBLIGATORIAS que SÍ debes configurar manualmente:

```bash
JWT_SECRET=<genera-uno-con-comando-abajo>
JWT_EXPIRATION=86400000
```

**Generar JWT_SECRET seguro (PowerShell):**
```powershell
$bytes = New-Object byte[] 32; [Security.Cryptography.RNGCryptoServiceProvider]::Create().GetBytes($bytes); [Convert]::ToBase64String($bytes)
```

**O usa este ejemplo seguro:**
```
5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
```

### Variables OPCIONALES (tienen defaults):

```bash
FILE_UPLOAD_DIR=/app/uploads
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB
```

## ✅ PASO 3: Verificar Conectividad entre Servicios

Railway automáticamente crea una **red privada** entre servicios en el mismo proyecto.

1. En el servicio PostgreSQL, verás variables como:
   ```
   PGHOST = postgres.railway.internal (o similar)
   ```

2. En el servicio de aplicación, Railway **automáticamente inyecta** todas las variables del servicio PostgreSQL enlazado.

3. **NO necesitas** configurar manualmente `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, ni `SPRING_DATASOURCE_PASSWORD` si usas `DATABASE_URL`.

## ✅ PASO 4: Lista Final de Variables de Entorno

**En el Dashboard de Railway, verifica que tengas EXACTAMENTE esto:**

### Servicio de Aplicación (bootcamp-sistema-inventario-electronica)

```bash
# OBLIGATORIAS - Configúralas manualmente
JWT_SECRET=<tu-secreto-de-32+-caracteres>
JWT_EXPIRATION=86400000

# OPCIONALES - Railway las inyecta automáticamente desde PostgreSQL
# DATABASE_URL (viene de PostgreSQL automáticamente)
# PGHOST (viene de PostgreSQL automáticamente)
# PGPORT (viene de PostgreSQL automáticamente)
# PGDATABASE (viene de PostgreSQL automáticamente)
# PGUSER (viene de PostgreSQL automáticamente)
# PGPASSWORD (viene de PostgreSQL automáticamente)
# PORT (Railway lo inyecta automáticamente)

# NO CONFIGURAR - La app las toma automáticamente:
# ❌ SPRING_DATASOURCE_URL
# ❌ SPRING_DATASOURCE_USERNAME  
# ❌ SPRING_DATASOURCE_PASSWORD
# ❌ SPRING_PROFILES_ACTIVE (seteado en Dockerfile)
```

## ✅ PASO 5: Reiniciar Deployment

Después de configurar las variables:

1. Railway detecta cambios automáticamente
2. O manualmente: **Settings** → **Redeploy**

## 🔍 Verificación de Deployment Exitoso

Cuando el deployment funcione correctamente, verás en los logs:

```
✅ Active Profile :: prod
✅ Tomcat started on port(s): 8080 (http)
✅ Started InventarioElectronicaApplication in X seconds
```

## ❌ Errores Comunes y Soluciones

### Error: "Driver claims to not accept jdbcUrl"
**Causa:** Variable `SPRING_DATASOURCE_URL` configurada con sintaxis incorrecta
**Solución:** 
- Borrar `SPRING_DATASOURCE_URL` de las variables
- Dejar que la app use `DATABASE_URL` automáticamente
- O usar referencia correcta: `${{Postgres.DATABASE_URL}}`

### Error: "WeakKeyException: 240 bits not secure enough"
**Causa:** `JWT_SECRET` tiene menos de 32 caracteres
**Solución:** Regenerar con comando PowerShell o usar ejemplo de 64 caracteres

### Error: "Connection refused" o "UnknownHostException"
**Causa:** PostgreSQL no está en el mismo proyecto o región diferente
**Solución:** 
- Verificar que PostgreSQL esté en el mismo proyecto Railway
- Verificar que ambos servicios estén en la misma región

### Error: "Healthcheck failed - service unavailable"
**Causa:** App no arranca por algún error (JWT, DB, etc.)
**Solución:** Ver logs específicos para identificar error exacto

## 🎯 Configuración Mínima GARANTIZADA

**Si sigues estos pasos EXACTAMENTE, funcionará:**

1. ✅ Agregar PostgreSQL en Railway
2. ✅ Configurar SOLO estas 2 variables en el servicio de aplicación:
   ```bash
   JWT_SECRET=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
   JWT_EXPIRATION=86400000
   ```
3. ✅ NO configurar nada más (Railway inyecta el resto automáticamente)
4. ✅ Push código a GitHub
5. ✅ Esperar ~3 minutos

## 📊 Timeline Esperado

- **00:00** - Push a GitHub
- **00:10** - Railway detecta cambio
- **00:20** - Build inicia (Maven download dependencies)
- **01:30** - Build completa (JAR creado)
- **01:40** - Imagen Docker creada
- **01:50** - Container inicia
- **02:00** - Spring Boot arranca
- **02:50** - Healthcheck pasa
- **03:00** - ✅ **DEPLOYMENT EXITOSO**

## 🆘 Si TODAVÍA Falla

1. **Borrar TODAS las variables** excepto `JWT_SECRET` y `JWT_EXPIRATION`
2. **Verificar** que PostgreSQL esté en el mismo proyecto
3. **Verificar** en logs que veas: `DATABASE_URL = postgresql://...`
4. **Compartir logs** específicos del error para diagnóstico

## 🔐 Seguridad Post-Deployment

Una vez funcionando:

1. ✅ Cambiar `JWT_SECRET` por uno único
2. ✅ Configurar dominio personalizado (opcional)
3. ✅ Habilitar HTTPS (Railway lo hace automáticamente)
4. ✅ Revisar logs periódicamente
