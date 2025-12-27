# Actualizaciones y Preservación de Datos

## Cómo funcionan las actualizaciones en ControlPacientes

Cuando instalas una nueva versión del MSI, la aplicación **preservará automáticamente** toda tu información de la base de datos. No perderás ningún dato durante la actualización.

### Ubicación de los datos

Los datos de la base de datos se guardan en una carpeta compartida de Windows, **separada de la aplicación**:

```
C:\ProgramData\ControlPacientes\data\
```

Esto significa que:
- ✅ Los datos persisten entre actualizaciones
- ✅ Los datos persisten incluso si desinstala la aplicación (a menos que los borre manualmente)
- ✅ Múltiples usuarios en el mismo PC comparten la misma base de datos
- ✅ Los datos son accesibles desde otras aplicaciones si es necesario

### Estructura de carpetas

```
Instalación de la aplicación:
C:\Program Files\ControlPacientes\
├── ControlPacientes\
│   ├── bin\
│   │   └── ControlPacientes.exe
│   ├── lib\
│   └── runtime\
└── ...

Datos (preservados):
C:\ProgramData\ControlPacientes\
├── data\
│   └── [base de datos, configuraciones, etc.]
└── logs\
    └── [archivos de log]
```

### Actualizar la aplicación

1. **Descarga** el nuevo MSI desde GitHub (Releases)
2. **Ejecuta** el MSI como antes
3. El instalador detectará la versión anterior y ofrecerá actualizar
4. **Haz clic en "Instalar"** para actualizar
5. **Listo** - Tus datos están intactos

### Hacer backup de datos

Si deseas hacer una copia de seguridad de tu base de datos:

1. Copia la carpeta: `C:\ProgramData\ControlPacientes\data\`
2. Pégala en un USB o servicio en la nube
3. Para restaurar, pega el contenido nuevamente en esa carpeta

### Reinstalar completamente (perder datos)

Si necesitas hacer una instalación completamente limpia:

1. **Desinstala** la aplicación desde Panel de Control
2. **Elimina manualmente** la carpeta: `C:\ProgramData\ControlPacientes\`
3. **Instala** el nuevo MSI
4. Tendrás una base de datos completamente nueva

### Cambiar ubicación de datos

Si necesitas guardar los datos en otro lugar (ej: Dropbox, OneDrive):

1. Instala la aplicación normalmente
2. Copia toda la carpeta `C:\ProgramData\ControlPacientes\data\` a tu ubicación deseada
3. Crea un enlace simbólico (o cambia manualmente la ruta en la configuración)

### Solucionar problemas de actualización

**Problema**: La actualización falla
- Solución: Asegúrate de que no hay ninguna instancia de ControlPacientes en ejecución. Cierra la aplicación completamente.

**Problema**: Después de actualizar, la app no inicia
- Solución: Ejecuta el script `debug-app.bat` para ver qué error ocurre
- Probablemente necesites una versión de Java más reciente

**Problema**: Los datos no se actualizaron correctamente
- Solución: Los datos están en `C:\ProgramData\ControlPacientes\data\`. Verifica que no están corruptos. Si es necesario, restaura desde tu backup.

### Migración a otra computadora

Para mover la aplicación a otro PC:

1. En el PC actual: Copia `C:\ProgramData\ControlPacientes\data\` a USB
2. En el nuevo PC: Instala ControlPacientes normalmente
3. Reemplaza `C:\ProgramData\ControlPacientes\data\` con los datos del USB
4. ¡Listo! Todos tus datos estarán disponibles
