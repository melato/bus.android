package org.melato.bus.android.activity;

import java.util.List;

import org.melato.android.AndroidLogger;
import org.melato.bus.android.R;
import org.melato.geometry.gpx.PathTracker;
import org.melato.gps.Earth;
import org.melato.gps.Point;
import org.melato.gpx.GPX;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.log.Log;

import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StopsContext extends LocationContext {
  private List<Waypoint> waypoints;
  private Path path;
  private PathTracker pathTracker;
  private int closestStop = -1;
  private boolean isSelected;
  private StopsAdapter adapter;

  private ListActivity list;

  public void setGPX(GPX gpx) {
    waypoints = gpx.getRoutes().get(0).getWaypoints();
    path = new Path(waypoints);
    pathTracker = new PathTracker();
    pathTracker.setPath(path);
    list.setListAdapter(adapter = new StopsAdapter());
    setEnabledLocations(true);
  }
  
  public StopsContext(ListActivity activity) {
    super(activity);
    this.list = activity;
  }

  @Override
  public void setLocation(Point point) {
    super.setLocation(point);
    if ( point != null) {
      pathTracker.setLocation(point);
      closestStop = pathTracker.getNearestIndex();
    }
    adapter.notifyDataSetChanged();
    // scroll to the nearest stop, if we haven't done it yet.
    if ( ! isSelected && closestStop >= 0 ) {
      isSelected = true;
      list.setSelection(closestStop);
    }
  }

  
  public List<Waypoint> getWaypoints() {
    return waypoints;
  }


  class StopsAdapter extends ArrayAdapter<Waypoint> {
    TextView view;

    public StopsAdapter() {
      super(context, R.layout.list_item, waypoints); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      Waypoint waypoint = waypoints.get(position);
      String text = waypoint.getName();
      Point here = getLocation();
      if ( here != null && closestStop == position ) {
        float straightDistance = Earth.distance(here, waypoint); 
        text += " " + UI.straightDistance(straightDistance);
      }
      UI.highlight(view, position == closestStop );
      view.setText( text );
      return view;
    }
  }

}
