package org.melato.bus.android.activity;

import org.melato.bus.model.Route;

public interface ColorScheme {
  int getColor(Route route);
  int getBackground(Route route);
}
