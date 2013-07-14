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

import org.melato.android.progress.ActivityProgressHandler;
import org.melato.android.progress.ProgressTitleHandler;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.otp.OTPPlanner;
import org.melato.bus.plan.NamedPoint;
import org.melato.bus.plan.Plan;
import org.melato.bus.plan.PlanRequest;
import org.melato.bus.plan.Planner;
import org.melato.progress.ProgressGenerator;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/** Computes and displays a list of plans for going to a destination.
 * This is experimental.  It is not part of the production app yet.
 * */
public class PlanActivity extends Activity {
  public static final String POINT = "POINT";
  private ActivityProgressHandler progress;
  private static NamedPoint origin;
  private static NamedPoint destination;
  public static Plan[] plans;

  class PlanTask extends AsyncTask<Void,Void,Plan[]> {    
    private Exception exception;
    @Override
    protected void onPreExecute() {
    }

    @Override
    protected Plan[] doInBackground(Void... params) {
      ProgressGenerator.setHandler(progress);
      Planner planner = new OTPPlanner();
      planner.setRouteManager(Info.routeManager(PlanActivity.this));
      PlanRequest request = new PlanRequest();
      request.setFromPlace(origin);
      request.setToPlace(destination);
      try {
        return planner.plan(request);
      } catch(Exception e) {
        exception = e;
        return null;
      }
    }

    @Override
    protected void onPostExecute(Plan[] plans) {
      progress.end();
      if ( plans == null) {
        if ( exception != null) {
          Toast toast = Toast.makeText(PlanActivity.this, exception.toString(), Toast.LENGTH_SHORT);
          toast.show();              
        }        
      } else {
        PlanActivity.plans = plans;
        setTitle(R.string.best_route);
        startActivity(new Intent(PlanActivity.this, PlanResultsActivity.class));
      }
    }
  }
  
  void showEndpoints() {
    if ( origin != null) {
      TextView view = (TextView) findViewById(R.id.from);
      view.setText(origin.getName());
    }
    if ( destination != null) {
      TextView view = (TextView) findViewById(R.id.to);
      view.setText(destination.getName());
    }
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    progress = new ProgressTitleHandler(this);
    Intent intent = getIntent();
    NamedPoint point = (NamedPoint) intent.getSerializableExtra(POINT);
    if ( point != null) {
      if ( origin == null ) {
        origin = point;
      } else {
        destination = point;
      }
    }
    setContentView(R.layout.plan);
    showEndpoints();
  }

  void plan() {
    if ( destination == null) {
      setTitle("Missing Destination");
    } else if ( origin == null) {
      setTitle("Missing Origin");
    } else {
      new PlanTask().execute();      
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.plan_menu, menu);
     //HelpActivity.addItem(menu, this, Help.PLAN);
     return true;
  }

  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.swap:
        NamedPoint temp = origin;
        origin = destination;
        destination = temp;
        showEndpoints();
        handled = true;
        break;
      case R.id.plan:
        plan();
        handled = true;
        break;
    }
    return handled ? true : false;
  }
  
  

}