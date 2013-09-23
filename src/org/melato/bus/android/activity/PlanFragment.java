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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.PlanOptions;
import org.melato.bus.android.R;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.OTPRequest;
import org.melato.bus.plan.NamedPoint;
import org.melato.gps.Point2D;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/** Computes and displays a list of plans for going to a destination.
 **/
public class PlanFragment extends Fragment implements OnClickListener {
  public static NamedPoint origin;
  public static NamedPoint destination;
  public static OTP.Plan plan;
  private Mode[] modes;
  private View view;
  
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
  void showEndpoints() {
    TextView v = (TextView) view.findViewById(R.id.from);
    v.setText(origin != null ? origin.toString() : "");
    v = (TextView) view.findViewById(R.id.to);
    v.setText(destination != null ? destination.toString() : "");
  }
  
  public void swap() {
    NamedPoint temp = origin;
    origin = destination;
    destination = temp;
    showEndpoints();    
  }
  
  private PlanOptions getOptions() {
    return new PlanOptions(getActivity());
  }
  
  
  @Override
  public void onClick(View v) {
    switch( v.getId() ) {
    case R.id.delete_from:
      origin = null;
      showEndpoints();
      break;
    case R.id.delete_to:
      destination = null;
      showEndpoints();
      break;
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
          Bundle savedInstanceState) {
      view = inflater.inflate(R.layout.plan, container, false);
      LinearLayout modeView = (LinearLayout)view.findViewById(R.id.modeView);
      ((ImageButton)view.findViewById(R.id.delete_from)).setOnClickListener(this);
      ((ImageButton)view.findViewById(R.id.delete_to)).setOnClickListener(this);
      Context context = getActivity();
      modes = new Mode[] {
          new Mode(context, OTPRequest.BUS, R.string.mode_bus),
          new Mode(context, OTPRequest.TRAM, R.string.mode_tram),
          new Mode(context, OTPRequest.SUBWAY, R.string.mode_subway),        
      };
      for( int i = 0; i < modes.length; i++ ) {
        modeView.addView(modes[i].createCheckBox(context));
      }
      showEndpoints();
      return view;
  }

  @Override
  public void onResume() {
    super.onResume();
    showEndpoints();
  }
  
  @Override
  public void onDestroyView() {
    savePreferences();
    super.onDestroy();
  }

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
  public OTPRequest buildRequest(Point2D from) {
    OTPRequest request = new OTPRequest();
    Info.routeManager(getActivity()).setOtpDefaults(request);
    request.setFromPlace(from);
    request.setToPlace(destination);
    Date date = new Date();
    TextView timeView = (TextView) view.findViewById(R.id.time);
    int time = parseTime(timeView.getText().toString());
    if ( time >= 0) {
      date = OTPRequest.replaceTime(date, time);
    }
    request.setDate(date);
    CheckBox arriveView = (CheckBox) view.findViewById(R.id.arrive);
    if ( arriveView.isChecked()) {
      request.setArriveBy(true);
    }
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
    return request;
  }

  void savePreferences() {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
    Editor editor = settings.edit();
    for( Mode mode: modes ) {
      mode.setPreference(editor);
    }
    editor.commit();
  }
  
}