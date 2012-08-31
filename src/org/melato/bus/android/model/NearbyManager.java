package org.melato.bus.android.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.GPXWriter;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

/**
 * Provides access to nearby stops.
 * It caches the results to a file, so that subsequent calls from a nearby location are faster.
 * @author Alex Athanasopoulos
 *
 */
public class NearbyManager {
  static final float TARGET_DISTANCE = 1000f;
  /** Extra distance to cache. */
  static final float CACHE_DISTANCE = 100f;
  static final String NEARBY_FILE = "nearby.gpx";
  static final String LAT = "lat";
  static final String LON = "lon";
  
  private RouteManager routeManager;
  private File          cacheDir;
  private Preferences preferences;  
  
  public NearbyManager(RouteManager routeManager, File cacheDir,
      Preferences preferences) {
    super();
    this.routeManager = routeManager;
    this.cacheDir = cacheDir;
    this.preferences = preferences;
  }

  public Point getLastLocation() {
    float lat = preferences.getFloat(LAT, Float.NaN);
    float lon = preferences.getFloat(LON, Float.NaN);
    if ( Float.isNaN(lat) || Float.isNaN(lon)) {
      return null;
    }
    return new Point(lat,lon);
  }
  
  private void setLastLocation(Point location) {
    preferences.putFloat(LAT, location.getLat());
    preferences.putFloat(LON, location.getLon());
    try {
      preferences.flush();
    } catch (BackingStoreException e) {
      throw new RuntimeException( e );
    }
  }
  
  private Waypoint[] filterDistance(List<Waypoint> waypoints, Point target) {    
    WaypointDistance[] array = WaypointDistance.createArray(waypoints, target);
    Arrays.sort(array);
    int size = 0;
    for( ; size < array.length; size++ ) {
      if ( array[size].getDistance() > TARGET_DISTANCE )
        break;
    }
    Waypoint[] result = new Waypoint[size];
    for( int i = 0; i < size; i++ ) {
      result[i] = array[i].getWaypoint();
    }
    return result;
  }
  
  public Waypoint[] getNearbyWaypoints(Point location) {
    Point lastLocation = getLastLocation();
    File file = new File(cacheDir, NEARBY_FILE ); 
    if ( lastLocation != null && Earth.distance(lastLocation, location) < CACHE_DISTANCE ) {
      try {
        GPXParser parser = new GPXParser();
        GPX gpx = parser.parse(file);
        return filterDistance(gpx.getWaypoints(), location);
      } catch( IOException e ) {
        file.delete();
      }
    }
    
    // not in cache.  filter the global list
    
    List<Waypoint> list = routeManager.findNearbyStops(location, TARGET_DISTANCE + CACHE_DISTANCE);
    GPX gpx = new GPX();
    gpx.setWaypoints(list);
    GPXWriter writer = new GPXWriter();
    try {
      writer.write(gpx,  file);
      setLastLocation(location);
    } catch( IOException e ) {
    }
    
    return filterDistance(list, location);
  }
    
  public NearbyStop[] getNearby(Point location) {
    Waypoint[] waypoints = getNearbyWaypoints(location);
    List<NearbyStop> nearby = new ArrayList<NearbyStop>();
    Set<String> routeIds = new HashSet<String>();
    for( Waypoint p: waypoints ) {
      for( String link: p.getLinks() ) {
        if ( ! routeIds.contains( link )) {
          routeIds.add(link);
          Route route = routeManager.getRoute(link);
          if ( route != null ) {
            NearbyStop stop = new NearbyStop(p, route);
            stop.setDistance(Earth.distance(p,  location));
            nearby.add(stop);
          }
        }
      }
    }
    NearbyStop[] array = nearby.toArray(new NearbyStop[0]);
    return array;
  }

}
