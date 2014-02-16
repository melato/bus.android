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
  public static boolean includeMenu;
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
      //button.setBackgroundColor(Color.TRANSPARENT);
      //button.setBackgroundColor(Color.BLACK);
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
