# Script para monitorear logs en tiempo real durante la compilación
# Uso: .\monitor-build.ps1
# Ejecuta en otra ventana PowerShell mientras build-windows-installer-with-logs.ps1 está corriendo

param(
    [int]$RefreshSeconds = 2
)

$logDir = "build-logs"

# Crear directorio si no existe
if (-not (Test-Path $logDir)) {
    Write-Host "No se encontró directorio de logs: $logDir" -ForegroundColor Red
    Write-Host "Ejecuta primero: .\build-windows-installer-with-logs.ps1" -ForegroundColor Yellow
    exit 1
}

# Obtener el archivo de log más reciente
function Get-LatestLog {
    $logs = Get-ChildItem $logDir -Filter "build-installer-*.log" -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending
    return $logs[0].FullName
}

$logFile = Get-LatestLog

if (-not $logFile) {
    Write-Host "No se encontraron archivos de log" -ForegroundColor Red
    exit 1
}

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Monitor de Compilación en Tiempo Real" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Monitoreando: $logFile" -ForegroundColor Green
Write-Host "Actualizando cada $RefreshSeconds segundos..." -ForegroundColor Yellow
Write-Host "(Presiona Ctrl+C para detener)" -ForegroundColor Yellow
Write-Host ""

$lastLineCount = 0
$processComplete = $false

while ($true) {
    try {
        # Leer el archivo
        $logContent = Get-Content $logFile
        $currentLineCount = if ($null -eq $logContent) { 0 } else { @($logContent).Count }
        
        # Si hay nuevas líneas, mostrarlas
        if ($currentLineCount -gt $lastLineCount) {
            $newLines = $logContent | Select-Object -Skip $lastLineCount
            
            foreach ($line in $newLines) {
                # Extraer nivel y mensaje
                if ($line -match '\[.+\] \[([^\]]+)\] (.+)$') {
                    $level = $matches[1]
                    $message = $matches[2]
                    
                    # Colorear según nivel
                    switch ($level) {
                        "SUCCESS" { Write-Host "✅ $message" -ForegroundColor Green }
                        "ERROR" { Write-Host "❌ $message" -ForegroundColor Red }
                        "WARNING" { Write-Host "⚠️  $message" -ForegroundColor Yellow }
                        "INFO" { Write-Host "ℹ️  $message" -ForegroundColor Cyan }
                        "DEBUG" { Write-Host "🔍 $message" -ForegroundColor Gray }
                        default { Write-Host $message }
                    }
                } else {
                    Write-Host $line
                }
            }
            
            $lastLineCount = $currentLineCount
        }
        
        # Detectar si el proceso completó
        if ($logContent -match "Compilación finalizada") {
            if (-not $processComplete) {
                Write-Host ""
                Write-Host "========================================" -ForegroundColor Green
                Write-Host "✅ Compilación completada" -ForegroundColor Green
                Write-Host "========================================" -ForegroundColor Green
                Write-Host ""
                $processComplete = $true
            }
        }
        
        Start-Sleep -Seconds $RefreshSeconds
    } catch {
        Write-Host "Error al leer log: $_" -ForegroundColor Red
        Start-Sleep -Seconds $RefreshSeconds
    }
}
