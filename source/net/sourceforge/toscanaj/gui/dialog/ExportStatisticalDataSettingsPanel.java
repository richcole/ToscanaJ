/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: ExportStatisticalDataSettingsPanel.java,v 1.2 2003/02/19 03:27:19
 * peterbecker Exp $
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class ExportStatisticalDataSettingsPanel extends JComponent {
    private JTextField filterClauseTextField;
    private JCheckBox includeContingentListsCheckBox;
    private JCheckBox includeIntentExtentListsCheckBox;
	
    public ExportStatisticalDataSettingsPanel()
        throws HeadlessException {
        super();
        init();
    }

    private void init() {
    	JLabel clauseFieldLabel = new JLabel("Additional filter clause to use:");
    	this.filterClauseTextField = new JTextField();
    	JLabel includeListLabel = new JLabel("Include lists of objects and attributes:");
        this.includeContingentListsCheckBox = new JCheckBox("contingents");
        this.includeIntentExtentListsCheckBox = new JCheckBox("intent/extent");

    	setLayout(new GridBagLayout());
        
        add(clauseFieldLabel, new GridBagConstraints(
                            0, 0, 3, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(5,5,5,5),
                            2,2)
        );
        add(this.filterClauseTextField, new GridBagConstraints(
                            0, 1, 3, 1, 1, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0,25,5,5),
                            2,2)
        );
        add(includeListLabel, new GridBagConstraints(
                            0, 2, 3, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(5,5,5,5),
                            2,2)
        );
        add(this.includeContingentListsCheckBox, new GridBagConstraints(
                            0, 3, 1, 1, 1, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0,25,5,5),
                            2,2)
        );
        add(this.includeIntentExtentListsCheckBox, new GridBagConstraints(
                            1, 3, 2, 1, 1, 0,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.HORIZONTAL,
                            new Insets(0,5,5,5),
                            2,2)
        );
        add(new JPanel(), new GridBagConstraints(
                            0, 4, 1, 1, 1, 1,
                            GridBagConstraints.NORTHEAST,
                            GridBagConstraints.BOTH,
                            new Insets(5,5,5,5),
                            2,2)
        );
    }
    
    public String getFilterClause() {
    	return this.filterClauseTextField.getText().trim();
    }
    
    public boolean hasIncludeContingentListsSet() {
        return this.includeContingentListsCheckBox.isSelected();
    }

    public boolean hasIncludeIntentExtentListsSet() {
        return this.includeIntentExtentListsCheckBox.isSelected();
    }
    
}
