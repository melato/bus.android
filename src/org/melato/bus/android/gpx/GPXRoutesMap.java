package org.melato.bus.android.gpx;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.melato.bus.android.RoutesMap;
import org.melato.bus.android.activity.LocationEndpoints;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.LegGroup;
import org.melato.bus.plan.NamedPoint;
import org.melato.bus.plan.RouteLeg;
import org.melato.bus.plan.Sequence;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;
import org.melato.gpx.GPXWriter;
import org.melato.gpx.Waypoint;
import org.melato.log.Log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class GPXRoutesMap implements RoutesMap {
  private Context context;
  private RouteManager routeManager;
  private boolean useFile = false;


  public GPXRoutesMap(Context context, RouteManager routeManager) {
    super();
    this.context = context;
    this.routeManager = routeManager;
  }

  void putGPX(Intent intent, GPX gpx) {
    GPXWriter writer = new GPXWriter();
    try {
      if ( useFile ) {
        File file = new File(Environment.getExternalStorageDirectory(), "routes.gpx");
        writer.write(gpx, file);      
        intent.setDataAndType(Uri.fromFile(file), "application/gpx");
      } else {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        writer.write(gpx, buf);
        intent.putExtra("gpx", buf.toByteArray());
        intent.setType("application/gpx");
      }
    } catch (IOException e) {
      Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
    }
  }
  
  public void viewGPX(Context context, GPX gpx) {
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    putGPX(intent, gpx);
    context.startActivity(intent);
  }
  
  public static void editGPX(Activity activity, int requestCode, GPX gpx) {
    File file = new File(Environment.getExternalStorageDirectory(), "routes.gpx");
    GPXWriter writer = new GPXWriter();
    try {
      writer.write(gpx, file);
    } catch (IOException e) {
      Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
    }
    Intent intent = new Intent(Intent.ACTION_EDIT);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setDataAndType(Uri.fromFile(file), "application/gpx");
    activity.startActivityForResult(intent, requestCode);
  }
  
  @Override
  public void showRoute(RStop rstop) {
    Stop[] stops = routeManager.getStops(rstop.getRouteId());
    GPXMaker gpx = new GPXMaker();
    Route route = routeManager.getRoute(rstop.getRouteId());
    gpx.addRoute(route, stops);
    Stop stop = rstop.getStop();
    if ( stop != null ) {
      gpx.addPoint(stop);
    }
    viewGPX(context, gpx.getGpx());
  }

  @Override
  public void showSequence(Sequence sequence) {
    GPXMaker gpxMaker = new GPXMaker();
    for( LegGroup leg: sequence.getLegs()) {
      RouteLeg t = leg.leg;
      Stop[] stops = routeManager.getStops(t.getRouteId());
      Route route = routeManager.getRoute(t.getRouteId());
      Stop stop2 = t.getStop2();
      int end = stop2 != null ? stop2.getIndex() + 1 : stops.length;
      gpxMaker.addRoute(route, Arrays.asList(stops).subList(t.getStop1().getIndex(), end));
    }
    GPX gpx = gpxMaker.getGpx();
    gpx.setName(sequence.getLabel(routeManager));
    viewGPX(context, gpx);
  }

  @Override
  public void showMap() {
    GPX gpx = new GPX();
    viewGPX(context, gpx);
  }
  
  public void startActivityForEndpoints(LocationEndpoints endpoints, Activity activity, int requestCode) {    
    GPXMaker gpx = new GPXMaker();
    if ( endpoints != null ) {
      gpx.addPoint(endpoints.origin);      
      gpx.addPoint(endpoints.destination);      
    }
    editGPX(activity, requestCode, gpx.getGpx());
  }
  
  NamedPoint toNamedPoint(Waypoint w) {
    NamedPoint p = new NamedPoint(w);
    p.setName(w.getName());
    return p;
  }
  public LocationEndpoints getEndpoints(Intent intent) {
    String data = intent.getStringExtra("gpx");
    if ( data != null ) {
      GPXParser parser = new GPXParser();
      byte[] buf = data.getBytes();
      try {
        GPX gpx = parser.parse(new ByteArrayInputStream(buf));
        List<Waypoint> waypoints = gpx.getWaypoints();
        switch(waypoints.size()) {
        case 2:
          return new LocationEndpoints(toNamedPoint(waypoints.get(0)), toNamedPoint(waypoints.get(1)));
        case 1:
          return new LocationEndpoints(null, toNamedPoint(waypoints.get(0)));
        default:
          return null;
        }
      } catch (IOException e) {
        Log.info(e.toString());
      }
    }
    return null;    
  }
  
}
