#!/bin/bash

echo "🚀 Iniciando Sistema de Inventario Electrónica..."
echo ""

# Verificar si existe .env
if [ ! -f .env ]; then
    echo "⚠️  Archivo .env no encontrado. Creando desde .env.example..."
    cp .env.example .env
    echo "✅ Archivo .env creado"
    echo ""
    echo "⚠️  IMPORTANTE: Edita .env y cambia JWT_SECRET antes de deploy en producción"
    echo ""
fi

# Detener contenedores existentes
echo "🛑 Deteniendo contenedores existentes..."
docker-compose down

# Construir y levantar contenedores
echo ""
echo "🔨 Construyendo imágenes..."
docker-compose build

echo ""
echo "▶️  Levantando contenedores..."
docker-compose up -d

# Esperar a que los servicios estén listos
echo ""
echo "⏳ Esperando a que los servicios estén listos..."
sleep 10

# Mostrar logs
echo ""
echo "📋 Mostrando logs (Ctrl+C para salir):"
echo ""
docker-compose logs -f app
