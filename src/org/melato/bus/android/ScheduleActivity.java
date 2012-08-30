package org.melato.bus.android;

import java.util.AbstractList;
import java.util.Date;

import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

/**
 * Displays the schedule for a route
 * @author Alex Athanasopoulos
 *
 */
public class ScheduleActivity extends ListActivity {
  Route route;
  Schedule schedule;

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
    int[] times;

    public TimeList(Schedule schedule) {
      times = schedule.getTimes(new Date());
    }
    @Override
    public Time get(int location) {
      return new Time(times[location]);
    }

    @Override
    public int size() {
      return times.length;
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
      setListAdapter(new ScheduleAdapter());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  class ScheduleAdapter extends ArrayAdapter<Time> {
    public ScheduleAdapter() {
      super(ScheduleActivity.this, R.layout.list_item, new TimeList(schedule));
    }
  }  
  
  class ScheduleAdapter2 extends BaseAdapter {
    TimeList list = new TimeList(schedule);
    
    @Override
    public int getCount() {
      return list.size();
    }

    @Override
    public Object getItem(int position) {
      return list.get(position);
    }

    @Override
    public long getItemId(int position) {
      return list.times[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      return null;
    }
    
  }  
}