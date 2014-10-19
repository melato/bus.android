/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013,2014 Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.android;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.melato.bus.android.activity.Pref;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.android.gpx.GPXRoutesMap;
import org.melato.bus.android.map.GoogleRoutesMap;
import org.melato.bus.client.NearbyManager;
import org.melato.bus.model.Agency;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.ScheduleId;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.NamedPoint;
import org.melato.bus.plan.Sequence;
import org.melato.bus.plan.WalkModel;
import org.melato.client.Serialization;
import org.melato.log.Log;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.Toast;

/** Provides access to global (static) objects. */
public class Info {
  public static final float MARK_PROXIMITY = 200f;
  public static final String PREF_WALK_SPEED = "walk_speed";
  public static final String SEQUENCE_FILE = "sequence.dat";
  private static RouteManager routeManager;
  private static WalkModel walkModel;
  private static AndroidTrackHistory trackHistory;
  private static Map<String,Drawable.ConstantState> agencyIcons = new HashMap<String,Drawable.ConstantState>();
  private static Sequence sequence;
  private static ScheduleId stickyScheduleId;
  private static List<Runnable> reloadCallbacks = new ArrayList<Runnable>();
  private static Boolean hasMap = null;
  
  public static RouteManager routeManager(Context context) {
    if ( routeManager == null ) {
      synchronized(Info.class) {
        if ( routeManager == null ) {
          context = context.getApplicationContext();
          routeManager = new RouteManager(new SqlRouteStorage(context));          
        }
      }
    }
    return routeManager;
  }
  public static boolean isValidDatabase(Context context) {
    File file = SqlRouteStorage.databaseFile(context);
    if ( ! file.exists() )
      return false;
    SqlRouteStorage storage = (SqlRouteStorage) routeManager(context).getStorage();
    if ( storage.checkVersion() )
      return true;
    reload();
    return false;
  }
  
  public static AndroidTrackHistory trackHistory(Context context) {
    if ( trackHistory == null ) {
      synchronized(Info.class) {
        if ( trackHistory == null ) {
          context = context.getApplicationContext();
          trackHistory = new AndroidTrackHistory(context);          
        }
      }
    }
    return trackHistory;
  }  
  
  public static NearbyManager nearbyManager(Context context) {
    File cacheDir = context.getCacheDir();
    return new NearbyManager(routeManager(context), cacheDir); 
  }
  
  /** Add a callback to be called when the database is updated. */
  public static void addReloadCallback(Runnable callback) {
    reloadCallbacks.add(callback);    
  }
  
  /** Uncache any database data, in order to use a newly downloaded database. */
  public static void reload() {
    routeManager = null;
    trackHistory = null;
    for(Runnable callback: reloadCallbacks ) {
      callback.run();
    }
  }
  
  public static synchronized Drawable getAgencyIcon(Context context, Agency agency) {
    String agencyName = agency.getName();
    Drawable.ConstantState state = agencyIcons.get(agencyName);
    if ( state == null && ! agencyIcons.containsKey(agencyName)) {
      byte[] icon = agency.getIcon();
      if ( icon != null) {
        Drawable drawable = new BitmapDrawable(context.getResources(), new ByteArrayInputStream(icon));
        state = drawable.getConstantState();
      }    
      agencyIcons.put(agencyName, state);
    }
    if ( state != null) {
      return state.newDrawable(context.getResources());
    } else {
      return null;
    }
  }
  public static synchronized Drawable getAgencyIcon(Context context, RouteId routeId) {
    Agency agency = Info.routeManager(context).getAgency(routeId);
    return getAgencyIcon(context, agency);
  }
  
  public static String getDefaultAgencyName(Context context) {
    RouteManager routeManager = routeManager(context);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String agency = prefs.getString(Pref.DEFAULT_AGENCY, null);
    if ( agency != null) {
      Agency a = routeManager.getAgency(agency); // make sure it exists.
      if ( a == null) {
        agency = null;
      }
    }
    if ( agency == null) {
      agency = routeManager.getDefaultAgency();      
    }
    return agency;
  }
  public static void setDefaultAgencyName(Context context, String agency) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    Editor editor = prefs.edit();
    editor.putString(Pref.DEFAULT_AGENCY, agency);
    editor.commit();
  }

  private static Sequence loadSequence(Context context) {
    File dir = context.getFilesDir();
    File file = new File(dir, SEQUENCE_FILE);
    return (Sequence) Serialization.read(Sequence.class, file);
  }
  
  public static Sequence getSequence(Context context) {
    if ( sequence == null) {
      sequence = loadSequence(context);
      if ( sequence == null) {
        sequence = new Sequence();
      }
    }
    return sequence;
  }
  
  public static void setSequence(Context context, Sequence sequence) {
    Info.sequence = sequence;
    saveSequence(context);
  }
  
  public static void saveSequence(Context context) {
    if ( sequence != null) {
      File dir = context.getFilesDir();
      File file = new File(dir, SEQUENCE_FILE);
      try {
        Serialization.write(sequence, file);
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }

  public static ScheduleId getStickyScheduleId() {
    return stickyScheduleId;
  }

  public static void setStickyScheduleId(ScheduleId stickyScheduleId) {
    Info.stickyScheduleId = stickyScheduleId;
  }

  public static WalkModel walkModel(Context context) {
    if ( walkModel == null ) {
      synchronized(Info.class) {
        if ( walkModel == null ) {
          context = context.getApplicationContext();
          PlanOptions options = new PlanOptions(context);
          walkModel = new WalkModel(options.getWalkSpeedMetric());
        }
      }
    }
    return walkModel;
  }
  
  public static NamedPoint namedPoint(Context context, RStop rstop) {
    if ( rstop == null) {
      return null;
    }
    Stop stop = rstop.getStop();
    NamedPoint point = new NamedPoint(stop);
    Route route = Info.routeManager(context).getRoute(rstop.getRouteId());
    point.setName(stop.getName() + " " + route.getLabel());
    return point;
  }
  public static RoutesMap routesMap(Context context) {
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    String mapPref = prefs.getString(Pref.MAP, "");
    Log.info("map pref: " + mapPref);
    if ( "gpx".equals(mapPref)) {
      return new GPXRoutesMap(context, routeManager(context));
    } else if ( "mapsforge".equals(mapPref)) {
      String packageName = "org.melato.mapsforge"; 
      GPXRoutesMap map = new GPXRoutesMap(context, routeManager(context));
      map.setPackage(packageName);
      if ( hasMap == null ) {
        PackageManager packageManager = context.getPackageManager();
        try {
          packageManager.getPackageInfo(packageName, 0);
          hasMap = true;
        } catch (NameNotFoundException e) {
          hasMap = false;
          Toast.makeText(context, R.string.mapsforge_not_available, Toast.LENGTH_SHORT).show();
        }
      }
      return map;
    } else {
      return new GoogleRoutesMap(context);
    }
  }
}
