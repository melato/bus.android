package org.melato.bus.android.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.melato.bus.android.db.RoutesDatabase.Routes;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteStorage;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlRouteStorage implements RouteStorage {
  /**
    /data/data/org.melato.bus.android/databases/ROUTES.db
   */
  private String databaseFile;
  public SqlRouteStorage(Context context) {
    // I don't know how to get the databases directory officially, so we'll figure it out.
    File dir = context.getFilesDir();
    dir = dir.getParentFile();
    databaseFile = new File(dir, "databases/" + RoutesDatabase.DATABASE_NAME).toString();
  }

  SQLiteDatabase getDatabase() {
    return SQLiteDatabase.openDatabase(databaseFile,
        null, SQLiteDatabase.OPEN_READONLY);
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
