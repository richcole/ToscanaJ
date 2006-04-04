ToscanaJ Readme File
====================

What is it?
-----------
ToscanaJ is a Java reimplementation of the
classic Formal Concept Analysis tool named "Toscana".

It is open sourced under a BSD-style licence (see the text
file licence.txt in the distribution). Read the licence files
in the "libs" directory to see the licences of the libraries
used.


Why this?
---------
The old Toscana versions where build on top of a 16bit OWL
implementation, which turned out to be hardly maintainable
in the last years. ToscanaJ shall build upon the experience
collected with these tools but recreate the program with more
modern technologies.


Who did this?
-------------
It started with a hackfest of the KVO group (1), was put
into the Tockit project (2) for a while and has now its
own development stream on Sourceforge:
   http://toscanaj.sourceforge.net
It stills shares developers with Tockit and we will try
to create some common code base.


What do I need to run ToscanaJ?
-------------------------------
You need a Java runtime environment (JRE) or Java develop-
ment kit (JDK), with version number 1.4 or above.


How do I start ToscanaJ?
------------------------
Run the appropriate start script ("run-toscanaj.bat" on 
Windows, insource the script on Unix/Linux with the command
". ./run-toscanaj.sh"). If you run it from the command line 
make sure you are in the installation directory, it won't work 
from within another path. Alternatively you can start it direcly 
with "java -jar ToscanaJ.jar". On Windows you can connect the 
".jar" extension with "javaw -jar", then you can just double-
click the JAR file in the explorer. The same applies to the other
".jar" files in the main directory.


What can I do with ToscanaJ?
----------------------------
ToscanaJ comes with some examples, the most important is in the
folder examples/sql/pctest, named pctest.csx, which is a data from 
a PC test that will be loaded into the internal database engine.
There is a readme text in the directory examples/sql that explains 
how to set up the database example for other database engines.

The data is rather old but it still should be able to show you 
how a Toscana system does work. If you have a newer system in the
old Toscana 2/3 format that might be distributed please tell us,
we would be happy to port it and add it into the distribution.

Double click on one of the diagram titles on the left side to 
open your first diagram. You can add multiple diagrams to the 
list of selected diagrams this way. 

Once you have an open diagram you can do the following things: 
- move labels
- double click the object labels to display their entries
- double click them again to go back to the numbers
- if a diagram is left in the list of selected diagrams:
  double click a node to filter the objects and go to the
  next diagram
- click on a node to highlight the upset and downset (click
  on background to remove)
- switch the display options in the view menu
- switch the filter method in the diagram menu, using either
  all objects that matched the nodes you selected or only the
  ones that matched exactly the attributes of the underlying
  concept, but nothing more (extent vs. object contingent)
- create nested diagrams (only one level of nesting at the
  moment)
- print diagrams (only fit-to-page at the moment)
- export diagrams as bitmaps (PNG, JPG)

There are many other features, unfortunately the user manual
is still work in progress.

Can I extent ToscanaJ?
----------------------
Yes, there are a number of plugin interfaces. You can add other
database views and new graphic export options. Check the ToscanaJ
and Tockit download pages for available plugin downloads. They
get installed by unzipping them into the plugin directory.


How do I create examples?
-------------------------
The only fully working editor at the moment is Elba, which
you can start with the "run-elba" scripts. Note that Elba
needs a database connection to work, so if some features are
not available, please make sure you are connected to a 
database. You can connect to a database by using the rightmost
button in the toolbar.

What are your plans for ToscanaJ?
---------------------------------
To be able to replace all existing Toscana installations and
to offer a stable and easy to extend platform for implementing
new features. See the feature request tracker on Sourceforge:
  http://sourceforge.net/tracker/?atid=418907&group_id=37081


Where can I get help?
---------------------
The best place is the user mailing list for ToscanaJ:
  http://sourceforge.net/mail/?group_id=37081

If you prefer not to subscribe to this list use the webforum:
  http://sourceforge.net/forum/forum.php?forum_id=115820


ToscanaJ is broken!
-------------------
All in all it worked for us ;-) There are some known bugs
which are collected here:
  http://sourceforge.net/tracker/?group_id=37081&atid=418904

Feel free to add comments/items if you think you found
something new. You can mail comments/bugreports to
toscanaj-developers@lists.sf.net -- we are always happy to
hear from users, esp. if it helps improving the quality of
our programs.


How can I help?
---------------
First of all: give feedback. Comment, complain, ask questions.
Put comments and new items into the tracker system. Tell us
about projects you would like to do with ToscanaJ and what you
miss to do them.

You can follow the development by getting the latest version
from the Subversion (SVN) repository:
  http://sourceforge.net/svn/?group_id=37081

The repository contains the source, all libraries needed, a 
build script for usage with Apache Ant (3), a setup for the
Eclipse IDE (4) and some other useful files.

Alternatively you can try snapshots from this site:
  http://toscanaj.sourceforge.net/cvsbuilds/
  
We put up-to-date versions there whenever it seems worthwhile.
These builds are not officially supported, but you can use
them to give early feedback or access a new feature before
the official release.

You can join the developer mailing list to discuss with us:
  http://sourceforge.net/mail/?group_id=37081

If you want to contribute code contact us on the developer
list. You can submit patches directly but be aware that the
code is still moving fast, it might be a good idea to ask us
first if we are currently working on the part you want to
change.

===========================================================

Enjoy!
  The ToscanaJ team

===========================================================
(1): http://www.kvocentral.org
(2): http://tockit.sourceforge.net
(3): http://jakarta.apache.org/ant/index.html
