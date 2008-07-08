/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.gui.activity.FileActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.dialog.CheckDuplicateFileChooser;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

public class SaveFileAction extends KeyboardMappedAction {

    FileActivity activity;
    SimpleActivity postSaveActivity;
    File previousFile;
    SimpleActivity preSaveActivity;

    public SaveFileAction(final JFrame frame, final FileActivity activity,
            final int mnemonic, final KeyStroke keystroke) {
        super(frame, "Save...", mnemonic, keystroke);
        this.activity = activity;
    }

    public SaveFileAction(final JFrame frame, final FileActivity activity) {
        super(frame, "Save...");
        this.activity = activity;
    }

    public void setPostSaveActivity(final SimpleActivity activity) {
        this.postSaveActivity = activity;
    }

    public void setPreSaveActivity(final SimpleActivity activity) {
        this.preSaveActivity = activity;
    }

    public void actionPerformed(final ActionEvent e) {
        if (this.preSaveActivity != null) {
            boolean result = false;
            try {
                result = this.preSaveActivity.doActivity();
            } catch (final Exception ex) {
                ErrorDialog.showError(null, ex,
                        "Could not initialize save operation");
            }
            if (result == false) {
                return;
            }
        }
        saveFile();
    }

    public boolean saveFile() throws HeadlessException {
        final JFileChooser saveDialog;

        boolean result = false;
        try {
            result = this.activity.prepareToProcess();
        } catch (final Exception ex) {
            JOptionPane.showMessageDialog(this.frame,
                    "Unable to initiate file saving:" + ex.getMessage(),
                    "Error preparing to save", JOptionPane.ERROR_MESSAGE);
        }

        if (result) {
            final String[] csxExtension = { "csx" };
            final ExtensionFileFilter csxFileFilter = new ExtensionFileFilter(
                    csxExtension, "Conceptual Schema");
            final ExtensionFileFilter[] filterArray = { csxFileFilter };
            saveDialog = new CheckDuplicateFileChooser(this.previousFile,
                    filterArray);

            if (saveDialog.showSaveDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = saveDialog.getSelectedFile();
                this.previousFile = selectedFile;
                try {
                    this.activity.processFile(selectedFile);
                } catch (final Exception ex) {
                    ErrorDialog.showError(this.frame, ex, "Error saving file",
                            "Failure to save the file:" + ex.getMessage());
                    return false;
                }
                if (this.postSaveActivity != null) {
                    try {
                        return this.postSaveActivity.doActivity();
                    } catch (final Exception e) {
                        ErrorDialog
                                .showError(this.frame, e,
                                        "Error after saving the file",
                                        "The file was saved, but some postprocessing failed.");
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public File getLastFileUsed() {
        return this.previousFile;
    }

    public void setPreviousFile(final File previousFile) {
        this.previousFile = previousFile;
    }
}
