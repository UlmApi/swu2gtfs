package de.uulm.datalove.swu2gtfs;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class tripsWriter {
	
	public tripsWriter(HashMap<String, Route> routes) {
		System.out.print("Writing trips.txt ");

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
			System.out.println("\ntrips.txt written");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
