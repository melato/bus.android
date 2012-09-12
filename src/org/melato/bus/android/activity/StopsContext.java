package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.client.WaypointDistance;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;

import android.app.ListActivity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StopsContext extends LocationContext {
  private List<Waypoint> waypoints;
  private Path path;
  private int closestStop = -1;
  private float closestPathDistance = 0;
  private boolean isSelected;
  private StopsAdapter adapter;

  private ListActivity list;

  public void setGPX(GPX gpx) {
    waypoints = gpx.getRoutes().get(0).getWaypoints();
    path = new Path(waypoints);
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
      closestStop = path.findNearestIndex(point);
      closestPathDistance = path.getPathLength(point);
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
        float d = path.getPathLength(position) - closestPathDistance;
        text += " (" + WaypointDistance.formatDistance(d) + ")";
      }
      /*
      if ( position == closestStop ) {
        view.setBackgroundColor(Color.CYAN);
        view.setTextColor(Color.BLACK);
        //text = "* " + text;
      } else {
        view.setBackgroundColor(Color.BLACK);
        view.setTextColor(Color.WHITE);
      }
      */
      Resources resources = context.getResources();
      if ( position == closestStop ) {
        view.setBackgroundColor(context.getResources().getColor(R.color.list_highlighted_background));
        view.setTextColor(context.getResources().getColor(R.color.list_highlighted_text));
      } else {
        view.setBackgroundColor(context.getResources().getColor(R.color.list_background));
        view.setTextColor(context.getResources().getColor(R.color.list_text));
      }
      view.setText( text );
      return view;
    }
  }

}
