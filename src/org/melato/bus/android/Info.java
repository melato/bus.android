package org.melato.bus.android;

import java.io.File;

import org.melato.android.AndroidLogger;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.client.NearbyManager;
import org.melato.bus.model.RouteManager;
import org.melato.log.Log;

import android.content.Context;

public class Info {
  public static final float MARK_PROXIMITY = 200f;
  private static RouteManager routeManager;
  
  public static RouteManager routeManager(Context context) {
    if ( routeManager == null ) {
      synchronized(Info.class) {
        if ( routeManager == null ) {
          context = context.getApplicationContext();
          Log.setLogger(new AndroidLogger(context));
          routeManager = new RouteManager(new SqlRouteStorage(context));          
        }
      }
    }
    return routeManager;
  }
  
  
  public static NearbyManager nearbyManager(Context context) {
    File cacheDir = context.getCacheDir();
    return new NearbyManager(routeManager(context), cacheDir); 
  }

}
