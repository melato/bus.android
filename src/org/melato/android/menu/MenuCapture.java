package org.melato.android.menu;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.R;

import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MenuCapture {
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

  static LinearLayout addIconsLayout(Activity activity, LinearLayout view) {
    ViewGroup iconsContainer = (ViewGroup) activity.getLayoutInflater().inflate(R.layout.icons_layout, null, false);
    LinearLayout icons = (LinearLayout) iconsContainer.findViewById(R.id.icons);
    iconsContainer.removeView(icons);
    view.addView(icons);
    return icons;
  }
  
  public static void addIcons(Activity activity, LinearLayout icons, int menuId, OnClickListener onClickListener) {
    MenuCapture.Item[] items = MenuCapture.capture(activity.getMenuInflater(), menuId);
    for( MenuCapture.Item item: items ) {
      ImageButton button = new ImageButton(activity);
      button.setId(item.id);
      button.setImageResource(item.icon);
      button.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
      //button.setText(item.title);
      if ( onClickListener != null) {
        button.setOnClickListener(onClickListener);
      }
      icons.addView(button);
    }
  }
  public static void addIconView(Activity activity, LinearLayout parent, int menuId, OnClickListener onClickListener) {
    LinearLayout icons = addIconsLayout(activity, parent);
    addIcons(activity, icons, menuId, onClickListener);
  }

  
}
