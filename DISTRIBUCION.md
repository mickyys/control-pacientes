## Distribución Exportable - Control Pacientes

### ✅ Build Completado

Tu aplicación ha sido compilada en un **JAR ejecutable autocontenido** que no requiere instalación.

### 📦 Ubicación del Ejecutable

```
target/control-pacientes-java-1.0.0.jar  (117 MB)
```

### 🚀 Cómo Ejecutar en Windows

**Opción 1: Script automático** (Recomendado)
```cmd
run.bat
```

**Opción 2: Script alternativo** (Si tienes problemas de rendering)
```cmd
run-debug.bat
```

**Opción 3: Directamente con Java**
```cmd
java -jar target\control-pacientes-java-1.0.0.jar
```

**Opción 4: Con perfil de desarrollo (con Hot Reload)**
```cmd
java -jar target\control-pacientes-java-1.0.0.jar --spring.profiles.active=dev
```

### 🐧 Cómo Ejecutar en macOS/Linux

```bash
./run.sh
```

O directamente:
```bash
java -jar target/control-pacientes-java-1.0.0.jar
```

### 📋 Requisitos del Sistema

- **Java 19 o superior** (lo único que necesitas instalar)
- Ejecutable en: Windows 10/11, macOS, Linux

### 🔧 Solución de Problemas en Windows

Si ves "QuantumRenderer: no suitable pipeline found":

1. **Intenta primero:** `run.bat` (script recomendado)
2. **Si no funciona:** `run-debug.bat` (configuración extendida)
3. **Como último recurso:** 
   ```cmd
   java -Dprism.useSWRender=true -jar target\control-pacientes-java-1.0.0.jar
   ```

### 📁 Distribución

Para distribuir tu aplicación:

1. Copiar el archivo: `target/control-pacientes-java-1.0.0.jar`
2. Copiar también: 
   - `run.bat` - Script de ejecución para Windows
   - `run.sh` - Script de ejecución para macOS/Linux
   - `run-debug.bat` - Script alternativo para Windows (opcional)

3. El usuario solo necesita:
   - Java 19+ instalado
   - Ejecutar: `run.bat` (Windows) o `./run.sh` (Mac/Linux)

### 🎯 Lo que Incluye el JAR

✅ Todas las dependencias de Spring Boot  
✅ JavaFX 19 completo (incluyendo bibliotecas nativas de Windows)  
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

Esto regenerará los scripts y el JAR con todas las dependencias.

