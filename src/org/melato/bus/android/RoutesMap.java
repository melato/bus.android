package org.melato.bus.android;

import org.melato.bus.model.RStop;
import org.melato.bus.plan.Sequence;

public interface RoutesMap {
  void showRoute(RStop rstop);
  void showSequence(Sequence sequence);
}
