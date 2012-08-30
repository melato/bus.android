package org.melato.bus.android;

import java.util.List;

import org.melato.bus.android.model.WaypointDistance;
import org.melato.bus.model.Route;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class StopsActivity extends ListActivity {
  Route route;
  GPX gpx;
  WaypointDistance[] stops;

  public StopsActivity() {    
  }
    
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String name = (String) getIntent().getSerializableExtra(Activities.KEY_ROUTE);
    route = Info.routeManager().getRoute(name);
    gpx = Info.routeManager().loadGPX(route);
    List<Waypoint> waypoints = gpx.getRoutes().get(0).path.getWaypoints();
    stops = new WaypointDistance[waypoints.size()];
    double pathLength = 0;
    Waypoint previous = null;
    for( int i = 0; i < stops.length; i++ ) {
      Waypoint p = waypoints.get(i);
      if ( i != 0 ) {
        pathLength += Earth.distance(previous, p);
      }
      stops[i] = new WaypointDistance(p, (float) pathLength);
      previous = p;
    }
    setListAdapter(new StopsAdapter());
}

  class StopsAdapter extends ArrayAdapter<WaypointDistance> {
    public StopsAdapter() {
      super(StopsActivity.this, R.layout.list_item, stops);
      Log.i("melato.org", "stops.length=" + stops.length );
    }
  }
    
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.stops_menu, menu);
     return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item)
  {
     boolean handled = false;

     switch (item.getItemId())
     {
        case R.id.schedule:
          Activities.showSchedule(this, route);
          handled = true;
          break;
        default:
          break;
     }
     return handled;
  }
  
}