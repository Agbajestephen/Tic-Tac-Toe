package com.example.tictactoe;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

public class TileBoard {

    private StackPane  pane;
    private InfoCenter infoCenter;
    private Tile[][]   tiles = new Tile[3][3];
    private Line       winningLine;
    private TurnTimer  turnTimer;

    private char    playerTurn  = 'X';
    private boolean isEndOfGame = false;
    private boolean isAIMode    = false;
    private boolean timerEnabled = true;
    private AIPlayer aiPlayer;

    private static final String X_COLOR       = "#00D4FF";
    private static final String O_COLOR       = "#FF6B9D";
    private static final String BG_COLOR      = "#1A1A2E";
    private static final String TILE_COLOR    = "#16213E";
    private static final String TILE_HOVER    = "#0F3460";
    private static final String BORDER_COLOR  = "#533483";

    public TileBoard(InfoCenter infoCenter) {
        this.infoCenter = infoCenter;
        aiPlayer = new AIPlayer(AIPlayer.Difficulty.HARD);

        turnTimer = new TurnTimer(new TurnTimer.TimerListener() {
            @Override
            public void onTick(int secondsLeft) {
                infoCenter.updateTimer(secondsLeft);
            }
            @Override
            public void onTimeUp() {
                if (!isEndOfGame) {
                    String skipped = String.valueOf(playerTurn);
                    infoCenter.updateMeassage("Player " + skipped + " ran out of time!");
                    infoCenter.resetTimerBar();
                    PauseTransition pause = new PauseTransition(Duration.millis(800));
                    pause.setOnFinished(e -> {
                        if (!isEndOfGame) {
                            changePlayerTurn();
                            startTimer();
                            if (isAIMode && playerTurn == 'O') triggerAIMove();
                        }
                    });
                    pause.play();
                }
            }
        });

        pane = new StackPane();
        pane.setMinSize(UIConstants.APP_WIDTH, UIConstants.TILE_BOARD_HEIGHT);
        pane.setTranslateX(UIConstants.APP_WIDTH / 2);
        pane.setTranslateY((UIConstants.TILE_BOARD_HEIGHT / 2) + UIConstants.INFO_CENTER_HEIGHT);
        pane.setStyle("-fx-background-color: " + BG_COLOR + ";");

        addAllTiles();

        winningLine = new Line();
        winningLine.setStroke(Color.web("#FFD700"));
        winningLine.setStrokeWidth(5);
        winningLine.setVisible(false);
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#FFD700"));
        glow.setRadius(15);
        glow.setSpread(0.4);
        winningLine.setEffect(glow);
        pane.getChildren().add(winningLine);
    }

    // ── Public API ─────────────────────────────────────────────

    public void setAIMode(boolean aiMode, AIPlayer.Difficulty difficulty) {
        this.isAIMode = aiMode;
        aiPlayer.setDifficulty(difficulty);
    }

    public void setTimerEnabled(boolean enabled) {
        this.timerEnabled = enabled;
        if (!enabled) {
            turnTimer.stop();
            infoCenter.resetTimerBar();
            infoCenter.hideTimer();
        }
    }

    public void startNewGame() {
        isEndOfGame = false;
        playerTurn  = 'X';
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                tiles[row][col].setValue("");
        winningLine.setVisible(false);
        infoCenter.resetTimerBar();
        if (timerEnabled) {
            infoCenter.showTimer();
            startTimer();
        } else {
            infoCenter.hideTimer();
        }
    }

    public StackPane getStackPane() { return pane; }

    // ── Turn logic ─────────────────────────────────────────────

    public void changePlayerTurn() {
        playerTurn = (playerTurn == 'X') ? 'O' : 'X';
        if (isAIMode) {
            infoCenter.updateMeassage(playerTurn == 'X' ? "Your turn" : "AI is thinking...");
        } else {
            infoCenter.updateMeassage("Player " + playerTurn + "'s turn");
        }
    }

    public String getPlayerTurn() { return String.valueOf(playerTurn); }

    private void startTimer() {
        if (!timerEnabled) return;
        if (isAIMode && playerTurn == 'O') {
            turnTimer.stop();
            infoCenter.resetTimerBar();
        } else {
            turnTimer.start();
        }
    }

    // ── AI ─────────────────────────────────────────────────────

    private String[][] getBoardSnapshot() {
        String[][] snapshot = new String[3][3];
        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++)
                snapshot[r][c] = tiles[r][c].getValue();
        return snapshot;
    }

    private void triggerAIMove() {
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(e -> {
            if (!isEndOfGame) {
                int[] move = aiPlayer.getBestMove(getBoardSnapshot());
                if (move != null) tiles[move[0]][move[1]].playMark();
            }
        });
        pause.play();
    }

    // ── Win detection ──────────────────────────────────────────

    public void checkForWinner() {
        checkRowsForWinner();
        checkColsForWinner();
        checkTopLeftToBottomRightForWinner();
        checkTopRightToBottomLeftForWinner();
        checkForStalemate();
    }

    private void checkRowsForWinner() {
        for (int row = 0; row < 3; row++) {
            if (!tiles[row][0].getValue().isEmpty()
                    && tiles[row][0].getValue().equals(tiles[row][1].getValue())
                    && tiles[row][0].getValue().equals(tiles[row][2].getValue())) {
                endGame(tiles[row][0].getValue(),
                        new WinningTiles(tiles[row][0], tiles[row][1], tiles[row][2]));
                return;
            }
        }
    }

    private void checkColsForWinner() {
        if (isEndOfGame) return;
        for (int col = 0; col < 3; col++) {
            if (!tiles[0][col].getValue().isEmpty()
                    && tiles[0][col].getValue().equals(tiles[1][col].getValue())
                    && tiles[0][col].getValue().equals(tiles[2][col].getValue())) {
                endGame(tiles[0][col].getValue(),
                        new WinningTiles(tiles[0][col], tiles[1][col], tiles[2][col]));
                return;
            }
        }
    }

    private void checkTopLeftToBottomRightForWinner() {
        if (isEndOfGame) return;
        if (!tiles[0][0].getValue().isEmpty()
                && tiles[0][0].getValue().equals(tiles[1][1].getValue())
                && tiles[0][0].getValue().equals(tiles[2][2].getValue())) {
            endGame(tiles[0][0].getValue(),
                    new WinningTiles(tiles[0][0], tiles[1][1], tiles[2][2]));
        }
    }

    private void checkTopRightToBottomLeftForWinner() {
        if (isEndOfGame) return;
        if (!tiles[0][2].getValue().isEmpty()
                && tiles[0][2].getValue().equals(tiles[1][1].getValue())
                && tiles[0][2].getValue().equals(tiles[2][0].getValue())) {
            endGame(tiles[0][2].getValue(),
                    new WinningTiles(tiles[0][2], tiles[1][1], tiles[2][0]));
        }
    }

    private void checkForStalemate() {
        if (isEndOfGame) return;
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                if (tiles[row][col].getValue().isEmpty()) return;
        isEndOfGame = true;
        turnTimer.stop();
        infoCenter.hideTimer();
        infoCenter.updateMeassage("Stalemate! Nobody wins 😐");
        infoCenter.showStartButton();
    }

    private void endGame(String winner, WinningTiles winningTiles) {
        isEndOfGame = true;
        turnTimer.stop();
        infoCenter.hideTimer();
        drawWinningLine(winningTiles);
        infoCenter.addScore(winner);
        if (isAIMode) {
            infoCenter.updateMeassage(winner.equals("X") ? "You Win! 🎉" : "AI Wins! 🤖");
        } else {
            infoCenter.updateMeassage("Player " + winner + " Wins! 🎉");
        }
        infoCenter.showStartButton();
    }

    private void drawWinningLine(WinningTiles wt) {
        winningLine.setStartX(wt.start.getStackPane().getTranslateX());
        winningLine.setStartY(wt.start.getStackPane().getTranslateY());
        winningLine.setEndX(wt.end.getStackPane().getTranslateX());
        winningLine.setEndY(wt.end.getStackPane().getTranslateY());
        winningLine.setTranslateX(wt.middle.getStackPane().getTranslateX());
        winningLine.setTranslateY(wt.middle.getStackPane().getTranslateY());
        winningLine.setVisible(true);
    }

    // ── Inner classes ──────────────────────────────────────────

    private class WinningTiles {
        Tile start, middle, end;
        WinningTiles(Tile s, Tile m, Tile e) { start = s; middle = m; end = e; }
    }

    private void addAllTiles() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                Tile tile = new Tile();
                tile.getStackPane().setTranslateX((col * 100) - 100);
                tile.getStackPane().setTranslateY((row * 100) - 100);
                pane.getChildren().add(tile.getStackPane());
                tiles[row][col] = tile;
            }
        }
    }

    private class Tile {
        private StackPane pane;
        private Label     label;

        Tile() {
            pane = new StackPane();
            pane.setMinSize(100, 100);

            Rectangle border = new Rectangle(100, 100);
            border.setFill(Color.web(TILE_COLOR));
            border.setStroke(Color.web(BORDER_COLOR));
            border.setStrokeWidth(2);
            border.setArcWidth(12);
            border.setArcHeight(12);
            pane.getChildren().add(border);

            label = new Label("");
            label.setAlignment(Pos.CENTER);
            label.setFont(Font.font("Arial", FontWeight.BOLD, 36));
            pane.getChildren().add(label);

            pane.setOnMouseEntered(e -> {
                if (label.getText().isEmpty() && !isEndOfGame) {
                    border.setFill(Color.web(TILE_HOVER));
                    border.setStroke(Color.web("#9B59B6"));
                    DropShadow s = new DropShadow();
                    s.setColor(Color.web("#9B59B6"));
                    s.setRadius(10);
                    pane.setEffect(s);
                }
            });

            pane.setOnMouseExited(e -> {
                if (label.getText().isEmpty()) {
                    border.setFill(Color.web(TILE_COLOR));
                    border.setStroke(Color.web(BORDER_COLOR));
                    pane.setEffect(null);
                }
            });

            pane.setOnMouseClicked(e -> {
                if (isAIMode && playerTurn == 'O') return;
                if (!label.getText().isEmpty() || isEndOfGame) return;
                playMark();
            });
        }

        void playMark() {
            turnTimer.stop();
            String current = getPlayerTurn();
            label.setText(current);

            Color markColor = Color.web(current.equals("X") ? X_COLOR : O_COLOR);
            label.setTextFill(markColor);
            DropShadow glow = new DropShadow();
            glow.setColor(markColor);
            glow.setRadius(12);
            label.setEffect(glow);

            if (pane.getChildren().get(0) instanceof Rectangle rect)
                rect.setFill(Color.web(TILE_HOVER));
            pane.setEffect(null);

            changePlayerTurn();
            checkForWinner();

            if (!isEndOfGame) {
                if (isAIMode && playerTurn == 'O') {
                    triggerAIMove();
                } else {
                    startTimer();
                }
            }
        }

        StackPane getStackPane() { return pane; }
        String getValue()        { return label.getText(); }
        void setValue(String v)  {
            label.setText(v);
            label.setEffect(null);
            label.setTextFill(Color.BLACK);
            if (pane.getChildren().get(0) instanceof Rectangle rect) {
                rect.setFill(Color.web(TILE_COLOR));
                rect.setStroke(Color.web(BORDER_COLOR));
            }
            pane.setEffect(null);
        }
    }
}