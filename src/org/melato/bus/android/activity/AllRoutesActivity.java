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

import org.melato.android.app.HelpActivity;
import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.android.db.SqlRouteStorage;
import org.melato.bus.model.Agency;
import org.melato.bus.model.RouteGroup;
import org.melato.bus.model.RouteManager;
import org.melato.util.Transliteration;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * Displays the list of all routes
 * @author Alex Athanasopoulos
 *
 */
public class AllRoutesActivity extends RoutesActivity implements OnClickListener {
  private RouteGroup[] all_groups;
  private EditText editView;
  private Agency agency;
  private static Transliteration transliteration;
  
  class TextListener implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
        int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
      String text = s.toString();
      int position = findPosition(text);
      if ( position >= 0 ) {
        ListView listView = getListView();
        listView.setSelectionFromTop(position, 0);
      }
    }    
  }

  public void setAgency(String agencyName) {
    RouteManager routeManager = activities.getRouteManager();
    agency = routeManager.getAgency(agencyName);
    if ( agency != null) {
      setTitle(agency.getLabel());
    }
    List<RouteGroup> groups = RouteGroup.group(routeManager.getRoutesForAgency(agencyName));
    all_groups = groups.toArray(new RouteGroup[0]);
    setRoutes(all_groups);
  }
  
  protected void initializeRoutes() {    
    String agencyName = Info.getDefaultAgencyName(this);
    setAgency(agencyName);
  }
    
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.all_routes);
    editView = (EditText) findViewById(R.id.routeFilter);
    editView.addTextChangedListener(new TextListener());
    ImageButton agencies = (ImageButton) findViewById(R.id.agencies);
    if ( agencies != null) {
      agencies.setOnClickListener(this);
    }
    //MenuCapture.addIcons(this, (LinearLayout) findViewById(R.id.icons), R.menu.all_routes_menu, this);    
  }
  
  @Override
  protected void onResume() {    
    super.onResume();
    String agencyName = Info.getDefaultAgencyName(this);
    if ( agencyName != null && agency != null && ! agencyName.equals(agency.getName())) {
      initializeRoutes();
    }
  }

  private String transliterateString(String text) {
    if ( transliteration == null) {
      SqlRouteStorage storage = (SqlRouteStorage) Info.routeManager(this).getStorage();
      String map = storage.getTransliteration();
      if ( map == null) {
        map = "";
      }
      transliteration = new Transliteration(map);
    }
    return transliteration.map(text);
  }
  private int findPosition( String text ) {
    if ( text != null ) {
      text = text.trim();
    }
    if ( text.length() == 0 )
      text = null;
    if ( text == null ) {
      return -1;
    }
    text = text.toUpperCase();
    text = transliterateString(text);
    for( int i = 0; i < all_groups.length; i++ ) {
      if ( all_groups[i].getTitle().startsWith(text)) {
        return i;
      }      
    }
    return -1;
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu)
  {
     MenuInflater inflater = getMenuInflater();
     inflater.inflate(R.menu.all_routes_menu, menu);
     HelpActivity.addItem(menu, this, Help.ALL);
     return true;
  }

  private void showAgencies() {
    Intent intent = new Intent(this, AgenciesActivity.class);
    intent.putExtra(Keys.SELECTOR, true);
    startActivityForResult(intent, REQUEST_AGENCY);      
  }
  
  private void selectAgency(String agencyName) {    
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if ( requestCode == REQUEST_AGENCY && resultCode == RESULT_OK) {
      String agencyName = data.getStringExtra(Keys.AGENCY);
      if ( agencyName != null) {
        selectAgency(agencyName);
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }
    
  public boolean onItemSelected(int itemId) {
    switch (itemId) {
      case R.id.agencies:
        showAgencies();
        return true;
      default:
        return false;
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if ( onItemSelected(item.getItemId()) ) {
      return true;
    } else {
      return super.onOptionsItemSelected(item);
    }
  }

  @Override
  public void onClick(View v) {
    onItemSelected(v.getId());
  }
  
}