package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.model.RouteGroup;

import android.view.Menu;
import android.view.MenuInflater;

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
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.all_routes_menu, menu);
     return true;
  }

  
}