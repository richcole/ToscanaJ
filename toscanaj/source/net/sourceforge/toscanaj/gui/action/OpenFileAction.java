/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.gui.activity.FileActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OpenFileAction extends KeyboardMappedAction {

    private FileActivity openActivity;
    private File previousFile;

    private List postOpenActivities = new ArrayList();

    public void addPostOpenActivity(SimpleActivity activity) {
        this.postOpenActivities.add(activity);
    }

    protected void processPostOpenActivities() throws Exception {
        for (Iterator it = this.postOpenActivities.iterator(); it.hasNext();) {
            SimpleActivity activity = (SimpleActivity) it.next();
            if (!activity.doActivity()) {
                break;
            }
        }
    }

    public OpenFileAction(
            JFrame frame,
            FileActivity activity,
            File defaultOpenLocation,
            int mnemonic,
            KeyStroke keystroke) {
        super(frame, "Open...", mnemonic, keystroke);
        this.openActivity = activity;
        this.previousFile = defaultOpenLocation;
    }

    public OpenFileAction(
            JFrame frame,
            FileActivity activity,
            File defaultOpenLocation) {
        super(frame, "Open...");
        this.openActivity = activity;
        this.previousFile = defaultOpenLocation;
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser openDialog;

        boolean result = false;
        try {
            result = this.openActivity.prepareToProcess();
        } catch (Exception ex) {
            ErrorDialog.showError(
                    this.frame,
                    ex,
                    "Unable to initiate file saving:" + ex.getMessage(),
                    "Error preparing to save");
        }

        if (result) {

            if (this.previousFile != null) {
                openDialog = new JFileChooser(this.previousFile);
            } else {
                openDialog = new JFileChooser(System.getProperty("user.dir"));
            }
            openDialog.setFileFilter(new ExtensionFileFilter(
                                                this.openActivity.getExtensions(),
                                                this.openActivity.getDescription()));

            if (openDialog.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = openDialog.getSelectedFile();
                try {
                    this.openActivity.processFile(selectedFile);
                } catch (Exception ex) {
                    ErrorDialog.showError(
                            this.frame,
                            ex,
                            "Failure to read the file:" + ex.getMessage(),
                            "Error opening file");
                    ex.printStackTrace();
                }
                this.previousFile = selectedFile;
                try {
                    processPostOpenActivities();
                } catch (Exception ex) {
                    ErrorDialog.showError(
                            this.frame,
                            ex,
                            "Failure to process the file:" + ex.getMessage(),
                            "Error processing file");
                }
            }
        }
    }

}

