package com.example.tictactoe;

import com.example.tictactoe.model.Game;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void newGameIsEmpty() {
        Game game = new Game("TestGame");
        assertEquals(Game.Status.IN_PROGRESS, game.getStatus());
        assertEquals('X', game.getCurrentPlayer());
        for (char c : game.getBoard()) assertEquals(' ', c);
    }

    @Test
    void xWinsHorizontal() {
        Game game = new Game("TestGame");
        // X: 0,1,2  O: 3,4
        assertTrue(game.makeMove(0));
        assertTrue(game.makeMove(3));
        assertTrue(game.makeMove(1));
        assertTrue(game.makeMove(4));
        assertTrue(game.makeMove(2));
        assertEquals(Game.Status.X_WINS, game.getStatus());
    }

    @Test
    void draw() {
        Game game = new Game("TestGame");
        // X O X
        // X X O
        // O X O
        int[] moves = {0, 1, 2, 5, 3, 6, 4, 8, 7};
        for (int m : moves) game.makeMove(m);
        assertEquals(Game.Status.DRAW, game.getStatus());
    }

    @Test
    void cannotMoveOnOccupiedCell() {
        Game game = new Game("TestGame");
        game.makeMove(4);
        assertFalse(game.makeMove(4));
    }

    @Test
    void cannotMoveAfterGameOver() {
        Game game = new Game("TestGame");
        game.makeMove(0); game.makeMove(3);
        game.makeMove(1); game.makeMove(4);
        game.makeMove(2); // X wins
        assertFalse(game.makeMove(5));
    }
}
