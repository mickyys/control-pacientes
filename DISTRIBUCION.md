## Distribución Exportable - Control Pacientes

### ✅ Build Completado

Tu aplicación ha sido compilada en un **JAR ejecutable autocontenido** que no requiere instalación.

### 📦 Ubicación del Ejecutable

```
target/control-pacientes-java-1.0.0.jar  (109 MB)
```

### 🚀 Cómo Ejecutar

**Opción 1: Usando el script** (Recomendado)
```bash
./run.sh
```

**Opción 2: Directamente con Java**
```bash
java -jar target/control-pacientes-java-1.0.0.jar
```

**Opción 3: Con perfil de desarrollo (con Hot Reload)**
```bash
java -jar target/control-pacientes-java-1.0.0.jar --spring.profiles.active=dev
```

### 📋 Requisitos del Sistema

- **Java 19 o superior** (lo único que necesitas instalar)
- Ejecutable en: Windows, macOS, Linux

### 📁 Distribución

Para distribuir tu aplicación:

1. Copiar el archivo: `target/control-pacientes-java-1.0.0.jar`
2. Copiar también (opcional): 
   - `run.sh` - Script de ejecución para macOS/Linux
   - `run.bat` - Script de ejecución para Windows (crear si es necesario)

3. El usuario solo necesita:
   - Java 19+ instalado
   - Ejecutar: `./run.sh` o `java -jar control-pacientes-java-1.0.0.jar`

### 🎯 Lo que Incluye el JAR

✅ Todas las dependencias de Spring Boot  
✅ JavaFX 19 (UI)  
✅ SQLite (Base de datos integrada)  
✅ Apache POI (Excel)  
✅ iText (PDF)  
✅ Todas las configuraciones y recursos  

### 💾 Base de Datos

La aplicación usa SQLite, que se crea automáticamente en la carpeta de datos del usuario.

### 🔄 Para Recompilar

Si haces cambios en el código:
```bash
mvn clean package -DskipTests
```

