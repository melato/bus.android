package org.melato.android.gpx.map;

import org.melato.gpx.Point;

import android.location.Location;

import com.google.android.maps.GeoPoint;

public class GMap {
  public static GeoPoint geoPoint(float lat, float lon) {
    return new GeoPoint( (int) (lat * 1E6f), (int)(lon * 1E6f));
  }
  public static GeoPoint geoPoint(Point p) {   
    return geoPoint(p.getLat(), p.getLon());    
  }
  public static GeoPoint geoPoint(Location loc) {
    if ( loc == null )
      return null;
    return geoPoint((float) loc.getLatitude(), (float) loc.getLongitude());
  }

  public static Point point(GeoPoint p) {   
    return new Point( p.getLatitudeE6() / 1E6f,  p.getLongitudeE6() / 1E6f);    
  }
}
