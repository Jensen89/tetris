package org.oosd.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.*;

public class GameConfig {
    
    private static GameConfig instance;
    
    // Configuration properties with defaults
    private boolean musicEnabled = true;
    private boolean soundEffectsEnabled = true;
    private boolean aiPlayEnabled = false;
    private boolean extendModeEnabled = false;
    private int fieldWidth = 10;
    private int fieldHeight = 20;
    private int gameLevel = 1;
    
    private static final String CONFIG_FILE = "tetris-config.json";
    private final ObjectMapper objectMapper;
    
    private GameConfig() {
        this.objectMapper = new ObjectMapper();
        loadConfig();
    }
    
    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }
    
    public void saveConfig() {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(CONFIG_FILE), this);
        } catch (IOException e) {
            System.err.println("Failed to save configuration: " + e.getMessage());
        }
    }
    
    public void loadConfig() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                JsonNode node = objectMapper.readTree(configFile);
                
                musicEnabled = node.path("musicEnabled").asBoolean(true);
                soundEffectsEnabled = node.path("soundEffectsEnabled").asBoolean(true);
                aiPlayEnabled = node.path("aiPlayEnabled").asBoolean(false);
                extendModeEnabled = node.path("extendModeEnabled").asBoolean(false);
                fieldWidth = node.path("fieldWidth").asInt(10);
                fieldHeight = node.path("fieldHeight").asInt(20);
                gameLevel = node.path("gameLevel").asInt(1);
            } else {
                System.out.println("No configuration file found, using defaults");
            }
        } catch (IOException e) {
            System.err.println("Failed to load configuration: " + e.getMessage() + ", using defaults");
        }
    }
    
    // Getters
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    public boolean areSoundEffectsEnabled() {
        return soundEffectsEnabled;
    }
    
    public boolean isAiPlayEnabled() {
        return aiPlayEnabled;
    }
    
    public boolean isExtendModeEnabled() {
        return extendModeEnabled;
    }
    
    public int getFieldWidth() {
        return fieldWidth;
    }
    
    public int getFieldHeight() {
        return fieldHeight;
    }
    
    public int getGameLevel() {
        return gameLevel;
    }
    
    // Setters
    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        saveConfig();
    }
    
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        this.soundEffectsEnabled = soundEffectsEnabled;
        saveConfig();
    }
    
    public void setAiPlayEnabled(boolean aiPlayEnabled) {
        this.aiPlayEnabled = aiPlayEnabled;
    }
    
    public void setExtendModeEnabled(boolean extendModeEnabled) {
        this.extendModeEnabled = extendModeEnabled;
    }
    
    public void setFieldWidth(int fieldWidth) {
        if (fieldWidth >= 5 && fieldWidth <= 15) {
            this.fieldWidth = fieldWidth;
        }
    }
    
    public void setFieldHeight(int fieldHeight) {
        if (fieldHeight >= 15 && fieldHeight <= 30) {
            this.fieldHeight = fieldHeight;
        }
    }
    
    public void setGameLevel(int gameLevel) {
        if (gameLevel >= 1 && gameLevel <= 10) {
            this.gameLevel = gameLevel;
        }
    }
}