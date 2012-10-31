package org.melato.bus.android.app;

import org.melato.bus.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

public class HelpActivity extends Activity {
  
  protected void setHelpText(TextView view) {
    int helpId = getIntent().getIntExtra(KEY_ID, R.string.help_default);
    view.setText(helpId);
  }
  public static final String KEY_ID = "help_id";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.help);
    TextView helpView = (TextView) findViewById(R.id.help);
    setHelpText(helpView);
    helpView.setMovementMethod(new ScrollingMovementMethod());    
  }
  
  public static void showHelp(Context context, int stringId) {
    Intent intent = new Intent(context, HelpActivity.class);
    intent.putExtra(KEY_ID, stringId);
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
  public static void addItem(Menu menu, Context context, int helpId) {
    MenuItem item = menu.add(R.string.help);
    item.setOnMenuItemClickListener(new HelpListener(context, helpId));
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.help_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if ( item.getItemId() == R.id.about ) {
      startActivity( new Intent(this, AboutActivity.class));
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  
  
}
