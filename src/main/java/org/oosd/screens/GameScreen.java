package org.oosd.screens;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.oosd.dialogs.ExitConfirmationDialog;
import org.oosd.Main;
import org.oosd.config.GameConfig;
import org.oosd.game.TetrisGame;
import org.oosd.game.pieces.TetrisPiece;
import org.oosd.ui.ScorePanel;


public class GameScreen implements TetrisGame.GameEventListener {
    private final StackPane root;
    private final Main mainApp;

    //Game logic
    private TetrisGame game;

    //UI components
    private Pane gameArea;
    private VBox gameScreenContainer;
    private StackPane gameAreaContainer;
    private VBox pauseOverlay;
    private ScorePanel scorePanel;

    //Animation components
    private AnimationTimer gameLoop;
    private long lastFallTime = 0;
    private long fallInterval = 1_000_000_000L; // 1 second in nanoseconds

    private static final int BLOCK_SIZE = 30;

    public GameScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }


    public void show() {

        //Initialise game with config dimensions
        GameConfig config = GameConfig.getInstance();
        game = new TetrisGame(config.getFieldWidth(), config.getFieldHeight());
        game.setEventListener(this);

        //Main container
        gameScreenContainer = new VBox(20);
        gameScreenContainer.setPadding(new Insets(20));
        gameScreenContainer.setAlignment(Pos.TOP_CENTER);

        final int GAME_WIDTH = game.getGridWidth() * BLOCK_SIZE;
        final int GAME_HEIGHT = game.getGridHeight() * BLOCK_SIZE;

        //Game pane with temp static size - add ability to change variables later
        gameArea = new Pane();
        gameArea.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        gameArea.setMaxSize(GAME_WIDTH, GAME_HEIGHT);
        gameArea.setMinSize(GAME_WIDTH, GAME_HEIGHT);

        //Temp game area styling
        gameArea.setStyle(
                        "-fx-border-color: #333333;" +
                        "-fx-border-width: 3;" +
                        "-fx-background-color: #f0f0f0;"
        );

        //Create container for pause overlay
        gameAreaContainer = new StackPane();
        gameAreaContainer.setMaxSize(GAME_WIDTH,GAME_HEIGHT);
        gameAreaContainer.setAlignment(Pos.CENTER);

        //Create pause overlay
        createPauseOverlay();

        gameAreaContainer.getChildren().addAll(gameArea, pauseOverlay);

        //Create score panel
        scorePanel = new ScorePanel();

        //Create horizontal container for score panel and game area
        HBox gameContainer = new HBox(20);
        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.getChildren().addAll(scorePanel, gameAreaContainer);

        Label gameTitle = new Label("PLAY");
        gameTitle.getStyleClass().add("title-label");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            //Pause the game first if it's running
            if (game.isGameRunning() && !game.isGamePaused()) {
                game.pauseGame();
                hidePauseOverlay();
            }

            Stage stage = (Stage) root.getScene().getWindow();

            //Show confirmation dialog
            ExitConfirmationDialog confirmDialog = new ExitConfirmationDialog(
                    "Quit Game",
                    "Are you sure you want to quit the current game?"
            );

            boolean shouldQuit = confirmDialog.show(stage);

            if (shouldQuit) {
                stopGame();
                mainApp.showMainMenuScreen();
            } else {
                if (game.isGamePaused() && game.isGameStarted()) {
                    game.resumeGame();
                }

                //Refocus on game container for controls after pause
                gameScreenContainer.requestFocus();
            }
        });

        gameScreenContainer.getChildren().addAll(gameTitle, gameContainer, backButton);

        root.getChildren().setAll(gameScreenContainer);

        setupKeyboardControls(gameScreenContainer);

        renderGameGrid();

        startGame();
    }

    private void createPauseOverlay() {
        pauseOverlay = new VBox(10);
        pauseOverlay.setAlignment(Pos.CENTER);

        Label pauseLabel = new Label("Game Paused");
        Label pauseInstructionLabel = new Label("Press P to continue");

        pauseOverlay.getChildren().addAll(pauseLabel, pauseInstructionLabel);
        pauseOverlay.setVisible(false);
    }

    private void showPauseOverlay() {
        pauseOverlay.setVisible(true);
    }

    private void hidePauseOverlay() {
        pauseOverlay.setVisible(false);
    }


    private void renderGameGrid() {
        //Clear existing visual blocks
        gameArea.getChildren().removeIf(node -> node instanceof Rectangle);

        //Draw fixed blocks from game grid
        int [][] grid = game.getGrid();
        for (int row = 0; row < game.getGridHeight(); row++) {
            for (int col = 0; col < game.getGridWidth(); col++) {
                if (grid[row][col] == 1) {
                    Rectangle block = createBlock(col, row, Color.GREY); // Change this to keep block original colour
                    gameArea.getChildren().add(block);
                }
            }
        }

        //Draw current falling piece
        TetrisPiece currentPiece = game.getCurrentPiece();
        if (currentPiece != null) {
            int[][] positions = currentPiece.getOccupiedPositions();
            for (int[] pos : positions) {
                if (pos[0] >= 0 && pos[0] < game.getGridWidth() && pos[1] >= 0 && pos[1] < game.getGridHeight()) {
                    Rectangle block = createBlock(pos[0], pos[1], currentPiece.getColor());
                    gameArea.getChildren().add(block);
                }
            }
        }
    }

    private Rectangle createBlock(int gridX, int gridY, Color color) {
        Rectangle block = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
        block.setFill(color);
        block.setStroke(Color.DARKGRAY);
        block.setStrokeWidth(1);

        //Position based on grid coordinates
        block.setX(gridX * BLOCK_SIZE);
        block.setY(gridY * BLOCK_SIZE);

        return block;
    }

    //Start the game loop
    private void startGame() {
        game.startGame();

        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!game.isGameRunning() || game.isGamePaused()) {
                    return;
                }

                //Check if it's time for the piece to fall
                if (now - lastFallTime >= fallInterval) {
                    game.tick();
                    lastFallTime = now;
                }
            }
        };

        gameLoop.start();
    }

    private void stopGame() {
        game.stopGame();
        if  (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void restartGame() {
        game.restartGame();
        scorePanel.resetStats();
        hidePauseOverlay();
    }

    //Set up keyboard event handling
    private void setupKeyboardControls(VBox gameScreenContainer) {
        gameScreenContainer.setFocusTraversable(true);
        gameScreenContainer.requestFocus();

        gameScreenContainer.setOnKeyPressed(event -> {
            //Don't process keys if game is paused (except P to unpause)
            if (game.isGamePaused() && event.getCode() != javafx.scene.input.KeyCode.P) {
                return;
            }

            switch (event.getCode()) {
                case LEFT -> game.moveLeft();
                case RIGHT -> game.moveRight();
                case DOWN -> {
                    if (!game.moveDown()) {
                        game.tick();
                    }
                }
                case UP -> game.rotate();
                case SPACE -> game.hardDrop();
                case R -> restartGame();
                case P -> {
                    game.togglePause();
                    if (game.isGamePaused()) {
                        showPauseOverlay();
                    } else {
                        hidePauseOverlay();
                    }
                }
                default -> {
                    //Do nothing for other keys
                }
            }

            event.consume(); //Prevent event from bubbling up
        });
    }

    @Override
    public void onGridUpdated() {
        renderGameGrid();
    }

    @Override
    public void onGameOver() {
        //Add game over ui here
    }

    @Override
    public void onLinesClearedUpdate(int lines){
        scorePanel.updateLines(lines);
    }
    
    @Override
    public void onScoreUpdate(int score) {
        scorePanel.updateScore(score);
    }
    
    @Override
    public void onNextPieceUpdate(TetrisPiece nextPiece) {
        scorePanel.updateNextPiece(nextPiece);
    }
}

