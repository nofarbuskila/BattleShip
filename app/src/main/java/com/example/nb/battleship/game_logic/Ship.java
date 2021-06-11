package com.example.nb.battleship.game_logic;

import java.io.Serializable;
import java.util.ArrayList;

public class Ship {

    private final int ID;
    private int shipSize;
    private ArrayList<Tile> shipList;
    private boolean isSunk;
    private boolean isVertical;

    public Ship(int id, int shipSize) {
        this.ID = id;
        this.shipSize = shipSize;
        this.shipList = new ArrayList<>();
        this.isSunk = false;
        this.isVertical = false;
    }

    public int getID() {
        return ID;
    }

    public int getShipSize() {
        return shipSize;
    }

    public void setShipSize(int shipSize) {
        this.shipSize = shipSize;
    }

    public ArrayList<Tile> getShipList() {
        return shipList;
    }

    public void setShipList(ArrayList<Tile> shipList) {
        this.shipList = shipList;
    }

    public boolean isSunk() {
        return isSunk;
    }

    public void setSunk() {
        if(isSunk)
            return;

        for (Tile tile: shipList)
        {
            if(!tile.isHit())
                return;
        }

        isSunk = true;

    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        isVertical = vertical;
    }

    public void addTileToShip(Tile tile){
        shipList.add(tile);
    }

}