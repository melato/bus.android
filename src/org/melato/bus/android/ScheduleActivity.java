package org.melato.bus.android;

import java.util.AbstractList;
import java.util.Date;

import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class ScheduleActivity extends ListActivity {
  Route route;
  Schedule schedule;
  Date  currentTime = new Date();

  static class Time {
    int time;
    
    
    public Time(int time) {
      super();
      this.time = time;
    }

    @Override
    public String toString() {
      return Schedule.formatTime(time);
    }    
  }
  
  static class TimeList extends AbstractList<Time> {
    Schedule schedule;
    int[] times;
    Date  currentTime;

    public TimeList(Schedule schedule, Date currentTime) {
      this.schedule = schedule;
      times = schedule.getTimes(currentTime);
      this.currentTime = currentTime;
    }
    @Override
    public Time get(int location) {
      return new Time(times[location]);
    }

    @Override
    public int size() {
      return times.length;
    }
    
    public int getDefaultPosition() {
      int time = Schedule.getTime(currentTime);
      for( int i = 1; i < times.length; i++ ) {
        if ( times[i] >= time )
          return i - 1;
      }
      return times.length - 1;
    }
    
    
  }
  
  public ScheduleActivity() {    
  }
    
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      String name = (String) getIntent().getSerializableExtra(Info.KEY_ROUTE);
      route = Info.routeManager().loadRoute(name);
      schedule = route.getSchedule();
      TimeList times = new TimeList(schedule,currentTime);
      ScheduleAdapter scheduleAdapter = new ScheduleAdapter(times);
      setListAdapter(scheduleAdapter);
      int pos = times.getDefaultPosition();
      if ( pos >= 0 )
        this.setSelection(pos);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  class ScheduleAdapter extends ArrayAdapter<Time> {
    TimeList times;
    public ScheduleAdapter(TimeList times) {
      super(ScheduleActivity.this, R.layout.list_item, times);
    }
  }    
}