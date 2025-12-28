# Fix para el Instalador de Windows

## Problema Identificado

El instalador de Windows no se estaba creando correctamente porque:

1. **Error WiX**: El archivo `main.wxs` personalizado contenía referencias a variables que no eran compatibles con el proceso automático de jpackage
2. **Error específico**: `Unresolved bind-time variable !(bind.FileVersion.ControlPacientes.exe)`
3. **Causa raíz**: La configuración de WiX estaba intentando usar referencias de archivos que jpackage genera dinámicamente

## Cambios Realizados

### 1. Simplificación de `src/main/resources/wix/main.wxs`

Se simplificó el archivo WiX para ser completamente compatible con jpackage:

- ✅ Removidas todas las referencias manuales a archivos ejecutables
- ✅ Utilizadas solo las variables provistas automáticamente por jpackage
- ✅ Removida la configuración de detección de Java (jpackage la maneja)
- ✅ Configuración básica de directorios y features
- ✅ Soporte para actualización automática

### 2. Actualización del Workflow de GitHub Actions

Se simplificó el comando `jpackage` en `.github/workflows/build-windows-installer.yml`:

- ✅ Removidas las opciones que conflictaban (`--install-dir`, `--module-path`, `--add-modules`)
- ✅ jpackage ahora usa sus valores por defecto para ruta de módulos
- ✅ Removida la referencia a `--resource-dir` (el archivo WiX se copia internamente)
- ✅ Agregadas opciones Java necesarias (`-Xmx512m`, `-Dfile.encoding=UTF-8`)

### 3. Actualización del Script Local PowerShell

El archivo `build-windows-installer.ps1` también fue actualizado para consistencia:

- ✅ Mismos parámetros simplificados
- ✅ Mejor manejo de errores
- ✅ Mensajes de estado mejorados

## Cómo Probar

### En Windows (Local)

```powershell
# 1. Navega al directorio del proyecto
cd C:\ruta\a\control-pacientes

# 2. Ejecuta el script de construcción
.\build-windows-installer.ps1

# 3. El instalador se generará en target/installer/
```

### En GitHub Actions

```bash
# 1. Hacer un commit y push de los cambios
git add .
git commit -m "Fix: Corregir configuración de instalador Windows"
git push origin main

# 2. El workflow se ejecutará automáticamente
# 3. Descarga el artefacto desde la pestaña "Actions"
```

### En GitHub Releases

```bash
# 1. Crear un tag para generar una versión oficial
git tag v1.0.0
git push origin v1.0.0

# 2. El instalador se creará automáticamente en "Releases"
```

## Validación

Después de generar el instalador:

1. **Ejecuta el MSI**: `ControlPacientes-1.0.0.msi`
2. **Verifica la instalación**:
   - ✅ Se crea acceso directo en el escritorio
   - ✅ Se crea entrada en el Menú Inicio
   - ✅ La aplicación se puede ejecutar desde cualquier acceso directo
   - ✅ Se registra correctamente en "Programas y características"
   - ✅ La desinstalación funciona correctamente

3. **Comprueba la base de datos**:
   - Los datos se almacenan en: `C:\ProgramData\ControlPacientes\data\`
   - Los datos persisten después de la desinstalación

## Variables de Configuración

Las siguientes variables son proporcionadas automáticamente por jpackage:

```
- JpProductCode: Identificador único del producto
- JpProductUpgradeCode: Código para detectar upgrades
- JpAppName: Nombre de la aplicación (ControlPacientes)
- JpAppVersion: Versión de la aplicación (1.0.0)
- JpAppDescription: Descripción de la aplicación
- JpAppVendor: Fabricante de la aplicación
- JpConfigDir: Directorio de configuración
```

## Proximos Pasos Opcionales

Si deseas agregar más funcionalidad al instalador:

1. **Componente para crear directorio de datos**:
   - Se puede hacer referencia en Feature usando `<ComponentRef>`
   
2. **Atajos de escritorio/menú personalizados**:
   - Agregar componentes adicionales en el archivo WiX
   - Cada componente debe tener un GUID único

3. **Localización en español**:
   - El archivo `MsiInstallerStrings_es.wxl` puede contener textos personalizados
   - Se pasa automáticamente a través de `--resource-dir` si lo necesitas

## Resolución de Problemas

### Error: "light.exe exited with 298 code"
- **Causa**: Referencia a variable no resuelta
- **Solución**: Verificar que el archivo WiX solo use variables de jpackage

### Error: "jpackage not found"
- **Causa**: JDK no incluye jpackage (Java < 14)
- **Solución**: Usar Java 14+ (recomendado Java 19+)

### El instalador no crea accesos directos
- **Causa**: El archivo WiX no incluye componentes para atajos
- **Solución**: Pasar `--win-menu` y `--win-shortcut` a jpackage

## Referencias

- [Documentación de jpackage](https://docs.oracle.com/en/java/javase/19/jpackage/overview.html)
- [WiX Toolset Documentation](https://wixtoolset.org/)
- [Spring Boot con jpackage](https://spring.io/blog/2022/10/20/spring-boot-project-build-packaging-with-spring-native-and-jpackage)
