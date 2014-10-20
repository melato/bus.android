/*
 * Modified by Alex Athanasopoulos from Android TimePickerDialog.java
 * 
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.melato.bus.android.activity;

import java.util.Date;

import org.melato.bus.android.R;
import org.melato.bus.model.Schedule;
import org.melato.log.Log;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ToggleButton;

/**
 * A dialog that prompts the user for the time of day using a {@link TimePicker}.
 *
 * <p>See the <a href="{@docRoot}guide/topics/ui/controls/pickers.html">Pickers</a>
 * guide.</p>
 */
public class TimeDialog extends AlertDialog
        implements OnClickListener, OnTimeChangedListener {

    /**
     * The callback interface used to indicate the user is done filling in
     * the time (they clicked on the 'Set' button).
     */
    public interface OnTimeSetListener {
      /**
       * 
       * @param view
       * @param arrive True for arrival time
       * @param timeInMinutes time or null for now
       */
        void onTimeSet(TimePicker view, boolean arrive, Integer timeInMinutes);
    }

    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";
    private static final String IS_24_HOUR = "is24hour";

    private final TimePicker mTimePicker;
    private final ToggleButton mArriveButton;
    private final OnTimeSetListener mCallback;

    int mInitialHourOfDay;
    int mInitialMinute;
    boolean mIs24HourView;

    /**
     * @param context Parent.
     * @param callBack How parent is notified.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public TimeDialog(Context context,
            OnTimeSetListener callBack,
            boolean arrive, Integer timeMinutes) {
        this(context, 0, callBack, arrive, timeMinutes);
    }

    /**
     * @param context Parent.
     * @param theme the theme to apply to this dialog
     * @param callBack How parent is notified.
     * @param hourOfDay The initial hour.
     * @param minute The initial minute.
     * @param is24HourView Whether this is a 24 hour view, or AM/PM.
     */
    public TimeDialog(Context context,
            int theme,
            OnTimeSetListener callBack,
            boolean arrive,
            Integer timeMinutes) {
        super(context, theme);
        mCallback = callBack;
        if ( timeMinutes == null ) {
          timeMinutes = Schedule.getTime(new Date());
        }
        mInitialHourOfDay = timeMinutes / 60;
        mInitialMinute = timeMinutes % 60;
        mIs24HourView = true;

        setIcon(0);
        setTitle(R.string.time_dialog_title);

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, themeContext.getText(R.string.time_set), this);
        setButton(BUTTON_NEGATIVE, themeContext.getText(R.string.timeDepartNow), this);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.time_dialog, null);
        setView(view);
        mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
        mArriveButton = (ToggleButton) view.findViewById(R.id.arriveButton);
        mArriveButton.setChecked(arrive);

        // initialize state
        mTimePicker.setIs24HourView(mIs24HourView);
        mTimePicker.setCurrentHour(mInitialHourOfDay);
        mTimePicker.setCurrentMinute(mInitialMinute);
        mTimePicker.setOnTimeChangedListener(this);
    }

    public void onClick(DialogInterface dialog, int which) {
      if ( mCallback != null ) {
        boolean arrive = mArriveButton.isChecked();
        mTimePicker.clearFocus();
        switch(which) {
        case BUTTON_POSITIVE:
          mCallback.onTimeSet(mTimePicker, arrive, mTimePicker.getCurrentHour() * 60 + 
              mTimePicker.getCurrentMinute());
          break;
        case BUTTON_NEGATIVE:
          mCallback.onTimeSet(mTimePicker, false, null); 
          break;
        default:
          break;
        }
      }
    }

    public void updateTime(int hourOfDay, int minutOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minutOfHour);
    }

    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        /* do nothing */
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(HOUR, mTimePicker.getCurrentHour());
        state.putInt(MINUTE, mTimePicker.getCurrentMinute());
        state.putBoolean(IS_24_HOUR, mTimePicker.is24HourView());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mTimePicker.setIs24HourView(savedInstanceState.getBoolean(IS_24_HOUR));
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(minute);
    }
}
