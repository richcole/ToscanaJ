#!/usr/bin/perl
# -*-Perl-*-
#
# Perl filter to handle the log messages from the checkin of files in
# a directory.  This script will group the lists of files by log
# message, and mail a single consolidated log message at the end of
# the commit.
#
# This file assumes a pre-commit checking program that leaves the
# names of the first and last commit directories in a temporary file.
#
# Also, the file names must be passed by cvs in the %{Vsv} format
#
# Contributed by David Hampton <hampton@cisco.com>
#
# hacked greatly by Greg A. Woods <woods@planix.com>
#
# Hacked even more by Max Maischein <maischein@navicon.de> for NaviCon GmbH
# * Now can post to a newsgroup
# * Now has (broken) HTML output as a logfile
# * Compatibility with Perl 4 is broken
# * Needs Net::News for usenet postings
# * Needs HTML::Entities for HTML output
#
# Minor changes by Peter Becker <becker@navicon.de>
# * Now '-s' turns ON "cvs status"
#
# Usage: log_accum.pl [-d] [-s] [-M module] [-u user] [[-m mailto] ...] [[-R replyto] ...] [-f logfile]
#
# Usage in CVS' loginfo file:
# [module]        [perl] [pathToCVSROOTdir]/log_accum.pl [options] %{Vsv}
#
# Options are:
#        -d                - turn on debugging
#        -m mailto         - send mail to "mailto" (multiple)
#        -R replyto        - set the "Reply-To:" to "replyto" (multiple)
#        -n newsgroup      - post news to "newsgroup"
#        -N newsserver     - post news to this newsserver (otherwise it will default
#                            the environment variable NNTPSERVER or NEWSHOST, or, if none
#                            of these exist, it will use the machine "news")
#        -M modulename     - set module name to "modulename"
#        -f logfile        - write commit messages to logfile too
#        -h htmlfile       - append stuff to HTML file "htmlfile" (will be created if necessary)
#        -s                - run "cvs status" for each file
#        -u                - CVS client username $USER passed from loginfo
#        -v                - use "-v" if using "cvs status"
#        -w                - show working directory with log message
#        -t                - use first line of log message as the subject for mail/news/html
#                            This will be confusing for the text CVS log, as that line is currently
#                            ommitted from there. If you want unmangled text CVS logs, don't use
#                            the -t switch.

#
#        Configurable options
#

# set this to something that takes a whole message on stdin
$MAILER               = "/usr/lib/sendmail -t";

#
#        End user configurable options.
#

# Constants (don't change these!)
#
$STATE_NONE    = 0;
$STATE_CHANGED = 1;
$STATE_ADDED   = 2;
$STATE_REMOVED = 3;
$STATE_LOG     = 4;

$LAST_FILE     = "/tmp/#cvs.lastdir";

$CHANGED_FILE  = "/tmp/#cvs.files.changed";
$ADDED_FILE    = "/tmp/#cvs.files.added";
$REMOVED_FILE  = "/tmp/#cvs.files.removed";
$LOG_FILE      = "/tmp/#cvs.files.log";

$FILE_PREFIX   = "#cvs.files";


# Initialize some variables to begin with making use strict; happy ...
# This obviously breaks Perl 4 compatibility ...
my $filelist = "";
my $commitlog = "";
my $htmlfile = "";
my $newsgroup = "";
my $newsserver = "";
my $nicesubject = 0;

# $maildate is what news and mail want to see as a date
my $maildate = `/bin/date -R`; chomp $maildate;
# $userdate is what the user gets to see as a date
my $userdate = `/bin/date`; chomp $userdate;

# The hash that holds the message split up in sections :
my %message;

#
#        Subroutines
#

sub cleanup_tmpfiles {
    local($wd, @files);

    $wd = `pwd`;
    chdir("/tmp") || die("Can't chdir('/tmp')\n");
    opendir(DIR, ".");
    push(@files, grep(/^$FILE_PREFIX\..*\.$id$/, readdir(DIR)));
    closedir(DIR);
    foreach (@files) {
        unlink $_;
    }
    unlink $LAST_FILE . "." . $id;

    chdir($wd);
}

sub write_logfile {
    local($filename, @lines) = @_;

    open(FILE, ">$filename") || die("Cannot open log file $filename.\n");
    print FILE join("\n", @lines), "\n";
    close(FILE);
}

sub format_names {
    local($dir, @files) = @_;
    local(@lines);

    $format = "\t%-" . sprintf("%d", length($dir)) . "s%s ";

    $lines[0] = sprintf($format, $dir, ":");

    if ($debug) {
        print STDERR "format_names(): dir = ", $dir, "; files = ", join(":", @files), ".\n";
    }
    foreach $file (@files) {
        if ( $file =~ s/^TAG:// ) {
            $lines[++$#lines] = sprintf($format, " ", "Branch: $file: ");
            next;
        }
        if (length($lines[$#lines]) + length($file) > 65) {
            $lines[++$#lines] = "\t\t";
        }

        # Fix up names just in case they contain spaces ...
        #$file = '"' . $file . '"' if ($file =~ / /);
        $lines[$#lines] .= $file . " ";
    }

    @lines;
}

sub format_lists {
    local(@lines) = @_;
    local(@text, @files, $lastdir);

    if ($debug) {
        print STDERR "format_lists(): ", join(":", @lines), "\n";
    }
    @text = ();
    @files = ();
    $lastdir = shift @lines;        # first thing is always a directory
    if ($lastdir !~ /.*\/$/) {
        die("Damn, $lastdir doesn't look like a directory!\n");
    }
    push(@dirs, $lastdir);
    foreach $line (@lines) {
        if ($line =~ /.*\/$/) {
            push(@text, &format_names($lastdir, @files));
            $lastdir = $line;
            push(@dirs, $lastdir);
            @files = ();
        } else {
            push(@files, $line);
        }
    }
    push(@text, &format_names($lastdir, @files));

    @text;
}

sub append_names_to_file {
    local($filename, $dir, @files) = @_;

    if (@files) {
        open(FILE, ">>$filename") || die("Cannot open file $filename.\n");
        print FILE $dir, "\n";
        print FILE join("\n", @files), "\n";
        close(FILE);
    }
}

sub read_line {
    local($line);
    local($filename) = @_;

    open(FILE, "<$filename") || die("Cannot open file $filename.\n");
    $line = <FILE>;
    close(FILE);
    chop($line);
    $line;
}

sub read_logfile {
    local(@text);
    local($filename, $leader) = @_;

    open(FILE, "<$filename");
    while (<FILE>) {
        chop;
        push(@text, $leader.$_);
    }
    close(FILE);
    @text;
}

sub build_header {
    local($header);
    $header = "Changes by:\t$login\t$userdate";
#    local($sec,$min,$hour,$mday,$mon,$year,$wday,$yday,$isdst) = localtime(time);
#    $header = sprintf("Changes by:\t%s\t%02d/%02d/%02d %02d:%02d:%02d",
#                      $login,
#                      $mon+1, $mday, $year%100,
#                      $hour, $min, $sec);
#    $header = sprintf("CVSROOT:\t%s\nModule name:\t%s\nRepository:\t%s\nChanges by:\t%s@%s\t%02d/%02d/%02d %02d:%02d:%02d",
#                      $cvsroot,
#                      $modulename,
#                      $dir,
#                      $login, $hostdomain,
#                      $year%100, $mon+1, $mday,
#                      $hour, $min, $sec);
}

sub mail_notification {
    local(@text) = @_;

    open(MAIL, "| $MAILER");
    print MAIL "Date:     " . $maildate . "\n";
    print MAIL "Subject:  " . $subjectline . "\n";
    print MAIL "To:       " . $mailto . "\n";
    print MAIL "Reply-To: " . $replyto . "\n";
    print MAIL "\n";
    print MAIL join("\n", @text), "\n";
    close(MAIL);
}

sub news_notification {
    my (@text) = @_;

    # We'll use Net::NNTP for posting ...
    require Net::NNTP;

    #print "Please wait while I post some news...\n";

    my $news = Net::NNTP->new($newsserver);
    my $usednewsserver = $newsserver || "(default)";

    if ($news) {
      my @header = (
                     "From: $login\@meganesia.int.gu.edu.au",
                     "Date: $maildate",
                     "Subject: $subjectline",
                     "X-Mailer: Perl skript",
                     "Newsgroups: $newsgroup",
                     # "Charset: ",
               );
      my @newsmsg = join("\n", @header, "", @text );

      if ($news->group($newsgroup)) {
        if ($news->postok()) {
          print "Couldn't post news to the news server\n" unless $news->post(@newsmsg);
        } else {
          print "I'm not allowed to post to $newsgroup on $usednewsserver\n";
        };
      } else {
        print "Error changing the group to $newsgroup on server $usednewsserver\n"
      };

      $news->quit();
    } else {
      print "Error connecting to newsserver $usednewsserver\n";
    };
}

sub send_notification {
  my (@text) = @_;
  &news_notification( @text ) if $newsgroup;
  &mail_notification( @text ) if $mailto;
};

sub write_commitlog {
    local($logfile, @text) = @_;

    open(FILE, ">>$logfile");
    print FILE join("\n", @text), "\n";
    close(FILE);
}

sub write_htmllog {
    my($htmlfile) = @_;
    my $part;

    # Local copy for munging
    my %msg = %message;

    use HTML::Entities;

    unless ( -e $htmlfile ) {
      local *FILE;
      open FILE, "> $htmlfile" or die "Can't create $htmlfile : $!\n";
      print FILE "<HTML>\n<LINK rel=\"stylesheet\" href=\"cvslog.css\">\n<HEAD>\n<TITLE>CVS log</TITLE>\n</HEAD>\n<BODY>\n";
      close FILE;
      chmod 0666, $htmlfile;
    };

    # First, encode all stuff like "<", ">", "&" etc. in the message bodies
    foreach (keys %msg) {
      encode_entities( $msg{$_} );
      $msg{$_} =~ s/\n/<BR>\n/gsm;
    };

    my $text = "<DIV class=\"subject\">" . $message{"subject"} . "</DIV>\n";

    # Now fix up the HTML a bit

    # Patch up the log message with nice HTML
    $text .= "<DIV class='datelogin'>" . $msg{"who"} . "</DIV>\n<DIV class='logmessage'>" . $msg{"message"} . "</DIV>";

    # Patch up the added/modified/removed files
    my ($type, $name);
    for $type ("added", "modified", "removed") {
      if ($msg{$type}) {
        ($name = $type) =~ s/^(.)/\u$1/;
        $msg{$type} = "<DIV class='filesheader'>$name files :</DIV><SPAN class='fileslist'>" . $msg{$type} . "</SPAN>\n";
        $text .= $msg{$type};
      };
    };

    if ($do_status) {
      # Patch up the status

      $msg{"status"} =~ s!(={60,})<BR>!<DIV class='statusdivider'>\1</DIV><DIV class='statusmessage'>!sm;
      $msg{"status"} .= "</DIV>";
      $text .= $msg{"status"};
    };

    open(FILE, ">>$htmlfile");
    print FILE $text, "<HR>\n";
    # So we're writing bad HTML ... D'oh

    close(FILE);
};

#
#        Main Body
#

# Initialize basic variables
#
$debug = 0;
$id = getpgrp();                # note, you *must* use a shell which does setpgrp()
$state = $STATE_NONE;
# $login = getlogin || (getpwuid($<))[0] || "nobody";
chop($hostname = `hostname`);
chop($domainname = `domainname`);
if ($domainname !~ '^\..*') {
    $domainname = '.' . $domainname;
}
$hostdomain = $hostname . $domainname;
$cvsroot = $ENV{'CVSROOT'};
$do_status = 0;                        # moderately useful
$show_tags = "";                # use -v with status command
$show_wd = 0;                        # useless in client/server
$modulename = "";

# parse command line arguments (file list is seen as one arg)
#
while (@ARGV) {
    $arg = shift @ARGV;

    if ($arg eq '-d') {
        $debug = 1;
        print STDERR "Debug turned on...\n";
    } elsif ($arg eq '-m') {
        if ($mailto eq '') {
            $mailto = shift @ARGV;
        } else {
            $mailto = $mailto . ", " . shift @ARGV;
        }
    } elsif ($arg eq '-R') {
        if ($replyto eq '') {
            $replyto = shift @ARGV;
        } else {
            $replyto = $replyto . ", " . shift @ARGV;
        }
    } elsif ($arg eq '-M') {
        $modulename = shift @ARGV;
    } elsif ($arg eq '-h') {
        ($htmlfile) && die("Too many arguments! Only one HTML file allowed.\n");
        $htmlfile = shift @ARGV;
    } elsif ($arg eq '-n') {
        ($newsgroup) && die("Too many arguments! Only one newsgroup allowed.\n");
        $newsgroup = shift @ARGV;
    } elsif ($arg eq '-N') {
        ($newsserver) && die("Too many arguments! Only one newsserver allowed.\n");
        $newsserver = shift @ARGV;
    } elsif ($arg eq '-s') {
        $do_status = 1;
    } elsif ($arg eq '-w') {
        $show_wd = 1;
    } elsif ($arg eq '-v') {
        $show_tags = "-v";
    } elsif ($arg eq '-t') {
        $nicesubject = 1;
    } elsif ($arg eq '-f') {
        ($commitlog) && die("Too many '-f' args\n");
        $commitlog = shift @ARGV;
    } elsif ($arg eq '-u') {
        $login = shift @ARGV;
    } else {
        ($filelist) && die("Too many arguments!  Check usage.\n");
        $filelist = $arg;
    }
}

#($mailto) || die("No mail recipient specified (use -m)\n");
if ($login eq '') {
    $login = getlogin || (getpwuid($<))[0] || "nobody";
}
if ($replyto eq '') {
    $replyto = $login;
}

# cut up filelist to build hashes of versions
#
undef $special;
if ($filelist =~ s/^([^\,]+)\s\-\s(New director.*|Imported sources)/$2/) {
    $repo = $1;
    $special = $filelist;
} else {
    $filelist =~ s/^([^\,]+)\s(([0-9]+\.[0-9\.]*|NONE)\,.*)$/$2/;
    $repo = $1;
}

if ($debug) {
    print STDERR "repo is \"" . $repo . "\"\n";
    print STDERR "filelist is \"" . $filelist . "\"\n";
}

# Special cases of New directory and Imported sources
# Just dump the text.

if ($special) {
    local(@text);

    @text = ();
    $subjectline = $repo . " - " . $special;
    push(@text, &build_header());
    push(@text, "");
    push(@text, $special);
    push(@text, "");

    while (<STDIN>) {
        if (/^In directory/) {
            if ($show_wd) {                # useless in client/server mode
                push(@text, $_);
            }
        } else {
            chop;                        # Drop the newline
            push(@text, $_);
        }
    }

    &send_notification(@text);
    #&mail_notification(@text);

    exit 0;
}

# for now, the first "file" is the repository directory being committed,
# relative to the $CVSROOT location
#
@path = split('/', $repo);

# XXX There are some ugly assumptions in here about module names and
# XXX directories relative to the $CVSROOT location -- really should
# XXX read $CVSROOT/CVSROOT/modules, but that's not so easy to do, since
# XXX we have to parse it backwards.
# XXX
# XXX Fortunately it's relatively easy for the user to specify the
# XXX module name as appropriate with a '-M' via the directory
# XXX matching in loginfo.
#
if ($modulename eq "") {
    $modulename = "DEFAULT " . $path[0];        # I.e. the module name == top-level dir
}
if ($#path == 0) {
    $dir = $repo;
    if ( "$dir" eq "" ) {
       $dir = ".";
    }
} else {
    $dir = join('/', @path);
}
$dir = $dir . "/";

if ($debug) {
    print STDERR "module - ", $modulename, "\n";
    print STDERR "dir    - ", $dir, "\n";
    print STDERR "path   - ", join(":", @path), "\n";
    print STDERR "files  - ", $filelist, "\n";
    print STDERR "id     - ", $id, "\n";
}

my %versions, $filename;

while ($filelist) {
    last unless ($filelist =~ s/^\s?([0-9]+\.[0-9\.]*|NONE)\,([^\,]+)\,([0-9]+\.[0-9\.]*|NONE)(\s.*)?$/$4/);
    $filename = $2;

    # Fix the quotes on $filename
    #$filename = '"' . $filename . '"' if ($filename =~ / /);
    $versions{$filename} = [$1, $3];

    if ($debug) {
        print STDERR "file name is \"" . $filename . "\"\n";
        print STDERR "old version was \"" . "$versions{$filename}[0]" . "\"\n";
        print STDERR "new version is \"" . "$versions{$filename}[1]" . "\"\n";
    }


    if ( "$versions{$filename}[0]" eq "NONE" ) {
        push(@added_file_list, $filename);
    } elsif ( "$versions{$filename}[1]" eq "NONE" ) {
        push(@removed_file_list, $filename);
    } else {
        push(@changed_file_list, $filename);
    }

}

# If there is anything still on $filelist, our RE didn't match - which means that we were called
# with unexpected parameters
print STDERR "Odd Filelist : $filelist\n*** Please make sure you call log_accum.pl with %{Vsv} !" if $filelist;

# Iterate over the body of the message collecting information.
#
while (<STDIN>) {
    chop;                        # Drop the newline

    if (/^In directory/) {
        if ($show_wd) {                # useless in client/server mode
            push(@log_lines, $_);
            push(@log_lines, "");
        }
        next;
    }

    if (/^Modified Files/) { $state = $STATE_CHANGED; next; }
    if (/^Added Files/)    { $state = $STATE_ADDED;   next; }
    if (/^Removed Files/)  { $state = $STATE_REMOVED; next; }
    if (/^Log Message/)    { $state = $STATE_LOG;     next; }

    s/^[ \t\n]+//;                # delete leading whitespace
    s/[ \t\n]+$//;                # delete trailing whitespace

    if (/^Tag:/) {
        s/^Tag\:\s+/TAG:/;
    }
    if (/^No tag/) {
        s/^.*$/TAG:Trunk/;
    }

    if ($state == $STATE_CHANGED) { push(@changed_files, $_); }
    if ($state == $STATE_ADDED)   { push(@added_files,   $_); }
    if ($state == $STATE_REMOVED) { push(@removed_files, $_); }
    if ($state == $STATE_LOG)     { push(@log_lines,     $_); }
}

# Strip leading and trailing blank lines from the log message.  Also
# compress multiple blank lines in the body of the message down to a
# single blank line.
#
while ($#log_lines > -1) {
    last if ($log_lines[0] ne "");
    shift(@log_lines);
}
while ($#log_lines > -1) {
    last if ($log_lines[$#log_lines] ne "");
    pop(@log_lines);
}
for ($i = $#log_lines; $i > 0; $i--) {
    if (($log_lines[$i - 1] eq "") && ($log_lines[$i] eq "")) {
        splice(@log_lines, $i, 1);
    }
}

if ($debug) {
    print STDERR "Searching for log file index...";
}
# Find an index to a log file that matches this log message
#
for ($i = 0; ; $i++) {
    local(@text);

    last if (! -e "$LOG_FILE.$i.$id"); # the next available one
    @text = &read_logfile("$LOG_FILE.$i.$id", "");
    last if ($#text == -1);        # nothing in this file, use it
    last if (join(" ", @log_lines) eq join(" ", @text)); # it's the same log message as another
}
if ($debug) {
    print STDERR " found log file at $i.$id, now writing tmp files.\n";
}

# Spit out the information gathered in this pass.
#
&append_names_to_file("$CHANGED_FILE.$i.$id", $dir, @changed_files);
&append_names_to_file("$ADDED_FILE.$i.$id",   $dir, @added_files);
&append_names_to_file("$REMOVED_FILE.$i.$id", $dir, @removed_files);
&write_logfile("$LOG_FILE.$i.$id", @log_lines);

# Check whether this is the last directory.  If not, quit.
#
if ($debug) {
    print STDERR "Checking current dir against last dir.\n";
}
$_ = &read_line("$LAST_FILE.$id");

if ($_ ne $cvsroot . "/" . $repo) {
    if ($debug) {
        print STDERR sprintf("Current directory %s is not last directory %s.\n", $cvsroot . "/" .$repo, $_);
    }
    exit 0;
}
if ($debug) {
    print STDERR sprintf("Current directory %s is last directory %s -- all commits done.\n", $repo, $_);
}

#
#        End Of Commits!
#

# This is it.  The commits are all finished.  Lump everything together
# into a single message, fire a copy off to the mailing list, and drop
# it on the end of the Changes file.



#
# Produce the final compilation of the log messages
#
@text = ();
@status_txt = ();
push(@text, &build_header());
push(@text, "");

# Also clean our hash of details
%message = ();
$message{"who"} = "$login\t$userdate";

for ($i = 0; ; $i++) {
    last if (! -e "$LOG_FILE.$i.$id"); # we've done them all!

    # First print the log messages
    @lines = &read_logfile("$LOG_FILE.$i.$id", "\t");
    if ($#lines >= 0) {
        push(@text, "Log message:");

        # We always and only take the first log message with the -t switch
        if ($nicesubject) {
          ($subjectline = shift @lines ) =~ s/^\s*//;
          undef $nicesubject;
        };

        push(@text, @lines);
        push(@text, "");

        $message{"message"} = join( "\n", @lines );
    }

    # now go on for some other info detailing the files that were changed
    @lines = &read_logfile("$CHANGED_FILE.$i.$id", "");
    if ($#lines >= 0) {
        push(@text, "Modified files:");
        push(@text, &format_lists(@lines));
        $message{"modified"} = join( "\n", &format_lists(@lines));
    }
    @lines = &read_logfile("$ADDED_FILE.$i.$id", "");
    if ($#lines >= 0) {
        push(@text, "Added files:");
        push(@text, &format_lists(@lines));
        $message{"added"} = join( "\n", &format_lists(@lines));
    }
    @lines = &read_logfile("$REMOVED_FILE.$i.$id", "");
    if ($#lines >= 0) {
        push(@text, "Removed files:");
        push(@text, &format_lists(@lines));
        $message{"removed"} = join( "\n", &format_lists(@lines));
    }

    sort(@dirs);
    $lastdir = @dirs[0];
    push(@subject, $lastdir);
    foreach $dir (@dirs) {
        if ($dir ne $lastdir) {
            push(@subject, $dir);
            $lastdir = $dir;
        }
    }
    $subjectline = join (" ", @subject) unless $subjectline;
    $message{"subject"} = $subjectline;

    if ($do_status) {
        local(@changed_files);
        local(@dofiles);

        @changed_files = ();
        push(@changed_files, &read_logfile("$CHANGED_FILE.$i.$id", ""));
        push(@changed_files, &read_logfile("$ADDED_FILE.$i.$id", ""));
        push(@changed_files, &read_logfile("$REMOVED_FILE.$i.$id", ""));

        @dofiles = ();
        foreach $dofile (@changed_files) {
            if (($dofile =~ /\/$/) || ($dofile =~ /^TAG:/)) {
                next;                # ignore the silly "dir" entries
                                # and the tags
            }
            push(@dofiles, $dofile);
        }

        if ($debug) {
            print STDERR "main: pre-sort changed_files = ", join(":", @dofiles), ".\n";
        }
        sort(@dofiles);
        if ($debug) {
            print STDERR "main: post-sort changed_files = ", join(":", @dofiles), ".\n";
        }

        foreach $dofile (@dofiles) {
            if ($debug) {
                print STDERR "main(): doing 'cvs -nQq status $show_tags $dofile'\n";
            }
            if ( "$show_tags" eq "-v" ) {
                open(STATUS, "-|") || exec 'cvs', '-nQq', 'status', '-v', $dofile;
            } else {
                open(STATUS, "-|") || exec 'cvs', '-nQq', 'status', $dofile;

           }
            while (<STATUS>) {
                chop;
                push(@status_txt, $_);
            }
        }
    }
}

# Write to the commitlog file
#
if ($commitlog) {
    &write_commitlog($commitlog, @text);
}

if ($#status_txt >= 0) {
# Trim the output from cvs status
# compress multiple blank lines in the body of the message down to a
# single blank line.
#
    for ($i = $#status_txt; $i > 0; $i--) {
        if (($status_txt[$i] =~ /^\s*$/) || ($status_txt[$i] =~ /^File:\s.*Status:\s.*$/)) {
            splice(@status_txt, $i, 1);
        }
    }
    push(@text, @status_txt);

    $message{"status"} = join( "\n", @status_txt );
}

if ($htmlfile) {
  &write_htmllog($htmlfile);
};

# Send the notification.
&send_notification(@text);

# cleanup
#
if (! $debug) {
    &cleanup_tmpfiles();
}

exit 0;