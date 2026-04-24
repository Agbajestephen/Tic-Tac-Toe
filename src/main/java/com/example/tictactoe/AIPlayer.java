package com.example.tictactoe;

import java.util.ArrayList;
import java.util.List;

public class AIPlayer {

    public enum Difficulty { EASY, HARD }

    private Difficulty difficulty;

    public AIPlayer(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public int[] getBestMove(String[][] board) {
        return difficulty == Difficulty.EASY
                ? getRandomMove(board)
                : getMinimaxMove(board);
    }

    private int[] getRandomMove(String[][] board) {
        List<int[]> available = new ArrayList<>();
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c].isEmpty()) available.add(new int[]{r, c});
        if (available.isEmpty()) return null;
        return available.get((int)(Math.random() * available.size()));
    }

    private int[] getMinimaxMove(String[][] board) {
        int bestScore = Integer.MIN_VALUE;
        int[] bestMove = null;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[r][c].isEmpty()) {
                    board[r][c] = "O";
                    int score = minimax(board, false, 0);
                    board[r][c] = "";
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new int[]{r, c};
                    }
                }
            }
        }
        return bestMove;
    }

    private int minimax(String[][] board, boolean isMaximizing, int depth) {
        String result = checkWinner(board);
        if (result != null) {
            return switch (result) {
                case "O"    -> 10 - depth;
                case "X"    -> depth - 10;
                default     -> 0;
            };
        }
        if (isMaximizing) {
            int best = Integer.MIN_VALUE;
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    if (board[r][c].isEmpty()) {
                        board[r][c] = "O";
                        best = Math.max(best, minimax(board, false, depth + 1));
                        board[r][c] = "";
                    }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int r = 0; r < 3; r++)
                for (int c = 0; c < 3; c++)
                    if (board[r][c].isEmpty()) {
                        board[r][c] = "X";
                        best = Math.min(best, minimax(board, true, depth + 1));
                        board[r][c] = "";
                    }
            return best;
        }
    }

    private String checkWinner(String[][] board) {
        for (int i = 0; i < 3; i++) {
            if (!board[i][0].isEmpty() && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) return board[i][0];
            if (!board[0][i].isEmpty() && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) return board[0][i];
        }
        if (!board[0][0].isEmpty() && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) return board[0][0];
        if (!board[0][2].isEmpty() && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) return board[0][2];
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                if (board[r][c].isEmpty()) return null;
        return "draw";
    }
}