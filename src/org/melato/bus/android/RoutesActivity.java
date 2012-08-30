package org.melato.bus.android;

import java.util.List;

import org.melato.bus.model.Route;

import android.app.ListActivity;
import android.content.Intent;
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
  
  public void showSchedule(Route route) {
   Intent intent = new Intent(this, ScheduleActivity.class);
   intent.putExtra(Info.KEY_ROUTE, route.qualifiedName());
   startActivity(intent);
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
    Route route = routes.get(position);
    showSchedule(route);
  }

  class RoutesAdapter extends ArrayAdapter<Route> {
    public RoutesAdapter() {
      super(RoutesActivity.this, R.layout.route_item, routes);
    }
  }  
}