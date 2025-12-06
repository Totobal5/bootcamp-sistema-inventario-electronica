-- Script de inicialización con datos de prueba
-- Se ejecuta automáticamente al crear el contenedor de PostgreSQL

-- Esperar a que las tablas sean creadas por Hibernate
-- Este script solo inserta datos de ejemplo si las tablas ya existen

-- Nota: Los datos se insertarán después de que la aplicación Spring Boot
-- cree las tablas con Hibernate (ddl-auto=update)

-- Por ahora dejamos este archivo preparado para datos iniciales
-- Si necesitas datos de prueba, puedes usar la API REST después del deploy

-- Ejemplo de inserción de usuario admin (descomentar si lo necesitas):
-- INSERT INTO usuario (username, email, password, rol, activo, created_at, updated_at)
-- VALUES ('admin', 'admin@empresa.com', '$2a$10$...', 'ADMIN', true, NOW(), NOW())
-- ON CONFLICT (username) DO NOTHING;

COMMIT;
