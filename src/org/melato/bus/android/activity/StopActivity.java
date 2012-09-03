package org.melato.bus.android.activity;

import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.model.WaypointDistance;
import org.melato.bus.model.MarkerInfo;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

import android.os.Bundle;

/**
 * Displays information about one stop.
 * @author Alex Athanasopoulos
 *
 */
public class StopActivity extends ItemsActivity {
  public static final String KEY_MARKER = "marker";
  public static final String KEY_INDEX = "index";
  public static final float WALK_OVERHEAD = 1.25f;
  public static final float WALK_SPEED = 5f;
  public static final float BIKE_OVERHEAD = 1.35f;
  public static final float BIKE_SPEED = 15f;
  static int[] SPEEDS = new int[] { 20, 30, 40, 50 };
  
  MarkerInfo markerInfo;
  Waypoint marker;
  Path path;
  int markerIndex;
  float markerPosition;
  float straightDistance;
  /** The path distance to the marker */  
  float routeDistance;

  class MarkerPosition {
    public String toString() {
      return formatProperty( R.string.marker_position, formatDistance(markerPosition));
    }
  }
  
  class StraightDistance {
    public String toString() {
      return formatProperty( R.string.straight_distance, formatDistance(straightDistance));
    }
  }
  
  class PathDistance {
    public String toString() {
      return formatProperty( R.string.route_distance, formatDistance(routeDistance));
    }
  }
  
  class PathETA {
    float speed;
    
    public PathETA(float speed) {
      this.speed = speed;
    }

    public String toString() {
      String label = getResources().getString(R.string.ETA) + " @ " + Math.round(speed) + " Km/h";
      float time = routeDistance / (speed *1000/3600);
      return formatProperty( label, formatTime(time));
    }
  }
  
  class StraightETA {
    int labelId;
    float speed;
    float overhead;
    
    public StraightETA(int labelId, float speed, float overhead) {
      super();
      this.labelId = labelId;
      this.speed = speed;
      this.overhead = overhead;
    }

    public String toString() {
      String label = getResources().getString(labelId);
      float time = straightDistance / (speed *1000/3600);
      return formatProperty( label, formatTime(time));
    }
  }
  
  String formatTime( float secondsFromNow ) {
    Date eta = new Date(System.currentTimeMillis() + (int) (secondsFromNow*1000));
    int minutes = Math.round(secondsFromNow/60);
    return Schedule.formatTime(Schedule.getTime(eta)) +
        " (" + Schedule.formatTime(minutes) + ")";
    
  }
  
  public StopActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String symbol = (String) getIntent().getSerializableExtra(KEY_MARKER);
    Integer index = (Integer) getIntent().getSerializableExtra(KEY_INDEX);
    if ( symbol == null ) {
      return;
    }
    Route route = getRoute();
    GPX gpx = Info.routeManager(this).loadGPX(route);
    List<Waypoint> waypoints = gpx.getRoutes().get(0).path.getWaypoints();
    path = new Path(waypoints);    
    markerInfo = Info.routeManager(this).loadMarker(symbol);
    marker = markerInfo.getWaypoint();
    if ( index != null ) {
      markerIndex = index;
    } else {
      markerIndex = path.findWaypointIndex(marker);
    }
    setTitle(marker.getName());
    
    markerPosition = path.getPathLength(markerIndex);    
    addItem(new StraightDistance());
    addItem(new PathDistance());
    for( float speed: SPEEDS ) {
      addItem( new PathETA(speed));
    }
    addItem(new StraightETA(R.string.walkETA, WALK_SPEED, WALK_OVERHEAD));
    addItem(new StraightETA(R.string.bikeETA, BIKE_SPEED, BIKE_OVERHEAD));
    addItem(getResources().getString(R.string.routes));
    for( Route r: markerInfo.getRoutes() ) {
      addItem( r );
    }
    setEnabledLocations(true);
  }

  private String formatDistance(float d) {
    return WaypointDistance.formatDistance(d);
  }
  @Override
  public void setLocation(Point point) {
    super.setLocation(point);
    if ( point == null )
      return;
    straightDistance = Earth.distance(point, marker);
    float pointPosition = path.getPathLength(point);
    routeDistance = markerPosition - pointPosition;
    refresh();
  }

  
}