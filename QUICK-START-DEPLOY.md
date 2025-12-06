# ⚡ Quick Start - Deployment en 5 Minutos

Guía ultra-rápida para desplegar en Railway (la opción más simple).

## 🚀 Paso a Paso

### 1. Preparar Código ✅
```bash
# Ya está listo - solo verifica:
git status
git add .
git commit -m "feat: preparado para deployment"
git push origin main
```

### 2. Railway Account (2 min) 🎫
1. Ve a https://railway.app
2. "Start a New Project"
3. Login con GitHub ✅

### 3. Deploy (1 min) 🚂
1. "New Project"
2. "Deploy from GitHub repo"
3. Selecciona: `bootcamp-sistema-inventario-electronica`
4. ✅ Railway detecta Dockerfile automáticamente

### 4. Base de Datos (1 min) 🗄️
1. En tu proyecto Railway: "+ New"
2. "Database" → "PostgreSQL"
3. Espera estado "Active" (30 seg)
4. ✅ Variables creadas automáticamente

### 5. Variables de Entorno (1 min) 🔐

En el servicio de tu app, ve a "Variables" y agrega:

```bash
# Generar JWT Secret
openssl rand -base64 32
```

Agrega estas variables:
```env
SPRING_DATASOURCE_URL=${DATABASE_URL}
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<pegar-resultado-openssl-aqui>
JWT_EXPIRATION=86400000
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

Railway **automáticamente** provee:
- `DATABASE_URL`
- `PGHOST`, `PGPORT`, `PGDATABASE`
- `PGUSER`, `PGPASSWORD`

### 6. URL Pública (<1 min) 🌐
1. "Settings" → "Networking"
2. "Generate Domain"
3. ✅ URL: `https://bootcamp-sistema-inventario-electronica-production.up.railway.app`

### 7. Verificar (30 seg) ✅
```bash
# Health check
curl https://tu-app.up.railway.app/actuator/health

# Debería devolver: {"status":"UP"}
```

Abre en navegador:
```
https://tu-app.up.railway.app/swagger-ui.html
```

---

## 🎯 Testing Rápido

### Crear Usuario ADMIN
Desde Swagger UI o:

```bash
curl -X POST https://tu-app.up.railway.app/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@empresa.com",
    "password": "admin123",
    "nombre": "Admin",
    "rol": "ADMIN"
  }'
```

### Login
```bash
curl -X POST https://tu-app.up.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Copia el token** devuelto.

### Crear Componente
```bash
curl -X POST https://tu-app.up.railway.app/api/componentes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_AQUI>" \
  -d '{
    "nombre": "Resistencia 10kΩ",
    "descripcion": "1/4W",
    "categoria": "RESISTENCIA",
    "stock": 100,
    "precioUnitario": 0.05
  }'
```

---

## 🎉 ¡Listo!

Tu API está ahora:
- ✅ Desplegada en Railway
- ✅ Accesible públicamente vía HTTPS
- ✅ Conectada a PostgreSQL
- ✅ Documentada en Swagger

**URL Final**: `https://tu-app.up.railway.app`

---

## 🐛 Si Algo Sale Mal

### Build Fallido
1. Ve a "Deployments" en Railway
2. Click en el deployment fallido
3. Revisa logs (líneas en rojo)

**Causas comunes:**
- Variables de entorno faltantes
- JWT_SECRET muy corto (min 32 chars)
- DATABASE_URL incorrecta

### No Conecta a PostgreSQL
1. Verifica que PostgreSQL esté "Active"
2. En Variables, asegúrate que `SPRING_DATASOURCE_URL` apunta a `${DATABASE_URL}`

### Servicio Activo pero 503 Error
- Espera 1-2 minutos (primera request toma tiempo)
- Verifica logs: `Deployments → View Logs`

---

## 📚 Guías Completas

Si necesitas más detalles:
- **Railway**: [RAILWAY-DEPLOYMENT.md](./RAILWAY-DEPLOYMENT.md)
- **Render**: [RENDER-DEPLOYMENT.md](./RENDER-DEPLOYMENT.md)
- **Checklist**: [DEPLOYMENT-CHECKLIST.md](./DEPLOYMENT-CHECKLIST.md)
- **Testing**: [API-TESTING.md](./API-TESTING.md)

---

## 💰 Costos

Railway Free Tier:
- **$5 USD gratis/mes**
- Incluye PostgreSQL
- Suficiente para demos y desarrollo

---

## ⏭️ Próximo Paso

**Tarea 15**: Grabar video tutorial de YouTube mostrando:
1. Arquitectura del sistema
2. Demo de la API en Swagger
3. Código clave (Security, JPA, Services)
4. Deployment en Railway
5. Testing de endpoints

**Duración**: 8-10 minutos

---

**¿Dudas?** Revisa los logs en Railway o las guías completas arriba ⬆️
