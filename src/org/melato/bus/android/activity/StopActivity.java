/*-------------------------------------------------------------------------
 * Copyright (c) 2012, Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.android.activity;

import org.melato.android.ui.PropertiesDisplay;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.model.MarkerInfo;
import org.melato.bus.model.Route;
import org.melato.bus.model.Stop;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * Displays information about one stop.
 * @author Alex Athanasopoulos
 *
 */
public class StopActivity extends ListActivity {
  private StopContext stop;
  private PropertiesDisplay properties;
  private BusActivities activities;
  private RouteStop routeStop;
  
  public StopActivity() {
  }

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    stop = new StopContext(this);
    properties = stop.getProperties();
    activities = new BusActivities(this);
    IntentHelper intentHelper = new IntentHelper(this);
    routeStop = intentHelper.getRouteStop();
    if ( routeStop == null || routeStop.getStopSymbol() == null) {
      return;
    }
    Stop[] waypoints = Info.routeManager(this).getStops(routeStop.getRouteId());
    stop.setWaypoints(waypoints);
    
    MarkerInfo markerInfo = Info.routeManager(this).loadMarker(routeStop.getStopSymbol());
    int index = routeStop.getStopIndex();
    if ( index < 0 ) {
      index = findWaypointIndex(waypoints, markerInfo.getStop());
    }
    stop.setMarkerIndex(index);
    setTitle(stop.getMarker().getName());
   
    /*
    properties.add(getResources().getString(R.string.routes));
    for( Route r: markerInfo.getRoutes() ) {
      properties.add( r );
    }
    */
    setListAdapter(stop.createAdapter(R.layout.stop_item));
  }
  
  @Override
  protected void onDestroy() {
    stop.close();
    super.onDestroy();
  }  

  static int findWaypointIndex(Stop[] waypoints, Stop p) {
    int size = waypoints.length;
    for( int i = 0; i < size; i++ ) {
      if ( p.equals(waypoints[i])) {
        return i;
      }
    }
    return -1;
  }
  
  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    Object obj = properties.getItem(position);
    if ( obj instanceof Route ) {
      activities.showRoute((Route) obj);
    }
  }
 
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.stop_menu, menu);
    HelpActivity.addItem(menu,this, R.string.help_stop);
    return true;
  }
 
  private void showNearby() {
    Stop point = stop.getMarker();
    NearbyActivity.start(this, point);
  }
  /**
   * Start the Schedule activity for the given stop.
   */
  private void showStopSchedule() {
    Intent intent = new Intent(this, ScheduleActivity.class);
    new IntentHelper(intent).putRouteStop(routeStop);
    startActivity(intent);        
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch (item.getItemId()) {
      case R.id.nearby:
        showNearby();
        handled = true;
        break;
      case R.id.schedule:
        showStopSchedule();
        handled = true;
        break;
      default:
        break;
    }
    if ( handled )
      return true;
    return activities.onOptionsItemSelected(item);
  }  
  
}