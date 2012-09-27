package org.melato.bus.android.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.Info;
import org.melato.bus.android.activity.NearbyActivity;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Waypoint;

import android.content.Context;
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
  private List<RouteId> routes = new ArrayList<RouteId>();
  private RoutePointManager routePointManager;
  private RouteId selectedRoute;
  private GeoPoint center;
  private Set<RouteId> primaryRoutes = new HashSet<RouteId>();

	public RoutesOverlay(Context context) {
    super();
    routeManager = Info.routeManager(context);
    for( Route route: routeManager.getPrimaryRoutes() ) {
      addPrimaryRoute(route.getRouteId());
    }
  }

	private void addPrimaryRoute(RouteId routeId) {
	  addRoute(routeId);
	  primaryRoutes.add(routeId);
	}
	public void addRoute(RouteId routeId) {
	  routes.add(routeId);
	}
	
  public void setSelectedRoute(RouteId routeId) {
    selectedRoute = routeId;
    List<Waypoint> waypoints = routeManager.getWaypoints(routeId);
    RoutePoints route = RoutePoints.createFromPoints(Waypoint.asPoints(waypoints));
    center = route.getCenterGeoPoint();
  }
  
	public GeoPoint getCenter() {
	  return center;
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
	
	public void refresh() {
	  routes = null;
	  selectedRoute = null;
	}
	
	List<RouteId> getMapRoutes(MapView view) {
	  if ( routes == null ) {
      routes = new ArrayList<RouteId>();
      GeoPoint center = view.getMapCenter();
      routeManager.iterateNearbyRoutes(GMap.point(center), latDiff, lonDiff, routes);
	  }
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

  Map<RouteId,Integer> routeColors = new HashMap<RouteId,Integer>();
  int[] colors = new int[] { Color.BLUE, Color.RED, Color.GREEN, Color.CYAN, Color.MAGENTA};
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
    routePointManager = RoutePointManager.getInstance(view.getContext());
    for( RouteId routeId: getMapRoutes(view)) {
      int color = 0;
      if ( routeId.equals(selectedRoute)) {
        color = Color.BLUE;
      } else if ( primaryRoutes.contains(routeId)) {
        color = Color.BLACK;
      } else {
        color = getRouteColor(routeId);
      }
      paint.setColor(color);
      RoutePoints route = routePointManager.getRoutePoints(routeId);
      if ( route != null ) {
        drawPath(canvas, paint, projection, route);
        // if route is null, the routepoint manager is loading
        // The RouteMapActivity will be waiting for it load
        // and it will invalidate the map view, causing this to draw again.
      }
    }
	}

  @Override
  public boolean onTap(GeoPoint geoPoint, MapView mapView) {
    NearbyActivity.start(mapView.getContext(), GMap.point(geoPoint));
    return true;
  }
  
}
