package org.melato.bus.android.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.melato.android.AndroidLogger;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.help.AboutActivity;
import org.melato.bus.android.map.RouteMapActivity;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.xml.RouteHandler;
import org.melato.bus.model.xml.RouteWriter;
import org.melato.log.Log;
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
  
  public static final String VIEW = "view";
  public static final String VIEW_SCHEDULE = "schedule";
  public static final String VIEW_STOPS = "stops";
  public static final String VIEW_MAP = "map";
  public static final String VIEW_TRACK = "track";
  
  public static final int MRU_SIZE = 10;
  
  MRU<Route> mru;
  
  private Context context;

  private IntentHelper intentHelper;
  
  public BusActivities(Activity activity) {
    super();
    this.context = activity;    
    intentHelper = new IntentHelper(activity);
  }
  
  private RouteManager routeManager;
  
  
  public RouteManager getRouteManager() {
    if ( routeManager == null ) {
      routeManager = Info.routeManager(context);
    }
    return routeManager;
  }

  
  public RouteId getRouteId() {
    return intentHelper.getRouteId();
  }
  public Route getRoute() {
    return intentHelper.getRoute();
  }

  protected SharedPreferences getNavigationPreferences() {
    return context.getSharedPreferences(NAV_PREFERENCES, Context.MODE_PRIVATE);
  }
  
  public void showRoute(Route route, RouteStop stop, Class<? extends Activity> activity) {
    getRecentRoutes().add(route);
    saveRecentRoutes();
    Intent intent = new Intent(context, activity);
    new IntentHelper(intent).putRouteStop(stop);
    context.startActivity(intent);    
  }
  public void showRoute(Route route, Class<? extends Activity> activity) {
    RouteStop stop = intentHelper.getRouteStop();
    if ( stop != null && ! route.getRouteId().equals(stop.getRouteId())) {
      stop = new RouteStop(route.getRouteId());
    }
    showRoute( route, stop, activity);
  }
  
  public void showRoute(Route route, RouteStop stop) {
    String view = getNavigationPreferences().getString(VIEW, "schedule");
    Class<? extends Activity> activity = null;
    if ( VIEW_SCHEDULE.equals(view)) {
      activity = ScheduleActivity.class;      
    } else if ( VIEW_STOPS.equals(view)) {
      activity = StopsActivity.class;
    } else if ( VIEW_MAP.equals(view)) {
      activity = RouteMapActivity.class;
    } else {
      activity = ScheduleActivity.class;      
    }
    if ( activity != null )
      showRoute(route, stop, activity );      
  }
  public void showRoute(Route route) {
    showRoute(route, new RouteStop(route.getRouteId()));
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
  
  public void showAbout() {
    context.startActivity( new Intent(context, AboutActivity.class));    
  }
  
  public void showInBrowser(Route route) {
    Uri uri = Uri.parse(Info.routeManager(context).getUri(route));
    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
    context.startActivity(browserIntent);   
   }
  
  private void benchmark() {
    Log.setLogger(new AndroidLogger(context));
    getRouteManager().benchmark();
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
        if ( route != null ) {
          setDefaultView(VIEW_MAP);
          showRoute(route, RouteMapActivity.class);
        } else {
          context.startActivity(new Intent(context, RouteMapActivity.class));    
        }
        handled = true;
        break;
      case R.id.about:
        handled = true;
        showAbout();
        break;
      /*
      case R.id.benchmark:
        handled = true;
        benchmark();    
        break;
      */
      case R.id.browse:
        showInBrowser(route);
        handled = true;
        break;
      case R.id.all_schedules:
        showRoute(route, SchedulesActivity.class);
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
      List<Route> routes = RouteHandler.parseRoutes(recentRoutesFile());
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
      writer.writeRoutes(mru, recentRoutesFile());
    } catch( IOException e ) {
      throw new RuntimeException(e);
    }
  }
}