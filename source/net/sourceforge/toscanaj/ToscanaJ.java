/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import javax.swing.JOptionPane;

import net.sourceforge.toscanaj.gui.ToscanaJMainPanel;

public class ToscanaJ {
    /**
     * The version name used in the about dialog.
     */
    static public final String VersionString = "CVS Build";

    /**
     *  Main method for running the program
     */
    public static void main(String[] args) {
    	testJavaVersion();
        final ToscanaJMainPanel mainWindow;
        if (args.length == 1) {
            mainWindow = new ToscanaJMainPanel(args[0]);
        } else {
            mainWindow = new ToscanaJMainPanel();
        }

        mainWindow.setVisible(true);
    }

	/**
	 * Tests if we are running at least JRE 1.4.0
	 */
	public static void testJavaVersion() {
		String versionString = System.getProperty("java.class.version","44.0");
		if("48.0".compareTo(versionString) > 0) {
			JOptionPane.showMessageDialog(null,"This program requires a Java Runtime Environment\n" +
				"with version number 1.4.0 or above.\n\n" +
				"Up to date versions of Java can be found at\n" +
				"http://java.sun.com.",
				"Java installation too old", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}
}