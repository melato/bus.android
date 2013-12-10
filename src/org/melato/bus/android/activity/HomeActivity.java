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
package org.melato.bus.android.activity;

import org.melato.android.app.BaseHomeActivity;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.android.app.UpdateActivity;
import org.melato.bus.android.map.RouteMapActivity;
import org.melato.bus.android.track.UploadStopsActivity;
import org.melato.client.MenuStorage;

import android.content.Context;

/** The main activity checks for updates and launches the next activity. */
public class HomeActivity extends BaseHomeActivity {
  static class Help extends InternalLaunchItem {
    private String helpName;
    
    public Help(int icon, int label, String helpName) {
      super(icon, label);
      this.helpName = helpName;
    }

    public void invoke(Context context) {
      HelpActivity.showHelp(context, helpName);
    }
  }

  static class About extends Help {    
    public About() {
      super(R.drawable.about, R.string.about, "about");
    }
  }  
  
  // references to our images
  private InternalLaunchItem[] internalItems = {
      new InternalLaunchItem(AllRoutesActivity.class, R.drawable.all, R.string.all_routes),
      new InternalLaunchItem(RecentRoutesActivity.class, R.drawable.recent, R.string.menu_recent_routes),
      new InternalLaunchItem(AgenciesActivity.class, R.drawable.agencies, R.string.menu_agencies),
      new InternalLaunchItem(SequenceActivity.class, R.drawable.sequence, R.string.sequence),
      new InternalLaunchItem(PlanTabsActivity.class, R.drawable.plan, R.string.search),
      new InternalLaunchItem(NearbyActivity.class, R.drawable.nearby, R.string.menu_nearby_routes),
      new InternalLaunchItem(RouteMapActivity.class, R.drawable.map, R.string.map),
      new InternalLaunchItem(SunActivity.class, R.drawable.sun, R.string.sun),
      new InternalLaunchItem(BusPreferencesActivity.class, R.drawable.preferences, R.string.pref_menu),
      new InternalLaunchItem(UploadStopsActivity.class, R.drawable.upload, R.string.upload),
      new About(),
  };

  
  @Override
  protected MenuStorage getMenuStorage() {
    return Info.menuManager(this);
  }
  
  @Override
  protected LaunchItem[] getInternalLaunchItems() {
    return internalItems;
  }
  @Override
  protected void showHelp(Context context, String target) {
    HelpActivity.showHelp(context, target);
  }
  @Override
  protected boolean checkUpdates() {
    return UpdateActivity.checkUpdates(this);
  }  
}
