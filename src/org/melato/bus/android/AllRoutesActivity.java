package org.melato.bus.android;

import java.util.List;

import org.melato.bus.model.Route;

/**
 * Displays a list of routes
 * @author Alex Athanasopoulos
 *
 */
public class AllRoutesActivity extends RoutesActivity {
  @Override
  protected List<Route> loadRoutes() {
    return Info.routeManager(this).getRoutes();
  }
 }