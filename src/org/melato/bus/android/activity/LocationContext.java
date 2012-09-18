package org.melato.bus.android.activity;

import org.melato.android.location.Locations;
import org.melato.gpx.Point;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * A location listener that attaches itself to an activity and maintains a current location.
 * Subclass and override setLocation() to do something with the locations.
 * The activity must call close() from its onDestroy() method
 * to remove the listener from the LocationManager.
 * @author Alex Athanasopoulos
 */
public class LocationContext implements LocationListener {
  protected Context context;
  private Point   location;
  private boolean enabledLocations;
  

  public LocationContext(Context context) {
    super();
    this.context = context;
  }
  
  public void setEnabledLocations(boolean enabled) {
    if ( enabledLocations == enabled )
      return;
    LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    if ( enabled ) {
      this.enabledLocations = true;
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 100f, this );
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, this);
      Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if ( location == null )
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      onLocationChanged(location);
    } else {
      this.enabledLocations = false;
      locationManager.removeUpdates(this);      
    }
  }
  
  /** remove location updates. */
  public void close() {
    setEnabledLocations(false);
  }

  public void setLocation(Point point) {
    if ( point == null )
      return;
    location = point;
  }
    
  public Point getLocation() {
    return location;
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