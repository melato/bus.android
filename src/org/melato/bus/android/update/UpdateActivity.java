package org.melato.bus.android.update;

import org.melato.android.progress.ActivityProgressHandler;
import org.melato.android.progress.ProgressTitleHandler;
import org.melato.bus.android.R;
import org.melato.log.PLog;
import org.melato.progress.CanceledException;
import org.melato.progress.ProgressGenerator;
import org.melato.update.UpdateFile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class UpdateActivity extends Activity implements Runnable {
  private UpdateManager updateManager;
  private ActivityProgressHandler progress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    progress = new ProgressTitleHandler(this);
    setContentView(R.layout.update);
    updateManager = new UpdateManager(this);
    StringBuilder buf = new StringBuilder();
    for(UpdateFile f: updateManager.getAvailableUpdates()) {
      if ( f.getNote() != null ) {
        if ( buf.length() > 0 ) {
          buf.append( "\n" );
        }
        buf.append(f.getNote());
      }
    }
    TextView noteView = (TextView) findViewById(R.id.note);
    noteView.setText(buf.toString());
  }

  /** Called from the update button */
  public void update(View view) {
    Button button = (Button) findViewById(R.id.update);
    button.setEnabled(false);
    new Thread(this).start();
  }
  
  /** Called from the cancel button */
  public void cancel(View view) {
    PLog.info( "cancel()" );
    Button button = (Button) findViewById(R.id.cancel);
    button.setEnabled(false);
    progress.cancel();    
    finish();
  }
  
  @Override
  public void run() {
    if ( progress != null ) {
      ProgressGenerator.setHandler(progress);
    }
    try {
      updateManager.update(updateManager.getAvailableUpdates());
    } catch( CanceledException e ) {      
    }
    finish();
  }
  
  
  @Override
  protected void onDestroy() {
    progress.cancel();
    super.onDestroy();
  }
  
  /** Check for updates, and if there are any available give the option of downloading them. */
  public static void checkUpdates(Context context) {
    ConnectivityManager network = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = network.getActiveNetworkInfo();
    if ( networkInfo != null && networkInfo.getState() == State.CONNECTED ) {
      UpdateManager updateManager = new UpdateManager(context);
      if ( ! updateManager.getAvailableUpdates().isEmpty() ) {
        context.startActivity(new Intent(context, UpdateActivity.class));
      }
    }
  }


}
