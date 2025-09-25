package org.oosd.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class HighScore implements Comparable<HighScore> {
    
    private String playerName;
    private int score;
    private int linesCleared;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    
    private String fieldSize;
    
    // Default constructor for Jackson
    public HighScore() {
    }
    
    public HighScore(String playerName, int score, int linesCleared, String fieldSize) {
        this.playerName = playerName;
        this.score = score;
        this.linesCleared = linesCleared;
        this.fieldSize = fieldSize;
        this.date = LocalDateTime.now();
    }
    
    // Compare by score (descending order - highest first)
    @Override
    public int compareTo(HighScore other) {
        return Integer.compare(other.score, this.score);
    }
    
    // Getters and setters
    public String getPlayerName() {
        return playerName;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
    public int getScore() {
        return score;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getLinesCleared() {
        return linesCleared;
    }
    
    public void setLinesCleared(int linesCleared) {
        this.linesCleared = linesCleared;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public String getFieldSize() {
        return fieldSize;
    }
    
    public void setFieldSize(String fieldSize) {
        this.fieldSize = fieldSize;
    }
    
    @Override
    public String toString() {
        return String.format("%s: %d points (%d lines) [%s] - %s", 
                playerName, score, linesCleared, fieldSize, date.toLocalDate());
    }
}