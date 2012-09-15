package org.melato.bus.android.activity;

import java.util.List;

import org.melato.geometry.gpx.PathTracker;
import org.melato.geometry.gpx.SpeedTracker;
import org.melato.gpx.Earth;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

import android.content.Context;

public class StopContext extends LocationContext {
  private List<Waypoint> waypoints;
  private int markerIndex;
  private Waypoint marker;

  private Path path;
  private PathTracker pathTracker;
  private SpeedTracker speed;
  
  private float straightDistance;


  public float getStraightDistance() {
    return straightDistance;
  }

  public Waypoint getMarker() {
    return marker;
  }
  
  
  public int getMarkerIndex() {
    return markerIndex;
  }

  public PathTracker getPathTracker() {
    return pathTracker;
  }

  public SpeedTracker getSpeed() {
    return speed;
  }

  public float getMarkerPosition() {
    return path.getLength(markerIndex);
  }

  public float getRouteDistance() {
    return path.getLength(markerIndex) - pathTracker.getPosition();
  }
  
  public void refresh() {}
  
  @Override
  public void setLocation(Point point) {
    super.setLocation(point);
    if ( point == null )
      return;
    straightDistance = Earth.distance(point, marker);
    pathTracker.setLocation(point);
    speed.compute();
    refresh();
  }

  public StopContext(Context context) {
    super(context);
  }

  public void setWaypoints(List<Waypoint> waypoints) {
    this.waypoints = waypoints;
    this.path = new Path(waypoints);
    pathTracker = new PathTracker();
    pathTracker.setPath(path);
    speed = new SpeedTracker(pathTracker);
  }
  
  public void setMarkerIndex(int index) {
    markerIndex = index;
    marker = waypoints.get(index);
    setEnabledLocations(true);
  }
}
