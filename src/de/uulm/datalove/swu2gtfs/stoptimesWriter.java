package de.uulm.datalove.swu2gtfs;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class stoptimesWriter {
	
	public stoptimesWriter(HashMap<String, Route> routes) {
		System.out.print("Writing stop_times.txt ");
		
		// First line: Write column headers
		StringBuffer outputBuffer = new StringBuffer("trip_id,arrival_time,departure_time,stop_id,stop_sequence,stop_headsign,pickup_type,drop_off_type,shape_dist_traveled\n");

		// Request ALL the routes! \o/
	    for( String name: routes.keySet() )
	    {
	       Route currentRoute = routes.get(name);
	       // Extract hashmap with all trips of current route
	       HashMap<String, trip> trips = currentRoute.trips();

	       // Iterate over all trips of current route
	       for (String cTidentifier: trips.keySet()) {
	    	   // … and append all StopTimes per trip
	    	   outputBuffer.append(trips.get(cTidentifier).getGtfsStopTimes());
	    	   }
	       
	       // One dot per finished route gives a little feedback to the user :)
	       System.out.print(".");
	    }

		
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( "stop_times.txt" ) ) );
			out.write(outputBuffer.toString());
			if( out != null ) out.close();
			System.out.println("\nstop_times.txt written");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
