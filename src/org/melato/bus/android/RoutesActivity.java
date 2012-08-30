package org.melato.bus.android;

import java.util.List;

import org.melato.bus.model.Route;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a list of routes
 * @author Alex Athanasopoulos
 *
 */
public class RoutesActivity extends ListActivity {
  List<Route> routes;
  
  public RoutesActivity() {    
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      routes = Info.routeManager().getRoutes();
      setListAdapter(new RoutesAdapter());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  class RoutesAdapter extends ArrayAdapter<Route> {
    public RoutesAdapter() {
      super(RoutesActivity.this, R.layout.route_item, routes);
    }
  }  
}