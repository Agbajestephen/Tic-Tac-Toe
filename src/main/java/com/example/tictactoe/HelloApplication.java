package com.example.tictactoe;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;



import java.io.IOException;

public class HelloApplication extends Application {

    private InfoCenter infoCenter;
    private TileBoard tileBoard;
    @Override
    public void start(Stage stage) throws IOException {
        try{
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root,UIConstants.APP_WIDTH,UIConstants.APP_HEIGHT);
            initLayout(root);
            stage.setScene(scene);
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
        infoCenter.setStartButtonOnAction(startNewGame());
        root.getChildren().add(infoCenter.getStackPane());
    }

    private EventHandler<ActionEvent> startNewGame(){
        return new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e){
                infoCenter.hideStartButton();
                infoCenter.updateMeassage("Player X's Turn");
                tileBoard.startNewGame();
            }
        };
    }

    private void initTileBoard(BorderPane root) {
        tileBoard = new TileBoard(infoCenter);
        root.getChildren().add(tileBoard.getStackPane());
    }


    public static void main(String[] args) {
        launch();
    }
}