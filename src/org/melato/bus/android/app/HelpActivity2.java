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
package org.melato.bus.android.app;

import org.melato.bus.android.Info;
import org.melato.bus.android.R;
import org.melato.bus.client.HelpItem;
import org.melato.bus.client.HelpStorage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.TextView;

/** Displays interlinked help items that are taken from the database. */
public class HelpActivity2 extends Activity {
  public static final String KEY_NAME = "help_name";
  public static final String KEY_NODE = "help_node";
  
  HelpItem getHelp() {
    HelpStorage db = (HelpStorage) Info.routeManager(this).getStorage();
    String name = getIntent().getStringExtra(KEY_NAME);
    if ( name != null) {
      return db.loadHelp(name);
    } else {
      int node = getIntent().getIntExtra(KEY_NODE, 0);
      if ( node != 0 ) {
        return db.loadHelp(node);
      }
    }
    Uri uri = getIntent().getData();
    if ( uri == null )
      return null;
    String scheme = uri.getScheme();
    if ( "help".equals(scheme)) {
      String path = uri.getSchemeSpecificPart();
      if ( path != null) {
        try {
          int node = Integer.parseInt(path);
          return db.loadHelp(node);
        } catch( NumberFormatException e) {          
        }
      }
    }
    return null;
  }
  
  public static void showHelp(Context context, String name) {
    Intent intent = new Intent(context, HelpActivity2.class);
    intent.putExtra(KEY_NAME, name);
    context.startActivity(intent);
  }
  
  protected void setHelpText(TextView view, HelpItem help) {
    if ( help != null) {
      Spanned s = Html.fromHtml(help.getText());
      view.setText(s);
    }
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help);
    HelpItem help = getHelp();
    if ( help != null) {
      setTitle(help.getTitle());
      TextView helpView = (TextView) findViewById(R.id.help);
      setHelpText(helpView, help);
      helpView.setMovementMethod(new LinkMovementMethod());    
    }
  }  
}
