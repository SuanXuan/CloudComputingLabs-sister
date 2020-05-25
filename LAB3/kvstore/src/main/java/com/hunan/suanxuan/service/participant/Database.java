package com.hunan.suanxuan.service.participant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
  private final Map<String, String> database = new HashMap<>();

  public String get(String key) {
    String[] values = database.get(key).trim().split(" ");
    String get_value = "*"+values.length+"\r\n";
    for(String s:values){
      get_value = get_value + "$" + s.length() + "\r\n" + s;
    }
    return get_value;
  }

  public String del(List<String> keys) {
    int i = 0;
    for (String key : keys) {
      if(database.remove(key)!=null){
        ++i;
      }
    }
    String get_del=":";
    get_del += i +"\r\n";
    return get_del;
  }

  public void set(String key, String value) {
    database.put(key, value);
  }

  public boolean isEmpty(){
    return database.isEmpty();
  }

}
