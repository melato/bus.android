package org.melato.bus.android.map;

import java.util.Collections;
import java.util.List;

import org.melato.gpx.Waypoint;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;

/**
 * A map overlay that contains waypoints.
 * @author Alex Athanasopoulos
 */
public class WaypointsOverlay extends ItemizedOverlay<WaypointOverlayItem> {
	private List<Waypoint> waypoints = Collections.emptyList();
	private Context context;
	
	public WaypointsOverlay(Drawable defaultMarker, Context context) {
		  super(boundCenterBottom(defaultMarker));
		  this.context = context;
	}

	public void setWaypoints(List<Waypoint> waypoints) {
		this.waypoints = waypoints;
		populate();
	}
	
	@Override
	protected WaypointOverlayItem createItem(int i) {
		return new WaypointOverlayItem(waypoints.get(i));
	}

	@Override
	public int size() {
		return waypoints.size();
	}
}
