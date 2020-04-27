#!/usr/local/bin/perl -Tw

use strict;
use CGI;

my($cgi) = new CGI;

print $cgi->header('text/html');
print $cgi->start_html(-title => "POST method",
                       -BGCOLOR => 'RED');
foreach my $param ($cgi->param)
{
 print "<LI>", "$param ", $cgi->param($param), "\n";
}
print "</UL>";
print $cgi->end_html, "\n";
