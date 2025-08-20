package org.oosd.game.pieces;

import javafx.scene.paint.Color;

// O-piece (square)
class OPiece extends TetrisPiece {
    public OPiece(int startX, int startY) {
        super(startX, startY, Color.YELLOW);
    }

    @Override
    protected void initialiseShapes() {
        shapes = new int[][][]{
                // All rotations are the same for square
                {{1, 1}, {1, 1}},
                {{1, 1}, {1, 1}},
                {{1, 1}, {1, 1}},
                {{1, 1}, {1, 1}}
        };
    }
}
