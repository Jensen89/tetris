package org.oosd.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.oosd.Main;

public class ConfigScreen {
    private final StackPane root;
    private final Main mainApp;

    //Config variables with defaults set
    private boolean musicEnabled = true;
    private boolean soundEffectsEnabled = true;
    private boolean aiPlayEnabled = false;
    private boolean extendModeEnabled = false;
    private int fieldWidth = 10;
    private int fieldHeight = 20;
    private int gameLevel = 1;


    public ConfigScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }

    public void show() {
        VBox configScreen = new VBox(15);
        configScreen.setPadding(new Insets(20));
        configScreen.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Configuration");

        VBox settingsContainer = new VBox(10);
        settingsContainer.setAlignment(Pos.CENTER);
        settingsContainer.setPadding(new Insets(20));

        //SETTINGS BUTTONS/SLIDERS
        //Music setting
        HBox musicBox = new HBox(10);
        musicBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox musicCB = new CheckBox("Music (on/off)");
        musicCB.setSelected(musicEnabled);
        Label musicStatus = new Label(musicEnabled ? "ON" : "OFF");
        musicCB.setOnAction(e -> {
            musicEnabled = musicCB.isSelected();
            musicStatus.setText(musicEnabled ? "ON" : "OFF");
        });
        musicBox.getChildren().addAll(musicCB, musicStatus);

        //Sound effect setting
        HBox soundBox = new HBox(10);
        soundBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox soundCB = new CheckBox("Sound Effects (on/off)");
        soundCB.setSelected(soundEffectsEnabled);
        Label soundStatus = new Label(soundEffectsEnabled ? "ON" : "OFF");
        soundCB.setOnAction(e -> {
            soundEffectsEnabled = soundCB.isSelected();
            soundStatus.setText(soundEffectsEnabled ? "ON" : "OFF");
        });
        soundBox.getChildren().addAll(soundCB, soundStatus);

        //AI play setting
        HBox aiBox = new HBox(10);
        aiBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox aiCB = new CheckBox("AI Play (on/off)");
        aiCB.setSelected(aiPlayEnabled);
        Label aiStatus = new Label(aiPlayEnabled ? "ON" : "OFF");
        aiCB.setOnAction(e -> {
            aiPlayEnabled = aiCB.isSelected();
            aiStatus.setText(aiPlayEnabled ? "ON" : "OFF");
        });
        aiBox.getChildren().addAll(aiCB, aiStatus);

        //Extend mode setting
        HBox extendBox = new HBox(10);
        extendBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox extendCB = new CheckBox("Extend Mode (on/off)");
        extendCB.setSelected(extendModeEnabled);
        Label extendStatus = new Label(extendModeEnabled ? "ON" : "OFF");
        extendCB.setOnAction(e -> {
            extendModeEnabled = extendCB.isSelected();
            extendStatus.setText(extendModeEnabled ? "ON" : "OFF");
        });
        extendBox.getChildren().addAll(extendCB, extendStatus);

        //Field width setting
        VBox widthBox = new VBox(5);
        Label widthLabel = new Label("Field Width");
        HBox widthControls = new HBox(10);
        widthControls.setAlignment(Pos.CENTER_LEFT);
        Slider widthSlider = new Slider(5, 15, fieldWidth);
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(5);
        widthSlider.setMinorTickCount(4);
        widthSlider.setSnapToTicks(true);
        widthSlider.setBlockIncrement(1);
        widthSlider.setPrefWidth(200);
        Label widthValue = new Label(String.valueOf(fieldWidth));
        widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fieldWidth = newValue.intValue();
            widthValue.setText(String.valueOf(fieldWidth));
        });
        widthControls.getChildren().addAll(widthSlider, widthValue);
        widthBox.getChildren().addAll(widthLabel, widthControls);

        //Field height setting
        VBox heightBox = new VBox(5);
        Label heightLabel = new Label("Field Height");
        HBox heightControls = new HBox(10);
        heightControls.setAlignment(Pos.CENTER_LEFT);
        Slider heightSlider = new Slider(15, 30, fieldHeight);
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(5);
        heightSlider.setMinorTickCount(4);
        heightSlider.setSnapToTicks(true);
        heightSlider.setBlockIncrement(1);
        heightSlider.setPrefWidth(200);
        Label heightValue = new Label(String.valueOf(fieldHeight));
        heightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            fieldHeight = newValue.intValue();
            heightValue.setText(String.valueOf(fieldHeight));
        });
        heightControls.getChildren().addAll(heightSlider, heightValue);
        heightBox.getChildren().addAll(heightLabel, heightControls);

        //Game level setting
        VBox levelBox = new VBox(5);
        Label levelLabel = new Label("Game Level");
        HBox levelControls = new HBox(10);
        levelControls.setAlignment(Pos.CENTER_LEFT);
        Slider levelSlider = new Slider(1, 10, gameLevel);
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setSnapToTicks(true);
        levelSlider.setBlockIncrement(1);
        levelSlider.setPrefWidth(200);
        Label levelValue = new Label(String.valueOf(gameLevel));
        levelSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            gameLevel = newValue.intValue();
            levelValue.setText(String.valueOf(gameLevel));
        });
        levelControls.getChildren().addAll(levelSlider, levelValue);
        levelBox.getChildren().addAll(levelLabel, levelControls);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> mainApp.showMainMenuScreen());

        configScreen.getChildren().addAll(
                musicBox,
                soundBox,
                aiBox,
                extendBox,
                widthBox,
                heightBox,
                levelBox
        );

        configScreen.getChildren().addAll(titleLabel, settingsContainer, backButton);

        root.getChildren().setAll(configScreen);
    }
}
