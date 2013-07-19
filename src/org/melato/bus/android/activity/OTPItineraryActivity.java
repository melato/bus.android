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

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.Formatting;
import org.melato.bus.model.Schedule;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.PlanConverter;
import org.melato.bus.otp.PlanConverter.MismatchException;
import org.melato.bus.plan.Sequence;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

/** Displays a single OTP itinerary */
public class OTPItineraryActivity extends ListActivity {
  public static String ITINERARY = "itinerary";
  private OTP.Itinerary itinerary;

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    itinerary = (OTP.Itinerary) intent.getSerializableExtra(ITINERARY);
    setListAdapter(new ItineraryAdapter());
  }

  String legLabel(OTP.Leg leg) {
    StringBuilder buf = new StringBuilder();
    if ( leg instanceof OTP.TransitLeg){
      buf.append(Formatting.formatTime(leg.startTime));
      buf.append(" ");
      OTP.TransitLeg t = (OTP.TransitLeg) leg; 
      buf.append(t.label);
      buf.append(" ");
      buf.append(t.from.name);
      buf.append(" -> ");
      buf.append(t.to.name);
      buf.append(" (");
      buf.append(Formatting.formatTime(leg.endTime));
      buf.append(")");
    } else if ( leg instanceof OTP.WalkLeg ) {
      buf.append(getString(R.string.walk));
      buf.append(Formatting.straightDistance(leg.distance));
      buf.append(" ");
      buf.append(Schedule.formatDuration(leg.getDuration()));
    }
    return buf.toString();
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
     inflater.inflate(R.menu.itinerary_menu, menu);
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
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.sequence:
        showSequence();
        handled = true;
        break;
    }
    return handled ? true : false;
  }

}