package org.melato.bus.android.activity;

import java.io.Serializable;

import org.melato.bus.plan.NamedPoint;

public class LocationEndpoints implements Serializable {
  private static final long serialVersionUID = 1L;
  public NamedPoint origin;
  public NamedPoint destination;
    
  public LocationEndpoints() {
    super();
  }

  public LocationEndpoints(NamedPoint origin, NamedPoint destination) {
    super();
    this.origin = origin;
    this.destination = destination;
  }
}
