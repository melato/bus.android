/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.bus.android.map;

import android.app.Activity;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * A map overlay for displaying a single point.
 * @author Alex Athanasopoulos
 */
public class PointOverlay extends ItemizedOverlay<OverlayItem> {
  Activity activity;
  GeoPoint point;
  
  
  public PointOverlay(Activity activity, GeoPoint point, int drawableId) {
    super(boundCenterBottom(activity.getResources().getDrawable(drawableId)));
    this.point = point;
    this.activity = activity;
    populate();
  }

  @Override
  protected OverlayItem createItem(int i) {
    return new OverlayItem(point, null, null);
  }
  @Override
  public int size() {
    return 1;
  }
  public GeoPoint getPoint() {
    return point;
  }
  public void setPoint(GeoPoint point) {
    this.point = point;
  }
}
