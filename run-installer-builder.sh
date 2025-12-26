#!/bin/bash
# Script para ejecutar el generador de instalable Windows
# Este script facilita el acceso a las guías y herramientas

echo "╔════════════════════════════════════════════════════════════════╗"
echo "║        Control Pacientes - Windows Installer Generator        ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo ""
echo "¿Cómo deseas proceder?"
echo ""
echo "1) Leer guía rápida (RECOMENDADO para empezar)"
echo "2) Leer guía completa (Detalles y solución de problemas)"
echo "3) Ver estado de configuración"
echo "4) Ejecutar construcción local (PowerShell en Windows)"
echo "5) Ejecutar construcción local (Bash en macOS/Linux)"
echo "6) Ver estructura de archivos"
echo "7) Salir"
echo ""
read -p "Selecciona una opción (1-7): " option

case $option in
    1)
        echo ""
        echo "Abriendo QUICK_START.md..."
        if command -v open &> /dev/null; then
            open QUICK_START.md
        elif command -v xdg-open &> /dev/null; then
            xdg-open QUICK_START.md
        else
            cat QUICK_START.md | less
        fi
        ;;
    2)
        echo ""
        echo "Abriendo WINDOWS_INSTALLER.md..."
        if command -v open &> /dev/null; then
            open WINDOWS_INSTALLER.md
        elif command -v xdg-open &> /dev/null; then
            xdg-open WINDOWS_INSTALLER.md
        else
            cat WINDOWS_INSTALLER.md | less
        fi
        ;;
    3)
        echo ""
        echo "Abriendo SETUP_STATUS.md..."
        if command -v open &> /dev/null; then
            open SETUP_STATUS.md
        elif command -v xdg-open &> /dev/null; then
            xdg-open SETUP_STATUS.md
        else
            cat SETUP_STATUS.md | less
        fi
        ;;
    4)
        echo ""
        echo "Para ejecutar construcción local en Windows:"
        echo ""
        echo "1. Abre PowerShell como Administrador"
        echo "2. Navega a este directorio"
        echo "3. Ejecuta: .\\build-windows-installer.ps1"
        echo ""
        echo "Se generará el archivo: target/installer/ControlPacientes-1.0.0.msi"
        echo ""
        ;;
    5)
        echo ""
        echo "Ejecutando construcción local..."
        echo ""
        
        if [ ! -f "build-windows-installer.sh" ]; then
            echo "❌ Error: build-windows-installer.sh no encontrado"
            exit 1
        fi
        
        chmod +x build-windows-installer.sh
        ./build-windows-installer.sh
        ;;
    6)
        echo ""
        echo "Estructura de archivos del proyecto:"
        echo ""
        tree -L 2 -a --charset ascii 2>/dev/null || find . -maxdepth 2 -type d | head -20
        echo ""
        echo "Para ver estructura completa, ejecuta: tree -L 3"
        ;;
    7)
        echo ""
        echo "¡Hasta luego!"
        exit 0
        ;;
    *)
        echo ""
        echo "❌ Opción no válida. Intenta nuevamente."
        exit 1
        ;;
esac

echo ""
echo "═══════════════════════════════════════════════════════════════"
echo ""
echo "📚 Documentación disponible:"
echo "  • QUICK_START.md          - Guía de inicio rápido"
echo "  • WINDOWS_INSTALLER.md    - Guía completa"
echo "  • SETUP_STATUS.md         - Estado actual de configuración"
echo ""
echo "🛠️  Scripts disponibles:"
echo "  • build-windows-installer.ps1  - Para Windows"
echo "  • build-windows-installer.sh   - Para macOS/Linux"
echo ""
echo "🔗 GitHub Actions:"
echo "  • .github/workflows/build-windows-installer.yml"
echo ""
echo "═══════════════════════════════════════════════════════════════"
