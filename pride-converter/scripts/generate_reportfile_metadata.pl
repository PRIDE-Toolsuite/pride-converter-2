#!/usr/bin/perl

use warnings;
use strict;

# loop through the given arguments
my $interactive = 0;

if (@ARGV == 0) {
	printUsage();
	exit 0;
}

my $contacts = "";
my $title = "";
my $protocolName = "Test protocol";
my $steps = "<ProtocolStep>\n\t\t<userParam name=\"Some step\" value=\"great value\" />\n\t</ProtocolStep>";
my $instrumentName = "ESI IonTrap";
my $source = "<userParam name=\"some param\" value=\"some value\" />";
my $analyzer = "<userParam name=\"some param\" value=\"some value\" />";
my $detector = "<userParam name=\"some name\" value=\"some value\" />";

for my $param (@ARGV) {
	# check the different parameters for options
	if ($param eq "--interactive") {
		$interactive = 1;
		
		if ($interactive) {
			$contacts = getInput("Enter contact info (if it should be replaced): ", 1);
			$title = getInput("Title");
			$protocolName = getInput("Protocol name");
			$steps = getInput("Protocol steps (including <ProtocolStep>..</...)", 1);
			$instrumentName = getInput("Instrument name");
			$source = getInput("Source (parameters)", 1);
			$analyzer = getInput("Analyzer (parameters)", 1);
			$detector = getInput("Detector (parameters)", 1);
		}
		else {
			$title = "Test experiment";
			$contacts = "<contact>\n\t<name>Johannes</name>\n\t<institution>EBI</institution>\n\t<contactInfo>ebi</contactInfo>\n</contact>";
		}
		
		next;
	}
	
	# make sure the file exists
	if (!-f $param) {
		print "Error: " . $param. " could not be found\n";
		next;
	}
	
	my $tmp = $param;
	$tmp =~ s/^.*\///;
	
	print "Processing " . $tmp . "...\n";
	
	insertMetaData($param);
}

sub printUsage {
	print <<ENDUSAGE
Usage: perl generate_reportfile_metadata.pl [--interactive] (report files)

Parameters:
--interactive         Instead of bogus data the user
                      will be prompted to enter the
                      data for the different fields.

ENDUSAGE
}

sub insertMetaData {
	my ( $reportFile ) = @_;
	
	# open the file
	open(my $in, "<" . $reportFile) or die "Failed to open report file";
	# create the temporary file
	open(my $out, ">" . $reportFile . ".tmp") or die "Failed to create temporary file";
	
	# indicates whether we're in the contacts section
	my $newContactInfoWritten = 0;
	my $inContact = 0;
	
	
	
	# process the file line-by-line
	while (<$in>) {
		my $line = $_;
		
		# if there are new contacts set, ignore existing ones
		if (index($line, "<contact>") != -1) {
			$inContact = 1;			
		}
		
		# if we're in a contact and the new info hasn't been written, write it
		if ($contacts && $inContact && !$newContactInfoWritten) {
			print $out $contacts;
			$newContactInfoWritten = 1;
		}
		
		if (index($line, "</contact>") != -1) {			
			$inContact = 0;
			# ignore the line if there are new contacts
			next if ($newContactInfoWritten);
		}
		
		# ignore any contact info if it should be replaced
		next if ($contacts && $inContact);
		
		# title - only replaced in interactive mode
		if (index($line, "<Title>") != -1 && $interactive) {
			print $out "<Title>" . $title . "</Title>\n";
			next;
		}
		
		# short label
		if (index($line, "<ShortLabel>") != -1) {
			my $shortLabel = "Dummy short label";
			$shortLabel = getInput("Short label") if ($interactive);
			print $out "<ShortLabel>" . $shortLabel . "</ShortLabel>\n";
			next;
		}
		
		# protocol name
		if (index($line, "<ProtocolName>") != -1) {
			print $out "<ProtocolName>" . $protocolName . "</ProtocolName>\n";
			next;
		}
		
		# protocol
		if (index($line, "<ProtocolSteps/>") != -1) {
			print $out "<ProtocolSteps>\n\t" . $steps . "\n</ProtocolSteps>\n";
			next;
		}
		
		# sample name
		if (index($line, "<sampleName>") != -1) {
			my $sampleName = "Test sample";
			$sampleName = getInput("Sample name") if ($interactive);
			print $out "<sampleName>" . $sampleName . "</sampleName>\n";
			next;
		} 
		
		# instrument name
		if (index($line, "<instrumentName>") != -1) {
			print $out "<instrumentName>" . $instrumentName . "</instrumentName>\n";
			next;
		}
		
		# source
		if (index($line, "<source/>") != -1) {
			print $out "<source>". $source . "</source>\n";
			next;
		}
		
		# analyzer list
		if (index($line, "<analyzerList") != -1) {
			print $out "<analyzerList count=\"1\">\n";
			print $out "\t<analyzer>\n";
			print $out "\t\t" . $analyzer . "\n";
			print $out "\t</analyzer>\n</analyzerList>\n";
			
			next;
		}
		
		# detector
		if (index($line, "<detector") != -1) {
			print $out "<detector>\n\t". $detector . "\n</detector>\n";
			next;
		}
		
		print $out $line;
	}
	
	close($in);
	close($out);
	
	# copy the tmp file
	`mv ${reportFile}.tmp $reportFile`;
}

sub getInput {
	# get the parameters
	my ($message, $requireEmptyLine) = @_;
	
	print $message . ": ";
	
	my $done = 0;
	my $input = "";
	
	while (!$done) {
		my $line = <STDIN>;
		chomp($line);
		
		$input .= (($input ne "") ? "\n" : "") . $line if ($line ne "");
		
		$done = 1 unless($requireEmptyLine);
		
		$done = 1 if ($line eq "");
	}
	
	return $input;
}