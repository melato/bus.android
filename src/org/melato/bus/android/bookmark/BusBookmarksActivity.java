package org.melato.bus.android.bookmark;

import org.melato.android.app.HelpActivity;
import org.melato.android.bookmark.BookmarksActivity;
import org.melato.bus.android.activity.Help;

import android.view.Menu;

public class BusBookmarksActivity extends BookmarksActivity {

  public BusBookmarksActivity() {
    super(new BookmarkTypes());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     HelpActivity.addItem(menu, this, Help.BOOKMARKS);
     return true;
  }
}
