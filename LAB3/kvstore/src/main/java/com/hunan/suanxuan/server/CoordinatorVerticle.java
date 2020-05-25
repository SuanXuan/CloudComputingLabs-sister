package com.hunan.suanxuan.server;

import com.hunan.suanxuan.service.coordinator.RequestToParticipant;
import com.hunan.suanxuan.utils.Config;
import com.hunan.suanxuan.utils.Messages;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

import java.util.ArrayList;
import java.util.List;

public class CoordinatorVerticle extends AbstractVerticle {
  private String message;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    Config config = new Config(config().getString("config"));
    List<Config.SocketTuple> participants = config.getParticipant();
    List<String> status = new ArrayList<>();
    NetServerOptions options = new NetServerOptions()
      .setPort(config.getCoordinator().get(0).getPort())
      .setHost(config.getCoordinator().get(0).getHost());
    NetServer server = vertx.createNetServer(options);
    RequestToParticipant request = new RequestToParticipant(vertx, config);
    server.connectStream().handler(socket ->
      socket
        .handler(buffer -> {
          vertx.executeBlocking(promise -> {
            request.request_to_prepare(status, buffer);
            if (status.size() != participants.size() || status.contains(Messages.NO)) {
              request.abort();
              socket.write(Messages.ERROR);
            } else {
              socket.write(request.commit());
            }
            status.clear();
            socket.close();
          },res->{});
        }));
    server.listen(res -> {
      if (res.succeeded()) {
        System.out.println("Coordinator is served on port: " + server.actualPort());
        startPromise.complete();
      } else {
        System.out.println("Failed to bind");
        startPromise.fail(res.cause());
      }
    });
  }

  public void heartbeat(RequestToParticipant request) {
    vertx.setPeriodic(2000, id -> {
      request.heart();
    });
  }
}
