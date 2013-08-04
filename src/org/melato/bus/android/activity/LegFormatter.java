package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.bus.client.Formatting;
import org.melato.bus.model.Schedule;
import org.melato.bus.plan.LegAdapter;

import android.content.Context;

public class LegFormatter {
  public static String label(LegAdapter leg, Context context) {
    StringBuilder buf = new StringBuilder();
    if ( ! leg.isWalk()) {
      buf.append(leg.getLabel());
      buf.append(" ");
      buf.append(Schedule.formatTime(leg.getStartTime() / 60));
      buf.append(" ");
      buf.append(leg.getFromName());
      buf.append(" -> ");
      buf.append(Schedule.formatTime(leg.getEndTime() / 60));
      buf.append(" ");
      buf.append(leg.getToName());
      int diffTime = leg.getDiffTime();
      if ( diffTime >= 0 ) {
        buf.append(" (");
        buf.append( context.getString(R.string.wait));
        buf.append(" ");
        buf.append(Schedule.formatDuration(diffTime));
        buf.append(")");
      }
    } else {
      buf.append(context.getString(R.string.walk));
      buf.append( " ");
      buf.append(Formatting.straightDistance(leg.getDistance()));
      buf.append(" ");
      buf.append(Schedule.formatDuration(leg.getDuration()));
    }
    return buf.toString();
  }
  
  
}
