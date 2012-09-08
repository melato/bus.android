package org.melato.bus.android;

import java.io.File;

import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.android.model.NearbyManager;
import org.melato.bus.model.RouteManager;

import android.content.Context;

public class Info {
  public static final float MARK_PROXIMITY = 200f;
  
  public static RouteManager routeManager(Context context) {
    return new RouteManager(new SqlRouteStorage(context));
  }
  
  
  public static NearbyManager nearbyManager(Context context) {
    File cacheDir = context.getCacheDir();
    return new NearbyManager(routeManager(context), cacheDir); 
  }

}
