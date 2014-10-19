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

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.PlanConverter;
import org.melato.bus.otp.PlanConverter.MismatchException;
import org.melato.bus.plan.Sequence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SequenceActivities {
  private static boolean useMap;
  
  public static void showItinerary(Activity context, OTP.Itinerary itinerary) {
    if ( useMap ) {
      showMap(context, itinerary);      
    } else {
      showList(context, itinerary);      
    }
  }
  
  public static void showList(Context context, OTP.Itinerary itinerary) {
    //useMap = false;
    Intent intent = new Intent(context, OTPItineraryActivity.class);
    intent.putExtra(Keys.ITINERARY, itinerary);
    context.startActivity(intent);
  }
  
  public static void showMap(Activity context, OTP.Itinerary itinerary) {
    //useMap = true;
    try {
      Sequence sequence = new PlanConverter(Info.routeManager(context)).convertToSequence(itinerary);
      showMap(context, sequence);
    } catch (MismatchException e) {
      Toast.makeText(context, R.string.error_convert_route, Toast.LENGTH_SHORT).show();
    }      
    
    /*
    Intent intent = new Intent(context, SequenceMapActivity.class);
    intent.putExtra(Keys.ITINERARY, itinerary);
    context.startActivity(intent);
    */
  }
  
  public static void showMap(Activity context, Sequence sequence) {
    if ( sequence.getLegs().isEmpty()) {
      return;
    }
    Info.routesMap(context).showSequence(sequence);
  }
}
