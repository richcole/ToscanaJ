/*
 * Copyright Peter Becker (http://www.peterbecker.de). Please
 * read licence.txt file provided with the distribution for
 * licensing information.
 * 
 * $Id$
 */
package net.sourceforge.toscanaj.gui.action;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

import org.tockit.context.model.Context;


public abstract class ExportContextAction extends KeyboardMappedAction {
    public interface ContextSource {
        Context getContext();
    }
    
    private ContextSource contextSource;

    public ExportContextAction(Frame frame, ContextSource contextSource) {
        super(frame, "dummy");
        putValue(Action.NAME, getName());
        this.contextSource = contextSource;
    }
    
    public ExportContextAction (Frame frame, ContextSource contextSource, int mnemonic, KeyStroke keystroke) {
        super(frame, "dummy", mnemonic, keystroke);
        putValue(Action.NAME, getName());
        this.contextSource = contextSource;
    }

    public void actionPerformed(ActionEvent e) {
        try {
            exportContext();
        } catch (FileNotFoundException e1) {
            ErrorDialog.showError(null, e1, "Can not save file");
        }
    }

    private void exportContext() throws FileNotFoundException {
        final JFileChooser saveDialog = new JFileChooser();
        
        saveDialog.setFileFilter(getFileFilter());
        
        int rv = saveDialog.showSaveDialog(this.frame);
        if (rv == JFileChooser.APPROVE_OPTION) {
            File selectedFile = saveDialog.getSelectedFile();
            ExtensionFileFilter extFileFilter = (ExtensionFileFilter) saveDialog.getFileFilter();

            if (selectedFile.getName().indexOf('.') == -1) {
                String[] extensions = extFileFilter.getExtensions();
                selectedFile = new File(selectedFile.getAbsolutePath() + "." + extensions[0]);
            }
            
            exportFile(selectedFile);
        }
    }
    
    protected ContextSource getContextSource() {
        return this.contextSource;
    }

    protected abstract void exportFile(File selectedFile) throws FileNotFoundException;

    protected abstract ExtensionFileFilter getFileFilter();

    protected abstract String getName();
}
