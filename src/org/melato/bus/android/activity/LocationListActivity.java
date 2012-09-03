package org.melato.bus.android.activity;

import org.melato.gpx.Point;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * A list activity that is also a location listener and maintains a current location
 * @author Alex Athanasopoulos
 *
 */
public class LocationListActivity extends ListActivity implements LocationListener {
  private Point   location;
  private boolean enabledLocations;
  
  public LocationListActivity() {
  }

  protected void setEnabledLocations(boolean enabled) {
    if ( enabledLocations == enabled )
      return;
    if ( enabled ) {
      this.enabledLocations = true;
      LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 100f, this );
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, this);
      Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if ( location == null )
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      onLocationChanged(location);
    } else {
      this.enabledLocations = false;
      LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
      locationManager.removeUpdates(this);      
    }
  }
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
  }

  
  @Override
  protected void onDestroy() {
    setEnabledLocations(false);
    super.onDestroy();
  }

  public void setLocation(Point point) {
    if ( point == null )
      return;
    location = point;
  }
    
  public Point getLocation() {
    return location;
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