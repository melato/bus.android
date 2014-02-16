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

import org.melato.android.menu.MenuCapture.Item;

import android.view.MenuItem;

class MenuItemInvocationHandler implements InvocationHandler {
  private Item item;  

  public MenuItemInvocationHandler(Item item) {
    super();
    this.item = item;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args)
      throws Throwable {    
    if ( MenuItem.class.equals(method.getReturnType())) {
      if ( "setIcon".equals(method.getName())) {
        item.icon = (Integer) args[0];
      }
      // MenuItem has a series of setters that return itself.
      // Therefore, we return the same MenuItem proxy
      return proxy;
    }
    // We don't care for any other methods. 
    return null;
  }

}
