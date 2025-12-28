# Script para analizar y mostrar resumen de logs del instalador
# Uso: .\analyze-build-logs.ps1

param(
    [string]$LogFile = "",
    [switch]$Latest = $false,
    [switch]$Summary = $false,
    [ValidateSet("All", "Success", "Error", "Warning")]
    [string]$FilterLevel = "All"
)

# Colores
$Colors = @{
    "SUCCESS" = "Green"
    "ERROR" = "Red"
    "WARNING" = "Yellow"
    "INFO" = "White"
    "DEBUG" = "Cyan"
}

# Buscar archivo de log si no se proporciona
if ([string]::IsNullOrEmpty($LogFile)) {
    if ($Latest) {
        # Obtener el archivo de log más reciente
        $logs = Get-ChildItem "build-logs" -Filter "build-installer-*.log" -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending
        if ($logs.Count -gt 0) {
            $LogFile = $logs[0].FullName
            Write-Host "Usando archivo de log más reciente: $LogFile" -ForegroundColor Cyan
        } else {
            Write-Host "No se encontraron archivos de log" -ForegroundColor Red
            exit 1
        }
    } else {
        # Listar archivos disponibles
        Write-Host "Archivos de log disponibles:" -ForegroundColor Cyan
        Write-Host ""
        $logs = Get-ChildItem "build-logs" -Filter "build-installer-*.log" -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending
        if ($logs.Count -eq 0) {
            Write-Host "No se encontraron archivos de log" -ForegroundColor Yellow
            Write-Host ""
            Write-Host "Para generar logs, ejecuta:" -ForegroundColor Cyan
            Write-Host "  .\build-windows-installer-with-logs.ps1"
            exit 0
        }
        
        for ($i = 0; $i -lt $logs.Count; $i++) {
            $log = $logs[$i]
            $size = [math]::Round($log.Length / 1KB, 2)
            Write-Host "[$($i+1)] $($log.Name) ($size KB) - $($log.LastWriteTime)" -ForegroundColor White
        }
        Write-Host ""
        $choice = Read-Host "Selecciona un número (o presiona Enter para el más reciente)"
        
        if ([string]::IsNullOrEmpty($choice)) {
            $LogFile = $logs[0].FullName
        } else {
            $index = [int]$choice - 1
            if ($index -ge 0 -and $index -lt $logs.Count) {
                $LogFile = $logs[$index].FullName
            } else {
                Write-Host "Selección inválida" -ForegroundColor Red
                exit 1
            }
        }
    }
}

# Verificar que el archivo existe
if (-not (Test-Path $LogFile)) {
    Write-Host "Archivo de log no encontrado: $LogFile" -ForegroundColor Red
    exit 1
}

# Leer el contenido del log
$logContent = Get-Content $LogFile
$totalLines = $logContent.Count

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Análisis de Log del Instalador" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Archivo: $LogFile" -ForegroundColor White
Write-Host "Tamaño: $('{0:N2}' -f ((Get-Item $LogFile).Length / 1KB)) KB" -ForegroundColor White
Write-Host "Líneas: $totalLines" -ForegroundColor White
Write-Host ""

# Contar eventos por nivel
$stats = @{
    "SUCCESS" = 0
    "ERROR" = 0
    "WARNING" = 0
    "INFO" = 0
    "DEBUG" = 0
}

foreach ($line in $logContent) {
    if ($line -match '\[(SUCCESS|ERROR|WARNING|INFO|DEBUG)\]') {
        $level = $matches[1]
        $stats[$level]++
    }
}

# Mostrar estadísticas
Write-Host "Estadísticas:" -ForegroundColor Cyan
Write-Host "  ✅ SUCCESS: $($stats['SUCCESS'])" -ForegroundColor Green
Write-Host "  ❌ ERROR: $($stats['ERROR'])" -ForegroundColor Red
Write-Host "  ⚠️  WARNING: $($stats['WARNING'])" -ForegroundColor Yellow
Write-Host "  ℹ️  INFO: $($stats['INFO'])" -ForegroundColor White
Write-Host "  🔍 DEBUG: $($stats['DEBUG'])" -ForegroundColor Cyan
Write-Host ""

# Determinar estado general
$buildSuccess = $stats['ERROR'] -eq 0
$statusColor = if ($buildSuccess) { "Green" } else { "Red" }
$statusText = if ($buildSuccess) { "✅ EXITOSO" } else { "❌ FALLÓ" }

Write-Host "Estado General: $statusText" -ForegroundColor $statusColor
Write-Host ""

# Extraer pasos ejecutados
Write-Host "Pasos Ejecutados:" -ForegroundColor Cyan
$pasos = $logContent | Select-String "PASO \d+:" -AllMatches
foreach ($paso in $pasos) {
    if ($paso.Line -match "PASO \d+: (.+)") {
        Write-Host "  • $($matches[1])" -ForegroundColor White
    }
}
Write-Host ""

# Mostrar resumen si se solicita
if ($Summary) {
    Write-Host "Resumen Detallado:" -ForegroundColor Cyan
    Write-Host ""
    
    foreach ($line in $logContent) {
        if ($line -match '\[.+\] \[(SUCCESS|ERROR|WARNING|INFO)\]') {
            $level = $matches[1]
            
            # Filtrar por nivel
            if ($FilterLevel -eq "All" -or $FilterLevel -eq $level) {
                $color = $Colors[$level]
                
                # Extraer mensaje
                if ($line -match '\] \[[^\]]+\] (.+)$') {
                    $message = $matches[1]
                    Write-Host "[$level] $message" -ForegroundColor $color
                }
            }
        }
    }
} else {
    # Mostrar solo SUCCESS y ERROR por defecto
    Write-Host "Eventos Importantes:" -ForegroundColor Cyan
    Write-Host ""
    
    $successCount = 0
    $errorCount = 0
    
    foreach ($line in $logContent) {
        if ($line -match '\[.+\] \[(SUCCESS|ERROR|WARNING|INFO)\] (.+)$') {
            $level = $matches[1]
            $message = $matches[2]
            
            if ($level -eq "SUCCESS" -or $level -eq "ERROR" -or $level -eq "WARNING") {
                $color = $Colors[$level]
                
                # Mostrar sin las líneas de los directorios
                if (-not $message.StartsWith("Directorio creado:") -and 
                    -not $message.StartsWith("Archivo copiado:") -and
                    -not $message.Contains("Ruta:")) {
                    
                    Write-Host "[$level] $message" -ForegroundColor $color
                    
                    if ($level -eq "SUCCESS") { $successCount++ }
                    if ($level -eq "ERROR") { $errorCount++ }
                }
            }
        }
    }
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan

# Mostrar recomendaciones
if ($buildSuccess) {
    Write-Host "✅ La compilación fue exitosa!" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos pasos:" -ForegroundColor Cyan
    Write-Host "  1. Valida el instalador:" -ForegroundColor White
    Write-Host "     .\validate-installer.ps1" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "  2. Instala la aplicación:" -ForegroundColor White
    Write-Host "     msiexec /i target\ControlPacientes-1.0.0.msi" -ForegroundColor Cyan
} else {
    Write-Host "❌ La compilación tiene errores" -ForegroundColor Red
    Write-Host ""
    Write-Host "Para ver el registro completo:" -ForegroundColor Cyan
    Write-Host "  .\analyze-build-logs.ps1 -LogFile '$LogFile' -Summary" -ForegroundColor White
    Write-Host ""
    Write-Host "Para filtrar solo errores:" -ForegroundColor Cyan
    Write-Host "  Select-String 'ERROR' '$LogFile'" -ForegroundColor White
}

Write-Host ""
Write-Host "Opciones:" -ForegroundColor Cyan
Write-Host "  -Latest        : Usar el archivo más reciente" -ForegroundColor White
Write-Host "  -Summary       : Mostrar resumen detallado" -ForegroundColor White
Write-Host "  -FilterLevel   : Filtrar por nivel (All, Success, Error, Warning)" -ForegroundColor White
Write-Host ""
Write-Host "Ejemplos:" -ForegroundColor Cyan
Write-Host "  .\analyze-build-logs.ps1 -Latest" -ForegroundColor White
Write-Host "  .\analyze-build-logs.ps1 -Latest -Summary" -ForegroundColor White
Write-Host "  .\analyze-build-logs.ps1 -Latest -FilterLevel Error" -ForegroundColor White
Write-Host ""
