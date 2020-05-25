package com.hunan.suanxuan;

import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class TestMainVerticle {
/*
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new ParticipantVerticle(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticle_deployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    vertx.createNetClient().connect(8081, "127.0.0.1", res -> {
      if (res.succeeded()) {
        NetSocket socket = res.result();
        socket.write(Buffer.buffer(Messages.REQUEST_TO_PREPARE));
        socket.handler(b -> {
          System.out.println(b.toString());
          testContext.completeNow();
        });
      } else {
        System.out.println("connected failed");
        testContext.failNow(res.cause());
      }
    });
  }*/
}
