package org.melato.bus.android.track;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.melato.android.AndroidLogger;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.LocationContext;
import org.melato.bus.android.activity.ScheduleActivity;
import org.melato.bus.model.Route;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.gpx.util.Path;
import org.melato.log.Log;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class TrackActivity extends ScheduleActivity {
  private LocationContext location;
  public TrackActivity() {    
  }
    
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    location = new LocationContext(this);
  }


  @Override
  protected void onDestroy() {
    location.close();
    super.onDestroy();
  }


  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.track_menu, menu);
     return true;
  }
  
  public void mark() {
    Log.setLogger(new AndroidLogger(this));
    Route route = activities.getRoute();
    Point point = location.getLocation();
    if ( route != null && point != null ) {
      Date date = point.getTime();
      if ( System.currentTimeMillis() - date.getTime() > 10000L) {
        return;
      }
      GPX gpx = activities.getRouteManager().loadGPX(route);
      List<Waypoint> waypoints = null;
      if ( gpx.getRoutes().isEmpty() ) {
        waypoints = Collections.emptyList();
      } else {
        waypoints = gpx.getRoutes().get(0).getWaypoints();
      }
      Path path = new Path(waypoints);
      int closestIndex = path.findNearestIndex(point);
      Waypoint p = path.getWaypoint(closestIndex);
      if ( p != null && Earth.distance(p,  point) < Info.MARK_PROXIMITY ) {
        Log.info("marker: " + p.getSym() );
        Pass pass = new Pass(route, p.getSym() );
        pass.setDate(point.getTime());
        BusTrackHelper trackDb = new BusTrackHelper(this);
        trackDb.insertPass(pass);
      }
      
    }
  }
  
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.mark:
        mark();
        return true;
      default:
        break;
    }
    return super.onOptionsItemSelected(item);    
  }
  
 }