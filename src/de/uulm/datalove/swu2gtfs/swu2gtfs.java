package de.uulm.datalove.swu2gtfs;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;


public class swu2gtfs {
	
	public static int kurse  = 0;
	public static int routen = 0;
	
	protected static HashMap<String, Route> routes = new HashMap<String, Route>(22);

	/**
	 * @param args
	 * provide path to directory (or directories) with all your files
	 */
	public static void main(String[] args) {
		
		new Parser(args, routes);				
				
	    for( String name: routes.keySet() )
	    {
	       Route currentRoute = routes.get(name);
	       routen++;
	       
	       HashMap<String, trip> trips = currentRoute.trips();
	       kurse = kurse + trips.size();
	    }

		System.out.println(routen + " Routen mit " + kurse + " Kursen angelegt.");
		
		writeGtfsStopTimes();
		writeGtfsTrips();
		
	}
	
	
	
	public static void writeGtfsStopTimes() {
		
		System.out.print("Schreibe stop_times.txt ");
		
		// Erste Zeile: Spaltenueberschriften schreiben
		StringBuffer outputBuffer = new StringBuffer("trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled\n");

		// Request ALL the routes! \o/
	    for( String name: routes.keySet() )
	    {
	       Route currentRoute = routes.get(name);
	       // Extrahiere die Trip-Hashmap aus der aktuellen Route
	       HashMap<String, trip> trips = currentRoute.trips();

	       // Iteriere ueber alle Trips der Route
	       for (String cTidentifier: trips.keySet()) {
	    	   // ... und fuege jeweils alle StopTimes hinzu
	    	   outputBuffer.append(trips.get(cTidentifier).getGtfsStopTimes());
	    	   }
	       
	       // Ein Puenktchen pro Route als visuelles Feedback.
	       System.out.print(".");
	    }

		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( "stop_times.txt" ) ) );
			out.write(outputBuffer.toString());
			if( out != null ) out.close();
			System.out.println("\nstop_times.txt geschrieben");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeGtfsTrips() {
		
		System.out.print("Schreibe trips.txt ");

		StringBuffer output = new StringBuffer("route_id,service_id,trip_id,trip_headsign,direction_id,block_id,shape_id\n");
		
	    for( String name: routes.keySet() )
	    {
	       Route currentRoute = routes.get(name);
	       
	       HashMap<String, trip> trips = currentRoute.trips();
	       
			for (String cTidentifier: trips.keySet()) {
				trip cT = trips.get(cTidentifier);
				output.append(currentRoute.route_id() + "," + cT.getService_id() + "," + cT.getTrip_id() + "," +
				cT.getTrip_headsign() + "," + cT.getDirection_id() + "," +cT.getBlock_id() + "," + cT.getShape_id() + "\n");
			}
	       System.out.print(".");
	    }
		

		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( "trips.txt" ) ) );
			out.write(output.toString());
			if( out != null ) out.close();
			System.out.println("\ntrips.txt geschrieben");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
