package org.melato.android.progress;

import org.melato.progress.ProgressHandler;

import android.app.Activity;

/**
 * ProgressHandler that calls its updateUI method on an activity's ui thread.
 * It also controls the frequency of the updates so they are not too fast for the ui. 
 * @author Alex Athanasopoulos
 *
 */
public abstract class ActivityProgressHandler implements ProgressHandler {
  protected Activity activity;
  protected int   position;
  protected int   limit;
  protected long  updateTime;
  protected String text = "";
  protected boolean cancelled;
  protected int DELAY = 50;
  protected boolean busy;  
  
  private Runnable uiRunnable = new UIRunnable();

  class UIRunnable implements Runnable {

    @Override
    public void run() {
      busy = false;
      updateUI();
    }
    
  }
  public ActivityProgressHandler(Activity activity) {
    super();
    this.activity = activity;
  }

  public void cancel() {
    cancelled = true;
  }

  protected abstract void updateUI();
  
  private void update() {
    if ( busy )
      return;
    long now = System.currentTimeMillis();
    if ( now - updateTime < DELAY )
      return;
    if ( ! activity.hasWindowFocus() )
      return;
    updateTime = now;
    busy = true;
    activity.runOnUiThread(uiRunnable);
  }
  @Override
  public void setPosition(int pos) {
    this.position = pos;
    update();
  }

  @Override
  public void setLimit(int limit) {
    this.limit = limit;
    update();
  }
  
  @Override
  public void setText(String text) {
    this.text = text;    
    update();
  }

  @Override
  public boolean isCanceled() {
    return cancelled;
  }
}  
