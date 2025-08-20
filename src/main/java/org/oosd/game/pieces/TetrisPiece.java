package org.oosd.game.pieces;

import javafx.scene.paint.Color;

public abstract class TetrisPiece {
    public int x, y;
    public int rotation;
    protected Color color;
    protected int [][][] shapes;

    public TetrisPiece(int startX, int startY, Color color) {

        this.x = startX;
        this.y = startY;
        this.color = color;
        this.rotation = 0;
        initialiseShapes();

    }

    protected abstract void initialiseShapes();

    // Get current shape based on rotation
    public int[][] getCurrentShape() {
        return shapes[rotation];
    }

    // Get all occupied positions for current piece
    public int[][] getOccupiedPositions() {
        int[][] shape = getCurrentShape();
        int[][] positions = new int[4][2]; // Max 4 blocks per piece
        int count = 0;

        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] == 1) {
                    positions[count][0] = x + col; // grid X
                    positions[count][1] = y + row; // grid Y
                    count++;
                }
            }
        }

        // Trim array to actual size
        int[][] result = new int[count][2];
        System.arraycopy(positions, 0, result, 0, count);
        return result;
    }

    // Movement methods
    public void moveLeft() { x--; }
    public void moveRight() { x++; }
    public void moveDown() { y++; }
    public void rotate() { rotation = (rotation + 1) % 4; }

    // Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    public int getRotation() { return rotation; }

}

