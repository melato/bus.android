package org.melato.bus.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteGroup;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;

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

  public void putRoute(Route route) {
    intent.putExtra(KEY_ROUTE, route.getRouteId().toString());
  }
  
  private Route getRoute(String key) {
    String textId = (String) intent.getSerializableExtra(key);
    if ( textId != null ) {
      RouteId routeId = new RouteId(textId);
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
      intent.putExtra(keyRoute(i), routes[i].getRouteId().toString());
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