package com.example.tictactoe;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class GameServer {
  static  GameState gameState = new GameState();

    public static void main(String[] args) {
        for (int i = 0; i < 3; i++) {
            Arrays.fill(gameState.buttons[i], "");
        }
        try{
            ServerSocket ss = new ServerSocket(2000);
            while (true){
                Socket Player1 = ss.accept();

                Thread t = new Thread(new ClientHandler(Player1));
                t.start();

            }


        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

class ClientHandler implements Runnable{
    Socket clientSocket;
    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
    }
    @Override
    public void run() {
       //get i/o streams
        try {
            InputStream playerInputStream = clientSocket.getInputStream();
            OutputStream playerOutputStream = clientSocket.getOutputStream();

            ObjectInputStream requestInputStream = new ObjectInputStream(playerInputStream);
            ObjectOutputStream responseOutputStream = new ObjectOutputStream(playerOutputStream);

            Request request =(Request) requestInputStream.readObject();

            if(request.request.equals("sync")){
                System.out.println("received sync request");
                responseOutputStream.writeObject(GameServer.gameState);
            }else if(request.request.equals("update")){
                System.out.println("Update from client");
                GameServer.gameState = request.gameState;
            }


        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
