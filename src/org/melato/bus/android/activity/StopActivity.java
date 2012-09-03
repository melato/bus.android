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
  Item  markerPos;
  Item  currentPos;
  Item  straightDistance;
  Item  routeDistance;
  Item[] speedItems;
  MarkerInfo markerInfo;
  Waypoint marker;
  Path path;
  /** The path distance to the marker */
  float markerPosition;
  int markerIndex;
  int[] speeds = new int[] { 30, 40, 50 };
  
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
    markerPos = new Item("marker position", WaypointDistance.formatDistance(markerPosition));
    currentPos = new Item("current position", null);
    straightDistance = new Item(getResources().getString(R.string.straight_distance), null);
    routeDistance = new Item(getResources().getString(R.string.route_distance), null);
    addItem(straightDistance);
    addItem(routeDistance);
    speedItems = new Item[speeds.length];
    for( int i = 0; i < speeds.length; i++ ) {
      speedItems[i] = new Item(getResources().getString(R.string.ETA) + " @ " + speeds[i] + " Km/h", null);
      addItem(speedItems[i]);
    }
    addItem(getResources().getString(R.string.routes));
    for( Route r: markerInfo.getRoutes() ) {
      addItem( r );
    }
    setEnabledLocations(true);
  }

  private String etaAtSpeed( long currentTime, float distance, float speed ) {
    float time = distance / (speed *1000/3600);
    Date date = new Date(currentTime + (int) (time*1000));
    return Schedule.formatTime(Schedule.getTime(date));
  }
  
  private String formatDistance(float d) {
    return WaypointDistance.formatDistance(d);
  }
  @Override
  public void setLocation(Point point) {
    super.setLocation(point);
    if ( point == null )
      return;
    straightDistance.setValue( formatDistance(Earth.distance(point, marker)));
    float pointPosition = path.getPathLength(point);
    if ( ! Float.isNaN(pointPosition)) {
      currentPos.setValue(formatDistance(pointPosition));
    } else {
      currentPos.setValue(null);
    }
    long time = System.currentTimeMillis();
    float remainingDistance = markerPosition - pointPosition;
    routeDistance.setValue(formatDistance(remainingDistance));
    for( int i = 0; i < speeds.length; i++ ) {
      if ( remainingDistance > 0 ) {
        speedItems[i].setValue(etaAtSpeed(time, remainingDistance, speeds[i]));
      } else {
        speedItems[i].setValue(null);        
      }
    }
    refresh();
  }

  
}