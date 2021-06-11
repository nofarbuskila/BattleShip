package com.example.nb.battleship.game_logic;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Game {

    private int numOfPlayerMoves;

    private Player player;
    private Player enemy;
    private Context context;
    private boolean isPlayerTurn;

    public Game(Context context, int[][] mat){
        numOfPlayerMoves = 0;
        this.context = context;
        this.isPlayerTurn = true;
        this.player = new Player(context);
        this.player.setPlayerBoardRandomly(mat);

        this.enemy = new ComputerPlayer(context);
        this.enemy.setComputerBoardRandomly();

    }

    public Game(Context context, int[][] mat1, int[][] mat2){
        numOfPlayerMoves = 0;
        this.context = context;
        this.isPlayerTurn = true;
        this.player = new Player(context);
        this.player.setPlayerBoardRandomly(mat1);

        this.enemy = new Player(context);
        this.enemy.setPlayerBoardRandomly(mat2);
    }

    public Board getPlayerBoard(){
        return player.getPlayerBoard();
    }

    public Board getEnemyBoard(){
        return enemy.getPlayerBoard();
    }

    public boolean playerTurn(Tile tile, Board board) {
        if(checkIfHitShip(tile, board))
        {
            if (checkIfShipSink(tile.isShip() - 1, board))
            {
                //GameActivity.enemyNumShipsLeft--;
                if (checkWinning(board))
                    return true;
            }
        }
        return false;
    }

    public Tile enemyTurn(){
        if(this.enemy instanceof ComputerPlayer)
            return ((ComputerPlayer)enemy).pickPlace(this.player.getPlayerBoard());

        return null;
    }

    public boolean checkComputerWinning(Tile tile){
        if(checkIfHitShip(tile, this.player.getPlayerBoard()))
        {
            if (checkIfShipSink(tile.isShip() - 1, player.getPlayerBoard()))
            {
                //GameActivity.playerNumShipsLeft--;
                if (checkWinning(player.getPlayerBoard()))
                    return true;
            }
        }
        return false;
    }

    //check if tile is part of ship(Assuming the tile wasn't hit)
    public boolean checkIfHitShip(Tile tile, Board board){
        int x = tile.getRow();
        int y = tile.getCol();

        tile = board.getLogicBoard()[x][y];
        tile.setHit(true);

        if(tile.isShip() != 0) //tile is part of ship and we hit that ship
            return true;

        return false;
    }

    public boolean checkIfShipSink(int shipNum, Board board){
        Ship ship = board.getShips().get(shipNum - 1);
        ship.setSunk();

        return ship.isSunk();
    }

    public boolean checkWinning(Board board){
        for (Ship ship: board.getShips())
        {
            if(!ship.isSunk())
                return false;
        }

        int boardSize = getPlayerBoard().getBOARD_SIZE();
        Tile[][] tiles = getPlayerBoard().getLogicBoard();

        for (int i = 0; i < boardSize; i++)
            for (int j = 0; j < boardSize; j++)
                tiles[i][j].setEnabled(false);

        return true;
    }

    public boolean getIsPlayerTurn(){
        return isPlayerTurn;
    }

    public void setTurn() {
        isPlayerTurn = !isPlayerTurn;
    }

    public int getNumOfPlayerMoves() {
        return numOfPlayerMoves;
    }

    //raise the number of player move in one
    public void setNumOfPlayerMoves() {
        this.numOfPlayerMoves++;
    }
}