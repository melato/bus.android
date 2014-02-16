package org.melato.android.menu;

import android.view.View;
import android.widget.Toast;

/** Displays a tooltip, using a short Toast. */
public class ToolTipListener implements View.OnLongClickListener {
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

