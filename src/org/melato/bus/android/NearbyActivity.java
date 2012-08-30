package org.melato.bus.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.melato.bus.android.model.NearbyStop;
import org.melato.bus.android.model.WaypointDistance;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Earth;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class NearbyActivity extends ListActivity implements LocationListener {
  private NearbyStop[] stops = new NearbyStop[0];
  private boolean haveLocation;
  
  void load(Point location) {
    long time = System.currentTimeMillis();
    RouteManager routeManager = Info.routeManager();
    Log.i( "melato.org", "nearby.load start" );
    List<Waypoint> list = routeManager.findNearbyStops(location, Info.NEARBY_TARGET_DISTANCE);
    Log.i( "melato.org", "nearby.load count=" + list.size() + " time=" + time );
    Waypoint[] waypoints = WaypointDistance.sort( list, location );
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
    time = (System.currentTimeMillis() - time)/1000;
    NearbyStop[] array = nearby.toArray(new NearbyStop[0]);
    this.stops = array;
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
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 5f, this);
      Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if ( location == null )
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      setLocation(location);
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
      update();
    }
    if ( point != null ) {
      haveLocation = true;
      load(point);
      update();
    }
  }
  
  public static Point location2Point(Location loc) {
    if ( loc == null )
      return null;
    Point p = new Point( (float) loc.getLatitude(), (float) loc.getLongitude());
    return p;
  }
  
  public void setLocation(Location loc) {
    setLocation(location2Point(loc));
  }
  
  @Override
  public void onLocationChanged(Location location) {
    setLocation(location);
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