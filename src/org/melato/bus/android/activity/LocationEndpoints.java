/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013,2014 Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
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

  @Override
  public String toString() {
    return origin + " -> " + destination;
  }
  
}
