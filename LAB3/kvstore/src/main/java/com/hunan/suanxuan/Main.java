package com.hunan.suanxuan;

import com.beust.jcommander.JCommander;
import com.hunan.suanxuan.server.CoordinatorVerticle;
import com.hunan.suanxuan.server.ParticipantVerticle;
import com.hunan.suanxuan.utils.Args;
import com.hunan.suanxuan.utils.Config;
import com.hunan.suanxuan.utils.Messages;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class Main {

  public static void main(String[] args) {
    Args arg = new Args();
    Vertx vertx = Vertx.vertx();
    JCommander.newBuilder()
      .addObject(arg).build().parse(args);
    Config config = new Config(arg.getPath());
    JsonObject conf = new JsonObject().put("config", arg.getPath());
    DeploymentOptions options = new DeploymentOptions().setConfig(conf);
    if (config.getMode().contentEquals(Messages.PARTICIPANT)) {
      vertx.deployVerticle(ParticipantVerticle.class.getName(), options);
    } else if (config.getMode().contentEquals(Messages.COORDINATOR)) {
      vertx.deployVerticle(CoordinatorVerticle.class.getName(), options);
    }
  }
}
