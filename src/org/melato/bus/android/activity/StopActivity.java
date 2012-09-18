package org.melato.bus.android.activity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.melato.android.ui.PropertiesDisplay;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.MarkerInfo;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays information about one stop.
 * @author Alex Athanasopoulos
 *
 */
public class StopActivity extends ListActivity {
  public static final String KEY_MARKER = "marker";
  public static final String KEY_INDEX = "index";
  public static final float WALK_OVERHEAD = 1.25f;
  public static final float WALK_SPEED = 5f;
  public static final float BIKE_OVERHEAD = 1.35f;
  public static final float BIKE_SPEED = 15f;
  StopContext stop;
  PropertiesDisplay properties;
  ArrayAdapter<Object> adapter;
  private BusActivities activities;
  
  class StraightDistance {
    public String toString() {
      return properties.formatProperty( R.string.straight_distance, UI.straightDistance(stop.getStraightDistance()));
    }
  }
  
  class PathDistance {
    public String toString() {
      return properties.formatProperty( R.string.route_distance, UI.routeDistance(stop.getRouteDistance()));
    }
  }
  
  class DistanceFromStart {
    public String toString() {
      return properties.formatProperty( R.string.position_from_start, UI.routeDistance(stop.getMarkerPosition()));
    }
  }
  
  class DistanceToEnd {
    public String toString() {
      return properties.formatProperty( R.string.position_from_start, UI.routeDistance(stop.getRouteDistance()));
    }
  }
  
  class Latitude {
    public String toString() {
      return properties.formatProperty( R.string.latitude, UI.degrees(stop.getMarker().getLat()));
    }
  }
  
  class Longitude{
    public String toString() {
      return properties.formatProperty( R.string.longitude, UI.degrees(stop.getMarker().getLon()));
    }
  }

  float getSpeed() {
    float speed = stop.getSpeed().getSpeed();
    if ( speed > 0.3f ) {
      // don't show speeds smaller than 0.3 m/s (about 1 Km/h)
      return speed;
    }
    return Float.NaN;
  }
  
  class PathSpeed {
    public String toString() {
      String label = getResources().getString(R.string.speed);
      String value = "";
      float speed = getSpeed() * 3600f/1000f;
      if ( ! Float.isNaN(speed)) {
        value = String.valueOf(Math.round(speed)) + " Km/h";
      }
      return properties.formatProperty( label, value);
    }
  }
  
  class PathETA {
    public String toString() {
      String label = getResources().getString(R.string.ETA);
      String value = "";
      float speed = getSpeed();
      if ( ! Float.isNaN(speed)) {
        float time = stop.getSpeed().getRemainingTime(stop.getMarkerIndex());
        value = formatTime(time);
      }
      return properties.formatProperty( label, value);
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
      return properties.formatProperty( label, formatTime(time));
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
    properties = new PropertiesDisplay(this);
    stop = new StopContext(this) {
      public void refresh() {
        adapter.notifyDataSetChanged();
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
    if ( index == null ) {
      index = findWaypointIndex(waypoints, markerInfo.getWaypoint());
    }
    stop.setMarkerIndex(index);
    setTitle(stop.getMarker().getName());
    
    properties.add(new StraightDistance());
    properties.add(new PathDistance());
    properties.add(new Latitude());
    properties.add(new Longitude());
    properties.add( new PathSpeed());
    properties.add( new PathETA());
    properties.add(new StraightETA(R.string.walkETA, WALK_SPEED, WALK_OVERHEAD));
    // properties.add(new StraightETA(R.string.bikeETA, BIKE_SPEED, BIKE_OVERHEAD));
    properties.add(getResources().getString(R.string.routes));
    for( Route r: markerInfo.getRoutes() ) {
      properties.add( r );
    }
    adapter = properties.createAdapter(R.layout.list_item);
    setListAdapter(adapter);
  }

  static int findWaypointIndex(List<Waypoint> waypoints, Waypoint p) {
    int size = waypoints.size();
    for( int i = 0; i < size; i++ ) {
      if ( p.equals(waypoints.get(i))) {
        return i;
      }
    }
    return -1;
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Object obj = properties.getItem(position);
    if ( obj instanceof Route ) {
      activities.showRoute((Route) obj);
    }
  }
  
}