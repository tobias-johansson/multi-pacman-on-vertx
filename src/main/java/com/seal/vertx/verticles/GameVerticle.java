package com.seal.vertx.verticles;

import java.io.IOException;

import com.google.gson.Gson;
import com.seal.vertx.Constants;
import com.seal.vertx.Engine;
import com.seal.vertx.domain.Action;
import com.seal.vertx.domain.GameState;
import com.seal.vertx.logic.UserInputManager;
import com.seal.vertx.message.ActionMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;


/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class GameVerticle extends AbstractVerticle {
    private final UserInputManager userInputManager;
    private final Engine engine;
    private final Gson gson;


    public GameVerticle() {
        this.userInputManager = new UserInputManager();
        this.engine = new Engine(this);
        this.gson = new Gson();
    }

    public void start() {
        engine.start();

        vertx.setPeriodic(Constants.timeStep,l -> {
            GameState update = engine.update(userInputManager.getLatestUserInput());
            userInputManager.reset();
            String json = gson.toJson(update, GameState.class);
            vertx.eventBus().publish("client", json);
        });

        vertx.eventBus().consumer("action", m -> {
            userInputManager.handle(m.body());
        });
    }

    public void scheduleReviving(String id) {
        vertx.setTimer(Constants.deadTime, l -> {
            ActionMessage am = new ActionMessage(Action.REVIVE, id);
            String json = gson.toJson(am, ActionMessage.class);
            vertx.eventBus().send("action", new JsonObject(json));
        });
    }
}
