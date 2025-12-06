# 📚 Índice del Proyecto - Sistema de Inventario Electrónica

Guía de navegación de toda la documentación del proyecto.

---

## 🚀 Para Empezar

### 1. Instalación Local
- **[README.md](./README.md)** - Documentación principal del proyecto
  - Descripción general
  - Arquitectura y tecnologías
  - Instalación local
  - Endpoints API

### 2. Ejecutar con Docker
- **[DOCKER-GUIDE.md](./DOCKER-GUIDE.md)** - Guía completa de Docker
  - Setup inicial
  - Comandos útiles
  - Troubleshooting Docker
  - Desarrollo con Docker

---

## ☁️ Deployment en la Nube

### Opción Recomendada: Railway

#### Inicio Rápido (5 min)
- **[QUICK-START-DEPLOY.md](./QUICK-START-DEPLOY.md)** ⭐ **EMPIEZA AQUÍ**
  - Deploy en Railway en 5 minutos
  - Pasos mínimos necesarios
  - Verificación rápida

#### Guía Completa
- **[RAILWAY-DEPLOYMENT.md](./RAILWAY-DEPLOYMENT.md)** - Guía detallada Railway
  - Setup paso a paso
  - Configuración de variables
  - Troubleshooting completo
  - CI/CD automático
  - Monitoreo y logs

### Opción Alternativa: Render
- **[RENDER-DEPLOYMENT.md](./RENDER-DEPLOYMENT.md)** - Guía completa Render
  - Setup Render desde cero
  - Diferencias con Railway
  - Configuración de PostgreSQL
  - Limitaciones Free tier

### Checklist Universal
- **[DEPLOYMENT-CHECKLIST.md](./DEPLOYMENT-CHECKLIST.md)** - Checklist completo
  - Pre-deployment
  - Variables de entorno
  - Verificación post-deploy
  - Testing en producción
  - Troubleshooting común

---

## 🧪 Testing & Validación

- **[API-TESTING.md](./API-TESTING.md)** - Testing completo de API
  - Comandos cURL para todos los endpoints
  - Scripts automatizados de testing
  - Workflow completo de prueba
  - Casos de prueba críticos

---

## 🗂️ Estructura del Proyecto

```
bootcamp-sistema-inventario-electronica/
├── 📄 README.md                    # Documentación principal
├── 📄 QUICK-START-DEPLOY.md        # ⭐ Deploy rápido (5 min)
├── 📄 RAILWAY-DEPLOYMENT.md        # Guía Railway completa
├── 📄 RENDER-DEPLOYMENT.md         # Guía Render completa
├── 📄 DEPLOYMENT-CHECKLIST.md      # Checklist deployment
├── 📄 API-TESTING.md               # Testing de API
├── 📄 DOCKER-GUIDE.md              # Guía Docker completa
├── 📄 INDEX.md                     # Este archivo
│
├── 🐳 Dockerfile                   # Imagen Docker multi-stage
├── 🐳 docker-compose.yml           # Orquestación local
├── 🐳 .dockerignore                # Exclusiones Docker
│
├── ⚙️ pom.xml                      # Configuración Maven
├── ⚙️ .env.example                 # Template variables entorno
├── ⚙️ .env                         # Variables locales (no en git)
│
├── 🔧 setup-env.ps1                # Script setup Windows
├── 🔧 setup-env.sh                 # Script setup Linux/Mac
├── 🔧 start.ps1                    # Inicio rápido Windows
├── 🔧 start.sh                     # Inicio rápido Linux/Mac
│
├── 📂 src/main/java/               # Código fuente Java
│   └── com/bootcamp/inventario/
│       ├── config/                 # Configuraciones (Security, Swagger)
│       ├── controller/             # Controladores REST
│       ├── dto/                    # Data Transfer Objects
│       ├── exception/              # Manejo de excepciones
│       ├── model/                  # Entidades JPA
│       ├── repository/             # Repositorios Spring Data
│       ├── security/               # JWT, Filters, UserDetails
│       └── service/                # Lógica de negocio
│
├── 📂 src/main/resources/
│   ├── application.properties      # Config desarrollo
│   └── application-prod.properties # Config producción
│
├── 📂 init-scripts/                # Scripts SQL inicialización
│   ├── 01-init-data.sql            # Placeholder schema
│   └── 02-sample-data.sql          # Datos de ejemplo
│
├── 📂 .github/workflows/           # GitHub Actions (opcional)
│   └── railway-deploy.yml          # CI/CD workflow
│
└── 📂 uploads/                     # Archivos subidos (Gerber, certs)
```

---

## 🎯 Workflows Comunes

### 1. Desarrollo Local con Docker
```bash
# Setup inicial
./setup-env.ps1        # Windows
./setup-env.sh         # Linux/Mac

# Iniciar todo
./start.ps1            # Windows
./start.sh             # Linux/Mac

# Acceder
http://localhost:8081/swagger-ui.html
```

**Documentación**: [DOCKER-GUIDE.md](./DOCKER-GUIDE.md)

---

### 2. Deploy en Railway (Recomendado)
```bash
# Paso 1: Push a GitHub
git push origin main

# Paso 2: Seguir guía rápida
# Ver: QUICK-START-DEPLOY.md

# Paso 3: Verificar
curl https://tu-app.up.railway.app/actuator/health
```

**Documentación**: 
1. [QUICK-START-DEPLOY.md](./QUICK-START-DEPLOY.md) - 5 minutos
2. [RAILWAY-DEPLOYMENT.md](./RAILWAY-DEPLOYMENT.md) - Completa
3. [DEPLOYMENT-CHECKLIST.md](./DEPLOYMENT-CHECKLIST.md) - Verificación

---

### 3. Testing Completo de API
```bash
# Ver todos los comandos cURL en:
# API-TESTING.md

# O usar Swagger UI:
https://tu-app.up.railway.app/swagger-ui.html
```

**Documentación**: [API-TESTING.md](./API-TESTING.md)

---

## 🔑 Variables de Entorno Críticas

### Desarrollo Local
Ver archivo `.env.example` con todas las variables comentadas.

### Producción (Railway/Render)
Variables mínimas necesarias:
```env
SPRING_DATASOURCE_URL=<auto-generada-por-railway>
SPRING_DATASOURCE_USERNAME=<auto-generada>
SPRING_DATASOURCE_PASSWORD=<auto-generada>
JWT_SECRET=<generar-con-openssl>
SPRING_PROFILES_ACTIVE=prod
```

**Generar JWT_SECRET**:
```bash
openssl rand -base64 32
```

---

## 🆘 Troubleshooting

### Problema: No compila localmente
- **Solución**: Usar Docker (el build interno de Maven funciona)
- **Ver**: [DOCKER-GUIDE.md](./DOCKER-GUIDE.md)

### Problema: Deploy fallido en Railway
- **Solución**: Revisar checklist y logs
- **Ver**: [DEPLOYMENT-CHECKLIST.md](./DEPLOYMENT-CHECKLIST.md) - Sección Troubleshooting

### Problema: 401 Unauthorized en API
- **Solución**: Verificar JWT_SECRET configurado
- **Ver**: [API-TESTING.md](./API-TESTING.md) - Sección Autenticación

### Problema: No conecta a PostgreSQL
- **Solución**: Verificar variables DATABASE_URL
- **Ver**: [RAILWAY-DEPLOYMENT.md](./RAILWAY-DEPLOYMENT.md) - Sección Troubleshooting

---

## 📊 Estado del Proyecto

### ✅ Completado (15/15 - 100%)
1. ✅ Configuración Spring Boot
2. ✅ Modelo de datos (8 entidades)
3. ✅ Spring Security + JWT
4. ✅ CRUD Componentes
5. ✅ Módulo Placas
6. ✅ Solicitudes Mecanizado
7. ✅ Solicitudes Armado
8. ✅ Sistema de Tareas
9. ✅ Upload Certificaciones
10. ✅ Manejo de Excepciones
11. ✅ Documentación Swagger
12. ✅ README profesional
13. ✅ Dockerización completa
14. ✅ Deployment Documentation
15. ✅ **Video Tutorial Guide** ← Acabamos de completar esto

### ⏳ Pendiente (0/15 - 0%)
**¡PROYECTO 100% COMPLETADO!** 🎉

---

## 🎥 Próximo Paso: Video Tutorial

### Guion Sugerido
1. **Intro (1 min)**
   - Problema que resuelve el sistema
   - Stack tecnológico usado

2. **Demo Swagger (3 min)**
   - Crear usuario, login
   - CRUD de componentes
   - Crear solicitud de armado
   - Verificar stock

3. **Código Clave (3 min)**
   - Arquitectura en capas
   - Spring Security + JWT
   - JPA Relationships
   - Global Exception Handler

4. **Deployment (1 min)**
   - Mostrar Railway dashboard
   - URL pública funcionando

5. **Cierre (1 min)**
   - Desafíos superados
   - Próximos pasos

**Duración Total**: 8-10 minutos

---

## 📞 Soporte

Si encuentras problemas:
1. Revisa la guía específica en este índice
2. Busca en la sección Troubleshooting
3. Revisa logs de Railway/Render/Docker

---

## 🏆 Logros del Proyecto

- ✅ API RESTful completa con 40+ endpoints
- ✅ Seguridad JWT con roles (ADMIN, OPERADOR, CLIENTE)
- ✅ Sistema de inventario con validación de stock
- ✅ Upload de archivos (Gerber, certificaciones)
- ✅ Docker multi-stage build optimizado
- ✅ Documentación Swagger interactiva
- ✅ Deploy automático en Railway
- ✅ 7 guías de documentación completas
- ✅ Scripts automatizados para desarrollo

---

**Última actualización**: Diciembre 2024
**Estado**: ✅ Listo para deployment y demo
