# GitHub Actions - Automatización de Build

## 📋 Workflows Disponibles

### 1. Build JAR Artifacts (`build-jars.yml`)
Genera JARs multiplataforma ejecutables

**Triggers:**
- Push a `main` o `develop`
- Pull requests
- Tags `v*`
- Manual trigger

**Genera:**
- `control-pacientes.jar` (205 MB) - Versión productiva
- `control-pacientes-java-1.0.0.jar` (1.4 MB) - Versión desarrollo

**Artifacts:**
- Disponibles en la sección de Actions por 30 días
- Incluidos automáticamente en Releases (cuando se crea tag)

### 2. Build Windows Installer (`build-windows-installer.yml`)
Genera MSI instalador para Windows + JARs

**Triggers:**
- Push a `main` o `develop`
- Pull requests
- Tags `v*`
- Manual trigger

**Genera:**
- `ControlPacientes-*.msi` - Instalador Windows
- `control-pacientes.jar` (205 MB) - JAR productivo
- `control-pacientes-java-1.0.0.jar` (1.4 MB) - JAR desarrollo

**Artifacts:**
- Disponibles en la sección de Actions por 30 días
- Incluidos automáticamente en Releases (cuando se crea tag)

**Requisitos:**
- Ejecuta en `windows-latest`
- Instala WiX Toolset automáticamente
- JDK 19 con Temurin

## 🚀 Cómo Usar

### Generar Solo JARs
1. Push a `develop` o crea PR → Workflow automático
2. O ejecuta manualmente: Actions → Build JAR Artifacts → Run workflow

### Crear Release con JARs + MSI
1. Crea un tag: `git tag v1.0.1`
2. Push: `git push origin v1.0.1`
3. Ambos workflows se ejecutan
4. GitHub Release se crea automáticamente con:
   - JAR productivo
   - JAR desarrollo
   - MSI instalador (si tiene exito Windows build)

### Descargar Artifacts

**Durante desarrollo:**
1. Ve a Actions
2. Selecciona workflow completado
3. Descarga "Artifacts" en el panel derecho

**Releases:**
1. Ve a Releases
2. Descarga los archivos asociados

## 📊 Estado de los Workflows

| Workflow | Rama | Trigger | Tiempo |
|----------|------|---------|--------|
| JAR | main/develop | Push/PR/Tag | ~5 min |
| Windows | main/develop | Push/PR/Tag | ~15 min |

## 🔧 Configuración

### Variables de Entorno
```yaml
GITHUB_TOKEN: Auto-inyectado por GitHub
```

### Cache Maven
- Automáticamente cacheado por `setup-java@v4`
- Acelera builds posteriores

### Retención de Artifacts
- Desarrollo: 30 días
- Releases: Indefinido

## 📦 Contenido de Artifacts

### JAR Production (control-pacientes.jar)
```
✓ Spring Boot 3.1.7
✓ JavaFX 19 (todos los módulos)
✓ SQLite driver
✓ Apache POI
✓ iText PDF
✓ Todas las dependencias
✓ Configuración optimizada
```

### JAR Development (control-pacientes-java-1.0.0.jar)
```
✓ Spring Boot 3.1.7
✓ JavaFX 19
✓ SQLite driver
✓ Herramientas de desarrollo
✓ Logging completo
```

## 🔐 Seguridad

- JARs compilados únicamente en repos autorizados
- Artifacts almacenados 30 días (GitHub)
- Releases firmadas con GitHub
- No se almacenan credenciales

## 📝 Logs y Debugging

### Ver logs de workflow
1. Ve a Actions
2. Selecciona workflow
3. Haz clic en job `build-jars` o `build-windows`
4. Expande los steps para ver detalles

### Problemas Comunes

**Maven cache corrupto:**
- Solución: Ejecutar con `--no-cache`

**JDK no encontrado:**
- Solución: setup-java automáticamente lo instala

**Artifacts no generados:**
- Verifica logs de Maven
- Revisa si hay errores de compilación

## 🚀 Próximas Mejoras

- [ ] Notificaciones Slack en completarse
- [ ] Firma digital de JARs
- [ ] Pruebas unitarias en CI
- [ ] Análisis de código (SonarQube)
- [ ] Docker image build
- [ ] Linux AppImage
- [ ] macOS DMG

## 📞 Soporte

Revisa los logs en GitHub Actions para troubleshooting detallado.
