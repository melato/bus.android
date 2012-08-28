package org.melato.bus.android;

public class BusStop {
  private String title;
  
  public BusStop(String title) {
    super();
    this.title = title;
  }

  @Override
  public String toString() {
    return title;
  }
  
}
