package com.seal.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

/**
 * Created by jacobsznajdman on 19/10/17.
 */
public class ConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerVerticle.class);
    private String address;

    public ConsumerVerticle(String address) {
        this.address = address;
    }

    public void start(Future<Void> startFuture) {
        LOG.info("Starting Consumer at address %s.", address);
        vertx.eventBus().consumer(address, message -> {
            LOG.info("Received message %s.", message.body());
        });
    }
}
