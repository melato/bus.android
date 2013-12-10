package org.melato.bus.android.track;

import java.io.File;
import java.io.IOException;

import org.melato.bus.model.RouteId;
import org.melato.bus.transit.ZipTrackWriter;
import org.melato.bus.transit.ZipTransitWriter;
import org.melato.mobile.MobileUser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class UploadInfo {
  public static final String UUID_PREF = "upload_client_id";
  public static String getClientId(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String uuid = prefs.getString(UUID_PREF, null);
    if ( uuid == null) {
      uuid = MobileUser.generateUuid();
      Editor editor = prefs.edit();
      editor.putString(UUID_PREF, uuid);
      editor.commit();
    }
    return uuid;
  }
  public static String getUrl(Context context) {
    //return Info.routeManager(context).getUploadUrl();
    return "http://transit.test.melato.org:8120";
    //return "http://metakinisi.melato.org:3011";
  }
  public static File createFile(Context context) {
    try {
      File file = File.createTempFile("transit", ".zip");
      ZipTransitWriter zip = new ZipTransitWriter(file);
      zip.begin(null);
      StopsDatabase.getInstance(context).loadNewStops(zip);
      zip.end();
      return file;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  public static File createTrackFile(Context context, RouteId routeId, File trackFile) {
    try {
      File file = File.createTempFile("transit", ".zip");
      ZipTrackWriter zip = new ZipTrackWriter(file);
      zip.open();
      try {
        zip.addTrack(routeId, trackFile);
      } finally {
        zip.close();
      }
      return file;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
