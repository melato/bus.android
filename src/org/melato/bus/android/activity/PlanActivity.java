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

import org.melato.android.AndroidLogger;
import org.melato.android.location.Locations;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.plan.Plan;
import org.melato.bus.plan.Planner;
import org.melato.bus.plan.SingleRoutePlanner;
import org.melato.gps.Point2D;
import org.melato.log.Log;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/** Computes and displays a list of plans for going to a destination. */
public class PlanActivity extends ListActivity {
  private Point2D origin;
  private Point2D destination;

  class PlanTask extends AsyncTask<Void,Void,Plan[]> {    
    @Override
    protected void onPreExecute() {
      setTitle(R.string.loading);
    }

    @Override
    protected Plan[] doInBackground(Void... params) {
      Planner planner = new SingleRoutePlanner();
      planner.setRouteManager(Info.routeManager(PlanActivity.this));
      return planner.plan(origin, destination);
    }

    @Override
    protected void onPostExecute(Plan[] plans) {
      setTitle(R.string.best_route);
      setListAdapter(new ArrayAdapter<Plan>(PlanActivity.this, R.layout.list_item, plans));      
    }
  }
  
  
  public PlanActivity() {
  }

/** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.setLogger(new AndroidLogger(this));
    destination = Locations.getGeoUriPoint(getIntent());        
    origin = new Point2D(37.989048f, 23.790638f);    
    if ( origin == null) {
      setTitle("Missing Origin");
    } else if ( destination == null) {
      setTitle("Missing Destination");
    } else {
      new PlanTask().execute();
    }
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}