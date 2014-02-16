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

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import android.view.Menu;
import android.view.MenuInflater;

/** Extracts information from a menu resource so that the menu items may be implemented as ImageButtons.
 * It uses MenuInflater with a fake Menu implementation that "captures" the menu items.
 * */  
public class MenuCapture {
  /** Represents the basic properties of a menu item. */
  public static class Item {
    public int id;
    public int icon;
    public String title;
    @Override
    public String toString() {
      return "Item [id=" + id + ", icon=" + icon + ", title=" + title + "]";
    }  
  }

  public static Item[] capture(MenuInflater inflater, int menuId) {
    List<Item> list = new ArrayList<Item>();
    inflater.inflate(menuId, (Menu) Proxy.newProxyInstance(Menu.class.getClassLoader(),
        new Class[] { Menu.class },
        new MenuInvocationHandler(list)));
    return list.toArray(new Item[0]);
  }

}
