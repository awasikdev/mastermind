package com.awasik.shared;

import java.util.ArrayList;

public class GameStateDto {

    private int index = 0;
    public String text;
    public float delta = 0;
    public int playersOnline = 0;

    public BodyInfo playerInfo = new BodyInfo();
    public ArrayList<BodyInfo> worldBodies = new ArrayList<>();


    public float serverDelta = 0;
    private String time = "0";

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "GameStateDto{" +
                "index=" + index +
                ", playerInfo=" + playerInfo +
                '}';
    }
}
