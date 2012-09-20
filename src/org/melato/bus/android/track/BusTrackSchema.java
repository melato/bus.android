package org.melato.bus.android.track;

import org.melato.android.db.Column;

import android.net.Uri;
import android.provider.BaseColumns;

public final class BusTrackSchema {
  public static final String AUTHORITY = "org.melato.android.bus.track";
  public static final Uri CONTENT_URI = Uri.parse("content://"
      + BusTrackSchema.AUTHORITY);
  static final String DATABASE_NAME = "BUSTRACK.db";
  /** The version of the database schema */
  static final int DATABASE_VERSION = 1;
  
  public static Column ID = new Column( BaseColumns._ID, "INTEGER PRIMARY KEY AUTOINCREMENT");

  public static final class Passes extends PassColumns implements
      android.provider.BaseColumns {
    /** The name of this table */
    public static final String TABLE = "passes";
    
    public static final Column[] columns = {
      new Column(ROUTE, "TEXT"),
      new Column(DIRECTION, "TEXT"),
      new Column(MARKER, "TEXT"),
      new Column(TIMESTAMP, "INTEGER"),
      new Column(DAY_OF_WEEK, "INTEGER"),
      new Column(TIME_OF_DAY, "INTEGER"),
      new Column(PASS, "INTEGER"),
      ID
    };
        
  }

  public static class PassColumns {
    /** route id (without direction) */
    public static final String ROUTE = "route";
    /** route direction */
    public static final String DIRECTION = "direction";
    /** marker (stop) symbol */
    public static final String MARKER = "marker";
    /** unix timestamp (seconds since 1970) */
    public static final String TIMESTAMP = "pass_time";
    /** day of the week (1 = SUNDAY) */
    public static final String DAY_OF_WEEK = "day";
    /** time of day in seconds since midnight. */
    public static final String TIME_OF_DAY = "day_time";
    /** type of entry.  0 = manual 1 = automatic */
    public static final String PASS = "pass_type";
  }
}
