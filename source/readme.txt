ToscanaJ 0.2 Readme File
========================

What is it?
-----------
A second release of the Java reimplementation of the
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
You need a Java 2 runtime environment, i.e. any JRE or
JDK with version number 1.2 or above. We develop with
Suns JDK version 1.3.1 on Windows 2000, tests were made
with different JDK 1.3.x on Windows NT and 98 plus 
different Linux Systems.


How do I start ToscanaJ?
------------------------
Run the appropriate start script ("start.bat" on Windows,
"start.sh" on Unix/Linux). If you run it from the command
line make sure you are in the installation directory, it
won't work from within another path. Alternatively you can
start it direcly with "java -jar ToscanaJ.jar". On Windows
you can connect the ".jar" extension with "javaw -jar",
then you can just double-click the JAR file in the explorer.


What can I do with ToscanaJ?
----------------------------
ToscanaJ should open an example on startup, unfortunately
the only examples we have at the moment are in German, this
will hopefully change soon. Double click on one of the diagram
titles on the left side to open your first diagram. You can
add multiple diagrams to the list of selected diagrams this
way. Once you have an open diagram you can do the following
things:

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
- export diagrams as vector graphic (SVG)

Added features in ToscanaJ 0.2:
- connections can be made with all JDBC drivers instead of
  only the JDBC-ODBC bridge
- labels are now resizable and scrollable
- SQL aggregates can be defined in the CSX files to e.g.
  display an average price in the diagram


Are there other examples?
-------------------------
The current distribution contains the same example as Access
database. To use this you have to create an ODBC data source
name (DSN) pointing to the "pctest.mdb" file in the examples
directory. The data set is the same but this demonstrates how
to use an ODBC/JDBC connection and how to define extra views
for SQL aggregates.


How do I create examples?
-------------------------
Unfortunately we currently can't offer any editor. There is
a tool converting the old CSC files into the new CSX files,
but due to the different scaling approach this usually needs
some editing afterwards.

You can edit the CSX files in an XML or generic text editor,
the structure should be self-explanatory if you know something
about the way Toscana works. Ask on the mailing lists for help
if you really want to create your own examples, it is not that
hard if you know what to do, but it is hard to explain ;-)


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
something new.


How can I help?
---------------
First of all: give feedback. Comment, complain, ask questions.
Put comments and new items into the tracker system. Tell us
about projects you would like to do with ToscanaJ and what you
miss to do them.

You can follow the development by getting the latest version
from the CVS repository:
  http://sourceforge.net/cvs/?group_id=37081

The CVS contains the source, all libraries needed, a build
script for usage with Apache Ant (3) and some other useful
files.

You can join the developer mailing list to discuss with us:
  http://sourceforge.net/mail/?group_id=37081

If you want to contribute code contact us on the developer
list. You can submit patches directly but be aware that the
code is still moving fast, it might be a good idea to ask us
first if we are currently working on the part you want to
change.

===========================================================

Enjoy!
  The KVO team

===========================================================
(1): http://www.int.gu.edu.au/kvo
(2): http://tockit.sourceforge.net
(3): http://jakarta.apache.org/ant/index.html