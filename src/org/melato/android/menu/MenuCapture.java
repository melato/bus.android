package org.melato.android.menu;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.R;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

/** Extracts information from a menu resource so that the menu items may be implemented as ImageButtons.
 * It uses MenuInflater with a fake Menu implementation that "captures" the menu items.
 * Can also instantiate ImageButtons from the captured menu items.
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
  
  static class ContextListener implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
    private String label;
    private ImageButton view;
    private View.OnClickListener onClickListener;    
    

    public ContextListener(String label, ImageButton view,
        OnClickListener onClickListener) {
      super();
      this.label = label;
      this.view = view;
      this.onClickListener = onClickListener;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenuInfo menuInfo) {
      MenuItem item = menu.add(0, 1, 100, label);
      item.setEnabled(false);
      item.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
      onClickListener.onClick(view);
      return true;
    }        
  }
  
  static class ToolTipListener implements View.OnLongClickListener {
    private String label;
    

    public ToolTipListener(String label) {
      super();
      this.label = label;
    }
    
    @Override
    public boolean onLongClick(View v) {
      Toast.makeText(v.getContext(), label, Toast.LENGTH_SHORT).show();
      return true;
    }
  }
  
  public static Item[] capture(MenuInflater inflater, int menuId) {
    List<Item> list = new ArrayList<Item>();
    inflater.inflate(menuId, (Menu) Proxy.newProxyInstance(Menu.class.getClassLoader(),
        new Class[] { Menu.class },
        new MenuInvocationHandler(list)));
    return list.toArray(new Item[0]);
  }

  static LinearLayout addIconsLayout(Activity activity, LinearLayout view) {
    ViewGroup iconsContainer = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.icons_layout, null, false);
    LinearLayout icons = (LinearLayout) iconsContainer.findViewById(R.id.icons);
    iconsContainer.removeView(icons);
    view.addView(icons);
    return icons;
  }
  
  public static void addIcons(Activity activity, LinearLayout icons, int menuId, View.OnClickListener onClickListener) {
    MenuCapture.Item[] items = MenuCapture.capture(activity.getMenuInflater(), menuId);
    for( MenuCapture.Item item: items ) {
      ImageButton button = new ImageButton(activity);
      button.setId(item.id);
      button.setImageResource(item.icon);
      button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      //button.setText(item.title);
      if ( onClickListener != null) {
        button.setOnClickListener(onClickListener);
        //button.setOnCreateContextMenuListener(new ContextListener(item.title, button, onClickListener));        
        button.setOnLongClickListener(new ToolTipListener(item.title));        
      }
      icons.addView(button);
    }
  }
  public static void addIconView(Activity activity, LinearLayout parent, int menuId, View.OnClickListener onClickListener) {
    LinearLayout icons = addIconsLayout(activity, parent);
    addIcons(activity, icons, menuId, onClickListener);
  }

  
}
