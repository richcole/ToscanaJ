/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.Container;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

public class ExportStatisticalDataSettingsDialog extends JDialog {
	private static final String CONFIGURATION_SECTION = "ExportStatisticalDataSettingsDialog";
    private JTextField filterClauseTextField;
    private JCheckBox includeContingentListsCheckBox;
    private JCheckBox includeIntentExtentListsCheckBox;
	private boolean positiveResult = false;
	
    public ExportStatisticalDataSettingsDialog(Frame owner)
        throws HeadlessException {
        super(owner, true);
        init();
    }
    
    public ExportStatisticalDataSettingsDialog(JDialog owner)
        throws HeadlessException {
        super(owner, true);
        init();
    }

    private void init() {
    	JLabel clauseFieldLabel = new JLabel("Additional filter clause to use:");
    	this.filterClauseTextField = new JTextField();
    	JLabel includeListLabel = new JLabel("Include lists of objects and attributes:");
        this.includeContingentListsCheckBox = new JCheckBox("contingents");
        this.includeIntentExtentListsCheckBox = new JCheckBox("intent/extent");
    	
    	JButton okButton = new JButton("Ok");
        okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	positiveResult = true;
            	dispose();
            }
        });

    	JButton cancelButton = new JButton("Cancel");
    	cancelButton.addActionListener(new ActionListener(){
    		public void actionPerformed(ActionEvent e) {
    			dispose();
            }
    	});
    	
    	final JDialog dialog = this;
    	this.addWindowListener(new WindowAdapter(){
    		public void windowClosed(WindowEvent e) {
    			ConfigurationManager.storePlacement(CONFIGURATION_SECTION, dialog);
            }
    	});
    	
    	Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());
        
        contentPane.add(clauseFieldLabel, new GridBagConstraints(
                            0, 0, 3, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(5,5,5,5),
                            2,2)
        );
        contentPane.add(this.filterClauseTextField, new GridBagConstraints(
                            0, 1, 3, 1, 1, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0,25,5,5),
                            2,2)
        );
        contentPane.add(includeListLabel, new GridBagConstraints(
                            0, 2, 3, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(5,5,5,5),
                            2,2)
        );
        contentPane.add(this.includeContingentListsCheckBox, new GridBagConstraints(
                            0, 3, 1, 1, 1, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0,25,5,5),
                            2,2)
        );
        contentPane.add(this.includeIntentExtentListsCheckBox, new GridBagConstraints(
                            1, 3, 2, 1, 1, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0,5,5,5),
                            2,2)
        );
        contentPane.add(new JPanel(), new GridBagConstraints(
                            0, 4, 1, 1, 1, 1,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.BOTH,
                            new Insets(5,5,5,5),
                            2,2)
        );
        contentPane.add(okButton, new GridBagConstraints(
                            1, 4, 1, 1, 0, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.NONE,
                            new Insets(5,5,5,5),
                            2,2)
        );
        contentPane.add(cancelButton, new GridBagConstraints(
                            2, 4, 1, 1, 0, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.NONE,
                            new Insets(5,5,5,5),
                            2,2)
        );
        ConfigurationManager.restorePlacement(CONFIGURATION_SECTION, this, new Rectangle(50,50,300,200));
    }
    
    public String getFilterClause() {
    	return this.filterClauseTextField.getText();
    }
    
    public boolean hasIncludeContingentListsSet() {
        return this.includeContingentListsCheckBox.isSelected();
    }

    public boolean hasIncludeIntentExtentListsSet() {
        return this.includeIntentExtentListsCheckBox.isSelected();
    }

    public boolean hasPositiveResult() {
    	return this.positiveResult;
    }
}
