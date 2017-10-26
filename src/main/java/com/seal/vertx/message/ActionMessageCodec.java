package com.seal.vertx.message;

import com.google.gson.Gson;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;

/**
 * Created by jacobsznajdman on 26/10/17.
 */
public class ActionMessageCodec implements MessageCodec<ActionMessage, ActionMessage> {
    private final Gson gson;

    public ActionMessageCodec() {
        this.gson = new Gson();
    }

    @Override
    public void encodeToWire(Buffer buffer, ActionMessage customMessage) {
        String json = gson.toJson(customMessage, ActionMessage.class);
        buffer.appendString(json);
    }

    @Override
    public ActionMessage decodeFromWire(int position, Buffer buffer) {
        return gson.fromJson(buffer.slice(position, buffer.length()).toString(), ActionMessage.class);

    }

    @Override
    public ActionMessage transform(ActionMessage customMessage) {
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