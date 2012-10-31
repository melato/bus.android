package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.bus.model.Route;

import android.view.Menu;
import android.view.MenuInflater;

/**
 * Displays the list of recent routes
 * @author Alex Athanasopoulos
 *
 */
public class RecentRoutesActivity extends RoutesActivity {  
  protected Object[] initialRoutes() {
    return activities.getRecentRoutes().toArray(new Route[0]);
  }  
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.recent_routes_menu, menu);
     return true;
  }

  
 }