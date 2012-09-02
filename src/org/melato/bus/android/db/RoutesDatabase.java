package org.melato.bus.android.db;


public final class RoutesDatabase {
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
     /** The name of this table */
     public static final String TABLE = "routes";
  }

  
  public static final class Markers extends MarkersColumns implements android.provider.BaseColumns
  {
     /** The name of this table */
     public static final String TABLE = "markers";
  }

  public static final class Stops extends StopsColumns implements android.provider.BaseColumns
  {
     public static final String TABLE = "stops";
  }
  
}
