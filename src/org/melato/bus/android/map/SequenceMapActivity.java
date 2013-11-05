/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.android.map;

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.bus.model.cache.RoutePoints;
import org.melato.bus.plan.LegGroup;
import org.melato.bus.plan.RouteLeg;
import org.melato.bus.plan.Sequence;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

/** An activity that displays a map of a sequence. */
public class SequenceMapActivity extends MapActivity {
  private Sequence sequence;
  private MapView map;
  private Rect boundary;
  private GeoPoint center;
  @Override
  protected boolean isRouteDisplayed() {
    return true;
  }

  Rect getBoundary(Rect boundary, List<GeoPoint> path) {
    if ( path.isEmpty()) {
      return boundary;
    }
    Rect r = null;
    if ( boundary == null ) {
      r = new Rect();
      GeoPoint p = path.get(0);
      r.top = r.bottom = p.getLatitudeE6();
      r.left = r.right = p.getLongitudeE6();      
    } else {
      r = new Rect(boundary);
    }
    for(GeoPoint p: path) {
      int lat = p.getLatitudeE6();
      int lon = p.getLongitudeE6();
      r.bottom = Math.min(r.bottom,  lat);
      r.top = Math.max(r.top,  lat);
      r.left = Math.min(r.left,  lon);
      r.right = Math.max(r.right,  lon);
    }
    return r;
  }
  Rect getBoundary(RoutePath[] paths) {
    Rect boundary = null;
    for(RoutePath path: paths) {
      boundary = getBoundary(boundary, path.points);
    }
    return boundary;
  }
  
  RoutePath[] loadPaths(Sequence sequence) {
    RouteManager routeManager = Info.routeManager(SequenceMapActivity.this);
    RoutePointManager routePointManager = RoutePointManager.getInstance(this);
    List<RoutePath> paths = new ArrayList<RoutePath>();
    for( LegGroup leg: sequence.getLegs()) {
      RouteLeg t = leg.leg;
      RoutePoints routePoints = routePointManager.getRoutePoints(t.getRouteId());
      RoutePath path = new RoutePath();
      path.route = routeManager.getRoute(t.getRouteId());
      path.points = new RoutePointsGeoPointList(routePoints,
          t.getStop1().getIndex(), t.getStop2().getIndex()); 
      paths.add(path);
    }
    return paths.toArray(new RoutePath[0]);
  }
  
  class LoadTask extends AsyncTask<Sequence,Void,RoutePath[]> {
    @Override
    protected RoutePath[] doInBackground(Sequence... params) {
      return loadPaths(params[0]);
    }

    @Override
    protected void onPostExecute(RoutePath[] paths) {
      setPaths(paths);
    }
  }
  
  void setPaths(RoutePath[] paths) {
    boundary = getBoundary(paths);
    center = new GeoPoint((boundary.bottom+boundary.top)/2, (boundary.left+boundary.right)/2);
    Log.i("aa", "boundary: " + boundary);
    Log.i("aa", "center: " + center);
    map.getOverlays().add(new RoutePathsOverlay(paths)); 
    map.getController().setCenter(center);
  }
  
  
  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    Intent intent = getIntent();
    sequence = (Sequence) intent.getSerializableExtra(Keys.SEQUENCE);
    setTitle( sequence.getLabel(Info.routeManager(this)));
    setContentView(R.layout.map);
    map = (MapView) findViewById(R.id.mapview);
    map.setBuiltInZoomControls(true);
    
    MapController mapController = map.getController();
    mapController.setZoom(RouteMapActivity.defaultZoom);
    //setPaths(loadPaths(sequence));
    new LoadTask().execute(sequence);
  }

  public static void showMap(Context context, Sequence sequence) {
    Intent intent = new Intent(context, SequenceMapActivity.class);
    intent.putExtra(Keys.SEQUENCE, sequence);
    context.startActivity(intent);    
  }
  
  
}
