package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.model.RouteGroup;

import android.content.Intent;
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
public class AllRoutesActivity extends BusActivity {
  private List<RouteGroup> groups;
  
  public AllRoutesActivity() {    
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      groups = RouteGroup.group(getRouteManager().getRoutes());
      setListAdapter(new RouteGrousAdapter());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    RouteGroup group = groups.get(position);
    Intent intent = new Intent(this, RouteGroupActivity.class);
    IntentHelper helper = new IntentHelper(intent);
    helper.putRoutes(group);
    startActivity(intent);    
  }

  class RouteGrousAdapter extends ArrayAdapter<RouteGroup> {
    public RouteGrousAdapter() {
      super(AllRoutesActivity.this, R.layout.list_item, groups);
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