package com.hunan.suanxuan.service.participant;

import com.hunan.suanxuan.utils.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RESPAnalyzer {
  private String method = "";
  private final List<String> keys = new ArrayList<>();
  private String value = "";

  public boolean analyze(String resp) {
    String[] strings = resp.trim().split("\r\n");
    if (strings.length <= 2) {
      return false;
    }
    try {
      switch (strings[2]) {
        case "SET": {
          method = "SET";
          keys.add(strings[4]);
          int remainValue = Integer.parseInt(Character.toString(strings[0].charAt(1)));
          for (int i = 6; i < remainValue * 2 + 1; i = i + 2) {
            value = value.concat(strings[i]);
          }
          break;
        }
        case "GET": {
          method = "GET";
          keys.add(strings[4]);
          break;
        }
        case "DEL": {
          method = "DEL";
          int remainKeys = Integer.parseInt(Character.toString(strings[0].charAt(1)));
          for (int i = 4; i < remainKeys * 2 + 1; i = i + 2) {
            keys.add(strings[i]);
          }
          break;
        }
        default: {
          return false;
        }
      }
    } catch (NullPointerException e) {
      return false;
    }
    return true;
  }

  public String getMethod() {
    return method;
  }

  public List<String> getKeys() {
    return keys;
  }

  public String getValue() {
    return value;
  }

  public Optional<String> commit(Database database) {
    switch (method) {
      case "SET": {
        database.set(keys.get(0), value);
        clear();
        return Optional.of(Messages.OK);
      }
      case "DEL": {
        Optional<String> result = Optional.of(database.del(keys).toString());
        clear();
        return result;
      }
      case "GET": {
        if (!database.isEmpty()) {
          if (!keys.isEmpty()) {
            return Optional.of(database.get(keys.get(0)));
          }
        }
        clear();
        return Optional.empty();
      }
    }
    clear();
    return Optional.empty();
  }

  public void clear() {
    method = "";
    keys.clear();
    value = "";
  }
}
