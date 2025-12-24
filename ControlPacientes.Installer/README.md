# Instalador de Control de Pacientes

## Descripción
Este proyecto contiene la configuración para generar el instalador Windows (.exe) de la aplicación Control de Pacientes.

## Herramientas Utilizadas
- **WiX Toolset 3.11** (o superior) - Para crear instaladores MSI
- **Heat.exe** - Para generar características y archivos
- **Candle.exe** - Para compilar
- **Light.exe** - Para enlazar

## Instrucciones de Instalación

### Paso 1: Instalar WiX Toolset
1. Descargar WiX Toolset desde: https://github.com/wixtoolset/wix3/releases
2. Ejecutar el instalador
3. Completar la instalación

### Paso 2: Preparar los Archivos
1. Publicar la aplicación en Release:
   ```bash
   dotnet publish -c Release -r win-x64 --self-contained
   ```

2. Copiar los archivos publicados a la carpeta `Instalador\Files`

### Paso 3: Crear el Instalador
```bash
cd ControlPacientes.Installer

# Compilar WiX
candle.exe Product.wxs

# Enlazar
light.exe -out ControlPacientes-Setup.exe Product.wixobj
```

### Paso 4: Distribuir
El archivo `ControlPacientes-Setup.exe` está listo para distribuir.

## Características del Instalador
- ✅ Instalación en directorio personalizado
- ✅ Atajos en el menú Inicio
- ✅ Desinstalación completa
- ✅ Verificación de permisos de administrador
- ✅ .NET 8.0 Runtime (si es necesario)

## Configuración
Editar `Product.wxs` para personalizar:
- Nombre de la aplicación
- Versión
- Fabricante
- Directorio de instalación

## Licencia
Propiedad exclusiva - Todos los derechos reservados
