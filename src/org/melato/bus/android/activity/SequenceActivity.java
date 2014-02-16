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

import java.util.ArrayList;
import java.util.List;

import org.melato.android.app.HelpActivity;
import org.melato.android.menu.Menus;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.Formatting;
import org.melato.bus.model.Route;
import org.melato.bus.model.RouteManager;
import org.melato.bus.model.Stop;
import org.melato.bus.plan.LegGroup;
import org.melato.bus.plan.RouteLeg;
import org.melato.bus.plan.Sequence;
import org.melato.bus.plan.WalkModel;
import org.melato.gps.Point2D;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

/**
 * Displays a sequence and allows simple editing.
 * Launches the sequence schedule activity.
 * @author Alex Athanasopoulos
 */
public class SequenceActivity extends FragmentActivity implements OnItemClickListener, OnClickListener {
  private Sequence sequence;
  private List<SequenceItem> items;
  private ArrayAdapter<SequenceItem> adapter;
  private ListView listView;

  public static interface SequenceItem {    
  }
  class LegItem implements SequenceItem {
    private LegGroup leg;
    private Route route;

    public LegItem(LegGroup leg, RouteManager routeManager) {
      super();
      this.leg = leg;
      route = routeManager.getRoute(leg.getLeg().getRouteId());
    }

    @Override
    public String toString() {
      RouteLeg leg = this.leg.getLeg();
      StringBuilder buf = new StringBuilder();
      buf.append(route.getLabel());
      buf.append( " " );
      buf.append(leg.getStop1().getName());
      Stop stop2 = leg.getStop2();
      if ( stop2 != null) {
        buf.append( " -> " );
        buf.append(stop2.getName());
      }
      return buf.toString();
    }
    
    
  }
  
  public static class WalkItem implements SequenceItem {
    private float distance;
    private String label;
    
    private void initLabel(Context context) {
      WalkModel walkModel = Info.walkModel(context);
      label = context.getString(R.string.walk_leg, Formatting.straightDistance(distance), walkModel.distanceDuration(distance));
    }
    public WalkItem(Point2D point1, Point2D point2, RouteManager routeManager, Context context) {
      super();
      distance = routeManager.getMetric().distance(point1, point2);
      System.out.println( "WalkItem: from=" + point1 + " to=" + point2 + " distance=" + distance);
      initLabel(context);
    }
    @Override
    public String toString() {
      return label;
    }
  }
  
  public List<SequenceItem> getSequenceItems(Sequence sequence, RouteManager routeManager) {
    List<SequenceItem> items = new ArrayList<SequenceItem>();
    RouteLeg previous = null;
    for(LegGroup leg: sequence.getLegs() ) {
      if ( previous != null) {
        items.add(new WalkItem(previous.getStop2(), leg.getLeg().getStop1(), routeManager, this));
      }
      items.add(new LegItem(leg, routeManager));
      previous = leg.getLeg();
    }
    return items;
  }
  
  
  public SequenceActivity() {
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.list_activity);
    listView = (ListView) findViewById(R.id.listView);
    listView.setOnItemClickListener(this);
    registerForContextMenu(listView);
    sequence = Info.getSequence(this);
    registerForContextMenu(listView);
    resetList();
    Menus.addIcons(this, (LinearLayout) findViewById(R.id.icons), R.menu.sequence_menu, this);    
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Info.saveSequence(this);
  }

  private void open(int position) {
    SequenceItem item = items.get(position);
    if ( item instanceof LegItem ) {
      RouteLeg leg = ((LegItem) item).leg.getLeg();
      BusActivities activities = new BusActivities(this);
      activities.showRoute(leg.getRStop1());
    }
  }
  @Override
  public void onItemClick(AdapterView<?> l, View view, int position, long id) {
    open(position);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     Menus.inflate(inflater, R.menu.sequence_menu, menu);
     HelpActivity.addItem(menu, this, Help.SEQUENCE);
     return true;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
      ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.sequence_context, menu);
  }
  
  private void resetList() {
    items = getSequenceItems(sequence, Info.routeManager(this));
    adapter = new ArrayAdapter<SequenceItem>(this, R.layout.list_item, items);
    listView.setAdapter(adapter);
    if ( sequence.getLegs().isEmpty()) {
      setTitle(R.string.empty_sequence);
    } else {
      setTitle( sequence.getLabel(Info.routeManager(this)));
    }
  }
  
  private void removeLast() {
    List<LegGroup> legs = sequence.getLegs();
    if ( ! legs.isEmpty()) {
      RouteLeg last = legs.get(legs.size()-1).getLeg();
      if ( last.getStop2() != null) {
        last.setStop2(null);
      } else {
        legs.remove(legs.size()-1);
      }
      resetList();
    }
  }
  private void removeFirst() {
    List<LegGroup> legs = sequence.getLegs();
    if ( ! legs.isEmpty()) {
      legs.remove(0);
      resetList();
    }
  }
  
  private int legIndex(int position) {
    int n = -1;
    for( int i = 0; i <= position; i++ ) {
      if ( items.get(i) instanceof LegItem) {
        n++;
      }
    }
    return n;
  }
  private void removeLeg(int position) {
    if ( items.get(position) instanceof LegItem) {
      int index = legIndex(position);
      if ( index >= 0 ) {
        sequence.getLegs().remove(index);
        resetList();
      }
    }
  }
  

  public boolean onItemSelected(int itemId) {
    boolean handled = false;
    switch(itemId) {
      case R.id.clear:
        sequence.getLegs().clear();
        resetList();
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
      case R.id.map:
        SequenceActivities.showMap(this, sequence);
        handled = true;
        break;
    }
    return handled ? true : false;
  }
   
  
  @Override
  public void onClick(View v) {
    onItemSelected(v.getId());
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return onItemSelected(item.getItemId());
  }
   
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    boolean handled = false;
    switch(item.getItemId()) {
      case R.id.open:
        open(info.position);
        handled = true;
        break;
      case R.id.remove:
        removeLeg(info.position);
        handled = true;
        break;
      default:
        break;
    }
    return handled;
  }
}