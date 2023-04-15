package com.example.tictactoe;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.Serializable;

public class GameState implements Serializable {
  public  String buttons[][] = new String[3][3];
  boolean playerXTurn = false;

//    Label playerXScoreLabel, playerOScoreLabel;
//    boolean playerXTurn = true;
}
