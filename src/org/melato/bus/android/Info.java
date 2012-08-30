package org.melato.bus.android;

import java.io.File;

import org.melato.bus.model.RouteManager;

public class Info {
  public static final float NEARBY_TARGET_DISTANCE = 1000f;
  
  private static RouteManager routeManager;
  
  public static RouteManager routeManager() {
    if ( routeManager ==  null ) {
      routeManager = new RouteManager(new File("/sdcard/bus/"));
    }
    return routeManager;
  }

}
