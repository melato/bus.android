package org.melato.bus.android.bookmark;

import org.melato.android.AndroidLogger;
import org.melato.android.bookmark.BookmarkHandler;
import org.melato.android.bookmark.BookmarkType;
import org.melato.android.bookmark.SqlBookmark;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.Keys;
import org.melato.client.Bookmark;
import org.melato.log.Log;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;


public class BookmarkAppWidgetConfigure extends BusBookmarksActivity {
  private int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
  private BookmarkHandler bookmarkHandler = new BookmarkTypes();
  @Override
  public void onCreate(Bundle savedInstanceState) {
    setHasContextMenu(false);
    super.onCreate(savedInstanceState);
    Log.setLogger(new AndroidLogger(this));
    Intent intent = getIntent();
    Bundle extras = intent.getExtras();
    if (extras != null) {
        widgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, 
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }
    setResult(RESULT_CANCELED);    
  }
  protected void open(Bookmark bookmark) {
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        
    Log.info("bookmark type=" + bookmark.getType());
    BookmarkType type = bookmarkHandler.getBookmarkType(bookmark.getType());
    if ( type != null) {
      RemoteViews views = new RemoteViews(this.getPackageName(), R.layout.bookmark_appwidget);
      views.setTextViewText(R.id.label, bookmark.getName());
      Intent intent = type.createIntent(this, bookmark);
      if ( intent != null) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FILL_IN_DATA);
        views.setOnClickPendingIntent(R.id.bookmarkButton, pendingIntent);
      }
      appWidgetManager.updateAppWidget(widgetId, views);
      setResult(RESULT_OK);
    }

    if ( bookmark instanceof SqlBookmark ) {
      long bookmarkId = ((SqlBookmark)bookmark).getId();
      Intent resultValue = new Intent();
      resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);    
      resultValue.putExtra(Keys.BOOKMARK, bookmarkId);
      setResult(RESULT_OK, resultValue);
    }
    finish();
  }
}
