package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.BusLogger;
import org.melato.bus.model.Route;
import org.melato.log.Log;

/**
 * Displays a list of routes that are stored in the Intent
 * @author Alex Athanasopoulos
 *
 */
public class RouteGroupActivity extends RoutesActivity {
  @Override
  protected List<Route> loadRoutes() {
    Log.setLogger(new BusLogger(this));
    IntentHelper helper = new IntentHelper(this);
    return helper.getRoutes();
  }
 }