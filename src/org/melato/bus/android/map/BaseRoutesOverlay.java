package org.melato.bus.android.map;

import org.melato.bus.model.RouteId;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public abstract class BaseRoutesOverlay extends Overlay {

  public abstract void addRoute(RouteId routeId);

  public abstract void setSelectedRoute(RouteId routeId);

  public abstract GeoPoint getCenter();
  
  public abstract void refresh(MapView view);

}