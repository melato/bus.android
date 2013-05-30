package org.melato.bus.android;

import java.util.Locale;

import org.melato.bus.android.activity.Pref;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

public class BusApplication extends Application {
  private Locale locale;

  private void updateLocale(Configuration config) {
    config.locale = locale;
    Locale.setDefault(locale);
    Resources resources = getBaseContext().getResources(); 
    resources.updateConfiguration(config, resources.getDisplayMetrics());
  }
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if ( locale != null ) {
      updateLocale(newConfig);
      /*
      newConfig.locale = locale;
      Locale.setDefault(locale);
      Resources resources = getBaseContext().getResources(); 
      resources.updateConfiguration(newConfig, resources.getDisplayMetrics());
      */
    }
  }
  @Override
  public void onCreate() {
    super.onCreate();
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

    Resources resources = getBaseContext().getResources(); 
    Configuration config = resources.getConfiguration();

    String lang = settings.getString(Pref.LANG, "");
    if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
    {
        locale = new Locale(lang);
        updateLocale(config);
    }
  }
}
