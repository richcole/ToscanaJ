#!/bin/sh
ulimit -s 2048
java -cp ToscanaJ.jar:libs/batik-awt-util.jar:libs/batik-dom.jar:libs/batik-svggen.jar:libs/batik-util.jar:libs/batik-xml.jar:libs/canvas.jar:libs/events.jar:libs/freehep-pdfpatch.zip:libs/freehep-base.jar:libs/freehep-graphics2d.jar:libs/freehep-graphicsio-emf.jar:libs/freehep-graphicsio-pdf.jar:libs/freehep-graphicsio-ppm.jar:libs/freehep-graphicsio-ps.jar:libs/freehep-graphicsio.jar:libs/hsqldb.jar:libs/jdom.jar:libs/pluginArchitecture.jar:libs/relations.jar:libs/tockit-swing.jar net.sourceforge.toscanaj.Lucca
