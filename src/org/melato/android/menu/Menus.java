/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013,2014 Alex Athanasopoulos.  All Rights Reserved.
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
package org.melato.android.menu;

import org.melato.bus.android.R;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

/** Instantiates ImageButtons from a menu resource.
 * */  
public class Menus {
  public static boolean includeMenu = true;
  static LinearLayout addIconsLayout(Activity activity, LinearLayout view) {
    ViewGroup iconsContainer = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.icons_layout, null, false);
    LinearLayout icons = (LinearLayout) iconsContainer.findViewById(R.id.icons);
    iconsContainer.removeView(icons);
    view.addView(icons);
    return icons;
  }
  
  public static void addIcons(Activity activity, LinearLayout icons, int menuId, View.OnClickListener onClickListener) {
    /*
    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ) {
      return;
    }
    */
    MenuCapture.Item[] items = MenuCapture.capture(activity.getMenuInflater(), menuId);
    for( MenuCapture.Item item: items ) {
      ImageButton button = new ImageButton(activity);
      // If we make the color transparent, we lose the different button states (pressed, etc.)
      // button.setBackgroundColor(Color.TRANSPARENT);
      button.setId(item.id);
      button.setImageResource(item.icon);
      button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      if ( onClickListener != null) {
        button.setOnClickListener(onClickListener);
        button.setOnLongClickListener(new ToolTipListener(item.title));        
      }
      icons.addView(button);
    }
  }
  public static void addIconView(Activity activity, LinearLayout parent, int menuId, View.OnClickListener onClickListener) {
    LinearLayout icons = addIconsLayout(activity, parent);
    addIcons(activity, icons, menuId, onClickListener);
  }
  public static void inflate(MenuInflater inflater, int menuId, Menu menu) {
    if ( includeMenu ) {
      inflater.inflate(menuId,  menu);
    }
  }

  
}
