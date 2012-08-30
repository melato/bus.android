package org.melato.bus.android.model;

import java.text.DecimalFormat;

import org.melato.bus.model.Route;
import org.melato.gpx.Waypoint;

/** Maintains information about a bus stop nearby. */
public class NearbyStop implements Comparable<NearbyStop> {
  private Waypoint  waypoint;
  private Route     route;
  public float      distance;
  static DecimalFormat kmFormat = new DecimalFormat( "0.00" );
  
  public NearbyStop(Waypoint waypoint, Route route) {
    this.waypoint = waypoint;
    this.route = route;
  }
  
  public Waypoint getWaypoint() {
    return waypoint;
  }
  
  public Route getRoute() {
    return route;
  }

  static String formatDistance(float distance) {
    if ( distance < 1000 ) {
      return String.valueOf( Math.round(distance)) + " m";
    } else {
      return kmFormat.format(distance/1000) + " Km";
    }
  }

  
  @Override
  public int compareTo(NearbyStop x) {
    if ( distance < x.distance )
      return -1;
    if ( distance > x.distance )
      return 1;
    return 0;
  }

  @Override
  public String toString() {
    String s = route.getName() + " " + route.getTitle() + formatDistance(distance) + " (" + waypoint.getName() + ")";
    return s;
  }
  
}
