package net.sourceforge.toscanaj.gui.action;

import net.sourceforge.toscanaj.model.XML_Reader;
import net.sourceforge.toscanaj.model.XML_SyntaxError;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
            KeyStroke keystroke)
    {
        super(frame, mnemonic, keystroke);
        this.activity = activity;
    }

    public SaveFileAction(
            JFrame frame,
            FileActivity activity)
    {
        super(frame );
        this.activity = activity;
    }

    public void actionPerformed(ActionEvent e) {
        final JFileChooser saveDialog;

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
                JOptionPane.showMessageDialog(
                        frame,
                        "Failure to save the file:" + ex.getMessage(),
                        "Error saving file",
                        JOptionPane.ERROR_MESSAGE);
            }
            previousFile = selectedFile;
        }
    }

}

