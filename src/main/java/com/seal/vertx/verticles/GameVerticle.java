package com.seal.vertx.verticles;

import com.seal.vertx.Engine;
import com.seal.vertx.domain.Action;
import com.seal.vertx.domain.GameState;
import com.seal.vertx.logic.UserInputManager;
import io.vertx.core.AbstractVerticle;

import java.util.Map;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class GameVerticle extends AbstractVerticle {
    private UserInputManager userInputManager;
    private Engine engine;


    public GameVerticle(UserInputManager userInputManager, Engine engine) {
        this.userInputManager = userInputManager;
        this.engine = engine;
    }

    public void start() {
        engine.start();

        vertx.setPeriodic(10, l -> {
            Map<String, Action> latestUserInput = userInputManager.getLatestUserInput();
            GameState update = engine.update(latestUserInput);
            userInputManager.updateFromState(update);
            vertx.eventBus().publish("client", update);
        });

        vertx.eventBus().consumer("action", m -> {
            userInputManager.handle(m);
        });
    }
}
