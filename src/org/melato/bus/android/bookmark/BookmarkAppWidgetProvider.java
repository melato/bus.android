package org.melato.bus.android.bookmark;

import org.melato.log.Log;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

public class BookmarkAppWidgetProvider extends AppWidgetProvider {

  public BookmarkAppWidgetProvider() {
    super();
    System.out.println(getClass().getName());
    Log.info(getClass().getName());
    Log.info("ab1245");
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
  }
}
