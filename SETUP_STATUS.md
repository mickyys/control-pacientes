# Estado de Configuración - Windows Installer

## ✅ Completado

Los siguientes archivos y configuraciones han sido establecidos exitosamente:

### 1. GitHub Actions Workflow
- **Archivo**: `.github/workflows/build-windows-installer.yml`
- **Descripción**: Workflow automático que genera instalador MSI para Windows
- **Triggers**:
  - Push a ramas `main` y `develop`
  - Pull Requests
  - Tags de versión (v*)
  - Ejecución manual
- **Funciones**:
  - Compila con Maven
  - Genera aplicación imagen
  - Crea instalador MSI con jpackage
  - Crea ejecutable alternativo con Launch4j
  - Sube artefactos
  - Crea releases automáticas

### 2. Scripts de Construcción Local

#### PowerShell (Windows)
- **Archivo**: `build-windows-installer.ps1`
- **Uso**: `.\build-windows-installer.ps1`
- **Características**:
  - Construcción local en Windows
  - Parámetros personalizables
  - Validación de requisitos
  - Genera MSI para Windows

#### Bash (macOS/Linux)  
- **Archivo**: `build-windows-installer.sh`
- **Uso**: `./build-windows-installer.sh`
- **Características**:
  - Construcción local en Unix
  - Genera instaladores específicos del SO (DMG, DEB)
  - Validación de requisitos
  - Información detallada en consola

### 3. Documentación

#### QUICK_START.md
- Guía rápida con 3 opciones
- Instrucciones resumidas
- FAQ
- Próximos pasos

#### WINDOWS_INSTALLER.md
- Guía completa y detallada
- Todas las opciones explicadas
- Solución de problemas
- Información técnica
- Matriz de compatibilidad

#### .github/workflows/README.md
- Documentación específica del workflow
- Cómo usar GitHub Actions
- Personalización
- Estructura de archivos

### 4. Recursos del Proyecto

#### Icono
- **Archivo**: `src/main/resources/images/icono.png`
- **Estado**: ✅ Verificado - Archivo existe
- **Uso**: Automáticamente en instalador, menú y escritorio

#### pom.xml
- **Estado**: ✅ Compatible
- **Versión Java**: 19
- **Dependencias**: JavaFX 19, Spring Boot, etc.

---

## 🎯 Características del Instalador Generado

El instalador Windows (.MSI) incluirá:

✅ **Icono personalizado**
- Origen: `src/main/resources/images/icono.png`
- Aparece en: Instalador, accesos directos, ejecutable

✅ **Acceso directo en Menú Inicio**
- Grupo: "ControlPacientes"
- Nombre: "Control Pacientes"
- Icono: Personalizado

✅ **Opción de acceso directo en escritorio**
- Se pregunta durante la instalación
- Personalizable

✅ **Información de la aplicación**
- Nombre: ControlPacientes
- Versión: 1.0.0
- Descripción: Sistema de Control de Pacientes
- Vendor: ControlPacientes

✅ **Desinstalador profesional**
- Accesible desde Panel de Control
- Desinstalación limpia

---

## 🚀 Cómo Usar

### Opción 1: GitHub Actions (Recomendado)
```bash
# Subir código
git add .
git commit -m "Agregar Windows installer"
git push

# Crear versión
git tag v1.0.0
git push origin v1.0.0

# Descargar en GitHub
# Actions → build-windows-installer → Artifacts
```

### Opción 2: PowerShell (Windows Local)
```powershell
.\build-windows-installer.ps1
# Resultado: target/installer/ControlPacientes-1.0.0.msi
```

### Opción 3: Bash (macOS/Linux Local)
```bash
chmod +x build-windows-installer.sh
./build-windows-installer.sh
# Resultado: target/installer/ (DMG o DEB según SO)
```

---

## 📋 Requisitos

| Requisito | GitHub Actions | Local Windows | Local macOS/Linux |
|-----------|-----------------|---------------|------------------|
| Java 19+ | Automático | Necesario | Necesario |
| Maven | Cacheable | Necesario | Necesario |
| Sistema Operativo | Windows | Windows | macOS/Linux |
| Git | No | No | Recomendado |
| PowerShell | No | Necesario | No |
| Bash | No | No | Necesario |

---

## 📊 Estructura de Archivos

```
control-pacientes/
├── .github/
│   └── workflows/
│       ├── build-windows-installer.yml    ← GitHub Actions workflow
│       └── README.md                      ← Documentación workflow
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/controlpacientes/...  ← Código fuente
│   │   └── resources/
│   │       └── images/
│   │           └── icono.png             ← Icono usado ✅
│   └── test/
├── build-windows-installer.ps1           ← Script PowerShell
├── build-windows-installer.sh            ← Script Bash
├── QUICK_START.md                        ← Guía rápida ← EMPIEZA AQUÍ
├── WINDOWS_INSTALLER.md                  ← Guía completa
├── pom.xml                               ← Configuración Maven
└── README.md
```

---

## ⚙️ Personalización Rápida

### Cambiar nombre de la aplicación
**Archivo**: `.github/workflows/build-windows-installer.yml`
```yaml
--name "MiAplicacion"  # Cambiar aquí
--win-menu-group "MiAplicacion"  # Y aquí
```

### Cambiar versión
**Archivo**: `.github/workflows/build-windows-installer.yml`
```yaml
--app-version "1.1.0"  # Cambiar aquí
```

### Cambiar icono
1. Reemplazar: `src/main/resources/images/icono.png`
2. Debe ser PNG de alta resolución (256x256+)
3. Recompilar

### Aumentar memoria JVM
**Archivo**: `.github/workflows/build-windows-installer.yml`
```yaml
--java-options "-Xmx4096m"  # Cambiar 4096 por MB deseado
```

---

## 🔍 Verificación

Para verificar que todo está configurado correctamente:

```bash
# 1. Verificar archivos creados
ls -la .github/workflows/build-windows-installer.yml
ls -la build-windows-installer.ps1
ls -la build-windows-installer.sh
ls -la WINDOWS_INSTALLER.md
ls -la QUICK_START.md

# 2. Verificar icono existe
ls -la src/main/resources/images/icono.png

# 3. Verificar pom.xml
cat pom.xml | grep "java.version"

# 4. Compilar localmente (test)
mvn clean package -DskipTests
```

---

## 📞 Soporte y Troubleshooting

Consulta los siguientes archivos según tu caso:

| Problema | Consultar |
|----------|-----------|
| ¿Cómo empiezo? | `QUICK_START.md` |
| ¿Instrucciones detalladas? | `WINDOWS_INSTALLER.md` |
| ¿Cómo funciona GitHub Actions? | `.github/workflows/README.md` |
| ¿Error en compilación? | `WINDOWS_INSTALLER.md` - Solución de problemas |
| ¿Personalizar instalador? | `.github/workflows/build-windows-installer.yml` |

---

## 🎯 Próximos Pasos

1. **Revisar guía rápida**: Abre `QUICK_START.md`
2. **Elegir método**: GitHub Actions, PowerShell o Bash
3. **Generar instalador**: Sigue las instrucciones
4. **Personalizar** (opcional): Edita los scripts si lo deseas
5. **Distribuir**: Comparte el `.msi` con los usuarios

---

## ✨ Resumen de Configuración

| Componente | Estado | Ubicación |
|-----------|--------|-----------|
| Workflow GitHub Actions | ✅ Configurado | `.github/workflows/build-windows-installer.yml` |
| Script PowerShell | ✅ Listo | `build-windows-installer.ps1` |
| Script Bash | ✅ Listo | `build-windows-installer.sh` |
| Guía rápida | ✅ Lista | `QUICK_START.md` |
| Guía completa | ✅ Lista | `WINDOWS_INSTALLER.md` |
| Icono | ✅ Existe | `src/main/resources/images/icono.png` |
| Configuración Maven | ✅ Compatible | `pom.xml` |

---

**Configuración completada: 26 de Diciembre de 2024**

🎉 ¡Tu proyecto está listo para generar instalables profesionales para Windows!
