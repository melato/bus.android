package org.melato.bus.android.track;

import org.melato.android.db.Column;

import android.provider.BaseColumns;

/** Specifies the database schema, except for the statistics table. */
public final class StopsSchema {
  /** The version of the database schema */
  static final int DATABASE_VERSION = 2;

  public static final class Stops extends StopColumns implements
      android.provider.BaseColumns {
    /** The name of this table */
    public static final String TABLE = "stops";

    public static final Column[] columns = {
      /** The timestamp of the record, in milliseconds. */
      new Column(TIMESTAMP, "INTEGER NOT NULL"),
      new Column(LAT, "FLOAT"),
      new Column(LON, "FLOAT"),
      new Column(SYMBOL, "TEXT NOT NULL"),
      new Column(FLAGS, "INTEGER NOT NULL"),
      new Column(STATUS, "INTEGER NOT NULL DEFAULT 0"),
      new Column(BaseColumns._ID, "INTEGER PRIMARY KEY AUTOINCREMENT")};      
  }

  public static class StopColumns {
    public static final String TIMESTAMP = "timestamp";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String SYMBOL = "symbol";
    public static final String FLAGS = "flags";
    public static final String STATUS = "status";
    /*
    public static final String SEAT = "seat";
    public static final String COVER = "cover";
    */
  }

}
