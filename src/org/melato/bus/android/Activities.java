package org.melato.bus.android;

import org.melato.bus.model.Route;

import android.content.Context;
import android.content.Intent;

public class Activities {
  /** the qualified name of a route. */
  public static final String KEY_ROUTE = "org.melato.bus.android.route";
  
  public static void showSchedule(Context context, Route route) {
    Intent intent = new Intent(context, ScheduleActivity.class);
    intent.putExtra(KEY_ROUTE, route.qualifiedName());
    context.startActivity(intent);
   }

  public static void showStops(Context context, Route route) {
    Intent intent = new Intent(context, StopsActivity.class);
    intent.putExtra(KEY_ROUTE, route.qualifiedName());
    context.startActivity(intent);
   }
  
  public static void showNearby(Context context) {
    context.startActivity(new Intent(context, NearbyActivity.class));
  }
}
