package org.melato.bus.android.model;

import org.melato.bus.model.Route;
import org.melato.gpx.Waypoint;

/** Maintains information about a bus stop nearby. */
public class NearbyStop extends WaypointDistance {
  private Route     route;

  public NearbyStop(Waypoint waypoint, Route route) {
    super(waypoint, 0f);
    this.route = route;
  }
  
  public Route getRoute() {
    return route;
  }

  @Override
  public String toString() {
    String s = route + " (" + formatDistance(getDistance()) + ") " + getWaypoint().getName();
    return s;
  }
}
