package com.hunan.suanxuan.server;

import com.hunan.suanxuan.utils.Config;
import com.hunan.suanxuan.service.participant.Database;
import com.hunan.suanxuan.utils.Messages;
import com.hunan.suanxuan.service.participant.RESPAnalyzer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ParticipantVerticle extends AbstractVerticle {
  private final RESPAnalyzer respAnalyzer = new RESPAnalyzer();
  private String resp = "";
  private final List<String> logs = new ArrayList<>();
  private final Database database = new Database();

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    // 从config对象中获取配置文件目录
    Config config = new Config(config().getString("config"));
    // 配置服务器socket
    NetServerOptions options = new NetServerOptions().setPort(config.getParticipant().get(0).getPort()).setHost(config.getParticipant().get(0).getHost());
    NetServer server = vertx.createNetServer(options);
    server
      .connectHandler(socket -> {
        socket
          // 编写控制器
          .handler(b -> {
            System.out.println("Participant received: " + b.toString());
            if (b.toString().contentEquals(Messages.PING)) {
              socket.write(Messages.PONG);
            } else if (b.toString().trim().contentEquals(Messages.COMMIT) && logs.contains(Messages.REQUEST_TO_PREPARE)) {
              logs.clear();
              Optional<String> res = respAnalyzer.commit(database);
              if (res.isPresent()) {
                System.out.println("write: " + res.get());
                socket.write(res.get());
                socket.close();
              } else {
                System.out.println("write: " + Messages.DONE);
                socket.write(Messages.DONE);
                socket.close();
              }
            } else if (b.toString().trim().contentEquals(Messages.ABORT)) {
              logs.clear();
              respAnalyzer.clear();
              System.out.println("write: " + Messages.DONE);
              socket.write(Messages.DONE);
              socket.close();
            } else {
              logs.clear();
              resp = b.toString();
              if (!respAnalyzer.analyze(resp)) {
                System.out.println("write: " + Messages.NO);
                socket.write(Buffer.buffer(Messages.NO));
                socket.close();
              } else {
                logs.add(Messages.REQUEST_TO_PREPARE);
                System.out.println("write: " + Messages.PREPARE);
                socket.write(Buffer.buffer(Messages.PREPARE));
                socket.close();
              }
            }
          });
        socket.closeHandler(Void -> System.out.println("Closed"));
      })
      .exceptionHandler(System.out::println);
    server.listen(res -> {
      if (res.succeeded()) {
        System.out.println("Participant is served on port: " + server.actualPort());
        startPromise.complete();
      } else {
        System.out.println("Failed to bind: " + res.cause());
        startPromise.fail(res.cause());
      }
    });
  }
}
