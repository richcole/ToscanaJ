/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj;

import net.sourceforge.toscanaj.gui.AnacondaJMainPanel;

public class AnacondaJ {
    /**
     *  Main method for running the program
     */
    public static void main(String[] args) {
        final AnacondaJMainPanel mainWindow;
        mainWindow = new AnacondaJMainPanel();
        mainWindow.setVisible(true);
    }
}
