package org.oosd.game.pieces;

import javafx.scene.paint.Color;

// L-piece
class LPiece extends TetrisPiece {
    public LPiece(int startX, int startY) {
        super(startX, startY, Color.ORANGE);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][]{
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
