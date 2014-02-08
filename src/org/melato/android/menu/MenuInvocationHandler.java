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
      if ( "add".equals(method.getName()) && args.length == 4) {
        Item item = new Item();
        item.id = (Integer) args[1];
        item.title = args[3].toString();
        items.add(item);
        return (MenuItem) Proxy.newProxyInstance(MenuItem.class.getClassLoader(),
            new Class[] { MenuItem.class },
            new MenuItemInvocationHandler(item));
      }
      return menuItem;
    }
    return null;
  }

}
