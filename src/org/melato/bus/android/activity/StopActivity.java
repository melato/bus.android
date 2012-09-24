package org.melato.bus.android.activity;

import java.util.List;

import org.melato.android.ui.PropertiesDisplay;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.help.HelpActivity;
import org.melato.bus.model.MarkerInfo;
import org.melato.bus.model.Route;
import org.melato.gpx.Waypoint;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    List<Waypoint> waypoints = activities.getRouteManager().loadWaypoints(route);
    stop.setWaypoints(waypoints);
    
    MarkerInfo markerInfo = Info.routeManager(this).loadMarker(symbol);
    if ( index == null ) {
      index = findWaypointIndex(waypoints, markerInfo.getWaypoint());
    }
    stop.setMarkerIndex(index);
    setTitle(stop.getMarker().getName());
   
    /*
    properties.add(getResources().getString(R.string.routes));
    for( Route r: markerInfo.getRoutes() ) {
      properties.add( r );
    }
    */
    setListAdapter(stop.createAdapter(R.layout.list_item));
  }
  
  @Override
  protected void onDestroy() {
    stop.close();
    super.onDestroy();
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
 
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stop_menu, menu);
    HelpActivity.addItem(menu,this, R.string.help_stop);
    return true;
  }
 
  private void showNearby() {
    Waypoint point = stop.getMarker();
    NearbyActivity.start(this, point);
  }
  /**
   * Start the Schedule activity for the given stop.
   * Pass:  stop name, time offset
   */
  private void showStopSchedule() {
    Intent intent = new Intent(this, ScheduleActivity.class);
    new IntentHelper(intent).putRoute(activities.getRoute());
    intent.putExtra(ScheduleActivity.KEY_STOP_NAME, stop.getMarker().getName());
    intent.putExtra(ScheduleActivity.KEY_TIME_OFFSET, stop.getTimeFromStart());
    startActivity(intent);        
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch (item.getItemId()) {
      case R.id.nearby:
        showNearby();
        handled = true;
        break;
      case R.id.schedule:
        showStopSchedule();
        handled = true;
        break;
      default:
        break;
    }
    if ( handled )
      return true;
    return activities.onOptionsItemSelected(item);
  }  
  
}