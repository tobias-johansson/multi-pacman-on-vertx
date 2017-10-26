package com.seal.vertx.domain;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class PlayerState {
    public final Player player;
    public final int x;
    public final int y;
    public final Direction direction;

    public PlayerState(Player player, int x, int y, Direction direction) {
        this.player = player;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}
