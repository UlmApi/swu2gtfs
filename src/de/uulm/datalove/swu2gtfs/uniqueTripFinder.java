package de.uulm.datalove.swu2gtfs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.uulm.datalove.diva2gtfs.entities.Route;
import de.uulm.datalove.diva2gtfs.entities.StopTime;
import de.uulm.datalove.diva2gtfs.entities.Trip;


public class uniqueTripFinder {

	private String headsign;
	
	public uniqueTripFinder(HashMap<String, Route> routes, StringBuffer output) {
		System.out.println("Trying to find unique trips.");
		
		// Request ALL the routes! \o/
	    for( String name: routes.keySet() )
	    {
	       // Extract hashmap with all trips of current route
	       HashMap<String, Trip> trips = routes.get(name).trips();

	       // Iterate over all trips of current route
	       for (String cTidentifier: trips.keySet()) {
	    	   Trip tTrip = trips.get(cTidentifier);
	    	   // … and find out whether the trip already has a shapeId set.
	    	   // if not, this will be the template to compare the rest of the trips with.
	    	   if (tTrip.getShape_id().isEmpty()) {
	    		   String shapeName = tTrip.getTrip_id();
	    		   int instances = 1;
	    		   System.out.print("New unique trip found, let's call it " + shapeName);
	    		   
	    		   String date = "20151214";
	    		   if(tTrip.sat() || tTrip.preholiday()) {
	    			   date = "20151219";
	    		   } else if (tTrip.sun()) {
	    			   date = "20151220";
	    		   }
	    		   Vector<StopTime> tTripStops = tTrip.getStopVector();
	    		   int tStart = tTripStops.get(0).getStop_id() / 100;
	    		   String tStartTime = tTripStops.get(0).getDeparture_time_24h();
	    		   int tDest = tTripStops.get(tTripStops.size()-1).getStop_id() / 100;
	    		   
	    		   // checking whether there is a connection with shape information on ding.eu
	    		   // if yes, the name is set.
    			   // afterwards, iterate over all the trips of the current route again
	    		   // and compare whether the stopVectors are identical. In which case the
	    		   // "new" trip also gets this name.
	    		   // Unfortunately, this creates many unnecessary ding.eu calls for all
	    		   // the routes without live system information (for example, E-Busse,
	    		   // which fall under the [in reality, nonexistent] route 16.

	    		   
	    		   if (shapeRequest(tStart, tDest, tStartTime, date, shapeName, output)) {
//	    		   if (true) {
	    			   tTrip.setShape_id(shapeName);
	    			   tTrip.setTrip_headsign(headsign);
		    		   for (String oTidentifier: trips.keySet()) {
		    			   if (trips.get(oTidentifier).getShape_id().isEmpty()) {
		    				   if (trips.get(oTidentifier).getStopIdVector().equals(tTrip.getStopIdVector())) {
		    					   trips.get(oTidentifier).setShape_id(shapeName);
		    					   trips.get(oTidentifier).setTrip_headsign(headsign);
		    					   System.out.print(".");
		    					   instances++;
		    				   }
		    			   }
		    		   }

	    		   } else {
	    			   //tTrip.setShape_id("missingShape");
		    		   
	    		   }
	    		   	    		   
	    		   
	    		   System.out.println(instances + " instances found.\n" +
	    		   		"Headsign for this trip is \"" + headsign +"\"");
	    		   
	    	   }
	       }
	       
	    }


		
	}
	
	public boolean shapeRequest(int startId, int destId, String time, String date, String shapeName, StringBuffer output)  {
		
		String request = "http://www.ding.eu/ding3/XSLT_TRIP_REQUEST2" +
		"?itdDate=" + date +
		"&itdTime=" + time + 
		"&itdTripDateTimeDepArr=dep" + 
		"&locationServerActive=1" +
		"&type_origin=stop" + 
		"&name_origin=" + startId +
		"&type_destination=stop"+
		"&name_destination=" + destId +
		"&coordOutputFormat=WGS84" + 
		"&coordListOutputFormat=STRING";
		
		System.out.println("Shape request for " + date + " at time " + time);
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
		Document doc = db.parse(new URL(request).openStream());

		XPath xpath = XPathFactory.newInstance().newXPath();
		
		// This specifies the XPath to the shapestring
		XPathExpression shpexpr = xpath.compile("//itdRoute[@changes='0']/itdPartialRouteList/itdPartialRoute/itdPathCoordinates/itdCoordinateString");
		// And this should take care of the means of transport. We'll grab the Headsign from that.
		XPathExpression dstexpr = xpath.compile("//itdRoute[@changes='0']/itdPartialRouteList/itdPartialRoute/itdMeansOfTransport");

		Object shpresult = shpexpr.evaluate(doc, XPathConstants.NODESET);
		Object dstresult = dstexpr.evaluate(doc, XPathConstants.NODE);
		NodeList shpnodes = (NodeList) shpresult;
		Node dstnode = (Node) dstresult;
		
		// No routes found.
		if (shpnodes.getLength() == 0) { 
			System.err.println("No trips from " + startId + " to " + destId + " found on " + date + " at " + time + ". Please fix this.\n");
			headsign = "";
			return false;			
		} else {
			// get the first item (itdCoordinateString), its Child Node (its content)
			// and look at its value. This is the coordinates string we need to transform.
				
		    // Taking care of the headsign.
		    headsign = dstnode.getAttributes().getNamedItem("destination").getNodeValue();
			
		    String coordinates = shpnodes.item(0).getChildNodes().item(0).getNodeValue();
		    coordinates = coordinates.replaceAll(".00000", "");
		    coordinates = coordinates.replaceAll(" ", ",");
		    
		    System.out.println(coordinates);
		    
		    String coordArray[] = coordinates.split(",");
		    int sequence = 0;
		    
		    for (int ca = 0; ca < coordArray.length; ca = ca+2) {
		    	if (coordArray[ca].length()>4 && coordArray[ca+1].length()>4) {
		    		String suffix1 = coordArray[ca].substring(coordArray[ca].length()-6, coordArray[ca].length());
			    	String prefix1 = coordArray[ca].substring(0, coordArray[ca].length()-6);
			    	String suffix2 = coordArray[ca+1].substring(coordArray[ca+1].length()-6, coordArray[ca].length());
			    	String prefix2 = coordArray[ca+1].substring(0, coordArray[ca+1].length()-6);
			    	
			    	// System.out.println(prefix1 + "." + suffix1 + ", " + prefix2 + "." + suffix2);
			    	
			    	output.append(shapeName + "," + prefix1 + "." + suffix1 + "," + prefix2 + "." + suffix2 + "," + sequence + "\n");

			    	sequence++;
		    		
		    	}
		    
		    }

		    return true;

		    
		}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		headsign = "";
		return false;
		
	}


}
