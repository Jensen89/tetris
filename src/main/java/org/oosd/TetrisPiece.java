package org.oosd;

import javafx.scene.paint.Color;

abstract class TetrisPiece {
    protected int x, y;
    protected int rotation;
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

// I-piece (straight line)
class IPiece extends TetrisPiece {
    public IPiece(int startX, int startY) {
        super(startX, startY, Color.CYAN);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][] {
                // Rotation 0 - horizontal
                {{1, 1, 1, 1}},

                // Rotation 1 - vertical
                {{1}, {1}, {1}, {1}},

                // Rotation 2 - horizontal (same as 0)
                {{1, 1, 1, 1}},

                // Rotation 3 - vertical (same as 1)
                {{1}, {1}, {1}, {1}}
        };
    }
}

// O-piece (square)
class OPiece extends TetrisPiece {
    public OPiece(int startX, int startY) {
        super(startX, startY, Color.YELLOW);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][] {
                // All rotations are the same for square
                {{1, 1}, {1, 1}},
                {{1, 1}, {1, 1}},
                {{1, 1}, {1, 1}},
                {{1, 1}, {1, 1}}
        };
    }
}

// T-piece
class TPiece extends TetrisPiece {
    public TPiece(int startX, int startY) {
        super(startX, startY, Color.PURPLE);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][] {
                // Rotation 0 - T pointing up
                {{0, 1, 0}, {1, 1, 1}},

                // Rotation 1 - T pointing right
                {{1, 0}, {1, 1}, {1, 0}},

                // Rotation 2 - T pointing down
                {{1, 1, 1}, {0, 1, 0}},

                // Rotation 3 - T pointing left
                {{0, 1}, {1, 1}, {0, 1}}
        };
    }
}

// S-piece
class SPiece extends TetrisPiece {
    public SPiece(int startX, int startY) {
        super(startX, startY, Color.GREEN);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][] {
                // Rotation 0 - horizontal S
                {{0, 1, 1}, {1, 1, 0}},

                // Rotation 1 - vertical S
                {{1, 0}, {1, 1}, {0, 1}},

                // Rotation 2 - horizontal S (same as 0)
                {{0, 1, 1}, {1, 1, 0}},

                // Rotation 3 - vertical S (same as 1)
                {{1, 0}, {1, 1}, {0, 1}}
        };
    }
}

// Z-piece
class ZPiece extends TetrisPiece {
    public ZPiece(int startX, int startY) {
        super(startX, startY, Color.RED);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][] {
                // Rotation 0 - horizontal Z
                {{1, 1, 0}, {0, 1, 1}},

                // Rotation 1 - vertical Z
                {{0, 1}, {1, 1}, {1, 0}},

                // Rotation 2 - horizontal Z (same as 0)
                {{1, 1, 0}, {0, 1, 1}},

                // Rotation 3 - vertical Z (same as 1)
                {{0, 1}, {1, 1}, {1, 0}}
        };
    }
}

// J-piece
class JPiece extends TetrisPiece {
    public JPiece(int startX, int startY) {
        super(startX, startY, Color.BLUE);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][] {
                // Rotation 0 - J normal
                {{1, 0, 0}, {1, 1, 1}},

                // Rotation 1 - J rotated 90°
                {{1, 1}, {1, 0}, {1, 0}},

                // Rotation 2 - J rotated 180°
                {{1, 1, 1}, {0, 0, 1}},

                // Rotation 3 - J rotated 270°
                {{0, 1}, {0, 1}, {1, 1}}
        };
    }
}

// L-piece
class LPiece extends TetrisPiece {
    public LPiece(int startX, int startY) {
        super(startX, startY, Color.ORANGE);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][] {
                // Rotation 0 - L normal
                {{0, 0, 1}, {1, 1, 1}},

                // Rotation 1 - L rotated 90°
                {{1, 0}, {1, 0}, {1, 1}},

                // Rotation 2 - L rotated 180°
                {{1, 1, 1}, {1, 0, 0}},

                // Rotation 3 - L rotated 270°
                {{1, 1}, {0, 1}, {0, 1}}
        };
    }
}

// Utility class to create random pieces
class TetrominoFactory {
    private static final Class<?>[] PIECE_TYPES = {
            IPiece.class, OPiece.class, TPiece.class, SPiece.class,
            ZPiece.class, JPiece.class, LPiece.class
    };

    public static TetrisPiece createRandomPiece(int startX, int startY) {
        int randomIndex = (int) (Math.random() * PIECE_TYPES.length);

        try {
            return (TetrisPiece) PIECE_TYPES[randomIndex]
                    .getConstructor(int.class, int.class)
                    .newInstance(startX, startY);
        } catch (Exception e) {
            // Fallback to I-piece if creation fails
            return new IPiece(startX, startY);
        }
    }

    public static TetrisPiece createPiece(String type, int startX, int startY) {
        return switch (type.toUpperCase()) {
            case "O" -> new OPiece(startX, startY);
            case "T" -> new TPiece(startX, startY);
            case "S" -> new SPiece(startX, startY);
            case "Z" -> new ZPiece(startX, startY);
            case "J" -> new JPiece(startX, startY);
            case "L" -> new LPiece(startX, startY);
            default -> new IPiece(startX, startY);
        };
    }
}