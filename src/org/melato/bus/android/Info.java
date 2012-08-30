package org.melato.bus.android;

import java.io.File;

import org.melato.bus.model.RouteManager;

public class Info {
  public static RouteManager routeManager() {
    return new RouteManager(new File("/sdcard/bus/"));
  }

}
