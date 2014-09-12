package org.melato.bus.android.map;

import org.melato.bus.android.RoutesMap;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.model.RStop;
import org.melato.bus.plan.Sequence;

import android.app.Activity;
import android.content.Intent;

public class GoogleRoutesMap implements RoutesMap {
  private Activity activity;
  
  public GoogleRoutesMap(Activity activity) {
    super();
    this.activity = activity;
  }

  @Override
  public void showRoute(RStop rstop) {
    new BusActivities(activity).showRoute(rstop, RouteMapActivity.class);
  }

  @Override
  public void showSequence(Sequence sequence) {
    Intent intent = new Intent(activity, SequenceMapActivity.class);
    intent.putExtra(Keys.SEQUENCE, sequence);
    activity.startActivity(intent);    
  }
}
