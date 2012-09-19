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
import android.widget.ListView;

/**
 * Displays information about one stop.
 * @author Alex Athanasopoulos
 *
 */
public class StopActivity extends ListActivity {
  public static final String KEY_MARKER = "marker";
  public static final String KEY_INDEX = "index";
  private StopContext stop;
  private PropertiesDisplay properties;
  private BusActivities activities;
  
  public StopActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    stop = new StopContext(this);
    properties = stop.getProperties();
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
    
    properties.add(getResources().getString(R.string.routes));
    for( Route r: markerInfo.getRoutes() ) {
      properties.add( r );
    }
    setListAdapter(stop.createAdapter(R.layout.list_item));
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