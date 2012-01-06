#!/usr/bin/perl

use strict;
use warnings;

# -------------------------------------------- 
# This tool converts a given Mascot csv file   
# into the quantitative text file format        
# expected by PRIDE Converter.                 
#                                              
# To generate such a csv file use Mascot's "Export"
# function and select "CSV" as output format.
# In the next screen adapt the following options:
# 1.) Adapt the first options (f.e. significance 
#     threshold etc.). 
# 2.) De-select all "Search Information" options
# 3.) For "Protein Hit Information" select only the
#     "Protein quantitation" option.
# 4.) For "Peptide Match Information" select only 
#     the "Sequence" and "Peptide quantitation"
#     option.

# ------------------------------------------------

my %labelCvParams = (
	"114" => "PRIDE\tPRIDE:0000114\tiTRAQ reagent 114",
	"115" => "PRIDE\tPRIDE:0000115\tiTRAQ reagent 115",
	"116" => "PRIDE\tPRIDE:0000116\tiTRAQ reagent 116",
	"117" => "PRIDE\tPRIDE:0000117\tiTRAQ reagent 117",
 	"126" => "PRIDE\tPRIDE:0000126\tiTRAQ reagent 126",
 	"127" => "PRIDE\tPRIDE:0000127\tiTRAQ reagent 127",
 	"128" => "PRIDE\tPRIDE:0000128\tiTRAQ reagent 128",
 	"129" => "PRIDE\tPRIDE:0000129\tiTRAQ reagent 129",
 	"130" => "PRIDE\tPRIDE:0000130\tiTRAQ reagent 130",
 	"131" => "PRIDE\tPRIDE:0000131\tiTRAQ reagent 131",
 	"unknown" => "!! ENTER MISSING LABEL CV_PARAM !!"
);

# ------------------------------------------------
#                CODE STARTS HERE
# ------------------------------------------------

# if there are no parameters passed show the usage
if (@ARGV == 0) {
	printUsage();
	exit 0;
}

# loop through the files
for my $filename (@ARGV) {
	# make sure the file exists
	if (!-f $filename) {
		print "Error: '" . $filename . "' does not exist\n";
		next;
	}
	
	my $tmp = $filename;
	$tmp =~ s/^.*\///;
	
	print "Processing file " . $tmp . "...\n";
	
	# convert the file
	convertMascotCsvFile($filename);
}


# -------------------------------------------------
#                  SUBROUTINES
# -------------------------------------------------

sub printUsage {
	print <<ENDUSAGE;
Usage: perl convert_mascot_csv_to_quant.pl (path to csv file)

For information how to create a required Mascot csv file
see the header of this file.
ENDUSAGE
}

sub convertMascotCsvFile {
	# save the filename
	my ($filename) = @_;
	
	# parse the file
	my $parsingResult = parseMascotFile($filename);
	
	my %fieldNames = %{${$parsingResult}[0]};
	my %proteins = %{${$parsingResult}[1]};
	my %peptides = %{${$parsingResult}[2]};
	
	print "File parsed successfully.\n";
	print "+-----------------------------------+\n";
	print "|      Input required meta-data     |\n";
	print "+-----------------------------------+\n";
	
	
	print "Please enter used quantitation method (f.e. iTRAQ): ";
	my $quantMethod = <STDIN>;
	chomp($quantMethod);
	
	print "Please enter quantitation level ('peptide' or 'protein'): ";
	my $quantLevel = <STDIN>;
	chomp($quantLevel);
	die "Invalid quantitation level entered" unless ($quantLevel eq "peptide" || $quantLevel eq "protein");
	
	# extract the quantitation labels from the used quant field names
	my %quantLabels = ( );
	
	for my $fieldName (keys(%fieldNames)) {
		if ($fieldName =~ /(\w+)\/(\w+)/) {
			$quantLabels{$1} = 1;
			$quantLabels{$2} = 1;
		}
		else {
			$quantLabels{$fieldName} = $1;
		}
	}
	
	# get the required additional info from the user
	my %subsampleReagents = ( );
	for my $label (sort(keys(%quantLabels))) {
		print "Please enter subsample number for label '" . $label . "' (1-99): ";
		my $input = <STDIN>;
		chomp($input);
		
		die "Invalid entry for subsample number." unless($input =~ /^\d+$/);
		
		$quantLabels{$label} = $input;
		$subsampleReagents{$input} = $label;
	}
	
	print " ----------------------------------- \n";
	
	# get the subsample descriptions
	my %descriptions = ( );
	for my $label (sort(keys(%quantLabels))) {
		print "Please enter description for subsample" . $quantLabels{$label} . " (" . $label . "): ";
		my $input = <STDIN>;
		chomp($input);
		$descriptions{$quantLabels{$label}} = $input;
	}	
	
	print " ----------------------------------- \n";
	print "\nRequired information gathered.\nWriting output file...";
	
	# write the file
	open (my $out, ">" . $filename . "_pride_quant.tsv") or die "Failed to open output file";
	
	print $out "quantification_method: " . $quantMethod ."\n";
	print $out "quantification_level: ". $quantLevel . "\n";
	print $out "intensity_measurement: absolute\n"; # TODO: fetch this data	
	
	for my $subsample (sort(keys(%descriptions))) {
		print $out "subsample" . $subsample . "_description: " . $descriptions{$subsample} . "\n";
	}
	for my $subsample (sort(keys(%subsampleReagents))) {
		# TODO: replace subsample reagent with CV PARAM
		print $out "subsample" . $subsample . "_reagent: " . getCvParamForLabel($subsampleReagents{$subsample}) . "\n";
	}
	
	# write the protein table
	print $out "\n";
	
	# get the protein fields
	my @fields = ( );
	
	for my $field (keys(%fieldNames)) {
		push @fields, $field if ($fieldNames{$field} eq "protein" || $fieldNames{$field} eq "both");
	}
	@fields = sort(@fields);
	
	# write the header
	print $out "protein_accession";
	for my $field (@fields) {
		print $out "\tprotein_" . getTableLabelForField($field, \%quantLabels);
	}
	print $out "\n";
	
	# write the data
	for my $accession (sort(keys(%proteins))) {
		print $out $accession;
		
		for my $field(@fields) {
			# get the protein's value for the field
			my $value = ${$proteins{$accession}}{$field};
			
			# ignore missing values
			$value = "" if ($value eq "---");
			
			# write the data
			print $out "\t" . $value;
		}
		
		print $out "\n";
	}
	
	# write the peptide table
	print $out "\n";
	
	# get the peptide fields
	@fields = ( );
	
	for my $field (keys(%fieldNames)) {
		push @fields, $field if ($fieldNames{$field} eq "peptide" || $fieldNames{$field} eq "both");
	}
	@fields = sort(@fields);
	
	# write the header
	print $out "protein_accession\tpeptide_sequence\tunique_identifier";
	for my $field (@fields) {
		print $out "\tpeptide_" . getTableLabelForField($field, \%quantLabels);
	}
	print $out "\n";
	
	# write the data
	for my $uid (sort(keys(%peptides))) {
		print $out ${$peptides{$uid}}{"accession"} . "\t" . ${$peptides{$uid}}{"sequence"} . "\t" . $uid;
		
		# write the fields
		for my $field (@fields) {
			my $value = ${$peptides{$uid}}{"fields"}{$field};
			# remove the "---" indicating missing values
			$value = "" unless(defined($value));
			$value = "" if ($value eq "---");
			print $out "\t" . $value;
		}
		
		print $out "\n";
	}
	
	close($out);
	
	print "Done.\nFile successfully written to '" . $filename . "_pride_quant.tsv'\n";
}

sub parseMascotFile {
	my ($filename) = @_;
	
	my $proteinHitsStarted = 0; # indicates whether the protein hits started
	my %header = ( );
	my %peptides = ( );
	my %proteins = ( );
	my %fieldNames = ( ); # the field names used for the quantitative data
	
	# process the file line by line
	open (my $in, "<" . $filename) or die "Failed to open " . $filename;
	
	while (<$in>) {
		my $line = $_;
		
		# remove any trailing whitespaces
		$line = trim($line);
		
		# ignore empty lines
		next if ($line eq "");
		
		# remove any "
		$line =~ s/"//g;
		
		# get the fields
		my @fields = split(/,/, $line);
		
		next if (@fields == 0);
		
		# check if it's the start of the protein table
		if ($fields[0] eq "Protein hits" && substr($fields[1], 0, 5) eq "-----") {
			$proteinHitsStarted = 1;
			next;
		}
		
		# ignore the line if the protein hits didn't start yet
		next unless ($proteinHitsStarted);
		
		# if the header hasn't been encoutered yet, parse it
		if (!defined($header{"prot_hit_num"})) {
			for (my $index = 0; $index < @fields; $index++) {
				$header{$fields[$index]} = $index;
			}
			
			next;
		}
		
		# process the line
		my $acc 	= $fields[$header{"prot_acc"}];
		my $query 	= $fields[$header{"pep_query"}];
		my $rank 	= $fields[$header{"pep_rank"}];
		my $sequence= $fields[$header{"pep_seq"}];
		
		die "Missing required field protein accession." unless (defined ($acc));
		die "Missing required field peptide query number." unless (defined ($query));
		die "Missing required field peptide rank." unless (defined ($rank));
		die "Missing required field peptide sequence." unless (defined ($sequence));
		
		# create the peptide
		my $pepId = $query . "_" . $rank;
		$peptides{$pepId}{"sequence"} = $sequence;
		$peptides{$pepId}{"accession"} = $acc;
		$peptides{$pepId}{"fields"} = { };
		
		# process the quantitative data
		#   The quantitative data starts after the last "normal" field (= with a header)
		my $fieldName = 0;
		my $inProteinData = 0;
		
		for (my $i = keys(%header); $i < @fields; $i++) {
			# the first field is always the field name
			if (!$fieldName) {
				$fieldName = $fields[$i];
				
				# check if the protein data started
				if ($fieldName eq "Quantitation summary for protein") {
					$inProteinData = 1;
					$fieldName = 0;
				}
				
				next;
			}
			
			# store the field name in the field names hash
			if (!$inProteinData) {
				$fieldNames{$fieldName} = "peptide" unless(defined($fieldNames{$fieldName}));
			}
			elsif ($inProteinData && defined($fieldNames{$fieldName})) {
				$fieldNames{$fieldName} = "both";
			}
			else {
				$fieldNames{$fieldName} = "protein";
			}
			
			# if the field name is set, it's the value
			my $value = $fields[$i];
			
			if (!$inProteinData) {
				${$peptides{$pepId}{"fields"}}{$fieldName} = $value;
			}
			else {
				# initiailize the protein's array if necessary
				$proteins{$acc} = { } unless(defined($proteins{$acc}));
				
				# store the field
				${$proteins{$acc}}{$fieldName} = $value;
				
				$i = $i + 3; # next three fields (num peptide ratios used, SD(geo) and asterix are ignored)
			}
			
			$fieldName = 0;
		}
	}
	
	# close the file
	close($in);
	
	# return the parsed data
	return [\%fieldNames, \%proteins, \%peptides];
}

# -----------------------------------------
# Returns the table header for the given field.
#
# @param $fieldName The name of the field to get the header field name for.
# @param \%quantLabels A hash with the quant labels as keys and the subsample number as value.
# @return The field's table header WITHOUT the "peptide_" or "protein_" before. 
# ------------------------------------------
sub getTableLabelForField {
	my ($fieldName, $quantLabels) = @_;
	
	# check if it's a ratio
	if ($fieldName =~ /(\w+)\/(\w+)/) {
		my $label1 = $1;
		my $label2 = $2;
		
		return "relative_subsample" . ${$quantLabels}{$label1} . "_subsample" .  ${$quantLabels}{$label2};
	}
	else {
		# an intensity is being reported
		return "intensity_subsample" . ${$quantLabels}{$fieldName};
	}
}

# -----------------------------------------
# Returns the (string) cvParam for the given label
#
# @param $label The label to get the cvParam for.
# @return The cvParam formatted as a string.
# ------------------------------------------
sub getCvParamForLabel {
	my ($label) = @_;
	
	return $labelCvParams{$label} if (defined($labelCvParams{$label}));
	
	print "WARNING: Unknown label '" . $label . "' encountered. Please correct generated file manually.\n";
	
	return $labelCvParams{"unknown"};
}

sub trim {
	my ($string) = @_;
	
	$string =~ s/(^\s*)|(\s*$)//g;
	
	return $string;
}