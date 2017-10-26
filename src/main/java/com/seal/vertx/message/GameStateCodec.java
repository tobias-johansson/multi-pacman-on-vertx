package com.seal.vertx.message;

import com.google.gson.Gson;
import com.seal.vertx.domain.GameState;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class GameStateCodec implements MessageCodec<GameState, GameState> {
    private final Gson gson;

    public GameStateCodec() {
        this.gson = new Gson();
    }

    @Override
    public void encodeToWire(Buffer buffer, GameState customMessage) {
        String json = gson.toJson(customMessage, GameState.class);
        buffer.appendString(json);
    }

    @Override
    public GameState decodeFromWire(int position, Buffer buffer) {
        return gson.fromJson(buffer.slice(position, buffer.length()).toString(), GameState.class);

    }

    @Override
    public GameState transform(GameState customMessage) {
        return customMessage;
    }

    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1;
    }

}