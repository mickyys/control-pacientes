# Guía de Generación de Instalable Windows

## 📋 Resumen

Esta guía te ayudará a generar un instalable para Windows (.MSI) de **Control Pacientes** con:
- ✅ Icono personalizado
- ✅ Acceso directo en el escritorio
- ✅ Entrada en el Menú Inicio
- ✅ Instalador profesional

---

## 🚀 Opción 1: Usar GitHub Actions (Recomendado)

### Pasos:

1. **Configura tu repositorio en GitHub**
   ```bash
   git remote add origin https://github.com/TuUsuario/control-pacientes.git
   git push -u origin main
   ```

2. **Activa el workflow automáticamente**
   - El archivo `.github/workflows/build-windows-installer.yml` ya está configurado
   - Se ejecutará automáticamente en cada push

3. **Descarga el instalador**
   - Ve a la pestaña **"Actions"** en GitHub
   - Selecciona la ejecución más reciente
   - Descarga el artefacto `control-pacientes-windows-installer`

### Crear una versión oficial (Release):

```bash
# Crear un tag para la versión
git tag v1.0.0

# Subir el tag
git push origin v1.0.0
```

El instalador se descargará automáticamente en la sección **"Releases"**.

---

## 💻 Opción 2: Construcción Local en Windows

### Requisitos:

- **Java 19+** ([Descargar](https://adoptium.net/))
- **Maven 3.8+** ([Descargar](https://maven.apache.org/))
- **Windows 10/11**
- Icono en: `src/main/resources/images/icono.png`

### Pasos:

1. **Abre PowerShell** como Administrador en la carpeta del proyecto

2. **Ejecuta el script de construcción**
   ```powershell
   .\build-windows-installer.ps1
   ```

   O con opciones personalizadas:
   ```powershell
   .\build-windows-installer.ps1 -Version "1.0.0" -OutputDir "instaladores"
   ```

3. **El instalador se generará en**: `target/installer/ControlPacientes-1.0.0.msi`

4. **Instala el programa**
   - Ejecuta el archivo `.msi`
   - Sigue el asistente de instalación
   - Se creará un acceso directo automáticamente

---

## 🛠️ Opción 3: Construcción Local en macOS/Linux

### Requisitos:

- **Java 19+** ([Descargar](https://adoptium.net/))
- **Maven 3.8+** ([Descargar](https://maven.apache.org/))
- **Bash/Zsh**

### Pasos:

1. **Abre Terminal** en la carpeta del proyecto

2. **Haz ejecutable el script**
   ```bash
   chmod +x build-windows-installer.sh
   ```

3. **Ejecuta el script**
   ```bash
   ./build-windows-installer.sh
   ```

   **Nota**: En macOS/Linux generará instaladores DMG/DEB, no MSI.
   Para MSI verdadero, usa la Opción 1 (GitHub Actions) o Opción 2 (Windows).

---

## 📦 Estructura de Archivos Generados

```
target/installer/
├── ControlPacientes-1.0.0.msi     ← Instalador principal
└── ControlPacientes.exe           ← Ejecutable (backup)
```

---

## 🎯 Características del Instalador

### Después de instalar, el usuario tendrá:

1. **Ejecutable en Program Files**
   ```
   C:\Program Files\ControlPacientes\ControlPacientes.exe
   ```

2. **Acceso directo en Menú Inicio**
   ```
   Inicio → Todas las aplicaciones → ControlPacientes
   ```

3. **Opción para crear acceso directo en escritorio**
   - El instalador pregunta si crear uno

4. **Icono personalizado**
   - Usa la imagen de `src/main/resources/images/icono.png`

### Desinstalar:

1. Abre **Panel de Control → Programas → Programas y características**
2. Busca "ControlPacientes"
3. Haz clic en "Desinstalar"

---

## ⚙️ Personalización

### Cambiar el nombre de la aplicación

Edita en `.github/workflows/build-windows-installer.yml`:

```yaml
--name "Mi Aplicación"  # Cambiar aquí
```

O en el script PowerShell:

```powershell
$APP_NAME = "Mi Aplicación"
```

### Cambiar el icono

1. Reemplaza el archivo: `src/main/resources/images/icono.png`
2. Debe ser PNG en alta resolución (256x256 o superior)
3. Recompila el proyecto

### Cambiar memoria JVM

En `.github/workflows/build-windows-installer.yml`, busca:

```yaml
--java-options "-Xmx2048m"  # Cambiar 2048 por el valor deseado (MB)
```

---

## 🔧 Solución de Problemas

### ❌ "jpackage no se encuentra"

**Causa**: Java sin jpackage

**Solución**:
```bash
# Verifica tu versión de Java
java -version

# Descarga Java 19+ con jpackage incluido
# https://adoptium.net/ → Busca "Latest LTS" de 19 en adelante
```

### ❌ "El icono no se carga"

**Causa**: El archivo PNG no existe o está en otra ubicación

**Solución**:
```bash
# Verifica que el archivo existe
ls src/main/resources/images/icono.png

# Si no existe, copiar desde otra ubicación
cp tu-icono.png src/main/resources/images/icono.png
```

### ❌ "Error de compilación"

**Causa**: Dependencias faltantes o Java incompatible

**Solución**:
```bash
# Limpia y recompila
mvn clean package -DskipTests

# Si aún falla, verifica que tienes Java 19+
java -version
```

### ❌ "El instalador no aparece"

**Causa**: Dirección de salida incorrecta

**Solución**:
```bash
# Verifica que la carpeta target existe
ls -la target/

# Si no existe, compila primero
mvn clean package
```

---

## 📊 Matriz de Compatibilidad

| Sistema | Opción | Formato | Nota |
|---------|--------|---------|------|
| Windows | GitHub Actions | .MSI | ✅ Recomendado |
| Windows | Script PowerShell | .MSI | ✅ Local |
| macOS | Script Bash | .DMG | Instalador macOS |
| Linux | Script Bash | .DEB | Instalador Linux |

---

## 📝 Información Técnica

### Tecnologías utilizadas:

- **jpackage**: Herramienta de Java para crear instaladores
- **WiX Toolset**: Para compilar MSI en Windows (GitHub Actions)
- **Launch4j**: Wrapper para crear EXE ejecutables
- **Maven**: Para compilación Java

### Flujo de construcción:

```
1. Maven compila JAR
   ↓
2. jpackage crea imagen de app
   ↓
3. jpackage genera MSI
   ↓
4. Subida a Artifacts/Releases
```

---

## 🤝 Soporte

Si tienes problemas:

1. Revisa los logs del workflow en GitHub Actions
2. Verifica que Java 19+ está instalado
3. Asegúrate de que `src/main/resources/images/icono.png` existe
4. Intenta limpiar y recompilar: `mvn clean package -DskipTests`

---

**Última actualización**: 26 de Diciembre de 2024
