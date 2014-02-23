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

import org.melato.android.bookmark.BookmarksActivity;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.NamedPoint;
import org.melato.client.Bookmark;
import org.melato.gps.Point2D;

import android.content.Intent;

public class LocationBookmarkActivity extends BookmarksActivity {
  public static final String KEY_LOCATION = "location";

  public LocationBookmarkActivity() {
    super(new BookmarkTypes());
    setHasContextMenu(false);
    setVisibleTypes(new int[] {BookmarkTypes.STOP, BookmarkTypes.LOCATION});
  }

  void setResult(Point2D point, Bookmark bookmark) {
    NamedPoint p = new NamedPoint(point, bookmark.getName());
    Intent data = new Intent();
    data.putExtra(KEY_LOCATION, p);
    setResult(RESULT_OK, data);
    finish();
  }
  @Override
  protected void open(Bookmark bookmark) {
    Object object = bookmark.getObject();
    if ( object instanceof RStop) {
      RStop rstop = (RStop) object;
      Stop stop = rstop.getStop();
      setResult(stop, bookmark);
    } else if ( object instanceof Point2D) {
      setResult((Point2D) object, bookmark);
    }
  }
}
