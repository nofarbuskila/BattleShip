package com.example.nb.battleship.game_logic;

import java.io.Serializable;
import java.util.Random;

public class RandomStrategy implements StrategyInterface, Serializable{

    private final int BOARD_SIZE;
    private Random random;

    public RandomStrategy() {
        this.BOARD_SIZE = 10;
        this.random = new Random();
    }

    @Override
    public Tile pickStrategyMove(Board board) {
        return null;
    }

    private Tile randTile(Board board){
        int row = random.nextInt(BOARD_SIZE);
        int col = random.nextInt(BOARD_SIZE);

        while(true)
        {
            Tile tile = board.getLogicBoard()[row][col];

            if(!tile.isHit())
            {
                return tile;
            }

            row = random.nextInt(BOARD_SIZE);
            col = random.nextInt(BOARD_SIZE);
        }

    }

}
