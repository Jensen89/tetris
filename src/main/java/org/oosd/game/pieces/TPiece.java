package org.oosd.game.pieces;

import javafx.scene.paint.Color;

// T-piece
class TPiece extends TetrisPiece {
    public TPiece(int startX, int startY) {
        super(startX, startY, Color.PURPLE);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][]{
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
