@echo off
REM Script de validacion de Java para ControlPacientes
REM Ejecutado antes de instalar para verificar Java

setlocal enabledelayedexpansion

echo ==========================================
echo Validacion de Requisitos - ControlPacientes
echo ==========================================
echo.

set JAVA_FOUND=0
set JAVA_VERSION_OK=0

REM Buscar Java en PATH
echo Buscando Java instalado...
for /f "tokens=*" %%A in ('where java 2^>nul') do (
    set "JAVA_PATH=%%A"
    set JAVA_FOUND=1
    goto CHECK_VERSION
)

REM Si no esta en PATH, buscar en registro
if !JAVA_FOUND! equ 0 (
    for /f "tokens=2*" %%A in ('reg query "HKEY_LOCAL_MACHINE\SOFTWARE\JavaSoft\Java Runtime Environment" /v CurrentVersion 2^>nul') do (
        set JAVA_VERSION=%%B
        set JAVA_FOUND=1
    )
)

:CHECK_VERSION
if !JAVA_FOUND! equ 1 (
    if defined JAVA_PATH (
        for /f "tokens=3" %%A in ('"!JAVA_PATH!" -version 2^>^&1 ^| find "version"') do (
            set JAVA_VERSION=%%A
        )
    )
    
    echo Java encontrado: !JAVA_VERSION!
    
    REM Validar que sea version 11 o superior
    for /f "tokens=1 delims=." %%A in ("!JAVA_VERSION:~1!") do (
        if %%A geq 11 (
            set JAVA_VERSION_OK=1
        ) else (
            if %%A equ 1 (
                REM Version 1.8, 1.9, 1.10 no son validas
                set JAVA_VERSION_OK=0
            )
        )
    )
) else (
    echo Java NO encontrado
    set JAVA_FOUND=0
    set JAVA_VERSION_OK=0
)

echo.
if !JAVA_VERSION_OK! equ 1 (
    echo [OK] Java valido encontrado
    echo.
    exit /b 0
) else (
    echo [ERROR] Java no esta instalado o version incorrecta
    echo.
    echo Por favor instale Java desde:
    echo   - https://www.java.com/download
    echo   - https://adoptium.net/installation/
    echo.
    pause
    exit /b 1
)
