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

import java.util.List;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.plan.Leg;
import org.melato.bus.plan.Sequence;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Displays a sequence
 * @author Alex Athanasopoulos
 */
public class SequenceActivity extends ListActivity {
  private Sequence sequence;
  private ArrayAdapter<Leg> adapter;

  public SequenceActivity() {
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    sequence = Info.getSequence(this);
    adapter = new ArrayAdapter<Leg>(this, R.layout.list_item, sequence.getLegs());
    setListAdapter(adapter);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Info.saveSequence(this);
  }

  @Override
  protected void onListItemClick(ListView l, View v, int position, long id) {
    Leg leg = sequence.getLegs().get(position);
    BusActivities activities = new BusActivities(this);
    activities.showRoute(leg.getRStop1());    
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.sequence_menu, menu);
     return true;
  }

  private void removeLast() {
    List<Leg> legs = sequence.getLegs();
    if ( ! legs.isEmpty()) {
      Leg last = legs.get(legs.size()-1);
      if ( last.getStop2() != null) {
        last.setStop2(null);
      } else {
        legs.remove(legs.size()-1);
      }
      adapter.notifyDataSetChanged();
    }
  }
  private void removeFirst() {
    List<Leg> legs = sequence.getLegs();
    if ( ! legs.isEmpty()) {
      legs.remove(0);
      adapter.notifyDataSetChanged();
    }
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    List<Leg> legs = sequence.getLegs();
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.clear:
        legs.clear();
        adapter.notifyDataSetChanged();
        handled = true;
        break;
      case R.id.remove_last:
        removeLast();
        handled = true;
        break;
      case R.id.remove_first:
        removeFirst();
        handled = true;
        break;
      case R.id.schedule:
        startActivity(new Intent(this, SequenceScheduleActivity.class));
        handled = true;
        break;
    }
    return handled ? true : false;
  }    
}