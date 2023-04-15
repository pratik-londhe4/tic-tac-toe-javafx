package com.example.tictactoe;

import java.io.Serializable;

public class Request implements Serializable {
    GameState gameState;
    String request;
}
