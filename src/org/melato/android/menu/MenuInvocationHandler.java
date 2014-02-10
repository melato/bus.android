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
