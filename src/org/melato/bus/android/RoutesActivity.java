package org.melato.bus.android;

import java.util.List;

import org.melato.bus.model.Route;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a list of routes
 * @author Alex Athanasopoulos
 *
 */
public class RoutesActivity extends BusActivity {
  List<Route> routes;
  
  public RoutesActivity() {    
  }
  
  protected List<Route> loadRoutes() {
    List<Route> routes = activities.getRecentRoutes();
    if ( routes.isEmpty() ) {
      return Info.routeManager(this).getRoutes();
    }
    return routes;
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      routes = loadRoutes();
      setListAdapter(new RoutesAdapter());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Route route = routes.get(position);
    showRoute(route);
  }

  class RoutesAdapter extends ArrayAdapter<Route> {
    public RoutesAdapter() {
      super(RoutesActivity.this, R.layout.list_item, routes);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.routes_menu, menu);
     return true;
  }

 }