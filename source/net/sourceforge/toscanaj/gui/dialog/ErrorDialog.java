/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.parser.DataFormatException;
import org.tockit.canvas.imagewriter.ImageGenerationException;

import javax.swing.*;


/**
 * This is a generic class top handle error messages.
 *
 * ToscanJ Exceptions will give the user the option to see a simple error message
 * or thay can view more detailed error message.
 */

public class ErrorDialog {

    /**
     * Constructor to show a simple error message
     */
    private ErrorDialog(JFrame frame, String title, String msg) {
        JOptionPane.showMessageDialog(frame,
                msg, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Constructor to show an error message based on exception thrown.
     *
     * The user has the choice to view more detail of the error
     * based on the original exception thrown.
     */
    private ErrorDialog(JFrame frame, Throwable e, String title) {
        showDetailedErrorMsg(frame, e, title, e.getMessage());
    }

    /**
     * Constructor to show a simple error message.
     *
     * The user has the choice to view more detail of the error
     * based on the original exception thrown.
     */
    private ErrorDialog(JFrame frame, Throwable e, String title, String errorMsg) {
        showDetailedErrorMsg(frame, e, title, errorMsg);
    }

    /**
     * Show an error dialog that gives the option to show a more detailed
     * error message if required.
     */
    private void showDetailedErrorMsg(JFrame frame, Throwable e, String title, String errorMsg) {
        ///@TODO an interface is requird for all toscanaJ exceptions
        Exception original = null;
        if (e instanceof ImageGenerationException) {
            original = ((ImageGenerationException) e).getOriginal();
        } else if (e instanceof DataFormatException) {
            original = ((DataFormatException) e).getOriginal();
        } else if (e instanceof DatabaseException) {
            original = ((DatabaseException) e).getOriginal();
        }
        if (original == null) {
            new ErrorDialog(frame, title, errorMsg);
            return;
        }
        Object[] options = {"OK", "Details"};
        int n = JOptionPane.showOptionDialog(frame,
                errorMsg,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
        if (n == 1) {
            JOptionPane.showMessageDialog(frame,
                    original.getMessage(),
                    title,
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Show a simple error message.
     */
    public static void showError(JFrame frame, String title, String errorMsg) {
        new ErrorDialog(frame, title, errorMsg);
    }

    /**
     * Show an error message using exception thrown
     *
     * User can see a more detail error message
     * based on original exception thrown
     */
    public static void showError(JFrame frame, Throwable e, String title) {
        new ErrorDialog(frame, e, title);
    }

    /**
     * Show a simple error message
     *
     * User can see a more detail error message
     * based on original exception thrown
     */
    public static void showError(JFrame frame, Throwable e, String title, String errorMsg) {
        new ErrorDialog(frame, e, title, errorMsg);
    }
}
