package org.melato.bus.android.bookmark;

import org.melato.android.bookmark.SqlBookmark;
import org.melato.client.Bookmark;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;


public class WidgetConfigurationActivity extends BusBookmarksActivity {
  private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  @Override
  public void onCreate(Bundle savedInstanceState) {
    setHasContextMenu(false);
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
        widgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, 
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    //setResult(RESULT_CANCELED);    
  }
  protected void open(Bookmark bookmark) {
    if ( ! (bookmark instanceof SqlBookmark) ) {
      return;      
    }
    WidgetUpdater widgetManager = new WidgetUpdater(this);
    widgetManager.addWidget(widgetId, (SqlBookmark)bookmark);
    widgetManager.updateWidget(widgetId, bookmark);      
  
    Intent result = new Intent();
    // The following is needed by Android 4.0.3.
    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
    setResult(RESULT_OK, result);
    finish();
  }
}
