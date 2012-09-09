package org.melato.bus.android.activity;

import org.melato.bus.android.AndroidLogger;
import org.melato.bus.android.R;
import org.melato.bus.android.model.NearbyStop;
import org.melato.log.Log;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class NearbyActivity extends ListActivity {
  private BusActivities activities;
  private NearbyContext nearby;

  public NearbyActivity() {
  }

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      activities = new BusActivities(this);
      nearby = new NearbyContext(this);
  }
  
  @Override
  protected void onDestroy() {
    nearby.close();
    super.onDestroy();
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    NearbyStop p = nearby.getStop(position);
    activities.showRoute(p.getRoute());
 }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.nearby_menu, menu);
     return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return activities.onOptionsItemSelected(item);
  }
 
}