package org.melato.bus.android;

import org.melato.bus.android.activity.LocationEndpoints;
import org.melato.bus.model.RStop;
import org.melato.bus.plan.Sequence;

import android.content.Intent;
import android.support.v4.app.Fragment;

public interface RoutesMap {
  void showRoute(RStop rstop);
  void showSequence(Sequence sequence);
  void showMap();
  /** Start an activity to edit two endpoints
   * @param endpoints  The existing endpoints, if any.
   * @param activity The activity that should receive the result.
   * @param requestCode The request code to use when calling activity.startActivityForResult.
   */
  void startActivityForEndpoints(LocationEndpoints endpoints, Fragment fragment, int requestCode);
  /**
   * Extract LocationEndpoints from an Intent
   * @param intent The intent received in onActivityResult()
   * @return The resulting LocationEndpoints or null
   */
  LocationEndpoints getEndpoints(Intent intent);
}
