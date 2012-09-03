package org.melato.bus.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.model.Route;

/**
 * Displays a list of routes
 * @author Alex Athanasopoulos
 *
 */
public class RecentRoutesActivity extends RoutesActivity {
  /**
   *  make a copy of the recent routes list.
   *  otherwise the order of the routes may change without notice
   *  and may not be in sync with the displayed order.
   * @param routes
   * @return
   */
  public static List<Route> copyRoutes(List<Route> routes) {
    List<Route> copy = new ArrayList<Route>();
    copy.addAll(routes);
    return copy;
  }
  @Override
  protected List<Route> loadRoutes() {
    return copyRoutes( activities.getRecentRoutes() );
  }
 }