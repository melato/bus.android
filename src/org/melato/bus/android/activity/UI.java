package org.melato.bus.android.activity;

import org.melato.bus.android.R;

import android.content.Context;
import android.widget.TextView;

public class UI {
  static void highlight(TextView view, boolean isHighlighted) {
    Context context = view.getContext();
    if ( isHighlighted ) {
      view.setBackgroundColor(context.getResources().getColor(R.color.list_highlighted_background));
      view.setTextColor(context.getResources().getColor(R.color.list_highlighted_text));
    } else {
      view.setBackgroundColor(context.getResources().getColor(R.color.list_background));
      view.setTextColor(context.getResources().getColor(R.color.list_text));
    }
  }

}
