package org.melato.bus.android.activity;

import java.util.Arrays;

import org.melato.bus.android.BusLogger;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.model.NearbyStop;
import org.melato.bus.android.model.WaypointDistance;
import org.melato.gpx.Point;
import org.melato.log.Log;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NearbyActivity extends BusActivity {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  NearbyAdapter adapter;

  void load(Point location) {
    Log.info( "Nearby.Activity load");
    stops = Info.nearbyManager(this).getNearby(location);
    Log.info( "Nearby.Activity loaded");
  }
  
  public NearbyActivity() {
  }

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.setLogger(new BusLogger(this));
      Log.info( "nearby.onCreate()");
      Point p = Info.nearbyManager(this).getLastLocation();
      if ( p != null ) {
        setLocation(p);
      }
      haveLocation = false;
      setEnabledLocations(true);
  }

  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    NearbyStop p = stops[position];
    showRoute(p.getRoute());
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
      adapter.notifyDataSetChanged();
   } else {
      haveLocation = true;
      load(point);
      setListAdapter(adapter = new NearbyAdapter());    
    }
  }
 
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.nearby_menu, menu);
     return true;
  }
 
}