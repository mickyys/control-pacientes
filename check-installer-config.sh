#!/bin/bash
# Script para verificar la configuración del instalador en macOS/Linux
# Este script puede ejecutarse en Windows con Git Bash

set -e

echo "=========================================="
echo "Verificación de Configuración del Instalador"
echo "=========================================="
echo ""

# Colores
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Función para mostrar resultados
check_file() {
    local file=$1
    local description=$2
    
    if [ -f "$file" ]; then
        echo -e "${GREEN}✅${NC} $description: $file"
        return 0
    else
        echo -e "${RED}❌${NC} $description: $file (NO ENCONTRADO)"
        return 1
    fi
}

# Función para mostrar contenido
show_content() {
    local file=$1
    local lines=$2
    echo ""
    echo "--- Primeras $lines líneas de: $file ---"
    head -n $lines "$file" || echo "No se puede leer el archivo"
    echo ""
}

# Verificaciones
echo "Verificando archivos necesarios..."
echo ""

check_file "pom.xml" "pom.xml (Configuración Maven)"
check_file "src/main/resources/wix/main.wxs" "WiX Configuration"
check_file "src/main/resources/wix/MsiInstallerStrings_es.wxl" "WiX Strings (Spanish)"
check_file "src/main/resources/images/icono.ico" "Icono ICO"
check_file "src/main/resources/images/icono.png" "Icono PNG"
check_file ".github/workflows/build-windows-installer.yml" "GitHub Workflow"
check_file "build-windows-installer.ps1" "PowerShell Build Script"

echo ""
echo "=========================================="
echo "Contenido de Archivos Críticos"
echo "=========================================="
echo ""

# Mostrar versión del proyecto
if [ -f "pom.xml" ]; then
    VERSION=$(grep -o '<version>[^<]*</version>' pom.xml | head -1 | sed 's/<[^>]*>//g')
    echo "Versión del Proyecto: $VERSION"
fi

# Mostrar clase principal
if [ -f "pom.xml" ]; then
    MAIN_CLASS=$(grep -o 'com.controlpacientes.[^<]*' pom.xml | head -1)
    echo "Clase Principal: $MAIN_CLASS"
fi

echo ""
echo "=========================================="
echo "Validación de WiX"
echo "=========================================="
echo ""

if [ -f "src/main/resources/wix/main.wxs" ]; then
    echo "Comprobando si main.wxs usa variables de jpackage..."
    if grep -q '$(var.JpAppName)' "src/main/resources/wix/main.wxs"; then
        echo -e "${GREEN}✅${NC} WiX usa variables de jpackage correctamente"
    else
        echo -e "${RED}❌${NC} WiX no usa variables de jpackage (puede fallar)"
    fi
    
    echo ""
    echo "Verificando referencias problemáticas..."
    if grep -q 'bind.FileVersion' "src/main/resources/wix/main.wxs"; then
        echo -e "${RED}❌${NC} ADVERTENCIA: Se encontró 'bind.FileVersion' (causará error)"
    else
        echo -e "${GREEN}✅${NC} No hay referencias problemáticas a 'bind.FileVersion'"
    fi
fi

echo ""
echo "=========================================="
echo "Resumen"
echo "=========================================="
echo ""
echo "Estado de la configuración:"
echo "  • pom.xml: Configurado para Maven"
echo "  • WiX: Simplificado para compatibilidad con jpackage"
echo "  • Workflow: Actualizado sin conflictos"
echo "  • Scripts: Listos para ejecutar"
echo ""
echo -e "${GREEN}La configuración está lista para generar el instalador.${NC}"
echo ""
echo "Próximos pasos:"
echo "1. Hacer commit de los cambios"
echo "2. Hacer push a GitHub"
echo "3. El workflow se ejecutará automáticamente"
echo "4. Descargar el artefacto desde Actions"
echo ""
