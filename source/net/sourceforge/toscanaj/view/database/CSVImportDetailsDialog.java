/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package net.sourceforge.toscanaj.view.database;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;

public class CSVImportDetailsDialog extends JDialog {
	
	private String filename;
	private DatabaseConnection connection;
	
	private String fieldSeparator = ";";
	
	private Hashtable columnNameToTypeMapping = new Hashtable();
	
	public CSVImportDetailsDialog(Frame owner, String filename, DatabaseConnection connection) throws HeadlessException {
		super(owner, "Text File Import Details", true);
		this.filename = filename;
		this.connection = connection;

		
		JPanel separatorsPanel = buildSeparatorButtonsPanel();
		
		this.getContentPane().add(separatorsPanel);
		
		pack();
		show();
	}
	

	private JPanel buildSeparatorButtonsPanel() {
	
		JPanel separatorsPanel = new JPanel(new FlowLayout());

		JRadioButton commaSeparatorButton = new JRadioButton("comma");
		commaSeparatorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setFieldSeparator(";");
			}
		});

		JRadioButton tabSeparatorButton = new JRadioButton("tab");
		tabSeparatorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// @todo should use something smarter here
				setFieldSeparator("	");
			}
		});
		
		JRadioButton semicolonSeparatorButton = new JRadioButton("semicolon");
		semicolonSeparatorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setFieldSeparator(";");
			}
		});
		
		ButtonGroup separatorsButtonGroup = new ButtonGroup();
		
		separatorsPanel.add(commaSeparatorButton);
		separatorsButtonGroup.add(commaSeparatorButton);
		separatorsPanel.add(tabSeparatorButton);
		separatorsButtonGroup.add(tabSeparatorButton);
		separatorsPanel.add(semicolonSeparatorButton);
		separatorsButtonGroup.add(semicolonSeparatorButton);
		
		commaSeparatorButton.setSelected(true);
			
		return separatorsPanel;
	}
	
	private void setFieldSeparator (String separator) {
		this.fieldSeparator = separator;
	}
	
	private void buildColumnSetupPanel () {
		
	}


}
