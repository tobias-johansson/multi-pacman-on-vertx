package com.seal.vertx.domain;

import static com.seal.vertx.Constants.speed;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public float getX() {
        switch (this) {
            case UP:
                return 0;
            case DOWN:
                return 0;
            case RIGHT:
                return speed;
            case LEFT:
                return -speed;
            default:
                throw new RuntimeException("Unimplemented direction");
        }
    }

    public float getY() {
        switch (this) {
            case UP:
                return -speed;
            case DOWN:
                return speed;
            case RIGHT:
                return 0;
            case LEFT:
                return 0;
            default:
                throw new RuntimeException("Unimplemented direction");
        }
    }

    public boolean isParallell(Direction other) {
        return (getX() == 0) == (other.getX() == 0);
    }
}
