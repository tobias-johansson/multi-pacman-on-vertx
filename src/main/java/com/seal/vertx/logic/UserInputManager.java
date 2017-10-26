package com.seal.vertx.logic;

import com.seal.vertx.domain.Action;
import com.seal.vertx.domain.GameState;
import com.seal.vertx.message.ActionMessage;

import java.util.Map;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class UserInputManager {
    private Map<String, Action> latestUserInput;

    public void handle(Object message) {
        ActionMessage am = (ActionMessage) message;
        Action action = am.getAction();
        String userId = am.getUserId();
        latestUserInput.put(userId, action);
    }

    public Map<String, Action> getLatestUserInput() {
        return latestUserInput;
    }

    public void updateFromState(GameState update) {

    }
}
