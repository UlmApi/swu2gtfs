#!/usr/bin/perl

use strict;
use warnings;
use utf8;

 if (@ARGV) {
	print '"service_id","date","exception_type"', "\n";
	my $arg = shift;

	open (FILE, "<", "$arg") or die("Could not open inputfile: $!");

	my $line;
	foreach $line (<FILE>) {
		chomp $line;

		my $date = $line;
		   $date =~ s/([0-9]{8}),[0-9]*/$1/;
		my $tagesart = $line;
		   $tagesart =~ s/[0-9]{8},([0-9]*)/$1/;

		if ($tagesart == 10 || $tagesart == 50 || $tagesart == 80) {
			# Wochentag (0), Schule, Uni
			print "0,$date,1\n";
			print "Ss,$date,1\n";
			print "Su,$date,1\n";
			print "Na,$date,1\n";
		}
		if ($tagesart == 11 || $tagesart == 51 || $tagesart == 81) {
			# Wochentag, keine Schule, keine Uni
			print "0,$date,1\n";
			print "Sf,$date,1\n";
			print "Na,$date,1\n";
		}
		if ($tagesart == 12 || $tagesart == 52 || $tagesart == 82) {
			# Wochentag, Schule, keine Uni
			print "0,$date,1\n";
			print "Ss,$date,1\n";
			print "Na,$date,1\n";
		}
		if ($tagesart == 13 || $tagesart == 53 || $tagesart == 83) {
			# Wochentag, keine Schule, Uni
			print "0,$date,1\n";
			print "Sf,$date,1\n";
			print "Su,$date,1\n";
			print "Na,$date,1\n";
		}

		if ($tagesart == 80 || $tagesart == 81 || $tagesart == 82 || $tagesart == 83) {
			# Vorfeiertag, Nachtverkehr
			print "Yr,$date,1\n"; #stimmt das?
			print "Fb,$date,1\n";
			print "Fa,$date,1\n";
		}

		if ($tagesart == 50 || $tagesart == 51 || $tagesart == 52 || $tagesart == 53) {
			# Freitag, Nachtbusse Fr auf Sa
			print "Yr,$date,1\n";
			print "Fa,$date,1\n";
		}

		if ($tagesart == 60 || $tagesart == 61 || $tagesart == 62 || $tagesart == 68) {
			# Samstag
			print "2,$date,1\n";
		}
		if ($tagesart == 61) {
			# Heiligabend
			print "Yt,$date,1\n";
		}
		if ($tagesart == 62) {
			# Silvester
			print "Yu,$date,1\n";
		}

		if ($tagesart == 70) {
			# Sonntag, kein Vorfeiertag
			print "3,$date,1\n";
			print "Nb,$date,1\n";
		}
		if ($tagesart == 71) {
			# Nachtverkehr am Sonntag (Vorfeiertag)
			print "3,$date,1\n";
			print "Ys,$date,1\n";
		}
	}


 } else {
   print "Usage: ", __FILE__ , " [tagesartenkalender.csv]\n";
   print " outputs a complete calendar_dates.txt to stdout.\n";
 }
