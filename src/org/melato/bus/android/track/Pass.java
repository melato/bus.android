package org.melato.bus.android.track;

import java.util.Date;

import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;

public class Pass {
  public static final int MANUAL = 0;
  public static final int AUTOMATIC = 1;
  private RouteId routeId;
  Date    date;
  String  marker;
  int     type;
  
  
  public Pass() {
  }

  public Pass(RouteId routeId, String marker) {
    this.routeId = routeId;
    this.marker = marker;
    this.date = new Date();
    this.type = MANUAL;
  }
  
  public Pass(Route route, String marker) {
    this.routeId = route.getRouteId();
    this.marker = marker;
    this.date = new Date();
    this.type = MANUAL;
  }
  
  public RouteId getRouteId() {
    return routeId;
  }
  public void setRouteId(RouteId routeId) {
    this.routeId = routeId;
  }
  public String getMarker() {
    return marker;
  }
  public void setMarker(String marker) {
    this.marker = marker;
  }
  public int getType() {
    return type;
  }
  public void setType(int type) {
    this.type = type;
  }
  public Date getDate() {
    return date;
  }
  public void setDate(Date date) {
    this.date = date;
  }
}
