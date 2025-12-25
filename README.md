# Control de Pacientes (Java Desktop Version)

## Descripción
Esta es una versión en Java del sistema "Control de Pacientes", diseñada como una **aplicación de escritorio** profesional utilizando **JavaFX** y **Spring Boot 3**.

## Tecnologías
- **Java 21**: Lenguaje principal.
- **JavaFX 21**: Framework para la interfaz de usuario de escritorio (equivalente a WPF).
- **Spring Boot 3.2**: Para inyección de dependencias y gestión del ciclo de vida.
- **Spring Data JPA / Hibernate**: Acceso a datos.
- **SQLite**: Base de datos local persistente.
- **Lombok**: Para un código más limpio.

## Estructura del Proyecto
- `src/main/resources/fxml`: Definiciones de la interfaz (XML).
- `src/main/resources/css`: Estilos premium para la aplicación.
- `src/main/java/com/controlpacientes/ui`: Controladores y utilidades de navegación.
- `src/main/java/com/controlpacientes/model`: Entidades de datos.
- `src/main/java/com/controlpacientes/service`: Lógica de negocio y validaciones.

## Características
- ✅ **Interfaz Premium**: Diseño inspirado en aplicaciones modernas con degradados y bordes redondeados.
- ✅ **Gestión de Pacientes**: CRUD completo con ventanas modales.
- ✅ **Validación de RUT**: Lógica de validación chilena integrada.
- ✅ **Persistencia Local**: Base de datos SQLite que funciona sin configuración adicional.

## Ejecución

### Requisitos
- Java 21 o superior instalado.
- Maven instalado (`brew install maven` en macOS).

### Pasos para iniciar
1. Abrir una terminal en la carpeta del proyecto:
   ```bash
   cd control-pacientes-java
   ```
2. Compilar y ejecutar la aplicación:
   ```bash
   mvn javafx:run
   ```

---
*Desarrollado como una alternativa multiplataforma al proyecto original en C#.*
