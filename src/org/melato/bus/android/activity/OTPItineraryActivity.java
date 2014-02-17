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

import org.melato.android.menu.Menus;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.OTP.Leg;
import org.melato.bus.otp.PlanConverter;
import org.melato.bus.otp.PlanConverter.MismatchException;
import org.melato.bus.plan.OTPLegAdapter;
import org.melato.bus.plan.RouteLeg;
import org.melato.bus.plan.Sequence;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/** Displays a single OTP itinerary as a list of legs. */
public class OTPItineraryActivity extends Activity implements OnItemClickListener, OnClickListener  {
  private OTP.Itinerary itinerary;

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_activity);
    Intent intent = getIntent();
    itinerary = (OTP.Itinerary) intent.getSerializableExtra(Keys.ITINERARY);
    ListView listView = (ListView)findViewById(R.id.listView);
    listView.setAdapter(new ItineraryAdapter());
    listView.setOnItemClickListener(this);
    Menus.addIcons(this, (LinearLayout) findViewById(R.id.icons), R.menu.itinerary_menu, this);
  }

  String legLabel(OTP.Leg leg) {
    return LegFormatter.label(new OTPLegAdapter(leg), this);
  }
  class ItineraryAdapter extends ArrayAdapter<OTP.Leg> {
    public ItineraryAdapter() {
      super(OTPItineraryActivity.this, R.layout.list_item, itinerary.legs); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      OTP.Leg leg = itinerary.legs[position];
      String text = legLabel(leg);
      view.setText( text );
      return view;
    }
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     Menus.inflate(inflater, R.menu.itinerary_menu, menu);
     //HelpActivity.addItem(menu, this, Help.PLAN);
     return true;
  }
  
  void showSequence() {
    try {
      Sequence sequence = new PlanConverter(Info.routeManager(this)).convertToSequence(itinerary);
      Info.setSequence(this, sequence);
      startActivity(new Intent(this, SequenceActivity.class));
    } catch (MismatchException e) {
      Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT);
      toast.show();    
    }
  }

  public boolean onItemSelected(int itemId) {
    boolean handled = false;
    switch(itemId) {
      case R.id.sequence:
        showSequence();
        handled = true;
        break;
      case R.id.map:
        SequenceActivities.showMap(this, itinerary);
        handled = true;
        break;
    }
    return handled ? true : false;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return onItemSelected(item.getItemId());
  }
  
  @Override
  public void onClick(View v) {
    onItemSelected(v.getId());
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Leg leg = itinerary.legs[position];
    if ( leg instanceof OTP.TransitLeg) {
      OTP.TransitLeg transit = (OTP.TransitLeg) leg;
      try {
        RouteLeg routeLeg = new PlanConverter(Info.routeManager(this)).convertLeg(transit);
        BusActivities activities = new BusActivities(this);
        activities.showRoute(routeLeg.getRStop1());    
      } catch (MismatchException e) {
        Toast toast = Toast.makeText(OTPItineraryActivity.this, R.string.error_convert_route, Toast.LENGTH_SHORT);
        toast.show();              
      }
    }
  }    
  
}