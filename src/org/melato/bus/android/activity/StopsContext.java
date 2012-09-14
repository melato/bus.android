package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.client.WaypointDistance;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.gpx.util.PathTracker;
import org.melato.gpx.util.SimplePathTracker;

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
  private float closestPathDistance = 0;
  private boolean isSelected;
  private StopsAdapter adapter;

  private ListActivity list;

  public void setGPX(GPX gpx) {
    waypoints = gpx.getRoutes().get(0).getWaypoints();
    path = new Path(waypoints);
    pathTracker = new SimplePathTracker();
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
      closestPathDistance = pathTracker.getPosition();
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
      String text = waypoints.get(position).getName();
      if ( closestStop >= 0 ) {
        float d = path.getLength(position) - closestPathDistance;
        text += " (" + WaypointDistance.formatDistance(d) + ")";
      }
      UI.highlight(view, position == closestStop );
      view.setText( text );
      return view;
    }
  }

}
