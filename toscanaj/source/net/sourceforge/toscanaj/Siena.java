/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import java.io.File;
import java.io.IOException;

import net.sourceforge.toscanaj.gui.SienaMainPanel;

public class Siena {
    /**
     * Main method for running the program
     */
    public static void main(final String[] args) {
        ToscanaJ.loadPlugins();
        SienaMainPanel mainWindow = null;
        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];
            if ((arg.compareToIgnoreCase("-importCernatoXML") == 0)
                    && (i < args.length - 1)) {
                mainWindow = new SienaMainPanel(false);
                try {
                    mainWindow.importCernatoXML(new File(args[i + 1]));
                } catch (final IOException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
                i++;
            }
        }
        if (mainWindow == null) {
            mainWindow = new SienaMainPanel(true);
        }
        mainWindow.setVisible(true);
    }
}
