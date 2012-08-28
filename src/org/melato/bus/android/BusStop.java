package org.melato.bus.android;

import java.util.Arrays;
import java.util.List;

import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;

public class BusStop {
  private String title;
  
  public BusStop(String title) {
    super();
    this.title = title;
  }

  public BusStop(Waypoint waypoint) {
    title = waypoint.getName();
  }
  @Override
  public String toString() {
    return title;
  }
  
  public static List<BusStop> listFromGPX(GPX gpx) {
    List<Waypoint> waypoints = gpx.getRoutes().get(0).path.getWaypoints();
    BusStop[] stops = new BusStop[waypoints.size()];
    for( int i = 0; i < stops.length; i++ ) {
      stops[i] = new BusStop(waypoints.get(i));
    }
    return Arrays.asList(stops);
  }
  
}
