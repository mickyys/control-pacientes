@echo off
REM Script para ejecutar Control Pacientes en Produccion
REM JAR: control-pacientes.jar (version productiva optimizada)
REM Uso: run-prod.bat

echo =========================================
echo Control Pacientes - PRODUCCION
echo =========================================
echo.

setlocal enabledelayedexpansion

set JAR_FILE=%~dp0target\control-pacientes.jar

if not exist "!JAR_FILE!" (
    echo Error: El archivo JAR de produccion no se encuentra
    echo Genera el JAR con: mvn clean package -Pprod
    pause
    exit /b 1
)

echo JAR: !JAR_FILE!
echo.
echo Iniciando en modo PRODUCCION...
echo.

REM Ejecutar con configuracion productiva
REM -server: Activar optimizaciones de servidor
REM -XX:+UseG1GC: Garbage collector optimizado
REM -Xms1g / -Xmx2g: Memoria recomendada para produccion
java ^
  -server ^
  -XX:+UseG1GC ^
  -XX:MaxGCPauseMillis=200 ^
  -XX:+ParallelRefProcEnabled ^
  -XX:G1HeapRegionSize=16M ^
  -Xms1g ^
  -Xmx2g ^
  -Djava.awt.headless=false ^
  -Dfile.encoding=UTF-8 ^
  -Dspring.profiles.active=prod ^
  -jar "!JAR_FILE!"

pause
