package com.seal.vertx.logic;

import com.google.gson.Gson;
import com.seal.vertx.domain.Action;
import com.seal.vertx.message.ActionMessage;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class UserInputManager {
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
        		(latestUserInput.get(userId).equals(Action.QUIT) || latestUserInput.get(userId).equals(Action.JOIN))) {
        	//
        } else {
        	latestUserInput.put(userId, action);
        }
    }

    public Map<String, Action> getLatestUserInput() {
        return latestUserInput;
    }

	public void reset() {
		latestUserInput.clear();
	}
}
