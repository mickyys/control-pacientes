# Sistema de Administración de Usuarios - Documentación

## Descripción General
Se ha implementado un sistema completo de gestión de usuarios con almacenamiento en base de datos SQLite, interfaz gráfica y funcionalidades de CRUD completo con cambio de contraseña.

## Cambios Realizados

### 1. Base de Datos
- **Entidad JPA (Usuario.java)**: Convertida a entidad persistente con anotaciones JPA
  - Campo `id` (clave primaria, auto-generada)
  - Campo `rut` (único, no nulo)
  - Campo `clave` (contraseña encriptada)
  - Campo `nombre`
  - Campo `activo` (estado del usuario)
  - Campos `fechaCreacion` y `fechaUltimaModificacion` (auditoría)

### 2. Repositorio
- **UsuarioRepository.java**: Actualizado a JpaRepository
  - `findByRut(String rut)`: Buscar usuario por RUT
  - `findByActivo(boolean activo)`: Obtener usuarios activos/inactivos
  - Hereda todas las operaciones CRUD de JpaRepository

### 3. Servicios
- **UsuarioService.java**: Servicio de lógica de negocio
  - `crear()`: Crear nuevo usuario con contraseña encriptada
  - `actualizar()`: Actualizar información del usuario
  - `cambiarContrasena()`: Cambiar contraseña validando la actual
  - `reiniciarContrasena()`: Reiniciar contraseña (para administradores)
  - `desactivar() / activar()`: Toggle de estado
  - `eliminar()`: Eliminar usuario
  - `obtenerTodos() / obtenerActivos() / obtenerPorId() / obtenerPorRut()`

- **AutenticacionService.java**: Actualizado para usar encriptación BCrypt
  - Valida credenciales con contraseña encriptada

### 4. Controladores UI
- **UsuariosListController.java**: 
  - Tabla con lista de usuarios
  - Búsqueda por RUT o nombre
  - Botones de acciones: Editar, Cambiar Contraseña, Activar/Desactivar
  - Carga dinámica de datos
  - Dialogo para cambiar contraseña

- **UsuarioFormController.java**:
  - Formulario para crear/editar usuarios
  - Validación de campos
  - Manejo dual (creación vs edición)
  - Formateo automático de RUT

### 5. Interfaz Gráfica
- **usuarios-list.fxml**: Panel de administración de usuarios
  - TableView con columnas: ID, RUT, Nombre, Estado, Fecha Creación, Acciones
  - Buscador en tiempo real
  - Botones para crear nuevo usuario y actualizar lista
  - Contador de usuarios

- **usuario-form.fxml**: Formulario de usuario
  - Campos: RUT, Nombre, Contraseña, Confirmar Contraseña, Estado
  - Botones: Guardar, Cancelar

- **main.fxml**: Menú principal actualizado
  - Nuevo menú "Administración"
  - Opción "Administrar Usuarios" que abre la ventana de gestión

### 6. Configuración
- **SecurityConfig.java**: Configuración de Spring Security
  - Bean de BCryptPasswordEncoder para encriptación de contraseñas

- **DataInitializer.java**: Inicializador de datos
  - Crea usuarios de demostración si la base de datos está vacía
  - Usuario Admin: RUT 12345678-9, Clave: admin
  - Usuario Doctor: RUT 87654321-0, Clave: doctor

### 7. Dependencias Agregadas
- Spring Security (spring-boot-starter-security) para encriptación BCrypt

## Características Implementadas

### CRUD Completo
- ✅ **Create**: Crear nuevos usuarios con validación
- ✅ **Read**: Listar usuarios con búsqueda
- ✅ **Update**: Editar información del usuario
- ✅ **Delete**: Eliminar usuarios

### Seguridad
- ✅ Contraseñas encriptadas con BCrypt
- ✅ Validación de contraseña actual al cambiar
- ✅ Minimo de 4 caracteres para contraseñas
- ✅ Confirmación de contraseña en formularios

### Gestión de Estado
- ✅ Activar/desactivar usuarios
- ✅ Estado visible en la tabla
- ✅ Solo usuarios activos pueden autenticarse

### Búsqueda y Filtrado
- ✅ Búsqueda en tiempo real por RUT o nombre
- ✅ Mostrador de cantidad total de usuarios

### Auditoría
- ✅ Fecha de creación registrada automáticamente
- ✅ Fecha de última modificación actualizada en cambios

## Uso

### Acceder a Administración de Usuarios
1. En el menú principal, ir a "Administración" → "Administrar Usuarios"
2. Se abrirá una ventana modal con la lista de usuarios

### Crear Nuevo Usuario
1. Clic en botón "+ Nuevo Usuario"
2. Completar formulario (RUT, Nombre, Contraseña)
3. Clic en "Guardar"

### Editar Usuario
1. En la tabla, clic en botón "Editar"
2. Modificar campos deseados
3. Si desea cambiar contraseña, complétela en los campos disponibles
4. Clic en "Guardar"

### Cambiar Contraseña
1. En la tabla, clic en botón "Cambiar Clave"
2. Ingrese contraseña actual
3. Ingrese nueva contraseña
4. Confirme nueva contraseña
5. Clic en "OK"

### Desactivar/Activar Usuario
1. En la tabla, clic en botón "Desactivar" (o "Activar" si está inactivo)
2. Confirmar en el dialogo
3. El usuario se desactivará/activará

## Base de Datos

### Tabla: usuarios
```
- id (INTEGER, PRIMARY KEY, AUTO_INCREMENT)
- rut (VARCHAR(20), UNIQUE, NOT NULL)
- clave (VARCHAR(255), NOT NULL) - Encriptada con BCrypt
- nombre (VARCHAR(100), NOT NULL)
- activo (BOOLEAN, NOT NULL)
- fecha_creacion (DATETIME, NOT NULL)
- fecha_ultima_modificacion (DATETIME)
```

## Notas Técnicas
- Usa JPA/Hibernate para mapeo objeto-relacional
- SQLite como base de datos local
- Spring Security para encriptación de contraseñas
- FXMLLoader con ControllerFactory para inyección de dependencias
- Validaciones tanto en cliente como en servicio
