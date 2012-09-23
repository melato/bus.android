package org.melato.bus.android.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.bus.android.Info;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Waypoint;

import android.content.Context;

/** Caches the coordinates of all routes in memory, for quick access by the map.
 * The cache is static, so it's valid throughout the life of the app.
 * */
public class RoutePointManager {
  private static RoutePointManager instance;
  private RouteManager routeManager;
  private Map<RouteId,RoutePoints> map = new HashMap<RouteId,RoutePoints>();
  private boolean loaded;

  private RoutePointManager(Context context) {
    super();
    routeManager = Info.routeManager(context); 
    new Thread(new RouteLoader()).start();
  }

  public synchronized static RoutePointManager getInstance(Context context) {
    if ( instance == null ) {
      instance = new RoutePointManager(context.getApplicationContext());
    }
    return instance;
  }
  
  class RouteLoader implements Runnable {
    public RouteLoader() {
      super();
    }

    @Override
    public void run() {
      load();
    }    
  }

  private void load() {
    RoutePointsCollector collector = new RoutePointsCollector();
    routeManager.iterateAllRouteStops(collector);
    synchronized(this) {
      map = collector.getMap();
      loaded =  true;
      this.notifyAll();
    }
  }
  
  public boolean isLoaded() {
    return loaded;
  }
  
  private RoutePoints loadRoute(RouteId routeId) {
    List<Waypoint> waypoints = routeManager.loadWaypoints(routeId);
    return RoutePoints.createFromPoints(Waypoint.asPoints(waypoints));
  }

  /**
   * Ensure that the RoutePoints are loaded.  Load them separately, if necessary.
   * @param routeManager
   * @param routeId
   * @return
   */
  public void ensureLoaded(RouteId routeId) {    
    RoutePoints route = null;
    synchronized(this) {
      route = map.get(routeId);
    }
    if ( route == null ) {
      route = loadRoute(routeId);
      synchronized(this) {
        map.put(routeId,  route);
      }
    }
  }
  
  /**
   * Get the RoutePoints for a route, if we have them.
   * @param routeManager
   * @param routeId
   * @return
   */
  public synchronized RoutePoints getRoutePoints(RouteId routeId) {
    return map.get(routeId);
  }  
  
}
