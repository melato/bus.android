package org.melato.bus.android.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.android.gpx.map.Maps;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Waypoint;
import org.melato.log.Log;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * A map overlay that displays routes.
 * @author Alex Athanasopoulos
 */
public class RoutesOverlay extends Overlay {
  private RouteManager routeManager;
  private float latDiff; 
  private float lonDiff;
  private List<RouteId> routes;
  private Map<RouteId,List<Waypoint>> routeCache = new HashMap<RouteId,List<Waypoint>>();
  	
	public RoutesOverlay(RouteManager routeManager) {
    super();
    this.routeManager = routeManager;
  }

	private void findBoundaries(MapView view) {
    int latSpan = view.getLatitudeSpan();
    int lonSpan = view.getLongitudeSpan();
    latDiff = ((float) latSpan) / 1E6f / 2; 
    lonDiff = ((float) lonSpan) / 1E6f / 2;
	}
	
	public void refresh() {
	  routes = null;
	}
	List<RouteId> getMapRoutes(MapView view) {
    Log.info("getMapRoutes");
	  if ( routes == null ) {
      routes = new ArrayList<RouteId>();
      GeoPoint center = view.getMapCenter();
      routeManager.iterateNearbyRoutes(Maps.point(center), latDiff, lonDiff, routes);
	  }
    Log.info("routes: " + routes.size());
    return routes;
	}
	
  Path getPath(Projection projection, List<Waypoint> waypoints) {
    Path path = new Path();
    int size = waypoints.size();
    if ( size == 0 )
      return path;
    Point p = new Point();
    projection.toPixels(Maps.geoPoint(waypoints.get(0)), p);
    path.moveTo(p.x, p.y);
    for( int i = 1; i < size; i++ ) {
      projection.toPixels(Maps.geoPoint(waypoints.get(i)), p);
      path.lineTo(p.x, p.y);
    }
    return path;
  }
	
  void drawPath(Canvas canvas, Paint paint, Projection projection, List<Waypoint> waypoints ) {
    Path path = getPath(projection, waypoints);
    canvas.drawPath(path, paint);    
  }

  List<Waypoint> getWaypoints(RouteId routeId) {
    List<Waypoint> waypoints = routeCache.get(routeId);
    if ( waypoints == null ) {
      waypoints = routeManager.loadWaypoints(routeId);
      routeCache.put(routeId,  waypoints);
    }
    return waypoints;
  }
  Map<RouteId,Integer> routeColors = new HashMap<RouteId,Integer>();
  int[] colors = new int[] { Color.RED, Color.BLUE, Color.GREEN, Color.CYAN, Color.YELLOW };
  int colorIndex = 0;
  
  int nextColor() {
    int color = colors[colorIndex];
    colorIndex = (colorIndex+1)%colors.length;
    return color;
  }

  int getRouteColor(RouteId routeId) {
    Integer color = routeColors.get(routeId);
    if ( color == null ) {
      color = nextColor();
      routeColors.put(routeId,  color);
    }
    return color;
  }
  
  public void draw(Canvas canvas, MapView view, boolean shadow){
    super.draw(canvas, view, shadow);
    findBoundaries(view);
    Paint   paint = new Paint();
    paint.setDither(true);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStrokeWidth(2);
    
    Projection projection = view.getProjection();    
    for( RouteId routeId: getMapRoutes(view)) {
      paint.setColor(getRouteColor(routeId));
      Log.info( "draw " + routeId );
      List<Waypoint> waypoints = getWaypoints(routeId);
      drawPath(canvas, paint, projection, waypoints);
    }
	}
}
