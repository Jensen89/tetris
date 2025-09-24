package org.oosd.game;

import org.oosd.game.pieces.TetrisPiece;
import org.oosd.game.pieces.TetrominoFactory;

import java.awt.*;
import java.util.Objects;

public class TetrisGame {

    //Game state
    private TetrisPiece currentPiece;
    private TetrisPiece nextPiece;
    private boolean gameRunning = false;
    private boolean gamePaused = false;
    private boolean gameStarted = false;
    private int score = 0;
    private int totalLinesCleared = 0;

    //Game dimensions
    private final int gridWidth;
    private final int gridHeight;
    private int[][] gameGrid;

    //Game listener for UI updates
    private GameEventListener listener;

    public interface GameEventListener {
        void onGridUpdated();
        void onGameOver();
        void onLinesClearedUpdate(int lines);
        void onScoreUpdate(int score);
    }

    public TetrisGame(int gridWidth, int gridHeight) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.gameGrid = new int[gridHeight][gridWidth];
        initialiseGrid();
    }

    public void setEventListener(GameEventListener listener) {
        this.listener = listener;
    }

    private void initialiseGrid() {
        for (int row = 0; row < gridHeight; row++) {
            for (int col = 0; col < gridWidth; col++) {
                gameGrid[row][col] = 0;
            }
        }
    }


    // Start the game (initial start)
    public void startGame() {
        gameRunning = true;
        gamePaused = false;
        gameStarted = true;

        // Only spawn a new piece if we don't have one
        if (currentPiece == null) {
            spawnNewPiece();
        }

        notifyGridUpdate();
    }

    // Restart the game
    public void restartGame() {
        initialiseGrid();
        currentPiece = null;
        nextPiece = null;
        gameStarted = false;
        gamePaused = false;
        score = 0;
        totalLinesCleared = 0;
        notifyScoreUpdate();
        notifyGridUpdate();
        startGame();
    }

    // Pause the game (maintains state)
    public void pauseGame() {
        gamePaused = true;
        System.out.println("Game Paused");
    }

    // Resume the game (continues from paused state)
    public void resumeGame() {
        gamePaused = false;
        System.out.println("Game Resumed");
    }

    // Stop the game loop completely
    public void stopGame() {
        gameRunning = false;
        gamePaused = false;
        gameStarted = false;
    }

    // Toggle pause/resume
    public void togglePause() {
        if (!gameStarted) {
            return;
        }

        if (gamePaused) {
            resumeGame();
        } else {
            pauseGame();
        }
    }

    // Movement methods - leaving return methods for future use
    public boolean moveLeft() {
        if (currentPiece != null && canPlacePiece(currentPiece, -1, 0, currentPiece.getRotation())) {
            currentPiece.moveLeft();
            notifyGridUpdate();
            return true;
        }
        return false;
    }

    public boolean moveRight() {
        if (currentPiece != null && canPlacePiece(currentPiece, 1, 0, currentPiece.getRotation())) {
            currentPiece.moveRight();
            notifyGridUpdate();
            return true;
        }
        return false;
    }

    public boolean moveDown() {
        if (currentPiece != null && canPlacePiece(currentPiece, 0, 1, currentPiece.getRotation())) {
            currentPiece.moveDown();
            notifyGridUpdate();
            return true;
        }
        return false;
    }

    // Hard drop - instantly drop piece to the bottom
    public void hardDrop() {
        if (currentPiece == null) return;

        // Keep moving down until piece can't move anymore
        while (moveDown()) {
            // moveDown handles the actual movement
        }

        // Lock the piece and spawn new one
        lockPiece();
        spawnNewPiece();
    }

    // Rotate current piece
    public boolean rotate() {
        if (currentPiece != null) {
            int newRotation = (currentPiece.getRotation() + 1) % 4;
            if (canPlacePiece(currentPiece, 0, 0, newRotation)) {
                currentPiece.rotate();
                notifyGridUpdate();
                return true;
            }
        }
        return false;
    }

    // Game tick - called by timer
    public void tick() {
        if (!gameRunning || gamePaused) {
            return;
        }

        if (currentPiece != null) {
            if (!moveDown()) {
                // Piece can't move down, lock it and spawn new one
                lockPiece();
                spawnNewPiece();
            }
        } else {
            // No current piece, spawn one
            spawnNewPiece();
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
            if (gridX < 0 || gridX >= gridWidth || gridY >= gridHeight) {
                canPlace = false;
                break;
            }

            // Check collision with existing blocks
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

    // Lock current piece into the game grid
    private void lockPiece() {
        if (currentPiece == null) return;

        int[][] positions = currentPiece.getOccupiedPositions();
        for (int[] pos : positions) {
            int gridX = pos[0];
            int gridY = pos[1];
            if (gridY >= 0 && gridY < gridHeight && gridX >= 0 && gridX < gridWidth) {
                gameGrid[gridY][gridX] = 1;
            }
        }

        // Clear any complete lines
        int linesCleared = clearCompleteLines();
        if (linesCleared > 0) {
            totalLinesCleared += linesCleared;
            int lineScore = calculateLineScore(linesCleared);
            score += lineScore;
            System.out.println("Cleared " + linesCleared + " lines! Score: +" + lineScore);
            if (listener != null) {
                listener.onLinesClearedUpdate(totalLinesCleared);
                listener.onScoreUpdate(score);
            }
        }

        currentPiece = null;
    }

    // Spawn a new piece
    private void spawnNewPiece() {
        currentPiece = Objects.requireNonNullElseGet(nextPiece,
                () -> TetrominoFactory.createRandomPiece(gridWidth / 2 - 1, -1));

        nextPiece = TetrominoFactory.createRandomPiece(gridWidth / 2 - 1, -1);

        // Check if game over
        if (!canPlacePiece(currentPiece, 0, 0, currentPiece.getRotation())) {
            gameOver();
            return;
        }

        notifyGridUpdate();
    }

    // Game over handling
    private void gameOver() {
        stopGame();
        System.out.println("Game Over! Press R to restart");
        currentPiece = null;
        nextPiece = null;

        if (listener != null) {
            listener.onGameOver();
        }
    }

    // Check if a line is complete
    private boolean isLineComplete(int row) {
        for (int col = 0; col < gridWidth; col++) {
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
            System.arraycopy(gameGrid[row - 1], 0, gameGrid[row], 0, gridWidth);
        }

        // Clear the top row
        for (int col = 0; col < gridWidth; col++) {
            gameGrid[0][col] = 0;
        }

        notifyGridUpdate();
    }

    // Check and clear all complete lines
    private int clearCompleteLines() {
        int linesCleared = 0;

        // Check from bottom to top
        for (int row = gridHeight - 1; row >= 0; row--) {
            if (isLineComplete(row)) {
                clearLine(row);
                linesCleared++;
                row++; // Check this row again since everything moved down
            }
        }

        return linesCleared;
    }

    // Notification methods
    private void notifyGridUpdate() {
        if (listener != null) {
            listener.onGridUpdated();
        }
    }
    
    private void notifyScoreUpdate() {
        if (listener != null) {
            listener.onScoreUpdate(score);
            listener.onLinesClearedUpdate(totalLinesCleared);
        }
    }
    
    // Calculate score based on number of lines cleared
    private int calculateLineScore(int linesCleared) {
        return switch (linesCleared) {
            case 1 -> 100;   // Single
            case 2 -> 300;   // Double  
            case 3 -> 500;   // Triple
            case 4 -> 800;   // Tetris
            default -> 0;
        };
    }

    // Getters for UI to render
    public int[][] getGrid() {
        return gameGrid;
    }

    public TetrisPiece getCurrentPiece() {
        return currentPiece;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean isGamePaused() {
        return gamePaused;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public int getGridHeight() {
        return gridHeight;
    }
    
    public int getScore() {
        return score;
    }
    
    public int getTotalLinesCleared() {
        return totalLinesCleared;
    }
}
