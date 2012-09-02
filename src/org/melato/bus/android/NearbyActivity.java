package org.melato.bus.android;

import java.util.Arrays;

import org.melato.bus.android.model.NearbyStop;
import org.melato.bus.android.model.WaypointDistance;
import org.melato.gpx.Point;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NearbyActivity extends BusActivity {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  NearbyAdapter adapter;

  void load(Point location) {
    stops = Info.nearbyManager(this).getNearby(location);
  }
  
  public NearbyActivity() {
  }

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
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
     inflater.inflate(R.menu.routes_menu, menu);
     return true;
  }
 
}