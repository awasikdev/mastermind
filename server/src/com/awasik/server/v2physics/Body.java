package com.awasik.server.v2physics;

import com.badlogic.gdx.math.Vector2;


public class Body {

    public static long ids = 0;
    public boolean reachedDestination = true;
    public boolean gameStateLocked = false;
    public boolean collided = false;
    public boolean hardstop = false;
    public Vector2 destination;
    public boolean syncNeeded = false;
    private String id;

    public int gameStateId = 0;

    public Vector2 pos = new Vector2(0,0);
    public Vector2 dim = new Vector2(0,0);
    public float vel = 0;
    public Vector2 progres = new Vector2(0,0);
    public Vector2 tileProgress = new Vector2(0,0);
    public Vector2 delay = new Vector2(0,0);
    private Vector2 position = new Vector2(0,0);
    public Vector2 toGo = new Vector2();

    /**
     * 0,0 - not walking
     * 1,0 - walking right
     * 1,1 - walking top right (not possible)
     * 0,1 - walking down
     */
    public boolean walking = false;
    public boolean walkingX = false;
    public boolean walkingY = false;
    public boolean progressedX = true;
    public boolean progressedY = true;

    private BodyType bodyType = BodyType.StaticBody;
    private Object userData;
    public Vector2 nextToGo = new Vector2();
    public Vector2 stopAt = null;

    public Body() {
        ids++;
        id = String.valueOf(ids);
    }

    public Body(String id) {
        this.id = id;
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }


    public void setLinearVelocity(float vX, float vY) {
//        if(vX != 0 && vY == 0) {
//            this.vel.x = vX;
//            walkingX = true;
//        }
//        else if(vY != 0 && vX == 0) {
//            this.walkingY = true;
//            this.vel.y = vY;
//        }
    }

    public Vector2 getLinearVelocity() {
        return Vector2.Zero;
    }

    public Vector2 getPosition() {
        if(toGo.x == 0 && toGo.y == 0) {
            return new Vector2(pos);
        }
        return new Vector2(pos).add(tileProgress);
    }

    public Vector2 getTranslatedPosition() {

        Vector2 temp = getPosition();
//        Vector2 temp2 = new Vector2(temp).add(0.5f, 0.5f);
//        temp2.x = round3(temp2.x,1);
//        temp2.y = round3(temp2.y,1);
        return temp;
    }

//    public static float round(float d, int decimalPlace) {
//        BigDecimal bd = new BigDecimal(Float.toString(d));
//        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
//        return bd.floatValue();
//    }

    public static float round3(float f, int decimalPlace) {
        return (float)Math.round(f * 10f) / 10f;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Body body = (Body) o;
        return id.equals(body.id) &&
                bodyType == body.bodyType;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public void goRight(Vector2 stopAt) {
        if(reachedDestination) {
            toGo.y = 0;
            toGo.x = 1;
            destination = stopAt;
        }
        else {
            if(nextToGo == null) {
                nextToGo = new Vector2(1,0);
            }
        }
    }

    public void goLeft(Vector2 stopAt) {
        if(reachedDestination) {
            toGo.y = 0;
            toGo.x = -1;
            destination = stopAt;
        }
        else {
            if(nextToGo == null) {
                nextToGo = new Vector2(-1,0);
            }
        }
    }
    public void goUp(Vector2 stopAt) {
        if(reachedDestination) {
            toGo.y = -1;
            toGo.x = 0;
            destination = stopAt;
        }
        else {
            if(nextToGo == null) {
                nextToGo = new Vector2(0,-1);
            }
        }
    }
    public void goDown(Vector2 stopAt) {
        if(reachedDestination) {
            toGo.y = 1;
            toGo.x = 0;
            destination = stopAt;
        }
        else {
            if(nextToGo == null) {
                nextToGo = new Vector2(0,1);
            }
        }
    }
    public void stop(Vector2 stopAt) {
        if(toGo.x != 0 || toGo.y != 0) {
        }
        if(reachedDestination) {
            System.out.println(" stop juz");
         //   this.stopAt = stopAt;
            toGo.x = 0;
            toGo.y = 0;
            nextToGo = null;
        }
        else {
            System.out.println(" stop gdy skonczysz ten kafelek");
            if(nextToGo == null) {
                this.stopAt = stopAt;
                nextToGo = new Vector2(0,0);
            }
        }
    }
    public boolean isGoingRight() {
        return toGo.x == 1;
    }
    public boolean isGoingLeft() {
        return toGo.x == -1;
    }
    public boolean isGoingUp() {
        return toGo.y == 1;
    }
    public boolean isGoingDown() {
        return toGo.y == -1;
    }

    @Override
    public String toString() {
        return "Body{" + id +": " +
                "pos=" + pos +
                '}';
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
}
