package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;
import net.sourceforge.toscanaj.gui.activity.FileActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

public class OpenFileAction extends KeyboardMappedAction {

    private FileActivity openActivity;
    private File previousFile;

    private ArrayList postOpenActivities = new ArrayList();

    public void addPostOpenActivity(SimpleActivity activity) {
        postOpenActivities.add(activity);
    }

    protected void processPostOpenActivities() throws Exception {
        for (Iterator it = postOpenActivities.iterator(); it.hasNext();) {
            SimpleActivity activity = (SimpleActivity) it.next();
            if ( ! activity.doActivity() ) {
                break;
            }
        }
    }

    /**
     *  @note
     *     If you don't want to specify mnemonics
     *     then use the other constructor.
     * @todo if you want another conmbination then write another constructor.
     */
    public OpenFileAction(
            JFrame frame,
            FileActivity activity,
            String defaultOpenLocation,
            int mnemonic,
            KeyStroke keystroke)
    {
        super(frame, "Open...", mnemonic, keystroke);
        this.openActivity = activity;
        this.previousFile = getFile(defaultOpenLocation);
    }

    public OpenFileAction(
            JFrame frame,
            FileActivity activity,
            String defaultOpenLocation)
    {
        super(frame, "Open...");
        this.openActivity = activity;
        this.previousFile = getFile(defaultOpenLocation);
    }

    private File getFile(String defaultOpenLocation) {
        File retVal;
        try {
            retVal = new File(defaultOpenLocation);
        } catch (Exception e) {
            retVal = null;
        }
        return retVal;
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser openDialog;

        boolean result = false;
        try {
            result = openActivity.prepareToProcess();
        }
        catch (Exception ex) {
            ErrorDialog.showError(
                    frame,
                    ex,
                    "Unable to initiate file saving:" + ex.getMessage(),
                    "Error preparing to save");
        }

        if ( result ) {

            if (previousFile != null) {
                openDialog = new JFileChooser(previousFile);
            } else {
                openDialog = new JFileChooser(System.getProperty("user.dir"));
            }

            if (openDialog.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = openDialog.getSelectedFile();
                try {
                    openActivity.processFile(selectedFile);
                } catch (Exception ex) {
                    ErrorDialog.showError(
                            frame,
                            ex,
                            "Failure to read the file:" + ex.getMessage(),
                            "Error opening file");
                    ex.printStackTrace();
                }
                previousFile = selectedFile;
                try {
                    processPostOpenActivities();
                } catch (Exception ex) {
                    ErrorDialog.showError(
                            frame,
                            ex,
                            "Failure to process the file:" + ex.getMessage(),
                            "Error processing file");
                }
            }
        }
    }

}

