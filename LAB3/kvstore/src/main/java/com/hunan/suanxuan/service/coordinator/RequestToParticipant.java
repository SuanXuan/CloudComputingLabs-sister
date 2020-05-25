package com.hunan.suanxuan.service.coordinator;

import com.hunan.suanxuan.utils.Config;
import com.hunan.suanxuan.utils.Messages;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequestToParticipant {
  private final Vertx vertx;
  private final Config config;
  private List<Config.SocketTuple> participants;
  private NetClient client;

  public RequestToParticipant(Vertx vertx, Config config) {
    this.vertx = vertx;
    this.config = config;
    this.participants = config.getParticipant();
    this.client = vertx.createNetClient(new NetClientOptions().setConnectTimeout(500));
  }

  public void request_to_prepare(List<String> status, Buffer buffer) {
    participants.forEach(participant -> {
      try (
        Socket client = new Socket(participant.getHost(), participant.getPort());
        DataOutputStream writer = new DataOutputStream(client.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      ) {
        writer.write(buffer.toString().getBytes());
        writer.flush();
        StringBuilder first = new StringBuilder();
        reader.lines().forEach(s->{
          first.append(s);
          if(s.equals("\r\n")){
            try {
              client.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
        status.add(first.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public void abort() {
    participants.forEach(participant -> {
      try (
        Socket client = new Socket(participant.getHost(), participant.getPort());
        DataOutputStream writer = new DataOutputStream(client.getOutputStream());
      ) {
        writer.write(Messages.ABORT.getBytes());
        writer.flush();
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
  }

  public String commit() {
    Set<String> results = new HashSet<>();
    participants.forEach(participant -> {
      try (
        Socket client = new Socket(participant.getHost(), participant.getPort());
        DataOutputStream writer = new DataOutputStream(client.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
      ) {
        writer.write(Messages.COMMIT.getBytes());
        writer.flush();
        StringBuilder result = new StringBuilder();
        reader.lines().forEach(s->{
          result.append(s);
          if(s.equals("\r\n")){
            try {
              client.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
        results.add(result.toString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    if (results.size() == 1) {//全部一致,否则有不同意的返回error
      return results.iterator().next();
    } else {
      return Messages.ERROR;
    }
  }

  public void heart() {
    List<Config.SocketTuple> failed = new ArrayList<>();
    participants.forEach(participant -> vertx.createNetClient()
      .connect(participant.getPort(), participant.getHost(),
        res -> {
          if (res.succeeded()) {
            NetSocket socket = res.result();
            socket.write(Messages.PING);
            socket.handler(b -> {
              System.out.println("Received participant heart: " + b.toString());
              if (!b.toString().contentEquals(Messages.PONG)) {
                failed.add(participant);
              }
            });
          }
        }));
    failed.forEach(config::removeParticipant);
  }
}
