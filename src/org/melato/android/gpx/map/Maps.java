package org.melato.android.gpx.map;

import org.melato.gpx.Point;

import com.google.android.maps.GeoPoint;

public class Maps {
  public static GeoPoint geoPoint(Point p) {   
    return new GeoPoint( (int) (p.getLat() * 1E6f), (int)(p.getLon() * 1E6f));    
  }
  public static Point point(GeoPoint p) {   
    return new Point( p.getLatitudeE6() / 1E6f,  p.getLongitudeE6() / 1E6f);    
  }
}
