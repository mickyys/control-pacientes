#!/bin/bash

# Script para ejecutar Control Pacientes
# Uso: ./run.sh

echo "========================================="
echo "Control Pacientes - Aplicación"
echo "========================================="
echo ""

JAR_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/target/control-pacientes-java-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: El archivo JAR no se encuentra en:"
    echo "$JAR_FILE"
    echo ""
    echo "Por favor, ejecuta primero: mvn clean package"
    exit 1
fi

echo "Iniciando Control Pacientes..."
echo "JAR: $JAR_FILE"
echo ""

# Ejecutar con perfil de producción por defecto
java -jar "$JAR_FILE" --spring.profiles.active=prod

