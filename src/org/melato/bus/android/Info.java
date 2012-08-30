package org.melato.bus.android;

import java.io.File;

import org.melato.bus.model.RouteManager;

public class Info {
  /** the qualified name of a route. */
  public static final String KEY_ROUTE = "org.melato.bus.android.route";
  
  public static RouteManager routeManager() {
    return new RouteManager(new File("/sdcard/bus/"));
  }

}
