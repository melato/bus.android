package org.melato.bus.android;

import org.melato.log.Logger;

import android.content.Context;

public class AndroidLogger implements Logger {
  Context context;
  
  public AndroidLogger(Context context) {
    super();
    this.context = context;
  }
  
  @Override
  public void log(String message) {
    long time = System.currentTimeMillis()/1000L;
    message = time + " " + message; 
    android.util.Log.i("melato.org", message);
  }  
}
