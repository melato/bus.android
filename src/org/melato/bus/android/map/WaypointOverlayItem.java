package org.melato.bus.android.map;

import org.melato.gpx.Waypoint;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

public class WaypointOverlayItem extends OverlayItem {
	private Waypoint waypoint;
	
	static GeoPoint getGeoPoint(Waypoint p) {		
		return new GeoPoint( (int) (p.getLat() * 1E6), (int)(p.getLon()*1E6));		
	}
	
	public WaypointOverlayItem(Waypoint waypoint) {
		super( getGeoPoint(waypoint), null, waypoint.name);
		this.waypoint = waypoint;
	}

	public Waypoint getWaypoint() {
		return waypoint;
	}
	

}
