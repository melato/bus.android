package org.melato.bus.android.activity;

import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.TimeOfDay;
import org.melato.bus.client.TimeOfDayList;
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Schedule;
import org.melato.gpx.Waypoint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class ScheduleActivity extends Activity {
  public static final String KEY_DAYS = "days";
  protected BusActivities activities;
  private Schedule schedule;
  private Date  currentTime = new Date();
  private DaySchedule daySchedule;
  private RouteStop routeStop;
  private String  stopName;
  private int     timeOffset;
  
  public static String getScheduleName(Context context, int days) {
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
      case DaySchedule.EVERYDAY:
        resourceId = R.string.days_all;
        break;
      default:
        return "";
    }
    return context.getResources().getString(resourceId);

    
  }
  protected String getScheduleName() {
    if ( daySchedule == null )
      return "";
    return getScheduleName(this, daySchedule.getDays());
  }
  
  private void setStopInfo(RouteStop stop) {
    this.routeStop = stop;
    List<Waypoint> waypoints = Info.routeManager(this).getWaypoints(stop.getRouteId());
    stopName = stop.getStopName(waypoints);
    timeOffset = stop.getTimeFromStart(waypoints);
    if ( timeOffset == 0 && waypoints.size() > 0 ) {
      stopName = waypoints.get(0).getName();
    }
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      IntentHelper helper = new IntentHelper(this);
      RouteStop routeStop = helper.getRouteStop();
      if ( routeStop == null )
        return;
      setStopInfo(routeStop);
      schedule = activities.getRouteManager().getSchedule(routeStop.getRouteId());
      Integer days = (Integer) getIntent().getSerializableExtra(KEY_DAYS);
      if ( days != null ) {
        for( DaySchedule d: schedule.getSchedules() ) {
          if ( days == d.getDays() ) {
            this.daySchedule = d;
            break;
          }
        }
      }
      if ( daySchedule == null ) {
        daySchedule = schedule.getSchedule(currentTime); 
      }
      setContentView(R.layout.schedule);
      ListView listView = (ListView) findViewById(R.id.listView);
      TextView textView = (TextView) findViewById(R.id.textView);
      String scheduleText = getScheduleName();
      String title = helper.getRoute().getFullTitle();
      if ( stopName != null ) {
        scheduleText += " - " + stopName;
      }
      textView.setText(scheduleText);
      setTitle(title);
      if ( daySchedule != null ) {
        TimeOfDayList times = new TimeOfDayList(daySchedule,currentTime);
        times.setTimeOffset(timeOffset);
        ScheduleAdapter scheduleAdapter = new ScheduleAdapter(times);
        listView.setAdapter(scheduleAdapter);
        int pos = times.getDefaultPosition();
        if ( pos >= 0 )
          listView.setSelection(pos);
      }
  }

  class ScheduleAdapter extends ArrayAdapter<TimeOfDay> {
    TimeOfDayList times;
    int currentPosition;
    public ScheduleAdapter(TimeOfDayList times) {
      super(ScheduleActivity.this, R.layout.list_item, times);
      currentPosition = times.getDefaultPosition();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      UI.highlight(view, position == currentPosition );
      return view;
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.schedule_menu, menu);
     return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }
  
 }