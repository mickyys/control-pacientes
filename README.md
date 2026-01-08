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
- ✅ **Hot Reload**: Cambios en FXML y CSS se reflejan automáticamente sin reiniciar.

## Ejecución

### Requisitos
- Java 21 o superior instalado.
- Maven instalado (`brew install maven` en macOS).

### Pasos para iniciar

#### Modo Desarrollo (con Hot Reload)
Usa este modo durante el desarrollo para ver cambios en FXML y CSS en tiempo real:

```bash
./dev.sh
```

O manualmente:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

**Hot Reload disponible para:**
- `src/main/resources/fxml/*.fxml`
- `src/main/resources/css/style.css`

Los cambios se reflejan automáticamente en la interfaz. Para más detalles, ver [HOT_RELOAD_GUIDE.md](HOT_RELOAD_GUIDE.md)

#### Modo Producción
```bash
./prod.sh
```

O manualmente:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---
*Desarrollado como una alternativa multiplataforma al proyecto original en C#.*
