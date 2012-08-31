package org.melato.bus.android.map;

import org.melato.gpx.Waypoint;

import com.google.android.maps.GeoPoint;

public class Maps {
  public static GeoPoint geoPoint(Waypoint p) {   
    return new GeoPoint( (int) (p.getLat() * 1E6), (int)(p.getLon()*1E6));    
  }
}
