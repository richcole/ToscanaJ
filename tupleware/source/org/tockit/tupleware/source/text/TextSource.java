/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.text;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

import org.tockit.relations.model.Relation;
import org.tockit.swing.RelationImportPanel;
import org.tockit.tupleware.gui.IndexSelectionDialog;
import org.tockit.tupleware.source.TupleSource;


public class TextSource implements TupleSource {
    public static final String[] FILE_EXTENSIONS = new String[]{"tuples"};
	public static final String FILE_DESCRIPTION = "Tuple Sets";
	
	private int[] objectIndices;
	private Relation tuples;
	private File selectedFile;
    private RelationImportPanel optionPanel;
    public TextSource() {
        this.optionPanel = new RelationImportPanel();
    }
		
	public void show(JFrame parent, File lastLocation) {
		final JFileChooser openDialog = new JFileChooser(lastLocation);
 		openDialog.setFileFilter(new ExtensionFileFilter(FILE_EXTENSIONS, FILE_DESCRIPTION));
 		openDialog.setAccessory(this.optionPanel);
		if (openDialog.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			this.selectedFile = openDialog.getSelectedFile();
			try {
				Reader reader = new FileReader(this.selectedFile);
				try {
					this.tuples = SeparatedTextParser.parseTabDelimitedTuples(
					        reader, 
					        this.optionPanel.getSeparator(), 
					        this.optionPanel.getQuote(), 
					        this.optionPanel.getEscape(), 
					        this.optionPanel.firstLineIsHeader());
				} finally {
					reader.close();
				}
                if(this.tuples == null) {
                    throw new IOException("No tuples found in file");
                }
                IndexSelectionDialog dialog = new IndexSelectionDialog(parent, "Select object set", this.tuples.getDimensionNames());
				dialog.setVisible(true);
				this.objectIndices = dialog.getSelectedIndices();
			} catch (Exception e) {
				ErrorDialog.showError(parent, e, "Could not read file");
			}
		}
    }

    public String getMenuName() {
        return "Load from tab-delimited file...";
    }

    public File getSelectedFile() {
        return this.selectedFile;
    }

    public Relation getTuples() {
        return this.tuples;
    }

    public int[] getObjectIndices() {
        return this.objectIndices;
    }
}
