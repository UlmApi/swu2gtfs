package de.uulm.datalove.swu2gtfs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import de.uulm.datalove.diva2gtfs.entities.Route;
import de.uulm.datalove.diva2gtfs.utils.csv2routes;


public class swu2gtfs {

	public static int tripCounter  = 0;
	public static int routeCounter = 0;

	public static HashMap<String, Route> routes = new HashMap<String, Route>(22);


	public static void main(String[] args) {

		if (args != null) {
			

			File folder = new File(args[0]);
			
			if (folder.exists()) {
				File[] sourcefiles = folder.listFiles();
				new csv2routes(sourcefiles, routes);	
			}			

			for( String name: routes.keySet() )
			{
				Route currentRoute = routes.get(name);
				routeCounter++;

				tripCounter += currentRoute.getTripCount();
			}

			System.out.println(routeCounter + " Routes with " + tripCounter + " trips created.");

//						StringBuffer shapeOutput = new StringBuffer("shape_id,shape_pt_lon,shape_pt_lat,shape_pt_sequence\n");
//						new uniqueTripFinder(routes, shapeOutput); 
			new stoptimesWriter(routes);
			new tripsWriter(routes);

/*						try {
							BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream( "shapes.txt" ) ) );
							out.write(shapeOutput.toString());
							if( out != null ) out.close();
							System.out.println("\nshapes.txt written");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

*/
			System.out.println("Done."); 

		} else {
			System.err.println("Usage: pass a directory with TSV timetable data");
		}		
	}

}
