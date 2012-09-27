package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.android.help.HelpActivity;
import org.melato.bus.android.update.UpdateActivity;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteGroup;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a list of routes
 * @author Alex Athanasopoulos
 *
 */
public class RoutesActivity extends ListActivity {
  public static final String TYPE_KEY = "routes_type";
  public static final String RECENT = "recent";
  public static final String ALL = "all";
  
  public BusActivities activities;
  private Object[] items = new Object[0];

  private void setRoutes(List<Route> routes) {
    items = routes.toArray(new Route[0]);    
  }

  private boolean initIntentRoutes() {    
    IntentHelper helper = new IntentHelper(this);
    List<Route> routes = helper.getRoutes();
    if ( routes != null && ! routes.isEmpty() ) {
      setRoutes(routes);
      setTitle(R.string.routes);
      return true;
    }
    return false;
  }
  
  private boolean initRecentRoutes() {
    List<Route> routes = activities.getRecentRoutes();
    /*
     *  make a copy of the recent routes list.
     *  otherwise the order of the routes may change without notice
     *  and may not be in sync with the displayed order.
     *  toArray() makes a copy.
     */
    setRoutes(routes);
    if ( ! routes.isEmpty() ) {
      setTitle(R.string.routes);
      return true;
    }
    return false;
  }
  
  private boolean initAllRoutes() {
    setTitle(R.string.all_routes);
    List<RouteGroup> groups = RouteGroup.group(activities.getRouteManager().getRoutes());
    items = groups.toArray(new RouteGroup[0]);
    setTitle(R.string.all_routes);
    return true;
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if ( ! UpdateActivity.checkUpdates(this) ) {
        return;
      }
      activities = new BusActivities(this);
      String type = (String) getIntent().getSerializableExtra(TYPE_KEY);
      if ( RECENT.equals(type)) {
        initRecentRoutes();
      } else if ( ALL.equals(type)) {
        initAllRoutes();
      } else {
        boolean init = initIntentRoutes();
        if ( ! init )
          init = initRecentRoutes();
        if ( ! init )
          initAllRoutes();
      }
      setListAdapter(new RoutesAdapter());
  }

  void showGroup(RouteGroup group) {
    if ( group.getRoutes().length == 1 ) {
      activities.showRoute(group.getRoutes()[0]);
    } else {
      Intent intent = new Intent(this, RoutesActivity.class);
      IntentHelper helper = new IntentHelper(intent);
      helper.putRoutes(group);
      startActivity(intent);
    }
  }
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Object item = items[position];
    if ( item instanceof Route ) {
      activities.showRoute((Route)item);
    } else if ( item instanceof RouteGroup ) {
      showGroup((RouteGroup)item);
    }
  }

  class RoutesAdapter extends ArrayAdapter<Object> {
    public RoutesAdapter() {
      super(RoutesActivity.this, R.layout.list_item, items);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      Route route = null;
      if ( items[position] instanceof Route ) {
        route = (Route) items[position];
      } else if ( items[position] instanceof RouteGroup ) {
        RouteGroup group = (RouteGroup) items[position];
        Route[] routes = group.getRoutes();
        if ( routes.length > 0 ) {
          route = routes[0];
          for( int i = 0; i < routes.length; i++ ) {
            if ( ! route.isSameColor(routes[i])) {
              route = null;
              break;
            }
          }
        }
      }
      if ( route != null ) {
        view.setTextColor(UI.routeColor(route.getColor()));
        view.setBackgroundColor(UI.routeColor(route.getBackgroundColor()));
      } else {
        view.setTextColor(Color.BLACK);
        view.setBackgroundColor(Color.WHITE);        
      }
      return view;
    }
}
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.routes_menu, menu);
     HelpActivity.addItem(menu, this, R.string.help_routes);
     return true;
  }

  public static void showRecent(Context context) {
    Intent intent = new Intent(context, RoutesActivity.class);
    intent.putExtra(RoutesActivity.TYPE_KEY, RoutesActivity.RECENT);
    context.startActivity(intent);        
  }
  
  public static void showAll(Context context) {
    Intent intent = new Intent(context, RoutesActivity.class);
    intent.putExtra(RoutesActivity.TYPE_KEY, RoutesActivity.ALL);
    context.startActivity(intent);        
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;

    switch (item.getItemId()) {
      case R.id.nearby:
        startActivity(new Intent(this, NearbyActivity.class));
        handled = true;
        break;
      case R.id.all_routes:
        initAllRoutes();
        setListAdapter(new RoutesAdapter());
        handled = true;
        break;
      case R.id.recent_routes:
        initRecentRoutes();
        setListAdapter(new RoutesAdapter());
        handled = true;
        break;
      default:
        break;
    }
    if ( ! handled ) {
      handled = activities.onOptionsItemSelected(item);
    }
    return handled;
  } 
 }