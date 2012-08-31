package org.melato.bus.android;

import java.util.Arrays;

import org.melato.bus.android.model.NearbyStop;
import org.melato.bus.android.model.WaypointDistance;
import org.melato.gpx.Point;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NearbyActivity extends ListActivity implements LocationListener {
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
      LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 100f, this );
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, this);
      Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if ( location == null )
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      onLocationChanged(location);
  }

  
  @Override
  protected void onDestroy() {
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationManager.removeUpdates(this);
    super.onDestroy();
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
    if ( point == null )
      return;
    if ( haveLocation ) {
      WaypointDistance.setDistance(stops, point);
      Arrays.sort(stops);
    } else {
      haveLocation = true;
      load(point);
    }
    update();
  }
  
  public static Point location2Point(Location loc) {
    if ( loc == null )
      return null;
    Point p = new Point( (float) loc.getLatitude(), (float) loc.getLongitude());
    return p;
  }
  
  @Override
  public void onLocationChanged(Location loc) {
    setLocation(location2Point(loc));
  }
  @Override
  public void onProviderDisabled(String provider) {
  }
  @Override
  public void onProviderEnabled(String provider) {
  }
  @Override
  public void onStatusChanged(String provider, int status, Bundle extras) {
  }
}