# Solución de Problemas - ControlPacientes

## La aplicación no inicia después de instalar

### Opción 1: Ejecutar desde línea de comandos
Para ver mensajes de error detallados, abre una ventana de comandos (CMD) y ejecuta:

```cmd
cd "C:\Program Files\ControlPacientes\ControlPacientes\bin"
ControlPacientes.exe
```

Esto mostrará cualquier error en la consola.

### Opción 2: Ver logs de Windows
1. Abre **Visor de eventos** (presiona `Win + R`, escribe `eventvwr` y presiona Enter)
2. Ve a **Registros de Windows → Aplicación**
3. Busca errores recientes de "ControlPacientes"

### Opción 3: Verificar requisitos
- Asegúrate de tener **Java 19** instalado (aunque debería estar incluido)
- Verifica que tienes al menos **512 MB de RAM libres**
- Comprueba que tienes permisos de administrador en la carpeta de instalación

### Opción 4: Usar el script de diagnóstico
1. Descarga `debug-app.bat` desde el repositorio
2. Colócalo en el escritorio
3. Haz doble clic para ejecutarlo
4. Verá todos los errores detallados

### Problemas Comunes

**Error: "No se puede encontrar java.exe"**
- Solución: La aplicación incluye Java empaquetado, pero podría no inicializar correctamente. Reinstala desde el MSI.

**La ventana aparece y desaparece**
- Solución: Hay una excepción en la aplicación. Ejecuta desde CMD (Opción 1) para ver el error exacto.

**Error: "Cannot find symbol" o errores de compilación**
- Solución: El JAR está corrupto. Reinstala la aplicación.

**Puerta 3306 (MySQL) rechazada**
- Solución: Asegúrate de que MySQL está corriendo. La aplicación no puede conectarse a la base de datos.

### Reportar el Problema
Si ninguna solución funciona:
1. Abre CMD y ejecuta la Opción 1
2. Copia el mensaje de error exacto
3. Crea un issue en GitHub con:
   - El error completo
   - Tu versión de Windows
   - La salida de `java -version` (si tienes Java instalado)
