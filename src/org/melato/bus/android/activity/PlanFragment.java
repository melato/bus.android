/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013,2014 Alex Athanasopoulos.  All Rights Reserved.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.melato.android.app.HelpActivity;
import org.melato.android.bookmark.BookmarksActivity;
import org.melato.android.menu.Menus;
import org.melato.bus.android.Info;
import org.melato.bus.android.PlanOptions;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.TimeDialog.OnTimeSetListener;
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.bookmark.BookmarkTypes;
import org.melato.bus.android.bookmark.LocationBookmarkActivity;
import org.melato.bus.android.map.SelectionMapActivity;
import org.melato.bus.model.Schedule;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.OTPRequest;
import org.melato.bus.plan.NamedPoint;
import org.melato.bus.plan.PlanEndpoints;
import org.melato.client.Bookmark;
import org.melato.client.Serialization;
import org.melato.gps.Point2D;
import org.melato.util.DateId;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

/** Computes and displays a list of plans for going to a destination.
 **/
public class PlanFragment extends Fragment implements OnClickListener, OnTimeSetListener, OnDateSetListener {
  public static NamedPoint origin;
  public static NamedPoint destination;
  public static OTP.Plan plan;
  private Mode[] modes;
  private LinearLayout view;
  private TextView timeView;
  private TextView dateView;
  private static Integer timeInMinutes;
  private static Integer dateId;
  private static boolean arriveAt;
  private int contextViewId;
  private int REQUEST_MAP = 1;
  private int REQUEST_BOOKMARK = 2;
  
  /** A mode of transport. */
  static class Mode {
    /** the programmatic code of the mode, e.g. TRANSIT */
    private String code;
    /** The resourse id for the mode label */
    private int     labelResourceId;
    /** Whether or not this mode is enabled. */
    private boolean enabled;
    private CheckBox check;
    
    public String prefKey() {
      return "mode." + code;
    }
    public boolean getPreference(Context context) {
      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
      return settings.getBoolean(prefKey(), true);
    }
    public void setPreference(SharedPreferences.Editor settings) {
      settings.putBoolean(prefKey(), check.isChecked());
    }
    public Mode(Context context, String code, int resource) {
      super();
      this.code = code;
      this.labelResourceId = resource;
      enabled = getPreference(context);
    }
    public CheckBox createCheckBox(Context context) {
      check = new CheckBox(context);
      check.setText(labelResourceId);
      check.setChecked(enabled);
      return check;
    }
    
  }
  
  /** Fragment for displaying the time picker dialog */
  class TimeFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      int time = 0;
      if ( timeInMinutes != null) {
        time = timeInMinutes;
      } else {
        time = Schedule.getTime(new Date());
      }
      TimeDialog dialog = new TimeDialog(getActivity(), PlanFragment.this, time / 60, time % 60, true);
      return dialog;
    }    
  }

  /** Fragment for displaying the date picker dialog */
  class DateFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
      int year = 0;
      int month = 0;
      int day = 0;
      if ( dateId != null) {
        year = DateId.getYear(dateId);
        month = DateId.getMonth(dateId) - 1;
        day = DateId.getDay(dateId);
      } else {
        Calendar cal = new GregorianCalendar();
        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
      }
      DatePickerDialog dialog = new DatePickerDialog(getActivity(), PlanFragment.this, year, month, day); 
      return dialog;
    }    
  }

  @Override
  public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
    timeInMinutes = hourOfDay * 60 + minute;
    timeView.setText(Schedule.formatTime(timeInMinutes));
  }
    
  @Override
  public void onDateSet(DatePicker view, int year, int monthOfYear,
      int dayOfMonth) {
    dateId = DateId.dateId(year, monthOfYear + 1, dayOfMonth);
    dateView.setText(String.valueOf(dateId));
  }

  void showParameters() {
    TextView v = (TextView) view.findViewById(R.id.from);
    if ( origin != null ) {
      v.setText(origin.toString());
    } else {
      v.setText(R.string.useCurrentLocation);
    }
    v = (TextView) view.findViewById(R.id.to);
    if ( destination != null ) {
      v.setText(destination.toString());
    } else {
      v.setText(R.string.selectDestination);
    }
    if ( timeInMinutes != null) {
      timeView.setText(Schedule.formatTime(timeInMinutes));      
    } else {
      timeView.setText(R.string.timeNow);
    }
  }
  
  public void swap() {
    NamedPoint temp = origin;
    origin = destination;
    destination = temp;
    showParameters();    
  }
  
  private PlanOptions getOptions() {
    return new PlanOptions(getActivity());
  }
  
  
  void bookmark() {
    PlanEndpoints endpoints = getEndpoints();
    Bookmark bookmark = new Bookmark(BookmarkTypes.PLAN, endpoints.getName(), endpoints);
    BookmarksActivity.addBookmarkDialog(getActivity(), bookmark);
  }
  
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);    
    PlanEndpoints endpoints = Serialization.cast(activity.getIntent().getSerializableExtra(Keys.ENDPOINTS), PlanEndpoints.class);
    if ( endpoints != null) {
      setEndpoints(endpoints);
    }
  }


  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      view = (LinearLayout) inflater.inflate(R.layout.plan, container, false);
      ViewGroup modeView = (ViewGroup)view.findViewById(R.id.modeView);
      timeView = (TextView)view.findViewById(R.id.time);
      dateView = (TextView)view.findViewById(R.id.date);
      ((TextView)view.findViewById(R.id.from)).setOnClickListener(this);
      ((TextView)view.findViewById(R.id.to)).setOnClickListener(this);
      ((TextView)view.findViewById(R.id.timeType)).setOnClickListener(this);
      timeView.setOnClickListener(this);
      dateView.setOnClickListener(this);
      registerForContextMenu(view.findViewById(R.id.timeType));
      registerForContextMenu(view.findViewById(R.id.from));
      registerForContextMenu(view.findViewById(R.id.to));
      Context context = getActivity();
      modes = new Mode[] {
          new Mode(context, OTPRequest.BUS, R.string.mode_bus),
          new Mode(context, OTPRequest.TRAM, R.string.mode_tram),
          new Mode(context, OTPRequest.SUBWAY, R.string.mode_subway),        
      };
      for( int i = 0; i < modes.length; i++ ) {
        modeView.addView(modes[i].createCheckBox(context));
      }
      showParameters();
      setHasOptionsMenu(true);
      Menus.addIcons(getActivity(), (LinearLayout) view.findViewById(R.id.icons), R.menu.plan_menu, this);      
      return view;
  }
  
  @Override
  public void onClick(View v) {
    switch( v.getId() ) {
    case R.id.time:
      {
        TimeFragment timeFragment = new TimeFragment();
        FragmentActivity activity = (FragmentActivity) getActivity();
        timeFragment.show(activity.getSupportFragmentManager(), "timePicker");      
      }
      break;
    case R.id.date:
    {
      DateFragment fragment = new DateFragment();
      FragmentActivity activity = (FragmentActivity) getActivity();
      fragment.show(activity.getSupportFragmentManager(), "datePicker");      
    }
    break;
    case R.id.from:
    case R.id.to:
    case R.id.timeType:
      contextViewId = v.getId();
      getActivity().openContextMenu(v);
      break;
    case R.id.plan:
      ((PlanTabsActivity) getActivity()).plan();
      break;
    case R.id.map:
      showMap();
      break;
    case R.id.swap:
      swap();
      break;
    case R.id.bookmark:
      bookmark();
      break;
    case R.id.pref:
      startActivity(new Intent(getActivity(), BusPreferencesActivity.class));      
      break;
    }
  }
  
  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getActivity().getMenuInflater();
    switch(v.getId()) {
      case R.id.timeType:
        inflater.inflate(R.menu.plan_time_menu, menu);
        break;
      case R.id.from:
        inflater.inflate(R.menu.plan_location_menu, menu);
        inflater.inflate(R.menu.plan_here_menu, menu);
        break;
      case R.id.to:
        inflater.inflate(R.menu.plan_location_menu, menu);
        break;
      default:
        break;
    }
  }
  
  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    Menus.inflate(inflater, R.menu.plan_menu, menu);
    HelpActivity.addItem(menu, getActivity(), Help.PLAN);
  }


  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch(item.getItemId()) {
    case R.id.now:
      timeInMinutes = null;
      arriveAt = false;
      ((TextView)view.findViewById(R.id.timeType)).setText(R.string.timeDepart);
      timeView.setText(R.string.timeNow);
      break;
    case R.id.leaveAt:
      arriveAt = false;
      if ( timeInMinutes == null) {
        timeInMinutes = Schedule.getTime(new Date()); 
      }      
      ((TextView)view.findViewById(R.id.timeType)).setText(R.string.timeDepart);
      break;
    case R.id.arriveAt:
      arriveAt = true;
      ((TextView)view.findViewById(R.id.timeType)).setText(R.string.timeArrive);
      break;
    case R.id.bookmark:
      selectBookmark();
      break;
    case R.id.map:
      showMap();
      break;
    case R.id.here:
      origin = null;
      showParameters();
      break;
    }
    return super.onContextItemSelected(item);
  }


  @Override
  public void onResume() {
    super.onResume();
    showParameters();
  }
  
  @Override
  public void onDestroyView() {
    savePreferences();
    super.onDestroy();
  }

  /** Parse a time string
   * @param s A string of the form hh:mm
   * @return seconds since midnight.
   */
  int parseTime(String s) {
    if ( s == null )
      return -1;
    s = s.trim();
    String[] fields = s.split(":");
    if ( fields.length == 2 ) {
      try {
        int time = Integer.parseInt(fields[0]) * 60 + Integer.parseInt(fields[1]);
        return time * 60;
      } catch( NumberFormatException e) {        
      }
    }
    return -1;
  }
  
  public PlanEndpoints getEndpoints() {
    PlanEndpoints endpoints = new PlanEndpoints();
    endpoints.origin = origin;
    endpoints.destination = destination;
    endpoints.time = timeInMinutes;
    endpoints.arriveAt = arriveAt;
    return endpoints;    
  }
  public void setEndpoints(PlanEndpoints endpoints) {
    origin = endpoints.origin;
    destination = endpoints.destination;
    timeInMinutes = endpoints.time;
    arriveAt = endpoints.arriveAt;
  }
  public OTPRequest buildRequest(Point2D from) {
    OTPRequest request = new OTPRequest();
    Info.routeManager(getActivity()).setOtpDefaults(request);
    request.setFromPlace(from);
    request.setToPlace(destination);
    Calendar cal = new GregorianCalendar();
    if ( dateId != null) {
      DateId.setCalendar(dateId, cal);
    }
    if ( timeInMinutes != null) {
      cal.set(Calendar.HOUR_OF_DAY, timeInMinutes / 60);
      cal.set(Calendar.MINUTE, timeInMinutes % 60);
      cal.set(Calendar.SECOND, 0);
      cal.set(Calendar.MILLISECOND, 0);
    }
    Date date = cal.getTime();
    Log.i("aa", date.toString());
    request.setDate(date);
    request.setArriveBy(arriveAt);
    List<String> modeList = new ArrayList<String>();
    modeList.add(OTPRequest.WALK);
    for( Mode mode: modes ) {
      if ( mode.check.isChecked()) {
        modeList.add(mode.code);
      }
    }
    request.setMode(modeList);
    PlanOptions options = getOptions();
    request.setMaxWalkDistance(options.getMaxWalk());
    request.setWalkSpeed(options.getWalkSpeedMetric());
    request.setMin(options.isFewerTransfers() ? OTPRequest.OPT_TRANSFERS : OTPRequest.OPT_QUICK);
    request.setMinTransferTime(options.getMinTransferTime());
    return request;
  }
  
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if ( resultCode != Activity.RESULT_OK ) {
      return;
    }
    if ( requestCode == REQUEST_MAP) {
      LocationEndpoints endpoints = (LocationEndpoints) data.getSerializableExtra(Keys.LOCATION_ENDPOINTS);
      if ( endpoints != null) {
        PlanFragment.origin = endpoints.origin;
        PlanFragment.destination = endpoints.destination;
        showParameters();
      }
    } else if ( requestCode == REQUEST_BOOKMARK) {
      NamedPoint point = (NamedPoint) data.getSerializableExtra(LocationBookmarkActivity.KEY_LOCATION);
      if ( point != null ) {
        switch( contextViewId ) {
        case R.id.from:
          PlanFragment.origin = point;
          showParameters();
          break;
        case R.id.to:
          PlanFragment.destination = point;
          showParameters();
          break;
        default:
          break;
        }
      }
    }
  }

  void showMap() {
    Intent intent = new Intent(getActivity(), SelectionMapActivity.class);
    intent.putExtra(Keys.LOCATION_ENDPOINTS, new LocationEndpoints(PlanFragment.origin, PlanFragment.destination));
    startActivityForResult(intent, REQUEST_MAP);    
  }
  void selectBookmark() {
    Intent intent = new Intent(getActivity(), LocationBookmarkActivity.class);
    startActivityForResult(intent, REQUEST_BOOKMARK);    
  }
  
  void savePreferences() {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
    Editor editor = settings.edit();
    for( Mode mode: modes ) {
      mode.setPreference(editor);
    }
    editor.commit();
  }

  private static void updateEndpoint(NamedPoint original, NamedPoint result) {
    if ( original != null && original.getName() == null && result != null) {
      original.setName(result.getName());
    }
  }
  public void setPlan(OTP.Plan plan, OTPRequest request) {
    PlanFragment.plan = plan;
    updateEndpoint(PlanFragment.origin, plan.from);
    updateEndpoint(PlanFragment.destination, plan.to);
    showParameters();
  }
  
}