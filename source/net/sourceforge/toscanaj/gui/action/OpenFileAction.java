package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class OpenFileAction extends KeyboardMappedAction {

    FileActivity activity;
    File previousFile;

    /**
     *  @note
     *     If you don't want to specify mnemonics
     *     then use the other constructor.
     * @todo if you want another conmbination then write another constructor.
     */
    public OpenFileAction(
            JFrame frame,
            FileActivity activity,
            int mnemonic,
            KeyStroke keystroke)
    {
        super(frame, mnemonic, keystroke);
        this.activity = activity;
    }

    public OpenFileAction(
            JFrame frame,
            FileActivity activity)
    {
        super(frame );
        this.activity = activity;
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser openDialog;

        boolean result = false;
        try {
            result = activity.prepareToProcess();
        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Unable to initiate file saving:" + ex.getMessage(),
                    "Error preparing to save",
                    JOptionPane.ERROR_MESSAGE);
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
                    activity.processFile(selectedFile);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Failure to read the file:" + ex.getMessage(),
                            "Error opening file",
                            JOptionPane.ERROR_MESSAGE);
                }
                previousFile = selectedFile;
            }
        }
    }

}

