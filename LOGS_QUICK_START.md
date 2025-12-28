# 🚀 GUÍA RÁPIDA - Usar Logs del Instalador

## 3 Formas de Usar Logs

### ✅ Forma 1: Con Monitoreo en Tiempo Real (RECOMENDADO)

**Paso 1:** Abre DOS ventanas PowerShell

**Ventana 1 - Ejecutar compilación:**
```powershell
.\build-windows-installer-with-logs.ps1
```

**Ventana 2 - Monitorear progreso:**
```powershell
.\monitor-build.ps1
```

**Resultado:**
- Ves los pasos en tiempo real
- Colores para fácil lectura
- Sabes exactamente dónde estás en el proceso

---

### ✅ Forma 2: Compilar y Luego Revisar Logs

**Paso 1:** Ejecutar compilación
```powershell
.\build-windows-installer-with-logs.ps1
```

**Paso 2:** Cuando termina, analizar logs
```powershell
# Ver estadísticas y resumen
.\analyze-build-logs.ps1 -Latest

# Ver resumen detallado
.\analyze-build-logs.ps1 -Latest -Summary

# Ver solo errores
.\analyze-build-logs.ps1 -Latest -FilterLevel Error
```

---

### ✅ Forma 3: Ver Logs Directamente

```powershell
# Abrir en Notepad
notepad build-logs\build-installer-*.log

# O ver en PowerShell
Get-Content build-logs\build-installer-*.log

# O buscar errores específicos
Select-String "ERROR" build-logs\build-installer-*.log
```

---

## 📊 Interpretar Resultados Rápidamente

### ✅ Si ves esto = ÉXITO

```
[SUCCESS] Proceso completado exitosamente!
[SUCCESS] • ControlPacientes-1.0.0.msi (150.25 MB)
Estado General: ✅ EXITOSO
```

**Acción:** Valida el instalador
```powershell
.\validate-installer.ps1
```

---

### ❌ Si ves esto = ERROR

```
[ERROR] Maven compilación falló
[ERROR] Java no está instalado
[ERROR] JAR no encontrado
Estado General: ❌ FALLÓ
```

**Acción:** Busca el error en el log
```powershell
Select-String "ERROR" build-logs\build-installer-*.log
```

---

## 🎯 Comandos Más Comunes

### Compilar y generar log
```powershell
.\build-windows-installer-with-logs.ps1
```

### Ver el log más reciente
```powershell
.\analyze-build-logs.ps1 -Latest
```

### Ver resumen completo
```powershell
.\analyze-build-logs.ps1 -Latest -Summary
```

### Monitorear en tiempo real
```powershell
.\monitor-build.ps1
```

### Ver solo errores
```powershell
Select-String "ERROR" build-logs\build-installer-*.log | Select-Object Line
```

### Ver lista de logs disponibles
```powershell
Get-ChildItem build-logs -Filter "*.log" | Select-Object Name, LastWriteTime
```

---

## 📁 Dónde Están los Logs

```
Proyecto/
├── build-logs/                    ← AQUÍ
│   ├── build-installer-20251227_143022.log
│   ├── build-installer-20251227_140515.log
│   └── build-installer-20251227_135840.log
├── build-windows-installer-with-logs.ps1
├── analyze-build-logs.ps1
└── monitor-build.ps1
```

---

## 🎨 Entender los Colores en el Log

```
🟢 ✅ [SUCCESS]  = Algo funcionó bien (verde)
🔴 ❌ [ERROR]    = Algo falló (rojo)
🟡 ⚠️  [WARNING]  = Advertencia, pero continúa (amarillo)
🔵 ℹ️  [INFO]     = Información general (azul)
🔷 🔍 [DEBUG]    = Detalles técnicos (cian)
```

---

## 💡 Ejemplos Prácticos

### Ejemplo 1: Compilación Exitosa Rápida

```powershell
# Paso 1: Compilar
.\build-windows-installer-with-logs.ps1 -SkipMavenBuild

# Resultado: Termina en ~3 minutos
# Log muestra: ✅ EXITOSO
```

### Ejemplo 2: Encontrar Error en Maven

```powershell
# Paso 1: Compilar (falla)
.\build-windows-installer-with-logs.ps1

# Paso 2: Buscar error
Select-String "ERROR" build-logs\build-installer-*.log

# Paso 3: Leer el error y solucionar
# Ej: Error = "Cannot find symbol"
# Solución = Revisar código Java
```

### Ejemplo 3: Monitorear Compilación Larga

```powershell
# Abre 2 PowerShells

# PowerShell 1:
.\build-windows-installer-with-logs.ps1

# PowerShell 2:
.\monitor-build.ps1

# Resultado: Ves progreso en tiempo real
```

---

## 🔍 Solucionar Problemas Comunes

### Problema: "No se genera el MSI"

```powershell
# 1. Revisar log
.\analyze-build-logs.ps1 -Latest -Summary

# 2. Buscar PASO 6 y 7
Select-String "PASO 6:|PASO 7:" build-logs\build-installer-*.log

# 3. Ver todos los errores
Select-String "ERROR" build-logs\build-installer-*.log
```

### Problema: "Maven no compila"

```powershell
# 1. Ver qué pasó en PASO 2
Select-String "PASO 2:" -A 50 build-logs\build-installer-*.log

# 2. Buscar "ERROR" o "FAILED"
Select-String "ERROR|FAILED" build-logs\build-installer-*.log

# 3. Solucionar código Java
# 4. Recompilar
.\build-windows-installer-with-logs.ps1
```

### Problema: "WiX da error"

```powershell
# 1. Buscar errores WiX
Select-String "LGHT|WiX|light.exe" build-logs\build-installer-*.log

# 2. Si ves LGHT0298 = Ya está corregido
# 3. Recompila con código actualizado
.\build-windows-installer-with-logs.ps1
```

---

## ⏱️ Tiempo Esperado

| Paso | Tiempo |
|------|--------|
| Maven Compilación | 5-10 seg |
| jpackage | 5-10 seg |
| WiX | 3-5 seg |
| **Total Aprox** | **15-30 seg** |

---

## 📊 Estadísticas en Log

El análisis te muestra:

```
✅ SUCCESS: 15    (15 pasos completados)
❌ ERROR: 0      (0 errores)
⚠️  WARNING: 2    (2 advertencias)
ℹ️  INFO: 8      (8 mensajes info)
🔍 DEBUG: 42     (42 detalles técnicos)

Estado General: ✅ EXITOSO
```

---

## 🎯 Quick Start (2 minutos)

```powershell
# 1. Compilar con logs (en PowerShell 1)
.\build-windows-installer-with-logs.ps1

# 2. Monitorear (en PowerShell 2) - OPCIONAL
.\monitor-build.ps1

# 3. Esperar a que termine...

# 4. Ver resultado
.\analyze-build-logs.ps1 -Latest

# Si es exitoso:
.\validate-installer.ps1

# Listo! Tu instalador está generado
```

---

## 📚 Para Más Información

- **Detallado:** Lee [BUILD_LOGS_GUIDE.md](BUILD_LOGS_GUIDE.md)
- **Problemas:** Lee [INSTALLER_FIX.md](INSTALLER_FIX.md)
- **Validar:** Ejecuta [validate-installer.ps1](validate-installer.ps1)

---

**¡Ahora tienes visibilidad completa del proceso de compilación!** 🎉
