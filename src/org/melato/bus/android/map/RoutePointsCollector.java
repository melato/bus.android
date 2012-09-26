package org.melato.bus.android.map;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteStopCallback;
import org.melato.gps.Point;

public class RoutePointsCollector implements RouteStopCallback {
  private Map<RouteId,RoutePoints> routes = new HashMap<RouteId,RoutePoints>();
  @Override
  public void add(RouteId routeId, List<Point> waypoints) {
    RoutePoints points = RoutePoints.createFromPoints(waypoints);
    routes.put(routeId,points);
  }
  public Map<RouteId, RoutePoints> getMap() {
    return routes;
  }
}
