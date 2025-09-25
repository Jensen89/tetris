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
import org.oosd.config.GameConfig;

public class ConfigScreen {
    private final StackPane root;
    private final Main mainApp;
    private final GameConfig config;


    public ConfigScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
        this.config = GameConfig.getInstance();
    }

    public void show() {
        VBox configScreen = new VBox(20);
        configScreen.setPadding(new Insets(20));
        configScreen.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Configuration");
        titleLabel.getStyleClass().add("title-label");

        VBox settingsContainer = new VBox(10);
        settingsContainer.setAlignment(Pos.CENTER_LEFT);
        settingsContainer.setPadding(new Insets(20));

        //SETTINGS BUTTONS/SLIDERS
        //Music setting
        HBox musicBox = new HBox(10);
        musicBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox musicCB = new CheckBox("Music (on/off)");
        musicCB.setSelected(config.isMusicEnabled());
        Label musicStatus = new Label(config.isMusicEnabled() ? "ON" : "OFF");
        musicCB.setOnAction(e -> {
            config.setMusicEnabled(musicCB.isSelected());
            musicStatus.setText(config.isMusicEnabled() ? "ON" : "OFF");
        });
        musicBox.getChildren().addAll(musicCB, musicStatus);

        //Sound effect setting
        HBox soundBox = new HBox(10);
        soundBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox soundCB = new CheckBox("Sound Effects (on/off)");
        soundCB.setSelected(config.areSoundEffectsEnabled());
        Label soundStatus = new Label(config.areSoundEffectsEnabled() ? "ON" : "OFF");
        soundCB.setOnAction(e -> {
            config.setSoundEffectsEnabled(soundCB.isSelected());
            soundStatus.setText(config.areSoundEffectsEnabled() ? "ON" : "OFF");
        });
        soundBox.getChildren().addAll(soundCB, soundStatus);

        //AI play setting
        HBox aiBox = new HBox(10);
        aiBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox aiCB = new CheckBox("AI Play (on/off)");
        aiCB.setSelected(config.isAiPlayEnabled());
        Label aiStatus = new Label(config.isAiPlayEnabled() ? "ON" : "OFF");
        aiCB.setOnAction(e -> {
            config.setAiPlayEnabled(aiCB.isSelected());
            aiStatus.setText(config.isAiPlayEnabled() ? "ON" : "OFF");
        });
        aiBox.getChildren().addAll(aiCB, aiStatus);

        //Extend mode setting
        HBox extendBox = new HBox(10);
        extendBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox extendCB = new CheckBox("Extend Mode (on/off)");
        extendCB.setSelected(config.isExtendModeEnabled());
        Label extendStatus = new Label(config.isExtendModeEnabled() ? "ON" : "OFF");
        extendCB.setOnAction(e -> {
            config.setExtendModeEnabled(extendCB.isSelected());
            extendStatus.setText(config.isExtendModeEnabled() ? "ON" : "OFF");
        });
        extendBox.getChildren().addAll(extendCB, extendStatus);

        //Field width setting
        VBox widthBox = new VBox(5);
        Label widthLabel = new Label("Field Width");
        HBox widthControls = new HBox(10);
        widthControls.setAlignment(Pos.CENTER_LEFT);
        Slider widthSlider = new Slider(5, 15, config.getFieldWidth());
        widthSlider.setShowTickLabels(true);
        widthSlider.setShowTickMarks(true);
        widthSlider.setMajorTickUnit(5);
        widthSlider.setMinorTickCount(4);
        widthSlider.setSnapToTicks(true);
        widthSlider.setBlockIncrement(1);
        widthSlider.setPrefWidth(200);
        Label widthValue = new Label(String.valueOf(config.getFieldWidth()));
        widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            config.setFieldWidth(newValue.intValue());
            widthValue.setText(String.valueOf(config.getFieldWidth()));
        });
        widthControls.getChildren().addAll(widthSlider, widthValue);
        widthBox.getChildren().addAll(widthLabel, widthControls);

        //Field height setting
        VBox heightBox = new VBox(5);
        Label heightLabel = new Label("Field Height");
        HBox heightControls = new HBox(10);
        heightControls.setAlignment(Pos.CENTER_LEFT);
        Slider heightSlider = new Slider(15, 30, config.getFieldHeight());
        heightSlider.setShowTickLabels(true);
        heightSlider.setShowTickMarks(true);
        heightSlider.setMajorTickUnit(5);
        heightSlider.setMinorTickCount(4);
        heightSlider.setSnapToTicks(true);
        heightSlider.setBlockIncrement(1);
        heightSlider.setPrefWidth(200);
        Label heightValue = new Label(String.valueOf(config.getFieldHeight()));
        heightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            config.setFieldHeight(newValue.intValue());
            heightValue.setText(String.valueOf(config.getFieldHeight()));
        });
        heightControls.getChildren().addAll(heightSlider, heightValue);
        heightBox.getChildren().addAll(heightLabel, heightControls);

        //Game level setting
        VBox levelBox = new VBox(5);
        Label levelLabel = new Label("Game Level");
        HBox levelControls = new HBox(10);
        levelControls.setAlignment(Pos.CENTER_LEFT);
        Slider levelSlider = new Slider(1, 10, config.getGameLevel());
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setSnapToTicks(true);
        levelSlider.setBlockIncrement(1);
        levelSlider.setPrefWidth(200);
        Label levelValue = new Label(String.valueOf(config.getGameLevel()));
        levelSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            config.setGameLevel(newValue.intValue());
            levelValue.setText(String.valueOf(config.getGameLevel()));
        });
        levelControls.getChildren().addAll(levelSlider, levelValue);
        levelBox.getChildren().addAll(levelLabel, levelControls);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            config.saveConfig();
            mainApp.showMainMenuScreen();
        });

        settingsContainer.getChildren().addAll(
                widthBox,
                heightBox,
                levelBox,
                musicBox,
                soundBox,
                aiBox,
                extendBox
        );

        configScreen.getChildren().addAll(titleLabel, settingsContainer, backButton);

        root.getChildren().setAll(configScreen);
    }
}
