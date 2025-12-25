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
        springContext = new SpringApplicationBuilder(ControlPacientesApplication.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Obtener UINavigator y establecer la ventana principal
        var uiNavigator = springContext.getBean(com.controlpacientes.ui.UINavigator.class);
        uiNavigator.setMainStage(primaryStage);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

        primaryStage.setTitle("Control de Pacientes");
        // Configurar icono de la aplicación
        try {
            Image iconImage = new Image(getClass().getResource("/images/icono.png").toExternalForm());
            primaryStage.getIcons().add(iconImage);
        } catch (Exception e) {
            System.err.println("Error cargando icono: " + e.getMessage());
        }
        
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        springContext.close();
        Platform.exit();
    }
}
