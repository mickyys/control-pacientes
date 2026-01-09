# JAR Productivo - Control Pacientes

## 📦 Archivos Generados

### Versión de Desarrollo/Testing
- **control-pacientes-java-1.0.0.jar** (1.4 MB)
- Ligero, rápido para desarrollo
- Incluye herramientas de debug

### Versión Productiva (RECOMENDADA)
- **control-pacientes.jar** (205 MB)
- Totalmente optimizado para producción
- Todas las dependencias incluidas
- Configuración de producción aplicada

## 🚀 Cómo Ejecutar

### macOS/Linux - Modo Producción
```bash
./run-prod.sh
```

### Windows - Modo Producción
```cmd
run-prod.bat
```

### Línea de comandos (cualquier SO)
```bash
java -server -XX:+UseG1GC -Xms1g -Xmx2g -jar target/control-pacientes.jar
```

## 🔧 Configuraciones de Producción Aplicadas

### Logging
```properties
logging.level.root=WARN              # Solo warnings
logging.level.com.controlpacientes=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
```

### Base de Datos
```properties
spring.jpa.hibernate.ddl-auto=validate    # Solo validar, no crear
spring.jpa.show-sql=false                 # Sin SQL logging
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.idle-timeout=600000
```

### JVM Tuning
```
-server                      # Optimizaciones de servidor
-XX:+UseG1GC                # Garbage collector G1
-XX:MaxGCPauseMillis=200    # Pausas cortas de GC
-Xms1g                      # Heap mínimo: 1 GB
-Xmx2g                      # Heap máximo: 2 GB
```

## 📊 Comparativa

| Aspecto | Desarrollo | Producción |
|---------|-----------|-----------|
| Tamaño JAR | 1.4 MB | 205 MB |
| Logging | DEBUG | WARN/INFO |
| Validación DB | UPDATE | VALIDATE |
| GC Pauses | Normal | Optimizado |
| Startup Time | ~10-15s | ~15-20s |
| Performance | Bueno | Excelente |
| Memory | 512 MB - 1 GB | 1 GB - 2 GB |

## ⚙️ Generación del JAR Productivo

Si necesitas regenerar el JAR productivo:

```bash
mvn clean package -Pprod
```

Esto generará:
- `target/control-pacientes.jar` (Versión productiva)
- `target/control-pacientes-java-1.0.0.jar` (Versión dev)

## 🔐 Recomendaciones de Seguridad

1. **Firewall**: Ejecutar detrás de firewall corporativo
2. **Certificados**: Usar certificados SSL si se expone a red
3. **Base de Datos**: Hacer backup regular de `control_pacientes.db`
4. **Logging**: Monitorear logs de errores
5. **Actualizaciones**: Mantener Java actualizado

## 📈 Monitoreo

### Ver logs de startup
```bash
java -XX:+PrintCompilation -jar target/control-pacientes.jar
```

### Ver estadísticas de GC
```bash
java -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -jar target/control-pacientes.jar
```

## 🛠️ Solución de Problemas

### Aumentar memoria si la aplicación es lenta
```bash
java -Xms2g -Xmx4g -jar target/control-pacientes.jar
```

### Reducir memoria (sistemas con pocas recursos)
```bash
java -Xms512m -Xmx1g -jar target/control-pacientes.jar
```

### Deshabilitar validación de DB (si tienes problemas)
```bash
java -jar target/control-pacientes.jar \
  --spring.jpa.hibernate.ddl-auto=update
```

## 📋 Requisitos Mínimos del Sistema

- **Java 19+** instalado
- **RAM**: Mínimo 2 GB disponible
- **Almacenamiento**: 500 MB libres para JAR + BD
- **SO**: Windows 10+, macOS 10.14+, Linux (cualquier versión)

## 🎯 Distribución Recomendada

Para distribuir tu aplicación, envía:

1. **JAR Productivo**: `target/control-pacientes.jar`
2. **Scripts**: `run-prod.sh` y `run-prod.bat`
3. **Guía**: Este archivo `JAR_PRODUCTIVO.md`

El usuario solo necesita tener Java 19+ instalado.

## 📞 Diferencias con Desarrollo

| Tarea | Desarrollo | Producción |
|------|-----------|-----------|
| Generar | `mvn package` | `mvn package -Pprod` |
| Ejecutar | `./run.sh` | `./run-prod.sh` |
| Logging | Detallado | Mínimo |
| Memoria | Flexible | 1-2 GB dedicado |
| Validación BD | Auto-actualizar | Solo validar |
