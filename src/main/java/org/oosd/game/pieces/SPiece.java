package org.oosd.game.pieces;

import javafx.scene.paint.Color;

// S-piece
class SPiece extends TetrisPiece {
    public SPiece(int startX, int startY) {
        super(startX, startY, Color.GREEN);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][]{
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
