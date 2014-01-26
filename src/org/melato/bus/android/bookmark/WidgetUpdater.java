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
package org.melato.bus.android.bookmark;

import org.melato.android.bookmark.BookmarkDatabase;
import org.melato.android.bookmark.BookmarkHandler;
import org.melato.android.bookmark.BookmarkType;
import org.melato.android.bookmark.SqlBookmark;
import org.melato.bus.android.R;
import org.melato.client.Bookmark;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.RemoteViews;

/** Maps widgets to bookmarks, via preferences. */
public class WidgetUpdater {
  public static final String PREFS = "widgets";
  private Context context;
  private BookmarkHandler bookmarkHandler = new BookmarkTypes();
  
  public WidgetUpdater(Context context) {
    super();
    this.context = context;
  }

  private String key(int widgetId) {
    return "widget." + widgetId;
  }
  public void addWidget(int widgetId, SqlBookmark bookmark) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS, 0);
    Editor editor = prefs.edit();
    editor.putLong(key(widgetId), bookmark.getId());
    editor.apply();
  }
  public void removeWidget(int widgetId) {
    SharedPreferences prefs = context.getSharedPreferences(PREFS, 0);
    Editor editor = prefs.edit();
    editor.remove(key(widgetId));
    editor.apply();
  }
  public Bookmark getBookmarkForWidget(int widgetId) {    
    SharedPreferences prefs = context.getSharedPreferences(PREFS, 0);
    long id = prefs.getLong(key(widgetId), -1);
    if ( id > 0 ) {
      return BookmarkDatabase.getInstance(context).loadBookmark(id);
    }
    return null;
  }
  
  public void updateWidget(int widgetId, Bookmark bookmark) {
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    BookmarkType type = bookmarkHandler.getBookmarkType(bookmark.getType());
    if ( type != null) {
      RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bookmark_appwidget);
      views.setImageViewResource(R.id.bookmarkButton, type.getIcon());
      views.setTextViewText(R.id.label, bookmark.getName());
      Intent intent = type.createIntent(context, bookmark);
      if ( intent != null) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, Intent.FILL_IN_DATA);
        views.setOnClickPendingIntent(R.id.bookmarkButton, pendingIntent);
      }
      appWidgetManager.updateAppWidget(widgetId, views);    
    }
  }
  public void updateWidget(int widgetId) {
    Bookmark bookmark = getBookmarkForWidget(widgetId);
    if (bookmark != null) {
      updateWidget(widgetId, bookmark);
    }
  }
}
