package org.oosd.game.pieces;

import javafx.scene.paint.Color;

// J-piece
class JPiece extends TetrisPiece {
    public JPiece(int startX, int startY) {
        super(startX, startY, Color.BLUE);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][]{
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
