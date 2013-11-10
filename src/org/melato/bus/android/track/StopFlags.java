package org.melato.bus.android.track;

import org.melato.bus.transit.StopDetails;

public class StopFlags {
  /**
   * we need 3 bits for each flag:  (unknown=0, yes=3, no=2) x 2 flags (seat,cover)
   */
  public static final int FLAG_KNOWN_SEAT = 0x20;
  public static final int FLAG_SEAT = 0x40;
  public static final int FLAG_KNOWN_COVER = 0x80;
  public static final int FLAG_COVER = 0x100;
  public static int seatFlag(Boolean hasSeat) {
    if ( hasSeat == null ) {
      return 0;
    }
    return FLAG_KNOWN_SEAT | (hasSeat ? FLAG_SEAT : 0);
  }
  public static int coverFlag(Boolean hasCover) {
    if ( hasCover == null ) {
      return 0;
    }
    return FLAG_KNOWN_COVER | (hasCover ? FLAG_COVER : 0);
  }
  public static Boolean hasSeat(int flags) {
    if ( (flags & FLAG_KNOWN_SEAT) != 0) {
      return (flags & FLAG_SEAT) != 0 ? Boolean.TRUE : Boolean.FALSE;
    } else {
      return null;
    }
  }
  public static Boolean hasCover(int flags) {
    if ( (flags & FLAG_KNOWN_COVER) != 0) {
      return (flags & FLAG_COVER) != 0 ? Boolean.TRUE : Boolean.FALSE;
    } else {
      return null;
    }
  }
  public static int getFlags(StopDetails stop) {
    return coverFlag(stop.getCover()) | seatFlag(stop.getSeat());    
  }
}
