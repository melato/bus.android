package org.melato.bus.android.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.track.TrackActivity;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteHandler;
import org.melato.bus.model.RouteWriter;
import org.melato.util.MRU;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.view.MenuItem;


/**
 * @author Alex Athanasopoulos
 */
public class BusActivities  {
  public static final String NAV_PREFERENCES = "nav";
  public static final String KEY_ROUTE = "org.melato.bus.android.route";
  
  public static final String VIEW = "view";
  public static final String VIEW_SCHEDULE = "schedule";
  public static final String VIEW_STOPS = "stops";
  public static final String VIEW_MAP = "map";
  public static final String VIEW_TRACK = "track";
  
  public static final int MRU_SIZE = 10;
  
  MRU<Route> mru;
  
  private Context context;
  /** The current route, if any. */
  protected Route route;

  public BusActivities(Activity activity) {
    super();
    this.context = activity;    
    Route route = new IntentHelper(activity).getRoute();
    if ( route != null ) {
      setRoute(route);
    }
  }
  
  public Route getRoute() {
    return route;
  }

  public void setRoute(Route route) {
    this.route = route;
  }

  protected SharedPreferences getNavigationPreferences() {
    return context.getSharedPreferences(NAV_PREFERENCES, Context.MODE_PRIVATE);
  }
  
  public void showRoute(Route route, Class activity) {
    getRecentRoutes().add(route);
    saveRecentRoutes();
    Intent intent = new Intent(context, activity);
    new IntentHelper(intent).putRoute(route);
    context.startActivity(intent);    
  }
  
  public void showRoute(Route route) {    
    String view = getNavigationPreferences().getString(VIEW, "schedule");
    if ( VIEW_SCHEDULE.equals(view)) {
      showRoute(route, ScheduleActivity.class);      
    } else if ( VIEW_STOPS.equals(view)) {
      showRoute(route, StopsActivity.class);
    } else if ( VIEW_MAP.equals(view)) {
      showRoute(route, RouteMapActivity.class);
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

  public void showInBrowser(Route route) {
    Uri uri = Uri.parse(Info.routeManager(context).getUri(route));
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
    context.startActivity(browserIntent);   
   }
  
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    Route route = getRoute();

    switch (item.getItemId()) {
      case R.id.recent_routes:
        RoutesActivity.showRecent(context);
        break;
      case R.id.all_routes:
        RoutesActivity.showAll(context);
        break;
      case R.id.schedule:
        setDefaultView(VIEW_SCHEDULE);
        showRoute(route, ScheduleActivity.class);
        handled = true;
        break;
      case R.id.stops:
        setDefaultView(VIEW_STOPS);
        showRoute(route, StopsActivity.class);
        handled = true;
        break;
      case R.id.map:
        setDefaultView(VIEW_MAP);
        showRoute(route, RouteMapActivity.class);
        handled = true;
        break;
      case R.id.track:
        setDefaultView(VIEW_TRACK);
        showRoute(route, TrackActivity.class);
        handled = true;
        break;
      case R.id.browse:
        showInBrowser(route);
        handled = true;
        break;
      default:
        break;
    }
    return handled;
  } 

  private File recentRoutesFile() {
    File cacheDir = context.getCacheDir();
    return new File(cacheDir, "recent-routes.xml");
  }
  MRU<Route> getRecentRoutes() {
    if ( mru != null ) {
      return mru;
    }
    mru = new MRU<Route>(MRU_SIZE);
    try {
      List<Route> routes = RouteHandler.parse(recentRoutesFile());
      for( Route route: routes ) {
        mru.add(mru.size(), route);
      }
    } catch(IOException e) {
    }
    return mru;
  }
  void saveRecentRoutes() {
    if ( mru == null ) {
      return;
    }
    try {
      RouteWriter writer = new RouteWriter();
      writer.setIncludeSchedule(false);
      writer.setIncludeStops(false);
      writer.writeRoutes(mru, recentRoutesFile());
    } catch( IOException e ) {
      throw new RuntimeException(e);
    }
  }
}