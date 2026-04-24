package com.example.tictactoe;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class InfoCenter {

    private static final int  TURN_SECONDS    = 10;
    private static final double TIMER_BAR_WIDTH = 300;

    private StackPane pane;
    private Label     message;
    private Button    startGameButton;
    private Label     xScoreLabel;
    private Label     oScoreLabel;
    private int       xScore = 0;
    private int       oScore = 0;

    // Timer UI
    private VBox      timerBox;
    private Label     timerLabel;
    private Rectangle timerBarFill;

    // Mode buttons
    private Button twoPlayerBtn;
    private Button easyAIBtn;
    private Button hardAIBtn;

    // Timer toggle
    private Button  timerToggleBtn;
    private boolean timerEnabled = true;

    // Listeners
    private ModeChangeListener  modeChangeListener;
    private TimerToggleListener timerToggleListener;

    public interface ModeChangeListener {
        void onModeChanged(boolean isAI, AIPlayer.Difficulty difficulty);
    }

    public interface TimerToggleListener {
        void onTimerToggled(boolean enabled);
    }

    public void setModeChangeListener(ModeChangeListener l)   { this.modeChangeListener  = l; }
    public void setTimerToggleListener(TimerToggleListener l) { this.timerToggleListener = l; }
    public boolean isTimerEnabled() { return timerEnabled; }

    public InfoCenter() {
        pane = new StackPane();
        pane.setMinSize(UIConstants.APP_WIDTH, UIConstants.INFO_CENTER_HEIGHT);
        pane.setTranslateX(UIConstants.APP_WIDTH / 2);
        pane.setTranslateY(UIConstants.INFO_CENTER_HEIGHT / 2);
        pane.setStyle("-fx-background-color: #1A1A2E;");

        // ── Score row ──────────────────────────────────────────
        xScoreLabel = buildScoreLabel("X  0", "#00D4FF");
        oScoreLabel = buildScoreLabel("O  0", "#FF6B9D");
        Label sep = new Label("  |  ");
        sep.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        sep.setTextFill(Color.web("#533483"));
        HBox scoreBox = new HBox(xScoreLabel, sep, oScoreLabel);
        scoreBox.setAlignment(Pos.CENTER);

        // ── Message label ──────────────────────────────────────
        message = new Label("Tic-Tac-Toe");
        message.setMinSize(UIConstants.APP_WIDTH, 30);
        message.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        message.setAlignment(Pos.CENTER);
        message.setTextFill(Color.web("#E0E0E0"));
        DropShadow labelGlow = new DropShadow();
        labelGlow.setColor(Color.web("#9B59B6"));
        labelGlow.setRadius(10);
        message.setEffect(labelGlow);

        // ── Timer bar ──────────────────────────────────────────
        timerLabel = new Label("⏱  10");
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        timerLabel.setTextFill(Color.web("#E0E0E0"));

        Rectangle timerBarBg = new Rectangle(TIMER_BAR_WIDTH, 6);
        timerBarBg.setFill(Color.web("#2A2A4E"));
        timerBarBg.setArcWidth(6);
        timerBarBg.setArcHeight(6);

        timerBarFill = new Rectangle(TIMER_BAR_WIDTH, 6);
        timerBarFill.setFill(Color.web("#00D4FF"));
        timerBarFill.setArcWidth(6);
        timerBarFill.setArcHeight(6);

        StackPane timerBar = new StackPane(timerBarBg, timerBarFill);
        timerBar.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        timerBox = new VBox(3, timerLabel, timerBar);
        timerBox.setAlignment(Pos.CENTER);
        timerBox.setVisible(false);

        // ── Mode buttons ───────────────────────────────────────
        twoPlayerBtn = buildModeButton("👥 2 Player", true);
        easyAIBtn    = buildModeButton("🤖 Easy AI",  false);
        hardAIBtn    = buildModeButton("💀 Hard AI",  false);

        twoPlayerBtn.setOnAction(e -> {
            setActiveMode(twoPlayerBtn);
            resetScores();
            if (modeChangeListener != null)
                modeChangeListener.onModeChanged(false, AIPlayer.Difficulty.EASY);
        });
        easyAIBtn.setOnAction(e -> {
            setActiveMode(easyAIBtn);
            resetScores();
            if (modeChangeListener != null)
                modeChangeListener.onModeChanged(true, AIPlayer.Difficulty.EASY);
        });
        hardAIBtn.setOnAction(e -> {
            setActiveMode(hardAIBtn);
            resetScores();
            if (modeChangeListener != null)
                modeChangeListener.onModeChanged(true, AIPlayer.Difficulty.HARD);
        });

        HBox modeBox = new HBox(8, twoPlayerBtn, easyAIBtn, hardAIBtn);
        modeBox.setAlignment(Pos.CENTER);

        // ── Timer toggle button ────────────────────────────────
        timerToggleBtn = new Button("⏱ Timer ON");
        timerToggleBtn.setMinSize(110, 28);
        timerToggleBtn.setStyle(activeModeStyle());
        timerToggleBtn.setOnAction(e -> {
            timerEnabled = !timerEnabled;
            if (timerEnabled) {
                timerToggleBtn.setText("⏱ Timer ON");
                timerToggleBtn.setStyle(activeModeStyle());
            } else {
                timerToggleBtn.setText("⏱ Timer OFF");
                timerToggleBtn.setStyle(inactiveModeStyle());
                timerBox.setVisible(false);
            }
            if (timerToggleListener != null)
                timerToggleListener.onTimerToggled(timerEnabled);
        });

        // ── Start button ───────────────────────────────────────
        startGameButton = new Button("▶  Start New Game");
        startGameButton.setMinSize(160, 35);
        startGameButton.setStyle(buttonStyle("#533483"));
        DropShadow btnGlow = new DropShadow();
        btnGlow.setColor(Color.web("#9B59B6"));
        btnGlow.setRadius(12);
        btnGlow.setSpread(0.2);
        startGameButton.setEffect(btnGlow);
        startGameButton.setOnMouseEntered(e -> startGameButton.setStyle(buttonStyle("#7D5FC4")));
        startGameButton.setOnMouseExited(e  -> startGameButton.setStyle(buttonStyle("#533483")));
        startGameButton.setVisible(false);

        // ── Layout ─────────────────────────────────────────────
        HBox controlsRow = new HBox(8, modeBox, timerToggleBtn);
        controlsRow.setAlignment(Pos.CENTER);

        VBox layout = new VBox(6, scoreBox, message, timerBox, controlsRow, startGameButton);
        layout.setAlignment(Pos.CENTER);
        pane.getChildren().add(layout);
    }

    // ── Public API ─────────────────────────────────────────────

    public void updateTimer(int secondsLeft) {
        timerLabel.setText("⏱  " + secondsLeft);
        double ratio = (double) secondsLeft / TURN_SECONDS;
        timerBarFill.setWidth(TIMER_BAR_WIDTH * ratio);
        if (secondsLeft > 6) {
            timerBarFill.setFill(Color.web("#00D4FF"));
            timerLabel.setTextFill(Color.web("#E0E0E0"));
        } else if (secondsLeft > 3) {
            timerBarFill.setFill(Color.web("#FFD700"));
            timerLabel.setTextFill(Color.web("#FFD700"));
        } else {
            timerBarFill.setFill(Color.web("#FF4444"));
            timerLabel.setTextFill(Color.web("#FF4444"));
        }
    }

    public void resetTimerBar() {
        timerBarFill.setWidth(TIMER_BAR_WIDTH);
        timerBarFill.setFill(Color.web("#00D4FF"));
        timerLabel.setText("⏱  10");
        timerLabel.setTextFill(Color.web("#E0E0E0"));
    }

    public void showTimer() { timerBox.setVisible(true);  }
    public void hideTimer() { timerBox.setVisible(false); }

    public void updateMeassage(String msg) {
        this.message.setText(msg);
        if (msg.contains("X") || msg.contains("You")) {
            this.message.setTextFill(Color.web("#00D4FF"));
            DropShadow g = new DropShadow();
            g.setColor(Color.web("#00D4FF")); g.setRadius(12);
            this.message.setEffect(g);
        } else if (msg.contains("O") || msg.contains("AI")) {
            this.message.setTextFill(Color.web("#FF6B9D"));
            DropShadow g = new DropShadow();
            g.setColor(Color.web("#FF6B9D")); g.setRadius(12);
            this.message.setEffect(g);
        } else {
            this.message.setTextFill(Color.web("#E0E0E0"));
            DropShadow g = new DropShadow();
            g.setColor(Color.web("#9B59B6")); g.setRadius(10);
            this.message.setEffect(g);
        }
    }

    public void addScore(String player) {
        if (player.equals("X")) { xScore++; xScoreLabel.setText("X  " + xScore); }
        else                    { oScore++; oScoreLabel.setText("O  " + oScore); }
    }

    private void resetScores() {
        xScore = 0; oScore = 0;
        xScoreLabel.setText("X  0");
        oScoreLabel.setText("O  0");
    }

    public StackPane getStackPane() { return pane; }
    public void showStartButton()   { startGameButton.setVisible(true);  }
    public void hideStartButton()   { startGameButton.setVisible(false); }
    public void setStartButtonOnAction(EventHandler<ActionEvent> a) {
        startGameButton.setOnAction(a);
    }

    // ── Helpers ────────────────────────────────────────────────

    private Label buildScoreLabel(String text, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        label.setTextFill(Color.web(color));
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web(color));
        glow.setRadius(8);
        label.setEffect(glow);
        return label;
    }

    private Button buildModeButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMinSize(90, 28);
        btn.setStyle(active ? activeModeStyle() : inactiveModeStyle());
        btn.setOnMouseEntered(e -> {
            if (!btn.getStyle().equals(activeModeStyle()))
                btn.setStyle(buttonStyle("#2A2A4E"));
        });
        btn.setOnMouseExited(e -> {
            if (!btn.getStyle().equals(activeModeStyle()))
                btn.setStyle(inactiveModeStyle());
        });
        return btn;
    }

    private void setActiveMode(Button active) {
        for (Button b : new Button[]{twoPlayerBtn, easyAIBtn, hardAIBtn})
            b.setStyle(b == active ? activeModeStyle() : inactiveModeStyle());
    }

    private String activeModeStyle() {
        return "-fx-background-color: #533483;" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-cursor: hand;";
    }

    private String inactiveModeStyle() {
        return "-fx-background-color: #1A1A2E;" +
                "-fx-text-fill: #888888;" +
                "-fx-font-size: 12px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 15;" +
                "-fx-border-color: #533483;" +
                "-fx-border-radius: 15;" +
                "-fx-cursor: hand;";
    }

    private String buttonStyle(String bg) {
        return "-fx-background-color: " + bg + ";" +
                "-fx-text-fill: #FFFFFF;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 20;" +
                "-fx-cursor: hand;";
    }
}