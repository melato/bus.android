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
import android.widget.ArrayAdapter;

public class NearbyContext extends LocationContext {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  private ListActivity activity;
  private NearbyAdapter adapter;

  class NearbyAdapter extends ArrayAdapter<NearbyStop> {
    public NearbyAdapter() {
      super(context, R.layout.list_item, stops);
    }
  }
  
  void load(Point location) {
    Clock clock = new Clock("NearbyContext.load()" );
    stops = Info.nearbyManager(context).getNearby(location);
    Log.info(clock);
  }
    
  public NearbyStop getStop(int index) {
    return stops[index];
  }
  
  public NearbyContext(ListActivity activity) {
    super(activity);
    this.activity = activity;
    Log.info( "NearbyContext activity=" + activity);
    Point p = Info.nearbyManager(activity).getLastLocation();
    if ( p != null ) {
      setLocation(p);
    }
    haveLocation = false;
    setEnabledLocations(true);
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
