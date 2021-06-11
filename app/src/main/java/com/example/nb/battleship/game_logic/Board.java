package com.example.nb.battleship.game_logic;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Board {

    private final int BOARD_SIZE;
    private Tile[][] logicBoard;
    private ArrayList<Ship> ships;
    private Context context;

    public Board(Context context) {
        this.BOARD_SIZE = 10;
        logicBoard = new Tile[BOARD_SIZE][BOARD_SIZE];
        this.ships = new ArrayList<>();
        this.context = context;

        initialBoard();
        initialShips();
    }

    public int getBOARD_SIZE() {
        return BOARD_SIZE;
    }

    public Tile[][] getLogicBoard() {
        return logicBoard;
    }

    //when we want to define board manually we use setters
    public void setLogicBoard(Tile[][] logicBoard) {
        this.logicBoard = logicBoard;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

    private void initialShips() {
        ships.add(new Ship(1, 2)); //ship_2
        ships.add(new Ship(2, 3)); //ship_3a
        ships.add(new Ship(3, 3)); //ship_3b
        ships.add(new Ship(4, 4)); //ship_4
        ships.add(new Ship(5, 5)); //ship_5
    }

    private void initialBoard() {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                logicBoard[i][j] = new Tile(context, i, j);
    }

    public void createBoard(Tile[][] logicBoard, ArrayList<Ship> ships) {
        this.logicBoard = logicBoard;
        this.ships = ships;
    }

    //create randomly board - computer
    public void createRandomBoard() {
        int mat[][] = new int[10][10];
        placeRandomShipsOnBoard(mat, 5);

        /*delete*/
        for (int i = 0; i < BOARD_SIZE; i++){
            for (int j = 0; j < BOARD_SIZE; j++)
                System.out.print(mat[i][j] + "  ");
            System.out.println();
        }

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if (mat[i][j] != 0)
                {
                    logicBoard[i][j].setShip(mat[i][j]);
                    Ship ship = ships.get(mat[i][j] - 1);
                    ship.addTileToShip(logicBoard[i][j]);
                    if (!ship.isVertical() && (i + 1) < BOARD_SIZE)
                        if (mat[i + 1][j] == mat[i][j])
                            ship.setVertical(true);
                }
    }

    //create randomly board - player
    public void createBoardPlayer(int[][] mat) {

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if (mat[i][j] != 0)
                {
                    logicBoard[i][j].setShip(mat[i][j]);
                    Ship ship = ships.get(mat[i][j] - 1);
                    ship.addTileToShip(logicBoard[i][j]);
                    if (!ship.isVertical() && (i + 1) < BOARD_SIZE)
                        if (mat[i + 1][j] == mat[i][j])
                            ship.setVertical(true);
                }
    }

    private void placeRandomShipsOnBoard(int[][] myBoard, int mNumOfShips) {
        for (int i = mNumOfShips; i >= 1; i--) {
            if (i >= 3)
                placeShipsOnBoard(myBoard, i, i);
            else
                placeShipsOnBoard(myBoard, i + 1, i);
        }
    }

    private void placeShipsOnBoard(int[][] myBoard, int sizeOfShips, int shipNum) {
        Random r = new Random();
        boolean validPlace = false;

        while (!validPlace) {
            int startPositionI = r.nextInt(10);
            int startPositionJ = r.nextInt(10);

            validPlace = tryPlaceSubmarinesOnBoard(myBoard, startPositionI, startPositionJ, sizeOfShips, shipNum);
        }
    }

    private boolean tryPlaceSubmarinesOnBoard(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip, int shipNum) {
        boolean validPlace = false;

        if (myBoard[startPositionI][startPositionJ] == 0) { //empty space for start position

            Random r = new Random();
            boolean vertical = r.nextBoolean();

            if (vertical) {
                if (canPlaceVerticalDown(myBoard, startPositionI, startPositionJ, sizeOfShip)) { //vertical down
                    for (int i = startPositionI; i <= startPositionI + sizeOfShip - 1; i++)
                        myBoard[i][startPositionJ] = shipNum;

                    validPlace = true;

                } else if (canPlaceVerticalUp(myBoard, startPositionI, startPositionJ, sizeOfShip)) //vertical up
                {
                    for (int i = startPositionI; i >= startPositionI - sizeOfShip + 1; i--)
                        myBoard[i][startPositionJ] = shipNum;


                    validPlace = true;
                }
            } else    // if( !vertical)
            {

                if (canPlaceHorizontalLeft(myBoard, startPositionI, startPositionJ, sizeOfShip)) //horizontal left
                {
                    for (int j = startPositionJ; j >= startPositionJ - sizeOfShip + 1; j--)
                        myBoard[startPositionI][j] = shipNum;


                    validPlace = true;

                } else if (canPlaceHorizontalRight(myBoard, startPositionI, startPositionJ, sizeOfShip)) //horizontal right
                {
                    for (int j = startPositionJ; j <= startPositionJ + sizeOfShip - 1; j++)
                        myBoard[startPositionI][j] = shipNum;

                    validPlace = true;

                }
            }
        }

        return validPlace;
    }

    private boolean canPlaceVerticalDown(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionI + sizeOfShip - 1 < 10)
            for (int i = startPositionI; i <= startPositionI + sizeOfShip - 1; i++) {
                if (myBoard[i][startPositionJ] != 0)
                    return false;
            }
        else
            return false;

        return true;
    }

    private boolean canPlaceVerticalUp(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionI - sizeOfShip + 1 >= 0) {
            for (int i = startPositionI; i >= startPositionI - sizeOfShip + 1; i--) {
                if (myBoard[i][startPositionJ] != 0)
                    return false;
            }
        } else
            return false;

        return true;
    }

    private boolean canPlaceHorizontalRight(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionJ + sizeOfShip - 1 < 10) {
            for (int j = startPositionJ; j <= startPositionJ + sizeOfShip - 1; j++) {
                if (myBoard[startPositionI][j] != 0)
                    return false;
            }
        } else
            return false;

        return true;
    }

    private boolean canPlaceHorizontalLeft(int[][] myBoard, int startPositionI, int startPositionJ, int sizeOfShip) {

        if (startPositionJ - sizeOfShip + 1 >= 0) {
            for (int j = startPositionJ; j >= startPositionJ - sizeOfShip + 1; j--) {
                if (myBoard[startPositionI][j] != 0)
                    return false;
            }
        } else
            return false;

        return true;
    }

    public Ship getShipByNumber(int shipNum){
        return ships.get(shipNum - 1);
    }

    //strategy
    Tile placeAt(int x, int y) {
        if (this.logicBoard == null || isOutOfBounds(x, y) || logicBoard[x][y] == null)
            return null;

        return logicBoard[x][y];
    }

    boolean isOutOfBounds(int x, int y) {
        return x >= BOARD_SIZE || y >= BOARD_SIZE || x < 0 || y < 0;
    }

    public boolean isAllShipsSink() {
        for (Ship ship : ships) {
            if (!ship.isSunk())
                return false;
        }

        return true;
    }

    public boolean isAllHit() {
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++)
                if (!logicBoard[i][j].isHit())
                    return false;

        return true;
    }

}
