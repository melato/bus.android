package org.melato.bus.android.map;

import org.melato.gpx.Waypoint;

import com.google.android.maps.OverlayItem;

public class WaypointOverlayItem extends OverlayItem {
	private Waypoint waypoint;
	
	public WaypointOverlayItem(Waypoint waypoint) {
		super( Maps.geoPoint(waypoint), null, waypoint.name);
		this.waypoint = waypoint;
	}

	public Waypoint getWaypoint() {
		return waypoint;
	}
	

}
