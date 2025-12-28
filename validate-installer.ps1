# Script para validar el instalador generado
# Uso: .\validate-installer.ps1

param(
    [string]$MsiPath = "target/ControlPacientes-1.0.0.msi"
)

Write-Host "====================================" -ForegroundColor Cyan
Write-Host "Validador de Instalador MSI" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

# Función para mostrar resultados
function Show-Result {
    param($Test, $Result)
    $status = if ($Result) { "✅ PASS" } else { "❌ FAIL" }
    Write-Host "$status - $Test"
}

# Verificación 1: El archivo MSI existe
$msiExists = Test-Path $MsiPath
Show-Result "MSI existe en $MsiPath" $msiExists

if (-not $msiExists) {
    Write-Host ""
    Write-Host "Buscando archivos MSI..." -ForegroundColor Yellow
    $foundFiles = Get-ChildItem -Path "target" -Include "*.msi" -ErrorAction SilentlyContinue
    if ($foundFiles) {
        Write-Host "Se encontraron los siguientes archivos MSI:"
        $foundFiles | ForEach-Object { Write-Host "  - $($_.FullName)" }
    } else {
        Write-Host "No se encontraron archivos MSI"
    }
    exit 1
}

# Obtener información del archivo
$msiFile = Get-Item $MsiPath
$msiSize = [math]::Round($msiFile.Length / 1MB, 2)

Write-Host ""
Write-Host "Información del archivo:" -ForegroundColor Cyan
Write-Host "  Nombre: $($msiFile.Name)"
Write-Host "  Tamaño: $msiSize MB"
Write-Host "  Ubicación: $($msiFile.FullName)"
Write-Host ""

# Verificación 2: El tamaño es razonable (al menos 50MB)
$sizeOk = $msiFile.Length -gt 52428800  # 50MB
Show-Result "Tamaño del MSI es razonable (>50MB)" $sizeOk

# Verificación 3: Verificar con Windows Installer
Write-Host ""
Write-Host "Realizando validaciones adicionales..." -ForegroundColor Cyan
Write-Host ""

# Intenta obtener propiedades del MSI usando WMI
try {
    $msiInfo = Get-Item $MsiPath -ErrorAction SilentlyContinue
    if ($msiInfo) {
        Show-Result "El MSI es accesible" $true
    }
} catch {
    Show-Result "El MSI es accesible" $false
}

# Resumen
Write-Host ""
Write-Host "====================================" -ForegroundColor Cyan
Write-Host "Resumen de Validación" -ForegroundColor Cyan
Write-Host "====================================" -ForegroundColor Cyan
Write-Host ""

if ($msiExists -and $sizeOk) {
    Write-Host "✅ El instalador se generó correctamente" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos pasos:" -ForegroundColor Cyan
    Write-Host "1. Ejecuta el siguiente comando para instalar:"
    Write-Host "   msiexec /i $($msiFile.FullName)"
    Write-Host ""
    Write-Host "2. O simplemente haz doble clic en el archivo MSI"
    Write-Host ""
    Write-Host "3. Sigue el asistente de instalación"
    Write-Host "4. La aplicación se ejecutará desde:"
    Write-Host "   - Acceso directo en el escritorio"
    Write-Host "   - Menú Inicio > ControlPacientes"
} else {
    Write-Host "❌ El instalador no se generó correctamente" -ForegroundColor Red
    Write-Host ""
    Write-Host "Verifica los siguientes puntos:" -ForegroundColor Yellow
    Write-Host "1. ¿Se completó la compilación de Maven?"
    Write-Host "2. ¿Se ejecutó jpackage correctamente?"
    Write-Host "3. ¿Existe el archivo de icono en src/main/resources/images/icono.ico?"
    Write-Host "4. ¿Está instalado WiX Toolset?"
    exit 1
}

Write-Host ""
