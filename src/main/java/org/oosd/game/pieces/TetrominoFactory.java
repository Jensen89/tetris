package org.oosd.game.pieces;

import java.util.Random;

// Factory class to create Tetris pieces with support for synchronized generation
public class TetrominoFactory {
    public static final Class<?>[] PIECE_TYPES = {
            IPiece.class, OPiece.class, TPiece.class, SPiece.class,
            ZPiece.class, JPiece.class, LPiece.class
    };

    private static Random random = new Random();

    // Set a specific seed for synchronized piece generation (2-player mode)
    public static void setSeed(long seed) {
        random = new Random(seed);
    }

    // Reset to truly random generation (single-player mode)
    public static void setRandomMode() {
        random = new Random();
    }

    public static TetrisPiece createRandomPiece(int startX, int startY) {
        int randomIndex = random.nextInt(PIECE_TYPES.length);

        try {
            return (TetrisPiece) PIECE_TYPES[randomIndex]
                    .getConstructor(int.class, int.class)
                    .newInstance(startX, startY);
        } catch (Exception e) {
            // Fallback to I-piece if creation fails
            return new IPiece(startX, startY);
        }
    }
}
