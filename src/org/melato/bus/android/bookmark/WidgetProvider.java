package org.melato.bus.android.bookmark;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;

public class WidgetProvider extends AppWidgetProvider {

  public WidgetProvider() {
    super();
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager,
      int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    WidgetUpdater updater = new WidgetUpdater(context);
    for(int widgetId: appWidgetIds) {
      updater.updateWidget(widgetId);
    }
  }

  @Override
  public void onDeleted(Context context, int[] appWidgetIds) {
    super.onDeleted(context, appWidgetIds);
    WidgetUpdater widgetUpdater = new WidgetUpdater(context);
    for(int widgetId: appWidgetIds) {
      widgetUpdater.removeWidget(widgetId);
    }
  }
}
