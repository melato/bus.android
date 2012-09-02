package org.melato.bus.android;

import org.melato.bus.android.map.GPXOverlay;
import org.melato.bus.android.map.Maps;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.util.AveragePoint;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class RouteMapActivity extends MapActivity {
  private static final String KEY_ZOOM_LEVEL = "zoomLevel";
  private BusActivities activities;
  private MapView map;
  private MyLocationOverlay myLocation; 

  @Override
  protected boolean isRouteDisplayed() {
    return true;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      Route route = activities.getRoute();
      String title = String.format( getResources().getString(R.string.map_title),
          route.qualifiedName(), route.getTitle() );
      setTitle(title);

      setContentView(R.layout.map);
      map = (MapView) findViewById(R.id.mapview);
      map.setBuiltInZoomControls(true);
      String name = (String) getIntent().getSerializableExtra(BusActivity.KEY_ROUTE);
      RouteManager routeManager = Info.routeManager(this);
      GPX gpx = routeManager.loadGPX(name);
      activities.setRoute(routeManager.getRoute(name));
      
      Point center = AveragePoint.getCenter(gpx );
      MapController mapController = map.getController();
      int zoom = getSharedPreferences(BusActivities.NAV_PREFERENCES, 0).getInt(KEY_ZOOM_LEVEL, 15 );
      mapController.setZoom(zoom);
      if ( center != null ) {
        mapController.setCenter(Maps.geoPoint(center));
      }

      GPXOverlay pathOverlay = new GPXOverlay(gpx);
      map.getOverlays().add(pathOverlay);
      
      myLocation = new MyLocationOverlay(this, map);
      map.getOverlays().add(myLocation);
      
      /*
      List<Waypoint> path = gpx.getRoutes().get(0).path.getWaypoints();
      Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
      WaypointsOverlay stopsOverlay = new WaypointsOverlay(drawable, this);
      stopsOverlay.setWaypoints( path );      
      map.getOverlays().add(stopsOverlay);
      */
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    myLocation.disableMyLocation();
    SharedPreferences.Editor edit = getSharedPreferences(BusActivities.NAV_PREFERENCES, 0).edit();
    edit.putInt( KEY_ZOOM_LEVEL, map.getZoomLevel());
    edit.commit();
  }
  
    
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }
    
}
