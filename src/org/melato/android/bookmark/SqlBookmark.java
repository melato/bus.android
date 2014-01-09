package org.melato.android.bookmark;

import org.melato.client.Bookmark;

public class SqlBookmark extends Bookmark {
  private BookmarkDatabase db;
  long id;

  SqlBookmark(BookmarkDatabase db, long id, int type, String name, Object object) {
    super(type, name, object);
    this.db = db;
    this.id = id;
  }
  @Override
  public Object getObject() {
    if ( object == null) {
      object = db.loadObject(id);
    }
    
    return super.getObject();
  }
  
}
