package org.melato.bus.android;

import java.util.Arrays;

import org.melato.bus.android.model.NearbyStop;
import org.melato.bus.android.model.WaypointDistance;
import org.melato.gpx.Point;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NearbyActivity extends LocationListActivity {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  
  void load(Point location) {
    stops = Info.nearbyManager(this).getNearby(location);
  }
  
  public NearbyActivity() {
  }

  private void update() {
    setListAdapter(new NearbyAdapter());    
  }
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setEnabledLocations(true);
      //Log.setLogger( new BusLogger(this));
  }

  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    NearbyStop p = stops[position];
    Activities.showSchedule(this, p.getRoute());
 }

  class NearbyAdapter extends ArrayAdapter<NearbyStop> {
    public NearbyAdapter() {
      super(NearbyActivity.this, R.layout.list_item, stops);
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
    } else {
      haveLocation = true;
      load(point);
    }
    update();
  }
  
}