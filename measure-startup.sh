#!/bin/bash

# Script para medir tiempo de startup
# Uso: ./measure-startup.sh

echo "========================================="
echo "Medidor de Tiempo de Startup"
echo "========================================="
echo ""

JAR_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/target/control-pacientes-java-1.0.0.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: El archivo JAR no se encuentra"
    exit 1
fi

echo "Iniciando aplicación y midiendo tiempo..."
echo "Cierra la aplicación para ver el resultado"
echo ""

start_time=$(date +%s%N)

# Ejecutar con mismo perfil optimizado
java \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled \
  -XX:G1HeapRegionSize=16M \
  -Djava.awt.headless=false \
  -jar "$JAR_FILE" \
  --spring.profiles.active=prod

end_time=$(date +%s%N)

elapsed_ms=$(( (end_time - start_time) / 1000000 ))
elapsed_s=$(( elapsed_ms / 1000 ))
elapsed_ms=$(( elapsed_ms % 1000 ))

echo ""
echo "========================================="
echo "Tiempo total de ejecución:"
echo "${elapsed_s}s ${elapsed_ms}ms"
echo "========================================="
