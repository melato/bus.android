package org.melato.bus.android.activity;

import org.melato.bus.android.Info;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;

import android.os.Bundle;
import android.view.MenuItem;


/**
 * Base Activity class that provides:
 * ListActivity
 * Location listener
 * activities menu
 * route manager
 * current route
 * @author Alex Athanasopoulos
 */
public class BusActivity extends LocationListActivity {
  BusActivities activities;
    
  private RouteManager routeManager;
  
  
  public RouteManager getRouteManager() {
    if ( routeManager == null ) {
      routeManager = Info.routeManager(this);
    }
    return routeManager;
  }

  public Route getRoute() {
    return activities.getRoute();
  }

  public void setRoute(Route route) {
    activities.setRoute(route);
  }
  
  protected void showRoute(Route route) {
    activities.showRoute(route);
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activities = new BusActivities(this);
  }

 
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }
 
  public BusActivity() {
  }
}