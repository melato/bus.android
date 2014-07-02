package org.melato.bus.android;

import java.util.Locale;

import org.melato.bus.android.activity.Pref;
import org.melato.log.Log;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;

public class LanguageManager {
  private static LanguageManager instance;
  private Context context;
  private Locale locale;
  
  public static LanguageManager getInstance(ContextWrapper context) {
    if ( instance == null ) {
      instance = new LanguageManager(context);
    }
    return instance;
  }

  private LanguageManager(ContextWrapper app) {
    context = app.getBaseContext();    
  }
  
  /** Set our own language, independently of the system language. */
  public void initLanguage() {
    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

    Resources resources = context.getResources(); 
    Configuration config = resources.getConfiguration();

    String lang = settings.getString(Pref.LANG, "");
    Log.info("language settings= " + lang + " config=" + config.locale.getLanguage());
    this.locale = config.locale;
    if (! "".equals(lang) && ! config.locale.getLanguage().equals(lang))
    {
      Locale locale = new Locale(lang);
      config.locale = locale;
      Locale.setDefault(locale);
      resources.updateConfiguration(config, resources.getDisplayMetrics());
    }
  }
  
  public void updateLocale(String lang) {
    Locale locale = "".equals(lang) ? this.locale : new Locale(lang);
    Resources resources = context.getResources(); 
    Configuration config = resources.getConfiguration();
    config.locale = locale;
    resources.updateConfiguration(config, resources.getDisplayMetrics());    
  }
}
