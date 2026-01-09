#!/bin/bash

# Script para ejecutar Control Pacientes en Producción
# JAR: control-pacientes.jar (versión productiva optimizada)
# Uso: ./run-prod.sh

echo "========================================="
echo "Control Pacientes - PRODUCCIÓN"
echo "========================================="
echo ""

JAR_FILE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)/target/control-pacientes.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "Error: El archivo JAR de producción no se encuentra"
    echo "Genera el JAR con: mvn clean package -Pprod"
    exit 1
fi

echo "JAR: $JAR_FILE"
echo "Tamaño: $(du -h "$JAR_FILE" | cut -f1)"
echo ""
echo "Iniciando en modo PRODUCCIÓN..."
echo ""

# Ejecutar con configuración productiva
# -XX:+UseG1GC: Garbage collector optimizado
# -XX:MaxGCPauseMillis=200: Pausas cortas de GC
# -Xms1g / -Xmx2g: Asignación de memoria recomendada para producción
exec java \
  -server \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+ParallelRefProcEnabled \
  -XX:G1HeapRegionSize=16M \
  -XX:+UnlockDiagnosticVMOptions \
  -XX:G1SummarizeRSetStatsPeriod=1 \
  -Xms1g \
  -Xmx2g \
  -Djava.awt.headless=false \
  -Dfile.encoding=UTF-8 \
  -Dspring.profiles.active=prod \
  -jar "$JAR_FILE"
