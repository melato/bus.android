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
