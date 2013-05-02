package org.melato.bus.android.activity;

import java.util.List;

import org.melato.bus.android.R;
import org.melato.bus.android.activity.ScheduleActivity.TextColor;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HighlightAdapter <T> extends ArrayAdapter<T> {
  int selection;
  TextColor normalColor;
  TextColor selectedColor;
  
  public HighlightAdapter(Context context, List<T> items) {
    super(context, R.layout.list_item, items);
    selectedColor = new TextColor(context, R.color.list_highlighted_text, R.color.list_highlighted_background);
    normalColor = new TextColor(context, R.color.list_text, R.color.list_background);
  }  
  
  public void setSelection(int selection) {
    this.selection = selection;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    TextView view = (TextView) super.getView(position, convertView, parent);
    if ( position == selection ) {
      selectedColor.apply(view);
    } else {
      normalColor.apply(view);
    }
    return view;
  }
}
