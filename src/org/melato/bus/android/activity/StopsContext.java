package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.android.model.WaypointDistance;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StopsContext extends LocationContext {
  private WaypointDistance[] stops = new WaypointDistance[0];
  private int closestStop = -1;
  private boolean isSelected;
  private StopsAdapter adapter;

  private ListActivity list;

  public Waypoint getWaypoint(int index) {
    return stops[index].getWaypoint();
  }
  public void setGPX(GPX gpx) {
    List<Waypoint> waypoints = gpx.getRoutes().get(0).getWaypoints();
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
    list.setListAdapter(adapter = new StopsAdapter());
    setEnabledLocations(true);
  }
  
  public StopsContext(ListActivity activity) {
    super(activity);
    this.list = activity;
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
    adapter.notifyDataSetChanged();
    // scroll to the nearest stop, if we haven't done it yet.
    if ( ! isSelected && closestStop >= 0 ) {
      isSelected = true;
      list.setSelection(closestStop);
    }
  }

  class StopsAdapter extends ArrayAdapter<WaypointDistance> {
    TextView view;

    public StopsAdapter() {
      super(context, R.layout.list_item, stops);
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
      } else if (position == closestStop + 1 || position == closestStop -1 ) {
        view.setText( stop + distanceSuffix(stop) );
      }
      return view;
    }
  }

}
