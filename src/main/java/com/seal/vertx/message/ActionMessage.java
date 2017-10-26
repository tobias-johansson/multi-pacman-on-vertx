package com.seal.vertx.message;

import com.seal.vertx.domain.Action;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class ActionMessage {
    private final Action action;
    private final String userId;

    public ActionMessage(Action action, String userId) {
        this.action = action;
        this.userId = userId;
    }

    public Action getAction() {
        return action;
    }

    public String getUserId() {
        return userId;
    }
}
