/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import java.io.File;

import net.sourceforge.toscanaj.gui.SienaMainPanel;

public class Siena {
    /**
     *  Main method for running the program
     */
    public static void main(String[] args) {
		ToscanaJ.testJavaVersion();
        ToscanaJ.loadPlugins();
        final SienaMainPanel mainWindow;
        mainWindow = new SienaMainPanel();
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if( (arg.compareToIgnoreCase("-importCernatoXML") == 0) && (i < args.length - 1) ){
            	mainWindow.importCernatoXML(new File(args[i+1]));
            	i++;
            }
        }
        mainWindow.setVisible(true);
    }
}
