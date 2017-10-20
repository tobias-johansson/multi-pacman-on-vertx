package com.seal.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by jacobsznajdman on 19/10/17.
 */
public class ProducerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerVerticle.class);
    private String address;

    public ProducerVerticle(String address) {
        this.address = address;
    }

    public void start() {
        LOG.info("Producer sending hello to address {}.", address);
        vertx.eventBus().send(address, "hello");
    }
}
