@echo off
REM Script alternativo para ejecutar Control Pacientes en Windows
REM Con opciones para solucionar problemas de rendering
REM Uso: run-debug.bat

echo =========================================
echo Control Pacientes - Modo Debug
echo =========================================
echo.

setlocal enabledelayedexpansion

set JAR_FILE=%~dp0target\control-pacientes-java-1.0.0.jar

if not exist "!JAR_FILE!" (
    echo Error: El archivo JAR no se encuentra en:
    echo !JAR_FILE!
    pause
    exit /b 1
)

echo Iniciando Control Pacientes con configuracion extendida...
echo.

REM Usar el pipeline de software si hay problemas con hardware
java ^
  --add-modules javafx.controls,javafx.fxml ^
  -Djdk.gtk.version=2 ^
  -Dglass.platform=windows ^
  -Dprism.useSWRender=false ^
  -jar "!JAR_FILE!" ^
  --spring.profiles.active=prod

pause
