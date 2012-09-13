package org.melato.android.progress;


import android.app.Activity;
import android.util.Log;
import android.view.Window;

/**
 * ProgressHandler that uses the built-in progress bar on an activity's title.
 * @author Alex Athanasopoulos
 *
 */
public class ProgressTitleHandler extends ActivityProgressHandler {
  private CharSequence activityTitle;
  private boolean replacedTitle;
  
  public ProgressTitleHandler(Activity activity) {
    super(activity);
    activity.requestWindowFeature(Window.FEATURE_PROGRESS);
  }
  
  /** Restore the activity's original title. */
  public void end() {
    activity.setProgressBarVisibility(false);
    activity.setProgress(10000);
    if ( replacedTitle ) {
      activity.setTitle(activityTitle);
    }
  }
  @Override
  public void updateUI() {
    if ( limit == 0 || position >= limit - 1 || isCanceled() ) {      
      Log.i( "melato.org", "end progress" );
      end();
    } else {
      activity.setProgressBarVisibility(true);
      if ( text != null ) {
        if ( ! replacedTitle ) {
          replacedTitle = true;
          activityTitle = activity.getTitle();
        }
        activity.setTitle(text);        
      }
      Log.i( "melato.org", "position=" + position + " limit=" + limit + " p=" + position / limit * 10000);
      activity.setProgress(Math.round(position * 10000f / limit) );
    }
  }
}
