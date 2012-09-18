package org.melato.bus.android.activity;

import java.text.DecimalFormat;

import org.melato.bus.android.R;

import android.content.Context;
import android.widget.TextView;

public class UI {
  public static final DecimalFormat KM = new DecimalFormat( "0.00" );
  static void highlight(TextView view, boolean isHighlighted) {
    Context context = view.getContext();
    if ( isHighlighted ) {
      view.setBackgroundColor(context.getResources().getColor(R.color.list_highlighted_background));
      view.setTextColor(context.getResources().getColor(R.color.list_highlighted_text));
    } else {
      view.setBackgroundColor(context.getResources().getColor(R.color.list_background));
      view.setTextColor(context.getResources().getColor(R.color.list_text));
    }
  }
  
  public static String distance(float distance) {
    if ( Math.abs(distance) < 1000 ) {
      return String.valueOf( Math.round(distance)) + "m";
    } else {
      return KM.format(distance/1000) + "Km";
    }
  }
  
  public static String straightDistance(float distance) {
    return "(" + distance(distance) + ")";
  }

  public static String routeDistance(float distance) {
    if ( Float.isNaN(distance)) {
      return "";
    }
    String sign = distance >= 0 ? "+" : "-"; 
    return sign + distance(Math.abs(distance));
  }
  
  public static String degrees(float degrees) {
    return String.valueOf(degrees);
  }

}
