package com.controlpacientes.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
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
    private Stage mainStage;

    /**
     * Establece la ventana principal para que los modales aparezcan sobre ella
     */
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public <T> void openModal(String fxmlPath, String title, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                String errorMsg = "No se encontró el archivo FXML: " + fxmlPath;
                log.error(errorMsg);
                showErrorAlert("Error al cargar interfaz", errorMsg);
                return;
            }

            // Establecer el ClassLoader explícitamente para evitar NullPointerException
            loader.setClassLoader(getClass().getClassLoader());
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
            
            // Establecer la ventana principal como propietaria si está disponible
            if (mainStage != null) {
                stage.initOwner(mainStage);
            }
            
            Scene scene = new Scene(root);
            // Cargar CSS dinámicamente - intenta emerald primero, luego style.css
            String cssResource = loadStylesheet();
            if (cssResource != null) {
                scene.getStylesheets().add(cssResource);
            }
            stage.setScene(scene);
            
            // Establecer tamaño cercano a pantalla completa sin usar setMaximized
            if (mainStage != null) {
                stage.setWidth(mainStage.getWidth() * 0.95);
                stage.setHeight(mainStage.getHeight() * 0.95);
                stage.setX(mainStage.getX() + (mainStage.getWidth() - stage.getWidth()) / 2);
                stage.setY(mainStage.getY() + (mainStage.getHeight() - stage.getHeight()) / 2);
            } else {
                stage.setWidth(1200);
                stage.setHeight(800);
            }
            
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

    public <T> Parent loadView(String fxmlPath, Consumer<T> controllerConsumer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                String errorMsg = "No se encontró el archivo FXML: " + fxmlPath;
                log.error(errorMsg);
                showErrorAlert("Error al cargar interfaz", errorMsg);
                return null;
            }

            // Establecer el ClassLoader explícitamente para evitar NullPointerException
            loader.setClassLoader(getClass().getClassLoader());
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();

            if (root == null) {
                String errorMsg = "No se pudo cargar el root del FXML: " + fxmlPath;
                log.error(errorMsg);
                showErrorAlert("Error al cargar interfaz", errorMsg);
                return null;
            }

            T controller = loader.getController();
            if (controller == null) {
                String errorMsg = "No se pudo obtener el controlador del FXML: " + fxmlPath;
                log.error(errorMsg);
                showErrorAlert("Error al cargar interfaz", errorMsg);
                return null;
            }

            if (controllerConsumer != null) {
                controllerConsumer.accept(controller);
            }

            return root;
        } catch (IOException e) {
            String errorMsg = "Error al cargar la vista: " + fxmlPath + "\n" + e.getMessage();
            log.error(errorMsg, e);
            showErrorAlert("Error al cargar interfaz", errorMsg);
            return null;
        } catch (Exception e) {
            String errorMsg = "Error inesperado al cargar vista: " + e.getMessage();
            log.error(errorMsg, e);
            showErrorAlert("Error inesperado", errorMsg);
            return null;
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String loadStylesheet() {
        // Intentar cargar style.css primero (tema por defecto)
        try {
            var cssUrl = getClass().getResource("/css/style.css");
            if (cssUrl != null) {
                return cssUrl.toExternalForm();
            }
        } catch (Exception e) {
            log.debug("No se encontró style.css: {}", e.getMessage());
        }

        // Si no existe standard, cargar style-emerald.css (tema esmeralda)
        try {
            var emeraldCssUrl = getClass().getResource("/css/style-emerald.css");
            if (emeraldCssUrl != null) {
                return emeraldCssUrl.toExternalForm();
            }
        } catch (Exception e) {
            log.debug("No se encontró style-emerald.css: {}", e.getMessage());
        }

        // Si no se encuentra ninguno, mostrar warning pero continuar
        log.warn("No se encontró ningún archivo CSS. Continuando sin estilos.");
        return null;
    }
}
