package com.seal.vertx.logic;

import com.google.gson.Gson;
import com.seal.vertx.Constants;
import com.seal.vertx.domain.Action;
import com.seal.vertx.message.ActionMessage;

import com.seal.vertx.message.HeartbeatMessage;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class UserInputManager {
    private Map<String, Long> latestContactTime = new HashMap<>();
    private Map<String, Action> latestUserInput;
    private Gson gson = new Gson();

    public UserInputManager() {
		this.latestUserInput = new HashMap<String,Action>();
		this.gson = new Gson();
	}

	public void handle(Object message) {
        JsonObject json = (JsonObject) message;
        ActionMessage am = gson.fromJson(json.toString(), ActionMessage.class);
        Action action = am.getAction();
        String userId = am.getUserId();
        if (latestUserInput.containsKey(userId) && 
        		(latestUserInput.get(userId).isFinal())) {
        	//
        } else {
        	latestUserInput.put(userId, action);
        }
        latestContactTime.put(userId, System.currentTimeMillis());
    }

    public Map<String, Action> getLatestUserInput() {
        return latestUserInput;
    }
    public List<String> getActiveUsers() {
        long min = System.currentTimeMillis() - Constants.inactiveTimeout;
        return latestContactTime.entrySet().stream()
                .filter(e -> e.getValue() > min)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

	public void reset() {
		latestUserInput.clear();
	}

    public void heartbeat(Object message) {
        JsonObject json = (JsonObject) message;
        HeartbeatMessage m = gson.fromJson(json.toString(), HeartbeatMessage.class);
        latestContactTime.put(m.getUserId(), System.currentTimeMillis());
    }
}
