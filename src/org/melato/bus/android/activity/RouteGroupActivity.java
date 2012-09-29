package org.melato.bus.android.activity;

import java.util.Collections;
import java.util.List;

import org.melato.bus.model.Route;

import android.os.Bundle;

/**
 * Displays the list of recent routes
 * @author Alex Athanasopoulos
 *
 */
public class RouteGroupActivity extends RoutesActivity {
  private Route[] group;
  protected Object[] initialRoutes() {
    if ( group == null ) {
      IntentHelper helper = new IntentHelper(this);
      List<Route> routes = helper.getRoutes();
      if ( routes == null ) {
        routes = Collections.emptyList();
      }
      group = routes.toArray(new Route[0]);
    }
    return group;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initialRoutes();
    if ( group.length > 0 ) {
      setTitle(group[0].getLabel());
    }
  }
  
 }