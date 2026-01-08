# Hot Reload JavaFX - Guía de Uso

## ¿Qué es Hot Reload?

Hot Reload permite que los cambios realizados en archivos FXML y CSS se reflejen **automáticamente** en la aplicación sin necesidad de reiniciar.

## Características

✅ **Monitoreo automático** de cambios en FXML y CSS  
✅ **Recarga en tiempo real** sin reiniciar la aplicación  
✅ **Debouncing** para evitar múltiples recargas en corto tiempo  
✅ **Solo en modo desarrollo** (deshabilitado en producción)  
✅ **Logging detallado** de cambios detectados  

## Cómo Usar

### 1. Iniciar la aplicación en modo desarrollo

```bash
mvn spring-boot:run
```

O especificar el perfil explícitamente:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### 2. Editar archivos FXML o CSS

Simplemente edita los archivos en:
- `src/main/resources/fxml/*.fxml`
- `src/main/resources/css/style.css`

Los cambios se reflejarán automáticamente en la interfaz gráfica en **menos de 1 segundo**.

### 3. Ver los cambios

Los logs mostrarán mensajes como:

```
INFO  HotReloadService - Detectado cambio en CSS
INFO  HotReloadService - CSS recargado exitosamente
```

o

```
INFO  HotReloadService - Detectado cambio en FXML: ficha_form.fxml
INFO  HotReloadService - FXML recargado: ficha_form.fxml
```

## Configuración

### Modo Desarrollo (por defecto)

El archivo `application-dev.properties` activa el hot reload:

```properties
spring.profiles.active=dev
app.hotreload.enabled=true
```

### Modo Producción

El archivo `application-prod.properties` desactiva el hot reload:

```properties
spring.profiles.active=prod
app.hotreload.enabled=false
```

Para ejecutar en modo producción:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## Archivos Afectados

- `src/main/java/com/controlpacientes/ui/HotReloadService.java` - Servicio de monitoreo
- `src/main/java/com/controlpacientes/JavaFXApplication.java` - Integración en la aplicación
- `src/main/resources/application-dev.properties` - Configuración de desarrollo
- `src/main/resources/application-prod.properties` - Configuración de producción

## Limitaciones

⚠️ **Cambios en Java** - Requiere recompilación y reinicio  
⚠️ **Cambios en Controllers** - Requiere reinicio de la aplicación  
⚠️ **Nueva controladores en FXML** - La primera vez que se carga el controlador requiere reinicio  

## Solución de Problemas

### No se detectan cambios en FXML/CSS

1. Verifica que el archivo se guardó correctamente
2. Revisa los logs de la aplicación para errores
3. Asegúrate de estar en modo desarrollo

### Error al recargar FXML

Si hay un error de sintaxis en el FXML, aparecerá en los logs y la interfaz no se actualizará. Corrige el error y guarda nuevamente.

### El CSS no se aplica

- Asegúrate de que los selectores CSS son correctos
- Verifica que no hay estilos en conflicto
- El CSS se recarga completamente, no incrementalmente

## Consejos de Desarrollo

💡 Usa el hot reload para:
- Ajustar colores y estilos en CSS
- Modificar layouts en FXML
- Cambiar textos y etiquetas
- Probar diferentes disposiciones de componentes

🔄 El flujo de desarrollo recomendado:
1. Realiza cambios en FXML/CSS
2. Visualiza el cambio inmediatamente en la app
3. Refina hasta que quede perfecto
4. Cuando cambies código Java, reinicia la aplicación
