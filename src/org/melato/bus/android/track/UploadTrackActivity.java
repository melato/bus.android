package org.melato.bus.android.track;

import java.io.File;

import org.melato.android.AndroidLogger;
import org.melato.android.ui.PropertiesDisplay;
import org.melato.android.util.Invokable;
import org.melato.bus.android.R;
import org.melato.bus.android.activity.AllRoutesActivity;
import org.melato.bus.android.activity.Keys;
import org.melato.bus.model.Route;
import org.melato.bus.transit.TransitUpload;
import org.melato.log.Log;
import org.melato.mobile.HttpUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/** Upload a track from a GPX file. */
public class UploadTrackActivity extends Activity implements OnItemClickListener {
  private PropertiesDisplay properties;
  private MenuItem uploadMenu;
  private ListView listView;
  private TextView textView;
  private File gpxFile;
  private Route route;
  private ArrayAdapter<Object> adapter;
  
  private void enableMenu() {
    if ( uploadMenu != null && route != null && gpxFile != null) {
      uploadMenu.setEnabled(true);        
    }    
  }
  class UploadTask extends AsyncTask<Void, Void, HttpUtils.Result> {
    @Override
    protected void onPreExecute() {
      setProgressBarIndeterminate(true);
      setProgressBarVisibility(true);
      textView.setText(null);
    }
    
    @Override
    protected HttpUtils.Result doInBackground(Void... params) {
      //File file = UploadInfo.createTrackFile(UploadTrackActivity.this, route.getRouteId(), gpxFile);
      File file = new File(Environment.getExternalStorageDirectory(), "a.tst");
      try {
        String url = UploadInfo.getUrl(UploadTrackActivity.this);
        String clientId = UploadInfo.getClientId(UploadTrackActivity.this);
        TransitUpload upload = new TransitUpload(url, clientId);
        HttpUtils.Result result = upload.uploadTracks(file);
        return result;
      } finally {
        //file.delete();
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
    }
  }
  
  class RouteSelector implements Invokable {
    @Override
    public String toString() {
      if ( route != null ) {
        return properties.formatProperty(R.string.route, route);
      } else {
        return getString(R.string.select_route);
      }
    }
    @Override
    public void invoke(Context context) {
      Intent intent = new Intent(context, AllRoutesActivity.class);
      intent.putExtra(Keys.SELECTOR, true);
      startActivityForResult(intent, 0);
    }    
  }  

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position,
      long id) {
    Object value = properties.getItem(position);
    if ( value instanceof Invokable) {
      ((Invokable)value).invoke(this);
    }
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if ( resultCode == RESULT_OK) {
      this.route = (Route) data.getSerializableExtra(Keys.ROUTE);
      adapter.notifyDataSetChanged();
      enableMenu();
    }
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {    
    super.onCreate(savedInstanceState);
    Log.setLogger(new AndroidLogger(this));
    requestWindowFeature(Window.FEATURE_PROGRESS);    
    setContentView(R.layout.upload);    
    listView = (ListView) findViewById(R.id.list);
    textView = (TextView) findViewById(R.id.text);
    Intent intent = getIntent();
    Uri uri = intent.getData();
    if ( uri != null) {
      String path = uri.getPath();
      gpxFile = new File(path);
    }
    if ( gpxFile == null) {
      finish();
    }
    properties = new PropertiesDisplay(this);
    properties.add(properties.formatProperty(R.string.file, gpxFile.getName()));
    properties.add(new RouteSelector());
    adapter = properties.createAdapter(R.layout.list_item, R.color.white, R.color.stop_link);
    listView.setAdapter(adapter);
    listView.setOnItemClickListener(this);    
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
        if ( gpxFile != null && route != null) {
          item.setEnabled(false);
          new UploadTask().execute();
        }
        break;
      default:
        break;
    }
    return handled;
  }
  
  
  
 
  
}
