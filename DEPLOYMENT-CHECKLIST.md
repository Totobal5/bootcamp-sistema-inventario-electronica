# 🚀 Checklist de Deployment

Esta checklist te ayudará a completar el deployment paso a paso en Railway o Render.

## 📋 Pre-Deployment

### Código
- [ ] Último commit pusheado a GitHub en branch `main`
- [ ] `application-prod.properties` configurado correctamente
- [ ] Dockerfile funcional (verificado con `docker build .`)
- [ ] `.gitignore` excluye `.env`, `target/`, archivos sensibles
- [ ] README.md actualizado con instrucciones claras

### Seguridad
- [ ] JWT_SECRET NO está hardcodeado en código
- [ ] Passwords de base de datos NO están en application.properties
- [ ] `.env` NO está en GitHub
- [ ] application-prod.properties usa variables de entorno

---

## 🛤️ Railway Deployment

### Setup Inicial
- [ ] Cuenta creada en https://railway.app
- [ ] Repositorio GitHub conectado a Railway
- [ ] Proyecto Railway creado

### Base de Datos
- [ ] PostgreSQL provisionada desde Railway dashboard
- [ ] Base de datos está "Active" (luz verde)
- [ ] Variables de conexión generadas automáticamente:
  - `DATABASE_URL`
  - `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`

### Variables de Entorno
- [ ] `SPRING_DATASOURCE_URL` configurada (Railway Reference o manual)
- [ ] `SPRING_DATASOURCE_USERNAME` configurada
- [ ] `SPRING_DATASOURCE_PASSWORD` configurada
- [ ] `JWT_SECRET` generada con `openssl rand -base64 32`
- [ ] `JWT_EXPIRATION` = `86400000` (24 horas)
- [ ] `SPRING_PROFILES_ACTIVE` = `prod`
- [ ] `SPRING_JPA_HIBERNATE_DDL_AUTO` = `update`
- [ ] `SPRING_JPA_SHOW_SQL` = `false`
- [ ] `FILE_UPLOAD_DIR` = `/app/uploads`
- [ ] `LOGGING_LEVEL_COM_BOOTCAMP_INVENTARIO` = `INFO`

### Build & Deploy
- [ ] Railway detecta Dockerfile automáticamente
- [ ] Primer deploy iniciado
- [ ] Logs sin errores críticos
- [ ] Build completo exitoso (verde)
- [ ] Aplicación en estado "Active"

### Networking
- [ ] Dominio público generado: `*.up.railway.app`
- [ ] Puerto configurado correctamente (Railway usa PORT dinámico)
- [ ] URL pública accesible

---

## 🟢 Render Deployment (Alternativa)

### Setup Inicial
- [ ] Cuenta creada en https://render.com
- [ ] Repositorio GitHub autorizado

### Base de Datos
- [ ] PostgreSQL creada: "New → PostgreSQL"
- [ ] Nombre: `inventario-electronica-db`
- [ ] Plan: Free (o Starter si es producción)
- [ ] Estado: "Available"
- [ ] Internal Database URL copiada

### Web Service
- [ ] Web Service creado: "New → Web Service"
- [ ] Repositorio conectado
- [ ] Runtime: Docker (o Build Command + Start Command)
- [ ] Region: Mismo que PostgreSQL

### Variables de Entorno
- [ ] Todas las mismas variables que Railway (ver arriba)
- [ ] `SPRING_DATASOURCE_URL` usa Internal Database URL de Render

### Deploy
- [ ] Primer deploy completo
- [ ] Logs sin errores
- [ ] URL pública: `*.onrender.com`

---

## ✅ Post-Deployment Verification

### Health Checks
```bash
# Reemplaza <URL> con tu URL de Railway/Render

# 1. Health endpoint
curl https://<URL>/actuator/health
# Esperado: {"status":"UP"}

# 2. Swagger UI
# Abre en navegador: https://<URL>/swagger-ui.html
```

- [ ] Health check responde `{"status":"UP"}`
- [ ] Swagger UI carga correctamente
- [ ] Endpoints visibles en Swagger

### Autenticación
```bash
# 3. Registrar usuario ADMIN
curl -X POST https://<URL>/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@empresa.com",
    "password": "admin123",
    "rol": "ADMIN"
  }'
# Esperado: 200 OK con mensaje de éxito

# 4. Login
curl -X POST https://<URL>/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
# Esperado: {"token":"eyJ...","username":"admin","rol":"ADMIN"}
```

- [ ] Registro de usuario exitoso
- [ ] Login devuelve JWT token
- [ ] Token es válido (formato JWT: `eyJ...`)

### Funcionalidades Core
```bash
# 5. Crear componente (requiere token del paso 4)
curl -X POST https://<URL>/api/componentes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_AQUI>" \
  -d '{
    "nombre": "Resistencia 10kΩ",
    "descripcion": "Resistencia de carbón 1/4W",
    "categoria": "RESISTENCIA",
    "stock": 100,
    "precioUnitario": 0.05
  }'
# Esperado: 201 Created

# 6. Listar componentes
curl https://<URL>/api/componentes \
  -H "Authorization: Bearer <TOKEN_AQUI>"
# Esperado: Array JSON con componente creado
```

- [ ] Crear componente exitoso (201 Created)
- [ ] Listar componentes devuelve el creado
- [ ] Datos persistidos en PostgreSQL

### Base de Datos
```bash
# Si tienes Railway CLI instalado:
railway run psql $DATABASE_URL

# O desde Render dashboard: Database → Connect

# Verificar tablas creadas:
\dt

# Ver usuarios:
SELECT username, rol FROM usuarios;

# Ver componentes:
SELECT nombre, stock FROM componentes_electronicos;
```

- [ ] Tablas Hibernate creadas correctamente
- [ ] Usuarios insertados visibles
- [ ] Componentes visibles en BD

---

## 🐛 Troubleshooting

### ❌ Error: "Application failed to start"
- [ ] Revisar logs de Railway/Render
- [ ] Verificar todas las variables de entorno
- [ ] Confirmar JWT_SECRET tiene al menos 32 caracteres
- [ ] Verificar DATABASE_URL formato correcto

### ❌ Error: "Connection refused PostgreSQL"
- [ ] PostgreSQL está "Active"
- [ ] `SPRING_DATASOURCE_URL` correcta
- [ ] Usuario y password correctos
- [ ] Puerto correcto (Railway usa puerto interno)

### ❌ Build fallido
- [ ] Dockerfile sintaxis correcta
- [ ] `pom.xml` sin errores
- [ ] Maven compila localmente: `mvn clean package`

### ❌ 401 Unauthorized en endpoints
- [ ] JWT_SECRET está configurado
- [ ] Token se incluye en header: `Authorization: Bearer <token>`
- [ ] Token no expirado (24h)

---

## 📝 Documentación Final

### Actualizar README.md
```markdown
## 🚀 Producción

**URL de Producción**: https://tu-app.up.railway.app

**Swagger UI**: https://tu-app.up.railway.app/swagger-ui.html

**Credenciales de prueba**:
- Usuario: `demo@empresa.com`
- Password: `demo123`
- Rol: CLIENTE
```

- [ ] README actualizado con URL de producción
- [ ] Link a Swagger UI funcional documentado
- [ ] Credenciales de demo (si las hay) documentadas

### Compartir
- [ ] URL enviada al bootcamp/evaluadores
- [ ] Screenshots de Swagger añadidos a README
- [ ] Video demo grabado (Tarea 15)

---

## 🎉 ¡Deployment Completo!

Si todos los checkboxes están marcados, **¡felicitaciones!** 🎊

Tu aplicación está:
- ✅ Desplegada en producción
- ✅ Accesible públicamente vía HTTPS
- ✅ Conectada a PostgreSQL persistente
- ✅ Documentada con Swagger
- ✅ Lista para demos y evaluación

**Próximo paso**: Tarea 15 - Grabar Video Tutorial YouTube 🎥
