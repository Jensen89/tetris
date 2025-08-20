package org.oosd.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.oosd.Main;

public class ConfigScreen {
    private final StackPane root;
    private final Main mainApp;

    public ConfigScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }

    public void show() {
        VBox configScreen = new VBox(10);
        configScreen.setPadding(new Insets(20));
        configScreen.setAlignment(Pos.TOP_LEFT);

        Label label = new Label("Configuration");

        // Back button
        Button backButton = new Button("Back");
        backButton.setLayoutX(10);
        backButton.setLayoutY(10);
        backButton.setOnAction(e -> mainApp.showMainMenuScreen());

        // Setting buttons
        CheckBox musicCB = new CheckBox("Music (on/off)");
        CheckBox soundCB = new CheckBox("Sound Effects (on/off)");
        CheckBox aiCB = new CheckBox("AI Play (on/off)");
        CheckBox extendCB = new CheckBox("Extend Mode (on/off)");

        Label fWidthLabel = new Label("Field Width");
        Slider fWidthSlider = new Slider(0, 100, 600);
        fWidthSlider.setShowTickLabels(true);
        fWidthSlider.setMajorTickUnit(100);

        Label fHeightLabel = new Label("Field Height");
        Slider fHeightSlider = new Slider(0, 100, 600);
        fHeightSlider.setShowTickLabels(true);
        fHeightSlider.setMajorTickUnit(100);

        Label levelLabel = new Label("Game Level");
        Slider levelSlider = new Slider(0, 100, 600);
        levelSlider.setShowTickLabels(true);
        levelSlider.setMajorTickUnit(10);

        configScreen.getChildren().addAll(
                label,
                backButton,
                musicCB,
                soundCB,
                aiCB,
                extendCB,
                fWidthLabel,
                fWidthSlider,
                fHeightLabel,
                fHeightSlider,
                levelLabel,
                levelSlider
        );

        root.getChildren().setAll(configScreen);
    }
}
