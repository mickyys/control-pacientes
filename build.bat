@echo off
REM Crear estructura de carpetas para Build
if not exist "build" mkdir build
if not exist "publish" mkdir publish

REM Limpiar solución
echo Limpiando solución...
dotnet clean

REM Restaurar dependencias
echo Restaurando dependencias...
dotnet restore

REM Compilar en Release
echo Compilando en Release...
dotnet build -c Release

REM Publicar aplicación
echo Publicando aplicación...
dotnet publish ControlPacientes.UI\ControlPacientes.UI.csproj -c Release -r win-x64 --self-contained -o publish

echo.
echo ========================================
echo Compilación completada exitosamente
echo Archivos publicados en: publish\
echo ========================================
pause
