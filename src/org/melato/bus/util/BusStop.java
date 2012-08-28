package org.melato.bus.util;

import java.text.DecimalFormat;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;

/** Maintains track information about a bus stop. */
public class BusStop {
  private Waypoint waypoint;
  public float   distanceFromStart;
  static DecimalFormat kmFormat = new DecimalFormat( "0.00" );
  
  public BusStop(Waypoint waypoint) {
    this.waypoint = waypoint;
  }
  
  public Waypoint getWaypoint() {
    return waypoint;
  }

  static String formatDistance(float distance) {
    if ( distance < 1000 ) {
      return String.valueOf( Math.round(distance)) + " m";
    } else {
      return kmFormat.format(distance/1000) + " Km";
    }
  }

  @Override
  public String toString() {
    String s = waypoint.getName() + " " + formatDistance(distanceFromStart);
    return s;
  }
  
  public static List<BusStop> listFromGPX(GPX gpx) {
    List<Waypoint> waypoints = gpx.getRoutes().get(0).path.getWaypoints();
    BusStop[] stops = new BusStop[waypoints.size()];
    double distance = 0;
    Waypoint previousWaypoint = null;
    for( int i = 0; i < stops.length; i++ ) {
      Waypoint p = waypoints.get(i);
      stops[i] = new BusStop(p);
      if ( i > 0 ) {
        distance += Earth.distance(previousWaypoint,p);
        stops[i].distanceFromStart = (float) distance;
      }
      previousWaypoint = p;
    }
    return Arrays.asList(stops);
  }
  
  public static List<Waypoint> asWaypointList(final List<BusStop> list) {
    return new AbstractList<Waypoint>() {
      @Override
      public int size() {
        return list.size();
      }      
      @Override
      public Waypoint get(int index) {
        return list.get(index).getWaypoint();
      }
    };
  }
  
}
