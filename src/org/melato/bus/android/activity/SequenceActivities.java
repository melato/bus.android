package org.melato.bus.android.activity;

import org.melato.bus.android.map.SequenceMapActivity;
import org.melato.bus.otp.OTP;
import org.melato.bus.plan.Sequence;

import android.content.Context;
import android.content.Intent;

public class SequenceActivities {
  private static boolean useMap;
  
  public static void showItinerary(Context context, OTP.Itinerary itinerary) {
    if ( useMap ) {
      showMap(context, itinerary);      
    } else {
      showList(context, itinerary);      
    }
  }
  
  public static void showList(Context context, OTP.Itinerary itinerary) {
    useMap = false;
    Intent intent = new Intent(context, OTPItineraryActivity.class);
    intent.putExtra(Keys.ITINERARY, itinerary);
    context.startActivity(intent);
  }
  
  public static void showMap(Context context, OTP.Itinerary itinerary) {
    useMap = true;
    Intent intent = new Intent(context, SequenceMapActivity.class);
    intent.putExtra(Keys.ITINERARY, itinerary);
    context.startActivity(intent);
  }
  
  public static void showMap(Context context, Sequence sequence) {
    Intent intent = new Intent(context, SequenceMapActivity.class);
    intent.putExtra(Keys.SEQUENCE, sequence);
    context.startActivity(intent);    
  }
}
