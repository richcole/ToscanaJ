This is the directory containing examples for ToscanaJ as SQL scripts.

At the moment only one example exists: a pretty old (but classic *g*)
example from a German computer magazine testing Intel80486 based PCs.
Probably the data has suffered during the different ports of the
database, but let's assume you won't buy any of them anyway.

You should be able to use this example with most database management
systems, e.g. MySQL or PostGreSQL. It is set up at the moment for using
McKoi, a GPLed, Java-based database engine, which can be found at this
place:

   http://www.mckoi.com

Unless you are using another database engine anyway, we recommend using
McKoi -- it is powerful enough, easy to use and free. If you want to use
another system, you have to figure out how to access it via JDBC, i.e.
you have to find a JDBC driver, get its class name and the URL to access
your database. Once you have this information, go into the CSX file (e.g.
"pctest.csx" and change the element //conceptualSchema/database/url to
match your setup (that is the element <url> in <database> in 
<conceptualSchema> which contains the class name for the driver, the
username, the password and the connection URL).

Here is a quick howto for setting up McKoi with the pctest example:
- get McKoi and unzip it somewhere, this should create a directory
  like "mckoi0.93" (0.93 is the version at the time of writing)
- go into that directory (and stay there for the next steps)
- call
    java -jar mckoidb.jar -create "user" "password"
  which creates a new database with the given username and password.
  If you want to use a different username or password, you have to edit
  the beginning of the "pctest.csx" file to match the ones you pick (we
  will run McKoi on a TCP/IP port, so picking something better might
  be useful, although it is just an example).
- call
    java -jar mckoidb.jar
  to start the server. This should print some startup message and will
  block the shell unless you put it into the background. If you want to
  keep it this way, start a new shell and go into the same directory.
- call
    java -cp mckoidb.jar com.mckoi.tools.JDBCScriptTool -u user \
         -p password -in /path/to/the/pctest.sql
  Note that the first argument is now "-cp", not "-jar". The username
  and password have to match your setup, the last argument has to be
  the full path and filename to the "pctest.sql" file in the directory
  of this readme. If it works properly, you will get lots of SQL commands
  on the screen, all but the first answered with something like:
     +--------+
     | result |
     +--------+
     | 1      |
     +--------+
  If you get this, you should have now the database set up properly.
- copy the "mckoidb.jar" file into the "libs" directory of your ToscanaJ
  installation. It should be picked up automatically (if not, ToscanaJ
  will complain that it can't find the JDBC driver and you will have to
  fix your classpath -- should not happen, though).
- start ToscanaJ, open the "pctest.csx" file from this directory and
  hopefully you should be able to get the full-featured ToscanaJ.
- after you are finished, you should shutdown the DB server with:
    java -jar mckoidb.jar -shutdown "user" "password"
  This ensures that the database does not get broken. Remember to restart
  the server before using the example again.
  
If things fail, you can restart the process from scratch, just remove the
McKoi directory (assuming you did not change anything else in there) and
try to follow the steps again. If this should fail, feel free to post a
support request. Prefered way is the mailing list for users:

    mailto:toscanaj-users@lists.sourceforge.net

You can find subscription information on the project pages:

    http://sourceforge.net/projects/toscanaj

where you can also find a web-based support forum, which we try to
maintain, too. But the mailing list is safer ;-)


Enjoy!

   Peter (for the whole ToscanaJ team)
