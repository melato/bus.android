package org.melato.bus.android.bookmark;

import org.melato.android.bookmark.BookmarkHandler;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.BusActivities;
import org.melato.bus.android.activity.ScheduleActivity;
import org.melato.bus.model.RStop;
import org.melato.client.Bookmark;

import android.app.Activity;

public class BookmarkTypes implements BookmarkHandler {
  public static final int STOP = 1;
  public static final int PLAN = 2;
  
  @Override
  public int getTypeIcon(int type) {
    switch(type) {
    case STOP:
      return R.drawable.stops;
    case PLAN:
      return R.drawable.search;
    default:
      return R.drawable.bookmark;
    }
  }
  void gotoStop(Activity activity, Bookmark bookmark) {
    RStop rstop = (RStop)bookmark.getObject(RStop.class);
    if ( rstop != null ) {
      BusActivities activities = new BusActivities(activity);
      activities.showRoute(rstop, ScheduleActivity.class);
    }
  }
  @Override
  public void open(Activity activity, Bookmark bookmark) {
    switch(bookmark.getType()) {
    case STOP:
      gotoStop(activity, bookmark);
      break;
    case PLAN:
      break;
    default:
      break;
    }
  }
}
