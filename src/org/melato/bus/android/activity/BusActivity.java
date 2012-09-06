package org.melato.bus.android.activity;

import org.melato.bus.model.Route;

import android.os.Bundle;
import android.view.MenuItem;


/**
 * @author Alex Athanasopoulos
 */
public class BusActivity extends LocationListActivity {
  BusActivities activities;
    
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