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
import net.sourceforge.toscanaj.gui.dialog.CheckDuplicateFileChooser;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

import javax.swing.*;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;

public class SaveFileAction extends KeyboardMappedAction {

    FileActivity activity;
    SimpleActivity postSaveActivity;
    File previousFile;
    SimpleActivity preSaveActivity;

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
    
    public void setPostSaveActivity(SimpleActivity activity) {
        this.postSaveActivity = activity;
    }

    public void setPreSaveActivity(SimpleActivity activity) {
        this.preSaveActivity = activity;
    }

    public void actionPerformed(ActionEvent e) {
    	if(this.preSaveActivity != null) {
    		boolean result = false;
            try {
                result = this.preSaveActivity.doActivity();
            } catch (Exception ex) {
            	ErrorDialog.showError(null,ex,"Could not initialize save operation");
            }
    		if(result == false) {
    			return;
    		}
    	}
		saveFile();
    }

	public boolean saveFile() throws HeadlessException {
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
			String[] csxExtension = {"csx"};
			ExtensionFileFilter csxFileFilter = new ExtensionFileFilter(csxExtension,"Conceptual Schema");
			ExtensionFileFilter[] filterArray = { csxFileFilter };
            saveDialog = new CheckDuplicateFileChooser(previousFile, filterArray);
			
		    if (saveDialog.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
		        File selectedFile = saveDialog.getSelectedFile();
		        previousFile = selectedFile;
		        try {
		            activity.processFile(selectedFile);
		        } catch (Exception ex) {
		            ErrorDialog.showError(
		                    frame,
		                    ex,
		                    "Error saving file",
		                    "Failure to save the file:" + ex.getMessage()
		            );
		            return false;
		        }
		        if(this.postSaveActivity != null) {
		        	try {
                        return this.postSaveActivity.doActivity();
                    } catch (Exception e) {
		        	    ErrorDialog.showError(
		        	            frame,
		        	            e,
		        	            "Error after saving the file",
		        	            "The file was saved, but some postprocessing failed."
		        	    );
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
    
    public void setPreviousFile(File previousFile) {
        this.previousFile = previousFile;
    }
}

