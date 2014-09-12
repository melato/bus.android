package org.melato.bus.android.gpx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.melato.bus.model.Route;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.NamedPoint;
import org.melato.gpx.GPX;
import org.melato.gpx.KeyValue;
import org.melato.gpx.Sequence;
import org.melato.gpx.Waypoint;

public class GPXMaker {
  private GPX gpx = new GPX();

  static Waypoint toWaypoint(Stop stop, boolean includeName) {
    Waypoint p = new Waypoint(stop.getLat(), stop.getLon());
    if ( includeName ) {
      p.name = stop.getName();
      p.setSym( stop.getSymbol());
    }
    return p;
  }
  
  public void addPoint(NamedPoint p ) {
    Waypoint w = new Waypoint(p.getLat(), p.getLon());
    w.setName(p.getName());
    gpx.getWaypoints().add(w);
  }
  
  public void addPoint(Stop p ) {
    Waypoint w = new Waypoint(p.getLat(), p.getLon());
    w.setName(p.getName());
    gpx.getWaypoints().add(w);
  }
  
  public void addRoute(Route route, List<Stop> stops) {
    List<Waypoint> waypoints = new ArrayList<Waypoint>(stops.size());
    for(Stop stop: stops) {
      waypoints.add(toWaypoint(stop, false));
    }
    org.melato.gpx.Route rte = new org.melato.gpx.Route();
    rte.path = new Sequence(waypoints);
    rte.setExtensions(new KeyValue[] {
        new KeyValue("color", String.valueOf(route.getColor())),
    });
    gpx.getRoutes().add(rte);
  }
  
  public void addRoute(Route route, Stop[] stops) {
    addRoute(route, Arrays.asList(stops));
  }
  
  public GPX getGpx() {
    return gpx;
  }
}
