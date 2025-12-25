package com.controlpacientes.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class UINavigator {

    private final ApplicationContext applicationContext;

    public <T> void openModal(String fxmlPath, String title, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                String errorMsg = "No se encontró el archivo FXML: " + fxmlPath;
                log.error(errorMsg);
                showErrorAlert("Error al cargar interfaz", errorMsg);
                return;
            }

            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            if (root == null) {
                String errorMsg = "No se pudo cargar el root del FXML: " + fxmlPath;
                log.error(errorMsg);
                showErrorAlert("Error al cargar interfaz", errorMsg);
                return;
            }

            T controller = loader.getController();
            if (controller == null) {
                String errorMsg = "No se pudo obtener el controlador del FXML: " + fxmlPath;
                log.error(errorMsg);
                showErrorAlert("Error al cargar interfaz", errorMsg);
                return;
            }

            if (controllerConsumer != null) {
                controllerConsumer.accept(controller);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Scene scene = new Scene(root);
            try {
                scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            } catch (Exception e) {
                log.warn("No se pudo cargar el CSS: {}", e.getMessage());
            }
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            String errorMsg = "Error al cargar el modal: " + fxmlPath + "\n" + e.getMessage();
            log.error(errorMsg, e);
            showErrorAlert("Error al cargar interfaz", errorMsg);
        } catch (Exception e) {
            String errorMsg = "Error inesperado al abrir modal: " + e.getMessage();
            log.error(errorMsg, e);
            showErrorAlert("Error inesperado", errorMsg);
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
