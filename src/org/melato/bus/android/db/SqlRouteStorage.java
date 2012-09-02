package org.melato.bus.android.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.melato.bus.android.db.RoutesDatabase.Markers;
import org.melato.bus.android.db.RoutesDatabase.Routes;
import org.melato.bus.android.db.RoutesDatabase.Stops;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteStorage;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlRouteStorage extends SQLiteOpenHelper implements RouteStorage {
  public SqlRouteStorage(Context context) {
    super(context, RoutesDatabase.DATABASE_NAME, null,
        RoutesDatabase.DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(Routes.CREATE_STATEMENT);
    db.execSQL(Markers.CREATE_STATEMENT);
    db.execSQL(Stops.CREATE_STATEMENT);
  }

  SQLiteDatabase getDatabase() {
    //return getReadableDatabase();
    return SQLiteDatabase.openDatabase("/data/data/org.melato.bus.android/databases/ROUTES.db",
        null, SQLiteDatabase.OPEN_READONLY);
  }
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
  }

  @Override
  public List<Route> loadRoutes() {
    List<Route> routes = new ArrayList<Route>();
    SQLiteDatabase db = getDatabase();
    Cursor cursor = db.query(Routes.TABLE, new String[] {Routes.NAME, Routes.LABEL, Routes.TITLE, Routes.DIRECTION},
        null, null, null, null, null);
    if ( cursor.moveToFirst() ) {
      do {
        Route route = new Route();
        route.setName(cursor.getString(0));
        route.setLabel(cursor.getString(1));
        route.setTitle(cursor.getString(2));
        route.setDirection(cursor.getString(3));
        routes.add(route);
      } while( cursor.moveToNext() );
    }
    return routes;
  }

  @Override
  public Route loadRoute(String qualifiedName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public GPX loadGPX(String qualifiedName) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void iterateAllStops(Collection<Waypoint> collector) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void iterateNearbyStops(Point point, float distance,
      Collection<Waypoint> collector) {
    throw new UnsupportedOperationException();
  }
  
  
}
