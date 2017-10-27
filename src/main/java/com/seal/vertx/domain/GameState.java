package com.seal.vertx.domain;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class GameState {
    public final List<PlayerState> playerStates;

    public GameState(List<PlayerState> playerStates) {
        this.playerStates = playerStates;
    }

    public static GameState initial() {
        return new GameState(new ArrayList<>());
    }
}
