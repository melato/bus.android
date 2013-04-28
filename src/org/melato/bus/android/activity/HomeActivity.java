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
import org.melato.bus.android.app.BusPreferencesActivity;
import org.melato.bus.android.app.HelpActivity;
import org.melato.bus.android.map.RouteMapActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

/** The main activity checks for updates and launches the next activity. */
public class HomeActivity extends Activity implements OnItemClickListener {
  static class Item {
    int drawable;
    int text;
    public Item(int drawable, int text) {
      super();
      this.drawable = drawable;
      this.text = text;
    }
    
  }
  // references to our images
  private Item[] items = {
      new Item(R.drawable.recent, R.string.recent_routes),
      new Item(R.drawable.nearby, R.string.nearby_routes),
      new Item(R.drawable.map, R.string.map),
      new Item(R.drawable.all, R.string.all_routes),
      new Item(R.drawable.preferences, R.string.pref_menu),
      new Item(R.drawable.about, R.string.about),
  };
  
  
  /** Called when the activity is first created. */  
  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.home);
      GridView grid = (GridView) findViewById(R.id.gridView);
      grid.setAdapter(new ImageAdapter(this));
      grid.setOnItemClickListener(this);
  }

  void select(int position) {
    switch( items[position].drawable) {
      case R.drawable.nearby:
        startActivity(new Intent(this, NearbyActivity.class));
        break;
      case R.drawable.map:
        startActivity(new Intent(this, RouteMapActivity.class));
        break;
      case R.drawable.all:
        startActivity(new Intent(this, AllRoutesActivity.class));
        break;
      case R.drawable.recent:
        startActivity(new Intent(this, RecentRoutesActivity.class));
        break;
      case R.drawable.preferences:
        startActivity(new Intent(this, BusPreferencesActivity.class));
        break;
      case R.drawable.about:
        HelpActivity.showHelp(this, R.string.help_about, R.string.about, false, true);        
      default:
        break;
    }
  }
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    select(position);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.recent_routes_menu, menu);
     return true;
  }


  public class ImageAdapter extends BaseAdapter {
    private Context context;

    public ImageAdapter(Context c) {
        context = c;
    }

    public int getCount() {
        return items.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        Button button;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            button = new Button(context);
            button.setLayoutParams(new GridView.LayoutParams(128, 64));
            //button.setScaleType(ImageView.ScaleType.CENTER_CROP);
            button.setPadding(8, 8, 8, 8);
        } else {
            button = (Button) convertView;
        }
        Item item = items[position];
        button.setCompoundDrawablesWithIntrinsicBounds(0, item.drawable, 0, 0);
        button.setText(item.text);
        button.setBackgroundColor(Color.BLACK);
        button.setTextColor(Color.WHITE);
        button.setOnClickListener(new ButtonListener(position));
        return button;
    }
  }
  
  class ButtonListener implements OnClickListener {
    int pos;
    public ButtonListener(int pos) {
      super();
      this.pos = pos;
    }

    @Override
    public void onClick(View v) {
      select(pos);
    }
    
  }
}
