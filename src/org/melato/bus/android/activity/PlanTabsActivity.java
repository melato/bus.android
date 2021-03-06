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
package org.melato.bus.android.activity;

import java.net.ConnectException;
import java.net.UnknownHostException;

import org.melato.android.bookmark.BookmarksActivity;
import org.melato.android.location.Locations;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.bookmark.BookmarkTypes;
import org.melato.bus.otp.OTP;
import org.melato.bus.otp.OTPClient;
import org.melato.bus.otp.OTPRequest;
import org.melato.bus.plan.PlanEndpoints;
import org.melato.client.Bookmark;
import org.melato.client.Serialization;
import org.melato.gps.Point2D;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.Toast;

/** Computes and displays a list of plans for going to a destination.
 **/
public class PlanTabsActivity extends FragmentActivity implements OnTabChangeListener {
  public static final String POINT = "POINT";
  public static final String KEY_TAB = "tab";
  public static final String TAB_SEARCH = "search";
  public static final String TAB_RESULTS = "itineraries";
  private FragmentTabHost tabHost;
  private boolean justSearched;
  
  class PlanTask extends AsyncTask<OTPRequest,Void,OTP.Plan> {    
    private Exception exception;
    private OTPRequest request;
    @Override
    protected void onPreExecute() {
      setProgressBarIndeterminate(true);
      setProgressBarVisibility(true);
    }

    @Override
    protected OTP.Plan doInBackground(OTPRequest... params) {
      request = params[0];
      String url = Info.routeManager(PlanTabsActivity.this).getPlannerUrl();
      OTP.Planner planner = new OTPClient(url);
      try {
        return planner.plan(request);
      } catch(Exception e) {
        exception = e;
        return null;
      }
    }

    @Override
    protected void onPostExecute(OTP.Plan plan) {
      setProgressBarVisibility(false);
      if ( plan == null) {
        if ( exception != null) {
          showError(exceptionToString(exception));
        }        
      } else {
        getPlanFragment().setPlan(plan, request);
        //setTitle(R.string.suggested_routes);
        tabHost.setCurrentTabByTag(TAB_RESULTS);
        justSearched = true;
      }
    }
  }
  
  @Override
  public void onTabChanged(String tabId) {
    justSearched = false;
  }
      
  @Override
  public void onBackPressed() {
    if ( justSearched ) {
      tabHost.setCurrentTabByTag(TAB_SEARCH);
    } else {
      super.onBackPressed();
    }
  }



  void showError(String error) {
    Toast toast = Toast.makeText(PlanTabsActivity.this, error, Toast.LENGTH_SHORT);
    toast.show();              
  }
  void showError(int errorResource) {
    showError(getString(errorResource));
  }
  String exceptionToString(Exception e) {
    int resId = 0;
    if ( e instanceof UnknownHostException) {
      resId = R.string.error_connect;
    } else if ( e instanceof ConnectException) {
        resId = R.string.error_connect;
    } else {
      return e.toString();
    }
    return getString(resId);
  }
  
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    boolean enabled = TAB_SEARCH.equals(tabHost.getCurrentTabTag());
    return enabled;
  }

  public boolean onOption(int itemId) {
    boolean handled = false;
    FragmentManager fm = getSupportFragmentManager();
    switch(itemId) {
      case R.id.swap:
        {
          PlanFragment planFragment = (PlanFragment)fm.findFragmentByTag(TAB_SEARCH);
          planFragment.swap();
        }
        handled = true;
        break;
      case R.id.plan:
        plan();
        handled = true;
        break;
      case R.id.bookmark:
        bookmark();
        handled = true;
        break;
      case R.id.pref:
        startActivity(new Intent(this, BusPreferencesActivity.class));      
        break;
    }
    return handled ? true : false;
  }
  
  public void onClick(View v) {
    onOption(v.getId());
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return onOption(item.getItemId());
  }

  Point2D getCurrentLocation() {
    LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    return Locations.location2Point(location);    
  }
  
  PlanFragment getPlanFragment() {
    FragmentManager fm = getSupportFragmentManager();
    return (PlanFragment) fm.findFragmentByTag(TAB_SEARCH);
  }
  void plan() {
    tabHost.setCurrentTabByTag(TAB_SEARCH);
    PlanFragment planFragment = getPlanFragment();
    Point2D from = PlanFragment.origin;
    if ( from == null) {
      from = getCurrentLocation();
    }
    if ( PlanFragment.destination == null) {
      showError(R.string.missing_destination);
    } else if ( from == null) {
      showError(R.string.missing_origin);
    } else {
      OTPRequest request = planFragment.buildRequest(from);
      new PlanTask().execute(request);      
    }
  }
  
  void bookmark() {
    FragmentManager fm = getSupportFragmentManager();
    PlanFragment planFragment = (PlanFragment) fm.findFragmentByTag(TAB_SEARCH);
    PlanEndpoints endpoints = planFragment.getEndpoints();
    Bookmark bookmark = new Bookmark(BookmarkTypes.PLAN, endpoints.getName(), endpoints);
    BookmarksActivity.addBookmarkDialog(this, bookmark);
  }

  void setEndpoints(PlanEndpoints endpoints) {
    tabHost.setCurrentTabByTag(TAB_SEARCH);
    FragmentManager fm = getSupportFragmentManager();
    PlanFragment planFragment = (PlanFragment) fm.findFragmentByTag(TAB_SEARCH);
    planFragment.setEndpoints(endpoints);    
  }

  /** Start the activity, making sure that the SEARCH tab is displayed */
  public static void showSearch(Context context) {    
    Intent intent = new Intent(context, PlanTabsActivity.class);
    intent.putExtra(PlanTabsActivity.KEY_TAB, PlanTabsActivity.TAB_SEARCH);
    context.startActivity(intent);        
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_PROGRESS);  
    setContentView(R.layout.plantabs);
    tabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
    tabHost.setOnTabChangedListener(this);
    tabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);  
    tabHost.addTab(tabHost.newTabSpec(TAB_SEARCH).setIndicator(getString(R.string.search)),
        PlanFragment.class, null);
    tabHost.addTab(tabHost.newTabSpec(TAB_RESULTS).setIndicator(getString(R.string.itineraries)),
        OTPItinerariesFragment.class, null);
    PlanEndpoints endpoints = Serialization.cast(getIntent().getSerializableExtra(Keys.ENDPOINTS), PlanEndpoints.class);
    if ( endpoints != null) {
      tabHost.setCurrentTabByTag(TAB_SEARCH);      
    } else {
      String tab = getIntent().getStringExtra(KEY_TAB);
      if ( TAB_RESULTS.equals(tab)) {
        tabHost.setCurrentTabByTag(TAB_RESULTS);      
      } else if ( TAB_SEARCH.equals(tab)) {
        tabHost.setCurrentTabByTag(TAB_SEARCH);      
      } else if ( PlanFragment.plan != null) {
        tabHost.setCurrentTabByTag(TAB_RESULTS);
      } else {
        tabHost.setCurrentTabByTag(TAB_SEARCH);
      }
    }
  }
}