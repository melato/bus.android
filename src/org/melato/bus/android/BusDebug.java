package org.melato.bus.android;

import org.melato.log.Log;

import android.content.Context;

public class BusDebug {
  public static void initLogging(Context context) {
    //Log.setLogger(new AndroidLogger(context));
    //File file = new File(Environment.getExternalStorageDirectory(), "mybus.log");
    //Log.setLogger(new FileLogger(file));
    Log.info("initLogging");
  }

}
