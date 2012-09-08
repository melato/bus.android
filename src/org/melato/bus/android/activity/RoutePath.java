package org.melato.bus.android.activity;

import java.util.Collections;
import java.util.List;

import org.melato.bus.android.model.WaypointDistance;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

/**
 * Makes computations about a GPX path.
 * Computes the closest stop, etc.
 * @author Alex Athanasopoulos
 */
public class RoutePath {
  GPX gpx;
  WaypointDistance[] stops;
  int closestIndex = -1;

  public RoutePath(GPX gpx) {
    this.gpx = gpx;
    List<Waypoint> waypoints = null;
    if ( gpx.getRoutes().isEmpty() ) {
      waypoints = Collections.emptyList();
    } else {
      waypoints = gpx.getRoutes().get(0).path.getWaypoints();
    }
    stops = new WaypointDistance[waypoints.size()];
    double pathLength = 0;
    Waypoint previous = null;
    for (int i = 0; i < stops.length; i++) {
      Waypoint p = waypoints.get(i);
      if (i != 0) {
        pathLength += Earth.distance(previous, p);
      }
      stops[i] = new WaypointDistance(p, (float) pathLength);
      previous = p;
    }
  }

  private void findClosestWaypoint(Point point) {
    if ( point == null ) {
      closestIndex = -1;
      return;
    }
      
    float minDistance = 0;
    
    for( int i = 0; i < stops.length; i++ ) {
      float d = Earth.distance(point, stops[i].getWaypoint());
      if ( i == 0 || d < minDistance ) {
        minDistance = d;
        closestIndex = i;
      }
    }
  }

  public void setLocation(Point point) {
    findClosestWaypoint(point);
  }
  
  public Waypoint getClosestWaypoint() {
    if ( closestIndex >= 0 ) {
      return stops[closestIndex].getWaypoint();
    } else {
      return null;
    }
  }
}