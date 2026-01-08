#!/bin/bash

# Script para ejecutar Control Pacientes en modo desarrollo con Hot Reload
# Uso: ./dev.sh

echo "========================================="
echo "Control Pacientes - Modo Desarrollo"
echo "========================================="
echo ""
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

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
