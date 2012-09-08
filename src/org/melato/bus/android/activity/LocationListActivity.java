package org.melato.bus.android.activity;

import java.util.Date;

import org.melato.gpx.Point;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * A list activity that is also a location listener and maintains a current location.
 * This must be separated from ListActivity, since it doesn't really care what type of activity it is.
 * It was only because most bus views are list activities (except for the map activities which are not).
 * For now we're just using onCreate/onDestroy to request/remove location updates.
 * We're also allowing subclasses to use setLocation() in order to get notified of updates.
 * @author Alex Athanasopoulos
 */
public class LocationListActivity extends ListActivity implements LocationListener {
  private Point   location;
  private Date    locationDate;
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
    locationDate = new Date();
  }
    
  public Point getLocation() {
    return location;
  }
  
  public Date getLocationDate() {
    return locationDate;
  }

  @Override
  public void onLocationChanged(Location loc) {
    setLocation(Locations.location2Point(loc));
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