package com.seal.vertx.message;

import com.seal.vertx.domain.Action;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class HeartbeatMessage {
    private final String userId;

    public HeartbeatMessage(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
