package org.melato.bus.android.db;

import android.net.Uri;

public final class RoutesDatabase {
  public static final String AUTHORITY = "org.melato.bus";
  public static final Uri CONTENT_URI = Uri.parse( "content://" + RoutesDatabase.AUTHORITY );
  static final String DATABASE_NAME = "ROUTES.db";
  /** The version of the database schema */
  static final int DATABASE_VERSION = 1;

  public static class RoutesColumns
  {
     public static final String NAME        = "name";
     public static final String LABEL       = "label";
     public static final String TITLE       = "title";
     public static final String DIRECTION   = "direction";
     static final String NAME_TYPE           = "TEXT";
     static final String LABEL_TYPE          = "TEXT";
     static final String TITLE_TYPE          = "TEXT";
     static final String DIRECTION_TYPE      = "TEXT";
     static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
  }
  
  public static class MarkersColumns
  {
     public static final String SYMBOL       = "symbol";
     public static final String NAME         = "name";
     public static final String LAT          = "lat";
     public static final String LON          = "lon";

     static final String SYMBOL_TYPE          = "TEXT NOT NULL";
     static final String NAME_TYPE            = "TEXT NOT NULL";
     static final String LAT_TYPE             = "FLOAT NOT NULL";
     static final String LON_TYPE             = "FLOAT NOT NULL";
     static final String _ID_TYPE             = "INTEGER PRIMARY KEY AUTOINCREMENT";
  }
  
  public static class StopsColumns
  {
     public static final String ROUTE         = "route";
     public static final String MARKER        = "marker";
     public static final String SEQ           = "seq";
     static final String ROUTE_TYPE            = "INTEGER NOT NULL";
     static final String MARKER_TYPE           = "INTEGER NOT NULL";
     static final String SEQ_TYPE              = "INTEGER NOT NULL";
     static final String _ID_TYPE              = "INTEGER PRIMARY KEY AUTOINCREMENT";
  }

  public static final class Routes extends RoutesColumns implements android.provider.BaseColumns
  {
     public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.melato.bus.route";
     public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.org.melato.bus.route";
     public static final Uri CONTENT_URI = Uri.parse( "content://" + RoutesDatabase.AUTHORITY + "/" + Routes.TABLE );

     /** The name of this table */
     public static final String TABLE = "routes";
     static final String CREATE_STATEMENT = 
        "CREATE TABLE " + Routes.TABLE + "(" + " " + Routes._ID           + " " + Routes._ID_TYPE + 
                                         "," + " " + Routes.NAME          + " " + Routes.NAME_TYPE + 
                                         "," + " " + Routes.LABEL          + " " + Routes.LABEL_TYPE + 
                                         "," + " " + Routes.TITLE          + " " + Routes.TITLE_TYPE + 
                                         "," + " " + Routes.DIRECTION          + " " + Routes.DIRECTION_TYPE + 
                                         ");";
  }

  
  public static final class Markers extends MarkersColumns implements android.provider.BaseColumns
  {
     public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.melato.bus.marker";
     public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.org.melato.bus.marker";
     public static final Uri CONTENT_URI = Uri.parse( "content://" + RoutesDatabase.AUTHORITY + "/" + Markers.TABLE );

     /** The name of this table */
     public static final String TABLE = "markers";
     static final String CREATE_STATEMENT = 
        "CREATE TABLE " + Markers.TABLE + "(" + " " + Markers._ID           + " " + Markers._ID_TYPE + 
                                         "," + " " + Markers.SYMBOL          + " " + Markers.SYMBOL_TYPE + 
                                         "," + " " + Markers.NAME          + " " + Markers.NAME_TYPE + 
                                         "," + " " + Markers.LAT          + " " + Markers.LAT_TYPE + 
                                         "," + " " + Markers.LON          + " " + Markers.LON_TYPE + 
                                         ");";
  }

  public static final class Stops extends StopsColumns implements android.provider.BaseColumns
  {
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.org.melato.bus.stop";
     public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.nl.sogeti.bus.stop";
     public static final Uri CONTENT_URI = Uri.parse( "content://" + RoutesDatabase.AUTHORITY + "/" + Stops.TABLE );

     public static final String TABLE = "stops";
     static final String CREATE_STATEMENT = 
        "CREATE TABLE " + Stops.TABLE + "(" + " " + Stops._ID           + " " + Stops._ID_TYPE + 
                                         "," + " " + Stops.MARKER          + " " + Stops.MARKER_TYPE + 
                                         "," + " " + Stops.ROUTE          + " " + Stops.ROUTE_TYPE + 
                                         "," + " " + Stops.SEQ          + " " + Stops.SEQ_TYPE + 
                                         ");";
  }
  
}
