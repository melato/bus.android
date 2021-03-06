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
package org.melato.bus.android.bookmark;

import org.melato.android.bookmark.BookmarkHandler;
import org.melato.android.bookmark.BookmarkType;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.IntentHelper;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.android.activity.PlanTabsActivity;
import org.melato.bus.android.activity.PointSelectionActivity;
import org.melato.bus.android.activity.ScheduleActivity;
import org.melato.bus.android.activity.SequenceActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.plan.NamedPoint;
import org.melato.bus.plan.PlanEndpoints;
import org.melato.bus.plan.Sequence;
import org.melato.client.Bookmark;
import org.melato.gps.Point2D;

import android.content.Context;
import android.content.Intent;

public class BookmarkTypes implements BookmarkHandler {
  public static final int STOP = 1;
  public static final int PLAN = 2;
  public static final int LOCATION = 3;
  public static final int SEQUENCE = 4;
  
  static class StopType implements BookmarkType {
    @Override
    public int getIcon() {
      return R.drawable.stop;
    }

    @Override
    public Intent createIntent(Context context, Bookmark bookmark) {
      RStop rstop = bookmark.getObject(RStop.class);
      Intent intent = new Intent(context, ScheduleActivity.class);
      new IntentHelper(intent).putRStop(rstop);
      return intent;
    }   
  }
  static class PlanType implements BookmarkType {
    @Override
    public int getIcon() {
      return R.drawable.plan;
    }

    @Override
    public Intent createIntent(Context context, Bookmark bookmark) {
      PlanEndpoints endpoints = bookmark.getObject(PlanEndpoints.class);
      Intent intent = new Intent(context, PlanTabsActivity.class);
      intent.putExtra(Keys.ENDPOINTS, endpoints);
      return intent;
    }   
  }
  static class SequenceType implements BookmarkType {
    @Override
    public int getIcon() {
      return R.drawable.sequence;
    }

    @Override
    public Intent createIntent(Context context, Bookmark bookmark) {
      Sequence sequence = bookmark.getObject(Sequence.class);
      Info.setSequence(context, sequence);
      Intent intent = new Intent(context, SequenceActivity.class);
      return intent;
    }   
  }
  static class LocationType implements BookmarkType {
    @Override
    public int getIcon() {
      return R.drawable.place;
    }

    @Override
    public Intent createIntent(Context context, Bookmark bookmark) {
      Point2D point = bookmark.getObject(Point2D.class);
      NamedPoint namedPoint = new NamedPoint(point, bookmark.getName());
      Intent intent = new Intent(context, PointSelectionActivity.class);
      intent.putExtra(PointSelectionActivity.POINT, namedPoint);
      return intent;
    }   

  }
  @Override
  public BookmarkType getBookmarkType(int type) {
    switch(type) {
    case LOCATION:
      return new LocationType();
    case STOP:
      return new StopType();
    case PLAN:
      return new PlanType();
    case SEQUENCE:
      return new SequenceType();
    default:
      return null;
    }
  }
}
