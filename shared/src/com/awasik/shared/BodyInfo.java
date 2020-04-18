package com.awasik.shared;

import com.badlogic.gdx.math.Vector2;

public class BodyInfo {
    public String id = "";
    public Vector2 pos = new Vector2();
    public Vector2 destination = new Vector2();
    public boolean reachedDestination = true;
    public float velocity = 0f;
    public Vector2 translatedPos = new Vector2();
    public Vector2 tileProgress = new Vector2();
    public Vector2 nextToGo = new Vector2();
    public boolean collided = false;
    public Vector2 stopAt;
    public boolean syncNeeded = false;

    public boolean isGoingRight() {
        return destination.x == 1;
    }
    public boolean isGoingLeft() {
        return destination.x == -1;
    }
    public boolean isGoingUp() {
        return destination.y == 1;
    }
    public boolean isGoingDown() {
        return destination.y == -1;
    }

    public Vector2 getDirection() {
        Vector2 direction = new Vector2();
        if(destination == null) return direction;
        if(destination.x > pos.x) {
            direction.x = 1;
        }
        else if(destination.x < pos.x) {
            direction.x = -1;
        }
        else if(destination.y > pos.y) {
            direction.y = 1;
        }
        else if(destination.y < pos.y) {
            direction.y = -1;
        }
        else {
            direction.set(0,0);
        }
        return direction;
    }


    @Override
    public String toString() {
        return "BodyInfo{" +
                "id='" + id + '\'' +
                ", pos=" + pos +
                ", destination=" + destination +
                ", nextToGo=" + nextToGo +
                ", tileProgress=" + tileProgress +
                '}';
    }
}
