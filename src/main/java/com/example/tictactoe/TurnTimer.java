package com.example.tictactoe;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class TurnTimer {

    private static final int TURN_SECONDS = 10;

    private Timeline timeline;
    private int secondsLeft;
    private TimerListener listener;

    public interface TimerListener {
        void onTick(int secondsLeft);
        void onTimeUp();
    }

    public TurnTimer(TimerListener listener) {
        this.listener = listener;
    }

    public void start() {
        stop();
        secondsLeft = TURN_SECONDS;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            listener.onTick(secondsLeft);
            if (secondsLeft <= 0) {
                stop();
                listener.onTimeUp();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    public void reset() {
        stop();
        secondsLeft = TURN_SECONDS;
    }
}