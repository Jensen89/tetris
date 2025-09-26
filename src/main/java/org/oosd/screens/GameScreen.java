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
import org.oosd.dialogs.NameEntryDialog;
import org.oosd.Main;
import org.oosd.config.GameConfig;
import org.oosd.game.TetrisGame;
import org.oosd.game.pieces.TetrisPiece;
import org.oosd.manager.HighScoreManager;
import org.oosd.model.HighScore;
import org.oosd.ui.ScorePanel;
import org.oosd.audio.MusicManager;
import org.oosd.audio.SoundEffectsManager;
import org.oosd.game.pieces.TetrominoFactory;


public class GameScreen implements TetrisGame.GameEventListener {
    private final StackPane root;
    private final Main mainApp;

    //Game logic
    private TetrisGame game;
    private TetrisGame player2Game; // For 2-player mode
    private boolean isDualPlayerMode = false;

    //UI components
    private Pane gameArea;
    private Pane player2GameArea; // For 2-player mode
    private VBox gameScreenContainer;
    private StackPane gameAreaContainer;
    private StackPane player2GameAreaContainer; // For 2-player mode
    private VBox pauseOverlay;
    private VBox player2PauseOverlay; // For 2-player mode
    private ScorePanel scorePanel;
    private ScorePanel player2ScorePanel; // For 2-player mode

    //Animation components
    private AnimationTimer gameLoop;
    private long lastFallTime = 0;
    private long player2LastFallTime = 0; // For 2-player mode
    private long fallInterval = 1_000_000_000L; // 1 second in nanoseconds

    private static final int BLOCK_SIZE = 30;

    public GameScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }


    public void show() {

        //Initialise games based on extend mode
        GameConfig config = GameConfig.getInstance();
        isDualPlayerMode = config.isExtendModeEnabled();

        //Set up piece generation for synchronized play
        if (isDualPlayerMode) {
            long seed = System.currentTimeMillis();
            TetrominoFactory.setSeed(seed);
        } else {
            TetrominoFactory.setRandomMode();
        }

        //Initialize games
        game = new TetrisGame(config.getFieldWidth(), config.getFieldHeight());
        game.setEventListener(this);

        if (isDualPlayerMode) {
            player2Game = new TetrisGame(config.getFieldWidth(), config.getFieldHeight());
            player2Game.setEventListener(new Player2EventListener());
        }

        //Main container
        gameScreenContainer = new VBox(20);
        gameScreenContainer.setPadding(new Insets(20));
        gameScreenContainer.setAlignment(Pos.TOP_CENTER);

        final int GAME_WIDTH = game.getGridWidth() * BLOCK_SIZE;
        final int GAME_HEIGHT = game.getGridHeight() * BLOCK_SIZE;

        HBox gameContainer;

        if (isDualPlayerMode) {
            gameContainer = createDualPlayerLayout(GAME_WIDTH, GAME_HEIGHT);
        } else {
            gameContainer = createSinglePlayerLayout(GAME_WIDTH, GAME_HEIGHT);
        }

        String titleText = isDualPlayerMode ? "2-PLAYER MODE" : "PLAY";
        Label gameTitle = new Label(titleText);
        gameTitle.getStyleClass().add("title-label");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            //Pause the games first if they're running
            if (game.isGameRunning() && !game.isGamePaused()) {
                game.pauseGame();
                hidePauseOverlay();
            }
            if (isDualPlayerMode && player2Game != null && player2Game.isGameRunning() && !player2Game.isGamePaused()) {
                player2Game.pauseGame();
                hidePlayer2PauseOverlay();
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
                handleGameEnd(true);
            } else {
                if (game.isGamePaused() && game.isGameStarted()) {
                    game.resumeGame();
                }
                if (isDualPlayerMode && player2Game != null && player2Game.isGamePaused() && player2Game.isGameStarted()) {
                    player2Game.resumeGame();
                }

                //Refocus on game container for controls after pause
                gameScreenContainer.requestFocus();
            }
        });

        gameScreenContainer.getChildren().addAll(gameTitle, gameContainer, backButton);

        root.getChildren().setAll(gameScreenContainer);

        setupKeyboardControls(gameScreenContainer);

        if (isDualPlayerMode) {
            renderDualGameGrids();
        } else {
            renderGameGrid();
        }

        startGame();
    }

    private HBox createSinglePlayerLayout(int gameWidth, int gameHeight) {
        //Game pane
        gameArea = new Pane();
        gameArea.setPrefSize(gameWidth, gameHeight);
        gameArea.setMaxSize(gameWidth, gameHeight);
        gameArea.setMinSize(gameWidth, gameHeight);
        gameArea.setStyle("-fx-border-color: #333333; -fx-border-width: 3; -fx-background-color: #f0f0f0;");

        //Create container for pause overlay
        gameAreaContainer = new StackPane();
        gameAreaContainer.setMaxSize(gameWidth, gameHeight);
        gameAreaContainer.setAlignment(Pos.CENTER);

        //Create pause overlay
        createPauseOverlay();
        gameAreaContainer.getChildren().addAll(gameArea, pauseOverlay);

        //Create score panel
        scorePanel = new ScorePanel();

        //Create horizontal container
        HBox gameContainer = new HBox(20);
        gameContainer.setAlignment(Pos.CENTER);
        gameContainer.getChildren().addAll(scorePanel, gameAreaContainer);

        return gameContainer;
    }

    private HBox createDualPlayerLayout(int gameWidth, int gameHeight) {
        // Player 1 setup
        gameArea = new Pane();
        gameArea.setPrefSize(gameWidth, gameHeight);
        gameArea.setMaxSize(gameWidth, gameHeight);
        gameArea.setMinSize(gameWidth, gameHeight);
        gameArea.setStyle("-fx-border-color: #333333; -fx-border-width: 3; -fx-background-color: #f0f0f0;");

        gameAreaContainer = new StackPane();
        gameAreaContainer.setMaxSize(gameWidth, gameHeight);
        gameAreaContainer.setAlignment(Pos.CENTER);

        createPauseOverlay();
        gameAreaContainer.getChildren().addAll(gameArea, pauseOverlay);

        scorePanel = new ScorePanel();

        VBox player1Container = new VBox(10);
        player1Container.setAlignment(Pos.CENTER);
        Label player1Label = new Label("Player 1");
        player1Label.getStyleClass().add("title-label");
        player1Container.getChildren().addAll(player1Label, gameAreaContainer);

        // Player 1 section: [ScorePanel] [Player1Game]
        HBox player1Section = new HBox(20);
        player1Section.setAlignment(Pos.CENTER);
        player1Section.getChildren().addAll(scorePanel, player1Container);

        // Player 2 setup
        player2GameArea = new Pane();
        player2GameArea.setPrefSize(gameWidth, gameHeight);
        player2GameArea.setMaxSize(gameWidth, gameHeight);
        player2GameArea.setMinSize(gameWidth, gameHeight);
        player2GameArea.setStyle("-fx-border-color: #333333; -fx-border-width: 3; -fx-background-color: #f0f0f0;");

        player2GameAreaContainer = new StackPane();
        player2GameAreaContainer.setMaxSize(gameWidth, gameHeight);
        player2GameAreaContainer.setAlignment(Pos.CENTER);

        createPlayer2PauseOverlay();
        player2GameAreaContainer.getChildren().addAll(player2GameArea, player2PauseOverlay);

        player2ScorePanel = new ScorePanel();

        VBox player2Container = new VBox(10);
        player2Container.setAlignment(Pos.CENTER);
        Label player2Label = new Label("Player 2");
        player2Label.getStyleClass().add("title-label");
        player2Container.getChildren().addAll(player2Label, player2GameAreaContainer);

        // Player 2 section: [Player2ScorePanel] [Player2Game]
        HBox player2Section = new HBox(20);
        player2Section.setAlignment(Pos.CENTER);
        player2Section.getChildren().addAll(player2ScorePanel, player2Container);

        // Main layout: [Player1Section] [Player2Section]
        HBox dualGameContainer = new HBox(40);
        dualGameContainer.setAlignment(Pos.CENTER);
        dualGameContainer.getChildren().addAll(player1Section, player2Section);

        return dualGameContainer;
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

    private void createPlayer2PauseOverlay() {
        player2PauseOverlay = new VBox(10);
        player2PauseOverlay.setAlignment(Pos.CENTER);

        Label pauseLabel = new Label("Player 2 Paused");
        Label pauseInstructionLabel = new Label("Press P to continue");

        player2PauseOverlay.getChildren().addAll(pauseLabel, pauseInstructionLabel);
        player2PauseOverlay.setVisible(false);
    }

    private void showPlayer2PauseOverlay() {
        player2PauseOverlay.setVisible(true);
    }

    private void hidePlayer2PauseOverlay() {
        player2PauseOverlay.setVisible(false);
    }

    private void renderDualGameGrids() {
        renderGameGrid();
        renderPlayer2GameGrid();
    }

    private void renderPlayer2GameGrid() {
        if (player2Game == null || player2GameArea == null) return;

        //Clear existing visual blocks
        player2GameArea.getChildren().removeIf(node -> node instanceof Rectangle);

        //Draw fixed blocks from game grid
        int [][] grid = player2Game.getGrid();
        for (int row = 0; row < player2Game.getGridHeight(); row++) {
            for (int col = 0; col < player2Game.getGridWidth(); col++) {
                if (grid[row][col] == 1) {
                    Rectangle block = createBlock(col, row, Color.GREY);
                    player2GameArea.getChildren().add(block);
                }
            }
        }

        //Draw current falling piece
        TetrisPiece currentPiece = player2Game.getCurrentPiece();
        if (currentPiece != null) {
            int[][] positions = currentPiece.getOccupiedPositions();
            for (int[] pos : positions) {
                if (pos[0] >= 0 && pos[0] < player2Game.getGridWidth() && pos[1] >= 0 && pos[1] < player2Game.getGridHeight()) {
                    Rectangle block = createBlock(pos[0], pos[1], currentPiece.getColor());
                    player2GameArea.getChildren().add(block);
                }
            }
        }
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

        if (isDualPlayerMode && player2Game != null) {
            player2Game.startGame();
        }

        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // Handle Player 1 game
                if (game.isGameRunning() && !game.isGamePaused()) {
                    if (now - lastFallTime >= fallInterval) {
                        game.tick();
                        lastFallTime = now;
                    }
                }

                // Handle Player 2 game in dual mode
                if (isDualPlayerMode && player2Game != null &&
                    player2Game.isGameRunning() && !player2Game.isGamePaused()) {
                    if (now - player2LastFallTime >= fallInterval) {
                        player2Game.tick();
                        player2LastFallTime = now;
                    }
                }
            }
        };

        gameLoop.start();
    }

    private void stopGame() {
        game.stopGame();

        if (isDualPlayerMode && player2Game != null) {
            player2Game.stopGame();
        }

        if  (gameLoop != null) {
            gameLoop.stop();
        }
    }

    private void restartGame() {
        // Reset piece generation with new seed for dual mode
        if (isDualPlayerMode) {
            long seed = System.currentTimeMillis();
            TetrominoFactory.setSeed(seed);
        } else {
            TetrominoFactory.setRandomMode();
        }

        game.restartGame();
        scorePanel.resetStats();
        hidePauseOverlay();

        if (isDualPlayerMode && player2Game != null) {
            player2Game.restartGame();
            if (player2ScorePanel != null) {
                player2ScorePanel.resetStats();
            }
            hidePlayer2PauseOverlay();
        }
    }

    //Set up keyboard event handling
    private void setupKeyboardControls(VBox gameScreenContainer) {
        gameScreenContainer.setFocusTraversable(true);
        gameScreenContainer.requestFocus();

        gameScreenContainer.setOnKeyPressed(event -> {
            // Handle Player 1 controls (Arrow keys, Space, UP, P, R)
            if (!game.isGamePaused() || event.getCode() == javafx.scene.input.KeyCode.P) {
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
                    case P -> {
                        game.togglePause();
                        if (game.isGamePaused()) {
                            showPauseOverlay();
                        } else {
                            hidePauseOverlay();
                        }
                    }
                    case R -> restartGame();
                }
            }

            // Handle Player 2 controls (WASD, Q) - only in dual player mode
            if (isDualPlayerMode && player2Game != null &&
                (!player2Game.isGamePaused() || event.getCode() == javafx.scene.input.KeyCode.P)) {
                switch (event.getCode()) {
                    case A -> player2Game.moveLeft();
                    case D -> player2Game.moveRight();
                    case S -> {
                        if (!player2Game.moveDown()) {
                            player2Game.tick();
                        }
                    }
                    case W -> player2Game.rotate();
                    case Q -> player2Game.hardDrop();
                    case P -> {
                        // P pauses both players in dual mode
                        player2Game.togglePause();
                        if (player2Game.isGamePaused()) {
                            showPlayer2PauseOverlay();
                        } else {
                            hidePlayer2PauseOverlay();
                        }
                    }
                }
            }

            // Handle global controls (Music, Sound, etc.)
            switch (event.getCode()) {
                case M -> {
                    GameConfig config = GameConfig.getInstance();
                    config.setMusicEnabled(!config.isMusicEnabled());
                    MusicManager.getInstance().updateFromConfig();
                    System.out.println("Music " + (config.isMusicEnabled() ? "enabled" : "disabled"));
                }
                case N -> {
                    GameConfig config = GameConfig.getInstance();
                    config.setSoundEffectsEnabled(!config.areSoundEffectsEnabled());
                    System.out.println("Sound Effects " + (config.areSoundEffectsEnabled() ? "enabled" : "disabled"));
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
        System.out.println("GameScreen: Player 1 Game Over");

        if (isDualPlayerMode) {
            // Player 2 wins if Player 1 loses first
            handleDualPlayerGameOver(false); // Player 2 wins
        } else {
            // Single player mode
            if (gameLoop != null) {
                gameLoop.stop();
            }
            handleGameEnd(false);
        }
    }
    
    private void handleGameEnd(boolean isQuit) {
        int finalScore = scorePanel.getCurrentScore();
        int totalLines = scorePanel.getTotalLines();
        
        if (finalScore > 0) {
            HighScoreManager highScoreManager = HighScoreManager.getInstance();
            
            if (highScoreManager.isHighScore(finalScore)) {
                // Get field size for high score record
                String fieldSize = game.getGridWidth() + "x" + game.getGridHeight();
                int rank = highScoreManager.getScoreRank(finalScore);
                
                // Show name entry dialog
                Stage stage = (Stage) root.getScene().getWindow();
                NameEntryDialog nameDialog = new NameEntryDialog();
                String playerName = nameDialog.showDialog(stage, finalScore, rank);
                
                if (playerName != null) {
                    // Save the high score
                    HighScore highScore = new HighScore(playerName, finalScore, totalLines, fieldSize);
                    highScoreManager.addHighScore(highScore);
                    
                    // Show high scores screen
                    mainApp.showHighScoresScreen();
                    return;
                }
            }
        }
        
        // If no high score achieved or user canceled, return to main menu
        mainApp.showMainMenuScreen();
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

    // Event listener for Player 2 game
    private class Player2EventListener implements TetrisGame.GameEventListener {
        @Override
        public void onGridUpdated() {
            renderPlayer2GameGrid();
        }

        @Override
        public void onGameOver() {
            System.out.println("Player 2 Game Over!");
            // Handle 2-player game over logic here
            if (isDualPlayerMode) {
                // Player 1 wins if Player 2 loses first
                handleDualPlayerGameOver(true); // Player 1 wins
            }
        }

        @Override
        public void onLinesClearedUpdate(int lines) {
            if (player2ScorePanel != null) {
                player2ScorePanel.updateLines(lines);
            }
        }

        @Override
        public void onScoreUpdate(int score) {
            if (player2ScorePanel != null) {
                player2ScorePanel.updateScore(score);
            }
        }

        @Override
        public void onNextPieceUpdate(TetrisPiece nextPiece) {
            if (player2ScorePanel != null) {
                player2ScorePanel.updateNextPiece(nextPiece);
            }
        }
    }

    // Handle game over in dual player mode
    private void handleDualPlayerGameOver(boolean player1Wins) {
        // Stop both games
        if (game != null) {
            game.stopGame();
        }
        if (player2Game != null) {
            player2Game.stopGame();
        }

        // Stop animation timer
        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Show winner dialog
        String winner = player1Wins ? "Player 1" : "Player 2";
        int player1Score = game != null ? game.getScore() : 0;
        int player2Score = player2Game != null ? player2Game.getScore() : 0;

        System.out.println(winner + " Wins! P1: " + player1Score + " P2: " + player2Score);

        // For now, just return to main menu
        // In future, could show winner screen with both scores
        mainApp.showMainMenuScreen();
    }
}

