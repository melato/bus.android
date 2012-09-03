package org.melato.bus.android.activity;

import java.util.ArrayList;
import java.util.List;

import org.melato.bus.android.R;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;


/**
 * Displays a list of labeled items.
 * @author Alex Athanasopoulos
 */
public class ItemsActivity extends BusActivity {
  protected List<Object> items = new ArrayList<Object>();
  private ItemAdapter adapter;
  
  public static String formatProperty( String label, Object value ) {
    String s = (value == null) ? "" : value.toString();
    return label + ": " + s;
  }
  
  public String formatProperty( int labelResourceId, Object value ) {
    return formatProperty( getResources().getString(labelResourceId), value);
  }
  
  static class Item {
    String label;
    Object value;
    public Item(String label, Object value) {
      super();
      this.label = label;
      this.value = value;
    }
    @Override
    public String toString() {
      return formatProperty(label, value);
    }
    public String getLabel() {
      return label;
    }
    public void setLabel(String label) {
      this.label = label;
    }
    public Object getValue() {
      return value;
    }
    public void setValue(Object value) {
      this.value = value;
    }    
    
  }
  
  public void addItem( Object item ) {
    items.add(item);
  }
  public void addItem( String label, Object value ) {
    items.add( new Item(label, value));
  }
  public void addText( String text ) {
    if ( text == null )
      text = "";
    items.add( text );
  }
  class ItemAdapter extends ArrayAdapter<Object> {
    TextView view;

    public ItemAdapter() {
      super(ItemsActivity.this, R.layout.list_item, items);
    }
  }

  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    adapter = new ItemAdapter();
    setListAdapter(adapter);
  }
  
  public void refresh() {
    adapter.notifyDataSetChanged();
  }
}