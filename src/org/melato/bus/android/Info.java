package org.melato.bus.android;

import java.io.File;

import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.android.model.NearbyManager;
import org.melato.bus.model.RouteManager;

import android.content.Context;
import android.content.SharedPreferences;

public class Info {
  public static final File DATA_DIR = new File("/sdcard/bus/");
  
  public static RouteManager routeManager(Context context) {
    return new RouteManager(new SqlRouteStorage(context));
  }
  
  
  public static NearbyManager nearbyManager(Context context) {
    File cacheDir = context.getCacheDir();
    SharedPreferences prefs = context.getSharedPreferences("nearby", Context.MODE_PRIVATE);
    return new NearbyManager(routeManager(context), cacheDir, new JPreferences(prefs)); 
  }

}
