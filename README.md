# Control de Pacientes

## Descripción
Sistema de gestión de pacientes para consultorios médicos, desarrollado en C# con WPF. Funciona completamente offline con base de datos local SQLite.

## Características

### ✅ Funcionalidades Principales
- **Gestión de Pacientes**
  - Crear, editar y eliminar pacientes
  - Búsqueda por RUT, nombre y ciudad
  - Validación automática de RUT chileno
  - Almacenamiento de datos de contacto y dirección
  - Registro de notas clínicas (alergias, condiciones especiales)

- **Fichas Médicas**
  - Crear fichas para cada atención
  - Registrar diagnóstico y tratamiento
  - Asociar medicamentos a cada atención
  - Historial completo de atenciones por paciente

- **Medicamentos**
  - Agregar medicamentos a fichas médicas
  - Especificar dosis, frecuencia y duración
  - Registrar indicaciones especiales

### 🏗️ Arquitectura en Capas
```
ControlPacientes.UI          (Presentación - WPF)
    ↓
ControlPacientes.Business    (Lógica de Negocio)
    ↓
ControlPacientes.Data        (Acceso a Datos)
    ↓
ControlPacientes.Core        (Modelos)
```

### 🔒 Características de Seguridad
- Base de datos local (sin conexión a internet requerida)
- Validación de datos en múltiples niveles
- Manejo de excepciones personalizado
- Acceso estructurado a través de repositorios

### 💾 Tecnologías Utilizadas
- **Framework**: .NET 8.0 / C#
- **UI**: WPF (Windows Presentation Foundation)
- **Base de Datos**: SQLite con Entity Framework Core 8.0
- **Arquitectura**: Repository Pattern, Dependency Injection
- **Buenas Prácticas**: SOLID, Clean Code

## Instalación

### Requisitos Previos
- Windows 7 o superior
- .NET 8.0 Runtime (incluido en el instalador)

### Pasos de Instalación
1. Descargar el instalador `ControlPacientes-Setup.exe`
2. Ejecutar el instalador
3. Seguir los pasos del asistente
4. Completar la instalación

## Uso

### Primera Ejecución
1. Abrir "Control de Pacientes" desde el menú Inicio
2. La base de datos se crea automáticamente

### Gestionar Pacientes
1. Hacer clic en "Gestionar Pacientes" en la pantalla principal
2. Usar el botón "Nuevo Paciente" para crear uno nuevo
3. Completar el formulario con los datos requeridos
4. Buscar pacientes usando el cuadro de búsqueda o la ciudad

### Crear Fichas Médicas
1. Ir al menú de Fichas Médicas
2. Seleccionar un paciente
3. Crear una nueva ficha con la información de la atención
4. Agregar medicamentos según sea necesario

## Estructura de Carpetas
```
ControlPacientes/
├── ControlPacientes.UI/           # Interfaz de usuario
├── ControlPacientes.Business/     # Lógica de negocio
├── ControlPacientes.Data/         # Acceso a datos
├── ControlPacientes.Core/         # Modelos y entidades
├── ControlPacientes.Installer/    # Configuración del instalador
└── ControlPacientes.sln           # Solución Visual Studio
```

## Base de Datos

### Tablas Principales
- **Pacientes**: Información demográfica y de contacto
- **FichasMedicas**: Registros de atenciones médicas
- **MedicamentosAtencion**: Medicamentos asociados a cada ficha

### Ubicación
La base de datos SQLite se almacena en:
```
%APPDATA%\ControlPacientes\ControlPacientes.db
```

## Desarrollo

### Compilar desde Código Fuente
```bash
# Restaurar dependencias
dotnet restore

# Compilar solución
dotnet build

# Ejecutar la aplicación
dotnet run --project ControlPacientes.UI
```

### Crear Instalador (Windows)
```bash
# Publicar aplicación
dotnet publish -c Release -r win-x64 --self-contained

# Usar WiX Toolset para crear el MSI
# O usar herramienta equivalente para generar el .exe
```

## Validaciones Incluidas
- ✅ RUT válido con dígito verificador
- ✅ Formato de email correcto
- ✅ Fechas dentro de rango válido
- ✅ Campos obligatorios
- ✅ No permitir duplicados de RUT

## Mantenimiento

### Copias de Seguridad
Recomendamos hacer copias periódicas del archivo `ControlPacientes.db`

### Actualizaciones
El sistema notificará cuando haya actualizaciones disponibles

## Solución de Problemas

### La aplicación no inicia
- Verificar que .NET 8.0 esté instalado
- Ejecutar como administrador
- Revisar que la carpeta de datos sea accesible

### Base de datos corrupta
- Eliminar el archivo `ControlPacientes.db`
- Reiniciar la aplicación (creará una nueva base de datos)

## Soporte
Para reportar errores o solicitar funcionalidades, contacte al equipo de desarrollo.

## Licencia
Propiedad exclusiva de [Tu Organización]

## Versión
v1.0.0 - Diciembre 2025
