package org.melato.bus.android.app;

import org.melato.bus.android.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends HelpActivity {
  
  protected void setHelpText(TextView view) {
    String appVersion = "";
    PackageInfo packageInfo;
   try {
     packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0 );
     appVersion = packageInfo.versionName;
   } catch (NameNotFoundException e) {
   } 
    String text = String.format(getString(R.string.help_about), appVersion ); 
    view.setText(text);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }
  
}
