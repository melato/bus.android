package org.melato.bus.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.melato.bus.android.model.NearbyStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Earth;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
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
  
  private void sort( NearbyStop[] array, Point location ) {
    for(NearbyStop stop: array ) {
      stop.distance = Earth.distance(stop.getWaypoint(), location);          
    }
    Arrays.sort(array);    
  }
  
  void load(Point location) {
    long time = System.currentTimeMillis();
    RouteManager routeManager = Info.routeManager();
    Log.i( "melato.org", "nearby.load start" );
    List<Waypoint> waypoints = routeManager.findNearbyStops(location, Info.NEARBY_TARGET_DISTANCE);
    List<NearbyStop> nearby = new ArrayList<NearbyStop>();
    for( Waypoint p: waypoints ) {
      for( String link: p.getLinks() ) {
        Route route = routeManager.getRoute(link);
        if ( route != null ) {
          NearbyStop stop = new NearbyStop(p, route);
          nearby.add(stop);
        }
      }
    }
    time = (System.currentTimeMillis() - time)/1000;
    Log.i( "melato.org", "nearby.load count=" + nearby.size() + " time=" + time );
    NearbyStop[] array = nearby.toArray(new NearbyStop[0]);
    sort(array, location);
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
      Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if ( last == null )
        last = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      setLocation(last);
  }

  
  @Override
  protected void onDestroy() {
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    locationManager.removeUpdates(this);
    super.onDestroy();
  }

  public void showSchedule(Route route) {
    Intent intent = new Intent(this, ScheduleActivity.class);
    intent.putExtra(Info.KEY_ROUTE, route.qualifiedName());
    startActivity(intent);
   }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    NearbyStop p = stops[position];
    showSchedule(p.getRoute());
 }

  class NearbyAdapter extends ArrayAdapter<NearbyStop> {
    public NearbyAdapter() {
      super(NearbyActivity.this, R.layout.list_item, stops);
    }
  }
  
  public void setLocation(Point here) {
    if ( haveLocation ) {
      return;
      //sort(stops, here);
    }
    if ( here != null ) {
      haveLocation = true;
      load(here);
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