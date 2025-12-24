# Script de compilación para macOS/Linux
#!/bin/bash

# Crear estructura de carpetas
mkdir -p build publish

# Limpiar solución
echo "Limpiando solución..."
dotnet clean

# Restaurar dependencias
echo "Restaurando dependencias..."
dotnet restore

# Compilar en Release
echo "Compilando en Release..."
dotnet build -c Release

# Publicar aplicación
echo "Publicando aplicación..."
dotnet publish ControlPacientes.UI/ControlPacientes.UI.csproj -c Release -r win-x64 --self-contained -o publish

echo ""
echo "========================================"
echo "Compilación completada exitosamente"
echo "Archivos publicados en: publish/"
echo "========================================"
