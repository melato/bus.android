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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.melato.bus.android.R;
import org.melato.bus.client.Formatting;
import org.melato.bus.otp.OTP;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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

  class ItineraryAdapter extends ArrayAdapter<OTP.Leg> {
    public ItineraryAdapter() {
      super(OTPItineraryActivity.this, R.layout.list_item, itinerary.legs); 
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      TextView view = (TextView) super.getView(position, convertView, parent);
      OTP.Leg leg = itinerary.legs[position];
      String text = "";
      if ( leg instanceof OTP.TransitLeg){
        OTP.TransitLeg t = (OTP.TransitLeg) leg; 
        text = t.label + " (" + Formatting.formatTime(leg.startTime) + ") " + t.from.name + " -> " + t.to.name +
            " (" + Formatting.formatTime(leg.endTime) + ")";
      } else if ( leg instanceof OTP.WalkLeg ) {
        text = "Walk " + " (" + Formatting.formatTime(leg.startTime) + ") " + Formatting.straightDistance(leg.distance);
      }
      view.setText( text );
      return view;
    }
  }
}