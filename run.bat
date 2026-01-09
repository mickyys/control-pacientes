@echo off
REM Script para ejecutar Control Pacientes en Windows
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

REM Ejecutar con perfil de produccion por defecto
java -jar "!JAR_FILE!" --spring.profiles.active=prod

pause

