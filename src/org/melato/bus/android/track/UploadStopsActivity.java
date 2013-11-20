package org.melato.bus.android.track;

import java.io.File;

import org.melato.android.AndroidLogger;
import org.melato.android.ui.PropertiesDisplay;
import org.melato.bus.android.R;
import org.melato.bus.android.track.StopsDatabase.Count;
import org.melato.bus.transit.TransitUpload;
import org.melato.log.Log;
import org.melato.mobile.HttpUtils;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class UploadStopsActivity extends Activity {
  private PropertiesDisplay properties;
  private StopsDatabase.Count count;
  private MenuItem uploadMenu;
  private ListView listView;
  private TextView textView;
  
  class LoadTask extends AsyncTask<Void, Void, StopsDatabase.Count> {
    @Override
    protected Count doInBackground(Void... params) {
      StopsDatabase db = StopsDatabase.getInstance(UploadStopsActivity.this);
      Count count = db.getCount();
      return count;
    }

    @Override
    protected void onPostExecute(Count count) {
      properties = new PropertiesDisplay(UploadStopsActivity.this);
      properties.add(properties.formatProperty(R.string.saved_stops, count.totalSize()));
      properties.add(properties.formatProperty(R.string.new_stops, count.newCount));
      listView.setAdapter(properties.createAdapter(R.layout.list_item));
      UploadStopsActivity.this.count = count;
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
    protected void onPreExecute() {
      setProgressBarIndeterminate(true);
      setProgressBarVisibility(true);
      textView.setText(null);
    }
    
    @Override
    protected HttpUtils.Result doInBackground(Count... params) {
      File file = UploadInfo.createFile(UploadStopsActivity.this);
      try {
        String url = UploadInfo.getUrl(UploadStopsActivity.this);
        Log.info("upload url: " + url);
        String clientId = UploadInfo.getClientId(UploadStopsActivity.this);
        TransitUpload upload = new TransitUpload(url, clientId);
        HttpUtils.Result result = upload.uploadStops(file);
        if ( result.isSuccess()) {
          StopsDatabase.getInstance(UploadStopsActivity.this).markUploaded();
        }
        return result;
      } finally {
        file.delete();
      }
    }

    @Override
    protected void onPostExecute(HttpUtils.Result result) {
      setProgressBarVisibility(false);
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
      textView.setText(msg);
      //Toast.makeText(UploadActivity.this, msg, Toast.LENGTH_LONG).show();
      new LoadTask().execute((Void) null);
    }
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {    
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_PROGRESS);  
    Log.setLogger(new AndroidLogger(this));
    setContentView(R.layout.upload);
    listView = (ListView) findViewById(R.id.list);
    Log.info("list: " + listView);
    textView = (TextView) findViewById(R.id.text);
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
          item.setEnabled(false);
          new UploadTask().execute(count);
        }
        break;
      default:
        break;
    }
    return handled;
  }
  
  
  
 
  
}
