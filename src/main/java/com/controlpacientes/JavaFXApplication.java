package com.controlpacientes;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFXApplication extends Application {

    private ConfigurableApplicationContext springContext;

    @Override
    public void init() throws Exception {
        // Configurar propiedades del sistema para macOS - necesario para evitar errores de headless mode
        // al usar funciones de impresión en JavaFX
        System.setProperty("apple.awt.application.name", "Control Pacientes");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Control Pacientes");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        
        springContext = new SpringApplicationBuilder(ControlPacientesApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Obtener UINavigator y establecer la ventana principal
        var uiNavigator = springContext.getBean(com.controlpacientes.ui.UINavigator.class);
        uiNavigator.setMainStage(primaryStage);
        
        // Cargar pantalla de login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root);
        // Cargar CSS dinámicamente - intenta emerald primero, luego style.css
        String cssResource = loadStylesheet();
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource);
        }

        primaryStage.setTitle("Control de Pacientes - Login");
        // Configurar icono de la aplicación
        try {
            Image iconImage = new Image(getClass().getResource("/images/icono.png").toExternalForm());
            primaryStage.getIcons().add(iconImage);
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + e.getMessage());
        }
        
        primaryStage.setScene(scene);
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        // Iniciar Hot Reload en modo desarrollo
        if (isDevMode()) {
            var hotReloadService = springContext.getBean(com.controlpacientes.ui.HotReloadService.class);
            hotReloadService.startWatching(scene, uiNavigator);
        }
    }
    
    private boolean isDevMode() {
        String profiles = System.getProperty("spring.profiles.active");
        return profiles == null || !profiles.contains("prod");
    }

    private String loadStylesheet() {
        // Intentar cargar style.css primero (tema por defecto)
        try {
            var cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                System.out.println("✓ Cargando tema estándar: style.css");
                return cssUrl.toExternalForm();
            }
        } catch (Exception e) {
            System.err.println("No se encontró style.css: " + e.getMessage());
        }

        // Si no existe standard, cargar style-emerald.css (tema esmeralda)
        try {
            var emeraldCssUrl = getClass().getResource("/css/style-emerald.css");
            if (emeraldCssUrl != null) {
                System.out.println("✓ Cargando tema esmeralda: style-emerald.css");
                return emeraldCssUrl.toExternalForm();
            }
        } catch (Exception e) {
            System.err.println("No se encontró style-emerald.css: " + e.getMessage());
        }

        // Si no se encuentra ninguno, mostrar error pero continuar
        System.err.println("⚠ No se encontró ningún archivo CSS. Continuando sin estilos.");
        return null;
    }

    @Override
    public void stop() throws Exception {
        // Detener Hot Reload Service si está activo
        try {
            var hotReloadService = springContext.getBean(com.controlpacientes.ui.HotReloadService.class);
            hotReloadService.stop();
        } catch (Exception e) {
            System.err.println("Error deteniendo HotReload: " + e.getMessage());
        }
        
        springContext.close();
        Platform.exit();
    }
}
