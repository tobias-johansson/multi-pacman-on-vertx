package com.seal.vertx;

import com.seal.vertx.verticles.ConsumerVerticle;
import com.seal.vertx.verticles.ProducerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.MessageSource;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by jacobsznajdman on 20/09/17.
 */
public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        String type = args[0];

        ClusterManager mgr = new HazelcastClusterManager();
        VertxOptions options = new VertxOptions().setClusterManager(mgr)
                .setClustered(true)
                .setClusterHost("127.0.0.1")
                .setHAEnabled(true)
                .setHAGroup("dev");
        Vertx.clusteredVertx(options, res -> {
            if (res.succeeded()) {
                Vertx vertx = res.result();
                if (type.equals("producer")) {
                    deployProducer(vertx);
                } else if (type.equals("consumer")) {
                    deployConsumer(vertx);
                } else {
                    throw new RuntimeException("Usage: java App.class [producer|consumer]");
                }
            } else {
                // failed!
            }
        });

    }

    private static void deployConsumer(Vertx vertx) {
        Random rnd = new Random();
        int intAddress = rnd.nextInt();
        String address = String.valueOf(intAddress);
        ServiceDiscovery discovery = ServiceDiscovery.create(vertx);
        DeploymentOptions opts = new DeploymentOptions().setWorker(false);
        vertx.deployVerticle(new ConsumerVerticle(address), opts, ar -> {
            LOG.info("Consumer deployed at address {}.", address);
            Record record = new Record()
                    .setType(MessageSource.TYPE)
                    .setLocation(new JsonObject().put("address", address))
                    .setName("consumer");
            discovery.publish(record, pr -> {
                if (ar.succeeded()) {
                    // publication succeeded
                    Record publishedRecord = pr.result();
                    LOG.info("Published service {}", publishedRecord.getName());
                } else {
                    // publication failed
                }
            });
        });
    }

    private static void deployProducer(Vertx vertx) {
        DeploymentOptions opts = new DeploymentOptions().setWorker(false);
        vertx.deployVerticle(new ProducerVerticle(), opts, ar -> {
            LOG.info("Producer deployed.");
        });
    }
}
