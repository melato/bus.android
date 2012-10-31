package org.melato.bus.android.app;

import org.melato.bus.android.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class BusPreferencesActivity extends PreferenceActivity {
  @Override
  protected void onCreate( Bundle savedInstanceState ) 
  {
      super.onCreate( savedInstanceState );

      addPreferencesFromResource( R.layout.settings );
  }
}
