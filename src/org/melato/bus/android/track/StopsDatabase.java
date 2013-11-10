package org.melato.bus.android.track;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.android.db.Column;
import org.melato.bus.android.track.StopsSchema.StopColumns;
import org.melato.bus.android.track.StopsSchema.Stops;
import org.melato.bus.transit.StopDetails;
import org.melato.gps.Point2D;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** Provides low-level SQLite access to the database.
 *  Operations:
 *      load all stop flags
 *      load all stop details
 *      load one stop
 *      insert or update one stop
 * */
public class StopsDatabase extends SQLiteOpenHelper {
  private static StopsDatabase instance;
  /** All stop flags from the database, cached in memory for easy access in the stops activity. */
  private Map<String,Integer> allFlags;

  
  public static StopsDatabase getInstance(Context context) {
    if ( instance == null) {
      instance = new StopsDatabase(context.getApplicationContext(), "STOPS.db");
    }
    return instance;
  }
  
  
  private SQLiteDatabase getReadableDB() {
    return getReadableDatabase();
  }

  /**
   * 
   * @param context
   * @param filename The filename of the database, e.g. "STOPS.db"
   */
  public StopsDatabase(Context context, String filename) {
    super(context, filename, null, StopsSchema.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(Column.createStatement(Stops.TABLE, Stops.columns));
  }

  
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }

  public void vacuum() {
    new Thread() {
      @Override
      public void run() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("VACUUM");
      }
    }.start();

  }

  private long insertStop(SQLiteDatabase db, StopDetails stop) {
    ContentValues args = new ContentValues();
    args.put(StopColumns.TIMESTAMP, stop.getDate().getTime());
    args.put(StopColumns.SYMBOL, stop.getSymbol());
    args.put(StopColumns.FLAGS, StopFlags.getFlags(stop));
    Point2D point = stop.getLocation();
    if ( point != null ) {
      args.put(StopColumns.LAT, (float) point.getLat());
      args.put(StopColumns.LON, (float) point.getLon());
    }
    return db.insert(Stops.TABLE, null, args);
  }

  private synchronized void deleteStop(SQLiteDatabase db, String symbol) {
    String where = Stops.SYMBOL + " = '" + symbol + "'";
    db.delete(Stops.TABLE, where, null);
  }
  
  public void updateStop(StopDetails stop) {
    SQLiteDatabase db = getWritableDatabase();
    try {
      deleteStop(db, stop.getSymbol());
      insertStop(db, stop);
      if ( allFlags != null) {
        allFlags.put(stop.getSymbol(),  StopFlags.getFlags(stop));
      }
    } finally {
      db.close();
    }
  }

  private static final String[] STOPS_COLUMNS =
    { Stops.SYMBOL, Stops.TIMESTAMP, Stops.FLAGS, Stops.LAT, Stops.LON, Stops._ID};
  
  /** Read stops. */
  private void readStops(Cursor cursor, Collection<StopDetails> collector) {
    try {
      if ( cursor.moveToFirst() ) {
        do {
          StopDetails stop = new StopDetails();
          int i = 0;
          stop.setSymbol(cursor.getString(i++));
          stop.setDate(new Date(cursor.getLong(i++)));
          int flags = cursor.getInt(i++);
          stop.setSeat(StopFlags.hasSeat(flags));
          stop.setCover(StopFlags.hasCover(flags));
          Float lat = cursor.isNull(i) ? null : cursor.getFloat(i);
          i++;
          Float lon = cursor.isNull(i) ? null : cursor.getFloat(i);
          i++;          
          if ( lat != null && lon != null) {
            stop.setLocation(new Point2D(lat, lon));            
          }
          stop.setDate(new Date( cursor.getLong(i++)));
          if ( ! collector.add(stop) )
            break;
        } while (cursor.moveToNext());
      }
    } finally {
      cursor.close();
    }
  }
  
  /** Load stops
   * 
   * @param selection
   * @param collector
   */
  private void loadStops(String selection, Collection<StopDetails> collector) {
    SQLiteDatabase db = getReadableDB();
    try {
      Cursor cursor = db.query( Stops.TABLE, STOPS_COLUMNS, selection, null, null, null, Stops._ID + " ASC");
      readStops(cursor, collector);
    } finally {
      db.close();
    }
  }
  
  public void loadAllStops(Collection<StopDetails> collector) {
    loadStops(null, collector);
  }
  
  public StopDetails loadStop(String symbol) {
    List<StopDetails> list = new ArrayList<StopDetails>();
    loadStops(Stops.SYMBOL + "=" + symbol, list);
    return list.isEmpty() ? null : list.get(list.size()-1);
  }
  
  /** Load all stop flags
   * 
   * @param selection
   * @param collector
   * @return map of symbol -> flags
   */
  public Map<String,Integer> loadFlags() {
    SQLiteDatabase db = getReadableDB();
    try {
      String[] columns = new String[] 
          { Stops.SYMBOL, Stops.FLAGS, Stops._ID};
      Cursor cursor = db.query( Stops.TABLE, columns, null, null, null, null, Stops._ID + " ASC");
      Map<String,Integer> map = new HashMap<String,Integer>();
      try {
        if ( cursor.moveToFirst() ) {
          do {
            String symbol = cursor.getString(0);
            int flags = cursor.getInt(1);
            map.put(symbol,flags);
          } while (cursor.moveToNext());
        }
      } finally {
        cursor.close();
      }
      return map;
    } finally {
      db.close();
    }
  }
  
  public Integer getFlags(String symbol) {
    if ( allFlags == null) {
      allFlags = loadFlags();
    }
    return allFlags.get(symbol);
  }
  
  public synchronized void deleteAll() {
    SQLiteDatabase db = getWritableDatabase();
    try {
      db.delete(Stops.TABLE, null, null);
      allFlags.clear();
    } finally {
      db.close();
    }
  }
}