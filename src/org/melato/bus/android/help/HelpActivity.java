package org.melato.bus.android.help;

import org.melato.bus.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

public class HelpActivity extends Activity {
  public static final String KEY_ID = "help_id";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help_layout);
    TextView helpView = (TextView) findViewById(R.id.help);
    int helpId = getIntent().getIntExtra(KEY_ID, R.string.help_default);
    helpView.setText(helpId);
  }
  
  public static void showHelp(Context context, int helpId) {
    Intent intent = new Intent(context, HelpActivity.class);
    intent.putExtra(KEY_ID, helpId);
    context.startActivity(intent);
  }
  static class HelpListener implements OnMenuItemClickListener {
    Context context;
    int helpId;
    public HelpListener(Context context, int helpId) {
      super();
      this.context = context;
      this.helpId = helpId;
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
      showHelp(context, helpId);
      return true;
    }
    
  }
  public static MenuItem addItem(Menu menu, Context context, int helpId) {
    MenuItem item = menu.add(R.string.help);
    item.setOnMenuItemClickListener(new HelpListener(context, helpId));
    return item;
  }
}
