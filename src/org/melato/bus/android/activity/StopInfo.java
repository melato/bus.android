package org.melato.bus.android.activity;

import java.io.Serializable;

public class StopInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  private String  name;
  private int     index;
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public int getIndex() {
    return index;
  }
  public void setIndex(int index) {
    this.index = index;
  }
  public StopInfo(String name, int index) {
    this.name = name;
    this.index = index;
  }
  
}
