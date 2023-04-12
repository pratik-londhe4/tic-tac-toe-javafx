package com.example.tictactoe;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class TicTacToe extends Application {
    Label playerXScore,playerOScore;
    private boolean playerXTurn = true;
    Button[][] buttons = new Button[3][3];
   private  BorderPane createContent(){
       BorderPane root = new BorderPane();
       //title
       Label title = new Label("Tic Tac Toe");
       title.setStyle("-fx-alignment: center;-fx-font-size: 24pt;-fx-font-weight: bold");
       root.setTop(title);


       //board
       GridPane gridPane = new GridPane();
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
       playerOScore = new Label("Player O : 0");
       playerXScore = new Label("Player X : 0");

       scoreBoard.setStyle("-fx-font-size: 10pt;-fx-font-weight: bold");


       scoreBoard.getChildren().addAll(playerXScore,playerOScore);
       root.setBottom(scoreBoard);

       root.setPrefSize(450,450);
       return root;
   }

   void buttonClick(Button button){
       if(button.getText().equals("")){
           if(playerXTurn){
               button.setText("X");
           }else{
               button.setText("O");
           }
           playerXTurn = !playerXTurn;
           isWinner();
       }

       return;

   }

   private void isWinner(){
       //row
       for (int row = 0; row < 3; row++) {
           if(buttons[row][0].getText().equals(buttons[row][1].getText()) &&
                   buttons[row][1].getText().equals(buttons[row][2].getText())
                   && !buttons[row][0].getText().isEmpty()
           ){
               announceWinner(buttons[row][0].getText());
           }
       }

       //column
       //diagonal
   }

   private void announceWinner(String player){
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setContentText("Congratulations player "+player+"! won");
       alert.showAndWait();
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