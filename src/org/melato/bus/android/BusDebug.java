package org.melato.bus.android;

import org.melato.android.log.AndroidLogger;
import org.melato.log.Log;

import android.content.Context;

public class BusDebug {
  public static void initLogging(Context context) {
    //File crashFile = new File(Environment.getExternalStorageDirectory(), "buscrash");
    //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(crashFile));
    Log.setLogger(new AndroidLogger(context));
    //File logFile = new File(Environment.getExternalStorageDirectory(), "bus.log");
    //Log.setLogger(new FileLogger(logFile));
    Log.info("initLogging");
  }

}
