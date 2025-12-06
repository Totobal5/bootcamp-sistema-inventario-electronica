-- Script para crear la base de datos inicial
-- Ejecutar como superusuario de PostgreSQL

-- Crear base de datos
CREATE DATABASE inventario_electronica
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Chile.1252'
    LC_CTYPE = 'Spanish_Chile.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

COMMENT ON DATABASE inventario_electronica
    IS 'Base de datos para sistema de inventario de componentes electrónicos';

-- Conectar a la base de datos
\c inventario_electronica;

-- Las tablas serán creadas automáticamente por Hibernate/JPA
-- Este script es solo para referencia

-- Para crear manualmente (opcional):
/*
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'OPERADOR', 'CLIENTE')),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear índices
CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_email ON usuarios(email);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
*/
