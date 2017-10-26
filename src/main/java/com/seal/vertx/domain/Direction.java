package com.seal.vertx.domain;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public int getX() {
        switch (this) {
            case UP:
                return 0;
            case DOWN:
                return 0;
            case RIGHT:
                return 1;
            case LEFT:
                return -1;
            default:
                throw new RuntimeException("Unimplemented direction");
        }
    }

    public int getY() {
        switch (this) {
            case UP:
                return -1;
            case DOWN:
                return 1;
            case RIGHT:
                return 0;
            case LEFT:
                return 0;
            default:
                throw new RuntimeException("Unimplemented direction");
        }
    }
}
