/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.gui.activity.FileActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

public class OpenFileAction extends KeyboardMappedAction {

    private final FileActivity openActivity;
    private File previousFile;

    private final List<SimpleActivity> postOpenActivities = new ArrayList<SimpleActivity>();

    public void addPostOpenActivity(final SimpleActivity activity) {
        this.postOpenActivities.add(activity);
    }

    protected void processPostOpenActivities() throws Exception {
        for (final SimpleActivity activity : this.postOpenActivities) {
            if (!activity.doActivity()) {
                break;
            }
        }
    }

    public OpenFileAction(final JFrame frame, final FileActivity activity,
            final File defaultOpenLocation, final int mnemonic,
            final KeyStroke keystroke) {
        super(frame, "Open...", mnemonic, keystroke);
        this.openActivity = activity;
        this.previousFile = defaultOpenLocation;
    }

    public OpenFileAction(final JFrame frame, final FileActivity activity,
            final File defaultOpenLocation) {
        super(frame, "Open...");
        this.openActivity = activity;
        this.previousFile = defaultOpenLocation;
    }

    public void actionPerformed(final ActionEvent e) {
        final JFileChooser openDialog;

        boolean result = false;
        try {
            result = this.openActivity.prepareToProcess();
        } catch (final Exception ex) {
            ErrorDialog.showError(this.frame, ex,
                    "Unable to initiate file saving:" + ex.getMessage(),
                    "Error preparing to save");
        }

        if (result) {

            if (this.previousFile != null) {
                openDialog = new JFileChooser(this.previousFile);
            } else {
                openDialog = new JFileChooser(System.getProperty("user.dir"));
            }
            openDialog.setFileFilter(new ExtensionFileFilter(this.openActivity
                    .getExtensions(), this.openActivity.getDescription()));

            if (openDialog.showOpenDialog(this.frame) == JFileChooser.APPROVE_OPTION) {
                final File selectedFile = openDialog.getSelectedFile();
                try {
                    this.openActivity.processFile(selectedFile);
                } catch (final Exception ex) {
                    ErrorDialog.showError(this.frame, ex,
                            "Failure to read the file:" + ex.getMessage(),
                            "Error opening file");
                    ex.printStackTrace();
                }
                this.previousFile = selectedFile;
                try {
                    processPostOpenActivities();
                } catch (final Exception ex) {
                    ErrorDialog.showError(this.frame, ex,
                            "Failure to process the file:" + ex.getMessage(),
                            "Error processing file");
                }
            }
        }
    }

}
