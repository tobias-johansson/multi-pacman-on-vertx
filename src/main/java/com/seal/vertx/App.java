package com.seal.vertx;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route("/client/*").handler(StaticHandler.create("client"));
        router.route().handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/plain");
            response.end("Hello World from Vert.x-Web!");
        });

//        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
//        BridgeOptions bridgeOptions = new BridgeOptions();
//        PermittedOptions permittedOptions = new PermittedOptions();
//        permittedOptions.
//        bridgeOptions.addInboundPermitted()
//        sockJSHandler.bridge()
        server.requestHandler(router::accept).listen(8080);

    }

}
