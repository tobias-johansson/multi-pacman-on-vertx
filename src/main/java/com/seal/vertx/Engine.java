package com.seal.vertx;

import com.seal.vertx.domain.Action;
import com.seal.vertx.domain.GameState;

import java.util.Map;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class Engine {
    private GameState current;

    public Engine() {
    }

    public void start() {
        this.current = GameState.initial();
    }

    public GameState update(Map<String, Action> actions) {
        
    }

}
