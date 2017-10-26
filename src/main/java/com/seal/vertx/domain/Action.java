package com.seal.vertx.domain;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public enum Action {
    UP, DOWN, LEFT, RIGHT, JOIN, QUIT, REVIVE;

    public boolean isFinal() {
        switch (this) {
            case JOIN:
                return true;
            case QUIT:
                return true;
            case REVIVE:
                return true;
            default:
                return false;
        }
    }
}
