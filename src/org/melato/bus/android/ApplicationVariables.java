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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.AbstractMap;
import java.util.Set;

import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.update.Streams;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

public class ApplicationVariables extends AbstractMap<String,String> {
  private Context context;
  
  public ApplicationVariables(Context context) {
    super();
    this.context = context;
  }

  @Override
  public Set<java.util.Map.Entry<String, String>> entrySet() {
    return null;
  }

  String getAppVersion() {
    try {
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionName;
    } catch (NameNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
  
  String getDatabaseVersion() {
    SqlRouteStorage routeDB = (SqlRouteStorage) Info.routeManager(context)
        .getStorage();
    String databaseDate = routeDB.getBuildDate();
    return databaseDate != null ? databaseDate : "?";
  }
  
  String getArtists() {
    try {
      InputStream in = context.getResources().openRawResource(R.raw.artists);
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      Streams.copy(in,  out, true);
      return out.toString();
    } catch(Exception e) {
      return null;
    }
  }
  @Override
  public String get(Object key) {
    if ( "app.version".equals(key)) {
      return getAppVersion();
    }
    if ( "db.version".equals(key)) {
      return getDatabaseVersion();
    }
    if ( "app.artists".equals(key)) {
      return getArtists();
    }
    return null;
  }
}
