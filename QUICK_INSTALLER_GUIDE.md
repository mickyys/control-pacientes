# 🚀 INSTRUCCIONES RÁPIDAS - Generar Instalador Windows

## ⚡ Opción 1: Automático (Recomendado)

```bash
# 1. Hacer commit de los cambios
git add .
git commit -m "Fix: Corregir instalador Windows"
git push origin main

# 2. Esperar a GitHub Actions (2-5 minutos)
# 3. Descargar MSI desde: Actions > Última ejecución > Artifacts
```

## 💻 Opción 2: Compilar Localmente (Windows)

```powershell
# 1. Abre PowerShell como Administrador
# 2. Navega al proyecto
cd C:\ruta\a\control-pacientes

# 3. Instala WiX Toolset (si no lo tienes)
choco install wixtoolset

# 4. Genera el instalador
.\build-windows-installer.ps1

# 5. Valida el resultado
.\validate-installer.ps1

# 6. El MSI está en: target\output\ControlPacientes-1.0.0.msi
```

## ✅ Verificación Rápida

Después de generar, verifica:

```powershell
# Ver si el MSI fue creado
Get-ChildItem target\*.msi

# Ver tamaño (debe ser ~100-200 MB)
Get-ChildItem target\*.msi | Format-Table Length
```

## 📦 Instalar el MSI

```powershell
# Opción 1: Interfaz gráfica (más fácil)
# Haz doble clic en ControlPacientes-1.0.0.msi

# Opción 2: Línea de comandos
msiexec /i ControlPacientes-1.0.0.msi

# Opción 3: Instalación silenciosa
msiexec /i ControlPacientes-1.0.0.msi /quiet
```

## 🔍 Qué Validar Después de Instalar

- ✅ Ícono en escritorio
- ✅ Entrada en Menú Inicio
- ✅ Aplicación se ejecuta
- ✅ Datos guardados en `C:\ProgramData\ControlPacientes\data\`
- ✅ Desinstalación limpia

## ❌ Si Algo Falla

### Error: "light.exe exited with 298"
- **Solución**: Ya está arreglado en el código
- **Acción**: Hacer commit y push nuevamente

### Error: "jpackage not found"
- **Causa**: Java < 14
- **Solución**: Instalar Java 19+ desde https://adoptium.net

### Error: "WiX Toolset not found"
- **Causa**: No está instalado
- **Solución**: `choco install wixtoolset`

### El MSI no se genera
- **Verificar**: Maven compiló correctamente
- **Ejecutar**: `mvn clean package -DskipTests`
- **Ver logs**: `job-logs-10.txt`

## 📊 Archivos Generados

```
target/
├── classes/                    # Clases compiladas
├── *.jar                       # JAR ejecutable
├── output/
│   └── ControlPacientes-1.0.0.msi  ✅ INSTALADOR
└── installer/
    └── ControlPacientes-1.0.0.msi  ✅ COPIA
```

## 🎯 Resultado Final

Si todo es correcto, tendrás un archivo MSI que:

1. Se instala en `C:\Program Files\ControlPacientes\`
2. Crea acceso directo en escritorio
3. Crea entrada en Menú Inicio
4. Se registra en "Programas y características"
5. Se puede desinstalar completamente

## 📞 Comandos Útiles

```powershell
# Ver todas las compilaciones previas
Get-ChildItem target\*.msi -Recurse

# Limpiar antes de compilar
mvn clean

# Compilar sin ejecutar tests
mvn package -DskipTests

# Ejecutar con logs detallados
jpackage --verbose ...

# Desinstalar MSI desde línea de comandos
msiexec /x {ProductCode}
```

## 🔗 Links Útiles

- [GitHub Actions Status](../../actions)
- [Descargar Artefactos](../../actions)
- [Issues & Errores](../../issues)
- [Releases](../../releases)

---

**¿Necesitas ayuda?** → Ver `INSTALLER_FIX.md` para solución detallada de problemas
