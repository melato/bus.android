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
    long time = System.currentTimeMillis()/1000L;
    message = time + " " + message; 
    android.util.Log.i("melato.org", message);
    try {
      PrintWriter writer = new PrintWriter(new FileOutputStream(logFile, true));
      try {
        writer.println( message );
      } finally {
        writer.close();
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException( e );
    }
  }  
}
