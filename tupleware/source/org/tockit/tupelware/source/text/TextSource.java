/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.source.text;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

import org.tockit.tupelware.gui.IndexSelectionDialog;
import org.tockit.tupelware.model.TupelSet;
import org.tockit.tupelware.source.TupelSource;


public class TextSource implements TupelSource {
    public static final String[] FILE_EXTENSIONS = new String[]{"tuples"};
	public static final String FILE_DESCRIPTION = "Tuple Sets";
	
	private int[] objectIndices;
	private TupelSet tuples;
	private File selectedFile;
		
	public void show(JFrame parent, File lastLocation) {
		final JFileChooser openDialog = new JFileChooser();
 		openDialog.setFileFilter(new ExtensionFileFilter(FILE_EXTENSIONS, FILE_DESCRIPTION));
		if (openDialog.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			this.selectedFile = openDialog.getSelectedFile();
			try {
				Reader reader = new FileReader(this.selectedFile);
				this.tuples = TabDelimitedParser.parseTabDelimitedTuples(reader);
				IndexSelectionDialog dialog = new IndexSelectionDialog(parent, "Select object set", this.tuples.getVariableNames());
				dialog.show();
				this.objectIndices = dialog.getSelectedIndices();
			} catch (Exception e) {
				ErrorDialog.showError(parent, e, "Could not read file");
			}
		}
    }

    public String getMenuName() {
        return "Load form tab-delimited file...";
    }

    public File getSelectedFile() {
        return this.selectedFile;
    }

    public TupelSet getTuples() {
        return this.tuples;
    }

    public int[] getObjectIndices() {
        return this.objectIndices;
    }
}
