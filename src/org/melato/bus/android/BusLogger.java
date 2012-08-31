package org.melato.bus.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

import org.melato.log.Logger;

import android.content.Context;

public class BusLogger implements Logger {
  Context context;
  File    logFile;
  
  public BusLogger(Context context) {
    super();
    this.context = context;
    logFile = new File( context.getFilesDir(), "log.txt");    
  }
  
  @Override
  public void log(String message) {
    android.util.Log.i("melato.org", message);
    try {
      PrintWriter writer = new PrintWriter(new FileOutputStream(logFile, true));
      try {
        long time = System.currentTimeMillis()/1000L;
        writer.println( time + " " + message );
      } finally {
        writer.close();
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException( e );
    }
  }  
}
