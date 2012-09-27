package org.melato.bus.android.map;

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.help.HelpActivity;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
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

/** An activity that displays a map with one or more routes. */
public class RouteMapActivity extends MapActivity {
  private static final String KEY_ZOOM_LEVEL = "zoomLevel";
  private BusActivities activities;
  private MapView map;
  private RoutesOverlay routesOverlay;
  private boolean isShowingAll;

  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
  public GeoPoint getCurrentLocation() {
    LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    return GMap.geoPoint(loc);
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Log.info("RouteMapActivity.onCreate()");
      activities = new BusActivities(this);
      routesOverlay = new RoutesOverlay(this);
      Route route = activities.getRoute();
      if ( route != null ) {
        setTitle(route.getFullTitle());
        routesOverlay.addRoute(route.getRouteId());
        routesOverlay.setSelectedRoute(route.getRouteId());
      }

      setContentView(R.layout.map);
      map = (MapView) findViewById(R.id.mapview);
      map.setBuiltInZoomControls(true);
      
      MapController mapController = map.getController();
      int zoom = getSharedPreferences(BusActivities.NAV_PREFERENCES, 0).getInt(KEY_ZOOM_LEVEL, 15 );
      mapController.setZoom(zoom);
      GeoPoint center = routesOverlay.getCenter();
      if ( center != null ) {
        mapController.setCenter(center);
      }
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
    inflater.inflate(R.menu.map_menu, menu);
    HelpActivity.addItem(menu, this, R.string.help_map);
    return true;
  }
  
  class OnRoutesLoaded implements Runnable {
    @Override
    public void run() {
      setTitle(R.string.nearby);
      map.invalidate();
    }    
  }
  void showAllRoutes() {
    if ( ! isShowingAll ) {
      setTitle(R.string.loading);
    }
    routesOverlay.refresh();
    if ( ! isShowingAll ) {
      isShowingAll = true;
      RoutePointManager rm = RoutePointManager.getInstance(this);
      rm.runWhenLoaded(this, new OnRoutesLoaded());
    }
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.refresh:
        showAllRoutes();
        handled = true;
        break;
    }
    if ( handled )
      return true;
    return activities.onOptionsItemSelected(item);
  }
    
}
