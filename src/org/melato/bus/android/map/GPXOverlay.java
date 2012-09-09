package org.melato.bus.android.map;

import java.util.List;

import org.melato.gpx.GPX;
import org.melato.gpx.Route;
import org.melato.gpx.Waypoint;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

/**
 * A map overlay that contains waypoints.
 * @author Alex Athanasopoulos
 */
public class GPXOverlay extends Overlay {
	private GPX gpx;
	
	public GPXOverlay(GPX gpx) {
	  super();
		this.gpx = gpx;
	}
	
	void addToPath(Path path, Projection projection, List<Waypoint> waypoints) {
    int size = waypoints.size();
    if ( size == 0 )
      return;
    Point p = new Point();
    projection.toPixels(Maps.geoPoint(waypoints.get(0)), p);
    path.moveTo(p.x, p.y);
    for( int i = 1; i < size; i++ ) {
      projection.toPixels(Maps.geoPoint(waypoints.get(i)), p);
      path.lineTo(p.x, p.y);
    }
	}
	
	public void draw(Canvas canvas, MapView view, boolean shadow){
    super.draw(canvas, view, shadow);

    Path path = new Path();
    Projection projection = view.getProjection();
    for( Route route: gpx.getRoutes() ) {
      addToPath(path, projection, route.getWaypoints());
    }
    
    Paint   paint = new Paint();
    paint.setDither(true);
    paint.setColor(Color.RED);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
    paint.setStrokeCap(Paint.Cap.ROUND);
    paint.setStrokeWidth(2);

    canvas.drawPath(path, paint);
}
	

}
