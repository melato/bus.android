package org.melato.bus.android.activity;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.Route;
import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;
import org.melato.log.Log;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * Displays the schedule for a route
 * 
 * @author Alex Athanasopoulos
 * 
 */
public class StopsActivity extends ListActivity {
  private BusActivities activities;
  private StopsContext stops;

  public StopsActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.info("BusActivities");
    activities = new BusActivities(this);
    Log.info("StopContext");
    stops = new StopsContext(this);
    Route route = activities.getRoute();
    Log.info("route: " + route);
    setTitle(route.getFullTitle());

    GPX gpx = Info.routeManager(this).loadGPX(route);
    Log.info("gpx");
    stops.setGPX(gpx);
  }
  
  @Override
  protected void onDestroy() {
    stops.close();
    super.onDestroy();
  }

  private void showStop(int index) {
    Waypoint p = stops.getWaypoints().get(index);
    RouteStop stop = new RouteStop(activities.getRouteId(), p.getSym(), index);
    Intent intent = new Intent(this, StopActivity.class);
    IntentHelper helper = new IntentHelper(intent);
    helper.putRouteStop(stop);
    startActivity(intent);    
  }
 
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    showStop( position );
    
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stops_menu, menu);
    HelpActivity.addItem(menu, this, R.string.help_stops);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }  
}