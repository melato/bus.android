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

import java.util.Date;
import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.model.Schedule.DateScheduleFactory;
import org.melato.bus.model.Schedule.ScheduleFactory;
import org.melato.bus.model.Schedule.ScheduleIdScheduleFactory;
import org.melato.bus.model.ScheduleId;
import org.melato.bus.plan.Sequence;
import org.melato.bus.plan.SequenceInstance;
import org.melato.bus.plan.SequenceSchedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Displays a sequence
 * @author Alex Athanasopoulos
 */
public class SequenceScheduleActivity extends Activity implements OnItemClickListener {
  private Sequence sequence;
  private List<SequenceInstance> instances;

  public SequenceScheduleActivity() {
  }
  
  public static ScheduleFactory scheduleFactory() {
    ScheduleId scheduleId = Info.getStickyScheduleId();
    if ( scheduleId != null) {
      return new ScheduleIdScheduleFactory(scheduleId);
    } else {
      return new DateScheduleFactory();
    }
  }
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sequence = Info.getSequence(this);
    SequenceSchedule schedule = new SequenceSchedule(sequence, scheduleFactory(), Info.routeManager(this));
    instances = schedule.getInstances();
    HighlightAdapter<SequenceInstance> adapter = new HighlightAdapter<SequenceInstance>(this, instances);
    setContentView(R.layout.schedule);
    ListView listView = (ListView) findViewById(R.id.listView);
    TextView textView = (TextView) findViewById(R.id.textView);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);
        int pos = schedule.getTimePosition(new Date());
    if ( pos >= 0 ) {
      adapter.setSelection(pos);
      listView.setSelection(pos);
    }
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
    SequenceInstance instance = instances.get(position);
    Intent intent = new Intent(this, SequenceInstanceActivity.class);
    intent.putExtra(SequenceInstanceActivity.KEY_INSTANCE, instance);
    startActivity(intent);
  }
  
  @Override
  protected void onDestroy() {
    super.onDestroy();
    Info.saveSequence(this);
  }

}