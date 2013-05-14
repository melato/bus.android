package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.bus.model.Schedule.DateScheduleFactory;
import org.melato.bus.model.Schedule.ScheduleFactory;
import org.melato.bus.model.Schedule.ScheduleIdScheduleFactory;
import org.melato.bus.model.ScheduleId;
import org.melato.util.DateId;

import android.content.Context;

public class ScheduleUtilities {
  private static int getFirstBit(int bitmap ) {
    if ( bitmap == 0 )
      return -1;
    for( int i = 0; i < 32; i++ ) {
      int bit = 1 << i;
      if ( (bitmap & bit) != 0 ) {
        return i;
      }
    }
    return -1;
  }
  private static int getLastBit(int bitmap ) {
    if ( bitmap == 0 )
      return -1;
    for( int i = 31; i >= 0; i-- ) {
      int bit = 1 << i;
      if ( (bitmap & bit) != 0 ) {
        return i;
      }
    }
    return -1;
  }
  private static boolean isContiguous( int bitmap, int first, int last ) {
    for( int i = first; i <= last; i++ ) {
      int bit = 1 << i;
      if ( (bitmap & bit) == 0 ) {
        return false;
      }
    }
    return true;    
  }

  private static final int[] DAY_RESOURCES = {
    R.string.days_Su,
    R.string.days_Mo,
    R.string.days_Tu,
    R.string.days_We,
    R.string.days_Th,
    R.string.days_Fr,
    R.string.days_Sa,
  };
  public static String getDayName(Context context, int bit) {
    if ( bit < 7 ) {
      return context.getResources().getString(DAY_RESOURCES[bit]);      
    }
    return "";
  }
  public static String getDaysName(Context context, int days) {    
    int first = getFirstBit(days);
    int last = getLastBit(days);
    if ( first == last ) {
      return getDayName(context, first);      
    }
    if ( days == 127 ) {
      return context.getResources().getString(R.string.days_all);
    }
    if ( isContiguous(days, first, last)) {
      return getDayName(context, first) + "-" + getDayName(context,last);
    }
    StringBuilder buf = new StringBuilder();
    for( int i = first; i <= last; i++ ) {
      int bit = 1 << i;
      if ( (days & bit) != 0 ) { 
        if ( i > first ) {
          buf.append(",");
        }
        buf.append(getDayName(context,i));
      }
    }
    return buf.toString();
  }
  
  public static String getScheduleName(Context context, ScheduleId scheduleId) {
    int days = scheduleId.getDays();
    if ( days == 0 ) {
      return DateId.toString(scheduleId.getDateId());
    }
    return getDaysName(context, days);
  }

  public static String getScheduleName(Context context, ScheduleFactory schedule) {
    if ( schedule instanceof ScheduleIdScheduleFactory ) {
      return getScheduleName(context, ((ScheduleIdScheduleFactory)schedule).getScheduleId());      
    }
    if ( schedule instanceof DateScheduleFactory ) {
      return context.getString(R.string.today);      
    }
    return schedule.toString();
  }


}
