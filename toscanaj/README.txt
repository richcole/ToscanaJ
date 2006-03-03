This is a ToscanaJ sandbox, containing all files needed to build
ToscanaJ and its editors.

The structure is as follows:
  - the root folder contains this file and the build files
  - the "source" folder contains the Java source
  - the "libs" folder contains all required libraries
  - the "templates" folder contains templates used in the build
    process
  - the "distfiles" folder contains additional files used in the
    distribution, e.f. licence information and scripts
  - the "examples" folder contains example input files

To build ToscanaJ run Ant on the build.xml file on the toplevel.
All targets meant for external execution have descriptions, all
other targets are meant as internal targets and should not be
called. The default target creates a full distribution.

The Ant build process will create two additional folders "build"
and "dist", which should not be used for anything else. They will
be ignored by Subversion.
