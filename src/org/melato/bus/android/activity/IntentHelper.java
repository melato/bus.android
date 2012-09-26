package org.melato.bus.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteGroup;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gps.Point;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


/**
 * puts and gets bus datatypes to an Intent.
 * @author Alex Athanasopoulos
 */
public class IntentHelper  {
  public static final String KEY_ROUTE = "org.melato.bus.android.route";
  public static final String KEY_ROUTE_COUNT = "org.melato.bus.android.route_count";
  public static final String KEY_LATITUDE = "org.melato.android.gpx.lat";
  public static final String KEY_LONGITUDE = "org.melato.android.gpx.lon";
  
  private Intent    intent;
  private Context   context;
  private RouteManager routeManager;

  public IntentHelper(Activity activity) {
    super();
    this.intent = activity.getIntent();
    this.context = activity;
  }
  
  public RouteManager getRouteManager() {
    if ( routeManager == null) {
      routeManager = Info.routeManager(context);
    }
    return routeManager;
  }

  public IntentHelper(Intent intent) {
    super();
    this.intent = intent;
  }

  public static void putLocation(Intent intent, Point p) {
    intent.putExtra(KEY_LATITUDE, p.getLat());
    intent.putExtra(KEY_LONGITUDE, p.getLon());
  }
  
  public static Point getLocation(Intent intent) {
    Float lat = (Float) intent.getSerializableExtra(KEY_LATITUDE);
    Float lon = (Float) intent.getSerializableExtra(KEY_LONGITUDE);
    if ( lat != null && lon != null ) {
      return new Point(lat, lon);
    }
    return null;
  }
  
  
  public void putRoute(Route route) {
    intent.putExtra(KEY_ROUTE, route.getRouteId());
  }
  
  public void putRoute(String key, RouteId routeId) {
    intent.putExtra(key, routeId);
  }
  
  public void putRoute(RouteId routeId) {
    intent.putExtra(KEY_ROUTE, routeId);
  }
  
  private Route getRoute(String key) {
    RouteId routeId = (RouteId) intent.getSerializableExtra(key);
    /*
    RouteId routeId = null;
    String textId = (String) intent.getSerializableExtra(key);
    if ( textId != null ) {
      routeId = new RouteId(textId);
    }
    */
    if ( routeId != null ) {
      return getRouteManager().loadRoute(routeId);
    }
    return null;
  }
  public Route getRoute() {
    return getRoute(KEY_ROUTE);
  }
  
  private String keyRoute(int index) {
    return KEY_ROUTE + "." + index;
  }

  public void putRoutes(RouteGroup group) {
    Route[] routes = group.getRoutes();
    intent.putExtra(KEY_ROUTE_COUNT, routes.length );
    for(int i = 0; i < routes.length; i++ ) {
      putRoute(keyRoute(i), routes[i].getRouteId());
    }
  }
  public List<Route> getRoutes() {
    Integer count = (Integer) intent.getSerializableExtra(KEY_ROUTE_COUNT);
    List<Route> routes = new ArrayList<Route>();
    if ( count != null ) {
      for(int i = 0; i < count; i++ ) {
        Route route = getRoute(keyRoute(i));
        if ( route != null ) {
          routes.add(route);
        }
      }
    }
    return routes;
  }
}