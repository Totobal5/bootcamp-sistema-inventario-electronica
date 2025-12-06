# Copiar archivo de ejemplo
Copy-Item -Path .env.example -Destination .env -Force

Write-Host "✅ Archivo .env creado desde .env.example" -ForegroundColor Green
Write-Host ""
Write-Host "⚠️  IMPORTANTE: Edita el archivo .env y cambia:" -ForegroundColor Yellow
Write-Host "   - JWT_SECRET (debe ser una clave segura de 256 bits)" -ForegroundColor Yellow
Write-Host "   - POSTGRES_PASSWORD (en producción)" -ForegroundColor Yellow
Write-Host ""
Write-Host "💡 Puedes generar un JWT_SECRET con:" -ForegroundColor Cyan
Write-Host "   openssl rand -base64 32" -ForegroundColor Cyan
Write-Host ""
