package org.melato.bus.android.activity;

import org.melato.bus.model.Route;

/**
 * Displays the list of recent routes
 * @author Alex Athanasopoulos
 *
 */
public class RecentRoutesActivity extends RoutesActivity {  
  protected Object[] initialRoutes() {
    return activities.getRecentRoutes().toArray(new Route[0]);
  }  
 }