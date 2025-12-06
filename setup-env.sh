#!/bin/bash

# Copiar archivo de ejemplo
cp .env.example .env

echo "✅ Archivo .env creado desde .env.example"
echo ""
echo "⚠️  IMPORTANTE: Edita el archivo .env y cambia:"
echo "   - JWT_SECRET (debe ser una clave segura de 256 bits)"
echo "   - POSTGRES_PASSWORD (en producción)"
echo ""
echo "💡 Puedes generar un JWT_SECRET con:"
echo "   openssl rand -base64 32"
echo ""
