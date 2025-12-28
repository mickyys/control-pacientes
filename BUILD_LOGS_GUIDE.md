# 📋 Guía de Logs del Instalador

## 🎯 Propósito

Los archivos de log registran detalladamente **cada paso** de la compilación del instalador, permitiendo:

✅ Diagnosticar problemas rápidamente  
✅ Verificar qué pasos se completaron correctamente  
✅ Obtener información técnica detallada  
✅ Auditar el proceso de compilación  

---

## 📁 Ubicación de Logs

Los archivos de log se guardan en:

```
build-logs/
├── build-installer-20251227_143022.log
├── build-installer-20251227_140515.log
└── build-installer-20251227_135840.log
```

**Nombre del archivo:** `build-installer-YYYYMMDD_HHMMSS.log`

---

## 🚀 Cómo Generar Logs

### Opción 1: Script Completo (Recomendado)

```powershell
# Compila y genera log automáticamente
.\build-windows-installer-with-logs.ps1

# Con versión personalizada
.\build-windows-installer-with-logs.ps1 -Version "1.0.1"

# Saltando Maven
.\build-windows-installer-with-logs.ps1 -SkipMavenBuild
```

### Opción 2: Generar log desde archivo existente

```powershell
# Ejecutar compilación anterior y capturar en log
.\build-windows-installer.ps1 | Out-File -Path "build-logs/manual-build.log" -Encoding UTF8
```

---

## 📊 Analizar Logs

### Opción 1: Análisis Interactivo (Más Fácil)

```powershell
# Mostrar archivos disponibles y seleccionar
.\analyze-build-logs.ps1

# Usar el archivo más reciente automáticamente
.\analyze-build-logs.ps1 -Latest

# Ver resumen detallado
.\analyze-build-logs.ps1 -Latest -Summary
```

### Opción 2: Ver Log Directamente

```powershell
# Mostrar el contenido completo
notepad build-logs/build-installer-*.log

# O en PowerShell
Get-Content build-logs/build-installer-*.log

# Filtrar solo errores
Select-String "ERROR" build-logs/build-installer-*.log
```

---

## 📝 Estructura del Log

Cada línea tiene el siguiente formato:

```
[YYYY-MM-DD HH:MM:SS.fff] [LEVEL] Mensaje
```

### Ejemplo:

```
[2025-12-27 14:30:22.123] [INFO] ========================================
[2025-12-27 14:30:22.125] [INFO] Control Pacientes - Windows Builder
[2025-12-27 14:30:22.126] [DEBUG] Versión: 1.0.0
[2025-12-27 14:30:22.128] [SUCCESS] pom.xml (Configuración Maven) - ENCONTRADO
[2025-12-27 14:30:23.456] [INFO] Iniciando compilación: mvn clean package -DskipTests
[2025-12-27 14:30:50.789] [SUCCESS] Compilación Maven completada exitosamente
[2025-12-27 14:31:05.234] [ERROR] Error crítico: Icono no encontrado
```

---

## 🎨 Niveles de Log

### INFO (Azul)
Información general sobre el proceso
```
[INFO] PASO 1: Verificación de Archivos y Dependencias
```

### SUCCESS (Verde) ✅
Indicación de que una tarea se completó correctamente
```
[SUCCESS] pom.xml (Configuración Maven) - ENCONTRADO
[SUCCESS] Compilación Maven completada exitosamente
```

### ERROR (Rojo) ❌
Indica un error que detuvo el proceso
```
[ERROR] Java no está instalado o no está en el PATH
[ERROR] JAR no encontrado después de compilación
```

### WARNING (Amarillo) ⚠️
Advertencia que no detiene el proceso
```
[WARNING] Saltando compilación de Maven (--SkipMavenBuild)
```

### DEBUG (Cian) 🔍
Información técnica detallada
```
[DEBUG] Versión: 1.0.0
[DEBUG] Comando: mvn -v
[DEBUG] Tamaño: 150.45 MB
```

---

## 📋 Pasos Principales en el Log

El log divide el proceso en **9 pasos principales**:

```
PASO 1: Verificación de Archivos y Dependencias
  └─ Valida pom.xml, icono, Java, Maven

PASO 2: Compilación de Maven
  └─ Ejecuta: mvn clean package -DskipTests

PASO 3: Validación de Artefactos
  └─ Verifica que el JAR fue generado

PASO 4: Preparación de Directorios
  └─ Crea directorios necesarios

PASO 5: Copia de Recursos WiX
  └─ Copia archivos de configuración

PASO 6: Generación de Instalador MSI
  └─ Ejecuta jpackage para crear MSI

PASO 7: Validación de Archivos Generados
  └─ Verifica que el MSI se creó

PASO 8: Organización de Archivos
  └─ Mueve archivos a ubicaciones finales

PASO 9: Resumen Final
  └─ Muestra resultado y próximos pasos
```

---

## 🔍 Interpretar Resultados

### ✅ Compilación Exitosa

```
[SUCCESS] Proceso completado exitosamente!
[SUCCESS] • ControlPacientes-1.0.0.msi (150.25 MB)

Estado General: ✅ EXITOSO
```

**Qué significa:**
- El instalador MSI se generó correctamente
- Tiene un tamaño razonable (>50MB)
- Está listo para usar

### ❌ Error en Compilación

```
[ERROR] Maven compilación falló
[ERROR] Revisa los logs anteriores para más detalles

Estado General: ❌ FALLÓ
```

**Qué hacer:**
1. Lee el log completo para ver el error específico
2. Busca mensajes ERROR o WARNING en el log
3. Soluciona el problema y recompila

---

## 🎯 Buscar Problemas Específicos

### Problema: Maven no compila

**Buscar en el log:**
```powershell
Select-String "Maven" build-logs/build-installer-*.log
Select-String "ERROR" build-logs/build-installer-*.log
```

**Qué revisar:**
- ¿Maven está instalado?
- ¿Hay errores de compilación?
- ¿Están disponibles todas las dependencias?

### Problema: jpackage falla

**Buscar en el log:**
```powershell
Select-String "jpackage" build-logs/build-installer-*.log
Select-String "WiX" build-logs/build-installer-*.log
```

**Qué revisar:**
- ¿Java 14+ está instalado?
- ¿WiX Toolset está disponible?
- ¿El icono existe?

### Problema: MSI no se genera

**Buscar en el log:**
```powershell
Select-String "MSI" build-logs/build-installer-*.log
Select-String "output" build-logs/build-installer-*.log
```

**Qué revisar:**
- ¿Hay suficiente espacio en disco?
- ¿Permisos de escritura en target/?
- ¿jpackage completó sin errores?

---

## 📊 Ejemplos de Comandos Útiles

### Ver los últimos 50 líneas
```powershell
Get-Content build-logs/build-installer-*.log -Tail 50
```

### Ver solo líneas de ERROR
```powershell
Select-String "ERROR" build-logs/build-installer-*.log
```

### Ver solo líneas de SUCCESS
```powershell
Select-String "SUCCESS" build-logs/build-installer-*.log
```

### Contar eventos por tipo
```powershell
$log = Get-Content build-logs/build-installer-*.log
"SUCCESS: $($log | Select-String 'SUCCESS' | Measure-Object).Count"
"ERROR: $($log | Select-String 'ERROR' | Measure-Object).Count"
"WARNING: $($log | Select-String 'WARNING' | Measure-Object).Count"
```

### Buscar errores específicos
```powershell
Select-String "LGHT0298|WiX|jpackage" build-logs/build-installer-*.log
```

---

## 📈 Información Registrada

El log incluye:

### Pre-compilación
- ✅ Verificación de pom.xml
- ✅ Verificación de icono
- ✅ Verificación de Java
- ✅ Verificación de Maven

### Compilación
- ✅ Comando Maven ejecutado
- ✅ Salida de Maven
- ✅ Exit code de Maven
- ✅ Ubicación del JAR

### Generación MSI
- ✅ Parámetros de jpackage
- ✅ Salida de jpackage
- ✅ Errores de WiX (si los hay)
- ✅ Ubicación del MSI generado

### Post-compilación
- ✅ Validación de archivos
- ✅ Tamaño del MSI
- ✅ Fecha de creación
- ✅ Estado final

---

## 💾 Mantener Logs

### Limpiar logs antiguos
```powershell
# Eliminar logs más antiguos de 7 días
Get-ChildItem build-logs -Filter "build-installer-*.log" | Where-Object { $_.LastWriteTime -lt (Get-Date).AddDays(-7) } | Remove-Item
```

### Archivar logs
```powershell
# Crear un archivo ZIP con todos los logs
Compress-Archive -Path build-logs -DestinationPath "build-logs-archive-$(Get-Date -Format 'yyyyMMdd').zip"
```

---

## 🔗 Relacionado

- [QUICK_INSTALLER_GUIDE.md](QUICK_INSTALLER_GUIDE.md) - Guía rápida de compilación
- [validate-installer.ps1](validate-installer.ps1) - Validar resultado
- [INSTALLER_FIX.md](INSTALLER_FIX.md) - Solución de problemas

---

## ✨ Tips Útiles

**Tip 1:** Abre dos PowerShell
```powershell
# Ventana 1: Compila con logs
.\build-windows-installer-with-logs.ps1

# Ventana 2: Monitorea progreso
Get-Content build-logs/build-installer-*.log -Tail 20 -Wait
```

**Tip 2:** Exportar log a archivo de texto
```powershell
.\analyze-build-logs.ps1 -Latest -Summary | Out-File report.txt
```

**Tip 3:** Crear alias para comandos frecuentes
```powershell
Set-Alias logs ".\analyze-build-logs.ps1"
logs -Latest
```

---

## 📞 Resolver Problemas Comunes

### "No hay archivos de log"
**Causa:** No has compilado aún  
**Solución:** Ejecuta `.\build-windows-installer-with-logs.ps1`

### "El log es muy grande"
**Causa:** Incluye mucho debug  
**Solución:** Usa `Select-String` para filtrar

### "No entiendo el error"
**Causa:** El mensaje es técnico  
**Solución:** Busca en `INSTALLER_FIX.md`

---

**Ahora tienes visibilidad completa de cada paso de compilación.** 🎉
