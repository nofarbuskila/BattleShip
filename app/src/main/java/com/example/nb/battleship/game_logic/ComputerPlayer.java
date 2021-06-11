package com.example.nb.battleship.game_logic;

import android.content.Context;


class ComputerPlayer extends Player{

    /*The strategyInterface that will be used by the computer to play the game*/
    private StrategyInterface strategyInterface;

    ComputerPlayer(Context context){
        super(context);
        super.setPlayerName("Computer");
        strategyInterface = new SmartStrategy();
    }

    ComputerPlayer(Context context, Board board){
        super(context);
        super.setPlayerBoardManually(board.getLogicBoard(), board.getShips());
        super.setPlayerName("Computer");
        strategyInterface = new RandomStrategy();
    }

    /*Returns a place for the computer to shoot*/
    Tile pickPlace(Board opponentBoard){
        return strategyInterface.pickStrategyMove(opponentBoard);
    }

}
