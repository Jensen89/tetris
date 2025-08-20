package org.oosd.game.pieces;

import javafx.scene.paint.Color;

// Z-piece
class ZPiece extends TetrisPiece {
    public ZPiece(int startX, int startY) {
        super(startX, startY, Color.RED);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][]{
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
