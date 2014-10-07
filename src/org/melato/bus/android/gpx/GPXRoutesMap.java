package org.melato.bus.android.gpx;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.melato.bus.android.R;
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
import org.melato.gpx.Waypoint;
import org.melato.log.Log;
import org.melato.map.api.GPXIntentHelper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

/** Implements the RoutesMap interface by creating a GPX file and calling an activity to handle it. */
public class GPXRoutesMap implements RoutesMap {
  private Context context;
  private RouteManager routeManager;


  public GPXRoutesMap(Context context, RouteManager routeManager) {
    super();
    this.context = context;
    this.routeManager = routeManager;
  }

  void putGPX(Intent intent, GPX gpx) {
    try {
      new GPXIntentHelper(context).putGPX(gpx, intent);
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
  
  public void editGPX(Fragment fragment, int requestCode, GPX gpx) {
    Intent intent = new Intent(Intent.ACTION_EDIT);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    Log.info("editGPX");
    putGPX(intent, gpx);
    fragment.startActivityForResult(intent, requestCode);
  }
  
  @Override
  public void showRoute(RStop rstop) {
    Stop[] stops = routeManager.getStops(rstop.getRouteId());
    GPXMaker gpxMaker = new GPXMaker();
    Route route = routeManager.getRoute(rstop.getRouteId());
    gpxMaker.addRoute(route, stops);
    Stop stop = rstop.getStop();
    if ( stop != null ) {
      gpxMaker.addPoint(stop);
    }
    GPX gpx = gpxMaker.getGpx();
    gpx.setName(route.getFullTitle());
    viewGPX(context, gpx);
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
  
  @Override
  public void startActivityForEndpoints(LocationEndpoints endpoints, Fragment fragment, int requestCode) {    
    GPXMaker gpx = new GPXMaker();
    if ( endpoints != null ) {
      gpx.addPoint(endpoints.origin, GPXIntentHelper.TYPE_START, context.getString(R.string.set_origin));      
      gpx.addPoint(endpoints.destination, GPXIntentHelper.TYPE_END, context.getString(R.string.set_destination));      
    }
    editGPX(fragment, requestCode, gpx.getGpx());
  }
  
  NamedPoint toNamedPoint(Waypoint w) {
    NamedPoint p = new NamedPoint(w);
    p.setName(w.getName());
    return p;
  }
  
  @Override
  public LocationEndpoints getEndpoints(Intent intent) {
    try {
      GPX gpx = new GPXIntentHelper(context).getGPX(intent);
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
    return null;    
  }
  
}
