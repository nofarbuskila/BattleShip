package com.example.nb.battleship.game_logic;

public interface StrategyInterface {
    /*Given the board, returns the Tile that the strategy should hit*/
    Tile pickStrategyMove(Board board);
}

