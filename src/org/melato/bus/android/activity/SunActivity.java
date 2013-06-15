package org.melato.bus.android.activity;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.RouteStorage;
import org.melato.sun.SunsetProvider;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/** An activity that shows the sunrise and sunset for the current date. */
public class SunActivity extends ListActivity {  
  DecimalFormat format2d = new DecimalFormat("00");
  List<String> items = new ArrayList<String>();
  
  void addProperty(int resourceId, String value) {
    items.add( getString(resourceId) + ": " + value );    
  }
  
  String formatTime(int time) {
    return format2d.format( time / 60 ) + ":" + format2d.format(time % 60);
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTitle(R.string.sunrise_sunset);
    RouteStorage storage = Info.routeManager(this).getStorage();
    if ( storage instanceof SunsetProvider) {
      SunsetProvider sun = (SunsetProvider) storage;
      Date date = new Date();      
      int[] values = sun.getSunriseSunset(date);
      if ( values != null) {
        DateFormat dateFormat = new SimpleDateFormat("d-M-y");
        addProperty(R.string.date, dateFormat.format(date));
        addProperty(R.string.sunrise, formatTime(values[0]));
        addProperty(R.string.sunset, formatTime(values[1]));
      }
    }
    setListAdapter( new ArrayAdapter<String>(this, R.layout.list_item, items));
  }
}
