package org.melato.bus.android.db;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.melato.bus.model.DaySchedule;
import org.melato.bus.model.MarkerInfo;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteStopCallback;
import org.melato.bus.model.RouteStorage;
import org.melato.bus.model.Schedule;
import org.melato.gps.Point;
import org.melato.gpx.Waypoint;
import org.melato.log.Clock;
import org.melato.log.Log;
import org.melato.util.IntArrays;
import org.melato.util.VariableSubstitution;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SqlRouteStorage implements RouteStorage {
  public static final String DATABASE_NAME = "ROUTES.db";
  private String databaseFile;
  
  
  public String getProperty( String name) {
    SQLiteDatabase db = getDatabase();
    String sql = "select value from properties where name = '%s'";
    Cursor cursor = db.rawQuery(String.format(Locale.US, sql, quote(name)), null);
    try {
      if ( cursor.moveToFirst() ) {
        return cursor.getString(0);
      }
      return null;
    } finally {
      cursor.close();
    }
  }
  
  @Override
  public String getUri(RouteId routeId) {
    String urlTemplate = getProperty( "route_url");
    if ( urlTemplate != null ) {
      VariableSubstitution sub = new VariableSubstitution(VariableSubstitution.ANT_PATTERN);
      Map<String,String> vars = new HashMap<String,String>();
      vars.put( "name", routeId.getName());
      vars.put( "direction", routeId.getDirection());
      return sub.substitute(urlTemplate, vars);
    }
    return null;
  }
  public static File databaseFile(Context context) {
    File dir = context.getFilesDir();
    return new File(dir, DATABASE_NAME);    
  }
  public SqlRouteStorage(Context context) {
    databaseFile = databaseFile(context).toString();
  }
  public SqlRouteStorage(File databaseFile) {
    this.databaseFile = databaseFile.toString();
  }

  SQLiteDatabase getDatabase() {
    return SQLiteDatabase.openDatabase(databaseFile,
        null, SQLiteDatabase.OPEN_READONLY);
  }

  static private final String ROUTE_SELECT = "select routes.name, routes.label, routes.title, routes.direction," +
      " routes.color, routes.background_color," +
      " is_primary," +
      " routes._id from routes";
  
  private List<Route> loadRoutes(String where) {
    List<Route> routes = new ArrayList<Route>();
    SQLiteDatabase db = getDatabase();
    String sql = ROUTE_SELECT;
    if ( where != null ) {
      sql += " where " + where;
    }
    sql += " order by _id";
    Cursor cursor = db.rawQuery(sql, null);
    if ( cursor.moveToFirst() ) {
      do {
        routes.add(readBasic(cursor));
      } while( cursor.moveToNext() );
    }
    cursor.close();
    db.close();
    return routes;
  }

  @Override
  public List<Route> loadRoutes() {
    return loadRoutes(null);
  }

  @Override
  public List<Route> loadPrimaryRoutes() {
    return loadRoutes("routes.is_primary = 1");
  }

  private Route readBasic(Cursor cursor) {
    Route route = new Route();
    RouteId routeId = new RouteId(cursor.getString(0), cursor.getString(3));
    route.setRouteId(routeId);
    route.setLabel(cursor.getString(1));
    route.setTitle(cursor.getString(2));
    route.setColor(cursor.getInt(4));
    route.setBackgroundColor(cursor.getInt(5));
    Log.info( "color=" + route.getColor());
    if ( ! cursor.isNull(6)) {
      int primary = cursor.getInt(6);
      if ( primary == 1 )
        route.setPrimary(true);      
    }
    return route;
  }

  private Route loadBasic(Cursor cursor) {
    try {
      if ( cursor.moveToFirst() ) {
        return readBasic(cursor);
      }
      return null;
    } finally {
      cursor.close();
    }
  }
  
  private Route loadBasic(SQLiteDatabase db, RouteId routeId) {    
    String sql = ROUTE_SELECT + " where " + whereClause(routeId);
    Cursor cursor = db.rawQuery( sql, null);
    return loadBasic(cursor);
  }  
  
  private Schedule loadSchedule(SQLiteDatabase db, RouteId routeId) {
    String sql = "select days, minutes from schedule_times" +
        "\njoin schedules on schedules._id = schedule_times.schedule" +
        "\njoin routes on routes._id = schedules.route" +
        "\nwhere " + whereClause(routeId) +
        "\norder by days, minutes";
    Log.info( "loadSchedule sql=" + sql );
    Cursor cursor = db.rawQuery( sql, null);
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
            }
            lastDays = days;
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

  public Schedule loadSchedule(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    try {
      Schedule schedule = loadSchedule(db, routeId);
      schedule.setComment(loadScheduleComment(db, routeId));
      return schedule;
    } finally {
      db.close();
    }
  }
  
  @Override
  public Route loadRoute(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    try {
      Route route = loadBasic(db, routeId);
      return route;
    } finally {
      db.close();
    }
  }
  
  public String loadScheduleComment(SQLiteDatabase db, RouteId routeId) {
    String sql = "select schedule_comment from routes where " + whereClause(routeId);
    Cursor cursor = db.rawQuery(sql,  null);
    try {
      if ( cursor.moveToFirst()) {
        return cursor.getString(0);
      }
      return null;
    } finally {
      cursor.close();
    }
  }
  
  protected String quote(String s) {
    if ( s.indexOf('\'') < 0 )
      return s;
    return s.replaceAll( "'", "''" );
  }
  
  private String format(String sql, RouteId routeId ) {
    return String.format(Locale.US, sql, quote(routeId.getName()), quote(routeId.getDirection()));
  }
  private String whereClause(RouteId routeId) {
    return format("routes.name = '%s' and routes.direction = '%s'", routeId);    
  }
  
  /** use for benchmarking */
  public void iterateWaypoints(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, stops.seq from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere " + whereClause(routeId) + 
        "\norder by stops.seq";
    Cursor cursor = db.rawQuery( sql, null);
    try {      
      if ( cursor.moveToFirst() ) {
        do {
          cursor.getFloat(0);
          cursor.getFloat(1);
        } while ( cursor.moveToNext() );
      }
    } finally {
      cursor.close();
      db.close();
    }
  }

  @Override
  public List<Waypoint> loadWaypoints(RouteId routeId) {
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, markers.symbol, markers.name, stops.duration from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere " + whereClause(routeId) + 
        "\norder by stops._id";
    Log.info(sql );
    Cursor cursor = db.rawQuery( sql, null);
    try {
      List<Waypoint> waypoints = new ArrayList<Waypoint>();
      if ( cursor.moveToFirst() ) {
        do {
          Waypoint p = new Waypoint(cursor.getFloat(0), cursor.getFloat(1));
          p.setSym(cursor.getString(2));
          p.setName(cursor.getString(3));
          p.setTime(1000L * cursor.getInt(4));
          p.setLinks( Arrays.asList( new String[] { routeId.toString() }));
          waypoints.add(p);
        } while ( cursor.moveToNext() );
      }
      Log.info("loadWaypoints: " + waypoints.size());
      return waypoints;
    } finally {
      cursor.close();
      db.close();
    }
  }

  @Override
  public void iterateAllRouteStops(RouteStopCallback callback) {
    Clock clock = new Clock();
    SQLiteDatabase db = getDatabase();
    String sql = "select lat, lon, routes._id, routes.name, routes.direction from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\norder by routes._id, stops.seq";
    Cursor cursor = db.rawQuery( sql, null);
    try {
      int last_route_id = -1;
      RouteId routeId = null;
      List<Point> waypoints = null;
      if ( cursor.moveToFirst() ) {
        Log.info( clock.lap( "all.RouteStops.moveToFirst"));
        do {
          Point p = new Point(cursor.getFloat(0), cursor.getFloat(1));
          int route_id = cursor.getInt(2);
          if ( route_id != last_route_id ) {
            if ( routeId != null) {
              callback.add(routeId, waypoints );
            }
            last_route_id = route_id;
            routeId = new RouteId(cursor.getString(3), cursor.getString(4));
            waypoints = new ArrayList<Point>();
          }
          waypoints.add(p);
        } while ( cursor.moveToNext() );
        if ( routeId != null) {
          callback.add(routeId, waypoints );
        }
      }
      Log.info( clock.lap( "all.RouteStops.cursor"));
    } finally {
      cursor.close();
      db.close();
    }
  }

  @Override
  public void iterateNearbyStops(Point point, float latDiff, float lonDiff,
      Collection<Waypoint> collector) {
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
        String.format( Locale.US, sql, lat1, lat2, lon1, lon2),
        null);
    Clock clock = new Clock("sql.iterateNearbyStops");
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
          // can check the filter here.
          /*
          if ( Earth.distance(point,  p) > distance ) {
            p = null;
            continue;
          } 
          */         
          p.setSym(cursor.getString(2));
          p.setName(cursor.getString(3));
          p.setLinks( new ArrayList<String>() );
        }
        String routeName = cursor.getString(4);
        String direction = cursor.getString(5);
        p.getLinks().add( new RouteId(routeName, direction).toString());
      } while ( cursor.moveToNext() );
      if ( p != null ) {
        collector.add(p);
      }
    }
    cursor.close();
    db.close();
    Log.info(clock);    
  }

  @Override
  public void iterateNearbyRoutes(Point point, float latDiff, float lonDiff,
      Collection<RouteId> collector) {
    float lat1 = point.getLat() - latDiff;
    float lat2 = point.getLat() + latDiff;
    float lon1 = point.getLon() - lonDiff;
    float lon2 = point.getLon() + lonDiff;
    SQLiteDatabase db = getDatabase();
    String sql = "select distinct routes.name, routes.direction from markers" +
        "\njoin stops on markers._id = stops.marker" +
        "\njoin routes on routes._id = stops.route" +
        "\nwhere lat > %f and lat < %f and lon > %f and lon < %f";
    Cursor cursor = db.rawQuery(
        String.format( Locale.US, sql, lat1, lat2, lon1, lon2),
        null);
    Clock clock = new Clock("sql.iterateNearbyRoutes");
    if ( cursor.moveToFirst() ) {
      do {
        RouteId routeId = new RouteId( cursor.getString(0), cursor.getString(1));
        collector.add(routeId);
      } while ( cursor.moveToNext() );
    }
    cursor.close();
    db.close();
    Log.info(clock);    
  }

  private Waypoint loadWaypoint(SQLiteDatabase db, String symbol) {
    String sql = "select lat, lon, symbol, name, _id from markers" +
        "\nwhere symbol = '%s'";
    Cursor cursor = db.rawQuery(
        String.format(Locale.US, sql, quote(symbol)), null);
    try {
      if ( cursor.moveToFirst() ) {
        Waypoint p = new Waypoint(cursor.getFloat(0), cursor.getFloat(1));
        p.setSym(cursor.getString(2));
        p.setName(cursor.getString(3));
        return p;
      }
      return null;
    } finally {
      cursor.close();
    }
  }
  
  private List<Route> loadRoutesForMarker(SQLiteDatabase db, String symbol) {    
    List<Route> routes = new ArrayList<Route>();
    String sql = ROUTE_SELECT +
        "\njoin stops on routes._id = stops.route" +
        "\njoin markers on markers._id = stops.marker" +
        "\nwhere markers.symbol = '%s'";
    Cursor cursor = db.rawQuery(
        String.format( Locale.US, sql, quote(symbol)),
        null);
    try {
      Set<Integer> set = new HashSet<Integer>();
      if ( cursor.moveToFirst() ) {
        do {
          int id = cursor.getInt(4);
          if ( set.add(id)) { // skip duplicates.
            Route route = readBasic(cursor);
            routes.add(route);
          }
        } while(cursor.moveToNext());      
      }
    } finally {
      cursor.close();
    }
    return routes;
  }
  
  @Override
  public MarkerInfo loadMarker(String symbol) {
    SQLiteDatabase db = getDatabase();
    try {
      Waypoint waypoint = loadWaypoint(db, symbol);
      if ( waypoint == null )
        return null;
      List<Route> routes = loadRoutesForMarker(db, symbol);
      String[] links = new String[routes.size()];
          for(int i = 0; i < links.length; i++) {
            links[i] = routes.get(i).getRouteId().toString();
          }
      waypoint.setLinks( Arrays.asList(links));
      return new MarkerInfo(waypoint, routes);      
    } finally {
      db.close();
    }
  }
}
