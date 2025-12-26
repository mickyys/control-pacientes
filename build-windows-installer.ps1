# Script para construir el instalador de Windows localmente
# Uso: .\build-windows-installer.ps1

param(
    [switch]$SkipMavenBuild = $false,
    [string]$Version = "1.0.0",
    [string]$OutputDir = "target/installer"
)

# Variables
$APP_NAME = "ControlPacientes"
$VENDOR = "ControlPacientes"
$MAIN_CLASS = "com.controlpacientes.ControlPacientesApplication"
$JAR_NAME = "control-pacientes-java-${Version}.jar"
$ICON_PATH = "src/main/resources/images/icono.png"

Write-Host "===================================" -ForegroundColor Cyan
Write-Host "Control Pacientes - Windows Builder" -ForegroundColor Cyan
Write-Host "===================================" -ForegroundColor Cyan
Write-Host ""

# Función para mostrar errores
function Show-Error {
    param($Message)
    Write-Host "❌ Error: $Message" -ForegroundColor Red
    exit 1
}

# Función para mostrar éxito
function Show-Success {
    param($Message)
    Write-Host "✅ $Message" -ForegroundColor Green
}

# Función para mostrar info
function Show-Info {
    param($Message)
    Write-Host "ℹ️  $Message" -ForegroundColor Yellow
}

# Verificar que estamos en el directorio correcto
if (-not (Test-Path "pom.xml")) {
    Show-Error "pom.xml no encontrado. Ejecuta este script desde la raíz del proyecto."
}

# Verificar que el icono existe
if (-not (Test-Path $ICON_PATH)) {
    Show-Error "El archivo de icono no existe: $ICON_PATH"
}

# Verificar Java
try {
    $javaVersion = java -version 2>&1
    Show-Info "Java encontrado: $($javaVersion[0])"
} catch {
    Show-Error "Java no está instalado o no está en el PATH"
}

# Crear directorios
New-Item -ItemType Directory -Path "target/image" -Force | Out-Null
New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null

# Paso 1: Compilar con Maven
if (-not $SkipMavenBuild) {
    Write-Host ""
    Show-Info "Paso 1: Compilando la aplicación con Maven..."
    
    mvn clean package -DskipTests
    
    if ($LASTEXITCODE -ne 0) {
        Show-Error "Falló la compilación de Maven"
    }
    
    Show-Success "Compilación completada"
} else {
    Show-Info "Saltando compilación de Maven (--SkipMavenBuild)"
}

# Paso 2: Crear imagen de aplicación
Write-Host ""
Show-Info "Paso 2: Creando imagen de aplicación..."

$iconPath = Resolve-Path $ICON_PATH
$jarPath = Resolve-Path "target\$JAR_NAME" -ErrorAction SilentlyContinue

if (-not $jarPath) {
    Show-Error "JAR no encontrado. Asegúrate de compilar primero."
}

Show-Info "Usando JAR: $jarPath"
Show-Info "Usando icono: $iconPath"

# Paso 3: Generar instalador MSI
Write-Host ""
Show-Info "Paso 3: Generando instalador MSI con jpackage..."

try {
    $jpackageArgs = @(
        "--input", "target",
        "--name", $APP_NAME,
        "--main-jar", $JAR_NAME,
        "--main-class", $MAIN_CLASS,
        "--type", "msi",
        "--icon", $iconPath,
        "--dest", $OutputDir,
        "--install-dir", "Program Files",
        "--win-menu",
        "--win-menu-group", $APP_NAME,
        "--win-dir-chooser",
        "--win-shortcut",
        "--description", "Sistema de Control de Pacientes",
        "--vendor", $VENDOR,
        "--app-version", $Version
    )
    
    Write-Host "Ejecutando: jpackage $($jpackageArgs -join ' ')" -ForegroundColor Gray
    & jpackage @jpackageArgs
    
    if ($LASTEXITCODE -ne 0) {
        Show-Error "Falló la creación del instalador MSI"
    }
    
    Show-Success "Instalador MSI creado exitosamente"
} catch {
    Show-Error "Error al ejecutar jpackage: $_"
}

# Paso 4: Listar archivos generados
Write-Host ""
Show-Info "Paso 4: Archivos generados..."
Write-Host ""
Get-ChildItem -Path $OutputDir -Recurse | Format-Table -AutoSize

# Paso 5: Información adicional
Write-Host ""
Show-Success "¡Proceso completado exitosamente!"
Write-Host ""
Write-Host "Los instaladores están disponibles en: .\$OutputDir\" -ForegroundColor Cyan
Write-Host ""
Write-Host "Próximos pasos:" -ForegroundColor Cyan
Write-Host "1. Ejecuta el instalador .msi generado"
Write-Host "2. Se creará un acceso directo en el Menú Inicio"
Write-Host "3. Opcionalmente, crea un acceso directo en el escritorio"
Write-Host ""
Write-Host "Para más información, consulta: .\.github\workflows\README.md" -ForegroundColor Cyan
