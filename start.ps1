# Script para iniciar el proyecto en desarrollo

Write-Host "🚀 Iniciando Sistema de Inventario Electrónica..." -ForegroundColor Green
Write-Host ""

# Verificar Java
Write-Host "Verificando Java..." -ForegroundColor Yellow
java -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Java no está instalado. Por favor instala JDK 17 o superior." -ForegroundColor Red
    exit 1
}

# Verificar Maven
Write-Host ""
Write-Host "Verificando Maven..." -ForegroundColor Yellow
mvn -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Maven no está instalado." -ForegroundColor Red
    exit 1
}

# Verificar PostgreSQL
Write-Host ""
Write-Host "⚠️  Asegúrate de que PostgreSQL esté corriendo en localhost:5432" -ForegroundColor Yellow
Write-Host "   Base de datos: inventario_electronica" -ForegroundColor Cyan
Write-Host ""

# Compilar proyecto
Write-Host "📦 Compilando proyecto..." -ForegroundColor Green
mvn clean install -DskipTests

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ Compilación exitosa!" -ForegroundColor Green
    Write-Host ""
    Write-Host "🌐 Iniciando aplicación en http://localhost:8081" -ForegroundColor Cyan
    Write-Host "📚 Swagger UI: http://localhost:8081/swagger-ui.html" -ForegroundColor Cyan
    Write-Host ""
    
    # Ejecutar aplicación
    mvn spring-boot:run
} else {
    Write-Host "❌ Error en la compilación" -ForegroundColor Red
    exit 1
}
