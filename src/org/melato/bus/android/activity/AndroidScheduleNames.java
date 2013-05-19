package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.bus.client.ScheduleNames;

import android.content.Context;

public class AndroidScheduleNames extends ScheduleNames {
  private Context context;
    
  public AndroidScheduleNames(Context context) {
    super();
    this.context = context;
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
  
  @Override
  public String getDayName(int day) {
    return context.getResources().getString(DAY_RESOURCES[day]);      
  }
  @Override
  public String getAllDaysName() {
    return context.getResources().getString(R.string.days_all);
  }
  @Override
  public String getTodayName() {
    return context.getString(R.string.today);      
  }
}
