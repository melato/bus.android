/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013,2014, Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.client.IntAccessor;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class TimeIntervalFragment extends DialogFragment implements Dialog.OnShowListener, View.OnClickListener {
  private IntAccessor accessor;
  private EditText textView;
  private int title = R.string.wait_time;
  private int ok = R.string.ok;
  private AlertDialog dialog;
  
  public void setTitle(int title) {
    this.title = title;
  }
  public void setOk(int ok) {
    this.ok = ok;
  }
  public TimeIntervalFragment(IntAccessor accessor) {
    super();
    this.accessor = accessor;
  }
   
  
  @Override
  public void onClick(View v) {
    String text = textView.getText().toString().trim();
    int value = text.length() > 0 ? Integer.parseInt(text) : 0;
    try {
      accessor.setValue(value);
      dialog.dismiss();
    } catch (IllegalArgumentException e) {
      Toast.makeText(getActivity(), R.string.illegal_value, Toast.LENGTH_SHORT).show();
    }
  }

  @Override
  public void onShow(DialogInterface dialog) {
    Button button = this.dialog.getButton(AlertDialog.BUTTON_POSITIVE);
    button.setOnClickListener(this);
  }
  
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
      LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      View view = inflater.inflate(R.layout.integer_fragment, null);
      builder.setView(view);
      textView = (EditText) view.findViewById(R.id.integer);
      textView.setText(String.valueOf(accessor.getValue()));
      builder.setMessage(title);
      builder.setPositiveButton(ok, null);
      builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int id) {
                 }
             });
      dialog = builder.create();
      dialog.setOnShowListener(this);
      return dialog;
  }
}
