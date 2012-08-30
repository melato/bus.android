package org.melato.bus.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.melato.bus.model.BusStop;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.Point;
import org.melato.gpx.util.ProximityFinder;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class NearbyActivity extends ListActivity implements LocationListener {
  List<BusStop> stops;
  int   closestStop = -1;
  private Point location;
  
  List<BusStop> readStops(InputStream input) {
    if ( input == null ) {
      throw new RuntimeException( "Cannot find resource." );
    }
    try {
      GPXParser parser = new GPXParser();
      GPX gpx = parser.parse( input );
      return BusStop.listFromGPX(gpx);      
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  List<BusStop> loadStops() {
    return null;
  }
  
  List<BusStop> readStops(File file) {
    try {
      InputStream input = new FileInputStream(file);
      return readStops(input);
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public NearbyActivity() {
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setStops(loadStops());
      LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 100f, this );
      locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 5f, this);
      Location last = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
      if ( last == null )
        last = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
      setLocation(last);      
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  class BusListAdapter extends ArrayAdapter<BusStop> {
    public BusListAdapter() {
      super(NearbyActivity.this, R.layout.bus_stop_item, stops);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      convertView = super.getView(position, convertView, parent );
      TextView view = (TextView) convertView;
      String text = stops.get(position).toString();
      if ( closestStop == position ) {
        text = "-> " + text;
      }
      view.setText(text);
      return view;
    }

  }
  public void setStops(List<BusStop> stops) {
    this.stops = stops;
    setListAdapter(new BusListAdapter());
    //setListAdapter( new ArrayAdapter<BusStop>(this, R.layout.bus_stop_item, stops));           
    
  }
  
  public void setLocation(Point here) {
    if ( here == null )
      return;
    if ( here.equals(location)) {
      return;
    }
    location = here;
    ProximityFinder proximity = new ProximityFinder();
    proximity.setTargetDistance(1000f);
    proximity.setSequence(BusStop.asWaypointList(stops));
    int size = stops.size();
    for( int i = 0; i < size; i++ ) {
      stops.get(i).distanceFromStart = proximity.getPathLength(i);
    }
    closestStop = proximity.findClosestNearby(location);
    setStops(stops);
  }
  
  
  public void setLocation(Location loc) {
    if ( loc == null )
      return;
    Point p = new Point( (float) loc.getLatitude(), (float) loc.getLongitude());
    setLocation(p);
  }
  
  
  @Override
  public void onLocationChanged(Location location) {
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