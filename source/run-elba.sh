#!/bin/sh
ulimit -s 2048
java -cp ToscanaJ.jar:libs/tockit-canvas.jar:libs/tockit-events.jar:libs/hsqldb.jar:libs/jdom.jar:libs/tockit-pluginArchitecture.jar:libs/tockit-relations.jar:libs/tockit-swing.jar:libs/tockit-utils.jar net.sourceforge.toscanaj.Elba
