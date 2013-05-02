/*-------------------------------------------------------------------------
 * Copyright (c) 2012,2013 Alex Athanasopoulos.  All Rights Reserved.
 * alex@melato.org
 *-------------------------------------------------------------------------
 * This file is part of Athens Next Bus
 *
 * Athens Next Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Athens Next Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Athens Next Bus.  If not, see <http://www.gnu.org/licenses/>.
 *-------------------------------------------------------------------------
 */
package org.melato.bus.android.activity;

import org.melato.bus.android.R;
import org.melato.bus.plan.SequenceInstance;
import org.melato.bus.plan.SequenceInstance.LegInstance;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

/**
 * Displays a sequence instance:  An instance schedule at a particular time.
 * @author Alex Athanasopoulos
 */
public class SequenceInstanceActivity extends ListActivity {
  public static final String KEY_INSTANCE = "org.melato.bus.android.instance";
  private SequenceInstance instance;

  public SequenceInstanceActivity() {
  }
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    instance = (SequenceInstance) getIntent().getSerializableExtra(KEY_INSTANCE);
    if ( instance == null) {
      finish();
    }
    setListAdapter(new ArrayAdapter<LegInstance>(this, R.layout.list_item, instance.getLegInstances()));
  }  
}