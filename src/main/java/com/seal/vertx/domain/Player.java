package com.seal.vertx.domain;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Player {
    public final String id;
    public final PlayerType type;

    public Player(String id, PlayerType type) {
        this.id = id;
        this.type = type;
    }

    public static Player randomPlayer(GameState transforming, String userId) {
        PlayerType newPlayerType = System.currentTimeMillis() % 2 == 0 ? PlayerType.GHOST : PlayerType.PACMAN;
        return new Player(userId, newPlayerType);
    }
}
