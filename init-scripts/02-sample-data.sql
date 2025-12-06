-- Script de inicialización de datos para Railway/Render
-- Este script se ejecuta automáticamente si configuras Railway para ejecutarlo
-- O puedes ejecutarlo manualmente una vez desplegada la aplicación

-- NOTA: Las tablas ya son creadas por Hibernate (ddl-auto=update)
-- Este script solo inserta datos de prueba

-- ============================================
-- USUARIOS DE PRUEBA
-- ============================================
-- Password: "admin123" (debes usar BCrypt en producción)
-- Estos son ejemplos - la app usa BCrypt automáticamente al registrar

-- Para crear usuarios, usa el endpoint:
-- POST /api/auth/register con role ADMIN, OPERADOR, CLIENTE

-- ============================================
-- COMPONENTES ELECTRÓNICOS DE EJEMPLO
-- ============================================
-- Estos se insertan después de crear un usuario ADMIN y autenticarte
-- Usa POST /api/componentes

-- Ejemplos de componentes útiles:
/*
{
  "nombre": "Resistencia 10kΩ",
  "descripcion": "Resistencia de carbón 1/4W 5%",
  "categoria": "RESISTENCIA",
  "stock": 1000,
  "precioUnitario": 0.05
}

{
  "nombre": "Capacitor 100nF",
  "descripcion": "Capacitor cerámico 50V",
  "categoria": "CAPACITOR",
  "stock": 500,
  "precioUnitario": 0.10
}

{
  "nombre": "LED Rojo 5mm",
  "descripcion": "LED rojo ultra brillante 5mm",
  "categoria": "LED",
  "stock": 300,
  "precioUnitario": 0.15
}

{
  "nombre": "Arduino Nano",
  "descripcion": "Microcontrolador Arduino Nano compatible",
  "categoria": "MICROCONTROLADOR",
  "stock": 50,
  "precioUnitario": 8.50
}

{
  "nombre": "Transistor BC547",
  "descripcion": "Transistor NPN uso general",
  "categoria": "TRANSISTOR",
  "stock": 200,
  "precioUnitario": 0.20
}
*/

-- ============================================
-- VERIFICACIÓN DE DATOS
-- ============================================
-- Una vez insertados, puedes verificar con:
-- SELECT * FROM usuarios;
-- SELECT * FROM componentes_electronicos;

-- ============================================
-- NOTAS IMPORTANTES
-- ============================================
-- 1. Railway/Render usan PostgreSQL persistente
-- 2. Hibernate crea automáticamente las tablas (no necesitas CREATE TABLE)
-- 3. Usa Swagger UI o Postman para insertar datos de prueba
-- 4. El primer usuario debe ser ADMIN para crear componentes
-- 5. Las contraseñas se hashean automáticamente con BCrypt

-- ============================================
-- COMANDOS ÚTILES PARA RAILWAY CLI
-- ============================================
-- Conectar a PostgreSQL de Railway:
-- railway run psql $DATABASE_URL

-- Ver tablas creadas:
-- \dt

-- Ver datos de usuarios:
-- SELECT id, username, email, rol, activo FROM usuarios;

-- Ver componentes:
-- SELECT nombre, categoria, stock FROM componentes_electronicos;
