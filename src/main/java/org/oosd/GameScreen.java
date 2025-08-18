package org.oosd;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameScreen {
    private final StackPane root;
    private final Main mainApp;

    // Game components
    private TetrisPiece currentPiece;
    private TetrisPiece nextPiece;
    private AnimationTimer gameLoop;
    private long lastFallTime = 0;
    private long fallInterval = 1_000_000_000L; // 1 second in nanoseconds
    private boolean gameRunning = false;

    // Game area dimensions
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 20;
    private static final int BLOCK_SIZE = 30;

    // Track game state with grid
    private int[][] gameGrid = new int[GRID_HEIGHT][GRID_WIDTH];
    private Pane gameArea;

    public GameScreen(StackPane root, Main mainApp) {
        this.root = root;
        this.mainApp = mainApp;
    }

    public void show() {
        // Main container
        VBox gameScreen = new VBox(20);
        gameScreen.setPadding(new Insets(20));
        gameScreen.setAlignment(Pos.TOP_CENTER);

        final int GAME_WIDTH = GRID_WIDTH * BLOCK_SIZE;
        final int GAME_HEIGHT = GRID_HEIGHT * BLOCK_SIZE;

        // Game pane with temp static size - add ability to change variables later
        gameArea = new Pane();
        gameArea.setPrefSize(GAME_WIDTH, GAME_HEIGHT);
        gameArea.setMaxSize(GAME_WIDTH, GAME_HEIGHT);
        gameArea.setMinSize(GAME_WIDTH, GAME_HEIGHT);

        // Temp game area styling
        gameArea.setStyle(
                "-fx-border-color: #333333;" +
                        "-fx-border-width: 3;" +
                        "-fx-background-color: #f0f0f0;"
        );

        initialiseGameGrid();

        Label gameTitle = new Label("PLAY");

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> {
            stopGame(); // Stop the game when going back
            mainApp.showMainMenuScreen();
        });

        gameScreen.getChildren().addAll(gameTitle, gameArea, backButton);

        root.getChildren().setAll(gameScreen);

        setupKeyboardControls(gameScreen);

        renderGameGrid();

        startGame();
    }

    private void initialiseGameGrid() {
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                gameGrid[row][col] = 0; // 0 = empty
            }
        }
    }

    private void renderGameGrid() {
        // Clear existing visual blocks
        gameArea.getChildren().removeIf(node -> node instanceof Rectangle);

        // Draw fixed blocks from game grid
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (gameGrid[row][col] == 1) {
                    Rectangle block = createBlock(col, row, Color.LIGHTGRAY);
                    gameArea.getChildren().add(block);
                }
            }
        }

        // Draw current falling piece
        if (currentPiece != null) {
            int[][] positions = currentPiece.getOccupiedPositions();
            for (int[] pos : positions) {
                if (pos[0] >= 0 && pos[0] < GRID_WIDTH && pos[1] >= 0 && pos[1] < GRID_HEIGHT) {
                    Rectangle block = createBlock(pos[0], pos[1], currentPiece.getColor());
                    gameArea.getChildren().add(block);
                }
            }
        }
    }

    // Check if a piece can be placed at a specific position
    private boolean canPlacePiece(TetrisPiece piece, int offsetX, int offsetY, int rotation) {
        // Temporarily modify piece position and rotation
        int originalX = piece.getX();
        int originalY = piece.getY();
        int originalRotation = piece.getRotation();

        piece.x = originalX + offsetX;
        piece.y = originalY + offsetY;
        piece.rotation = rotation;

        int[][] positions = piece.getOccupiedPositions();
        boolean canPlace = true;

        // Check each block position
        for (int[] pos : positions) {
            int gridX = pos[0];
            int gridY = pos[1];

            // Check bounds
            if (gridX < 0 || gridX >= GRID_WIDTH || gridY >= GRID_HEIGHT) {
                canPlace = false;
                break;
            }

            // Check collision with existing blocks (allow negative Y for pieces entering from top)
            if (gridY >= 0 && gameGrid[gridY][gridX] == 1) {
                canPlace = false;
                break;
            }
        }

        // Restore original position and rotation
        piece.x = originalX;
        piece.y = originalY;
        piece.rotation = originalRotation;

        return canPlace;
    }

    // Move current piece left
    private void movePieceLeft() {
        if (currentPiece != null && canPlacePiece(currentPiece, -1, 0, currentPiece.getRotation())) {
            currentPiece.moveLeft();
            renderGameGrid();
        }
    }

    // Move current piece right
    private void movePieceRight() {
        if (currentPiece != null && canPlacePiece(currentPiece, 1, 0, currentPiece.getRotation())) {
            currentPiece.moveRight();
            renderGameGrid();
        }
    }

    // Move current piece down
    private boolean movePieceDown() {
        if (currentPiece != null && canPlacePiece(currentPiece, 0, 1, currentPiece.getRotation())) {
            currentPiece.moveDown();
            renderGameGrid();
            return true;
        }
        return false; // Piece cannot move down (landed)
    }

    // Hard drop - instantly drop piece to the bottom
    private void hardDropPiece() {
        if (currentPiece == null) return;

        // Keep moving down until piece can't move anymore
        while (movePieceDown()) {
            // Continue dropping
        }

        // Lock the piece and spawn new one
        lockPiece();
        spawnNewPiece();
    }

    // Rotate current piece
    private void rotatePiece() {
        if (currentPiece != null) {
            int newRotation = (currentPiece.getRotation() + 1) % 4;
            if (canPlacePiece(currentPiece, 0, 0, newRotation)) {
                currentPiece.rotate();
                renderGameGrid();
            }
        }
    }

    // Lock current piece into the game grid
    private void lockPiece() {
        if (currentPiece == null) return;

        int[][] positions = currentPiece.getOccupiedPositions();
        for (int[] pos : positions) {
            int gridX = pos[0];
            int gridY = pos[1];
            if (gridY >= 0 && gridY < GRID_HEIGHT && gridX >= 0 && gridX < GRID_WIDTH) {
                gameGrid[gridY][gridX] = 1;
            }
        }

        // Clear any complete lines
        int linesCleared = clearCompleteLines();
        if (linesCleared > 0) {
            // Here you could update score, play sound, etc.
            System.out.println("Cleared " + linesCleared + " lines!");
        }

        currentPiece = null;
    }

    // Spawn a new piece
    private void spawnNewPiece() {
        if (nextPiece != null) {
            currentPiece = nextPiece;
        } else {
            currentPiece = TetrominoFactory.createRandomPiece(GRID_WIDTH / 2 - 1, -1);
        }

        nextPiece = TetrominoFactory.createRandomPiece(GRID_WIDTH / 2 - 1, -1);

        // Check if game over (new piece can't be placed)
        if (!canPlacePiece(currentPiece, 0, 0, currentPiece.getRotation())) {
            gameOver();
            return;
        }

        renderGameGrid();
    }

    // Start the game loop
    private void startGame() {
        gameRunning = true;
        spawnNewPiece();

        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameRunning) {
                    stop();
                    return;
                }

                // Check if it's time for the piece to fall
                if (now - lastFallTime >= fallInterval) {
                    if (currentPiece != null) {
                        if (!movePieceDown()) {
                            // Piece can't move down, lock it and spawn new one
                            lockPiece();
                            spawnNewPiece();
                        }
                    } else {
                        // No current piece, spawn one
                        spawnNewPiece();
                    }
                    lastFallTime = now;
                }
            }
        };

        gameLoop.start();
    }

    // Stop the game loop
    private void stopGame() {
        gameRunning = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    // Pause/Resume game
    private void togglePause() {
        if (gameRunning) {
            stopGame();
            System.out.println("Game Paused - Press ESC to resume");
        } else {
            startGame();
            System.out.println("Game Resumed");
        }
    }

    // Game over handling
    private void gameOver() {
        stopGame();
        System.out.println("Game Over! Press R to restart");
        currentPiece = null;
        nextPiece = null;
    }

    // Restart the game
    private void restartGame() {
        initialiseGameGrid();
        currentPiece = null;
        nextPiece = null;
        renderGameGrid();
        startGame();
    }

    private Rectangle createBlock(int gridX, int gridY, Color color) {
        Rectangle block = new Rectangle(BLOCK_SIZE, BLOCK_SIZE);
        block.setFill(color);
        block.setStroke(Color.DARKGRAY);
        block.setStrokeWidth(1);

        // Position based on grid coordinates
        block.setX(gridX * BLOCK_SIZE);
        block.setY(gridY * BLOCK_SIZE);

        return block;
    }

    // Check if a position is valid (within bounds and empty)
    private boolean isValidPosition(int gridX, int gridY) {
        return gridX >= 0 && gridX < GRID_WIDTH &&
                gridY >= 0 && gridY < GRID_HEIGHT &&
                gameGrid[gridY][gridX] == 0;
    }

    // Check if a line is complete
    private boolean isLineComplete(int row) {
        for (int col = 0; col < GRID_WIDTH; col++) {
            if (gameGrid[row][col] == 0) {
                return false;
            }
        }
        return true;
    }

    // Clear a complete line and move everything down
    private void clearLine(int lineRow) {
        // Move all rows above down by one
        for (int row = lineRow; row > 0; row--) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                gameGrid[row][col] = gameGrid[row - 1][col];
            }
        }

        // Clear the top row
        for (int col = 0; col < GRID_WIDTH; col++) {
            gameGrid[0][col] = 0;
        }

        renderGameGrid();
    }

    // Check and clear all complete lines
    private int clearCompleteLines() {
        int linesCleared = 0;

        // Check from bottom to top
        for (int row = GRID_HEIGHT - 1; row >= 0; row--) {
            if (isLineComplete(row)) {
                clearLine(row);
                linesCleared++;
                row++; // Check this row again since everything moved down
            }
        }

        return linesCleared;
    }

    // Set up keyboard event handling
    private void setupKeyboardControls(VBox gameScreenContainer) {
        gameScreenContainer.setFocusTraversable(true);
        gameScreenContainer.requestFocus();

        gameScreenContainer.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case LEFT:
                    movePieceLeft();
                    break;
                case RIGHT:
                    movePieceRight();
                    break;
                case DOWN:
                    // Soft drop - move down one step
                    if (!movePieceDown()) {
                        lockPiece();
                        spawnNewPiece();
                    }
                    break;
                case UP:
                    rotatePiece();
                    break;
                case SPACE:
                    // Hard drop - instantly drop to bottom
                    hardDropPiece();
                    break;
                case R:
                    // Restart game
                    restartGame();
                    break;
                case ESCAPE:
                    // Pause/Resume game
                    togglePause();
                    break;
            }
            event.consume(); // Prevent event from bubbling up
        });
    }
}