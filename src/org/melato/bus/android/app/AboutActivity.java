package org.melato.bus.android.app;

import org.melato.bus.android.R;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class AboutActivity extends HelpActivity {
  
  @Override
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
  public boolean onCreateOptionsMenu(Menu menu)
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.about_menu, menu);
    //HelpActivity.addItem(menu, this, R.string.terms_of_use);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if ( item.getItemId() == R.id.terms ) {
      HelpActivity.showHelp(this, R.string.eula, R.string.terms_of_use);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
