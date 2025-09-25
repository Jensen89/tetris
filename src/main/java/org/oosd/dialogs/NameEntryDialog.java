package org.oosd.dialogs;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class NameEntryDialog {
    
    private String playerName = null;
    private Stage dialog;
    
    public String showDialog(Stage parent, int score, int rank) {
        dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(parent);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setTitle("New High Score!");
        dialog.setResizable(false);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Label congratsLabel = new Label("Congratulations!");
        congratsLabel.getStyleClass().add("title-label");
        
        Label scoreLabel = new Label("Score: " + score);
        scoreLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");
        
        Label rankLabel = new Label("Rank: #" + rank);
        rankLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        
        Label promptLabel = new Label("Enter your name:");
        promptLabel.setStyle("-fx-font-size: 14;");
        
        TextField nameField = new TextField();
        nameField.setPromptText("Player name (3-15 characters)");
        nameField.setPrefWidth(200);
        nameField.textProperty().addListener((obs, oldText, newText) -> {
            // Limit length and filter invalid characters
            if (newText.length() > 15) {
                nameField.setText(oldText);
            } else {
                // Remove invalid characters (keep letters, numbers, spaces, basic punctuation)
                String filtered = newText.replaceAll("[^a-zA-Z0-9 ._-]", "");
                if (!filtered.equals(newText)) {
                    nameField.setText(filtered);
                }
            }
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button okButton = new Button("OK");
        okButton.setPrefWidth(80);
        okButton.setDefaultButton(true);
        okButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            if (isValidName(name)) {
                playerName = name;
                dialog.close();
            } else {
                showError("Please enter a name with 3-15 characters");
                nameField.requestFocus();
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setPrefWidth(80);
        cancelButton.setCancelButton(true);
        cancelButton.setOnAction(e -> {
            playerName = null;
            dialog.close();
        });
        
        buttonBox.getChildren().addAll(okButton, cancelButton);
        
        // Handle Enter and Escape keys
        nameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                okButton.fire();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                cancelButton.fire();
            }
        });
        
        content.getChildren().addAll(
                congratsLabel,
                scoreLabel, 
                rankLabel,
                promptLabel,
                nameField,
                buttonBox
        );
        
        Scene scene = new Scene(content);
        dialog.setScene(scene);
        
        // Center dialog and focus name field
        dialog.centerOnScreen();
        Platform.runLater(() -> nameField.requestFocus());
        
        dialog.showAndWait();
        return playerName;
    }
    
    private boolean isValidName(String name) {
        return name != null && 
               name.length() >= 3 && 
               name.length() <= 15 &&
               !name.trim().isEmpty();
    }
    
    private void showError(String message) {
        // Simple error display - could be enhanced with a proper error dialog
        System.err.println("Name entry error: " + message);
    }
}