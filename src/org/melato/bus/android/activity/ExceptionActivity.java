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

import java.io.Serializable;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.RouteException;
import org.melato.bus.model.RouteId;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Schedule;
import org.melato.bus.model.ScheduleId;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * Displays the list of all routes
 * @author Alex Athanasopoulos
 *
 */
public class ExceptionActivity extends ListActivity {
  public static final String KEY_EXCEPTION = "exception";
  
  public static class ExceptionSpecifier implements Serializable {
    private static final long serialVersionUID = 1L;
    RouteId routeId;
    ScheduleId scheduleId;
    int time;
    public ExceptionSpecifier(RouteId routeId, ScheduleId scheduleId, int time) {
      super();
      this.routeId = routeId;
      this.scheduleId = scheduleId;
      this.time = time;
    }    
  }
  public static void showExceptions(Context context, ExceptionSpecifier exc) {
    Intent intent = new Intent(context, ExceptionActivity.class);
    intent.putExtra(KEY_EXCEPTION, exc);
    context.startActivity(intent);    
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    super.onCreate(savedInstanceState);
    ExceptionSpecifier exc = (ExceptionSpecifier) getIntent().getSerializableExtra(KEY_EXCEPTION);
    if ( exc == null) {
      finish();
    }
    RouteManager routeManager = Info.routeManager(this);
    Schedule schedule = routeManager.getSchedule(exc.routeId);
    //Route route = routeManager.getRoute(exc.routeId);
    List<RouteException> exceptions = schedule.getExceptions(exc.scheduleId, exc.time);
    setListAdapter(new ArrayAdapter<RouteException>(this, R.layout.list_item, exceptions));
  }
  
}
