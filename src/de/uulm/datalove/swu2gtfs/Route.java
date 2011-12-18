package de.uulm.datalove.swu2gtfs;

import java.util.HashMap;
import java.util.Vector;

public class Route {
	
	private HashMap<String, trip> trips;
	private String route_id;			// required
	private String agency_id;			// optional
	private String route_short_name;	// required
	private String route_long_name;		// required
	private String route_desc;			// optional
	private int route_type;				// required
	private String route_url;			// optional
	private String route_color;			// optional
	private String route_text_color;	// optional
	


	public Route (String route_id, String agency_id, String route_short_name, 
			String route_long_name, String route_desc, int route_type, String route_url,
			String route_color, String route_text_color) {
		
		this.route_id = route_id;
		this.agency_id = agency_id();
		this.route_short_name = route_short_name;
		this.route_long_name = route_long_name;
		this.route_desc = route_desc;
		this.route_type = route_type;
		this.route_url = route_url;
		this.route_color = route_color;
		this.route_text_color = route_text_color;
		
		this.trips  = new HashMap<String, trip>(100);
	}
	
	public void addTrip(String tripKey, trip newTrip) {
		this.trips.put(tripKey, newTrip);
	}
	
	public HashMap<String, trip> trips() {
		return trips;
	}
	
	public String route_id() {
		return route_id;
	}
	
	public String agency_id() {
		return agency_id;
	}

	public String short_name() {
		return route_short_name;
	}

	public String long_name() {
		return route_long_name;
	}

	public String route_desc() {
		return route_desc;
	}

	public int route_type() {
		return route_type;
	}

	public String route_url() {
		return route_url;
	}

	public String route_color() {
		return route_color;
	}

	public String route_text_color() {
		return route_text_color;
	}

}
