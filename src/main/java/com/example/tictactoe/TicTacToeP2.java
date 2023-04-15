package com.example.tictactoe;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;


public class TicTacToeP2 extends Application {
    Label playerXScoreLabel, playerOScoreLabel;
    static final String gameServer = "192.168.1.13";
    private boolean playerXTurn = true;
    Button[][] buttons = new Button[3][3];

    int playerXScore = 0,playerOScore = 0;
    private  BorderPane createContent(){
        BorderPane root = new BorderPane();

        root.setOnMouseMoved(mouseEvent -> {
            syncState();
        });
        root.setPadding(new Insets(20));
        //title
        Label title = new Label("Tic Tac Toe");
        title.setStyle("-fx-alignment: center;-fx-font-size: 24pt;-fx-font-weight: bold");
        root.setTop(title);
        BorderPane.setAlignment(title, Pos.CENTER);


        //board
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.CENTER);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.setStyle("-fx-font-size: 20pt;-fx-font-weight: bold");
                button.setPrefSize(150,150);
                button.setOnAction(event->buttonClick(button));
                buttons[i][j] = button;
                gridPane.add(button,j,i);

            }

        }


        root.setCenter(gridPane);
        //score
        HBox scoreBoard = new HBox(40);
        playerOScoreLabel = new Label("Player O : 0");
        playerXScoreLabel = new Label("Player X : 0");

        scoreBoard.setStyle("-fx-font-size: 10pt;-fx-font-weight: bold;-fx-alignment: center");


        scoreBoard.getChildren().addAll(playerXScoreLabel, playerOScoreLabel);
        root.setBottom(scoreBoard);

        root.setPrefSize(450,450);
        return root;
    }

    void buttonClick(Button button){
        if(button.getText().equals("")){
            if(!playerXTurn){
                enableAllButtons();
                button.setText("O");
                Thread t = new Thread(() -> {
                    updateState();
                    syncState();
                });
                t.start();
            }else{
                disableAllButtons();
                syncState();
            }
            playerXTurn = !playerXTurn;
            isWinner();
        }

        return;

    }

    void updateState(){
        String[][] buttons = getCurrentState();
        try {
            Socket socket = new Socket(gameServer,2000);
            InputStream responseInputStream = socket.getInputStream();
            OutputStream requestOutputStream = socket.getOutputStream();

            ObjectOutputStream req = new ObjectOutputStream(requestOutputStream);
            Request request = new Request();
            request.request = "update";
            request.gameState = new GameState();
            request.gameState.buttons = buttons;
            request.gameState.playerXTurn = playerXTurn;
            //

            req.writeObject(request);
            syncState();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    void syncState(){
        try {
            Socket socket = new Socket(gameServer,2000);
            InputStream responseInputStream = socket.getInputStream();
            OutputStream requestOutputStream = socket.getOutputStream();

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(requestOutputStream);
            ObjectInputStream objectInputStream = new ObjectInputStream(responseInputStream);

            Request request = new Request();
            request.gameState = null;
            request.request = "sync";

            objectOutputStream.writeObject(request);
            GameState gameState = (GameState) objectInputStream.readObject();

            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText(gameState.buttons[i][j]);
                }
            }
          playerXTurn = gameState.playerXTurn;

            if (!playerXTurn)enableAllButtons();

            isWinner();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void isWinner(){
        //row
        for (int row = 0; row < 3; row++) {
            if(buttons[row][0].getText().equals(buttons[row][1].getText()) &&
                    buttons[row][1].getText().equals(buttons[row][2].getText())
                    && !buttons[row][0].getText().isEmpty()
            ){
                String winner = buttons[row][0].getText();
                announceWinner(winner);
                updateScore(winner);
                resetBorad();
                updateState();
            }
        }

        //column

        for (int col = 0; col < 3; col++) {
            if(buttons[0][col].getText().equals(buttons[1][col].getText()) &&
                    buttons[1][col].getText().equals(buttons[2][col].getText())
                    && !buttons[0][col].getText().isEmpty()
            ){
                String winner = buttons[0][col].getText();
                announceWinner(winner);
                updateScore(winner);
                resetBorad();
                updateState();
            }
        }
        //diagonal

        if(buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][2].getText())
                && !buttons[0][0].getText().isEmpty()
        ){
            String winner = buttons[0][0].getText();
            announceWinner(winner);
            updateScore(winner);
            resetBorad();
            updateState();
            return;
        }
        //

        if(buttons[2][0].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[0][2].getText())
                && !buttons[2][0].getText().isEmpty()
        ){
            String winner = buttons[2][0].getText();
            announceWinner(winner);
            updateScore(winner);
            resetBorad();
            updateState();
            return;
        }

        //tie
        boolean isTie = true;
        for(Button[] row : buttons){
            for(Button button : row){
                if(button.getText().isEmpty()){
                    isTie = false;
                    break;
                }
            }
        }
        if(isTie){
            announceTie();
            resetBorad();
            updateState();
        }
    }

    String[][] getCurrentState(){
        String[][] curr = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                curr[i][j] = buttons[i][j].getText();
            }
        }
        return curr;
    }
    private void announceWinner(String player){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations");
        alert.setContentText("Congratulations player "+player+"! won");
        alert.showAndWait();
    }

    private void announceTie(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tie");
        alert.setContentText("It's a Tie");
        alert.showAndWait();
    }

    private void updateScore(String winner){
        if(winner.equals("X")){
            playerXScore++;
            playerXScoreLabel.setText("Player X: "+playerXScore);
        }else {
            playerOScore++;
            playerOScoreLabel.setText("Player ): "+playerOScore);
        }
    }

    private void resetBorad(){
        for(Button[] row : buttons){
            for(Button button : row){
                button.setText("");
            }
        }
    }

    void disableAllButtons(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }

    void enableAllButtons(){
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(false);
            }
        }
    }
    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(createContent());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}