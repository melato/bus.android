package org.melato.bus.android.track;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.melato.bus.android.track.BusTrackSchema.PassColumns;
import org.melato.bus.android.track.BusTrackSchema.Passes;
import org.melato.bus.model.RouteId;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BusTrackHelper extends SQLiteOpenHelper {
  public BusTrackHelper(Context context) {
    super(context, BusTrackSchema.DATABASE_NAME, null,
        BusTrackSchema.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(Column.createStatement( Passes.TABLE, Passes.columns ));
  }

  public void vacuum() {
    new Thread() {
      @Override
      public void run() {
        SQLiteDatabase sqldb = getWritableDatabase();
        sqldb.execSQL("VACUUM");
      }
    }.start();

  }

  public long insertPass(Pass pass)
   {
      SQLiteDatabase sqldb = getWritableDatabase();
      ContentValues args = new ContentValues();
      RouteId routeId = pass.getRouteId();
      Calendar cal = new GregorianCalendar();
      cal.setTime(pass.getDate());
      int day = cal.get(Calendar.DAY_OF_WEEK);
      int hour = cal.get(Calendar.HOUR_OF_DAY);
      int minute = cal.get(Calendar.MINUTE);
      int second = cal.get(Calendar.SECOND);
      int seconds = hour * 3600 + minute * 60 + second;

      args.put(PassColumns.ROUTE, routeId.getName());
      args.put(PassColumns.DIRECTION, routeId.getDirection());
      args.put(PassColumns.MARKER, pass.getMarker());
      args.put(PassColumns.TIMESTAMP, (int) (pass.getDate().getTime()/1000));
      args.put(PassColumns.PASS, pass.getType());
      args.put(PassColumns.DAY_OF_WEEK, day );
      args.put(PassColumns.TIME_OF_DAY, seconds );
      long passId = sqldb.insert(Passes.TABLE, null, args);
      return passId;
   }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }
}
