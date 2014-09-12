package org.melato.bus.android.gpx;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.melato.bus.android.RoutesMap;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.LegGroup;
import org.melato.bus.plan.RouteLeg;
import org.melato.bus.plan.Sequence;
import org.melato.gpx.GPX;
import org.melato.gpx.GPXWriter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class GPXRoutesMap implements RoutesMap {
  private Context context;
  private RouteManager routeManager;


  public GPXRoutesMap(Context context, RouteManager routeManager) {
    super();
    this.context = context;
    this.routeManager = routeManager;
  }

  public static void exportGPX(Context context, GPX gpx) {
    File file = new File(Environment.getExternalStorageDirectory(), "routes.gpx");
    GPXWriter writer = new GPXWriter();
    try {
      writer.write(gpx, file);
    } catch (IOException e) {
      Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
    }
    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.setDataAndType(Uri.fromFile(file), "application/gpx");
    context.startActivity(intent);
  }
  
  @Override
  public void showRoute(RStop rstop) {
    Stop[] stops = routeManager.getStops(rstop.getRouteId());
    GPXMaker gpx = new GPXMaker();
    Route route = routeManager.getRoute(rstop.getRouteId());
    gpx.addRoute(route, stops);
    exportGPX(context, gpx.getGpx());
  }

  @Override
  public void showSequence(Sequence sequence) {
    GPXMaker gpx = new GPXMaker();
    for( LegGroup leg: sequence.getLegs()) {
      RouteLeg t = leg.leg;
      Stop[] stops = routeManager.getStops(t.getRouteId());
      Route route = routeManager.getRoute(t.getRouteId());
      Stop stop2 = t.getStop2();
      int end = stop2 != null ? stop2.getIndex() + 1 : stops.length;
      gpx.addRoute(route, Arrays.asList(stops).subList(t.getStop1().getIndex(), end));
    }
    exportGPX(context, gpx.getGpx());
  }
}
