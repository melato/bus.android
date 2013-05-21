package org.melato.bus.android.activity;

import org.melato.bus.model.Schedule.ScheduleFactory;
import org.melato.bus.model.ScheduleId;

import android.content.Context;

public class ScheduleUtilities {
  public static String getScheduleName(Context context, ScheduleId scheduleId) {
    return new AndroidScheduleNames(context).getScheduleName(scheduleId);
  }

  public static String getScheduleName(Context context, ScheduleFactory schedule) {
    return new AndroidScheduleNames(context).getScheduleName(schedule);
  }

  public static String getDaysName(Context context, int days) {
    return new AndroidScheduleNames(context).getDaysName(days);
  }
}
