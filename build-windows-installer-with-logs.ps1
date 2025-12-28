# Script para construir el instalador de Windows con logging detallado
# Uso: .\build-windows-installer-with-logs.ps1
# Genera archivo de log: build-installer-$(fecha-hora).log

param(
    [switch]$SkipMavenBuild = $false,
    [string]$Version = "1.0.0",
    [string]$OutputDir = "target/installer",
    [string]$LogDir = "build-logs"
)

# Crear directorio de logs si no existe
New-Item -ItemType Directory -Path $LogDir -Force | Out-Null

# Generar nombre del archivo de log con timestamp
$timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$logFile = Join-Path $LogDir "build-installer-$timestamp.log"

# Variables
$APP_NAME = "ControlPacientes"
$VENDOR = "ControlPacientes"
$MAIN_CLASS = "com.controlpacientes.ControlPacientesApplication"
$JAR_NAME = "control-pacientes-java-${Version}.jar"
$ICON_PATH = "src/main/resources/images/icono.ico"

# Función para registrar en log y en consola
function Log-Message {
    param(
        [string]$Message,
        [ValidateSet("INFO", "SUCCESS", "ERROR", "WARNING", "DEBUG")]
        [string]$Level = "INFO",
        [bool]$WriteToFile = $true
    )
    
    $timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss.fff"
    $logEntry = "[$timestamp] [$Level] $Message"
    
    # Escribir en consola con color
    switch ($Level) {
        "INFO" { Write-Host $Message -ForegroundColor White }
        "SUCCESS" { Write-Host "✅ $Message" -ForegroundColor Green }
        "ERROR" { Write-Host "❌ $Message" -ForegroundColor Red }
        "WARNING" { Write-Host "⚠️  $Message" -ForegroundColor Yellow }
        "DEBUG" { Write-Host "🔍 $Message" -ForegroundColor Cyan }
    }
    
    # Escribir en archivo de log
    if ($WriteToFile) {
        Add-Content -Path $logFile -Value $logEntry
    }
}

# Función para ejecutar comandos y registrar resultado
function Execute-Command {
    param(
        [string]$Command,
        [string]$Description,
        [bool]$Critical = $true
    )
    
    Log-Message "Ejecutando: $Description" "DEBUG"
    Log-Message "Comando: $Command" "DEBUG"
    
    try {
        $output = Invoke-Expression $Command 2>&1
        $exitCode = $LASTEXITCODE
        
        if ($exitCode -eq 0 -or -not $Critical) {
            Log-Message "$Description - COMPLETADO (Exit Code: $exitCode)" "SUCCESS"
            Log-Message "Salida: $output" "DEBUG"
            return $true
        } else {
            Log-Message "$Description - FALLÓ (Exit Code: $exitCode)" "ERROR"
            Log-Message "Salida del error: $output" "ERROR"
            return $false
        }
    } catch {
        Log-Message "$Description - EXCEPCIÓN: $_" "ERROR"
        return $false
    }
}

# Función para validar archivo
function Validate-File {
    param(
        [string]$Path,
        [string]$Description
    )
    
    if (Test-Path $Path) {
        $file = Get-Item $Path
        Log-Message "$Description - ENCONTRADO" "SUCCESS"
        Log-Message "  Ruta: $Path" "DEBUG"
        Log-Message "  Tamaño: $('{0:N2}' -f ($file.Length / 1MB)) MB" "DEBUG"
        return $true
    } else {
        Log-Message "$Description - NO ENCONTRADO" "ERROR"
        Log-Message "  Ruta buscada: $Path" "ERROR"
        return $false
    }
}

# ============================================================================
# INICIO DEL SCRIPT
# ============================================================================

Log-Message "========================================" "INFO"
Log-Message "Control Pacientes - Windows Builder" "INFO"
Log-Message "========================================" "INFO"
Log-Message ""

Log-Message "Inicio del proceso de compilación" "INFO"
Log-Message "Versión: $Version" "DEBUG"
Log-Message "Directorio de salida: $OutputDir" "DEBUG"
Log-Message "Archivo de log: $logFile" "DEBUG"
Log-Message ""

# ============================================================================
# VERIFICACION PRELIMINAR
# ============================================================================

Log-Message "PASO 1: Verificación de Archivos y Dependencias" "INFO"
Log-Message "=================================================" "INFO"

# Verificar pom.xml
if (-not (Validate-File "pom.xml" "pom.xml (Configuración Maven)")) {
    Log-Message "Error crítico: pom.xml no encontrado" "ERROR"
    exit 1
}

# Verificar icono
if (-not (Validate-File $ICON_PATH "Icono ($ICON_PATH)")) {
    Log-Message "Error crítico: Icono no encontrado" "ERROR"
    exit 1
}

# Verificar Java
Log-Message "Verificando Java..." "DEBUG"
$javaCheck = Execute-Command "java -version" "Verificación de Java"
if (-not $javaCheck) {
    Log-Message "Java no está instalado o no está en el PATH" "ERROR"
    exit 1
}

# Verificar Maven
Log-Message "Verificando Maven..." "DEBUG"
$mavenCheck = Execute-Command "mvn -v" "Verificación de Maven"
if (-not $mavenCheck) {
    Log-Message "Maven no está instalado o no está en el PATH" "ERROR"
    exit 1
}

Log-Message ""

# ============================================================================
# COMPILACION CON MAVEN
# ============================================================================

Log-Message "PASO 2: Compilación de Maven" "INFO"
Log-Message "=============================" "INFO"

if ($SkipMavenBuild) {
    Log-Message "Saltando compilación de Maven (--SkipMavenBuild)" "WARNING"
} else {
    Log-Message "Iniciando compilación: mvn clean package -DskipTests" "INFO"
    
    $mavenBuild = Execute-Command `
        "mvn clean package -DskipTests" `
        "Compilación Maven" `
        $true
    
    if (-not $mavenBuild) {
        Log-Message "Maven compilación falló" "ERROR"
        Log-Message "Revisa los logs anteriores para más detalles" "ERROR"
        exit 1
    }
    
    Log-Message "Maven compilación completada exitosamente" "SUCCESS"
}

Log-Message ""

# ============================================================================
# VALIDACION DE ARTEFACTOS
# ============================================================================

Log-Message "PASO 3: Validación de Artefactos" "INFO"
Log-Message "=================================" "INFO"

$jarPath = "target\$JAR_NAME"
if (-not (Validate-File $jarPath "JAR compilado ($JAR_NAME)")) {
    Log-Message "JAR no encontrado después de compilación" "ERROR"
    exit 1
}

Log-Message ""

# ============================================================================
# CREACION DE DIRECTORIOS
# ============================================================================

Log-Message "PASO 4: Preparación de Directorios" "INFO"
Log-Message "==================================" "INFO"

try {
    New-Item -ItemType Directory -Path "target/image" -Force | Out-Null
    Log-Message "Directorio creado: target/image" "SUCCESS"
    
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
    Log-Message "Directorio creado: $OutputDir" "SUCCESS"
    
    New-Item -ItemType Directory -Path "target/wix-resources" -Force | Out-Null
    Log-Message "Directorio creado: target/wix-resources" "SUCCESS"
} catch {
    Log-Message "Error al crear directorios: $_" "ERROR"
    exit 1
}

Log-Message ""

# ============================================================================
# COPIA DE RECURSOS WIX
# ============================================================================

Log-Message "PASO 5: Copia de Recursos WiX" "INFO"
Log-Message "==============================" "INFO"

try {
    Copy-Item "src\main\resources\wix\MsiInstallerStrings_es.wxl" "target\wix-resources\" -Force -ErrorAction SilentlyContinue
    Log-Message "Archivo copiado: MsiInstallerStrings_es.wxl" "SUCCESS"
    
    Copy-Item "src\main\resources\wix\main.wxs" "target\wix-resources\" -Force -ErrorAction SilentlyContinue
    Log-Message "Archivo copiado: main.wxs" "SUCCESS"
} catch {
    Log-Message "Advertencia al copiar recursos WiX: $_" "WARNING"
}

Log-Message ""

# ============================================================================
# EJECUCION DE JPACKAGE
# ============================================================================

Log-Message "PASO 6: Generación de Instalador MSI con jpackage" "INFO"
Log-Message "==================================================" "INFO"

$iconPathResolved = Resolve-Path $ICON_PATH
Log-Message "Icono resuelto: $iconPathResolved" "DEBUG"

Log-Message "Parámetros de jpackage:" "DEBUG"
Log-Message "  --input: target" "DEBUG"
Log-Message "  --name: $APP_NAME" "DEBUG"
Log-Message "  --main-jar: $JAR_NAME" "DEBUG"
Log-Message "  --main-class: $MAIN_CLASS" "DEBUG"
Log-Message "  --type: msi" "DEBUG"
Log-Message "  --icon: $iconPathResolved" "DEBUG"
Log-Message "  --dest: $OutputDir" "DEBUG"
Log-Message "  --win-menu: true" "DEBUG"
Log-Message "  --win-menu-group: $APP_NAME" "DEBUG"
Log-Message "  --win-dir-chooser: true" "DEBUG"
Log-Message "  --win-shortcut: true" "DEBUG"
Log-Message "  --vendor: $VENDOR" "DEBUG"
Log-Message "  --app-version: $Version" "DEBUG"

Log-Message ""
Log-Message "Ejecutando jpackage..." "INFO"

$jpackageArgs = @(
    "--input", "target",
    "--name", $APP_NAME,
    "--main-jar", $JAR_NAME,
    "--main-class", $MAIN_CLASS,
    "--type", "msi",
    "--icon", $iconPathResolved,
    "--dest", $OutputDir,
    "--win-menu",
    "--win-menu-group", $APP_NAME,
    "--win-dir-chooser",
    "--win-shortcut",
    "--description", "Sistema de Control de Pacientes",
    "--vendor", $VENDOR,
    "--app-version", $Version,
    "--java-options", "-Xmx512m",
    "--java-options", "-Dfile.encoding=UTF-8"
)

try {
    # Ejecutar jpackage y capturar output
    $jpackageOutput = & jpackage @jpackageArgs 2>&1
    $jpackageExitCode = $LASTEXITCODE
    
    # Registrar output completo
    Log-Message "Output de jpackage:" "DEBUG"
    foreach ($line in $jpackageOutput) {
        Log-Message "  $line" "DEBUG"
    }
    
    if ($jpackageExitCode -eq 0) {
        Log-Message "jpackage completado exitosamente (Exit Code: 0)" "SUCCESS"
    } else {
        Log-Message "jpackage falló con exit code: $jpackageExitCode" "ERROR"
        Log-Message "Error completo registrado arriba" "ERROR"
        exit 1
    }
} catch {
    Log-Message "Excepción al ejecutar jpackage: $_" "ERROR"
    exit 1
}

Log-Message ""

# ============================================================================
# VALIDACION DE SALIDA
# ============================================================================

Log-Message "PASO 7: Validación de Archivos Generados" "INFO"
Log-Message "========================================" "INFO"

Log-Message "Buscando archivos MSI..." "DEBUG"

$msiFiles = Get-ChildItem -Path $OutputDir -Include "*.msi" -ErrorAction SilentlyContinue
if ($msiFiles.Count -gt 0) {
    Log-Message "Archivos MSI encontrados: $($msiFiles.Count)" "SUCCESS"
    foreach ($msiFile in $msiFiles) {
        $msiSize = [math]::Round($msiFile.Length / 1MB, 2)
        Log-Message "  MSI: $($msiFile.Name)" "SUCCESS"
        Log-Message "    Ruta: $($msiFile.FullName)" "DEBUG"
        Log-Message "    Tamaño: $msiSize MB" "DEBUG"
        Log-Message "    Fecha: $($msiFile.LastWriteTime)" "DEBUG"
    }
} else {
    Log-Message "No se encontraron archivos MSI" "ERROR"
    Log-Message "Contenido del directorio de salida:" "DEBUG"
    
    if (Test-Path $OutputDir) {
        $contents = Get-ChildItem -Path $OutputDir -Recurse
        foreach ($item in $contents) {
            Log-Message "  $($item.FullName)" "DEBUG"
        }
    } else {
        Log-Message "El directorio de salida no existe: $OutputDir" "ERROR"
    }
    
    exit 1
}

Log-Message ""

# ============================================================================
# COPIA A TARGET (si es necesario)
# ============================================================================

Log-Message "PASO 8: Organización de Archivos" "INFO"
Log-Message "=================================" "INFO"

if (Test-Path "$OutputDir/*.msi") {
    Log-Message "Copiando MSI de output a target..." "INFO"
    try {
        Copy-Item "$OutputDir/*.msi" "target/" -Force
        Log-Message "Archivos MSI copiados a target/" "SUCCESS"
    } catch {
        Log-Message "Advertencia al copiar: $_" "WARNING"
    }
}

Log-Message ""

# ============================================================================
# RESUMEN FINAL
# ============================================================================

Log-Message "PASO 9: Resumen Final" "INFO"
Log-Message "=====================" "INFO"

Log-Message "========================================" "SUCCESS"
Log-Message "Proceso completado exitosamente!" "SUCCESS"
Log-Message "========================================" "SUCCESS"

Log-Message ""
Log-Message "Archivos generados:" "INFO"
$finalMsi = Get-ChildItem -Path "target" -Include "*.msi" -ErrorAction SilentlyContinue
if ($finalMsi) {
    foreach ($msi in $finalMsi) {
        $msiSize = [math]::Round($msi.Length / 1MB, 2)
        Log-Message "  • $($msi.Name) ($msiSize MB)" "SUCCESS"
        Log-Message "    Ruta: $($msi.FullName)" "DEBUG"
    }
} else {
    Log-Message "  No se encontraron archivos MSI en target/" "WARNING"
}

Log-Message ""
Log-Message "Próximos pasos:" "INFO"
Log-Message "  1. Ejecuta el instalador .msi generado" "INFO"
Log-Message "  2. Se creará un acceso directo en el Menú Inicio" "INFO"
Log-Message "  3. Opcionalmente, crea un acceso directo en el escritorio" "INFO"

Log-Message ""
Log-Message "Archivo de log:" "INFO"
Log-Message "  $logFile" "SUCCESS"

Log-Message ""
Log-Message "Para validar el instalador:" "INFO"
Log-Message "  .\validate-installer.ps1" "INFO"

Log-Message ""
Log-Message "================================================" "SUCCESS"
Log-Message "Compilación finalizada" "SUCCESS"
Log-Message "================================================" "SUCCESS"

Write-Host ""
Write-Host "El archivo de log se ha guardado en: $logFile" -ForegroundColor Cyan
