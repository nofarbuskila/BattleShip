package com.example.nb.battleship.game_logic;

import android.content.Context;

import java.util.ArrayList;

public class Player {

    private String playerName = null;
    private Board playerBoard;

    public Player(Context context){
        this.playerBoard = new Board(context);
    }

    public Player(Context context, String name){
        this.playerBoard = new Board(context);
        this.playerName = name;
    }

    public void setPlayerBoardRandomly(int[][] mat){
        playerBoard.createBoardPlayer(mat);
    }

    public void setComputerBoardRandomly(){
        playerBoard.createRandomBoard();
    }

    public void setPlayerBoardManually(Tile[][] logicBoard, ArrayList<Ship> ships){
        playerBoard.createBoard(logicBoard, ships);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public void setPlayerBoard(Board playerBoard) {
        this.playerBoard = playerBoard;
    }

}