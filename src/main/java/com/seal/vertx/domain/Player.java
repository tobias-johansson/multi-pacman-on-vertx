package com.seal.vertx.domain;

import java.util.Random;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Player {
    public final String id;
    public final PlayerType type;
    private final static Random rnd = new Random();

    public Player(String id, PlayerType type) {
        this.id = id;
        this.type = type;
    }

    public static Player randomPlayer(GameState transforming, String userId) {
        long players = transforming.playerStates.stream().count();
        long ghosts = transforming.playerStates.stream().filter(ps -> ps.player.type == PlayerType.GHOST).count();
        float fractionGhosts = (float)ghosts/(float)players;
        PlayerType newPlayerType = rnd.nextFloat() > fractionGhosts ? PlayerType.GHOST : PlayerType.PACMAN;
        return new Player(userId, newPlayerType);
    }
}
