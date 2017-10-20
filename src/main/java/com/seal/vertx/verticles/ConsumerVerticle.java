package com.seal.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by jacobsznajdman on 19/10/17.
 */
public class ConsumerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerVerticle.class);
    private String address;

    public ConsumerVerticle(String address) {
        this.address = address;
    }

    public void start() {
        LOG.info("Starting Consumer at address {}.", address);
        vertx.eventBus().consumer(address, message -> {
            LOG.info("Received message {}.", message.body());
        });
    }
}
