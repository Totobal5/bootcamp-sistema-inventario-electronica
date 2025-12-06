# 📋 DATOS DE PRUEBA PARA VIDEO

Copiar y pegar estos JSONs durante la demo de Swagger.

---

## 1️⃣ AUTENTICACIÓN

### Registro ADMIN
```json
{
  "username": "admin",
  "email": "admin@empresa.com",
  "password": "admin123",
  "nombre": "Admin Sistema",
  "rol": "ADMIN"
}
```

### Registro OPERADOR
```json
{
  "username": "operador1",
  "email": "operador@empresa.com",
  "password": "oper123",
  "nombre": "Juan Operador",
  "rol": "OPERADOR"
}
```

### Registro CLIENTE
```json
{
  "username": "cliente1",
  "email": "cliente@empresa.com",
  "password": "cliente123",
  "nombre": "María Cliente",
  "rol": "CLIENTE"
}
```

### Login (todos usan la misma estructura)
```json
{
  "username": "admin",
  "password": "admin123"
}
```

---

## 2️⃣ COMPONENTES ELECTRÓNICOS

### Componente 1: Resistencia
```json
{
  "nombre": "Resistencia 10kΩ",
  "descripcion": "Resistencia de carbón 1/4W 5%",
  "categoria": "RESISTENCIA",
  "stock": 100,
  "precioUnitario": 0.05
}
```

### Componente 2: LED Rojo
```json
{
  "nombre": "LED Rojo 5mm",
  "descripcion": "LED rojo ultra brillante",
  "categoria": "LED",
  "stock": 50,
  "precioUnitario": 0.15
}
```

### Componente 3: Capacitor
```json
{
  "nombre": "Capacitor 100nF",
  "descripcion": "Capacitor cerámico 50V",
  "categoria": "CAPACITOR",
  "stock": 75,
  "precioUnitario": 0.10
}
```

### Componente 4: Transistor
```json
{
  "nombre": "Transistor BC547",
  "descripcion": "Transistor NPN uso general",
  "categoria": "TRANSISTOR",
  "stock": 60,
  "precioUnitario": 0.20
}
```

---

## 3️⃣ PLACAS PCB

### Placa 1: LED Intermitente
```json
{
  "nombre": "Placa LED Intermitente",
  "descripcion": "Circuito básico con LED y resistencia",
  "componentes": [
    {
      "componenteId": 1,
      "cantidadNecesaria": 2
    },
    {
      "componenteId": 2,
      "cantidadNecesaria": 1
    }
  ]
}
```

### Placa 2: Oscilador
```json
{
  "nombre": "Placa Oscilador 555",
  "descripcion": "Oscilador básico con timer 555",
  "componentes": [
    {
      "componenteId": 1,
      "cantidadNecesaria": 3
    },
    {
      "componenteId": 3,
      "cantidadNecesaria": 2
    },
    {
      "componenteId": 4,
      "cantidadNecesaria": 1
    }
  ]
}
```

---

## 4️⃣ SOLICITUDES DE MECANIZADO

### Solicitud Mecanizado 1
```json
{
  "placaId": 1,
  "observaciones": "Primera versión del diseño, revisar dimensiones"
}
```

**Nota**: Además requiere subir archivo Gerber (multipart form-data)

---

## 5️⃣ SOLICITUDES DE ARMADO

### Solicitud Armado 1 (CLIENTE)
```json
{
  "placaId": 1,
  "cantidad": 5,
  "prioridad": "ALTA",
  "observaciones": "Entrega urgente para cliente X"
}
```

### Solicitud Armado 2 (CLIENTE)
```json
{
  "placaId": 2,
  "cantidad": 10,
  "prioridad": "MEDIA",
  "observaciones": "Stock para tener disponible"
}
```

---

## 6️⃣ TAREAS

### Tarea 1 (OPERADOR)
```json
{
  "solicitudArmadoId": 1,
  "descripcion": "Armar 5 placas LED intermitente",
  "fechaEstimadaFinalizacion": "2024-12-31"
}
```

### Cambiar Estado: EN_PROCESO
```json
{
  "estado": "EN_PROCESO",
  "observaciones": "Iniciando proceso de armado"
}
```

### Cambiar Estado: COMPLETADA
```json
{
  "estado": "COMPLETADA",
  "observaciones": "Armado finalizado exitosamente, listo para QA"
}
```

---

## 7️⃣ CERTIFICACIONES

### Certificación 1 (OPERADOR)
```json
{
  "solicitudArmadoId": 1,
  "tipo": "CERTIFICACION",
  "descripcion": "Certificado de calidad ISO 9001"
}
```

**Nota**: Además requiere subir archivo PDF/imagen (multipart form-data)

---

## 📝 ORDEN SUGERIDO PARA EL VIDEO

1. Register ADMIN → Login ADMIN → Authorize
2. Crear Componente 1 (Resistencia)
3. Crear Componente 2 (LED)
4. Crear Componente 3 (Capacitor)
5. Listar componentes (GET /api/componentes)
6. Crear Placa 1 (LED Intermitente)
7. Verificar disponibilidad (GET /api/placas/1/verificar-disponibilidad)
8. Register CLIENTE → Login CLIENTE → Authorize
9. Crear Solicitud Armado 1 (5 placas)
10. Register OPERADOR → Login OPERADOR → Authorize
11. Crear Tarea 1
12. Cambiar estado tarea: EN_PROCESO
13. Cambiar estado tarea: COMPLETADA

**Duración estimada**: 2.5 minutos

---

## 🎯 TIPS PARA LA DEMO

- Copiar todos estos JSONs a un archivo .txt antes de empezar
- Tener Notepad++ o similar abierto en segunda pantalla
- Hacer Ctrl+C / Ctrl+V rápido durante la grabación
- Si algo falla, pausar y editar después
- Tener la aplicación pre-corriendo en localhost

---

## ⚠️ PREPARACIÓN PRE-GRABACIÓN

Antes de grabar, ejecutar estos pasos:

```bash
# 1. Iniciar Docker
docker-compose up -d

# 2. Verificar que está corriendo
curl http://localhost:8081/actuator/health

# 3. Abrir Swagger
# http://localhost:8081/swagger-ui.html

# 4. Limpiar base de datos (opcional)
docker-compose down -v
docker-compose up -d
```

---

## 🔄 ALTERNATIVA: USAR RAILWAY

Si prefieres hacer la demo en producción:

```
https://tu-app.up.railway.app/swagger-ui.html
```

**Ventajas**:
- Demuestra deployment real
- No depende de localhost

**Desventajas**:
- Latencia de red
- Menos control si algo falla

**Recomendación**: Usar localhost para la demo, mostrar Railway al final.
