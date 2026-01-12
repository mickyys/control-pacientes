#!/bin/bash

# Script para ejecutar Control Pacientes en modo desarrollo con Hot Reload
# Uso: ./dev.sh

echo "========================================="
echo "Control Pacientes - Modo Desarrollo"
echo "========================================="
echo ""
echo "Tema: Verde Esmeralda 🌿"
echo "Hot Reload está habilitado para:"
echo "  • FXML: src/main/resources/fxml/"
echo "  • CSS: src/main/resources/css/"
echo ""
echo "Realiza cambios en estos archivos y verás"
echo "los cambios reflejados automáticamente."
echo ""
echo "Presiona Ctrl+C para detener."
echo "========================================="
echo ""

# Compilar primero con los perfiles para que se procesen correctamente
mvn clean compile -Pwindows,emerald -q

# Luego ejecutar con spring-boot:run
mvn -Pwindows,emerald spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
