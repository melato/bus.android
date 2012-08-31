package org.melato.bus.android;

import java.util.List;

import org.melato.bus.model.Route;

/**
 * Displays a list of routes
 * @author Alex Athanasopoulos
 *
 */
public class RecentRoutesActivity extends RoutesActivity {
  @Override
  protected List<Route> loadRoutes() {
    return activities.getRecentRoutes();
  }
 }