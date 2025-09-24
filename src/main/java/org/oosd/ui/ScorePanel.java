package org.oosd.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ScorePanel extends VBox {
    
    private Label scoreLabel;
    private Label linesLabel;
    private Label scoreValueLabel;
    private Label linesValueLabel;
    
    private int currentScore = 0;
    private int totalLines = 0;
    
    public ScorePanel() {
        setupUI();
    }
    
    private void setupUI() {
        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_LEFT);
        setPrefWidth(200);
        
        scoreLabel = new Label("SCORE");
        scoreLabel.getStyleClass().add("score-label");
        
        scoreValueLabel = new Label("0");
        scoreValueLabel.getStyleClass().add("score-value");
        
        linesLabel = new Label("LINES");
        linesLabel.getStyleClass().add("score-label");
        
        linesValueLabel = new Label("0");
        linesValueLabel.getStyleClass().add("score-value");
        
        getChildren().addAll(scoreLabel, scoreValueLabel, linesLabel, linesValueLabel);
        
        getStyleClass().add("score-panel");
    }
    
    public void updateScore(int score) {
        this.currentScore = score;
        scoreValueLabel.setText(String.valueOf(score));
    }
    
    public void updateLines(int lines) {
        this.totalLines = lines;
        linesValueLabel.setText(String.valueOf(lines));
    }
    
    public void resetStats() {
        currentScore = 0;
        totalLines = 0;
        scoreValueLabel.setText("0");
        linesValueLabel.setText("0");
    }
    
    public int getCurrentScore() {
        return currentScore;
    }
    
    public int getTotalLines() {
        return totalLines;
    }
}