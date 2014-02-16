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
    // The result is needed by Android 4.0.3 (api 15), and maybe others.
    // It is not needed with api 18.
    result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
    setResult(RESULT_OK, result);
    finish();
  }
}
