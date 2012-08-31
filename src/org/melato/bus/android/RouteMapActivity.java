package org.melato.bus.android;

import java.util.List;

import org.melato.bus.android.map.PathOverlay;
import org.melato.bus.android.map.WaypointsOverlay;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class RouteMapActivity extends MapActivity {
  private MapView map;
  private MyLocationOverlay myLocation; 
  private static final String KEY_ZOOM_LEVEL = "zoomLevel";

  @Override
  protected boolean isRouteDisplayed() {
    return true;
  }
  
  public static GeoPoint createGeoPoint(Point p) {
    if ( p == null )
      return null;
    return new GeoPoint( (int) (p.lat * 1E6), (int)(p.lon*1E6));    
  }
    
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.map);
      map = (MapView) findViewById(R.id.mapview);
      map.setBuiltInZoomControls(true);
      Point center = new Point(38,23.8f);
      String name = (String) getIntent().getSerializableExtra(BusActivity.KEY_ROUTE);
      GPX gpx = Info.routeManager().loadGPX(name);
      List<Waypoint> path = gpx.getRoutes().get(0).path.getWaypoints();
      
      MapController mapController = map.getController();
      int zoom = getSharedPreferences(BusActivity.NAV_PREFERENCES, 0).getInt(KEY_ZOOM_LEVEL, 15 );
      mapController.setZoom(zoom);
      mapController.setCenter(createGeoPoint(center));

      Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
      WaypointsOverlay stopsOverlay = new WaypointsOverlay(drawable, this);
      stopsOverlay.setWaypoints( path );
      
      PathOverlay pathOverlay = new PathOverlay(path);
      
      myLocation = new MyLocationOverlay(this, map);
      map.getOverlays().add(myLocation);
      //map.getOverlays().add(stopsOverlay);
      map.getOverlays().add(pathOverlay);
  }

  
}
