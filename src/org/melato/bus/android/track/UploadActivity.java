package org.melato.bus.android.track;

import java.io.File;

import org.melato.android.ui.PropertiesDisplay;
import org.melato.bus.android.R;
import org.melato.bus.android.track.StopsDatabase.Count;
import org.melato.bus.transit.TransitUpload;
import org.melato.mobile.HttpUtils;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class UploadActivity extends ListActivity {
  private PropertiesDisplay properties;
  private StopsDatabase.Count count;
  private MenuItem uploadMenu;
  
  class LoadTask extends AsyncTask<Void, Void, StopsDatabase.Count> {
    @Override
    protected Count doInBackground(Void... params) {
      StopsDatabase db = StopsDatabase.getInstance(UploadActivity.this);
      Count count = db.getCount();
      return count;
    }

    @Override
    protected void onPostExecute(Count count) {
      properties.add(properties.formatProperty(R.string.saved_stops, count.totalSize()));
      properties.add(properties.formatProperty(R.string.new_stops, count.newCount));
      setListAdapter(properties.createAdapter(R.layout.list_item));
      UploadActivity.this.count = count;
      enableMenu();
    }
  }

  private void enableMenu() {
    if ( uploadMenu != null && count != null && count.newCount > 0) {
      uploadMenu.setEnabled(true);        
    }    
  }
  class UploadTask extends AsyncTask<Count, Void, HttpUtils.Result> {
    @Override
    protected HttpUtils.Result doInBackground(Count... params) {
      File file = UploadInfo.createFile(UploadActivity.this);
      try {
        String url = UploadInfo.getUrl(UploadActivity.this);
        String clientId = UploadInfo.getClientId(UploadActivity.this);
        TransitUpload upload = new TransitUpload(url, clientId);
        HttpUtils.Result result = upload.uploadStops(file);
        if ( result.isSuccess()) {
          StopsDatabase.getInstance(UploadActivity.this).markUploaded();
        }
        return result;
      } finally {
        file.delete();
      }
    }

    @Override
    protected void onPostExecute(HttpUtils.Result result) {
      String msg = null;
      if ( result.isSuccess() ) {
        msg = getString(R.string.upload_ok);
      } else {
        if ( result.error != null && result.error.length() != 0) {
          msg = result.error;
        } else {
          msg = getString(R.string.upload_error);
        }
      }
      Toast.makeText(UploadActivity.this, msg, Toast.LENGTH_LONG).show();
    }
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {    
    super.onCreate(savedInstanceState);
    properties = new PropertiesDisplay(this);
    new LoadTask().execute((Void) null);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.upload_menu, menu);
    uploadMenu = menu.findItem(R.id.upload);
    enableMenu();
    //HelpActivity.addItem(menu,this, Help.STOP);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    boolean handled = false;
    switch (item.getItemId()) {
      case R.id.upload:
        if ( count != null) {
          new UploadTask().execute(count);
        }
        break;
      default:
        break;
    }
    return handled;
  }
  
  
  
 
  
}
