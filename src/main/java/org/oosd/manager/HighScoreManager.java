package org.oosd.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.oosd.model.HighScore;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class HighScoreManager {
    
    private static HighScoreManager instance;
    private static final String HIGH_SCORES_FILE = "tetris-highscores.json";
    private static final int MAX_HIGH_SCORES = 10;
    
    private final ObjectMapper objectMapper;
    private List<HighScore> highScores;
    
    private HighScoreManager() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.highScores = new ArrayList<>();
        loadHighScores();
    }
    
    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }
    
    public void addHighScore(HighScore highScore) {
        if (highScore == null || highScore.getScore() <= 0) {
            return;
        }
        
        highScores.add(highScore);
        
        // Sort by score (descending) and keep only top MAX_HIGH_SCORES
        highScores = highScores.stream()
                .sorted()
                .limit(MAX_HIGH_SCORES)
                .collect(Collectors.toList());
        
        saveHighScores();
    }
    
    public List<HighScore> getTopHighScores() {
        return new ArrayList<>(highScores);
    }
    
    public List<HighScore> getTopHighScores(int limit) {
        return highScores.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    public boolean isHighScore(int score) {
        if (score <= 0) {
            return false;
        }
        
        // If we have fewer than MAX_HIGH_SCORES, any score > 0 qualifies
        if (highScores.size() < MAX_HIGH_SCORES) {
            return true;
        }
        
        // Check if score beats the lowest high score
        HighScore lowestHighScore = highScores.get(highScores.size() - 1);
        return score > lowestHighScore.getScore();
    }
    
    public int getScoreRank(int score) {
        for (int i = 0; i < highScores.size(); i++) {
            if (score > highScores.get(i).getScore()) {
                return i + 1;
            }
        }
        return highScores.size() + 1;
    }
    
    private void saveHighScores() {
        try {
            Map<String, Object> data = new HashMap<>();
            data.put("highScores", highScores);
            
            objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValue(new File(HIGH_SCORES_FILE), data);
        } catch (IOException e) {
            System.err.println("Failed to save high scores: " + e.getMessage());
        }
    }
    
    private void loadHighScores() {
        try {
            File file = new File(HIGH_SCORES_FILE);
            if (file.exists()) {
                JsonNode rootNode = objectMapper.readTree(file);
                JsonNode scoresNode = rootNode.path("highScores");
                
                if (scoresNode.isArray()) {
                    highScores = Arrays.asList(
                            objectMapper.treeToValue(scoresNode, HighScore[].class)
                    );
                    
                    // Sort to ensure correct order
                    highScores = new ArrayList<>(highScores);
                    highScores.sort(null);
                }
            } else {
                // Create initial high scores file with some default data
                createDefaultHighScores();
            }
        } catch (IOException e) {
            System.err.println("Failed to load high scores: " + e.getMessage() + ", creating defaults");
            createDefaultHighScores();
        }
    }
    
    private void createDefaultHighScores() {
        highScores = new ArrayList<>();
        
        // Add some default scores
        highScores.add(new HighScore("Alex", 50000, 125, "10x20"));
        highScores.add(new HighScore("Sarah", 45000, 110, "10x20"));
        highScores.add(new HighScore("Mike", 42000, 105, "10x20"));
        highScores.add(new HighScore("Emma", 38000, 95, "10x20"));
        highScores.add(new HighScore("James", 35000, 87, "10x20"));
        highScores.add(new HighScore("Lisa", 32000, 80, "10x20"));
        highScores.add(new HighScore("David", 28000, 70, "10x20"));
        highScores.add(new HighScore("Sophie", 25000, 62, "10x20"));
        highScores.add(new HighScore("Chris", 22000, 55, "10x20"));
        highScores.add(new HighScore("Kate", 20000, 50, "10x20"));
        
        saveHighScores();
    }
    
    public void clearHighScores() {
        highScores.clear();
        saveHighScores();
    }
}