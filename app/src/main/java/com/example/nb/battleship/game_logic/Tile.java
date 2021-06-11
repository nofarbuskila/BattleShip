package com.example.nb.battleship.game_logic;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;

import java.io.Serializable;

public class Tile extends AppCompatImageButton {

    private int row;
    private int col;
    private boolean isHit;
    private int isShip;

    public Tile(Context context, int row, int col) {
        super(context);
        this.row = row;
        this.col = col;
        this.isHit = false;
        this.isShip = 0;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public boolean isHit() {
        return isHit;
    }

    public void setHit(boolean hit) {
        isHit = hit;
    }

    public int isShip() {
        return isShip;
    }

    public void setShip(int ship) {
        isShip = ship;
    }

}

