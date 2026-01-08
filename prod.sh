#!/bin/bash

# Script para ejecutar Control Pacientes en modo producción
# Hot Reload está deshabilitado
# Uso: ./prod.sh

echo "========================================="
echo "Control Pacientes - Modo Producción"
echo "========================================="
echo ""
echo "Hot Reload está DESHABILITADO"
echo ""
echo "Presiona Ctrl+C para detener."
echo "========================================="
echo ""

mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
