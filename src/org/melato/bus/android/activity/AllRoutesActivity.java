package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.model.RouteGroup;

/**
 * Displays the list of all routes
 * @author Alex Athanasopoulos
 *
 */
public class AllRoutesActivity extends RoutesActivity {
  protected Object[] initialRoutes() {
    List<RouteGroup> groups = RouteGroup.group(activities.getRouteManager().getRoutes());
    return groups.toArray(new RouteGroup[0]);
  }  
}