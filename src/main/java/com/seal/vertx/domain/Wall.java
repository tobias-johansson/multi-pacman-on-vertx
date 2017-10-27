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
	public Location anchor() {
		return anchor;
	}
	
	public boolean contains(Location check) {
		float upperLeftCheckx = check.x;
		float upperLeftChecky = check.y;
		float lowwerRightCheckx = check.x+Constants.playerWidth;
		float lowwerRightChecky = check.y+Constants.playerWidth;
		if (Math.max(upperLeft.x, upperLeftCheckx) <= Math.min(lowerRight.x, lowwerRightCheckx) &&
	            Math.max(upperLeft.y, upperLeftChecky) <= Math.min(lowerRight.y, lowwerRightChecky)) {
			return true;
	    }
		return false;
	}
	
}
