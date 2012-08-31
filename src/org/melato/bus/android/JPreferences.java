package org.melato.bus.android;

import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/** Provides java.util.prefs.Preferences interface to android.content.SharedPreferences 
 * 
 * @author Alex Athanasopoulos
 *
 */
public class JPreferences extends AbstractPreferences {
  private SharedPreferences a;
  private Editor editor;
  private static final String[] emptyArray = new String[0];

  
  private Editor editor() {
    if ( editor == null ) {
      editor = a.edit();
    }
    return editor;
  }
  public JPreferences(SharedPreferences a) {
    super(null, "");
    this.a = a;
  }

  @Override
  protected AbstractPreferences childSpi(String name) {
    return null;
  }

  @Override
  protected String[] childrenNamesSpi() throws BackingStoreException {
    return emptyArray;
  }

  @Override
  protected void flushSpi() throws BackingStoreException {
    if ( editor != null ) {
      editor.commit();
      editor = null;
    }
  }

  @Override
  protected String getSpi(String key) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String[] keysSpi() throws BackingStoreException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void putSpi(String name, String value) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void removeNodeSpi() throws BackingStoreException {
  }

  @Override
  protected void removeSpi(String key) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void syncSpi() throws BackingStoreException {
  }

  @Override
  public boolean getBoolean(String key, boolean defValue) {
    return a.getBoolean(key, defValue);
  }

  @Override
  public float getFloat(String key, float defValue) {
    return a.getFloat(key, defValue);
  }

  @Override
  public int getInt(String key, int defValue) {
    return a.getInt(key, defValue);
  }

  @Override
  public long getLong(String key, long defValue) {
    return a.getLong(key, defValue);
  }

  @Override
  public String get(String key, String defValue) {
    return a.getString(key, defValue);
  }

  @Override
  public void putBoolean(String key, boolean value) {
    editor().putBoolean(key, value);
  }

  @Override
  public void putFloat(String key, float value) {
    editor().putFloat(key, value);
  }

  @Override
  public void putInt(String key, int value) {
    editor().putInt(key, value);
  }

  @Override
  public void putLong(String key, long value) {
    editor().putLong(key, value);
  }

  @Override
  public void put(String key, String value) {
    editor().putString(key, value);
  }

  @Override
  public void remove(String key) {
    editor().remove(key);
  }
  
  
}
