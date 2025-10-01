package org.oosd.game;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.oosd.game.pieces.TetrisPiece;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

public class TetrisGameTest {

    private TetrisGame game;

    @BeforeEach
    void setUp() {
        game = new TetrisGame(10, 20);
    }

    //Test 1: Initial Game State
    @Test
    void testInitialGameState() {
        assertEquals(0, game.getScore());
        assertEquals(0, game.getTotalLinesCleared());
        assertFalse(game.isGameRunning());
        assertFalse(game.isGameStarted());
        assertFalse(game.isGamePaused());
    }

    //Test 2: Game State Management
    @Test
    void testGameStateTransitions() {
        assertFalse(game.isGameRunning());
        assertFalse(game.isGameStarted());

        game.startGame();
        assertTrue(game.isGameRunning());
        assertTrue(game.isGameStarted());
        assertFalse(game.isGamePaused());

        game.pauseGame();
        assertTrue(game.isGamePaused());

        game.resumeGame();
        assertFalse(game.isGamePaused());

        game.stopGame();
        assertFalse(game.isGameRunning());
    }

    //Test 3: Piece Movement Boundaries
    @Test
    void testPieceCannotMoveOutOfBounds() {
        game.startGame();

        //Try to move piece far left
        for (int i = 0; i < 20; i++) {
            game.moveLeft();
        }

        //Piece should still be within grid bounds
        int[][] positions = game.getCurrentPiece().getOccupiedPositions();
        for (int[] pos : positions) {
            assertTrue(pos[0] >= 0, "Piece X position should not be negative");
            assertTrue(pos[0] < game.getGridWidth(), "Piece should not exceed grid width");
        }
    }

    //Test 4: Grid Initialization
    @Test
    void testGridInitialization() {
        int[][] grid = game.getGrid();

        //All cells should be empty (0) initially
        for (int row = 0; row < game.getGridHeight(); row++) {
            for (int col = 0; col < game.getGridWidth(); col++) {
                assertEquals(0, grid[row][col],
                        "Grid cell at [" + row + "][" + col + "] should be empty");
            }
        }
    }

    //Test 5: Restart Game Functionality
    @Test
    void testRestartGame() {
        game.startGame();

        //Simulate scoring
        int[][] grid = game.getGrid();
        grid[19][0] = 1; // Add some blocks

        //Play for a bit
        game.tick();
        game.tick();

        //Restart
        game.restartGame();

        //Check everything is reset
        assertEquals(0, game.getScore());
        assertEquals(0, game.getTotalLinesCleared());
        assertTrue(game.isGameStarted());
        assertFalse(game.isGamePaused());

        //Grid should be empty
        int[][] newGrid = game.getGrid();
        for (int row = 0; row < game.getGridHeight(); row++) {
            for (int col = 0; col < game.getGridWidth(); col++) {
                assertEquals(0, newGrid[row][col]);
            }
        }
    }

    //Test 6: Parameterized Test for Line Score Calculation
    @ParameterizedTest
    @CsvSource({
            "1, 100",   //Single line = 100 points
            "2, 300",   //Double line = 300 points
            "3, 500",   //Triple line = 500 points
            "4, 800",   //Tetris (4 lines) = 800 points
            "0, 0"      //No lines = 0 points
    })
    void testLineScoreCalculation(int linesCleared, int expectedScore) throws Exception {
        //Use reflection to access the private calculateLineScore method
        Method method = TetrisGame.class.getDeclaredMethod("calculateLineScore", int.class);
        method.setAccessible(true);

        int actualScore = (int) method.invoke(game, linesCleared);

        assertEquals(expectedScore, actualScore,
                "Score for " + linesCleared + " lines should be " + expectedScore);
    }

    //Test 7: Test Double - Stub for GameEventListener
    @Test
    void testGameNotifiesListenerOnStart() {
        //Create a stub listener that just stores the data
        StubGameEventListener stubListener = new StubGameEventListener();

        game.setEventListener(stubListener);
        game.startGame();

        //Verify the listener was notified
        assertTrue(stubListener.gridUpdated, "Grid should be updated on game start");
    }

    //Stub implementation - inner class in your test file
    class StubGameEventListener implements TetrisGame.GameEventListener {
        boolean gridUpdated = false;
        boolean gameOverCalled = false;
        int lastScore = -1;
        int lastLines = -1;
        TetrisPiece lastNextPiece = null;

        @Override
        public void onGridUpdated() {
            gridUpdated = true;
        }

        @Override
        public void onGameOver() {
            gameOverCalled = true;
        }

        @Override
        public void onLinesClearedUpdate(int lines) {
            lastLines = lines;  //Store the value
        }

        @Override
        public void onScoreUpdate(int score) {
            lastScore = score;
        }

        @Override
        public void onNextPieceUpdate(TetrisPiece nextPiece) {
            lastNextPiece = nextPiece;
        }
    }

    //Test 8: Mock with Mockito - Verify listener interactions
    @Test
    void testGameNotifiesListenerWithMock() {
        //Create a mock listener using Mockito
        TetrisGame.GameEventListener mockListener = mock(TetrisGame.GameEventListener.class);

        game.setEventListener(mockListener);
        game.startGame();

        //Verify that onGridUpdated was called at least once
        verify(mockListener, atLeastOnce()).onGridUpdated();

        //Verify that onNextPieceUpdate was called (because startGame spawns a piece)
        verify(mockListener, atLeastOnce()).onNextPieceUpdate(any());
    }
}