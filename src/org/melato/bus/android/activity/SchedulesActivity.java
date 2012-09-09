package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Route;
import org.melato.bus.model.Schedule;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a list of all schedules for a route
 * @author Alex Athanasopoulos
 */
public class SchedulesActivity extends ListActivity {
  protected BusActivities activities;
  private Schedule schedule;
  private DaySchedule[] schedules;
  private Route route;

  public SchedulesActivity() {    
  }
    
  class SchedulesAdapter extends ArrayAdapter<DaySchedule> {
    public SchedulesAdapter() {
      super(SchedulesActivity.this, R.layout.list_item, schedules);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      String name = ScheduleActivity.getScheduleName(getContext(), this.getItem(position).getDays());
      view.setText( name );
      return view;
    }
  }
  
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activities = new BusActivities(this);
    route = activities.getRoute();
    setTitle( route.getFullTitle() );
    schedule = route.getSchedule();
    schedules = schedule.getSchedules();
    setListAdapter(new SchedulesAdapter());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    int days = schedules[position].getDays();
    Intent intent = new Intent(this, ScheduleActivity.class);
    new IntentHelper(intent).putRoute(route);
    intent.putExtra(ScheduleActivity.KEY_DAYS, days);
    startActivity(intent);    
    
  }
}