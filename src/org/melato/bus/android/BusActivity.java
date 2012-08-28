package org.melato.bus.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.melato.gpx.GPX;
import org.melato.gpx.GPXParser;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BusActivity extends ListActivity {
  List<BusStop> stops;
  
  List<BusStop> createDemoStops1() {
    BusStop[] stops = new BusStop[80];
    for( int i = 0; i < stops.length; i++ ) {
      stops[i] = new BusStop("Stop " + i );
    }
    return Arrays.asList(stops);
  }
  
  List<BusStop> createDemoStops() {
    try {
      GPXParser parser = new GPXParser();
      InputStream input = getClass().getResourceAsStream("data/x051-1.gpx");
      if ( input == null ) {
        throw new RuntimeException( "Cannot find resource." );
      }
      GPX gpx = parser.parse( input );
      return BusStop.listFromGPX(gpx);      
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  public BusActivity() {
  }
  
/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setStops(createDemoStops());
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
  }

  public void setStops(List<BusStop> stops) {
    this.stops = stops;
    setListAdapter(new ArrayAdapter<BusStop>(this, R.layout.bus_stop_item,
        stops));
  }
}