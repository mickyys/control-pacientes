@echo off
REM Script para ejecutar Control Pacientes - Optimizado para startup rapido
REM Uso: run.bat

echo =========================================
echo Control Pacientes - Aplicacion
echo =========================================
echo.

setlocal enabledelayedexpansion

set JAR_FILE=%~dp0target\control-pacientes-java-1.0.0.jar

if not exist "!JAR_FILE!" (
    echo Error: El archivo JAR no se encuentra en:
    echo !JAR_FILE!
    echo.
    echo Por favor, ejecuta primero: mvn clean package
    pause
    exit /b 1
)

echo Iniciando Control Pacientes...
echo JAR: !JAR_FILE!
echo.

REM Ejecutar con opciones JVM optimizadas para startup rapido y configuracion de JavaFX
java ^
  -XX:+UseG1GC ^
  -XX:MaxGCPauseMillis=200 ^
  -XX:+ParallelRefProcEnabled ^
  -XX:G1HeapRegionSize=16M ^
  -Djdk.gtk.version=2 ^
  --add-modules javafx.controls,javafx.fxml ^
  -jar "!JAR_FILE!" ^
  --spring.profiles.active=prod

pause

