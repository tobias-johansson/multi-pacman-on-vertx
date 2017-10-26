package com.seal.vertx.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Created by jacobsznajdman on 19/10/17.
 */
public class ProducerVerticle extends AbstractVerticle {
    private static final Logger LOG = LoggerFactory.getLogger(ProducerVerticle.class);

    public ProducerVerticle() {
    }

    public void start() {
        vertx.setPeriodic(1000, l -> {
            ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
            discovery.getRecords(new JsonObject().put("name", "consumer"), ar -> {
                if (ar.succeeded()) {
                    List<Record> results = ar.result();
                    LOG.info("Found {} consumer services.", results.size());
                    // If the list is not empty, we have matching record
                    // Else, the lookup succeeded, but no matching service
                    for (Record record : results) {
                        String address = record.getLocation().getString("address");
                        LOG.info("Producer sending hello to address {}.", address);
                        vertx.eventBus().send(address, "hello");
                    }
                } else {
                    // lookup failed
                }
            });
        });
    }
}
