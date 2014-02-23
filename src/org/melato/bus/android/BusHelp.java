package org.melato.bus.android;

import org.melato.android.app.MetadataStorage;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.client.HelpItem;
import org.melato.client.HelpStorage;

import android.content.Context;

public class BusHelp implements HelpStorage {
  public static final String RECENT_CHANGES = "recent_changes";
  private Context context;
  private HelpStorage dbHelp;
  
  public BusHelp(Context context) {
    super();
    this.context = context;
  }

  HelpStorage getDatabaseHelp() {
    if ( dbHelp == null ) {
      dbHelp = new MetadataStorage(SqlRouteStorage.databaseFile(context).toString());
    }
    return dbHelp;
  }

  HelpItem getInternalHelp(String name) {
    if ( RECENT_CHANGES.equals(name)) {
      HelpItem item = new HelpItem();
      item.setNode(name);
      item.setName(name);
      item.setText(ApplicationVariables.rawString(context, R.raw.recent_changes, true));
      item.setTitle(context.getString(R.string.recent_changes));
      return item;
    }
    return null;
  }
  public HelpItem loadHelpByName(String name, String lang) {
    HelpItem item = getInternalHelp(name);
    if ( item != null ) {
      return item;
    }
    return getDatabaseHelp().loadHelpByName(name, lang);
  }

  public HelpItem loadHelpByNode(String node) {
    HelpItem item = getInternalHelp(node);
    if ( item != null ) {
      return item;
    }
    return getDatabaseHelp().loadHelpByNode(node);
  }
  
}
