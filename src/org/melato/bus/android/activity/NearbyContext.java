package org.melato.bus.android.activity;

import java.util.Arrays;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.NearbyStop;
import org.melato.bus.client.WaypointDistance;
import org.melato.gpx.Point;
import org.melato.log.Clock;
import org.melato.log.Log;

import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NearbyContext extends LocationContext {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  private ListActivity activity;
  private NearbyAdapter adapter;

  class NearbyAdapter extends ArrayAdapter<NearbyStop> {
    public NearbyAdapter() {
      super(context, R.layout.list_item, stops);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      int group = stops[position].getGroup();
      if ( group == 1 ) {
        view.setBackgroundColor(context.getResources().getColor(R.color.group1_background));
        view.setTextColor(context.getResources().getColor(R.color.group1_text));
      } else {
        view.setBackgroundColor(context.getResources().getColor(R.color.group2_background));
        view.setTextColor(context.getResources().getColor(R.color.group2_text));
      }
      return view;
    }
    
  }
  
  void colorStops(NearbyStop[] stops) {
    int group = 0;
    String groupSymbol = null;
    for( int i = 0; i < stops.length; i++ ) {
      String symbol = stops[i].getWaypoint().getSym();
      if ( ! symbol.equals(groupSymbol)) {
        group++;
        groupSymbol = symbol;
      }
      stops[i].setGroup(group % 2);      
    }
  }
  void load(Point location) {
    Clock clock = new Clock("NearbyContext.load()" );
    stops = Info.nearbyManager(context).getNearby(location);
    colorStops(stops);
    Log.info(clock);
  }
    
  public NearbyStop getStop(int index) {
    return stops[index];
  }
  
  public NearbyContext(ListActivity activity) {
    super(activity);
    this.activity = activity;
    Point center = IntentHelper.getLocation(activity.getIntent());
    Log.info( "NearbyContext center=" + center);
    if ( center != null) {
      setLocation(center);
    } else {
      Point p = Info.nearbyManager(activity).getLastLocation();
      if ( p != null ) {
        setLocation(p);
      }
      haveLocation = false;
      setEnabledLocations(true);
    }
  }

  public void setLocation(Point point) {
    super.setLocation(point);
    if ( point == null )
      return;
    //Log.info( "setLocation point=" + point + " haveLocation=" + haveLocation );
    if ( haveLocation ) {
      WaypointDistance.setDistance(stops, point);
      Arrays.sort(stops);
      adapter.notifyDataSetChanged();
   } else {
      haveLocation = true;
      load(point);
      Log.info( "activity: " + activity );
      activity.setListAdapter(adapter = new NearbyAdapter());    
    }
  }
 

}
