package com.controlpacientes.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilidad para manejar rutas de datos compartidos en Windows.
 * Los datos se guardan en C:\ProgramData\ControlPacientes\data\
 * en lugar de en la carpeta de instalación, para facilitar upgrades.
 */
public class DataPathUtil {
    
    private static final Logger LOGGER = Logger.getLogger(DataPathUtil.class.getName());
    
    /**
     * Obtiene el directorio de datos compartido de la aplicación.
     * En Windows: C:\ProgramData\ControlPacientes\data\
     * En otros SO: ~/.controlpacientes/data/
     */
    public static Path getDataDirectory() {
        Path dataDir;
        
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            // Windows: usar ProgramData
            String programData = System.getenv("ProgramData");
            if (programData == null) {
                programData = "C:\\ProgramData";
            }
            dataDir = Paths.get(programData, "ControlPacientes", "data");
        } else if (os.contains("mac")) {
            // macOS: usar Library/Application Support
            String home = System.getProperty("user.home");
            dataDir = Paths.get(home, "Library", "Application Support", "ControlPacientes", "data");
        } else {
            // Linux y otros: usar ~/.config
            String home = System.getProperty("user.home");
            dataDir = Paths.get(home, ".config", "controlpacientes", "data");
        }
        
        // Crear el directorio si no existe
        try {
            Files.createDirectories(dataDir);
            LOGGER.log(Level.INFO, "Directorio de datos: " + dataDir);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear directorio de datos: " + dataDir, e);
        }
        
        return dataDir;
    }
    
    /**
     * Obtiene el directorio de logs de la aplicación.
     */
    public static Path getLogsDirectory() {
        Path logsDir;
        
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            String programData = System.getenv("ProgramData");
            if (programData == null) {
                programData = "C:\\ProgramData";
            }
            logsDir = Paths.get(programData, "ControlPacientes", "logs");
        } else if (os.contains("mac")) {
            String home = System.getProperty("user.home");
            logsDir = Paths.get(home, "Library", "Logs", "ControlPacientes");
        } else {
            String home = System.getProperty("user.home");
            logsDir = Paths.get(home, ".local", "share", "controlpacientes", "logs");
        }
        
        try {
            Files.createDirectories(logsDir);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al crear directorio de logs: " + logsDir, e);
        }
        
        return logsDir;
    }
    
    /**
     * Obtiene la ruta completa de un archivo de datos.
     */
    public static Path getDataFile(String filename) {
        return getDataDirectory().resolve(filename);
    }
    
    /**
     * Obtiene la ruta completa de un archivo de log.
     */
    public static Path getLogFile(String filename) {
        return getLogsDirectory().resolve(filename);
    }
}
