# swu2gtfs

This piece of.. code intends to make use of time table information provided by the Stadtwerke Ulm (SWU), which is the transport authority responsible for most of Ulm's public transit system. As of now, there is no standardized interface to get hold of said time table information, so we use the CSV exports that are created anyways for some poor person typesetting said files into pocket time tables. With some degree of adaptation, it should be possible to use this code for any transit authority using DIVA by mentzDV. Some functions still rely on EFA, also by mentzDV

I tried to comment rather liberally, but am a poor programmer, so both comments and variables are a wild mixture of German and English.

## usage

Run it in java and provide a path to a directory filled to the brim with time table CSV files (and nothing else). Information about the route names will be expected through the file name. More documentation about the table layout, naming conventions and so on is available in the transport section of the ulmapi.de wiki system.

```
javac -cp ".:./../lib/opencsv-2.3.jar" de/uulm/datalove/swu2gtfs/swu2gtfs.java
java -cp ".:./../lib/opencsv-2.3.jar" de.uulm.datalove.swu2gtfs.swu2gtfs PATH_TO_CSV_FILES

```

Additionally, `tagesarten2gtfs.pl` and `kml2gtfs.pl` extract SWU's calendar information and KML stop definition.

## what it does

You'll end up with three files: `shapes.txt`, `trips.txt` and `stop_times.txt`. All of them should be valid GTFS files.

## what it doesn't

You will still need to provide the rest of all the necessary files: 

* `agency.txt` and `routes.txt`: Shouldn't be a problem 
* `stops.txt`: Can be extracted from the KMZ files provided on ulmapi.de through the `kml2gtfs.pl` script
* `calendar.txt`: Is pretty static. Right now, "0" is weekdays, "2" saturdays and "3" sundays. The rest of the schedule (Ss, Sf, Fa, etc.) goes according to the plain text notes in the CSV files. Refer to ulmapi.de as to what they mean. The `tagesarten2gtfs.pl` is a quick hack using a CSV file with SWU's Tagesarten to create a `calendar_dates.txt`
* `frequencies.txt`: Requires manual work: Compare the CSV to the printed schedule, delete the recurring trips before parsing and manually insert according entries into frequencies.txt. Or disregard this completely and only use the `calendar_dates.txt` data (current approach)

## wishlist
* automatic recognition of recurring trips and managing those into frequencies.txt

## further reading
* GTFS specification: https://code.google.com/intl/de-DE/transit/spec/transit_feed_specification.html

## changelog
* 2011-12-18 first published version
* 2012-08-24 added method to compare trips and group them when the follow the same route (for shapefiles)
* 2012-08-25 requesting XML output from DING.eu and extracting route shapes for shapes.txt
* 2012-09-04 also taking destination IDs from the XML output into account and using it for trip headsign information
* 2012-09-14 Bugfix: Mixed up lat and long for shapes.txt output made route shapes appear halfway across the globe :>
* 2013-02-18 included OLIF suffixes in order to match every bus trip to one specific „Haltepunkt“. The DING.eu output is no longer used due to license issues; this version depends on SWU data only and is not able to provide shapefiles.
* 2014-11-13 ADDED: `tagesarten2gtfs.pl` and `kml2gtfs.pl`
* 2015-12-06 ADDED: Forgotten utils subdirectory
* 2015-12-06 CHANGED: Slight alterations to work with 2015/16 schedule files temporarily
