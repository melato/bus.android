package org.melato.bus.android.activity;

import java.util.List;

import org.melato.gpx.Earth;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.PathTracker;
import org.melato.gpx.util.SimplePathTracker;

import android.content.Context;

public class StopContext extends LocationContext {
  private List<Waypoint> waypoints;
  private Waypoint marker;
  private Path path;
  private PathTracker pathTracker;
  private int markerIndex;
  private float markerPosition;
  private float straightDistance;
  /** The path distance to the marker */  
  private float routeDistance;


  public float getStraightDistance() {
    return straightDistance;
  }

  public Waypoint getMarker() {
    return marker;
  }

  public float getMarkerPosition() {
    return markerPosition;
  }

  public float getRouteDistance() {
    return routeDistance;
  }

  public void refresh() {}
  
  @Override
  public void setLocation(Point point) {
    super.setLocation(point);
    if ( point == null )
      return;
    straightDistance = Earth.distance(point, marker);
    pathTracker.setLocation(point);
    float pointPosition = pathTracker.getPosition();
    routeDistance = markerPosition - pointPosition;
    refresh();
  }

  public StopContext(Context context) {
    super(context);
  }

  public void setWaypoints(List<Waypoint> waypoints) {
    this.waypoints = waypoints;
    this.path = new Path(waypoints);
    pathTracker = new SimplePathTracker();
    pathTracker.setPath(path);
  }
  
  public void setMarkerIndex(int index) {
    markerIndex = index;
    marker = waypoints.get(index);
    markerPosition = path.getLength(markerIndex);        
    setEnabledLocations(true);
  }
}
