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

import java.util.Locale;
import java.util.Map;

import org.melato.android.app.FrameworkApplication;
import org.melato.android.app.HomeActivity.ActivityLaunchItem;
import org.melato.android.app.HomeActivity.HelpLaunchItem;
import org.melato.android.app.HomeActivity.InternalLaunchItem;
import org.melato.android.app.MetadataStorage;
import org.melato.bus.android.activity.AllRoutesActivity;
import org.melato.bus.android.activity.NearbyActivity;
import org.melato.bus.android.activity.PlanTabsActivity;
import org.melato.bus.android.activity.Pref;
import org.melato.bus.android.activity.RecentRoutesActivity;
import org.melato.bus.android.activity.SequenceActivity;
import org.melato.bus.android.activity.SunActivity;
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.app.UpdateManager;
import org.melato.bus.android.bookmark.BusBookmarksActivity;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.android.map.RouteMapActivity;
import org.melato.client.HelpStorage;
import org.melato.client.MenuStorage;
import org.melato.update.PortableUpdateManager;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class BusApplication extends Application implements FrameworkApplication {
  private Locale locale;

  private MetadataStorage getMetadataStorage() {
    return new MetadataStorage(SqlRouteStorage.databaseFile(this).toString());
  }
  
  public HelpStorage getHelpStorage() {
    return new BusHelp(this);
  }
  
  @Override
  public MenuStorage getMenuStorage() {
    return getMetadataStorage();
  }
  
  @Override
  public PortableUpdateManager getUpdateManager() {
    return new UpdateManager(this);
  }

  @Override
  public int getEulaResourceId() {
    return R.string.eula;
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
    BusDebug.initLogging(this);
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
    return new ApplicationVariables(this);
  }
  
  private InternalLaunchItem[] internalItems = {
      new ActivityLaunchItem(AllRoutesActivity.class, R.drawable.all, R.string.all_routes, R.string.all_routes_tooltip),
      new ActivityLaunchItem(RecentRoutesActivity.class, R.drawable.recent, R.string.menu_recent_routes, R.string.recent_routes_tooltip),
      //new ActivityLaunchItem(AgenciesActivity.class, R.drawable.agencies, R.string.menu_agencies),
      new ActivityLaunchItem(BusBookmarksActivity.class, R.drawable.bookmark, R.string.bookmarks, R.string.bookmarks_tooltip),
      new ActivityLaunchItem(PlanTabsActivity.class, R.drawable.plan, R.string.search, R.string.search_tooltip),
      new ActivityLaunchItem(SequenceActivity.class, R.drawable.sequence, R.string.sequence, R.string.sequence_tooltip),
      new ActivityLaunchItem(NearbyActivity.class, R.drawable.nearby, R.string.menu_nearby_routes, R.string.nearby_routes_tooltip),
      new ActivityLaunchItem(RouteMapActivity.class, R.drawable.map, R.string.map),
      new ActivityLaunchItem(SunActivity.class, R.drawable.sun, R.string.sun, R.string.sunrise_sunset),
      new ActivityLaunchItem(BusPreferencesActivity.class, R.drawable.preferences, R.string.pref_menu),
      //new ActivityLaunchItem(UploadStopsActivity.class, R.drawable.upload, R.string.upload),
      new HelpLaunchItem(R.drawable.about, R.string.about, "about"),
  };

  
  @Override
  public InternalLaunchItem[] getInternalLaunchItems() {
    return internalItems;
  }
  
}
