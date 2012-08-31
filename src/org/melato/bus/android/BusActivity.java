package org.melato.bus.android;

import org.melato.bus.model.Route;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.MenuItem;


/**
 * @author Alex Athanasopoulos
 */
public class BusActivity extends LocationListActivity {
  public static final String NAV_PREFERENCES = "nav";
  public static final String KEY_ROUTE = "org.melato.bus.android.route";
  
  public static final String VIEW = "view";
  public static final String VIEW_SCHEDULE = "schedule";
  public static final String VIEW_STOPS = "stops";
  
  /** The current route, if any. */
  protected Route route;
    
  public Route getRoute() {
    return route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  protected SharedPreferences getNavigationPreferences() {
    return getSharedPreferences(NAV_PREFERENCES, Context.MODE_PRIVATE);
  }
  
  protected void showRoute(Route route, Class activity) {
    Intent intent = new Intent(this, activity);
    intent.putExtra(KEY_ROUTE, route.qualifiedName());
    startActivity(intent);    
  }
  
  protected void showRoute(Route route) {
    String view = getNavigationPreferences().getString(VIEW, "schedule");
    if ( VIEW_SCHEDULE.equals(view)) {
      showRoute(route, ScheduleActivity.class);      
    } else if ( VIEW_STOPS.equals(view)) {
      showRoute(route, StopsActivity.class);
    } else {
      showRoute(route, ScheduleActivity.class);      
    }
  }
  
  protected void showRoute(Route route, String view) {
    if ( view != null ) {
      setDefaultView(view);
    }
    showRoute(route);
  }

  protected void setDefaultView(String view) {
    SharedPreferences prefs = getNavigationPreferences();
    Editor editor = prefs.edit();
    editor.putString(VIEW,  view);
    editor.commit();
  }
  public void showSchedule(Route route) {
    setDefaultView(VIEW_SCHEDULE);
    showRoute(route, ScheduleActivity.class);
   }

  public void showStops(Route route) {
    setDefaultView(VIEW_STOPS);
    showRoute(route, StopsActivity.class);
   }
  
  public void showMap(Route route) {
    showRoute(route, RouteMapActivity.class);
   }
  
  public void showNearby() {
    startActivity(new Intent(this, NearbyActivity.class));
  }
 
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    Route route = getRoute();

    switch (item.getItemId()) {
      case R.id.schedule:
        showSchedule(route);
        handled = true;
        break;
      case R.id.stops:
        showStops(route);
        handled = true;
        break;
      case R.id.map:
        showMap(route);
        handled = true;
        break;
      default:
        break;
    }
    return handled;
  }
 
  public BusActivity() {
  }
}