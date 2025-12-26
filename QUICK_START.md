# 🚀 Guía Rápida - Generador de Instalable Windows

## 3 formas de generar el instalable

### ✅ OPCIÓN 1: GitHub Actions (Recomendado)

```bash
# 1. Sube tu código a GitHub
git add .
git commit -m "Agregar GitHub Actions para Windows"
git push

# 2. Crea una versión etiquetada
git tag v1.0.0
git push origin v1.0.0

# 3. Descarga en GitHub
# Actions → build-windows-installer → Artifacts
```

**Ventajas**: 
- ✅ Automatizado
- ✅ Compila en Windows automático
- ✅ Descargable desde GitHub

---

### 💻 OPCIÓN 2: En Windows (Local)

```powershell
# 1. Abre PowerShell como Administrador
# 2. Ve a la carpeta del proyecto
# 3. Ejecuta:

.\build-windows-installer.ps1

# 4. El instalador estará en: target/installer/ControlPacientes-1.0.0.msi
```

**Ventajas**:
- ✅ Construcción local
- ✅ Control total
- ✅ Más rápido

---

### 🖥️ OPCIÓN 3: En macOS/Linux (Local)

```bash
# 1. Abre Terminal
# 2. Ve a la carpeta del proyecto  
# 3. Ejecuta:

chmod +x build-windows-installer.sh
./build-windows-installer.sh

# 4. Se generará instalador para tu sistema (.dmg o .deb)
# Para MSI verdadero, usa GitHub Actions o Windows
```

---

## 📦 Lo que se ha configurado

✅ **GitHub Actions Workflow** → `.github/workflows/build-windows-installer.yml`
- Genera automáticamente instalador MSI
- Se ejecuta en cada push/tag
- Incluye icono personalizado
- Crea acceso directo automático

✅ **Script PowerShell** → `build-windows-installer.ps1`
- Para construcción local en Windows
- Fácil de usar y personalizar

✅ **Script Bash** → `build-windows-installer.sh`
- Para construcción local en macOS/Linux
- Compatible con múltiples sistemas

✅ **Documentación** → `WINDOWS_INSTALLER.md`
- Guía completa y detallada
- Solución de problemas

✅ **Icono** 
- Usa automáticamente: `src/main/resources/images/icono.png`
- ✅ Ya existe en tu proyecto

---

## 📋 Requisitos

**Para GitHub Actions** (Recomendado):
- ✅ Repositorio en GitHub
- ✅ Java 19+ (configurable automáticamente)
- ✅ Nada más

**Para PowerShell (Windows)**:
- ✅ Java 19+ instalado
- ✅ Maven instalado
- ✅ PowerShell 7+
- ✅ Windows 10/11

**Para Bash (macOS/Linux)**:
- ✅ Java 19+ instalado
- ✅ Maven instalado
- ✅ Bash 4+

---

## 🎯 Características del Instalador

El instalador generado incluye:

✅ **Icono personalizado**
- Usa `src/main/resources/images/icono.png`
- Aparece en el instalador y atajos

✅ **Acceso directo en Menú Inicio**
- Grupo: "ControlPacientes"
- Fácil acceso

✅ **Opción de acceso directo en escritorio**
- El instalador pregunta si crear uno
- Personalizable

✅ **Desinstalador profesional**
- Acceso desde Panel de Control
- Limpieza automática

✅ **Ejecutable directo**
- Se puede crear desde línea de comandos
- Rápido y seguro

---

## 📊 Archivos creados/modificados

```
control-pacientes/
├── .github/workflows/
│   ├── build-windows-installer.yml    ← NUEVO: Workflow GitHub Actions
│   └── README.md                       ← NUEVO: Documentación workflow
├── build-windows-installer.ps1        ← NUEVO: Script PowerShell Windows
├── build-windows-installer.sh         ← NUEVO: Script Bash macOS/Linux
├── WINDOWS_INSTALLER.md               ← NUEVO: Guía detallada
└── QUICK_START.md                     ← NUEVO: Este archivo
```

---

## ⚡ Próximos Pasos

1. **Verificar que todo está correcto**
   ```bash
   git status  # Ver archivos nuevos
   ```

2. **Subir cambios a GitHub** (si usas GitHub Actions)
   ```bash
   git add .
   git commit -m "Agregar generador de instalable Windows"
   git push
   ```

3. **Generar el instalador**
   - Opción 1: Espera el workflow automático
   - Opción 2: Ejecuta script PowerShell localmente
   - Opción 3: Ejecuta script Bash localmente

4. **Personalizar** (opcional)
   - Cambiar nombre en `.github/workflows/build-windows-installer.yml`
   - Cambiar icono en `src/main/resources/images/`
   - Cambiar memoria JVM en los scripts

---

## ❓ Preguntas Frecuentes

**P: ¿Cómo cambio el nombre de la aplicación?**
R: Edita `APP_NAME` en los scripts o el YAML del workflow

**P: ¿Puedo usar otro icono?**
R: Sí, reemplaza `src/main/resources/images/icono.png`

**P: ¿Qué versión de Java necesito?**
R: Java 19 o superior con jpackage incluido

**P: ¿Puedo generar MSI en macOS?**
R: No, usa GitHub Actions (ejecuta en Windows) o genera en Windows

**P: ¿Dónde descargo el instalador?**
R: GitHub Actions → Artifacts o GitHub Releases (con tags)

---

## 🔗 Recursos útiles

- [jpackage Documentation](https://docs.oracle.com/en/java/javase/19/docs/specs/man/jpackage.html)
- [Adoptium JDK](https://adoptium.net/)
- [GitHub Actions](https://github.com/features/actions)
- [Maven Official](https://maven.apache.org/)

---

**¡Listo! Ahora puedes generar tu instalable Windows de forma profesional.** 🎉
