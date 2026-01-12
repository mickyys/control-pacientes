package com.controlpacientes.ui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class HotReloadService {

    private final ApplicationContext applicationContext;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "HotReload-Monitor");
        t.setDaemon(true);
        return t;
    });
    private WatchService watchService;
    private Map<String, Long> lastModifiedTimes = new HashMap<>();
    private Scene currentScene;
    private UINavigator uiNavigator;

    public HotReloadService(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Inicia el monitoreo de cambios en archivos FXML y CSS
     */
    public void startWatching(Scene scene, UINavigator uiNavigator) {
        this.currentScene = scene;
        this.uiNavigator = uiNavigator;
        
        executorService.execute(() -> {
            try {
                watchService = FileSystems.getDefault().newWatchService();
                
                // Obtener las rutas de recursos
                String resourcePath = getResourcesPath();
                Path fxmlPath = Paths.get(resourcePath, "fxml");
                Path cssPath = Paths.get(resourcePath, "css");
                
                // Registrar directorios
                if (Files.exists(fxmlPath)) {
                    fxmlPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    log.info("Monitoreando cambios en FXML: {}", fxmlPath);
                }
                
                if (Files.exists(cssPath)) {
                    cssPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                    log.info("Monitoreando cambios en CSS: {}", cssPath);
                }
                
                // Bucle de monitoreo
                watchForChanges();
                
            } catch (Exception e) {
                log.error("Error en HotReload: {}", e.getMessage(), e);
            }
        });
    }

    /**
     * Observa los cambios en los archivos
     */
    private void watchForChanges() throws InterruptedException {
        WatchKey key;
        while ((key = watchService.take()) != null) {
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    Path filePath = (Path) event.context();
                    String fileName = filePath.toString();
                    
                    // Evitar cambios duplicados en corto tiempo (debounce)
                    if (shouldProcess(fileName)) {
                        if (fileName.endsWith(".fxml")) {
                            handleFXMLChange(fileName);
                        } else if (fileName.endsWith(".css")) {
                            handleCSSChange();
                        }
                    }
                }
            }
            key.reset();
        }
    }

    /**
     * Maneja cambios en archivos FXML
     */
    private void handleFXMLChange(String fileName) {
        log.info("Detectado cambio en FXML: {}", fileName);
        Platform.runLater(() -> {
            try {
                reloadFXML(fileName);
            } catch (Exception e) {
                log.error("Error recargando FXML {}: {}", fileName, e.getMessage());
            }
        });
    }

    /**
     * Maneja cambios en archivos CSS
     */
    private void handleCSSChange() {
        log.info("Detectado cambio en CSS");
        Platform.runLater(() -> {
            try {
                reloadCSS();
            } catch (Exception e) {
                log.error("Error recargando CSS: {}", e.getMessage());
            }
        });
    }

    /**
     * Recarga el CSS sin reiniciar la aplicación
     */
    private void reloadCSS() throws Exception {
        if (currentScene == null) return;
        
        // Remover stylesheets antiguos
        currentScene.getStylesheets().clear();
        
        // Agregar nuevamente el stylesheet
        String cssResource = loadStylesheet();
        if (cssResource != null) {
            // Agregar timestamp para evitar caché
            currentScene.getStylesheets().add(cssResource + "?t=" + System.currentTimeMillis());
        }
        
        log.info("CSS recargado exitosamente");
    }

    /**
     * Recarga el FXML específico
     */
    private void reloadFXML(String fxmlFileName) throws Exception {
        if (currentScene == null || uiNavigator == null) return;
        
        try {
            String fxmlResource = "/fxml/" + fxmlFileName;
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlResource));
            loader.setControllerFactory(applicationContext::getBean);
            Parent newRoot = loader.load();
            
            currentScene.setRoot(newRoot);
            log.info("FXML recargado: {}", fxmlFileName);
        } catch (Exception e) {
            log.error("Error cargando FXML: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Implementa debounce para evitar múltiples cambios en corto tiempo
     */
    private boolean shouldProcess(String fileName) {
        long currentTime = System.currentTimeMillis();
        Long lastTime = lastModifiedTimes.get(fileName);
        
        if (lastTime == null || currentTime - lastTime > 500) { // 500ms debounce
            lastModifiedTimes.put(fileName, currentTime);
            return true;
        }
        return false;
    }

    /**
     * Obtiene la ruta de los recursos
     */
    private String getResourcesPath() {
        try {
            String classPath = getClass().getProtectionDomain()
                    .getCodeSource().getLocation().getPath();
            
            // Si está en target/classes, retorna la ruta src/main/resources
            if (classPath.contains("target/classes")) {
                return classPath.replace("target/classes", "src/main/resources");
            }
            
            // Intentar obtener del proyecto
            File projectRoot = new File(".").getAbsoluteFile();
            return projectRoot.getParent() + "/src/main/resources";
        } catch (Exception e) {
            log.warn("No se pudo determinar ruta de recursos: {}", e.getMessage());
            return "src/main/resources";
        }
    }

    /**
     * Detiene el monitoreo
     */
    public void stop() {
        try {
            if (watchService != null) {
                watchService.close();
            }
            executorService.shutdown();
            log.info("HotReload Service detenido");
        } catch (Exception e) {
            log.error("Error deteniendo HotReload: {}", e.getMessage());
        }
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
