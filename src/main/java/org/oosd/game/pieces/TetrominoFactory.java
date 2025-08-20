package org.oosd.game.pieces;

// Utility class to create random pieces
public class TetrominoFactory {
    public static final Class<?>[] PIECE_TYPES = {
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
}
