package org.melato.bus.android.map;

import java.util.AbstractList;

import org.melato.bus.model.Stop;

import com.google.android.maps.GeoPoint;

public class StopsGeoPointList extends AbstractList<GeoPoint> {
  private Stop[] stops;
  private int offset;
  private int size;
  
  public StopsGeoPointList(Stop[] stops, Stop stop1, Stop stop2) {
    super();
    this.stops = stops;
    offset = stop1.getIndex();
    size = stop2.getIndex() + 1 - offset;
  }

  @Override
  public GeoPoint get(int index) {
    Stop stop = stops[offset + index];
    return new GeoPoint((int) (1e6f*stop.getLat()), (int)(1e6f*stop.getLon()));    
  }

  @Override
  public int size() {
    return size;
  }

}
