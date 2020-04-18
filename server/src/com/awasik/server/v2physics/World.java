package com.awasik.server.v2physics;

import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class World {

    Queue<Body> bodiesQueue = new ConcurrentLinkedQueue<>();
    private Map<Vector2,Body> claimedTiles = new HashMap<>();

    public Body createBody(Body body) {
        bodiesQueue.add(body);
        return body;
    }

    public void remove(Body body) {
        boolean result = bodiesQueue.remove(body);
        System.out.println(body + " was: " + result);
    }


    public void step(float deltaTime) {
        claimedTiles.clear();
        for(Body body : bodiesQueue) {
            if(body.getBodyType().equals(BodyType.StaticBody)) {
                continue;
            }

            if(body.destination == null || body.destination.equals(body.pos)) {
                body.reachedDestination = true;
                body.destination = null;
            }
            else {
                body.reachedDestination = false;
            }



            /**
             * Server Listener, na podstawie informacji przeslanej z klienta,
             * ustawił na body jego miejsce docelowe
             *
             * body.destination
             *
             * Jesli klient raz wysle informacje o celu body nie moze tej tego dzialania przerwac.
             *
             * Server stara sie dotrzeć do wyznaczonego mu celu. Jesli jest to niemozliwe np.
             * ze wzgledu na kolizje, oznacza body informacja
             *
             * body.syncNeeded
             *
             * Client po otrzymaniu tej informacji wymusza synchronizacje z serwerem
             */

            Vector2 toGo = body.getDirection();

            if (detectCollision(body, toGo)) {
                continue;
            }

            if(!body.reachedDestination) {
                float progress = body.vel * deltaTime;
                if(toGo.x == 1) {
                    body.tileProgress.x += progress;
                    if (body.tileProgress.x > 1) {
                        body.tileProgress.x -= 1;
                        body.pos.add(toGo);
                        if(body.destination.x == body.pos.x) {
                            body.reachedDestination = true;
                            body.destination = null;
                        }
                    }
                }
                else if(toGo.x == -1) {
                    body.tileProgress.x -= progress;
                    if (body.tileProgress.x < -1) {
                        body.tileProgress.x += 1;
                        body.pos.add(toGo);
                        if(body.destination.x == body.pos.x) {
                            body.reachedDestination = true;
                            body.destination = null;
                        }
                    }
                }
                else if(toGo.y == 1) {
                    body.tileProgress.y += progress;
                    if (body.tileProgress.y > 1) {
                        body.tileProgress.y -= 1;
                        body.pos.add(toGo);
                        if(body.destination.x == body.pos.x) {
                            body.reachedDestination = true;
                            body.destination = null;
                        }
                    }
                }
                else if(toGo.y == -1) {
                    body.tileProgress.y -= progress;
                    if (body.tileProgress.y < -1) {
                        body.tileProgress.y += 1;
                        body.pos.add(toGo);
                        if(body.destination.x == body.pos.x) {
                            body.reachedDestination = true;
                            body.destination = null;
                        }
                    }
                }
                body.tileProgress.x = round3(body.tileProgress.x, 1);
                body.tileProgress.y = round3(body.tileProgress.y, 1);
            }

//            body.collided = false;
//            Body tileOwner = claimedTiles.get(newPos);
//            if(tileOwner != null && !tileOwner.equals(body)) {
//                body.reachedDestination = true;
//                body.walking = false;
//                body.destination.set(0,0);
//                body.nextToGo.set(0,0);
//                body.collided = true;
//                body.pos.x = Math.round(body.pos.x);
//                body.pos.y = Math.round(body.pos.y);
//                continue;
//            }
//            else {
//                claimedTiles.put(newPos, body);
//            }

            // oznacza, ze jestesmy na ostatnim stopAt
//            if(body.reachedDestination) {
//
//            }
//            if(body.reachedDestination) {
//                // body skonczylo podroz i moze zaczac poruszac sie
//                // do nastepnego celu
//                if(body.nextToGo != null && (body.nextToGo.x != 0 || body.nextToGo.y != 0)) {
//                    destination.set(body.nextToGo.x, body.nextToGo.y);
//                    body.nextToGo = null;
//                    body.reachedDestination = false;
//                }
//                // body skonczylo podroz i nie dostalo sygnalu w trakcie podrozy zeby
//                // isc dalej czekamy na nowy sygnal
//                else {
//                    if(destination.x != 0 || destination.y != 0) {
//                        boolean collide = checkCollision(newPos, body, bodiesQueue);
//                        if(collide) {
//                            body.destination.set(0,0);
//                            body.nextToGo = null;
//                            body.pos.x = Math.round(body.pos.x);
//                            body.pos.y = Math.round(body.pos.y);
//                            body.walking = false;
//                            body.collided = true;
//                            body.reachedDestination = true;
//                        }
//                        else {
//                            body.walking = true;
//                            body.reachedDestination = false;
//                        }
//                    }
//                    else {
//                        body.destination.set(0,0);
//                        body.pos.x = Math.round(body.pos.x);
//                        body.pos.y = Math.round(body.pos.y);
//                        body.tileProgress.set(0,0);
//                        body.nextToGo = null;
//                        body.reachedDestination = true;
//                    }
//                }
//            }
//
//            if(body.stopAt != null) {
//                float x = body.pos.x;
//                float y = body.pos.y;
//                boolean destinationReached = body.reachedDestination;
//
//                // albo hardstop
//                if(x == body.stopAt.x && y == body.stopAt.y) {
//                    body.destination.set(0,0);
//                    body.tileProgress.set(0,0);
//                    body.nextToGo = null;
//                    body.reachedDestination = true;
//                }
//                // albo dojsc
//                else if(body.isGoingLeft() && body.stopAt.x < body.pos.x) {
//                    body.destination.set()
//                }
//
//            }

//
            // body jest w trakcie przemieszczania sie
//            if(!body.reachedDestination) {
//                // dostalo polecenie zatrzymania sie
//                if(body.nextToGo != null && body.nextToGo.x == 0 && body.nextToGo.y == 0) {
//                    if(body.stopAt != null && body.stopAt.x == body.pos.x && body.stopAt.y == body.pos.y) {
//                        System.out.println("HARD STOP RECIVED FOR BODY: " + body.getId());
//                        System.out.println("at position: " + body.pos);
//                        body.reachedDestination = true;
//                        body.destination.set(0,0);
//                        body.nextToGo = null;
//                        body.tileProgress.x = 0;
//                        body.tileProgress.y = 0;
//                        body.stopAt = null;
//                    }
//                    else if(body.stopAt != null && body.stopAt.x > body.pos.x) {
//                        body.reachedDestination = true;
//                        body.destination.set(0,0);
//                        body.pos.set(body.stopAt.x, body.stopAt.y);
//                        body.nextToGo = null;
//                        body.tileProgress.x = 0;
//                        body.tileProgress.y = 0;
//                        body.stopAt = null;
//                    }
//                }
////                else if(body.nextToGo != null && body.nextToGo.y - 1 = 0) {
////                    System.out.println("test");
////                }
////                else {
////                    body.destination.set(body.nextToGo.x, body.nextToGo.y);
////                }
//            }

//            if(!body.reachedDestination) {
//                float progress = body.vel * deltaTime;
//                if(body.destination.x == 1) {
//                    body.tileProgress.x += progress;
//                    if (body.tileProgress.x > 1) {
//                        body.tileProgress.x -= 1;
//                        body.pos.add(body.destination);
//                        body.reachedDestination = true;
//                    }
//                }
//                else if(body.destination.x == -1) {
//                    body.tileProgress.x -= progress;
//                    if (body.tileProgress.x < -1) {
//                        body.tileProgress.x += 1;
//                        body.pos.add(body.destination);
//                        body.reachedDestination = true;
//                    }
//                }
//                else if(body.destination.y == 1) {
//                    body.tileProgress.y += progress;
//                    if (body.tileProgress.y > 1) {
//                        body.tileProgress.y -= 1;
//                        body.pos.add(body.destination);
//                        body.reachedDestination = true;
//                    }
//                }
//                else if(body.destination.y == -1) {
//                    body.tileProgress.y -= progress;
//                    if (body.tileProgress.y < -1) {
//                        body.tileProgress.y += 1;
//                        body.pos.add(body.destination);
//                        body.reachedDestination = true;
//                    }
//                }
//                body.tileProgress.x = round3(body.tileProgress.x, 1);
//                body.tileProgress.y = round3(body.tileProgress.y, 1);
//            }
        }
    }

    private boolean detectCollision(Body body, Vector2 toGo) {
        Vector2 newPos = new Vector2(body.pos).add(toGo);
        body.collided = false;

        if (checkCollision(newPos, body, bodiesQueue)) {
            resolveCollision(body);
            return true;
        }

        if (checkFrameCollision(body, newPos)) {
            resolveCollision(body);
            return true;
        }


        return false;
    }

    private void resolveCollision(Body body) {
        body.reachedDestination = true;
        body.collided = true;
        body.destination = null;
        body.toGo.set(0,0);
        body.pos.x = Math.round(body.pos.x);
        body.pos.y = Math.round(body.pos.y);
    }

    private boolean checkFrameCollision(Body body, Vector2 newPos) {
        Body tileOwner = claimedTiles.get(newPos);
        if(tileOwner != null && !tileOwner.equals(body)) {
            return true;
        }
        else {
            claimedTiles.put(newPos, body);
        }
        return false;
    }

//    public static float round(float d, int decimalPlace) {
//        BigDecimal bd = new BigDecimal(Float.toString(d));
//        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
//        return bd.floatValue();
//    }
//
//    public static float round2(float d, int decimalPlace) {
//        DecimalFormat df = new DecimalFormat("#.#");
//        df.setRoundingMode(RoundingMode.CEILING);
//        return Float.parseFloat(df.format(d));
//    }

    public static float round3(float f, int decimalPlace) {
        return (float)Math.round(f * 10f) / 10f;
    }

    private boolean checkCollision(Vector2 newPos, Body currentBody, Queue<Body> bodies) {
        for(Body body : bodies) {
            if(currentBody.equals(body)) {
                continue;
            }
            if(body.pos.equals(newPos)) {
                if(body.getBodyType().equals(BodyType.Dynamic)) {
                    //System.out.println(body + ", " + newPos + "- COLLISION");
                }
                return true;
            }
        }
        return false;
    }


}
