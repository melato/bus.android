package org.melato.bus.android;

import org.melato.bus.android.activity.Pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/** Contains preference values related to planning an itinerary */
public class PlanOptions {
  private int maxWalk;
  private float walkSpeed;
  private boolean fewerTransfers;
  
  /** Get the walk speed in m/s */
  public float getWalkSpeedMetric() {
    return walkSpeed * 1000f / 3600f;
  }
  
  public int getMaxWalk() {
    return maxWalk;
  }

  /** Get the walk speed in Km/h */
  public float getWalkSpeed() {
    return walkSpeed;
  }

  public boolean isFewerTransfers() {
    return fewerTransfers;
  }

  static int getInt(SharedPreferences prefs, String key, int defaultValue) {
    try {
      String s = prefs.getString(key, null);
      if ( s != null ) {
        return Integer.parseInt(s);
      }
    } catch( ClassCastException e ) {          
    } catch( NumberFormatException e ) {          
    }
    return defaultValue;
  }
  static float getFloat(SharedPreferences prefs, String key, float defaultValue) {
    try {
      String s = prefs.getString(key, null);
      if ( s != null ) {
        return Float.parseFloat(s);
      }
    } catch( ClassCastException e ) {          
    } catch( NumberFormatException e ) {          
    }
    return defaultValue;
  }
  static boolean getBoolean(SharedPreferences prefs, String key, boolean defaultValue) {
    try {
      String s = prefs.getString(key, null);
      if ( s != null ) {
        return Boolean.parseBoolean(s);
      }
    } catch( Exception e ) {          
    }
    return defaultValue;
  }
  public PlanOptions(Context context) {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
    maxWalk = getInt(settings, Pref.MAX_WALK_DISTANCE, 1000);
    walkSpeed = getFloat(settings, Pref.WALK_SPEED, 5.0f);
    fewerTransfers = getBoolean(settings, Pref.FEWER_TRANSFERS, true);    
  }
}

