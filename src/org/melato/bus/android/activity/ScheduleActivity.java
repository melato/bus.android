package org.melato.bus.android.activity;

import java.util.Date;

import org.melato.bus.android.R;
import org.melato.bus.android.model.TimeOfDay;
import org.melato.bus.android.model.TimeOfDayList;
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;

import android.app.ListActivity;
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
public class ScheduleActivity extends ListActivity {
  protected BusActivities activities;
  private Schedule schedule;
  private Date  currentTime = new Date();

  public ScheduleActivity() {    
  }
    
  protected String getScheduleName() {
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
      activities = new BusActivities(this);
      Route route = activities.getRoute();
      schedule = route.getSchedule();
      String title = route.getLabel() + "-" + route.getDirection() + " " + getScheduleName();
      setTitle(title);
      TimeOfDayList times = new TimeOfDayList(schedule,currentTime);
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

  class ScheduleAdapter extends ArrayAdapter<TimeOfDay> {
    TimeOfDayList times;
    public ScheduleAdapter(TimeOfDayList times) {
      super(ScheduleActivity.this, R.layout.list_item, times);
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