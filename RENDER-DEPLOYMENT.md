# 🚀 Guía de Deployment - Render

Esta guía alternativa te ayudará a desplegar en Render (si prefieres esta plataforma sobre Railway).

## 📋 Prerrequisitos

1. ✅ Cuenta de GitHub con el repositorio
2. ✅ Cuenta en Render (https://render.com)
3. ✅ Código pusheado a GitHub

## 🎯 Paso a Paso - Deployment en Render

### 1️⃣ Crear Cuenta en Render

1. Ve a https://render.com
2. Click en "Get Started"
3. Inicia sesión con GitHub
4. Autoriza Render

### 2️⃣ Crear PostgreSQL Database

1. En dashboard, click "+ New" → "PostgreSQL"
2. Configuración:
   - **Name**: `inventario-electronica-db`
   - **Database**: `inventario_electronica`
   - **User**: `inventario_user` (auto-generado)
   - **Region**: Selecciona el más cercano
   - **Plan**: Free (para desarrollo)
3. Click "Create Database"
4. **Espera 2-3 minutos** a que esté "Available"
5. **Guarda las credenciales** que aparecen (las necesitarás)

### 3️⃣ Crear Web Service

1. Click "+ New" → "Web Service"
2. Conecta GitHub repo: `bootcamp-sistema-inventario-electronica`
3. Configuración:
   - **Name**: `inventario-electronica-api`
   - **Region**: Mismo que la BD
   - **Branch**: `main`
   - **Root Directory**: dejar vacío
   - **Runtime**: Docker (Render detectará el Dockerfile)
   - **Plan**: Free (para desarrollo)

### 4️⃣ Configurar Build

En "Build & Deploy":

**Build Command** (si no usas Docker):
```bash
mvn clean package -DskipTests
```

**Start Command** (si no usas Docker):
```bash
java -jar target/inventario-electronica-1.0.0.jar
```

**Si usas Docker** (recomendado):
- Render detectará automáticamente el `Dockerfile`
- No necesitas configurar build/start commands

### 5️⃣ Configurar Variables de Entorno

En "Environment" del Web Service, agrega:

```env
# Database (obtener de la base de datos creada)
SPRING_DATASOURCE_URL=jdbc:postgresql://dpg-xxxxx.oregon-postgres.render.com/inventario_electronica
SPRING_DATASOURCE_USERNAME=inventario_user
SPRING_DATASOURCE_PASSWORD=<password-generado-por-render>

# JPA/Hibernate
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect

# JWT (generar clave segura)
JWT_SECRET=<generar-con-openssl-rand-base64-32>
JWT_EXPIRATION=86400000

# Server
SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod

# File Uploads
FILE_UPLOAD_DIR=/opt/render/project/src/uploads
SPRING_SERVLET_MULTIPART_MAX_FILE_SIZE=10MB
SPRING_SERVLET_MULTIPART_MAX_REQUEST_SIZE=10MB

# Logging
LOGGING_LEVEL_COM_BOOTCAMP_INVENTARIO=INFO
```

### 6️⃣ Obtener Credenciales de PostgreSQL

1. Ve al dashboard de Render
2. Click en tu base de datos PostgreSQL
3. En "Info", encontrarás:
   - **Internal Database URL**: Úsala para `SPRING_DATASOURCE_URL`
   - **Username**: Para `SPRING_DATASOURCE_USERNAME`
   - **Password**: Para `SPRING_DATASOURCE_PASSWORD`

**Formato de URL Interna:**
```
jdbc:postgresql://dpg-xxxxx-a.oregon-postgres.render.com:5432/inventario_electronica
```

### 7️⃣ Generar JWT Secret

```bash
openssl rand -base64 32
```

Copia el resultado y úsalo para `JWT_SECRET`.

### 8️⃣ Deploy

1. Click "Create Web Service"
2. Render comenzará a buildear la aplicación
3. Puedes ver logs en tiempo real
4. **Primera build toma ~5-10 minutos**

### 9️⃣ Obtener URL Pública

Una vez desplegado:
- URL será: `https://inventario-electronica-api.onrender.com`
- Render proporciona HTTPS automáticamente

### 🔟 Verificar Deployment

**Health Check:**
```bash
curl https://inventario-electronica-api.onrender.com/actuator/health
```

**Swagger UI:**
```
https://inventario-electronica-api.onrender.com/swagger-ui.html
```

**Crear usuario de prueba:**
```bash
curl -X POST https://inventario-electronica-api.onrender.com/api/auth/register \
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

### ❌ Deploy Fallido

**Revisar logs:**
- Ve a "Logs" en el dashboard
- Busca errores en rojo

**Causas comunes:**
1. **Build timeout**: Render Free tiene límite de 15 min
   - Solución: Usar Docker multi-stage build (más rápido)

2. **Puerto incorrecto**: Render asigna PORT dinámico
   ```properties
   server.port=${PORT:8080}
   ```

3. **Out of Memory**: Free tier tiene 512 MB RAM
   ```env
   JAVA_TOOL_OPTIONS=-Xmx400m -Xms256m
   ```

### ❌ No conecta a PostgreSQL

**Verificar:**
1. URL usa el **Internal Database URL** (no External)
2. Formato correcto: `jdbc:postgresql://host:port/database`
3. Usuario y password correctos

### ⚠️ Servicio "Spinning Down"

Render Free **pausa servicios** después de 15 min de inactividad.

**Primera request después de pausa:**
- Toma ~30-60 segundos en despertar
- Es normal en plan Free

**Solución para producción:**
- Upgrade a plan Starter ($7/mes)
- O usar cron job para hacer ping cada 10 min

---

## 🔄 Auto-Deploy

Render despliega automáticamente en cada push a `main`:

```bash
git add .
git commit -m "feat: nueva característica"
git push origin main
# Render detecta y redespliega automáticamente
```

### Desactivar Auto-Deploy

En Settings → "Build & Deploy":
- Desactiva "Auto-Deploy"
- Deploy manual desde dashboard

---

## 📊 Monitoreo

### Logs
```bash
# Ver logs en tiempo real
# Desde dashboard → Logs
```

### Métricas (Solo en planes pagos)
- CPU Usage
- Memory Usage
- Bandwidth

---

## 💰 Costos

### Render Free Tier
- **Web Service**: 750 horas/mes gratis
- **PostgreSQL**: 90 días gratis, luego $7/mes
- **RAM**: 512 MB
- **Build time**: 400 min/mes

**⚠️ Limitaciones Free:**
- Pausa después de 15 min inactividad
- PostgreSQL solo 1 GB storage
- Shared CPU

### Starter Plan ($7/mes por servicio)
- Sin pausa automática
- 512 MB RAM
- PostgreSQL incluida

### Estimación Total
- **Free**: $0 primeros 90 días, luego $7/mes (solo PostgreSQL)
- **Starter**: $14/mes (Web Service + PostgreSQL)

---

## 🎨 Dominio Personalizado

1. Ve a "Settings" del Web Service
2. En "Custom Domain", click "Add Custom Domain"
3. Ingresa: `api.tudominio.com`
4. Configura DNS:
   - Tipo: `CNAME`
   - Nombre: `api`
   - Valor: `inventario-electronica-api.onrender.com`
5. Render genera certificado SSL automáticamente

---

## 🔐 Seguridad

### Environment Groups

Para reutilizar variables:

1. Ve a "Environment" → "Environment Groups"
2. Crea grupo: `inventario-prod`
3. Agrega variables comunes
4. Vincula a múltiples servicios

### Secrets

Nunca expongas:
- JWT_SECRET
- Database passwords
- API keys

Usa variables de entorno de Render.

---

## 🆚 Railway vs Render

| Feature | Railway | Render |
|---------|---------|--------|
| **Free Tier** | $5 crédito/mes | 750h gratis web service |
| **PostgreSQL Free** | Incluido en $5 | 90 días gratis |
| **Auto-sleep** | Opcional | Sí (15 min) |
| **Build Time** | Ilimitado | 400 min/mes |
| **Setup** | Más simple | Más configuración |
| **CLI** | Excelente | Básico |
| **Docker** | Native | Native |

**Recomendación:**
- **Railway**: Mejor para desarrollo y proyectos pequeños
- **Render**: Mejor si necesitas más control y customización

---

## ✅ Checklist Final

- [ ] PostgreSQL creada y accesible
- [ ] Web Service desplegado exitosamente
- [ ] Variables de entorno configuradas
- [ ] JWT_SECRET generado y configurado
- [ ] URL pública funcionando
- [ ] Swagger UI accesible
- [ ] Health check OK
- [ ] Login/Register funcionando
- [ ] Logs sin errores críticos
- [ ] README actualizado con URL

---

## 📚 Recursos

- **Render Docs**: https://render.com/docs
- **Render Status**: https://status.render.com
- **Community**: https://community.render.com

---

## 🎉 ¡Deployment Exitoso!

**URL de tu aplicación:**
- API: https://inventario-electronica-api.onrender.com
- Swagger: https://inventario-electronica-api.onrender.com/swagger-ui.html

**¡Listo para compartir con el mundo!** 🚀
