package com.seal.vertx;

import com.seal.vertx.domain.Location;
import com.seal.vertx.domain.PlayerState;
import com.seal.vertx.domain.TimeInterval;

/**
 * Created by jacobsznajdman on 29/10/17.
 */
public class CollisionDetection {

    // Returns a time t in [0,Constants.timeStep]
    public static float timeToImpact(Location l1, float d1x, float d1y, Location l2, float d2x, float d2y) {
        TimeInterval total = new TimeInterval(0,Constants.timeStep);
        TimeInterval collidingX = collidingInterval(l2.x, l1.x, d2x, d1x);
        TimeInterval collidingY = collidingInterval(l2.y, l1.y, d2y, d1y);
        TimeInterval result = total.intersect(collidingX).intersect(collidingY);
        if (result.endTime <= result.startTime) {
            return Constants.timeStep;
        }
        return Math.max(0,result.startTime);
    }


    public  static boolean collides(PlayerState pacman1, PlayerState pacman2, PlayerState ghost1, PlayerState ghost2) {
        float timePacToWall = timeToTravel(pacman1.location, pacman2.location);
        float timeGhostToWall = timeToTravel(ghost1.location, ghost2.location);
        float dpx = pacman1.direction.getX() * timePacToWall / Constants.timeStep;
        float dpy = pacman1.direction.getY() * timePacToWall / Constants.timeStep;
        float dgx = ghost1.direction.getX() * timeGhostToWall / Constants.timeStep;
        float dgy = ghost1.direction.getX() * timeGhostToWall / Constants.timeStep;
        float timeToImpact = timeToImpact(pacman1.location, dpx, dpy, ghost1.location, dgx, dgy);
        return timeToImpact < Constants.timeStep;
    }

    private static float timeToTravel(Location l1, Location l2) {
        return (Math.abs(l1.x - l2.x)/Constants.speed) + (Math.abs(l1.y - l2.y)/Constants.speed);
    }

    private static TimeInterval collidingInterval(float pos1, float pos2, float d1, float d2) {
        float d = d1 - d2;
        float t1 = (pos2 - (pos1 + Constants.playerWidth)) / d;
        float t2 = ((pos2 + Constants.playerWidth) - pos1) / d;
        return new TimeInterval((long)Math.min(t1, t2), (long)Math.max(t1, t2));
    }
}
