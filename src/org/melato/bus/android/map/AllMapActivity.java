package org.melato.bus.android.map;

import org.melato.android.AndroidLogger;
import org.melato.android.gpx.map.Maps;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.BusActivities;
import org.melato.gpx.Point;
import org.melato.log.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/** An activity that displays all routes on the map. */
public class AllMapActivity extends MapActivity {
  private static final String KEY_ZOOM_LEVEL = "zoomLevel";
  private BusActivities activities;
  private MapView map;
  private RoutesOverlay routesOverlay;

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
  public GeoPoint getCurrentLocation() {
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    if ( loc == null )
      return null;
    return new GeoPoint((int) Math.round(loc.getLatitude() * 1E6), (int) Math.round(loc.getLongitude() * 1E6));    
  }
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.setLogger(new AndroidLogger(this));
      activities = new BusActivities(this);
      setContentView(R.layout.map);
      map = (MapView) findViewById(R.id.mapview);
      map.setBuiltInZoomControls(true);
      MapController mapController = map.getController();
      int zoom = getSharedPreferences(BusActivities.NAV_PREFERENCES, 0).getInt(KEY_ZOOM_LEVEL, 15 );
      zoom = 16;
      mapController.setZoom(zoom);
      GeoPoint center = getCurrentLocation();
      Point point = new Point(37.931496f,24.005596f);
      center = Maps.geoPoint(point);
      if ( center != null ) {
        mapController.setCenter(center);        
      }
      routesOverlay = new RoutesOverlay(activities.getRouteManager());
      map.getOverlays().add(routesOverlay);      
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    SharedPreferences.Editor edit = getSharedPreferences(BusActivities.NAV_PREFERENCES, 0).edit();
    edit.putInt( KEY_ZOOM_LEVEL, map.getZoomLevel());
    edit.commit();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.all_map_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.refresh:
        routesOverlay.refresh();
        handled = true;
        break;
    }
    if ( handled )
      return true;
    return activities.onOptionsItemSelected(item);
  }
  
}
