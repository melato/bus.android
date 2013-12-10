/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.melato.android.app.FrameworkApplication;
import org.melato.bus.android.activity.Pref;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.client.HelpStorage;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class BusApplication extends Application implements FrameworkApplication {
  private Locale locale;

  public HelpStorage getHelpStorage() {
    return Info.helpManager(this);    
  }
  
  private void updateLocale(Configuration config) {
    config.locale = locale;
    Locale.setDefault(locale);
    Resources resources = getBaseContext().getResources(); 
    resources.updateConfiguration(config, resources.getDisplayMetrics());
  }
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if ( locale != null ) {
      updateLocale(newConfig);
    }
  }
  @Override
  public void onCreate() {
    super.onCreate();
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

    Resources resources = getBaseContext().getResources(); 
    Configuration config = resources.getConfiguration();

    String lang = settings.getString(Pref.LANG, "");
    if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
    {
        locale = new Locale(lang);
        updateLocale(config);
    }
  }
  @Override
  public Map<String, String> getApplicationVariables() {
    Map<String, String> vars = new HashMap<String, String>();
    String appVersion = "?";
    PackageInfo packageInfo;
    try {
      packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
      appVersion = packageInfo.versionName;
    } catch (NameNotFoundException e) {
      throw new RuntimeException(e);
    }
    SqlRouteStorage routeDB = (SqlRouteStorage) Info.routeManager(this)
        .getStorage();
    String databaseDate = routeDB.getBuildDate();
    if (databaseDate == null) {
      databaseDate = "?";
    }
    vars.put("app.version", appVersion);
    vars.put("db.version", databaseDate);
    return vars;
  }
}
