package org.melato.bus.android.app;

import java.io.File;
import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.progress.ProgressGenerator;
import org.melato.update.PortableUpdateManager;
import org.melato.update.UpdateFile;

import android.content.Context;

/** Checks for and/or downloads database updates. */
public class UpdateManager extends PortableUpdateManager {
  public static final String ROUTES_UPDATE = "ROUTES.zip";  
  public static final String ROUTES_ENTRY = "ROUTES.db";
  private Context context;
  
  public UpdateManager(Context context) {
    super();
    this.context = context;
    setIndexUrl(context.getResources().getString(R.string.update_url));
    setFilesDir(context.getFilesDir());
  }
  
  public void update(List<UpdateFile> updates) {
    ProgressGenerator progress = ProgressGenerator.get();
    for( UpdateFile f: updates ) {
      if ( ROUTES_UPDATE.equals(f.getName())) {
        File databaseFile = SqlRouteStorage.databaseFile(context);
        progress.setText("Routes Database");
        updateZipedFile(f, ROUTES_ENTRY, databaseFile);
        continue;
      }
      if ( "test".equals(f.getName())) {
        progress.setText("Test");
        int n = 100;
        progress.setLimit(n);
        for( int i = 0; i < n; i++ ) {
          try {
            Thread.sleep(50);
            progress.setPosition(i);
          } catch (InterruptedException e) {
          }
        }
        setInstalled(f);
        continue;
      }
    }
  }
}
