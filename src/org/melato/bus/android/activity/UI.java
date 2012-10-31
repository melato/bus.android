package org.melato.bus.android.activity;

import java.text.DecimalFormat;

import org.melato.bus.android.R;
import org.melato.bus.model.Route;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
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
  
  public static int routeColor(int color) {
    // the color is RGB
    // we need to add opaqueness in the alpha channel, otherwise we won't see anything.
    color = color | 0xff000000;
    return color;
  }
  
  public static class OriginalColorScheme implements ColorScheme {
    @Override
    public int getColor(Route route) {
      if ( route == null )
        return Color.BLACK;
      return routeColor(route.getColor());
    }

    @Override
    public int getBackground(Route route) {
      if ( route == null )
        return Color.WHITE;
      return routeColor(route.getBackgroundColor());
    }    
  }
  public static class TwoColorScheme implements ColorScheme {
    private int color;
    private int backgroundColor;
    
    public TwoColorScheme(int color, int backgroundColor) {
      super();
      this.color = color;
      this.backgroundColor = backgroundColor;
    }
    @Override
    public int getColor(Route route) {
      return color;
    }
    @Override
    public int getBackground(Route route) {
      return backgroundColor;
    }    
  }
  public static ColorScheme getColorScheme(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String scheme = prefs.getString(Pref.ROUTE_COLORS, Pref.COLOR_ORIGINAL);
    if ( Pref.COLOR_BLACK_ON_WHITE.equals(scheme)) {
      return new TwoColorScheme(Color.BLACK, Color.WHITE);
    } else if ( Pref.COLOR_WHITE_ON_BLACK.equals(scheme)) {
      return new TwoColorScheme(Color.WHITE, Color.BLACK);      
    } else {
      return new OriginalColorScheme();
    }    
  }
}
