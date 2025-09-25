package org.oosd.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.oosd.game.pieces.TetrisPiece;

public class NextPiecePreview extends VBox {
    
    private Label nextLabel;
    private Pane previewArea;
    
    private static final int PREVIEW_BLOCK_SIZE = 20;
    private static final int PREVIEW_AREA_SIZE = 100;
    
    public NextPiecePreview() {
        setupUI();
    }
    
    private void setupUI() {
        setSpacing(10);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);
        
        nextLabel = new Label("NEXT");
        nextLabel.getStyleClass().add("next-label");
        
        previewArea = new Pane();
        previewArea.setPrefSize(PREVIEW_AREA_SIZE, PREVIEW_AREA_SIZE);
        previewArea.setMaxSize(PREVIEW_AREA_SIZE, PREVIEW_AREA_SIZE);
        previewArea.setMinSize(PREVIEW_AREA_SIZE, PREVIEW_AREA_SIZE);
        
        previewArea.setStyle(
                "-fx-border-color: #666666;" +
                "-fx-border-width: 1;" +
                "-fx-background-color: #f8f8f8;"
        );
        
        getChildren().addAll(nextLabel, previewArea);
        getStyleClass().add("next-piece-preview");
    }
    
    public void updateNextPiece(TetrisPiece nextPiece) {
        previewArea.getChildren().clear();
        
        if (nextPiece == null) {
            return;
        }
        
        // Get the piece shape at rotation 0 (default orientation)
        int originalRotation = nextPiece.getRotation();
        nextPiece.rotation = 0; // Temporarily set to 0 for preview
        
        int[][] positions = nextPiece.getOccupiedPositions();
        
        // Calculate bounds to center the piece in preview area
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        
        for (int[] pos : positions) {
            minX = Math.min(minX, pos[0]);
            maxX = Math.max(maxX, pos[0]);
            minY = Math.min(minY, pos[1]);
            maxY = Math.max(maxY, pos[1]);
        }
        
        // Calculate offset to center the piece
        int pieceWidth = (maxX - minX + 1) * PREVIEW_BLOCK_SIZE;
        int pieceHeight = (maxY - minY + 1) * PREVIEW_BLOCK_SIZE;
        int offsetX = (PREVIEW_AREA_SIZE - pieceWidth) / 2 - minX * PREVIEW_BLOCK_SIZE;
        int offsetY = (PREVIEW_AREA_SIZE - pieceHeight) / 2 - minY * PREVIEW_BLOCK_SIZE;
        
        // Draw the piece blocks
        for (int[] pos : positions) {
            Rectangle block = createPreviewBlock(pos[0], pos[1], offsetX, offsetY, nextPiece.getColor());
            previewArea.getChildren().add(block);
        }
        
        // Restore original rotation
        nextPiece.rotation = originalRotation;
    }
    
    private Rectangle createPreviewBlock(int gridX, int gridY, int offsetX, int offsetY, Color color) {
        Rectangle block = new Rectangle(PREVIEW_BLOCK_SIZE, PREVIEW_BLOCK_SIZE);
        block.setFill(color);
        block.setStroke(Color.DARKGRAY);
        block.setStrokeWidth(0.5);
        
        block.setX(gridX * PREVIEW_BLOCK_SIZE + offsetX);
        block.setY(gridY * PREVIEW_BLOCK_SIZE + offsetY);
        
        return block;
    }
    
    public void clear() {
        previewArea.getChildren().clear();
    }
}