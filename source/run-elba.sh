#!/bin/sh
ulimit -s 2048
java -cp ToscanaJ.jar:libs/canvas.jar:libs/events.jar:libs/jdom.jar:libs/batik-awt-util.jar:libs/batik-dom.jar:libs/batik-svggen.jar:libs/batik-util.jar:libs/batik-xml.jar:libs/hsqldb.jar:libs/iText.jar:libs/postscriptwriter.zip net.sourceforge.toscanaj.Elba
