package org.oosd.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ExitConfirmationDialog {
    private boolean confirmed = false;
    private String title;
    private String message;

    //Constructor with custom title and message
    public ExitConfirmationDialog(String title, String message) {
        this.title = title;
        this.message = message;
    }

    //Default constructor
    public ExitConfirmationDialog() {
        this("Exit Confirmation", "Are you sure you want to exit the game?");
    }

    public boolean show(Stage parentStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(parentStage);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setTitle(title);
        dialogStage.setResizable(false);

        //Create the dialog content
        VBox dialogContent = new VBox(20);
        dialogContent.setPadding(new Insets(30));
        dialogContent.setAlignment(Pos.CENTER);

        Label messageLabel = new Label(message);

        //Buttons container
        HBox buttonBox = new HBox(20);
        buttonBox.setAlignment(Pos.CENTER);

        //Yes button
        Button yesButton = new Button("Yes");
        yesButton.setPrefWidth(80);
        yesButton.setOnAction(e -> {
            confirmed = true;
            dialogStage.close();
        });

        //No button
        Button noButton = new Button("No");
        noButton.setPrefWidth(80);
        noButton.setOnAction(e -> {
            confirmed = false;
            dialogStage.close();
        });

        //Set default button (No)
        noButton.setDefaultButton(true);

        buttonBox.getChildren().addAll(yesButton, noButton);

        dialogContent.getChildren().addAll(messageLabel, buttonBox);

        Scene dialogScene = new Scene(dialogContent, 350, 150);
        dialogStage.setScene(dialogScene);

        //Center the dialog on the parent stage
        dialogStage.setOnShown(e -> {
            dialogStage.setX(parentStage.getX() + (parentStage.getWidth() - dialogStage.getWidth()) / 2);
            dialogStage.setY(parentStage.getY() + (parentStage.getHeight() - dialogStage.getHeight()) / 2);
        });

        //Show and wait for user response
        dialogStage.showAndWait();

        return confirmed;
    }
}