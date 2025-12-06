# Railway Deployment Troubleshooting

## ✅ Checklist Pre-Deployment

### 1. Variables de Entorno (CRÍTICO)
Verifica que estén configuradas en Railway dashboard:

**Obligatorias:**
- ✅ `SPRING_PROFILES_ACTIVE=prod` 
- ✅ `SPRING_DATASOURCE_URL`
- ✅ `SPRING_DATASOURCE_USERNAME`
- ✅ `SPRING_DATASOURCE_PASSWORD`
- ✅ `JWT_SECRET` (mínimo 32 caracteres)

**Opcionales con defaults:**
- `JWT_EXPIRATION=86400000`
- `FILE_UPLOAD_DIR=/app/uploads`
- `SERVER_PORT` (Railway lo inyecta automáticamente)

### 2. PostgreSQL Database
Railway debe tener un servicio PostgreSQL agregado:
1. Dashboard → "New" → "Database" → "PostgreSQL"
2. Railway genera automáticamente: `DATABASE_URL`, `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`
3. **Configurar variables manualmente** o usar Railway References:
   ```
   SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
   SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
   SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
   ```

### 3. Healthcheck Configuration
Railway.toml está configurado para:
- Path: `/actuator/health`
- Timeout: 600 segundos (10 minutos)
- Initial delay: 60 segundos
- **Spring Boot tarda 60-90s en arrancar en primera ejecución**

## 🔍 Diagnóstico de Errores Comunes

### Error: "Healthcheck failed - service unavailable"
**Causa:** Aplicación no arranca o demora más de 10 minutos
**Solución:**
1. Ver logs en Railway dashboard
2. Verificar que `SPRING_PROFILES_ACTIVE=prod`
3. Verificar conexión a PostgreSQL (variables correctas)
4. Revisar si JWT_SECRET está configurado

### Error: "Connection refused" o "UnknownHostException"
**Causa:** Variables de base de datos incorrectas
**Solución:**
1. Verificar que PostgreSQL está running en Railway
2. Comprobar `SPRING_DATASOURCE_URL` tiene formato correcto:
   ```
   jdbc:postgresql://host.railway.internal:5432/railway
   ```
3. Usar Railway References en lugar de copiar valores estáticos

### Error: "IllegalArgumentException: JWT secret cannot be null"
**Causa:** `JWT_SECRET` no está configurado
**Solución:**
1. Generar secret seguro: `openssl rand -base64 32`
2. Agregar en Railway: `JWT_SECRET=<valor-generado>`

### Error: Build exitoso pero app no responde
**Causa:** Puerto incorrecto o perfil no activado
**Solución:**
1. Verificar `SPRING_PROFILES_ACTIVE=prod`
2. Railway inyecta `PORT` automáticamente, `application-prod.properties` usa: `server.port=${PORT:8080}`
3. **NO configurar** `SERVER_PORT` manualmente

## 📊 Tiempos Esperados

- **Build time:** 60-90 segundos (con cache de Maven)
- **Startup time:** 60-90 segundos (primera vez)
- **Healthcheck success:** ~2-3 minutos después de deployment

## 🚀 Proceso de Deployment Correcto

1. **Push a GitHub:**
   ```bash
   git add .
   git commit -m "fix: Railway deployment configuration"
   git push origin main
   ```

2. **Railway auto-deploy:**
   - Detecta cambios en main
   - Build imagen Docker (~70s)
   - Inicia contenedor
   - Espera 60s (initial delay)
   - Verifica `/actuator/health` cada 10s
   - Declara healthy cuando responde 200 OK

3. **Verificar deployment:**
   ```bash
   # Desde Railway dashboard obtener la URL pública
   curl https://tu-app.railway.app/actuator/health
   # Debe responder: {"status":"UP"}
   ```

## 📝 Ver Logs en Railway

1. Dashboard → Tu servicio → "View Logs"
2. Buscar mensajes clave:
   - ✅ "Started InventarioElectronicaApplication in X seconds"
   - ✅ "Tomcat started on port(s)"
   - ❌ "APPLICATION FAILED TO START"
   - ❌ "SQLException" / "Connection refused"

## 🔒 Security Notes

- **JWT_SECRET:** Cambiar en producción, usar mínimo 32 caracteres aleatorios
- **Database credentials:** Railway las maneja automáticamente
- **No hardcodear** secrets en código ni commits
- Usar variables de entorno para toda configuración sensible

## 🆘 Si Nada Funciona

1. Borrar y recrear el servicio en Railway
2. Verificar región de PostgreSQL (debe ser igual que la app)
3. Revisar límites de memoria (free tier = 512MB)
4. Contactar soporte de Railway con logs específicos
