package com.awasik.shared;

import java.util.ArrayList;

public class MyJsonMessage {
    public String text = "";
    public float delta = 0f;
    public int playersOnline = 0;
    public BodyInfo bodyInfo = new BodyInfo();
    public ArrayList<BodyInfo> worldBodies = new ArrayList<>();
    public int gameStateId;
}
