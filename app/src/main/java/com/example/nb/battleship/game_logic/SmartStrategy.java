package com.example.nb.battleship.game_logic;

import java.util.Random;
import java.io.Serializable;

class SmartStrategy implements StrategyInterface, Serializable{

    private final int BOARD_SIZE;
    private Tile lastHit;
    private Tile lastNeighbor;
    private Random random;
    private int directionToHit; // 0 = no direction , 1 = horizontal, 2 = vertival

    public SmartStrategy() {
        this.BOARD_SIZE = 10;
        this.lastHit = null;
        this.lastNeighbor = null;
        this.random = new Random();
        this.directionToHit = 0;
    }

    @Override
    public Tile pickStrategyMove(Board board) {
        if(lastHit == null)
        {
            return randTile(board);
        }
        else
        {
            if(board.getShipByNumber(lastHit.isShip()).isSunk())
                return randTile(board);
            else
                return smartTile(board);
        }
    }

    private Tile smartTile(Board board) {
        Tile tile;
        if(directionToHit == 0) //first hit in ship
        {
           tile = tryLeft(board);
           return tile == null ? randTile(board) : tile;
        }
        else
        {
            if(directionToHit == 1) //the direction of ship is horizontal
            {
                if(lastNeighbor.isShip() == 0)
                    lastNeighbor = lastHit;

                return hitHorizontalLeft(board);
            }
            else //the direction of ship is vertical
            {
                if(lastNeighbor.isShip() == 0)
                    lastNeighbor = lastHit;

                return hitVerticalUp(board);
            }
        }
    }

    private Tile randTile(Board board){
        int row = random.nextInt(BOARD_SIZE);
        int col = random.nextInt(BOARD_SIZE);

        while(true)
        {
            Tile tile = board.getLogicBoard()[row][col];

            if(!tile.isHit())
            {
                if(tile.isShip() != 0)
                {
                    lastHit = tile;
                }
                else
                {
                    lastHit = null;
                }

                lastNeighbor = null;
                directionToHit = 0;
                return tile;
            }

            row = random.nextInt(BOARD_SIZE);
            col = random.nextInt(BOARD_SIZE);
        }

    }

    private Tile tryLeft(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastHit.getRow(), col = lastHit.getCol();

        if(col - 1 < 0)
            return tryRight(board);

        Tile tile = tiles[row][col - 1];
        if(!tile.isHit())
        {
            if(tile.isShip() != 0)
            {
                lastNeighbor = tile;
                directionToHit = 1;
            }
            return tile;
        }

        return tryRight(board);
    }

    private Tile tryRight(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastHit.getRow(), col = lastHit.getCol();

        if(col + 1 >= BOARD_SIZE)
            return tryUp(board);

        Tile tile = tiles[row][col + 1];
        if(!tile.isHit())
        {
            if(tile.isShip() != 0)
            {
                lastNeighbor = tile;
                directionToHit = 1;
            }
            return tile;
        }

        return tryUp(board);
    }

    private Tile tryUp(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastHit.getRow(), col = lastHit.getCol();

        if(row - 1 < 0)
            return tryDown(board);

        Tile tile = tiles[row - 1][col];
        if(!tile.isHit())
        {
            if(tile.isShip() != 0)
            {
                lastNeighbor = tile;
                directionToHit = 2;
            }
            return tile;
        }

        return tryDown(board);
    }

    private Tile tryDown(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastHit.getRow(), col = lastHit.getCol();

        if(row + 1 >= BOARD_SIZE)
            return null;

        Tile tile = tiles[row + 1][col];
        if(!tile.isHit())
        {
            if(tile.isShip() != 0)
            {
                lastNeighbor = tile;
                directionToHit = 2;
            }
            return tile;
        }

        return null;
    }

    private Tile hitHorizontalLeft(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastNeighbor.getRow(), col = lastNeighbor.getCol();

        if(col - 1 < 0 || lastHit == lastNeighbor)
            return hitHorizontalRight(board);

        Tile tile = tiles[row][col - 1];
        if(!tile.isHit())
        {
            lastNeighbor = tile;
            return tile;
        }

        return hitHorizontalRight(board);
    }

    private Tile hitHorizontalRight(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastNeighbor.getRow(), col = lastNeighbor.getCol();

        if(col + 1 >= BOARD_SIZE || lastHit == lastNeighbor)
            return randTile(board);

        Tile tile = tiles[row][col + 1];
        if(!tile.isHit())
        {
            lastNeighbor = tile;
            return tile;
        }

        return randTile(board);
    }

    private Tile hitVerticalUp(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastNeighbor.getRow(), col = lastNeighbor.getCol();

        if(row - 1 < 0 || lastHit == lastNeighbor)
            return hitVerticalDown(board);

        Tile tile = tiles[row - 1][col];
        if(!tile.isHit())
        {
            lastNeighbor = tile;
            return tile;
        }

        return hitVerticalDown(board);
    }

    private Tile hitVerticalDown(Board board){
        Tile[][] tiles = board.getLogicBoard();
        int row = lastNeighbor.getRow(), col = lastNeighbor.getCol();

        if(row + 1 >= BOARD_SIZE || lastHit == lastNeighbor)
            return randTile(board);

        Tile tile = tiles[row + 1][col];
        if(!tile.isHit())
        {
            lastNeighbor = tile;
            return tile;
        }

        return randTile(board);
    }

}
