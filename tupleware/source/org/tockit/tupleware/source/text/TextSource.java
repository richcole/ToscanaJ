/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.text;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;

import org.tockit.relations.model.Relation;
import org.tockit.tupleware.gui.IndexSelectionDialog;
import org.tockit.tupleware.source.TupleSource;


public class TextSource implements TupleSource {
    public static final String[] FILE_EXTENSIONS = new String[]{"tuples"};
	public static final String FILE_DESCRIPTION = "Tuple Sets";
	
	private int[] objectIndices;
	private Relation tuples;
	private File selectedFile;
    private JComponent optionPanel;
    
    private static class CharacterSelector {
        private char character;
        private String displayName;
        
        public CharacterSelector(char character, String displayName) {
            this.character = character;
            this.displayName = displayName;
        }
        
        public char getCharacter() {
            return this.character;
        }
        
        public String toString() {
            return this.displayName;
        }
    }
    
    private static final CharacterSelector[] SEPARATORS = new CharacterSelector[]{
            new CharacterSelector('\t', "Tab"),
            new CharacterSelector(',', "Comma"),
            new CharacterSelector(';', "Semicolon"),
            new CharacterSelector(' ', "Space")
    };
    
    private static final CharacterSelector[] QUOTES = new CharacterSelector[]{
            new CharacterSelector('\"', "Double Quote"),
            new CharacterSelector('\'', "Single Quote"),
            new CharacterSelector('\000', "None (\\000)")
    };
    
    private static final CharacterSelector[] ESCAPES = new CharacterSelector[]{
            new CharacterSelector('\\', "Backslash"),
            new CharacterSelector('$', "Dollar"),
            new CharacterSelector('%', "Percent"),
            new CharacterSelector('!', "Exclamation Mark"),
            new CharacterSelector('\000', "None (\\000)")
    };
    
    private JComboBox separatorComboBox = new JComboBox(SEPARATORS);
    private JComboBox quoteComboBox = new JComboBox(QUOTES);
    private JComboBox escapeComboBox = new JComboBox(ESCAPES);
    private JCheckBox headerCheckBox = new JCheckBox("First line is header", true);
    
    public TextSource() {
        this.optionPanel = new JPanel(new GridBagLayout());
        
        GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.gridx = 0;
        labelConstraints.gridy = GridBagConstraints.RELATIVE;
        labelConstraints.weightx = 0;
        labelConstraints.fill = GridBagConstraints.HORIZONTAL;
        labelConstraints.insets = new Insets(0,0,0,5);
        
        GridBagConstraints comboConstraints = new GridBagConstraints();
        comboConstraints.gridx = 1;
        comboConstraints.gridy = GridBagConstraints.RELATIVE;
        comboConstraints.weightx = 1;
        comboConstraints.fill = GridBagConstraints.HORIZONTAL;
                
        GridBagConstraints checkBoxConstraints = new GridBagConstraints();
        checkBoxConstraints.gridx = 0;
        checkBoxConstraints.gridwidth = 2;
        checkBoxConstraints.weightx = 1;
        checkBoxConstraints.anchor = GridBagConstraints.EAST;
        checkBoxConstraints.insets = new Insets(10,0,0,0);
        
        this.optionPanel.add(new JLabel("Separator:"), labelConstraints);
        this.optionPanel.add(this.separatorComboBox, comboConstraints);
        this.optionPanel.add(new JLabel("Quote:"), labelConstraints);
        this.optionPanel.add(this.quoteComboBox, comboConstraints);
        this.optionPanel.add(new JLabel("Escape:"), labelConstraints);
        this.optionPanel.add(this.escapeComboBox, comboConstraints);
        this.optionPanel.add(this.headerCheckBox, checkBoxConstraints);
    }
		
	public void show(JFrame parent, File lastLocation) {
		final JFileChooser openDialog = new JFileChooser(lastLocation);
 		openDialog.setFileFilter(new ExtensionFileFilter(FILE_EXTENSIONS, FILE_DESCRIPTION));
 		openDialog.setAccessory(this.optionPanel);
		if (openDialog.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			this.selectedFile = openDialog.getSelectedFile();
			char separator = ((CharacterSelector) this.separatorComboBox.getSelectedItem()).getCharacter();
			char quote = ((CharacterSelector) this.quoteComboBox.getSelectedItem()).getCharacter();
			char escape = ((CharacterSelector) this.escapeComboBox.getSelectedItem()).getCharacter();
			boolean firstLineHeader = this.headerCheckBox.isSelected();
			try {
				Reader reader = new FileReader(this.selectedFile);
				try {
					this.tuples = SeparatedTextParser.parseTabDelimitedTuples(reader, separator, quote, escape, firstLineHeader);
				} finally {
					reader.close();
				}
                if(this.tuples == null) {
                    throw new IOException("No tuples found in file");
                }
                IndexSelectionDialog dialog = new IndexSelectionDialog(parent, "Select object set", this.tuples.getDimensionNames());
				dialog.show();
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
