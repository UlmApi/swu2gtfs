#!/usr/bin/perl

use strict;
use warnings;
use utf8;
use XML::LibXML;

binmode STDOUT, ":encoding(utf8)";

my $parser = XML::LibXML->new();

 if (@ARGV) {
   print '"stop_id","stop_code","stop_name","location_type","parent_station","stop_lon","stop_lat"',"\n";
   foreach my $file (@ARGV) {
     process($file);
   }
 } else {
   print "Usage: ", __FILE__ , " [kml inputfile(s)]\n";
   print " outputs a complete stops.txt to stdout without checking for duplicates.\n";
 }

sub process {
 my $arg = shift;
 my $kml = $parser->parse_file($arg);

 # structure: delve into root->document->folder(Haltepunkte)
 # iterate over all folders here; each representing one stop
 # per stop-folder, each placemark is a sub-stop
 # each placemark has a node "description" nnnn01, with nnnn 
 #  specifying the ID of the whole stop and 01, 02… the sub-stop
 # each placemark has a Point with x,y,z coordinates
 # the last sub-stop marks the location for the whole stop

 foreach my $stopfolder ($kml->findnodes('/kml:kml/kml:Document/kml:Folder/kml:Folder')) {
    
    my $stop_code = $stopfolder->find('./kml:name');
    ( my $stop_name = $stop_code ) =~ s/.*, //;
    my $parent_station = "";
    my $lat;
    my $lon;
    my $stop_id;

    foreach my $stoppoint ($stopfolder->findnodes('./kml:Placemark')) {
      my $stop_id = $stoppoint->find('./kml:description');
         $stop_id =~ s/OLIFID: /900/;
      
      $parent_station = substr ($stop_id,0,7);

      my $coords = $stoppoint->find('./kml:Point/kml:coordinates');
      $lat = $coords;
         $lat =~ s/.*,(.*),.*/$1/;
      $lon = $coords;
         $lon =~ s/(.*),.*,.*/$1/;
    
      print "$stop_id,\"$stop_code\",\"$stop_name\",0,$parent_station,$lon,$lat\n";
    }
    print "$parent_station,\"$stop_code\",\"$stop_name\",1,,$lon,$lat\n";   
 }
}
