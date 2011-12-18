package de.uulm.datalove.swu2gtfs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import au.com.bytecode.opencsv.CSVReader;

public class Parser {

	private static String route_type;
	private static String route_no;
	private static String calendar;
	private static int intDirection;
	private static String direction;
	private static int startLine;
	private static String tripIdentifier;
	private static String hinweis;

	
	public Parser(String[] directories, HashMap<String, Route> routes) {
		if (directories.length > 0) {
			for (int i = 0; i < directories.length; i++) {
				File folder = new File(directories[i]);
				File[] listOfFiles = folder.listFiles();
				
				for (int j = 0; j < listOfFiles.length; j ++) {
					parseCSV(listOfFiles[j].getAbsolutePath(), routes);
				}
				
				
			}
		} else {
			System.err.println("Keinen Auslesepfad als Parameter angegeben. Breche ab.");
		}
	
	}
	
	public static void parseCSV(String csvPath, HashMap<String, Route> routes) {
		System.out.println("Verarbeite nun " + csvPath);
		try {
			
			// Dateinamen zerlegen. Uebliches Schema
			// 11001zR0
			// 11 Betriebszweig; 001 Liniennummer (mit 87 ergaenzen), R/H: Rueck/Hin, 0 Service_ID (0= Wochentag)
			
			Route currentRoute;
			int startColumn = 4; // erklaerung siehe unten bei der Abfrage.
			
			String route[] = csvPath.split("/");
			String routeName = route[route.length-1];
				
			route_type = (String) routeName.subSequence(0, 2);
				if (route_type.equals("11")) {
					route_type = "0";
				} else if (route_type.equals("10")){ route_type = "7"; }
			route_no = "87" + (String) routeName.substring(2,5);
			direction = routeName.substring(6,7);
				if (direction.equals("H")) { intDirection = 0; }
				else if (direction.equals("R")) { intDirection = 1; }
			calendar = (String) routeName.substring(7,8);
			System.out.println("Route type: " + route_type + ", route no: " + route_no + ", calendar: " + calendar + ", direction: " + direction); 
	
			if (Integer.parseInt(route_no)>87900) {
				startColumn = 3;
				calendar = "nightbus";
			}

			
			// Abfrage, ob diese Route bereits existiert
			
			if (routes.containsKey(route_no)) {
				currentRoute = routes.get(route_no);
			} else {
				currentRoute = new Route(route_no, "SWU", route_no.substring(2, 5), 
						route_no, "", Integer.parseInt(route_type), "", "", "");
				routes.put(route_no, currentRoute);				
			}
			
			// Parse the CSV contents 
			
			CSVReader reader = new CSVReader(new FileReader(csvPath), '\t');
			List<?> csvList = reader.readAll();
			
			String [] tripIndices = (String[]) csvList.get(0);
			
			// Lege die Spalte fest, in der die erste Abfahrt stattfindet. Falls Spalte 3
			// komplett leer ist (kommt vor), wird die startColumn erhoeht.
			// Handelt es sich um Nachtbusse (901, 902,...), beginnt der erste Trip
			// schon in Spalte 3!
			if (tripIndices[3].isEmpty()) {
				startColumn++;
			}
			
			// Dieser Block findet heraus, wo die erste Abfahrt startet und übernimmt
			// dies für den Rest der Zeiten im CSV
			for (int line = 3; line < 10; line ++) {
			   String [] currentLine = (String[]) csvList.get(line);
			   if (currentLine[0].equals("1")) {
				   startLine = line;
			   }
			}
			
			for (int column=startColumn; column<tripIndices.length; column++) {
				
				String[] tripStartId = tripIndices[column].split(":");
				tripIdentifier = route_no + direction + calendar + "-" + tripStartId[0] + tripStartId[1];
				trip newTrip = new trip(calendar , tripIdentifier, "", "", intDirection, "", "");
				int sequence = 1;
				
				for (int line = startLine; line < csvList.size(); line ++) {
					String [] currentLine = (String[]) csvList.get(line);
					if (!currentLine[column].contains("-") && !currentLine[column].contains("$")) {
						
						// Uhrzeit umformen: Erst das fuehrende Leerzeichen wegsplitten und den verbleibenden Rest
						// an den Punkten aufsplitten, Doppelpunkte einfuegen und :00 anhaengen.
						
						String splitTime[] = currentLine[column].split(" ");
						String cleanTime = splitTime[splitTime.length-1];
						splitTime = cleanTime.split("\\.");
						cleanTime = splitTime[0] + ":" + splitTime[1] + ":00";
						
						// Trip hinzufuegen
						newTrip.addStop(cleanTime, cleanTime, 
								9000000 + Integer.parseInt(currentLine[1]), sequence, "");
						sequence++;
					} 
				}
				
				
				// Wochentage eintueten
				
				String [] currentLine = (String[]) csvList.get(2);
				
				// Nur an Vorlesungstagen der uulm
				if (currentLine[column].toLowerCase().equals("su")) {
					newTrip.setUni(true);
					newTrip.setService_id("Su");
				} 
				
				// Nicht vor Feiertagen
				if (currentLine[column].toLowerCase().equals("na")) {
					newTrip.setNotpreholiday(true);
					newTrip.setService_id("Na");
				} 

				// Nur an Schultagen
				if (currentLine[column].toLowerCase().equals("ss")) {
					newTrip.setSchool(true);
					newTrip.setService_id("Ss");
				}
				
				// Nicht an Schultagen
				if (currentLine[column].toLowerCase().equals("sf")) {
					newTrip.setNoschool(true);
					newTrip.setService_id("Sf");
				}
								
				if (calendar.equals("0")) {
					// Mo-Fr
					
					newTrip.setMon(true);
					newTrip.setTue(true);
					newTrip.setWed(true);
					newTrip.setThu(true);
					newTrip.setFri(true);
										
					// Nur Freitags und vor Feiertagen
					if (currentLine[column].toLowerCase().equals("fa")) {
						newTrip.setMon(false);
						newTrip.setTue(false);
						newTrip.setWed(false);
						newTrip.setThu(false);
						newTrip.setFri(true);
						newTrip.setPreholiday(true);
						newTrip.setService_id("Fa");
					}

				} else if (calendar.equals("2")) {
					// Sa
					newTrip.setSat(true);
					
				} else if (calendar.equals("3")) {
					// So
					newTrip.setSun(true);
					
					// Sonntags, nicht vor Feiertagen
					if (currentLine[column].toLowerCase().equals("nb")) {
						newTrip.setNotpreholiday(true);
						newTrip.setService_id("Nb");
					}
					
					// Sonntags, nur vor Feiertagen
					if (currentLine[column].toLowerCase().equals("fb")) {
						newTrip.setPreholiday(true);
						newTrip.setService_id("Fb");
					}
				}
				
				if (Integer.parseInt(route_no)>87900) {
					newTrip.setPreholiday(true);
					newTrip.setService_id("nightbus");
				}

				currentRoute.addTrip(tripIdentifier + calendar, newTrip); }
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
