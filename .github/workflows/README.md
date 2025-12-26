# GitHub Actions - Windows Installer Builder

Este workflow automático genera un instalable para Windows (.MSI) de la aplicación Control Pacientes.

## Características

✅ **Compilación automática** con Maven
✅ **Generación de instalador MSI** usando jpackage
✅ **Icono personalizado** desde `src/main/resources/images/icono.png`
✅ **Acceso directo** en el menú de inicio y escritorio
✅ **Ejecutable alternativo** usando Launch4j (fallback)
✅ **Artefactos públicos** descargables en las acciones
✅ **Releases automáticas** al hacer tag en GitHub

## Cómo usar

### 1. Trigger automático

El workflow se ejecuta automáticamente en los siguientes eventos:

- **Push a ramas**: `main` o `develop`
- **Pull Requests** a las ramas anteriores
- **Tags de versión**: `v*` (ej: v1.0.0, v1.0.1)
- **Manual**: Desde la pestaña "Actions" en GitHub

### 2. Descargar el instalador

**Opción A - Desde Artifacts (últimas compilaciones)**
1. Ve a la pestaña "Actions" en GitHub
2. Selecciona la ejecución más reciente del workflow
3. Descarga los artefactos en la sección "Artifacts"

**Opción B - Desde Releases (versiones etiquetadas)**
1. Crea un tag en tu repositorio:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```
2. El instalador se descargará automáticamente en "Releases"

## Requisitos previos

✅ El archivo `src/main/resources/images/icono.png` debe existir
✅ Java 19+ instalado (el workflow lo configura automáticamente)
✅ pom.xml correctamente configurado (ya está listo)

## Estructura de carpetas para build

```
target/
├── ControlPacientes.msi          ← Instalador principal (jpackage)
├── ControlPacientes.exe          ← Ejecutable alternativo (Launch4j)
└── image/                        ← Imagen de aplicación jpackage
```

## Pasos del workflow

1. **Checkout**: Descarga el código del repositorio
2. **Setup JDK 19**: Configura Java 19
3. **Maven Build**: Compila la aplicación
4. **Create App Image**: Genera imagen de aplicación
5. **Download WiX**: Instala herramientas necesarias
6. **Create MSI**: Genera instalador Windows
7. **Create EXE**: Genera ejecutable alternativo
8. **Upload Artifacts**: Sube los instaladores
9. **Create Release**: (Solo para tags) Crea release oficial

## Personalización

### Cambiar la versión

Edita el archivo `.github/workflows/build-windows-installer.yml`:

```yaml
--app-version "1.0.0"  # Cambiar aquí
```

### Cambiar el nombre de la aplicación

```yaml
--name "MiAplicacion"  # Cambiar aquí
```

### Cambiar el grupo del menú de inicio

```yaml
--win-menu-group "MiGrupo"  # Cambiar aquí
```

### Aumentar la memoria JVM

```yaml
--java-options "-Xmx2048m"  # Cambiar 2048 por el valor deseado (MB)
```

## Solución de problemas

### ❌ "El icono no se encuentra"
- Verifica que existe: `src/main/resources/images/icono.png`
- El archivo debe ser PNG

### ❌ "jpackage falla"
- Asegúrate de que `pom.xml` está correcto
- Verifica que la clase principal es: `com.controlpacientes.ControlPacientesApplication`

### ❌ "El instalador no se descarga"
- Revisa la sección "Artifacts" en la ejecución del workflow
- Si hay errores, revisa los logs de la acción

## Variables de entorno

El workflow usa `GITHUB_TOKEN` automáticamente para crear releases.
No requiere configuración adicional.

## Licencia

Asegúrate de que existe un archivo `LICENSE` en la raíz del repositorio
(opcional pero recomendado para el instalador).

---

**Nota**: La primera ejecución puede tardar más (descargando herramientas).
Las siguientes serán más rápidas gracias al caché de Maven.
