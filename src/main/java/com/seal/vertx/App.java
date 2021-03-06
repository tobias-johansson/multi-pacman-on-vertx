package com.seal.vertx;

import com.seal.vertx.domain.Maze;
import com.seal.vertx.verticles.GameVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) throws IOException {
        Logging.configure();
        // one time gen of our datafile
        // Maze.parseMazeFile("client/platzhersh.json", "client/maze.json");

        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);

        router.route("/").handler(ctx -> ctx.response().putHeader("Location", "/client/").setStatusCode(302).end());

        router.route("/client/*").handler(StaticHandler.create("client"));

        // pipe ws <-> vertx bux
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions bridgeOptions = new BridgeOptions();
        PermittedOptions permitAll = new PermittedOptions();
        permitAll.setAddressRegex(".*");
        bridgeOptions.addInboundPermitted(permitAll);
        bridgeOptions.addOutboundPermitted(permitAll);
        SockJSHandler bridge = sockJSHandler.bridge(bridgeOptions);

        router.route("/eventbus/*").handler(bridge);

        router.route().handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");
            response.end("catchall");
        });

        vertx.deployVerticle(new GameVerticle());

        vertx.eventBus().consumer("draw", msg -> {
           LOG.info("Got: {}", msg.body());
        });

        server.requestHandler(router::accept).listen(8080);

    }

}
