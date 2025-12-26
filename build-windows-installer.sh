#!/bin/bash
# Script para construir el instalador de Windows localmente en macOS/Linux
# Uso: ./build-windows-installer.sh

set -e

echo "==================================="
echo "Control Pacientes - Windows Builder"
echo "==================================="
echo ""

# Variables
APP_NAME="ControlPacientes"
APP_VERSION="1.0.0"
VENDOR="ControlPacientes"
MAIN_CLASS="com.controlpacientes.ControlPacientesApplication"
JAR_NAME="control-pacientes-java-${APP_VERSION}.jar"
ICON_PATH="src/main/resources/images/icono.png"

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para mostrar errores
error_exit() {
    echo -e "${RED}❌ Error: $1${NC}"
    exit 1
}

# Función para mostrar éxito
success() {
    echo -e "${GREEN}✅ $1${NC}"
}

# Función para mostrar info
info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Verificar que estamos en el directorio correcto
if [ ! -f "pom.xml" ]; then
    error_exit "pom.xml no encontrado. Ejecuta este script desde la raíz del proyecto."
fi

# Verificar que el icono existe
if [ ! -f "$ICON_PATH" ]; then
    error_exit "El archivo de icono no existe: $ICON_PATH"
fi

# Verificar Java
if ! command -v java &> /dev/null; then
    error_exit "Java no está instalado"
fi

JAVA_VERSION=$(java -version 2>&1 | grep "version" | head -1)
info "Java encontrado: $JAVA_VERSION"

# Paso 1: Limpiar y compilar
echo ""
info "Paso 1: Compilando la aplicación con Maven..."
mvn clean package -DskipTests || error_exit "Falló la compilación"
success "Compilación completada"

# Paso 2: Crear imagen de aplicación
echo ""
info "Paso 2: Creando imagen de aplicación..."
mkdir -p target/image
mkdir -p target/installer

# Nota: En macOS/Linux, jpackage puede generar instaladores para Linux/macOS
# Para Windows, se recomienda usar GitHub Actions o ejecutar en Windows
if [[ "$OSTYPE" == "darwin"* ]]; then
    info "Sistema detectado: macOS"
    info "Generando instalador DMG..."
    
    jpackage \
        --input target \
        --name "$APP_NAME" \
        --main-jar "$JAR_NAME" \
        --main-class "$MAIN_CLASS" \
        --type dmg \
        --icon "$ICON_PATH" \
        --dest target/installer \
        --description "Sistema de Control de Pacientes" \
        --vendor "$VENDOR" \
        --app-version "$APP_VERSION" \
        || error_exit "Falló la creación del instalador DMG"
    
    success "Instalador DMG creado en: target/installer/"
    
elif [[ "$OSTYPE" == "linux"* ]]; then
    info "Sistema detectado: Linux"
    info "Generando instalador DEB..."
    
    jpackage \
        --input target \
        --name "$APP_NAME" \
        --main-jar "$JAR_NAME" \
        --main-class "$MAIN_CLASS" \
        --type deb \
        --icon "$ICON_PATH" \
        --dest target/installer \
        --description "Sistema de Control de Pacientes" \
        --vendor "$VENDOR" \
        --app-version "$APP_VERSION" \
        || error_exit "Falló la creación del instalador DEB"
    
    success "Instalador DEB creado en: target/installer/"
    
elif [[ "$OSTYPE" == "msys" || "$OSTYPE" == "cygwin" ]]; then
    info "Sistema detectado: Windows (Git Bash/Cygwin)"
    info "Generando instalador MSI..."
    
    jpackage \
        --input target \
        --name "$APP_NAME" \
        --main-jar "$JAR_NAME" \
        --main-class "$MAIN_CLASS" \
        --type msi \
        --icon "$ICON_PATH" \
        --dest target/installer \
        --install-dir "Program Files" \
        --win-menu \
        --win-menu-group "$APP_NAME" \
        --win-dir-chooser \
        --win-shortcut \
        --description "Sistema de Control de Pacientes" \
        --vendor "$VENDOR" \
        --app-version "$APP_VERSION" \
        || error_exit "Falló la creación del instalador MSI"
    
    success "Instalador MSI creado en: target/installer/"
else
    error_exit "Sistema operativo no soportado: $OSTYPE"
fi

# Paso 3: Mostrar archivos generados
echo ""
info "Paso 3: Archivos generados..."
echo ""
ls -lh target/installer/ 2>/dev/null || ls -l target/installer/

echo ""
success "¡Proceso completado exitosamente!"
echo ""
echo "Los instaladores están disponibles en: ./target/installer/"
echo ""
echo "Nota para Windows: Para generar instaladores .MSI en Windows,"
echo "ejecuta este script en un sistema Windows o usa GitHub Actions."
