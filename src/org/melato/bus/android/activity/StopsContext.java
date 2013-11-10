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

import org.melato.bus.android.R;
import org.melato.bus.android.track.StopFlags;
import org.melato.bus.android.track.StopsDatabase;
import org.melato.bus.client.Formatting;
import org.melato.bus.client.TrackContext;
import org.melato.bus.model.RStop;
import org.melato.bus.model.Route;
import org.melato.bus.model.Stop;
import org.melato.gps.Earth;
import org.melato.gps.PointTime;

import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StopsContext extends LocationContext {
  private TrackContext track;
  private int closestStop = -1;
  private boolean isSelected;
  private StopsAdapter adapter;
  private int markedIndex = -1;
  private StopsDatabase stopsDB;

  private ListActivity list;

  public void setRoute(Route route) {
    history.setRoute(route.getRouteId());
    track = history.getTrackContext();
    list.setListAdapter(adapter = new StopsAdapter());
    start();
  }
  
  public void setStop(RStop rstop) {
    if ( rstop != null) {
      markedIndex = rstop.getStopIndex();
    }
  }
  
  public StopsContext(ListActivity activity) {
    super(activity);
    this.list = activity;
    stopsDB = StopsDatabase.getInstance(activity);
  }

  @Override
  public void setLocation(PointTime point) {
    super.setLocation(point);
    if ( point != null) {
      track.setLocation(point);
      closestStop = track.getPathTracker().getNearestIndex();
    }
    adapter.notifyDataSetChanged();
    // scroll to the nearest stop, if we haven't done it yet.
    if ( ! isSelected && closestStop >= 0 ) {
      isSelected = true;
      list.setSelection(closestStop);
    }
  }
  
  public Stop[] getStops() {
    return track.getStops();
  }

  class StopsAdapter extends ArrayAdapter<Stop> {
    public StopsAdapter() {
      super(context, R.layout.stop_list_item, R.id.text, track.getStops()); 
    }

    private void setIcon(ImageView view, Boolean value, int yesId) {
      if ( value == null ) {
        view.setImageResource(R.drawable.unknown);
      } else if ( value ) {
        view.setImageResource(yesId);
      } else {
        view.setImageDrawable(null);
      }
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View view = super.getView(position, convertView, parent);
      TextView textView = (TextView) view.findViewById(R.id.text);
      ImageView seatView = (ImageView) view.findViewById(R.id.seat_icon);
      ImageView coverView = (ImageView) view.findViewById(R.id.cover_icon);
      Stop stop = track.getStops()[position];
      int flags = stop.getFlags();
      Integer localFlags = stopsDB.getFlags(stop.getSymbol());
      if ( localFlags != null ) {
        flags = localFlags;
      }
      setIcon(seatView, StopFlags.hasSeat(flags), R.drawable.seat);
      setIcon(coverView, StopFlags.hasCover(flags), R.drawable.cover);
      String text = stop.getName();
      PointTime here = getLocation();
      if ( here != null && closestStop == position ) {
        float straightDistance = Earth.distance(here, stop); 
        text += " " + Formatting.straightDistance(straightDistance);
      }
      if ( position == markedIndex ) {
        textView.setBackgroundColor(context.getResources().getColor(R.color.stop_background));
        textView.setTextColor(context.getResources().getColor(R.color.list_highlighted_text));
      } else {
        UI.highlight(textView, position == closestStop );        
      }
      textView.setText( text );
      return view;
    }
  }

}
