@echo off
REM Script de diagnóstico para ControlPacientes
REM Este script intenta ejecutar la aplicación y mostrar errores

echo =====================================
echo Diagnostico de ControlPacientes
echo =====================================
echo.

REM Buscar la carpeta de instalación
set "INSTALL_DIR=C:\Program Files\ControlPacientes"

if not exist "%INSTALL_DIR%" (
    echo ERROR: No se encontro la carpeta de instalacion en %INSTALL_DIR%
    echo Por favor, verifique que la aplicacion esta instalada correctamente.
    pause
    exit /b 1
)

echo Carpeta de instalacion encontrada: %INSTALL_DIR%
echo.

REM Buscar el ejecutable
if exist "%INSTALL_DIR%\ControlPacientes\bin\ControlPacientes.exe" (
    echo Ejecutable encontrado: %INSTALL_DIR%\ControlPacientes\bin\ControlPacientes.exe
    echo.
    echo Intentando iniciar la aplicacion...
    echo.
    cd /d "%INSTALL_DIR%\ControlPacientes\bin"
    
    REM Ejecutar con output para ver errores
    ControlPacientes.exe
    
    if errorlevel 1 (
        echo.
        echo ERROR: La aplicacion finalizo con codigo de error: %errorlevel%
    ) else (
        echo.
        echo Aplicacion cerrada correctamente.
    )
) else (
    echo ERROR: No se encontro el ejecutable en %INSTALL_DIR%\ControlPacientes\bin\
    echo.
    echo Archivos encontrados en %INSTALL_DIR%:
    dir "%INSTALL_DIR%" /s
)

echo.
echo Presione una tecla para cerrar...
pause
