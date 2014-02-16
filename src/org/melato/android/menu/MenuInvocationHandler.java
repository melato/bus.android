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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import org.melato.android.menu.MenuCapture.Item;

import android.view.MenuItem;

class MenuInvocationHandler implements InvocationHandler {
  private MenuItem menuItem;
  List<Item> items;
    
  public MenuInvocationHandler(List<Item> items) {
    super();
    this.items = items;
  }
  public MenuInvocationHandler() {
    menuItem = (MenuItem) Proxy.newProxyInstance(MenuItem.class.getClassLoader(),
        new Class[] { MenuItem.class },
        new MenuItemInvocationHandler(new Item()));
    
  }
  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {    
    if ( MenuItem.class.equals(method.getReturnType())) {
      // Capture the arguments of the add() method, which is the only method called by MenuInflater.
      if ( "add".equals(method.getName()) && args.length == 4) {        
        Item item = new Item();
        item.id = (Integer) args[1];
        item.title = args[3].toString();
        items.add(item);
        // return a fake menu item that captures more properties
        return (MenuItem) Proxy.newProxyInstance(MenuItem.class.getClassLoader(),
            new Class[] { MenuItem.class },
            new MenuItemInvocationHandler(item));
      }
      // Just in case, don't ever return a null MenuItem.
      return menuItem;
    }
    // for everything else, we don't care.
    return null;
  }

}
