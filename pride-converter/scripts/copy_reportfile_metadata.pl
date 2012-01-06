#!/usr/bin/perl

use strict;
use warnings;

use Getopt::Long;
use Pod::Usage;

# get the user options
my $man 		= 0;
my $help 		= 0;
my $basefile	= undef;

GetOptions(
	'help|h' => \$help, 
	'man' => \$man,
	'basefile|b=s' => \$basefile
) or pod2usage(2);

# display the help info if set
pod2usage(1) if ($help);
pod2usage(-exitstatus => 0, -verbose => 2) if ($man);

die "Error: Missing required parameter basefile.\n" unless($basefile);

# open the basefile
my %data = ( );

open (my $in, "<" . $basefile) or die "Error: Failed to open " . $basefile . "\n";

# read the whole file into a buffer
my $buffer = "";

while (<$in>) {
	$buffer .= $_;
}

close ($in);

# extract the metadata section
die "Error: Failed to extract meta-data from basefile.\n" unless($buffer =~ /<Metadata>(.*)<\/Metadata>/s);

$data{"metadata"} = $1;

# get the ptms
die "Error: Failed to extract ptm information.\n" unless($buffer =~ /<PTMs>(.*)<\/PTMs>/s);

$data{"ptms"} = $1;

# process all files
for my $filename (@ARGV) {
	print "Processing " . $filename . "...";
	
	open (my $in, "<" . $filename) or die "Error: Failed to open " . $filename . "\n";
	open (my $out, ">" . $filename . ".tmp") or die "Error: Failed to create temporary file\n";
	
	my $inMetadata = 0;
	my $inPTMs = 0;
	
	while (<$in>) {
		my $line = $_;
		
		# check if the line is the metadata line
		if ($line =~ /\s*<Metadata>\s*/) {
			$inMetadata = 1;
			
			print $out $line;
			print $out $data{"metadata"};
		}
		
		# check if the metadata ended
		if ($line =~ /\s*<\/Metadata>\s*/) {
			$inMetadata = 0;
		}
		
		# check if the PTMs section starts
		if ($line =~ /\s*<PTMs>\s*/) {
			$inPTMs = 1;
			
			print $out $line;
			print $out $data{"ptms"};
		}
		
		# check if the PTMs ended
		if ($line =~ /\s*<\/PTMs>\s*/) {
			$inPTMs = 0;
		}
		
		next if ($inPTMs || $inMetadata);
		
		print $out $line;
	} 
	
	close ($in);
	close($out);
	
	# replace the real file with the tmp file
	`mv ${filename}.tmp $filename`;
	
	print "OK.\n";
}


__END__

=head1 NAME

copy_reportfile_metadata.pl - Copies the metadata from one report file to others.

=head1 SYNOPSIS

copy_reportfile_metadata.pl [OPTIONS] [FILE ...]

Copies the metadata from the base reportfile to the others.

=head1 OPTIONS

=over 8

=item B<-b, -basefile <FILE>>

The base reportfile to copy the metadata from.

=item B<-h, -help>

Displays this help page.

=item B<-man>

Display the program's manpage.
                  
=back
                           
=head1 EXAMPLES

=cut