package com.example.tictactoe;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private InfoCenter infoCenter;
    private TileBoard  tileBoard;

    @Override
    public void start(Stage stage) {
        try {
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: #1A1A2E;");

            Scene scene = new Scene(root, UIConstants.APP_WIDTH, UIConstants.APP_HEIGHT);
            scene.setFill(Color.web("#1A1A2E"));

            initLayout(root);

            stage.setScene(scene);
            stage.setTitle("✦ Tic-Tac-Toe");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initLayout(BorderPane root) {
        initInfoCenter(root);
        initTileBoard(root);
    }

    private void initInfoCenter(BorderPane root) {
        infoCenter = new InfoCenter();

        infoCenter.setStartButtonOnAction(e -> {
            infoCenter.hideStartButton();
            infoCenter.updateMeassage("Player X's Turn");
            tileBoard.startNewGame();
        });

        infoCenter.setModeChangeListener((isAI, difficulty) -> {
            tileBoard.setAIMode(isAI, difficulty);
            infoCenter.hideStartButton();
            infoCenter.updateMeassage(isAI ? "Your turn (You are X)" : "Player X's Turn");
            tileBoard.startNewGame();
        });

        infoCenter.setTimerToggleListener(enabled -> {
            tileBoard.setTimerEnabled(enabled);
        });

        root.getChildren().add(infoCenter.getStackPane());
    }

    private void initTileBoard(BorderPane root) {
        tileBoard = new TileBoard(infoCenter);
        root.getChildren().add(tileBoard.getStackPane());
    }

    public static void main(String[] args) {
        launch();
    }
}