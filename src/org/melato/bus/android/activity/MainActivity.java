package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.app.UpdateActivity;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.model.Route;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
  protected BusActivities activities;
  
  private boolean checkUpdates() {
    if ( ! UpdateActivity.checkUpdates(this) ) {
      return false;
    }
    if ( ! SqlRouteStorage.databaseFile(this).exists()) {
      return false;
    }
    return true;
  }
  
  /** Called when the activity is first created. */  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if ( ! checkUpdates() ) {
        finish();
        return;
      }
      activities = new BusActivities(this);
      List<Route> recent = activities.getRecentRoutes();
      if ( recent.size() > 0 ) {
        RoutesActivity.showRecent(this);
      } else {
        RoutesActivity.showAll(this);
      }
      finish();
  }


}
