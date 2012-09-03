package org.melato.bus.android.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteStorage;
import org.melato.bus.model.Schedule;
import org.melato.gpx.Earth;
import org.melato.gpx.GPX;
import org.melato.gpx.Point;
import org.melato.gpx.Sequence;
import org.melato.gpx.Waypoint;
import org.melato.log.Log;
import org.melato.util.IntArrays;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlRouteStorage implements RouteStorage {
  static final String DATABASE_NAME = "ROUTES.db";
  private String databaseFile;
  public SqlRouteStorage(Context context) {
    databaseFile = context.getDatabasePath(DATABASE_NAME).toString();
  }
  public SqlRouteStorage(File databaseFile) {
    this.databaseFile = databaseFile.toString();
  }

  SQLiteDatabase getDatabase() {
    return SQLiteDatabase.openDatabase(databaseFile,
        null, SQLiteDatabase.OPEN_READONLY);
  }

  @Override
  public List<Route> loadRoutes() {
    List<Route> routes = new ArrayList<Route>();
    SQLiteDatabase db = getDatabase();
    String sql = "select name, label, title, direction, _id from routes order by _id";
    Cursor cursor = db.rawQuery(sql, null);
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

  private int loadBasic(SQLiteDatabase db, Route route) {    
    String sql = "select _id, name, label, title from routes where name = '%s' and direction = '%s';";
    Cursor cursor = db.rawQuery( String.format(sql, quote(route.getName()), quote(route.getDirection())), null);
    try {
      if ( cursor.moveToFirst() ) {
        route.setTitle(cursor.getString(3));
        return cursor.getInt(0);
      }
      return -1;
    } finally {
      cursor.close();
    }
  }
  
  private Schedule loadSchedule(SQLiteDatabase db, int routeId) {
    String sql = "select days, minutes from schedule_times" +
        "\njoin schedules on schedules._id = schedule_times.schedule" +
        "\njoin routes on routes._id = schedules.route" +
        "\nwhere routes._id = %d" +
        "\norder by days";
    Cursor cursor = db.rawQuery( String.format(sql, routeId), null);
    List<DaySchedule> daySchedules = new ArrayList<DaySchedule>(); 
    try {
      if ( cursor.moveToFirst() ) {
        int lastDays = 0;
        List<Integer> times = new ArrayList<Integer>();
        do {
          int days = cursor.getInt(0);
          int minutes = cursor.getInt(1);
          if ( days != lastDays ) {
            if ( ! times.isEmpty() ) {
              daySchedules.add( new DaySchedule(IntArrays.toArray(times), lastDays));
              times.clear();
              lastDays = days;
            }
          }
          times.add(minutes);
        } while ( cursor.moveToNext() );
        if ( ! times.isEmpty() ) {
          daySchedules.add( new DaySchedule(IntArrays.toArray(times), lastDays));
        }
      }
      return new Schedule(daySchedules.toArray(new DaySchedule[0]));
    } finally {
      cursor.close();
    }    
  }
  @Override
  public Route loadRoute(String qualifiedName) {
    String[] fields = qualifiedName.split("-");
    Route route = new Route();
    route.setName(fields[0]);
    route.setDirection(fields[1]);
    SQLiteDatabase db = getDatabase();
    try {
      int routeId = loadBasic(db, route);
      Schedule schedule = loadSchedule(db, routeId);
      route.setSchedule(schedule);
      return route;
    } finally {
      db.close();
    }
  }

  
  protected String quote(String s) {
    if ( s.indexOf('\'') < 0 )
      return s;
    return s.replaceAll( "'", "''" );
  }
  
  @Override
  public GPX loadGPX(String qualifiedName) {    
    String[] fields = qualifiedName.split("-");
    String routeName = fields[0];
    String direction = fields[1];
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, markers.symbol, markers.name, stops.seq from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere routes.name = '%s' and routes.direction = '%s'" +
        "\norder by stops.seq";
    Cursor cursor = db.rawQuery( String.format(sql, quote(routeName), quote(direction)), null);
    //Cursor cursor = db.rawQuery( sql, new String[] { routeName, direction });
    try {
      List<Waypoint> waypoints = new ArrayList<Waypoint>();
      if ( cursor.moveToFirst() ) {
        do {
          Waypoint p = new Waypoint(cursor.getFloat(0), cursor.getFloat(1));
          p.setSym(cursor.getString(2));
          p.setName(cursor.getString(3));
          p.setLinks( Arrays.asList( new String[] { qualifiedName }));
          waypoints.add(p);
        } while ( cursor.moveToNext() );
      }
      GPX gpx = new GPX();
      org.melato.gpx.Route rte = new org.melato.gpx.Route();
      rte.path = new Sequence(waypoints);
      gpx.getRoutes().add(rte);
      return gpx;      
    } finally {
      cursor.close();
      db.close();
    }
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
