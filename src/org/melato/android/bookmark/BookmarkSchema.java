package org.melato.android.bookmark;

import org.melato.android.db.Column;

import android.provider.BaseColumns;

/** Specifies the database schema, except for the statistics table. */
public final class BookmarkSchema {
  /** The version of the database schema */
  static final int DATABASE_VERSION = 1;

  public static final class Bookmarks extends BookmarkColumns implements
      android.provider.BaseColumns {
    /** The name of this table */
    public static final String TABLE = "bookmarks";

    public static final Column[] columns = {
      /** The timestamp of the record, in milliseconds. */
      new Column(TYPE, "INTEGER NOT NULL"),
      new Column(NAME, "TEXT NOT NULL"),
      new Column(OBJECT, "BLOB NOT NULL"),
      new Column(BaseColumns._ID, "INTEGER PRIMARY KEY AUTOINCREMENT")};      
  }

  public static class BookmarkColumns {
    public static final String TYPE = "bookmark_type";
    public static final String NAME = "name";
    public static final String OBJECT = "obj";
  }

}
