package org.melato.bus.android.map;

import org.melato.bus.android.RoutesMap;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.android.activity.LocationEndpoints;
import org.melato.bus.model.RStop;
import org.melato.bus.plan.Sequence;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

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

  @Override
  public void startActivityForEndpoints(LocationEndpoints endpoints, Fragment fragment, int requestCode) {
    Intent intent = new Intent(fragment.getActivity(), SelectionMapActivity.class);
    intent.putExtra(Keys.LOCATION_ENDPOINTS, endpoints);
    fragment.startActivityForResult(intent, requestCode);    
  }
  
  @Override
  public LocationEndpoints getEndpoints(Intent intent) {
    return (LocationEndpoints) intent.getSerializableExtra(Keys.LOCATION_ENDPOINTS);    
  }
  
}
