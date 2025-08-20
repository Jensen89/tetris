package org.oosd.game.pieces;

import javafx.scene.paint.Color;

// I-piece
class IPiece extends TetrisPiece {
    public IPiece(int startX, int startY) {
        super(startX, startY, Color.CYAN);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][]{
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
