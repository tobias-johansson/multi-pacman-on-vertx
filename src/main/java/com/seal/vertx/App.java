package com.seal.vertx;

import com.seal.vertx.verticles.ConsumerVerticle;
import com.seal.vertx.verticles.ProducerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;

import java.util.Set;

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
        String address = "0";
        DeploymentOptions opts = new DeploymentOptions().setWorker(false);
        vertx.deployVerticle(new ConsumerVerticle(address), opts, ar -> {
            LOG.info("Consumer deployed at address {}.", address);
        });
    }

    private static void deployProducer(Vertx vertx) {
        String address = "0";
        DeploymentOptions opts = new DeploymentOptions().setWorker(false);
        vertx.deployVerticle(new ProducerVerticle(address), opts, ar -> {
            LOG.info("Producer deployed at address {}.", address);
        });
    }
}
