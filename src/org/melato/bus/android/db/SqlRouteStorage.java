package org.melato.bus.android.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.melato.bus.android.db.RoutesDatabase.Markers;
import org.melato.bus.android.db.RoutesDatabase.Routes;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteStorage;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Waypoint;
import org.melato.log.Log;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlRouteStorage implements RouteStorage {
  /**
    /data/data/org.melato.bus.android/databases/ROUTES.db
   */
  private String databaseFile;
  public SqlRouteStorage(Context context) {
    //Log.setLogger( new BusLogger(context) );
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
    cursor.close();
    db.close();
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
    float latDiff = Earth.latitudeForDistance(distance);
    float lonDiff = Earth.longitudeForDistance(distance, point.getLat());
    float lat1 = point.getLat() - latDiff;
    float lat2 = point.getLat() + latDiff;
    float lon1 = point.getLon() - lonDiff;
    float lon2 = point.getLon() + lonDiff;
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, markers.symbol, markers.name, routes.name, routes.direction, markers._id from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere lat > %f and lat < %f and lon > %f and lon < %f" +
        "\norder by markers._id";
    Cursor cursor = db.rawQuery(
        String.format( sql, lat1, lat2, lon1, lon2),
        null);
    long time = System.currentTimeMillis();
    if ( cursor.moveToFirst() ) {
      int lastMarkerId = -1;
      Waypoint p = null;
      do {
        int markerId = cursor.getInt(6);
        if ( markerId != lastMarkerId) {
          lastMarkerId = markerId;
          if ( p != null ) {
            collector.add(p);
            p = null;
          }
        }
        if ( p == null ) {
          p = new Waypoint(cursor.getFloat(0), cursor.getFloat(1));
          if ( Earth.distance(point,  p) > distance ) {
            p = null;
            continue;
          }          
          p.setSym(cursor.getString(2));
          p.setName(cursor.getString(3));
          p.setLinks( new ArrayList<String>() );
        }
        String routeName = cursor.getString(4);
        String direction = cursor.getString(5);
        p.getLinks().add( Route.qualifiedName(routeName, direction));
      } while ( cursor.moveToNext() );
      if ( p != null ) {
        collector.add(p);
      }
    }
    cursor.close();
    db.close();
    time = System.currentTimeMillis() - time;
    Log.info( "sql.nearby time=" + time );    
  }
}
