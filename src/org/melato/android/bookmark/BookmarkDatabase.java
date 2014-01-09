package org.melato.android.bookmark;

import java.io.IOException;
import java.util.Collection;

import org.melato.android.bookmark.BookmarkSchema.BookmarkColumns;
import org.melato.android.bookmark.BookmarkSchema.Bookmarks;
import org.melato.android.db.Column;
import org.melato.client.Bookmark;
import org.melato.client.BookmarkStorage;
import org.melato.client.Serialization;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Provides low-level SQLite access to the database.
 *  Operations:
 * */
public class BookmarkDatabase extends SQLiteOpenHelper implements BookmarkStorage {
  private static BookmarkDatabase instance;

  public static BookmarkDatabase getInstance(Context context) {
    if ( instance == null) {
      instance = new BookmarkDatabase(context.getApplicationContext(), "BOOKMARKS.db");
    }
    return instance;
  }
  
  
  private SQLiteDatabase getReadableDB() {
    return getReadableDatabase();
  }

  /**
   * 
   * @param context
   * @param filename The filename of the database, e.g. "BOOKMARKS.db"
   */
  public BookmarkDatabase(Context context, String filename) {
    super(context, filename, null, BookmarkSchema.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(Column.createStatement(Bookmarks.TABLE, Bookmarks.columns));
  }

  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }

  private long insertBookmark(SQLiteDatabase db, Bookmark bookmark) throws IOException {
    byte[] data = Serialization.toByteArray(bookmark.getObject());
    ContentValues args = new ContentValues();
    args.put(BookmarkColumns.TYPE, bookmark.getType());
    args.put(BookmarkColumns.NAME, bookmark.getName());
    args.put(BookmarkColumns.OBJECT, data);
    return db.insert(Bookmarks.TABLE, null, args);
  }

  public Bookmark addBookmark(Bookmark bookmark) {
    SQLiteDatabase db = getWritableDatabase();
    try {
      long id = insertBookmark(db, bookmark);
      return new SqlBookmark(this, id, bookmark.getType(), bookmark.getName(), bookmark.getObject());
    } catch(IOException e) {
      return null;
    } finally {
      db.close();
    }
  }
  private synchronized void deleteBookmark(SQLiteDatabase db, long id) {
    String where = Bookmarks._ID + " = ?";
    db.delete(Bookmarks.TABLE, where, new String[] { String.valueOf(id) });
  }
  
  public void deleteBookmark(Bookmark bookmark) {
    SQLiteDatabase db = getWritableDatabase();
    try {
      SqlBookmark b = (SqlBookmark)bookmark;
      deleteBookmark(db, b.id);
    } finally {
      db.close();
    }
  }
  
  public void renameBookmark(Bookmark bookmark, String name) {
    SqlBookmark b = (SqlBookmark)bookmark;
    SQLiteDatabase db = getWritableDatabase();
    try {
      ContentValues values = new ContentValues();
      values.put(BookmarkColumns.NAME, name);
      db.update(Bookmarks.TABLE,  values,  Bookmarks._ID + "=?", new String[] {String.valueOf(b.id)});
    } finally {
      db.close();
    }
  }
  
  private static final String[] BOOKMARK_COLUMNS =
    { Bookmarks.NAME, Bookmarks.TYPE, Bookmarks._ID};
  
  /** Read Bookmarks. */
  private void readBookmarks(Cursor cursor, Collection<Bookmark> collector) {
    try {
      if ( cursor.moveToFirst() ) {
        do {          
          String name = cursor.getString(0);
          int type = cursor.getInt(1);
          int id = cursor.getInt(2);
          collector.add(new SqlBookmark(this, id, type, name, null));
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
  }
  
  public void loadBookmarks(Collection<Bookmark> bookmarks) {
    SQLiteDatabase db = getReadableDB();
    try {
      Cursor cursor = db.query( Bookmarks.TABLE, BOOKMARK_COLUMNS, null, null, null, null, Bookmarks.NAME + " ASC");
      readBookmarks(cursor, bookmarks);
    } finally {
      db.close();
    }
  }
  
  Object loadObject(long id) {
    SQLiteDatabase db = getReadableDB();
    try {
      String[] columns = new String[] { Bookmarks.OBJECT };
      Cursor cursor = db.query( Bookmarks.TABLE, columns,
          Bookmarks._ID + "=?", new String[] {String.valueOf(id)},
          null, null, null);
      if ( cursor.moveToFirst()) {
        byte[] data = cursor.getBlob(0);
        return Serialization.read(Object.class, data);
      }
      return null;
    } finally {
      db.close();
    }
  }
  
}