package de.uulm.datalove.diva2gtfs.entities;

import java.util.HashMap;


public class Route {
	
	private HashMap<String, Trip> trips;
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
		
		this.trips  = new HashMap<String, Trip>(100);
	}
	
	public void addTrip(String tripKey, Trip newTrip) {
		this.trips.put(tripKey, newTrip);
	}
	
	public HashMap<String, Trip> trips() {
		return trips;
	}
	
	public String route_id() {
		return route_id;
	}

	public void set_route_id(String rid) {
		this.route_id = rid;
	}
	
	public String agency_id() {
		return agency_id;
	}
	
	public void set_agency_id(String aid) {
		this.agency_id = aid;
	}

	public String short_name() {
		return route_short_name;
	}
	
	public void set_short_name(String sname) {
		this.route_short_name = sname;
	}

	public String long_name() {
		return route_long_name;
	}
	
	public void set_long_name(String lname) {
		this.route_long_name = lname;
	}

	public String route_desc() {
		return route_desc;
	}
	
	public void set_route_desc(String rdesc) {
		this.route_desc = rdesc;
	}

	public int route_type() {
		return route_type;
	}
	
	public void set_route_type(int rtype) {
		this.route_type = rtype;
	}

	public String route_url() {
		return route_url;
	}
	
	public void set_route_url(String rurl) {
		this.route_url = rurl;
	}

	public String route_color() {
		return route_color;
	}
	
	public void set_route_color(String rcol) {
		this.route_color = rcol;
	}

	public String route_text_color() {
		return route_text_color;
	}
	
	public void set_route_text_color(String rtcol) {
		this.route_text_color = rtcol;
	}
	
	public int getTripCount() {
		return this.trips.size();
	}

}
