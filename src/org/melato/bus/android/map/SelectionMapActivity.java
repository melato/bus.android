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

import org.melato.android.gpx.map.GMap;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.android.activity.LocationEndpoints;
import org.melato.gps.Point2D;

import android.os.Bundle;
import android.util.Log;
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
  private PointOverlay originOverlay;
  private PointOverlay destinationOverlay;
  
  @Override
  protected boolean isRouteDisplayed() {
    return false;
  }
  
  @Override
  protected void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    Point2D center = Info.routeManager(this).getCenter();
    setContentView(R.layout.map);
    map = (MapView) findViewById(R.id.mapview);
    registerForContextMenu(map);
    map.setBuiltInZoomControls(true);
    selectionOverlay = new SelectionOverlay(this);
    map.getOverlays().add(selectionOverlay); 
    map.getController().setCenter(GMap.geoPoint(center));
    map.getController().setZoom(14);
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.map_selection_menu, menu);
  }  

  private void setOrigin(GeoPoint point) {
    if ( originOverlay != null) {
      map.getOverlays().remove(originOverlay); 
    }
    originOverlay = new PointOverlay(this, point, R.drawable.start);    
    map.getOverlays().add(originOverlay); 
    map.invalidate();
  }
  private void setDestination(GeoPoint point) {
    if ( destinationOverlay != null) {
      map.getOverlays().remove(destinationOverlay); 
    }
    destinationOverlay = new PointOverlay(this, point, R.drawable.finish);    
    map.getOverlays().add(destinationOverlay); 
    map.invalidate();
  }
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch(item.getItemId()) {
    case R.id.origin:
      setOrigin(selectionOverlay.getSelectedPoint());
      break;
    case R.id.destination:
      setDestination(selectionOverlay.getSelectedPoint());
      break;
    default:
      return false;
    }
    return true;
  }
   
}
