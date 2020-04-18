package com.awasik.server;

import com.awasik.server.v2physics.Body;
import com.awasik.shared.BodyInfo;

import java.util.ArrayList;
import java.util.Collection;

public class BodyMapper {

    public BodyMapper() {
    }

    public BodyInfo toBodyInfo(Body body) {
        BodyInfo info = new BodyInfo();
        info.id = body.getId();
        info.pos = body.pos;
        info.translatedPos = body.getTranslatedPosition();
        info.tileProgress = body.tileProgress;
        info.reachedDestination = body.reachedDestination;
        info.destination = body.toGo;
        info.velocity = body.vel;
        info.nextToGo = body.nextToGo;
        info.collided = body.collided;
        info.syncNeeded = body.syncNeeded;
        return info;
    }

    public ArrayList<BodyInfo> toBodyInfoList(Collection<Body> values) {
        ArrayList<BodyInfo> world = new ArrayList<>();
        for(Body body : values) {
            world.add(toBodyInfo(body));
        }
        return world;
    }
}
