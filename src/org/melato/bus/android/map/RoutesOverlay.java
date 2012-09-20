package org.melato.bus.android.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.activity.NearbyActivity;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.AveragePoint;
import org.melato.log.Clock;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
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
  private int latMin6E;
  private int latMax6E;
  private int lonMin6E;
  private int lonMax6E;
  private List<RouteId> routes;
  private Map<RouteId,RoutePoints> routeCache;
  private int pointCount;
  private org.melato.gpx.Point averagePoint;
  private static Map<RouteId,RoutePoints> allRoutes;
  	
	public RoutesOverlay(RouteManager routeManager) {
    super();
    this.routeManager = routeManager;
    loadAllRoutes();
  }

  public RoutesOverlay(RouteManager routeManager, RouteId routeId) {
    super();
    this.routeManager = routeManager;
    List<Waypoint> waypoints = routeManager.loadWaypoints(routeId);
    AveragePoint average = new AveragePoint();
    average.add(waypoints);
    averagePoint = average.getCenter();
    routes = new ArrayList<RouteId>();
    routes.add(routeId);
    routeCache = new HashMap<RouteId,RoutePoints>();
    routeCache.put(routeId, RoutePoints.createFromPoints(Waypoint.asPoints(waypoints)));
  }

	public org.melato.gpx.Point getAveragePoint() {
    return averagePoint;
  }

  private void findBoundaries(MapView view) {
    int latSpan = view.getLatitudeSpan();
    int lonSpan = view.getLongitudeSpan();
    latDiff = ((float) latSpan) / 1E6f / 2; 
    lonDiff = ((float) lonSpan) / 1E6f / 2;
    GeoPoint center = view.getMapCenter();
    latMin6E = center.getLatitudeE6() - latSpan / 2;
    latMax6E = center.getLatitudeE6() + latSpan / 2;
    lonMin6E = center.getLongitudeE6() - lonSpan / 2;
    lonMax6E = center.getLongitudeE6() + lonSpan / 2;
	}
	
	private void loadAllRoutes() {
	  if ( allRoutes == null ) {
	    RoutePointsCollector collector = new RoutePointsCollector();
	    routeManager.iterateAllRouteStops(collector);
	    allRoutes = collector.getMap();
	  }
	  routeCache = allRoutes;
	}
	
	public void refresh() {
	  routes = null;
	}
	List<RouteId> getMapRoutes(MapView view) {
	  if ( routes == null ) {
      routes = new ArrayList<RouteId>();
      GeoPoint center = view.getMapCenter();
      routeManager.iterateNearbyRoutes(GMap.point(center), latDiff, lonDiff, routes);
	  }
    //Log.info("routes: " + routes.size());
    return routes;
	}
	
  Path getPath(Projection projection, RoutePoints points) {
    Path path = new Path();
    int size = points.size();
    if ( size == 0 )
      return path;
    Point p = new Point();
    boolean previousInside = false;
    projection.toPixels(points.getGeoPoint(0), p);
    path.moveTo(p.x, p.y);
    for( int i = 0; i < size; i++ ) {
      boolean inside = points.isInside(i, latMin6E, latMax6E, lonMin6E, lonMax6E);
      if ( inside ) {
        pointCount++;
      }
      if ( previousInside ) {
        // draw from previous point
        projection.toPixels(points.getGeoPoint(i), p);
        path.lineTo(p.x, p.y );
      } else if ( inside && i > 0 ) {
        projection.toPixels(points.getGeoPoint(i-1), p);
        path.moveTo(p.x, p.y );          
        projection.toPixels(points.getGeoPoint(i), p);
        path.lineTo(p.x, p.y );
      } else {
        // do nothing
        // segment is outside the view area or we are at the beginning            
      }
      previousInside = inside;
    }
    return path;
  }
	
  void drawPath(Canvas canvas, Paint paint, Projection projection, RoutePoints route ) {
    Path path = getPath(projection, route);
    canvas.drawPath(path, paint);    
  }

  RoutePoints getPoints(RouteId routeId) {
    RoutePoints points = routeCache.get(routeId);    
    /*
    if ( points == null ) {
      List<Waypoint> waypoints = routeManager.loadWaypoints(routeId);
      //List<Waypoint> waypoints = Collections.emptyList();
      //routeManager.getStorage().iterateWaypoints(routeId);
      points = new RoutePoints(waypoints);
      routeCache.put(routeId,  points);
    }
    */
    return points;
  }
  Map<RouteId,Integer> routeColors = new HashMap<RouteId,Integer>();
  int[] colors = new int[] { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.YELLOW };
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
    pointCount = 0;
    Clock clock = new Clock("RoutesOverlay.draw");
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
      RoutePoints route = getPoints(routeId);
      drawPath(canvas, paint, projection, route);
    }
    //Log.info(clock + " points=" + pointCount);
	}

  @Override
  public boolean onTap(GeoPoint geoPoint, MapView mapView) {
    NearbyActivity.start(mapView.getContext(), GMap.point(geoPoint));
    return true;
  }
  
}
