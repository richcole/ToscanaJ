/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

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
        final ToscanaJMainPanel mainWindow;
        if (args.length == 1) {
            mainWindow = new ToscanaJMainPanel(args[0]);
        } else {
            mainWindow = new ToscanaJMainPanel();
        }

        mainWindow.setVisible(true);
    }
}
