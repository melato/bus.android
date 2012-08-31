package org.melato.bus.android;

import java.util.List;

import org.melato.bus.model.Route;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
     boolean handled = false;

     switch (item.getItemId())
     {
        case R.id.nearby:
          showNearby();
          handled = true;
          break;
        default:
          break;
     }
     return handled;
  }
   
}