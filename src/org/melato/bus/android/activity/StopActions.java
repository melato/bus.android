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
package org.melato.bus.android.activity;

import org.melato.android.bookmark.BookmarksActivity;
import org.melato.android.util.LocationField;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.bookmark.BookmarkTypes;
import org.melato.bus.android.map.RouteMapActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.NamedPoint;
import org.melato.bus.plan.Sequence;
import org.melato.client.Bookmark;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;


/**
 * @author Alex Athanasopoulos
 */
public class StopActions  {
  private Activity activity;
  
  public StopActions(Activity activity) {
    super();
    this.activity = activity;    
  }
  
  public void showSchedule(RStop rstop) {
    Intent intent = new Intent(activity, ScheduleActivity.class);
    new IntentHelper(intent).putRStop(rstop);
    activity.startActivity(intent);       
  }

  public void showMap(RStop rstop) {
    Intent intent = new Intent(activity, RouteMapActivity.class);
    new IntentHelper(intent).putRStop(rstop);
    activity.startActivity(intent);       
  }
  
  public void showSearch(RStop rstop) {
    PointSelectionActivity.selectPoint(activity,rstop);    
  }
  
  public void addToSequence(RStop rstop, boolean after) {
    Sequence sequence = Info.getSequence(activity);
    RouteId routeId = rstop.getRouteId();
    Stop stop = rstop.getStop();
    if ( after ) {
      sequence.addStopAfter(Info.routeManager(activity), new RStop(routeId, stop));
    } else {
      sequence.addStopBefore(Info.routeManager(activity), new RStop(routeId, stop));
    }
    activity.startActivity(new Intent(activity, SequenceActivity.class));    
  }
  
  public static void addBookmark(FragmentActivity activity, RStop rstop) {
    String label = Info.routeManager(activity).getRoute(rstop.getRouteId()).getLabel();
    String name = label + " " + rstop.getStop().getName(); 
    Bookmark bookmark = new Bookmark(BookmarkTypes.STOP, name, rstop);
    BookmarksActivity.addBookmarkDialog(activity, bookmark);
  }
  
  public void shareLocation(RStop rstop) {
    Stop stop = rstop.getStop();
    new LocationField(stop.getName(), stop).invoke(activity);
  }
    
  public void showStop(RStop rstop) {
    Intent intent = new Intent(activity, StopActivity.class);
    new IntentHelper(intent).putRStop(rstop);
    activity.startActivity(intent);       
  }
  
  public void showNearby(RStop rstop) {
    NearbyActivity.start(activity, rstop.getStop());
  }
  
  public void setOrigin(RStop rstop) {
    NamedPoint p = Info.namedPoint(activity, rstop);
    PlanFragment.origin = p;
    PlanTabsActivity.showSearch(activity);
  }
  
  public void setDestination(RStop rstop) {
    NamedPoint p = Info.namedPoint(activity, rstop);
    PlanFragment.destination = p;
    PlanTabsActivity.showSearch(activity);
  }
  
  public boolean showRStop(RStop rstop, int itemId) {
    switch( itemId ) {
    case R.id.map:
      showMap(rstop);
      break;
    case R.id.schedule:
      showSchedule(rstop);
      break;
    case R.id.search:
      showSearch(rstop);
      break;
    case R.id.stop:
      showStop(rstop);
      break;
    case R.id.origin:
      setOrigin(rstop);
      break;
    case R.id.destination:
      setDestination(rstop);
      break;
    case R.id.add:
      addToSequence(rstop, true);
      break;
    case R.id.nearby:
      showNearby(rstop);
      break;
    case R.id.bookmark:
      if ( activity instanceof FragmentActivity) {
        addBookmark((FragmentActivity) activity, rstop);
        break;
      } else {
        return false;
      }
    case R.id.share:
      shareLocation(rstop);
      break;
    default:
      return new BusActivities(activity).onItemSelected(itemId);
    }
    return true;
  }
}