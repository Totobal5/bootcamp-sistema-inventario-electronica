# 🧪 Testing de API en Producción

Colección de comandos cURL para probar todos los endpoints de la API desplegada en Railway/Render.

**⚠️ IMPORTANTE**: Reemplaza `<URL>` con tu URL de producción (ej: `https://tu-app.up.railway.app`)

---

## 🔐 1. Autenticación

### Registrar Usuario ADMIN
```bash
curl -X POST https://<URL>/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@empresa.com",
    "password": "admin123",
    "nombre": "Administrador Sistema",
    "rol": "ADMIN"
  }'
```

### Registrar Usuario OPERADOR
```bash
curl -X POST https://<URL>/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "operador1",
    "email": "operador@empresa.com",
    "password": "oper123",
    "nombre": "Juan Operador",
    "rol": "OPERADOR"
  }'
```

### Registrar Usuario CLIENTE
```bash
curl -X POST https://<URL>/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "cliente1",
    "email": "cliente@empresa.com",
    "password": "cliente123",
    "nombre": "María Cliente",
    "rol": "CLIENTE"
  }'
```

### Login (obtener JWT token)
```bash
curl -X POST https://<URL>/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Guardar el token** devuelto para usarlo en los siguientes requests:
```bash
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 🔧 2. Componentes Electrónicos

### Crear Componente (ADMIN/OPERADOR)
```bash
curl -X POST https://<URL>/api/componentes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Resistencia 10kΩ",
    "descripcion": "Resistencia de carbón 1/4W 5%",
    "categoria": "RESISTENCIA",
    "stock": 1000,
    "precioUnitario": 0.05
  }'
```

### Listar Todos los Componentes
```bash
curl -X GET https://<URL>/api/componentes \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Componente por ID
```bash
curl -X GET https://<URL>/api/componentes/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar Componente
```bash
curl -X PUT https://<URL>/api/componentes/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "nombre": "Resistencia 10kΩ Premium",
    "descripcion": "Resistencia de película metálica 1/4W 1%",
    "categoria": "RESISTENCIA",
    "stock": 1500,
    "precioUnitario": 0.08
  }'
```

### Eliminar Componente
```bash
curl -X DELETE https://<URL>/api/componentes/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🖥️ 3. Placas PCB

### Crear Placa con Componentes
```bash
curl -X POST https://<URL>/api/placas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
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
  }'
```

### Listar Todas las Placas
```bash
curl -X GET https://<URL>/api/placas \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Placa por ID
```bash
curl -X GET https://<URL>/api/placas/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Verificar Disponibilidad de Componentes
```bash
curl -X GET https://<URL>/api/placas/1/verificar-disponibilidad \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📄 4. Solicitudes de Mecanizado

### Crear Solicitud de Mecanizado (CLIENTE)
```bash
# Login como CLIENTE primero
export TOKEN_CLIENTE="<token-del-cliente>"

# Crear solicitud con archivo Gerber
curl -X POST https://<URL>/api/solicitudes-mecanizado \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -F "placaId=1" \
  -F "observaciones=Primera versión del diseño" \
  -F "archivo=@/ruta/a/archivo.gerber"
```

### Listar Solicitudes de Mecanizado
```bash
curl -X GET https://<URL>/api/solicitudes-mecanizado \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Solicitud por ID
```bash
curl -X GET https://<URL>/api/solicitudes-mecanizado/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar Estado (OPERADOR)
```bash
export TOKEN_OPERADOR="<token-del-operador>"

curl -X PATCH https://<URL>/api/solicitudes-mecanizado/1/estado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OPERADOR" \
  -d '{
    "estado": "EN_PROCESO"
  }'
```

### Upload Nueva Versión Gerber
```bash
curl -X POST https://<URL>/api/solicitudes-mecanizado/1/gerber \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -F "archivo=@/ruta/a/archivo-v2.gerber"
```

---

## 🔩 5. Solicitudes de Armado

### Crear Solicitud de Armado (CLIENTE)
```bash
curl -X POST https://<URL>/api/solicitudes-armado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -d '{
    "placaId": 1,
    "cantidad": 10,
    "prioridad": "ALTA",
    "observaciones": "Necesario para entrega 15/01"
  }'
```

### Listar Solicitudes de Armado
```bash
curl -X GET https://<URL>/api/solicitudes-armado \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Solicitud por ID
```bash
curl -X GET https://<URL>/api/solicitudes-armado/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Actualizar Estado (OPERADOR)
```bash
curl -X PATCH https://<URL>/api/solicitudes-armado/1/estado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OPERADOR" \
  -d '{
    "estado": "EN_PROCESO"
  }'
```

### Confirmar Armado (descuenta stock)
```bash
curl -X POST https://<URL>/api/solicitudes-armado/1/confirmar \
  -H "Authorization: Bearer $TOKEN_OPERADOR"
```

---

## ✅ 6. Tareas

### Crear Tarea para Solicitud (OPERADOR)
```bash
curl -X POST https://<URL>/api/tareas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OPERADOR" \
  -d '{
    "solicitudArmadoId": 1,
    "descripcion": "Armar 10 placas LED",
    "fechaEstimadaFinalizacion": "2024-12-31"
  }'
```

### Listar Todas las Tareas
```bash
curl -X GET https://<URL>/api/tareas \
  -H "Authorization: Bearer $TOKEN_OPERADOR"
```

### Obtener Tarea por ID
```bash
curl -X GET https://<URL>/api/tareas/1 \
  -H "Authorization: Bearer $TOKEN_OPERADOR"
```

### Cambiar Estado a EN_PROCESO
```bash
curl -X PATCH https://<URL>/api/tareas/1/estado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OPERADOR" \
  -d '{
    "estado": "EN_PROCESO",
    "observaciones": "Iniciando armado"
  }'
```

### Pausar Tarea
```bash
curl -X PATCH https://<URL>/api/tareas/1/estado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OPERADOR" \
  -d '{
    "estado": "PAUSADA",
    "observaciones": "Falta componente, esperando restock"
  }'
```

### Completar Tarea
```bash
curl -X PATCH https://<URL>/api/tareas/1/estado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_OPERADOR" \
  -d '{
    "estado": "COMPLETADA",
    "observaciones": "Armado finalizado exitosamente"
  }'
```

---

## 📎 7. Certificaciones

### Upload Certificación (OPERADOR)
```bash
curl -X POST https://<URL>/api/certificaciones \
  -H "Authorization: Bearer $TOKEN_OPERADOR" \
  -F "solicitudArmadoId=1" \
  -F "tipo=CERTIFICACION" \
  -F "descripcion=Certificado de calidad ISO 9001" \
  -F "archivo=@/ruta/a/certificado.pdf"
```

### Listar Certificaciones de una Solicitud
```bash
curl -X GET https://<URL>/api/certificaciones/solicitud/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Obtener Certificación por ID
```bash
curl -X GET https://<URL>/api/certificaciones/1 \
  -H "Authorization: Bearer $TOKEN"
```

### Descargar Archivo de Certificación
```bash
curl -X GET https://<URL>/api/certificaciones/1/download \
  -H "Authorization: Bearer $TOKEN" \
  --output certificacion.pdf
```

---

## 🏥 8. Health & Monitoring

### Health Check
```bash
curl -X GET https://<URL>/actuator/health
```

### Info del Sistema
```bash
curl -X GET https://<URL>/actuator/info
```

---

## 📊 9. Workflow Completo de Prueba

Script completo para probar todo el flujo:

```bash
#!/bin/bash

# URL de tu app en Railway/Render
BASE_URL="https://tu-app.up.railway.app"

echo "=== 1. Registrar ADMIN ==="
curl -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","email":"admin@empresa.com","password":"admin123","nombre":"Admin","rol":"ADMIN"}'

echo -e "\n\n=== 2. Login ADMIN ==="
RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}')

TOKEN=$(echo $RESPONSE | jq -r '.token')
echo "Token: $TOKEN"

echo -e "\n\n=== 3. Crear Componentes ==="
curl -X POST $BASE_URL/api/componentes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"nombre":"Resistencia 10k","descripcion":"1/4W","categoria":"RESISTENCIA","stock":100,"precioUnitario":0.05}'

curl -X POST $BASE_URL/api/componentes \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"nombre":"LED Rojo","descripcion":"5mm","categoria":"LED","stock":50,"precioUnitario":0.15}'

echo -e "\n\n=== 4. Crear Placa ==="
curl -X POST $BASE_URL/api/placas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"nombre":"Placa LED","descripcion":"Test","componentes":[{"componenteId":1,"cantidadNecesaria":2},{"componenteId":2,"cantidadNecesaria":1}]}'

echo -e "\n\n=== 5. Registrar CLIENTE ==="
curl -X POST $BASE_URL/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"cliente1","email":"cliente@test.com","password":"cliente123","nombre":"Cliente Test","rol":"CLIENTE"}'

echo -e "\n\n=== 6. Login CLIENTE ==="
RESPONSE_CLIENTE=$(curl -s -X POST $BASE_URL/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"cliente1","password":"cliente123"}')

TOKEN_CLIENTE=$(echo $RESPONSE_CLIENTE | jq -r '.token')

echo -e "\n\n=== 7. Crear Solicitud de Armado ==="
curl -X POST $BASE_URL/api/solicitudes-armado \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN_CLIENTE" \
  -d '{"placaId":1,"cantidad":5,"prioridad":"ALTA","observaciones":"Prueba"}'

echo -e "\n\n=== 8. Health Check ==="
curl -X GET $BASE_URL/actuator/health

echo -e "\n\n=== ✅ Testing Completo ==="
```

**Guarda este script como `test-api.sh` y ejecútalo**:
```bash
chmod +x test-api.sh
./test-api.sh
```

---

## 🎯 Casos de Prueba Críticos

### ✅ Test 1: Autenticación funciona
- [ ] Registro de usuarios exitoso
- [ ] Login devuelve token JWT válido
- [ ] Token expira después de 24h

### ✅ Test 2: Control de acceso por roles
- [ ] CLIENTE no puede crear componentes (403)
- [ ] CLIENTE puede crear solicitudes de armado
- [ ] OPERADOR puede cambiar estados de tareas
- [ ] ADMIN puede hacer todo

### ✅ Test 3: Validación de stock
- [ ] No se puede crear solicitud de armado sin stock suficiente
- [ ] Confirmar armado descuenta stock correctamente
- [ ] Stock no queda negativo

### ✅ Test 4: Upload de archivos
- [ ] Archivos Gerber se guardan correctamente
- [ ] Certificaciones PDF/imagen suben OK
- [ ] Download de archivos funciona

### ✅ Test 5: Flujo completo
- [ ] CLIENTE crea solicitud de armado
- [ ] OPERADOR crea tarea asociada
- [ ] OPERADOR completa tarea
- [ ] OPERADOR confirma armado (descuenta stock)
- [ ] OPERADOR sube certificación

---

## 📝 Notas

- Todos los requests (excepto `/api/auth/*`) requieren header `Authorization: Bearer <token>`
- Los tokens expiran después de 24 horas
- IDs se generan automáticamente (usar los devueltos en responses)
- Archivos multipart requieren `-F` en lugar de `-d` en cURL

**¿Prefieres Postman?** Importa estos cURL a Postman o usa Swagger UI directamente:
```
https://tu-app.up.railway.app/swagger-ui.html
```
