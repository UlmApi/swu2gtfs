package de.uulm.datalove.diva2gtfs.entities;

import java.util.Vector;


public class Trip {

	private String service_id;
	private String trip_id;
	private String trip_headsign;
	private String trip_short_name;
	private String block_id;
	private String shape_id;
	private int direction_id;
	private Vector<StopTime> stopVector;
	private Vector<Integer> stopIdVector;
	private boolean mon = false;
	private boolean tue = false;
	private boolean wed = false;
	private boolean thu = false;
	private boolean fri = false;
	private boolean sat = false;
	private boolean sun = false;
	private boolean preholiday = false;
	private boolean notpreholiday = false;
	private boolean school = false;
	private boolean noschool = false;
	private boolean uni = false;
	
	public Trip(String service_id, String trip_id, 
			String trip_headsign, String trip_short_name, int direction_id, String block_id, String shape_id) {
		//System.out.println("Neuer Kurs angelegt, Linie " + route_id + ", Kurs " + trip_id);
		this.setService_id(service_id);
		this.setTrip_id(trip_id);
		this.setTrip_headsign(trip_headsign);
		this.setTrip_short_name(trip_short_name);
		this.setDirection_id(direction_id);
		this.setBlock_id(block_id);
		this.setShape_id(shape_id);
		stopVector = new Vector<StopTime>();
		stopIdVector = new Vector<Integer>();
	}
	
	
	public void addStop(String arrival_time, String departure_time, int stop_id, int stop_sequence, String stop_headsign, String departure_time_24h) {
		stopVector.add(new StopTime(arrival_time, departure_time, stop_id, stop_sequence, stop_headsign, departure_time_24h));
		stopIdVector.add(stop_id);
		//System.out.println(" Halt: " + stop_id + " " + departure_time);
	}

	public void setService_id(String service_id) {
		this.service_id = service_id;
	}

	public String getService_id() {
		return service_id;
	}

	public void setTrip_headsign(String trip_headsign) {
		this.trip_headsign = trip_headsign;
	}

	public String getTrip_headsign() {
		return trip_headsign;
	}

	public void setTrip_id(String trip_id) {
		this.trip_id = trip_id;
	}

	public String getTrip_id() {
		return trip_id;
	}

	public void setBlock_id(String block_id) {
		this.block_id = block_id;
	}

	public String getBlock_id() {
		return block_id;
	}

	public void setDirection_id(int direction_id) {
		this.direction_id = direction_id;
	}

	public int getDirection_id() {
		return direction_id;
	}

	public void setShape_id(String shape_id) {
		this.shape_id = shape_id;
	}

	public String getShape_id() {
		return shape_id;
	}

	public void setTrip_short_name(String trip_short_name) {
		this.trip_short_name = trip_short_name;
	}

	public String getTrip_short_name() {
		return trip_short_name;
	}


	public void setStopVector(Vector<StopTime> stopVector) {
		this.stopVector = stopVector;
	}


	public Vector<StopTime> getStopVector() {
		return stopVector;
	}
	
	public Vector<Integer> getStopIdVector() {
		return stopIdVector;
	}
	
	public void setMon(boolean mon) {
		this.mon = mon;
	}
	
	public boolean mon(){
		return mon;
	}
	
	public void setTue(boolean tue) {
		this.tue = tue;
	}
	
	public boolean tue() {
		return tue;
	}
	
	public void setWed(boolean wed) {
		this.wed = wed;
	}
	
	public boolean wed() {
		return wed;
	}
	
	public void setThu(boolean thu) {
		this.thu = thu;
	}
	
	public boolean thu() {
		return thu;
	}
	
	public boolean fri() {
		return fri;
	}
	
	public void setFri(boolean fri) {
		this.fri = fri;
	}
	
	public boolean sat() {
		return sat;
	}
	
	public void setSat(boolean sat) {
		this.sat = sat;
	}
	
	public boolean sun() {
		return sun;
	}
	
	public void setSun(boolean sun) {
		this.sun = sun;
	}
	
	public boolean preholiday() {
		return preholiday;
	}
	
	public void setPreholiday(boolean ph) {
		this.preholiday = ph;
	}
	
	public boolean uni() {
		return uni;
	}
	
	public void setUni(boolean uni) {
		this.uni = uni;
	}
	
	public boolean school() {
		return school;
	}
	
	public void setSchool(boolean school) {
		this.school = school;
	}
	
	public boolean noschool() {
		return noschool;
	}
	
	public void setNoschool(boolean noschool) {
		this.noschool = noschool;
	}
	
	public void setNotpreholiday(boolean notpreholiday) {
		this.notpreholiday = notpreholiday;
	}
	
	public boolean notpreholiday() {
		return notpreholiday;
	}
	
	
	public String getGtfsStopTimes() {
		String output = new String();

		Vector<StopTime> stops = this.getStopVector();
		
		for (int stopIterator = 0; stopIterator < stops.size() ; stopIterator++) {
			StopTime cS = stops.get(stopIterator);
			output = output + (this.getTrip_id() + "," + cS.getArrival_time() + "," + cS.getDeparture_time()
					+ "," + cS.getStop_id() + "," + cS.getStop_sequence() + "," + cS.getStop_headsign()
					+ ",,,\n");
		}

		return output;
	}

	@Override
	public String toString() {
		return this.getTrip_id() + " to " + this.getTrip_headsign() + " service " + this.getService_id();
	}
	
	

}
