# 🎥 Guía para Video Tutorial YouTube

Tutorial completo para grabar y publicar el video del proyecto.

---

## 🎯 Objetivo del Video

**Duración**: 8-10 minutos  
**Formato**: Tutorial técnico / Project showcase  
**Audiencia**: Reclutadores, compañeros de bootcamp, comunidad dev  
**Plataforma**: YouTube (unlisted o público)

---

## 📝 Guion Detallado

### 🎬 1. INTRO (1 minuto)

**[Pantalla inicial con título del proyecto]**

> "Hola, soy [Tu Nombre] y en este video te voy a mostrar mi proyecto final del Bootcamp de Desarrollo Backend con Java y Spring Boot."

**[Mostrar README.md en GitHub]**

> "Desarrollé un Sistema de Inventario de Componentes Electrónicos completo, que resuelve la gestión integral de stock, diseño de placas PCB, solicitudes de mecanizado y armado, con un sistema de tareas y certificaciones de calidad."

**Problema que resuelve:**
- Control de inventario en tiempo real
- Trazabilidad completa de solicitudes
- Validación automática de stock
- Gestión de archivos técnicos (Gerber, certificaciones)

---

### 💻 2. STACK TECNOLÓGICO (1 minuto)

**[Mostrar sección de tecnologías en README]**

> "El proyecto está construido con tecnologías modernas de backend:"

**Backend:**
- ✅ Java 17 (LTS)
- ✅ Spring Boot 3.2.0
- ✅ Spring Security + JWT
- ✅ Spring Data JPA (Hibernate)
- ✅ PostgreSQL 15

**DevOps:**
- ✅ Docker + Docker Compose
- ✅ Railway (deployment)
- ✅ GitHub Actions (CI/CD)

**Documentación:**
- ✅ Swagger/OpenAPI
- ✅ 8 guías completas en Markdown

**[Mostrar brevemente arquitectura en capas]**

---

### 🗄️ 3. MODELO DE DATOS (1.5 minutos)

**[Abrir diagrama ER o mostrar entidades en código]**

> "El sistema tiene 8 entidades principales con relaciones complejas:"

**Entidades clave:**

1. **Usuario** (roles: ADMIN, OPERADOR, CLIENTE)
2. **ComponenteElectronico** (inventario)
3. **Placa** (diseños PCB)
4. **PlacaComponente** (ManyToMany con cantidad necesaria)
5. **SolicitudMecanizado** (con archivos Gerber versionados)
6. **SolicitudArmado** (con validación de stock)
7. **Tarea** (estados del workflow)
8. **ArchivoCertificacion** (PDF/imágenes)

**[Mostrar código de una entidad, ej: Placa.java]**

```java
@Entity
@ManyToMany relationship con ComponenteElectronico
@OneToMany bidireccional con PlacaComponente
```

**Destacar:**
- Relaciones bidireccionales
- Cascade types apropiados
- Validaciones con @Column

---

### 🔐 4. SEGURIDAD JWT (1.5 minutos)

**[Abrir SecurityConfig.java]**

> "La seguridad está implementada con JWT stateless:"

**Componentes:**
1. **JwtTokenProvider** - Genera y valida tokens
2. **JwtAuthenticationFilter** - Intercepta requests
3. **UserDetailsServiceImpl** - Carga usuarios de BD
4. **SecurityConfig** - Configuración de seguridad

**[Mostrar método de generación de token]**

```java
public String generateToken(Authentication authentication) {
    // Expira en 24 horas
    // Secret key de 256 bits desde env
}
```

**[Mostrar endpoints públicos vs protegidos en Swagger]**

**Endpoints públicos:**
- `/api/auth/register`
- `/api/auth/login`

**Protegidos por rol:**
- `@PreAuthorize("hasRole('ADMIN')")` → Crear componentes
- `@PreAuthorize("hasRole('OPERADOR')")` → Cambiar estados tareas
- `@PreAuthorize("hasRole('CLIENTE')")` → Crear solicitudes

---

### 🚀 5. DEMO DE API EN SWAGGER (2.5 minutos)

**[Abrir Swagger UI en Railway/localhost]**

> "Ahora voy a demostrar el workflow completo del sistema usando la documentación Swagger interactiva."

#### 5.1 Autenticación (30 seg)

**[Ejecutar POST /api/auth/register]**

```json
{
  "username": "admin",
  "email": "admin@empresa.com",
  "password": "admin123",
  "nombre": "Admin Sistema",
  "rol": "ADMIN"
}
```

**[Ejecutar POST /api/auth/login]**
- Mostrar token JWT devuelto
- Copiar token para Authorize

**[Click en botón "Authorize" en Swagger]**
- Pegar token: `Bearer eyJ...`

---

#### 5.2 CRUD de Componentes (30 seg)

**[POST /api/componentes]**

```json
{
  "nombre": "Resistencia 10kΩ",
  "descripcion": "1/4W 5%",
  "categoria": "RESISTENCIA",
  "stock": 100,
  "precioUnitario": 0.05
}
```

**[Crear 2-3 componentes más rápidamente]**

**[GET /api/componentes]**
- Mostrar lista de componentes creados
- Destacar paginación si está implementada

---

#### 5.3 Crear Placa con Componentes (30 seg)

**[POST /api/placas]**

```json
{
  "nombre": "Placa LED Intermitente",
  "descripcion": "Circuito básico de prueba",
  "componentes": [
    {"componenteId": 1, "cantidadNecesaria": 2},
    {"componenteId": 2, "cantidadNecesaria": 1}
  ]
}
```

**[GET /api/placas/1/verificar-disponibilidad]**
- Mostrar que verifica stock suficiente

---

#### 5.4 Solicitud de Armado (30 seg)

**[Registrar usuario CLIENTE primero]**

**[Login como CLIENTE]**

**[POST /api/solicitudes-armado]**

```json
{
  "placaId": 1,
  "cantidad": 5,
  "prioridad": "ALTA",
  "observaciones": "Entrega urgente"
}
```

**Destacar:**
> "El sistema valida automáticamente que hay stock suficiente antes de crear la solicitud."

---

#### 5.5 Gestión de Tareas (30 seg)

**[Login como OPERADOR]**

**[POST /api/tareas]**

```json
{
  "solicitudArmadoId": 1,
  "descripcion": "Armar 5 placas LED",
  "fechaEstimadaFinalizacion": "2024-12-31"
}
```

**[PATCH /api/tareas/1/estado]**

Cambios de estado:
1. PENDIENTE → EN_PROCESO
2. EN_PROCESO → COMPLETADA

**[Mostrar logs de fechas automáticas]**

---

### 💡 6. CÓDIGO CLAVE (1.5 minutos)

**[Volver a VS Code]**

#### 6.1 Validación de Stock (30 seg)

**[Abrir SolicitudArmadoServiceImpl.java]**

```java
// Método validarStock()
private void validarStock(Placa placa, int cantidad) {
    for (PlacaComponente pc : placa.getComponentes()) {
        int stockNecesario = pc.getCantidadNecesaria() * cantidad;
        if (componente.getStock() < stockNecesario) {
            throw new InsufficientStockException(...);
        }
    }
}
```

> "Este método verifica que cada componente de la placa tenga stock suficiente antes de crear la solicitud."

---

#### 6.2 Descuento Automático de Stock (30 seg)

**[Mostrar método confirmarArmado()]**

```java
public void confirmarArmado(Long id) {
    // 1. Verificar estado
    // 2. Descontar stock de cada componente
    for (PlacaComponente pc : placa.getComponentes()) {
        int stockDescontar = pc.getCantidadNecesaria() * cantidad;
        componente.setStock(componente.getStock() - stockDescontar);
    }
    // 3. Actualizar estado a COMPLETADO
}
```

---

#### 6.3 Manejo de Excepciones (30 seg)

**[Abrir GlobalExceptionHandler.java]**

```java
@ExceptionHandler(InsufficientStockException.class)
public ResponseEntity<ErrorResponse> handleInsufficientStock(...) {
    return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
}
```

> "Todas las excepciones se manejan centralizadamente, devolviendo respuestas JSON consistentes."

---

### 🐳 7. DOCKER & DEPLOYMENT (1 minuto)

**[Mostrar Dockerfile]**

> "El proyecto está completamente dockerizado con multi-stage build:"

**Stage 1: Build**
- Maven + JDK 17 compila el proyecto

**Stage 2: Runtime**
- JRE 17 ligero (~200 MB menos)
- Usuario no-root por seguridad

**[Mostrar docker-compose.yml]**

**[Mostrar Railway dashboard brevemente]**
- Deployment automático desde GitHub
- PostgreSQL provisionada
- URL pública funcionando

> "Cada push a main despliega automáticamente en Railway."

---

### 🎓 8. DESAFÍOS TÉCNICOS SUPERADOS (30 seg)

**Desafíos principales:**

1. **Relaciones ManyToMany complejas**
   - PlacaComponente con cantidad necesaria
   - Evitar orphan removal incorrecto

2. **Validación de stock transaccional**
   - @Transactional para evitar race conditions
   - Locks optimistas con @Version (si implementado)

3. **Upload de archivos**
   - Manejo de multipart/form-data
   - Storage en filesystem con paths relativos
   - Versionado de archivos Gerber

4. **JWT Security**
   - Filter chain correcto
   - CORS configuration
   - Role-based access control

5. **Docker Lombok issues**
   - Annotation processing en Maven
   - Workaround con Docker build

---

### 🎉 9. CONCLUSIÓN (30 seg)

**[Volver a mostrar README en GitHub]**

> "Este proyecto demuestra mis habilidades en:"

✅ **Backend robusto** con Spring Boot  
✅ **Seguridad** con JWT y roles  
✅ **Persistencia** con JPA y relaciones complejas  
✅ **Documentación** completa (8 guías Markdown)  
✅ **DevOps** con Docker y deployment  
✅ **Testing** documentado con cURL  

**[Mostrar stats del proyecto]**
- 71 archivos Java
- 8 entidades, 40+ endpoints
- 83 KB de documentación
- 100% funcional en producción

**Llamado a la acción:**
> "El código completo está en GitHub: [tu-repo]"
> 
> "Puedes probar la API en: [URL de Railway]"
> 
> "Gracias por ver el video. ¡Déjame un comentario si tienes preguntas!"

---

## 🎬 Preparación para Grabación

### Checklist Pre-Grabación

#### Código
- [ ] Aplicación corriendo en localhost:8081
- [ ] PostgreSQL activo (Docker)
- [ ] Swagger UI cargando correctamente
- [ ] VS Code con código limpio y organizado

#### Deployment
- [ ] Railway desplegado y funcionando
- [ ] URL pública accesible
- [ ] Swagger UI de producción funcionando

#### Herramientas
- [ ] OBS Studio configurado (o software de grabación)
- [ ] Micrófono testeado
- [ ] Pantalla limpia (cerrar apps innecesarias)
- [ ] Zoom apropiado para lectura

#### Datos de Prueba
- [ ] Componentes creados previamente (o listos para crear)
- [ ] JSONs de ejemplo copiados y listos

---

## 🛠️ Configuración de Grabación

### Software Recomendado

**OBS Studio** (gratis):
1. Descargar: https://obsproject.com/
2. Configuración:
   - **Resolución**: 1920x1080 (Full HD)
   - **FPS**: 30
   - **Bitrate**: 2500-4000 kbps
   - **Formato**: MP4

**Alternativas:**
- Loom (más simple, online)
- Camtasia (pago, más profesional)
- Grabador de pantalla de Windows (Win+G)

---

### Configuración de Audio

- **Micrófono externo** (recomendado)
- **Audacity** para post-edición si es necesario
- **Reducción de ruido** ambiente
- **Volumen consistente**

---

### Configuración de Pantalla

**Resolución**: 1920x1080 recomendada

**Zoom de VS Code**: 
```
Ctrl + (aumentar)
Ctrl - (disminuir)
```
Usar zoom 110-120% para lectura

**Navegador**: 
- Zoom 100-110%
- Modo incógnito (sin extensiones)

**Terminal**:
- Font size 14-16
- Colores contrastados

---

## 🎨 Consejos de Presentación

### Durante la Demo

✅ **Hablar claro y pausado**
✅ **Explicar qué haces ANTES de hacerlo**
✅ **Resaltar partes importantes del código**
✅ **Usar el cursor para señalar**
✅ **Pausas breves entre secciones**

❌ **No ir muy rápido**
❌ **No asumir conocimiento previo**
❌ **No quedarte en silencio**
❌ **No leer código línea por línea**

---

### Edición Post-Grabación (Opcional)

**Software**: DaVinci Resolve (gratis) o iMovie (Mac)

**Ediciones básicas:**
- Cortar silencios largos
- Añadir intro/outro
- Música de fondo suave (sin copyright)
- Transiciones suaves entre secciones
- Subtítulos (auto-generados en YouTube)

---

## 📤 Publicación en YouTube

### Configuración del Video

**Título sugerido:**
> "Sistema de Inventario Electrónica - Spring Boot 3 + PostgreSQL + JWT | Proyecto Full Stack"

**Descripción:**
```
Sistema completo de gestión de inventario de componentes electrónicos desarrollado con Spring Boot 3, PostgreSQL, Docker y Railway.

🔧 TECNOLOGÍAS:
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- PostgreSQL 15
- Docker
- Railway

📚 FEATURES:
✅ Autenticación JWT con roles
✅ CRUD de componentes
✅ Sistema de placas PCB
✅ Solicitudes de mecanizado y armado
✅ Validación automática de stock
✅ Upload de archivos (Gerber, certificaciones)
✅ API documentada con Swagger
✅ Dockerizado completamente

🔗 LINKS:
- GitHub: [tu-repo]
- API en vivo: [URL Railway]
- Swagger UI: [URL]/swagger-ui.html

⏱️ TIMESTAMPS:
0:00 - Intro
1:00 - Stack Tecnológico
2:00 - Modelo de Datos
3:30 - Seguridad JWT
5:00 - Demo API Swagger
7:30 - Código Clave
9:00 - Docker & Deployment
9:30 - Conclusión

#SpringBoot #Java #Backend #PostgreSQL #JWT #Docker #Railway
```

**Tags:**
```
spring boot, java, backend, postgresql, jwt, docker, api rest, spring security, swagger, railway, bootcamp, proyecto final, inventario
```

---

### Configuración de Privacidad

**Opciones:**

1. **Unlisted** (recomendado para portfolio):
   - Solo accesible con el link
   - Ideal para enviar a reclutadores
   - No aparece en búsquedas

2. **Público**:
   - Visible para todos
   - Aparece en búsquedas
   - Potencialmente más visitas

3. **Privado**:
   - Solo tú puedes verlo
   - No sirve para compartir

---

### Miniatura del Video

**Diseño sugerido:**

```
┌─────────────────────────────────┐
│  [Logo Spring Boot]             │
│                                 │
│  Sistema de Inventario          │
│  Electrónica                    │
│                                 │
│  Spring Boot 3 + PostgreSQL     │
│  + JWT + Docker                 │
│                                 │
│  [Screenshot de Swagger UI]     │
└─────────────────────────────────┘
```

**Herramientas**:
- Canva (gratis, templates)
- Photoshop
- GIMP (gratis)

**Dimensiones**: 1280x720 px

---

## ✅ Checklist Final

### Pre-Grabación
- [ ] Guion leído y practicado
- [ ] Código funcionando perfectamente
- [ ] Datos de prueba preparados
- [ ] Software de grabación configurado
- [ ] Audio testeado
- [ ] Pantalla limpia

### Durante Grabación
- [ ] Hablar claro y pausado
- [ ] Seguir el guion (pero natural)
- [ ] Mostrar código relevante
- [ ] Demo completa en Swagger
- [ ] Mencionar desafíos superados

### Post-Grabación
- [ ] Edición básica (si necesario)
- [ ] Miniatura diseñada
- [ ] Título optimizado
- [ ] Descripción completa con links
- [ ] Tags agregados
- [ ] Timestamps en descripción

### Publicación
- [ ] Video subido a YouTube
- [ ] Privacidad configurada (Unlisted/Público)
- [ ] Link agregado al README.md
- [ ] Link compartido en bootcamp
- [ ] Link en LinkedIn (opcional)

---

## 🎯 Métricas de Éxito

**Duración final**: 8-10 minutos ✅  
**Calidad de audio**: Clara y sin ruido ✅  
**Calidad de video**: 1080p mínimo ✅  
**Código visible**: Zoom apropiado ✅  
**Demo funcional**: Sin errores ✅  
**Explicaciones claras**: Conceptos bien explicados ✅  

---

## 📝 Notas Adicionales

### Si cometes un error durante la grabación:
- **Pausa** y respira
- **Continúa desde el último punto bueno**
- **Edita después** (o graba por secciones)

### Si el video queda muy largo:
- Edita las partes lentas
- Acelera secciones repetitivas (1.2x-1.5x)
- Corta silencios largos

### Si te trabas explicando:
- **Practica antes** las secciones difíciles
- **Lee el guion** varias veces
- **Graba por partes** y une después

---

## 🚀 Próximos Pasos

1. **Practicar el guion** (sin grabar)
2. **Probar la grabación** (1-2 min de prueba)
3. **Grabar el video completo**
4. **Editar** (si necesario)
5. **Subir a YouTube**
6. **Compartir el link**

---

## 🎉 ¡Éxito!

Una vez publicado:
- ✅ Proyecto 100% completo (15/15 tareas)
- ✅ Video en YouTube
- ✅ Portfolio listo para mostrar
- ✅ Bootcamp finalizado 🎓

**¡FELICITACIONES!** 🎊

---

**Última actualización**: Diciembre 2024  
**Estado**: Listo para grabar 🎬
