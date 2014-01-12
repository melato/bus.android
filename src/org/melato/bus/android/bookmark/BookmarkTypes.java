package org.melato.bus.android.bookmark;

import org.melato.android.bookmark.BookmarkHandler;
import org.melato.android.bookmark.BookmarkType;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.IntentHelper;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.android.activity.PlanTabsActivity;
import org.melato.bus.android.activity.ScheduleActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.plan.PlanEndpoints;
import org.melato.client.Bookmark;

import android.content.Context;
import android.content.Intent;

public class BookmarkTypes implements BookmarkHandler {
  public static final int STOP = 1;
  public static final int PLAN = 2;
  
  static class StopType implements BookmarkType {
    @Override
    public int getIcon() {
      return R.drawable.stops;
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
      return R.drawable.search;
    }

    @Override
    public Intent createIntent(Context context, Bookmark bookmark) {
      PlanEndpoints endpoints = bookmark.getObject(PlanEndpoints.class);
      Intent intent = new Intent(context, PlanTabsActivity.class);
      intent.putExtra(Keys.ENDPOINTS, endpoints);
      return intent;
    }   
  }
  @Override
  public BookmarkType getBookmarkType(int type) {
    switch(type) {
    case STOP:
      return new StopType();
    case PLAN:
      return new PlanType();
    default:
      return null;
    }
  }
}
