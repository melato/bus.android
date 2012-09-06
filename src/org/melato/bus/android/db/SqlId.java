package org.melato.bus.android.db;

import org.melato.bus.model.Id;

public class SqlId implements Id {
  private static final long serialVersionUID = 1L;
  private int id;
    
  public SqlId(int id) {
    super();
    this.id = id;
  }
  @Override
  public String toString() {
    return String.valueOf(id);
  }
  public int getId() {
    return id;
  }
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SqlId other = (SqlId) obj;
    if (id != other.id)
      return false;
    return true;
  }
  
  
}
