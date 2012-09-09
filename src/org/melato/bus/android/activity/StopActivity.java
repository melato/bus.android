package org.melato.bus.android.activity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.WaypointDistance;
import org.melato.bus.model.MarkerInfo;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

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
  static int[] SPEEDS = new int[] { 20, 30, 40 };
  StopContext stop;
  private BusActivities activities;
  
  class MarkerPosition {
    public String toString() {
      return formatProperty( R.string.marker_position, formatDistance(stop.getMarkerPosition()));
    }
  }
  
  class StraightDistance {
    public String toString() {
      return formatProperty( R.string.straight_distance, formatDistance(stop.getStraightDistance()));
    }
  }
  
  class PathDistance {
    public String toString() {
      return formatProperty( R.string.route_distance, formatDistance(stop.getRouteDistance()));
    }
  }
  
  class Latitude {
    public String toString() {
      return formatProperty( R.string.latitude, formatDegrees(stop.getMarker().getLat()));
    }
  }
  
  class Longitude{
    public String toString() {
      return formatProperty( R.string.longitude, formatDegrees(stop.getMarker().getLon()));
    }
  }
  
  class PathETA {
    float speed;
    
    public PathETA(float speed) {
      this.speed = speed;
    }

    public String toString() {
      String label = getResources().getString(R.string.ETA) + " @ " + Math.round(speed) + " Km/h";
      float time = stop.getRouteDistance()/ (speed *1000/3600);
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
      float time = stop.getStraightDistance() / (speed *1000/3600);
      return formatProperty( label, formatTime(time));
    }
  }
  
  String formatTime( float secondsFromNow ) {
    Date eta = new Date(System.currentTimeMillis() + (int) (secondsFromNow*1000));
    int minutes = Math.round(secondsFromNow/60);
    String sign = "";
    if ( minutes < 0 ) {
      // we may have negative times, such as the time to a previous stop.
      minutes = -minutes;
      sign = "-";
    }
      
    return Schedule.formatTime(Schedule.getTime(eta)) +
        " (" + sign + Schedule.formatTime(minutes) + ")";
    
  }
  
  public StopActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    stop = new StopContext(this) {
      public void refresh() {
        StopActivity.this.refresh();
      }
    };
    activities = new BusActivities(this);
    String symbol = (String) getIntent().getSerializableExtra(KEY_MARKER);
    Integer index = (Integer) getIntent().getSerializableExtra(KEY_INDEX);
    if ( symbol == null ) {
      return;
    }
    Route route = activities.getRoute();
    GPX gpx = activities.getRouteManager().loadGPX(route);
    List<Waypoint> waypoints = Collections.emptyList();
    if ( ! gpx.getRoutes().isEmpty() ) {
      waypoints = gpx.getRoutes().get(0).getWaypoints();
    }
    stop.setWaypoints(waypoints);
    
    MarkerInfo markerInfo = Info.routeManager(this).loadMarker(symbol);
    if ( index != null ) {
      stop.setMarkerIndex(index);
    } else {
      stop.setMarker(markerInfo.getWaypoint());
    }
    setTitle(stop.getMarker().getName());
    
    addItem(new StraightDistance());
    addItem(new PathDistance());
    addItem(new Latitude());
    addItem(new Longitude());
    for( float speed: SPEEDS ) {
      addItem( new PathETA(speed));
    }
    addItem(new StraightETA(R.string.walkETA, WALK_SPEED, WALK_OVERHEAD));
    // addItem(new StraightETA(R.string.bikeETA, BIKE_SPEED, BIKE_OVERHEAD));
    addItem(getResources().getString(R.string.routes));
    for( Route r: markerInfo.getRoutes() ) {
      addItem( r );
    }
  }

  private String formatDegrees(float d) {
    return String.valueOf(d);
  }
  
  private String formatDistance(float d) {
    return WaypointDistance.formatDistance(d);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Object obj = items.get(position);
    if ( obj instanceof Route ) {
      activities.showRoute((Route) obj);
    }
  }
  
}