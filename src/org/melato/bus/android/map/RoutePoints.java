package org.melato.bus.android.map;

import java.util.List;

import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.util.DelegateList;

import com.google.android.maps.GeoPoint;

public class RoutePoints {
  private int[] lat;
  private int[] lon;
  
  public int size() {
    return lat.length;
  }
  
  public int getLatitude6E(int i) {
    return lat[i];
  }
  public int getLongitude6E(int i) {
    return lon[i];
  }
  public GeoPoint getGeoPoint(int i) {
    return new GeoPoint(lat[i], lon[i]);
  }
  public boolean isInside(int i, int latMin, int latMax, int lonMin, int lonMax) {
    int lat = getLatitude6E(i);
    int lon = getLongitude6E(i);
    return latMin < lat && lat < latMax && lonMin < lon && lon < lonMax;     
  }
  
  public RoutePoints(List<Point> waypoints) {
    int n = waypoints.size();
    lat = new int[n];
    lon = new int[n];
    for( int i = 0; i < n; i++ ) {
      Point p = waypoints.get(i);
      lat[i] = (int) (p.getLat()*1e6f);
      lon[i] = (int) (p.getLon()*1e6f);
    }
  }
}
