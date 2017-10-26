package com.seal.vertx.domain;

import com.seal.vertx.Constants;

public class Wall {
	Location anchor;
	// bounding rec should be type
	Location upperLeft;
	Location lowerRight;
	public Wall(Location anchor) {
		this.anchor = anchor;
		this.upperLeft = new Location(anchor.x, anchor.y);
		this.lowerRight = new Location(anchor.x+Constants.playerWidth, anchor.y+Constants.playerWidth);
	}
	public boolean contains(Location check) {
        boolean collidesInEndX = collidesSpatial(check.x, anchor.x);
        boolean collidesInEndY = collidesSpatial(check.y, anchor.y);
        if (collidesInEndX && collidesInEndY) {
            return true;
        }
		return false;
	}
    private boolean collidesSpatial(float x1, float x2) {
        return Math.abs(x1-x2) < Constants.playerWidth;
    }
	
}
