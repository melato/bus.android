package org.melato.bus.android.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.Info;
import org.melato.bus.android.activity.NearbyActivity;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.gpx.Waypoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;

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
  private RoutePointManager routePointManager;
  private RoutePoints route;

	public RoutesOverlay(Context context) {
    super();
    routeManager = Info.routeManager(context);
  }

	public void setRoute(RouteId routeId) {
	  routes = new ArrayList<RouteId>();
	  routes.add(routeId);
    List<Waypoint> waypoints = routeManager.loadWaypoints(routeId);
    route = RoutePoints.createFromPoints(Waypoint.asPoints(waypoints));
	}
	
	public GeoPoint getCenter() {
    if ( route != null ) {
      return route.getCenterGeoPoint();
    }
    return null;
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
	  route = null;
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
    findBoundaries(view);
    Paint   paint = new Paint();
    paint.setDither(true);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStrokeWidth(2);
    
    Projection projection = view.getProjection();
    if ( route != null ) {
      paint.setColor(Color.BLUE);
      drawPath(canvas, paint, projection, route);      
    }
    else {
      routePointManager = RoutePointManager.getInstance(view.getContext());
      for( RouteId routeId: getMapRoutes(view)) {
        paint.setColor(getRouteColor(routeId));
        RoutePoints route = routePointManager.getRoutePoints(routeId);
        if ( route != null ) {
          drawPath(canvas, paint, projection, route);
        }
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
