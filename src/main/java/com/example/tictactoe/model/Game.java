package com.example.tictactoe.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

public class Game {

    public enum Status { IN_PROGRESS, X_WINS, O_WINS, DRAW }

    private final String id;
    private final String name;
    private final Instant startDate;
    private final char[] board; // 9 cells: 0-8, ' ' = empty
    private char currentPlayer;
    private Status status;

    public Game(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.startDate = Instant.now();
        this.board = new char[9];
        for (int i = 0; i < 9; i++) board[i] = ' ';
        this.currentPlayer = 'X';
        this.status = Status.IN_PROGRESS;
    }

    @JsonCreator
    public Game(
            @JsonProperty("id") String id,
            @JsonProperty("name") String name,
            @JsonProperty("startDate") Instant startDate,
            @JsonProperty("board") char[] board,
            @JsonProperty("currentPlayer") char currentPlayer,
            @JsonProperty("status") Status status) {
        this.id = id;
        this.name = name;
        this.startDate = startDate;
        this.board = board;
        this.currentPlayer = currentPlayer;
        this.status = status;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Instant getStartDate() { return startDate; }
    public char[] getBoard() { return board; }
    public char getCurrentPlayer() { return currentPlayer; }
    public Status getStatus() { return status; }

    public boolean makeMove(int position) {
        if (status != Status.IN_PROGRESS) return false;
        if (position < 0 || position > 8) return false;
        if (board[position] != ' ') return false;

        board[position] = currentPlayer;
        updateStatus();
        if (status == Status.IN_PROGRESS) {
            currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
        }
        return true;
    }

    private void updateStatus() {
        int[][] lines = {
            {0,1,2}, {3,4,5}, {6,7,8}, // rows
            {0,3,6}, {1,4,7}, {2,5,8}, // cols
            {0,4,8}, {2,4,6}            // diagonals
        };
        for (int[] line : lines) {
            char a = board[line[0]], b = board[line[1]], c = board[line[2]];
            if (a != ' ' && a == b && b == c) {
                status = (a == 'X') ? Status.X_WINS : Status.O_WINS;
                return;
            }
        }
        boolean full = true;
        for (char cell : board) {
            if (cell == ' ') { full = false; break; }
        }
        if (full) status = Status.DRAW;
    }
}
