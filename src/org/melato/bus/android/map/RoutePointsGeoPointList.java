package org.melato.bus.android.map;

import java.util.AbstractList;

import org.melato.bus.model.cache.RoutePoints;

import com.google.android.maps.GeoPoint;

public class RoutePointsGeoPointList extends AbstractList<GeoPoint> {
  private RoutePoints points;
  private int offset;
  private int size;
  
  public RoutePointsGeoPointList(RoutePoints points) {
    super();
    this.points = points;
    offset = 0;
    size = points.size();
  }

  public RoutePointsGeoPointList(RoutePoints points, int start, int end) {
    super();
    this.points = points;
    offset = start;
    size = end - start;
  }

  @Override
  public GeoPoint get(int index) {
    return RoutePlotter.getGeoPoint(points, offset + index);
  }

  @Override
  public int size() {
    return size;
  }

}
