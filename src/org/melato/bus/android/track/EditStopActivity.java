package org.melato.bus.android.track;

import org.melato.bus.android.R;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.model.Stop;
import org.melato.bus.transit.StopDetails;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class EditStopActivity extends Activity {
  private Stop stop;
  public static void editStop(Activity activity, Stop stop) {
    Intent intent = new Intent(activity, EditStopActivity.class);
    intent.putExtra(Keys.STOP, stop);
    activity.startActivityForResult(intent, 0);
  }
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    stop = (Stop) intent.getSerializableExtra(Keys.STOP);
    if ( stop == null) {
      finish();
      return;
    }
    setContentView(R.layout.edit_stop);
    StopsDatabase db = StopsDatabase.getInstance(this);
    StopDetails s = db.loadStop(stop.getSymbol());
    if ( s != null) {
      setBoolean(s.getSeat(), R.id.seat_yes, R.id.seat_no);
      setBoolean(s.getCover(), R.id.cover_yes, R.id.cover_no);
    }
    
  }
  
  private Boolean getBoolean(int groupId, int yesId, int noId) {
    RadioGroup group = (RadioGroup) findViewById(groupId);
    int checked = group.getCheckedRadioButtonId();
    if ( checked == yesId )
      return Boolean.TRUE;
    else if ( checked == noId )
      return Boolean.FALSE;
    else
      return null;    
  }
  
  private void setBoolean(Boolean value, int yesId, int noId) {
    if ( value != null) {      
      RadioButton button = (RadioButton) findViewById(value ? yesId : noId);
      button.setChecked(true);      
    }
  }
  
  public void save(View view) {
    StopDetails s = new StopDetails();    
    s.setSymbol(stop.getSymbol());
    s.setSeat(getBoolean(R.id.seat_group, R.id.seat_yes, R.id.seat_no));
    s.setCover(getBoolean(R.id.cover_group, R.id.cover_yes, R.id.cover_no));
    StopsDatabase db = StopsDatabase.getInstance(this);
    db.updateStop(s);
    setResult(RESULT_OK);
    finish();
    
  }
  public void cancel(View view) {
    setResult(RESULT_CANCELED);
    finish();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.edit_menu, menu);
    //HelpActivity.addItem(menu,this, Help.STOP);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch (item.getItemId()) {
      case R.id.remove:
        StopsDatabase db = StopsDatabase.getInstance(this);
        db.deleteStop(stop.getSymbol());
        setResult(RESULT_OK);
        finish();
        handled = true;
        break;
      default:
        break;
    }
    return handled;
  }
  
  
  
 
  
}
