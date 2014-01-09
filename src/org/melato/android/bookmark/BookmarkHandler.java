package org.melato.android.bookmark;

import org.melato.client.Bookmark;

import android.app.Activity;

public interface BookmarkHandler {
  int getTypeIcon(int type);
  void open(Activity activity, Bookmark bookmark);
}
