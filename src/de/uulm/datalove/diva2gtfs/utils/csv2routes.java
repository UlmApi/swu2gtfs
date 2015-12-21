package de.uulm.datalove.diva2gtfs.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.uulm.datalove.diva2gtfs.entities.Route;
import de.uulm.datalove.diva2gtfs.entities.Trip;

import au.com.bytecode.opencsv.CSVReader;

public class csv2routes {

	private static String route_type;
	private static String route_no;
	private static String calendar;
	private static int intDirection;
	private static String direction;
	private static int startLine;
	private static String tripIdentifier;
	
	private static int operator_prefix = 87;
	private static String operator_name = "SWU";
	private static int bzw_tram = 11;
	private static int bzw_bus = 10;
	
	public csv2routes(File[] sourcefile, HashMap<String, Route> routes) {
		
		//HashMap<String, Route> routes = new HashMap<String, Route>(22);
		
		for (int i = 0; i<sourcefile.length; i++) {
			System.out.println("Working on " + sourcefile[i].getAbsolutePath());
			
			// Splitting up the file name. Typical pattern:
			// 11001zR0
			// 11 Betriebszweig; 001 Liniennummer (append 87 to the start), R/H: Rueck/Hin, 0 Service_ID (0= Weekday, …)
			
			Route currentRoute;
			int startColumn = 4; // This is explained farther below

			String routeName = sourcefile[i].getName(); // last part, i.e. the file name
			route_type = (String) routeName.subSequence(0, 2);
			// 11 is tram service, i.e. route_type 0. Otherwise buses, i.e. route_type 7
			if (route_type.equals(bzw_tram)) {
				route_type = "0"; 
			} else if (route_type.equals(bzw_bus)){ route_type = "7"; }
			
			// Affix operator prefix to the front. SWU Linie 1 becomes 87001 to conform with DING naming practice.
			route_no = operator_prefix + (String) routeName.substring(2,5);
			direction = routeName.substring(6,7);
				if (direction.equals("H")) { intDirection = 0; }
				else if (direction.equals("R")) { intDirection = 1; }
			calendar = (String) routeName.substring(7,8);
			System.out.println("Route type: " + route_type + ", route no: " + route_no + ", calendar: " + calendar + ", direction: " + direction); 

			// Check whether this route already exists
		
			if (routes.containsKey(route_no)) {
				currentRoute = routes.get(route_no);
			} else {
				currentRoute = new Route(route_no, operator_name, route_no.substring(2, 5), 
					route_no, "", Integer.parseInt(route_type), "", "", "");
				routes.put(route_no, currentRoute);				
			}
		
			// Parse the CSV contents 
		
			CSVReader reader;
			try {
				reader = new CSVReader(new FileReader(sourcefile[i].getAbsolutePath()), '\t');
				List<?> csvList = reader.readAll();
		
				String [] tripIndices = (String[]) csvList.get(0);
		
				// Define column which indicates the first stop on a trip. If column 3 is
				// completely empty (which happens), startColumn gets incremented.
				// This also is probably superfluous since 2012. TODO
				if (tripIndices[3].isEmpty()) {
					startColumn++;
				}
		
				// This block finds out in which line the first trip starts and uses it
				// for the rest of the CSV file. This is done by looking at the first column
				// and checking line for line until a "1" (for first stop) comes along.
				for (int line = 3; line < 10; line ++) {
					String [] currentLine = (String[]) csvList.get(line);
					if (currentLine[0].equals("1")) {
						startLine = line;
						break;
					}
				}
		
				for (int column=startColumn; column<tripIndices.length; column++) {
			
					String[] tripStartId = tripIndices[column].split(":");
					tripIdentifier = route_no + direction + calendar + "-" + tripStartId[0].subSequence(1, 3) + tripStartId[1];
					// Using the subSequence of the hours because since 2012 the schedule 
					// comes along with a leading whitespace.
					Trip newTrip = new Trip(calendar , tripIdentifier, "", "", intDirection, "", "");
					int sequence = 1;
			
					for (int line = startLine; line < csvList.size(); line ++) {
						String [] currentLine = (String[]) csvList.get(line);
						if (!currentLine[column].contains("-") && !currentLine[column].contains("$")) {
					
							// Uhrzeit umformen: Erst das fuehrende Leerzeichen wegsplitten und den verbleibenden Rest
							// an den Punkten aufsplitten, Doppelpunkte einfuegen und :00 anhaengen.
					
							String splitTime[] = currentLine[column].split(" ");
							String c24hTime;
							String cleanTime = splitTime[splitTime.length-1];
							splitTime = cleanTime.split("\\.");
							int hr = Integer.parseInt(splitTime[0]);
							if (hr > 23) hr = hr - 24;
							c24hTime = hr + ":" + splitTime[1] + ":00";
							cleanTime = splitTime[0] + ":" + splitTime[1] + ":00";
					
							// simpelster Fall: Ankunft ist gleich Abfahrt (genauer gehts nicht)
							String cleanDepartureTime = cleanTime;

					
							// überprüfen: Ist dies ein Halt mit Aufenthalt? Falls ja, steht in Spalte 3 „an (1)“
							if (currentLine[3].contains("an (1)")) {
						
								// Zeilenzaehler inkrementieren und die naechste Zeile holen, um die Abfahrtzeit zu bekommen
								line++;
								String [] nextLine = (String[]) csvList.get(line);
						
								// wie oben
								splitTime = nextLine[column].split(" ");
								cleanDepartureTime = splitTime[splitTime.length-1];
								splitTime = cleanDepartureTime.split("\\.");
								cleanDepartureTime = splitTime[0] + ":" + splitTime[1] + ":00";

							} 
							
							// Halt zum Trip hinzufuegen: Arrival time, departure time, vierstellige OLIF aus der zweiten
							// Spalte (Index 1) um zwei Stellen nach links geschoben (mal 100) plus fuehrende 900,
							// darauf dann das Haltepunktsuffix (vierte Spalte/Index 3) etc.
							newTrip.addStop(cleanTime, cleanDepartureTime, 
									900000000 + Integer.parseInt(currentLine[1])*100 + Integer.parseInt(currentLine[3]),  
									sequence, "", c24hTime);
							sequence++;
						} 
					}
			
			
					// Wochentage eintueten
			
					String [] currentLine = (String[]) csvList.get(2);
			
					// Nur an Vorlesungstagen der uulm
					if (currentLine[column].toLowerCase().contains("su")) {
						// newTrip.setUni(true);
						newTrip.setService_id("Su");
					} 
			
					// Nicht vor Feiertagen
					if (currentLine[column].toLowerCase().contains("na")) {
						// newTrip.setNotpreholiday(true);
						newTrip.setService_id("Na");
					} 

					// Nur an Schultagen
					if (currentLine[column].toLowerCase().contains("ss") ||
						currentLine[column].toLowerCase().contains("rs")) {
							// newTrip.setSchool(true);
							newTrip.setService_id("Ss");
					}
			
					// Nicht an Schultagen
					if (currentLine[column].toLowerCase().contains("sf") ||
						currentLine[column].toLowerCase().contains("rf")) {
						// newTrip.setNoschool(true);
						newTrip.setService_id("Sf");
					}
							
					if (calendar.equals("0")) {
						// Mo-Fr
				        newTrip.setService_id("0");
//						newTrip.setMon(true);
//						newTrip.setTue(true);
//						newTrip.setWed(true);
//						newTrip.setThu(true);
//						newTrip.setFri(true);
//									
						// Nur Freitags und vor Feiertagen
						if (currentLine[column].toLowerCase().contains("fa") ||
								currentLine[column].toLowerCase().contains("yr")) {
//							newTrip.setMon(false);
//							newTrip.setTue(false);
//							newTrip.setWed(false);
//							newTrip.setThu(false);
//							newTrip.setFri(true);
//							newTrip.setPreholiday(true);
							newTrip.setService_id("Fa");
						}
						
						// Nachtbus Fr auf Sa
						if (currentLine[column].toLowerCase().contains("yr")) {
							newTrip.setService_id("Yr");
						}

					} else if (calendar.equals("2")) {
						// Sa
						newTrip.setSat(true);
						newTrip.setService_id("2");
						
						// 24.12. to 25.12.
						if (currentLine[column].toLowerCase().contains("yt")) {
							newTrip.setService_id("Yt");
						}
						
						// 31.12. to 01.01.
						if (currentLine[column].toLowerCase().contains("yu")) {
							newTrip.setService_id("Yu");
						}
				
					} else if (calendar.equals("3")) {
						// So
						newTrip.setSun(true);
						newTrip.setService_id("3");
						// Sonntags, nicht vor Feiertagen
						if (currentLine[column].toLowerCase().contains("nb")) {
//							newTrip.setNotpreholiday(true);
							newTrip.setService_id("Nb");
						}
				
						// Sonntags, nur vor Feiertagen
						if (currentLine[column].toLowerCase().contains("fb") ||
							currentLine[column].toLowerCase().contains("ys")) {
							newTrip.setPreholiday(true);
							newTrip.setService_id("Fb");
						}
						
					}
			
//					if (Integer.parseInt(route_no)>87900) {
//						newTrip.setPreholiday(true);
//						newTrip.setService_id("nightbus");
//					}

					currentRoute.addTrip(tripIdentifier + calendar, newTrip); 
				}

				reader.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		
	}
	
}
