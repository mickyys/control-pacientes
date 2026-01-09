#!/bin/bash

# Script para ejecutar Control Pacientes - Optimizado para startup rápido
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

# Ejecutar con opciones JVM optimizadas para startup rápido
java \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled \
  -XX:G1HeapRegionSize=16M \
  -Djava.awt.headless=false \
  -jar "$JAR_FILE" \
  --spring.profiles.active=prod

