/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.gui.activity.FileActivity;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class SaveFileAction extends KeyboardMappedAction {

    FileActivity activity;
    File previousFile;

    /**
     *  @note
     *     If you don't want to specify mnemonics
     *     then use the other constructor.
     * @todo if you want another conmbination then write another constructor.
     */
    public SaveFileAction(
            JFrame frame,
            FileActivity activity,
            int mnemonic,
            KeyStroke keystroke) {
        super(frame, "Save...", mnemonic, keystroke);
        this.activity = activity;
    }

    public SaveFileAction(
            JFrame frame,
            FileActivity activity) {
        super(frame, "Save...");
        this.activity = activity;
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser saveDialog;

        boolean result = false;
        try {
            result = activity.prepareToProcess();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to initiate file saving:" + ex.getMessage(),
                    "Error preparing to save",
                    JOptionPane.ERROR_MESSAGE);
        }

        if (result) {
            if (previousFile != null) {
                saveDialog = new JFileChooser(previousFile);
            } else {
                saveDialog = new JFileChooser(System.getProperty("user.dir"));
            }

            if (saveDialog.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = saveDialog.getSelectedFile();
                try {
                    activity.processFile(selectedFile);
                } catch (Exception ex) {
                    ErrorDialog.showError(
                            frame,
                            ex,
                            "Error saving file",
                            "Failure to save the file:" + ex.getMessage()
                    );
                    ex.printStackTrace();
                }
                previousFile = selectedFile;
            }
        }
    }

}

