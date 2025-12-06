# 🚀 Railway - Próximos Pasos

## ✅ Cambios Aplicados

1. **Dockerfile Optimizado**
   - ✅ Upgrade a Java 21 (compatible con Spring Boot 3.2.0)
   - ✅ Maven dependency cache (reduce tiempo de build)
   - ✅ Multi-stage build optimizado
   - ✅ Health check configurado
   - ✅ JVM settings optimizados para Railway (512MB)

2. **Archivos Nuevos**
   - ✅ `railway.toml` - configuración de Railway
   - ✅ `railway.env.example` - variables de entorno requeridas
   - ✅ `.dockerignore` optimizado

## 📋 Configuración en Railway (5 minutos)

### 1. PostgreSQL Database

Railway debería detectar automáticamente el nuevo commit y reiniciar el build.

**Agregar PostgreSQL:**
```
1. En tu proyecto Railway → Click "+ New"
2. Seleccionar "Database" → "PostgreSQL"
3. Esperar a que se provisione (1-2 minutos)
```

### 2. Variables de Entorno

En el servicio de tu aplicación, configurar estas variables:

**OBLIGATORIAS:**
```bash
# Profile
SPRING_PROFILES_ACTIVE=prod

# JWT Secret (generar con: openssl rand -base64 32)
JWT_SECRET=<GENERAR_SECRETO_UNICO_AQUI>
JWT_EXPIRATION=86400000
```

**PostgreSQL (Railway las genera automáticamente):**
- Railway crea `DATABASE_URL` automáticamente
- Spring Boot detecta `DATABASE_URL` y configura la conexión

Si necesitas referenciarlas manualmente:
```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}
SPRING_DATASOURCE_USERNAME=${PGUSER}
SPRING_DATASOURCE_PASSWORD=${PGPASSWORD}
```

### 3. Generar JWT Secret Seguro

**En Windows (PowerShell):**
```powershell
# Opción 1: Con OpenSSL (si lo tienes instalado)
openssl rand -base64 32

# Opción 2: Con PowerShell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Convert]::ToBase64String($bytes)
```

**Copiar el resultado** y pegarlo en Railway como valor de `JWT_SECRET`

### 4. Verificar el Build

1. Ir a tu proyecto en Railway
2. Click en "Deployments"
3. El nuevo deploy debería aparecer automáticamente
4. Monitorear los logs:
   - ✅ "Detected Java project"
   - ✅ "Building with Maven"
   - ✅ "Downloading dependencies" (primera vez tarda ~3-5 min)
   - ✅ "BUILD SUCCESS"
   - ✅ "Started InventarioElectronicaApplication"

### 5. Generar URL Pública

Una vez el deploy sea exitoso:
```
1. Ir a "Settings" del servicio
2. En "Networking" → "Public Networking"
3. Click "Generate Domain"
4. Railway asigna: tu-app.up.railway.app
```

### 6. Probar la Aplicación

**Health Check:**
```bash
curl https://tu-app.up.railway.app/actuator/health
```

**Respuesta esperada:**
```json
{"status":"UP"}
```

**Swagger UI:**
```
https://tu-app.up.railway.app/swagger-ui.html
```

**Login:**
```
https://tu-app.up.railway.app/login
```

## 🐛 Troubleshooting

### Si el build falla con "timeout"

El nuevo Dockerfile optimiza el build con cache de Maven, pero la **primera vez** puede tardar 5-7 minutos descargando todas las dependencias.

**Railway Free Tier tiene timeout de 10 minutos**, debería ser suficiente.

Si falla:
1. Verificar los logs para el error específico
2. Puede ser que falte una variable de entorno
3. Revisar que PostgreSQL esté "Active"

### Si la aplicación no inicia

Verificar en los logs:
```
# Buscar errores comunes:
- "Cannot create connection to database"
  → PostgreSQL no está conectado o credenciales incorrectas

- "JWT secret key too short"
  → JWT_SECRET debe ser al menos 256 bits (32 bytes en base64)

- "Port already in use"
  → No debería pasar en Railway, verifica que SPRING_PROFILES_ACTIVE=prod
```

### Si las variables de PostgreSQL no se detectan

Railway conecta automáticamente PostgreSQL cuando:
1. PostgreSQL está en el mismo proyecto
2. Ambos servicios están en el mismo "environment"

**Para forzar la conexión:**
```
1. En tu servicio de app → "Variables"
2. Click "+ New Variable" → "Add Reference"
3. Seleccionar PostgreSQL service
4. Railway mostrará: PGHOST, PGPORT, PGDATABASE, PGUSER, PGPASSWORD
5. Hacer referencia a estas en SPRING_DATASOURCE_URL
```

## 📊 Monitoreo

**Ver logs en tiempo real:**
```
1. Click en tu servicio
2. Tab "Deployments"
3. Click en el deployment activo
4. Ver logs en tiempo real
```

**Métricas:**
- CPU Usage
- Memory Usage (max 512MB en free tier)
- Network Traffic

## 💰 Costos Estimados

**Railway Free Tier:**
- $5 USD de crédito gratis/mes
- Suficiente para desarrollo y demos

**Uso estimado:**
- Aplicación Spring Boot: ~$3-4/mes
- PostgreSQL: ~$2-3/mes
- **Total:** ~$5-7/mes

Railway pausará servicios inactivos automáticamente para ahorrar créditos.

## ✅ Checklist Final

- [ ] PostgreSQL provisionado y "Active"
- [ ] JWT_SECRET configurado (256 bits)
- [ ] SPRING_PROFILES_ACTIVE=prod
- [ ] Build exitoso (verificar "BUILD SUCCESS" en logs)
- [ ] Aplicación iniciada (buscar "Started InventarioElectronicaApplication")
- [ ] Health check respondiendo: `/actuator/health`
- [ ] Dominio público generado
- [ ] Swagger UI accesible
- [ ] Login funcionando
- [ ] Crear usuario admin inicial

## 🎯 Próximos Pasos

1. **Crear Usuario Admin:**
   El `DatabaseInitializer` debería crear automáticamente el usuario admin al iniciar.
   
   Credenciales por defecto:
   - Username: `admin`
   - Password: `password123`
   - Email: `admin@inventario.com`

2. **Probar Endpoints:**
   - Registrar nuevo usuario: `POST /api/auth/register`
   - Login: `POST /api/auth/login`
   - Crear componente: `POST /api/componentes`

3. **Documentar URL en README:**
   Actualizar README.md con la URL de producción

---

## 🆘 Soporte

Si sigues teniendo problemas, revisa:
- Railway Status: https://status.railway.app
- Railway Docs: https://docs.railway.app
- Logs del deployment en Railway Dashboard
