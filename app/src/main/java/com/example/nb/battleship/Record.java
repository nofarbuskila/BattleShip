package com.example.nb.battleship;

import java.io.Serializable;

public class Record implements Serializable {
    private int position;
    private String name;
    private int score;
    private String time;

    public Record(int position, String name, int score, String time) {
        this.position = position;
        this.name = name;
        this.score = score;
        this.time = time;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
