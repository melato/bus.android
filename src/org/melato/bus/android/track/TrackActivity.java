package org.melato.bus.android.track;

import java.util.Date;

import org.melato.bus.android.BusLogger;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.BusActivity;
import org.melato.bus.android.activity.RoutePath;
import org.melato.bus.android.model.TimeOfDayList;
import org.melato.bus.android.model.TimeOfDay;
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.log.Log;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class TrackActivity extends BusActivity {
  Schedule schedule;
  Date  currentTime = new Date();

  public TrackActivity() {    
  }
    
  private String getScheduleName() {
    DaySchedule schedule = this.schedule.getSchedule(currentTime);
    if ( schedule == null )
      return "";
    int days = schedule.getDays();
    int resourceId = 0;
    switch( days ) {
      case DaySchedule.SUNDAY:
        resourceId = R.string.days_Su;
        break;
      case DaySchedule.MONDAY_FRIDAY:
        resourceId = R.string.days_M_F;
        break;
      case DaySchedule.SATURDAY:
        resourceId = R.string.days_Sa;
        break;
      case DaySchedule.SATURDAY_SUNDAY:
        resourceId = R.string.days_SaSu;
        break;
      default:
        return "";
    }
    return getResources().getString(resourceId);

    
  }
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Route route = getRoute();
      schedule = route.getSchedule();
      String title = route.getLabel() + "-" + route.getDirection() + " " + getScheduleName();
      setTitle(title);
      TimeOfDayList times = new TimeOfDayList(schedule,currentTime);
      ScheduleAdapter scheduleAdapter = new ScheduleAdapter(times);
      setListAdapter(scheduleAdapter);
      int pos = times.getDefaultPosition();
      if ( pos >= 0 )
        this.setSelection(pos);
      super.setEnabledLocations(true);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  class ScheduleAdapter extends ArrayAdapter<TimeOfDay> {
    TimeOfDayList times;
    public ScheduleAdapter(TimeOfDayList times) {
      super(TrackActivity.this, R.layout.list_item, times);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.schedule_menu, menu);
     return true;
  }
  
  public void mark() {
    Log.setLogger(new BusLogger(this));
    Route route = getRoute();
    Point point = getLocation();
    Date date = getLocationDate();
    if ( route != null && point != null ) {
      if ( System.currentTimeMillis() - date.getTime() > 10000L) {
        return;
      }
      GPX gpx = getRouteManager().loadGPX(route);
      RoutePath path = new RoutePath(gpx);
      path.setLocation(point);
      Waypoint p = path.getClosestWaypoint();
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