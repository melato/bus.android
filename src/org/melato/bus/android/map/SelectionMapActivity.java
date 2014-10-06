/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013,2014 Alex Athanasopoulos.  All Rights Reserved.
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

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.android.activity.LocationEndpoints;
import org.melato.bus.plan.NamedPoint;
import org.melato.gps.Point2D;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/** Map activity for selecting locations for search. */
public class SelectionMapActivity extends MapActivity {
  private MapView map;
  private SelectionOverlay selectionOverlay;
  private LocationEndpoints endpoints;
  private PointOverlay[] pointOverlays = new PointOverlay[2];
  private int[] pointIcons = new int[] {R.drawable.start, R.drawable.finish};
  
  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    Point2D center = Info.routeManager(this).getCenter();
    int zoom = Info.routeManager(this).getZoomLevel();
    setContentView(R.layout.map);
    map = (MapView) findViewById(R.id.mapview);
    registerForContextMenu(map);
    map.setBuiltInZoomControls(true);
    selectionOverlay = new SelectionOverlay(this);
    map.getOverlays().add(selectionOverlay); 
    map.getController().setCenter(GMap.geoPoint(center));
    map.getController().setZoom(zoom);
    endpoints = (LocationEndpoints) getIntent().getSerializableExtra(Keys.LOCATION_ENDPOINTS);
    if ( endpoints != null ) {
      setPoint(GMap.geoPoint(endpoints.origin), 0);
      setPoint(GMap.geoPoint(endpoints.destination), 1);
    } else {
      endpoints = new LocationEndpoints();
    }
  }
  
  void setResult() {
    if ( endpoints.origin == null) {
      endpoints.origin = getNamedPoint(0);
    }
    if ( endpoints.destination == null) {
      endpoints.destination = getNamedPoint(1);
    }
    Intent intent = new Intent();
    intent.putExtra(Keys.LOCATION_ENDPOINTS, endpoints);
    setResult(RESULT_OK, intent);
  }
    
  private NamedPoint getNamedPoint(int i) {
    PointOverlay overlay = pointOverlays[i];
    if ( overlay != null) {
      return new NamedPoint(GMap.point(overlay.getPoint()));
    }
    return null;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_selection_menu, menu);
  }  

  private void setPoint(GeoPoint point, int i) {
    if ( pointOverlays[i] != null) {
      map.getOverlays().remove(pointOverlays[i]);
      pointOverlays[i] = null;
    }
    if ( point != null) {
      pointOverlays[i] = new PointOverlay(this, point, pointIcons[i]);    
      map.getOverlays().add(pointOverlays[i]);
    }
    map.invalidate();
  }
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch(item.getItemId()) {
    case R.id.origin:
      setPoint(selectionOverlay.getSelectedPoint(), 0);
      endpoints.origin = null;
      setResult();
      break;
    case R.id.destination:
      setPoint(selectionOverlay.getSelectedPoint(), 1);
      endpoints.destination = null;
      setResult();
      break;
    default:
      return false;
    }
    return true;
  }
   
}
