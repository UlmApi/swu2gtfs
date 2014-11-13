package de.uulm.datalove.diva2gtfs.entities;

public class StopTime {

	private String arrival_time;
	private String departure_time;
	private String departure_time_24h;
	private int stop_id;
	private int stop_sequence;
	private String stop_headsign;
	
	public StopTime(String arrival_time, String departure_time, int stop_id, int stop_sequence, String stop_headsign, String departure_time_24h) {
		this.setArrival_time(arrival_time);
		this.setDeparture_time(departure_time);
		this.setStop_id(stop_id);
		this.setStop_sequence(stop_sequence);
		this.setStop_headsign(stop_headsign);
		this.setDeparture_time_24h(departure_time_24h);
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
	
	public void setDeparture_time_24h(String departure_time_24h) {
		this.departure_time_24h = departure_time_24h;
	}

	public String getDeparture_time_24h() {
		return departure_time_24h;
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
