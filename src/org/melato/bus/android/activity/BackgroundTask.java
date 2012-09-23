package org.melato.bus.android.activity;

import android.app.Activity;

public abstract class BackgroundTask implements Runnable {
  private Activity activity;
  
  public BackgroundTask(Activity activity) {
    this.activity = activity;
  }

  /** This is called in the UI thread when the background thread is done. */
  protected abstract void onForeground();

  public void run() {
    activity.runOnUiThread(new Runnable() {
      public void run() {
        onForeground();
      }
    });
  }
  public static void example(Activity activity) {
    
  }
}
