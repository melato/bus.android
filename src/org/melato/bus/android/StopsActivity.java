package org.melato.bus.android;

import java.util.List;

import org.melato.bus.android.model.WaypointDistance;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays the schedule for a route
 * 
 * @author Alex Athanasopoulos
 * 
 */
public class StopsActivity extends BusActivity {
  GPX gpx;
  WaypointDistance[] stops;
  int closestStop = -1;
  boolean isSelected;

  public StopsActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    String name = (String) getIntent().getSerializableExtra(KEY_ROUTE);
    route = Info.routeManager().getRoute(name);
    String title = String.format( getResources().getString(R.string.stops_title),
        route.qualifiedName(), route.getTitle());
    setTitle(title);

    gpx = Info.routeManager().loadGPX(route);
    List<Waypoint> waypoints = gpx.getRoutes().get(0).path.getWaypoints();
    stops = new WaypointDistance[waypoints.size()];
    double pathLength = 0;
    Waypoint previous = null;
    for (int i = 0; i < stops.length; i++) {
      Waypoint p = waypoints.get(i);
      if (i != 0) {
        pathLength += Earth.distance(previous, p);
      }
      stops[i] = new WaypointDistance(p, (float) pathLength);
      previous = p;
    }
    setListAdapter(new StopsAdapter());
    setEnabledLocations(true);
  }

  private void findClosestStop(Point point) {
    if ( point == null ) {
      closestStop = -1;
      return;
    }
      
    float minDistance = 0;
    
    for( int i = 0; i < stops.length; i++ ) {
      float d = Earth.distance(point, stops[i].getWaypoint());
      if ( i == 0 || d < minDistance ) {
        minDistance = d;
        closestStop = i;
      }
    }
  }

  @Override
  public void setLocation(Point point) {
    super.setLocation(point);
    findClosestStop(point);
    
    // scroll to the nearest stop, if we haven't done it yet.
    if ( ! isSelected && closestStop >= 0 ) {
      isSelected = true;
      setSelection(closestStop);
    }
  }

  class StopsAdapter extends ArrayAdapter<WaypointDistance> {
    TextView view;

    public StopsAdapter() {
      super(StopsActivity.this, R.layout.list_item, stops);
      Log.i("melato.org", "stops.length=" + stops.length);
    }

    private String distanceSuffix(WaypointDistance stop) {
      Point here = getLocation();
      if ( here != null ) {
        return " (" + WaypointDistance.formatDistance(Earth.distance(here, stop.getWaypoint())) + ")";
      }
      return "";
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      WaypointDistance stop = stops[position];
      if ( position == closestStop ) {
        view.setText( "* " + stop + distanceSuffix(stop) );
      } else if (position == closestStop + 1 ) {
        view.setText( stop + distanceSuffix(stop) );
      }
      return view;
    }

  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stops_menu, menu);
    return true;
  }
}