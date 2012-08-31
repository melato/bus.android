package org.melato.bus.android;

import java.io.File;

import org.melato.bus.android.model.NearbyManager;
import org.melato.bus.model.RouteManager;

import android.content.Context;
import android.content.SharedPreferences;

public class Info {
  private static RouteManager routeManager;
  
  public static RouteManager routeManager() {
    if ( routeManager ==  null ) {
      routeManager = new RouteManager(new File("/sdcard/bus/"));
    }
    return routeManager;
  }
  
  public static NearbyManager nearbyManager(Context context) {
    File cacheDir = context.getCacheDir();
    SharedPreferences prefs = context.getSharedPreferences("nearby", Context.MODE_PRIVATE);
    return new NearbyManager(routeManager, cacheDir, new JPreferences(prefs)); 
  }

}
