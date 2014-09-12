package org.melato.bus.android.map;

import org.melato.bus.android.RoutesMap;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.android.activity.LocationEndpoints;
import org.melato.bus.model.RStop;
import org.melato.bus.plan.Sequence;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class GoogleRoutesMap implements RoutesMap {
  private Context context;
  
  public GoogleRoutesMap(Context context) {
    super();
    this.context = context;
  }
  
  @Override
  public void showRoute(RStop rstop) {
    new BusActivities(context).showRoute(rstop, RouteMapActivity.class);
  }

  @Override
  public void showSequence(Sequence sequence) {
    Intent intent = new Intent(context, SequenceMapActivity.class);
    intent.putExtra(Keys.SEQUENCE, sequence);
    context.startActivity(intent);    
  }

  @Override
  public void showMap() {
    context.startActivity(new Intent(context, RouteMapActivity.class));      
  }

  public void startActivityForEndpoints(LocationEndpoints endpoints, Activity activity, int requestCode) {    
    Intent intent = new Intent(activity, SelectionMapActivity.class);
    intent.putExtra(Keys.LOCATION_ENDPOINTS, endpoints);
    activity.startActivityForResult(intent, requestCode);    
  }
  
  public LocationEndpoints getEndpoints(Intent intent) {
    return (LocationEndpoints) intent.getSerializableExtra(Keys.LOCATION_ENDPOINTS);    
  }
  
}
