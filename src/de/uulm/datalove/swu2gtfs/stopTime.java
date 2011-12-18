package de.uulm.datalove.swu2gtfs;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class stopTime {

	private String arrival_time;
	private String departure_time;
	private int stop_id;
	private int stop_sequence;
	private String stop_headsign;
	
	public stopTime(String arrival_time, String departure_time, int stop_id, int stop_sequence, String stop_headsign) {
		this.setArrival_time(arrival_time);
		this.setDeparture_time(departure_time);
		this.setStop_id(stop_id);
		this.setStop_sequence(stop_sequence);
		this.setStop_headsign(stop_headsign);		
	}

	public void setArrival_time(String arrival_time) {
		this.arrival_time = arrival_time;
	}

	public String getArrival_time() {
		return arrival_time;
	}

	public void setDeparture_time(String departure_time) {
		this.departure_time = departure_time;
	}

	public String getDeparture_time() {
		return departure_time;
	}
	
	public void setStop_id(int stop_id) {
		this.stop_id = stop_id;
	}

	public int getStop_id() {
		return stop_id;
	}

	public void setStop_sequence(int stop_sequence) {
		this.stop_sequence = stop_sequence;
	}

	public int getStop_sequence() {
		return stop_sequence;
	}

	public void setStop_headsign(String stop_headsign) {
		this.stop_headsign = stop_headsign;
	}

	public String getStop_headsign() {
		return stop_headsign;
	}

}
