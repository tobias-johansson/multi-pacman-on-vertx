package com.seal.vertx.domain;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class GameState {
    public final List<PlayerState> playerStates;
    public final Maze maze;

    public GameState(List<PlayerState> playerStates, Maze maze) {
        this.playerStates = playerStates;
        this.maze = maze;
    }

    public static GameState initial() {
        return new GameState(new ArrayList<>(), new Maze());
    }
}
