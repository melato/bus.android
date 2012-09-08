package org.melato.bus.android.track;

/** Helper class for defining an SQLite column */
public class Column {
  public final String name;
  public final String type;

  public Column(String name, String type) {
    super();
    this.name = name;
    this.type = type;
  }

  @Override
  public String toString() {
    return name;
  }
  
  public String createClause() {
    return name + " " + type;
  }
  
  /** Generate a create table statement for a table with the given columns. */
  public static String createStatement(String table, Column[] columns) {
    StringBuilder buf = new StringBuilder();
    buf.append( "CREATE TABLE " + table + "(");
    for( int i = 0; i < columns.length; i++ ) {
      if ( i > 0 ) {
        buf.append( ", " );
      }
      buf.append( columns[i].createClause() );
    }
    buf.append( ")" );
    return buf.toString();
  }
}
