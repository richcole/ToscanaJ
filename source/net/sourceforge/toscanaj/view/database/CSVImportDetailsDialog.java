/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

package net.sourceforge.toscanaj.view.database;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.SQLTypeInfo;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

public class CSVImportDetailsDialog extends JDialog {
	
	private String filename;
	private DatabaseConnection connection;
	
	private String fieldSeparator = ";";
	private String tableName = "TEST";
	
	private Hashtable columnNameToTypeMapping = new Hashtable();
	
	private JTextField columnNameField;
	private JComboBox dataTypesComboBox;
	private JButton addColumnButton;
	private JButton removeColumnButton;
	
	private DBTypesTableModel columnsTableModel = new DBTypesTableModel();
	
	private JTable columnsDisplayTable;
	
	
	public CSVImportDetailsDialog(Frame owner, String filename, DatabaseConnection connection) throws HeadlessException {
		super(owner, "Text File Import Details", true);
		this.filename = filename;
		this.connection = connection;

		JLabel headingLabel = new JLabel ("Database Setup Details");
		
		
		JPanel dbSetupPanel = buildDatabaseSetupDetailsPanel();
		JPanel columnsSetupPanel = buildColumnSetupPanel();
		JPanel buttonsPanel = buildButtonsPanel();

		JPanel mainPanel = new JPanel();		
		
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		dbSetupPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		dbSetupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(dbSetupPanel);
		
		columnsSetupPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
		columnsSetupPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		mainPanel.add(columnsSetupPanel);
		mainPanel.setBorder(BorderFactory.createRaisedBevelBorder());


		headingLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

		this.getContentPane().add(headingLabel, BorderLayout.NORTH);
		this.getContentPane().add(mainPanel, BorderLayout.CENTER);
		this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
		
		pack();
		setLocationRelativeTo(owner);
		show();
	}
	

	private JPanel buildDatabaseSetupDetailsPanel() {
	
		JPanel separatorsPanel = new JPanel();
		separatorsPanel.setLayout(new GridBagLayout());
		
		JRadioButton commaSeparatorButton = new JRadioButton("comma");
		commaSeparatorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setFieldSeparator(";");
			}
		});

		JRadioButton tabSeparatorButton = new JRadioButton("tab");
		tabSeparatorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// @todo should use something smarter here instead of tab string
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

		separatorsButtonGroup.add(commaSeparatorButton);
		separatorsButtonGroup.add(tabSeparatorButton);
		separatorsButtonGroup.add(semicolonSeparatorButton);
		
		commaSeparatorButton.setSelected(true);


		separatorsPanel.add(new JLabel("Fields Separator "),new GridBagConstraints(
				0,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));
		separatorsPanel.add(commaSeparatorButton,new GridBagConstraints(
				1,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));
		separatorsPanel.add(tabSeparatorButton,new GridBagConstraints(
				2,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));
		separatorsPanel.add(semicolonSeparatorButton,new GridBagConstraints(
				3,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));
			
		return separatorsPanel;
	}
	
	
	private JPanel buildColumnSetupPanel () {
		JPanel columnSetupPanel = new JPanel();
		columnSetupPanel.setLayout(new GridBagLayout());

		JLabel columnNameFieldLabel = new JLabel("Column Name");
		columnNameField = new JTextField(20);
		columnNameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateAddColumnButton();
			}
		});

		JLabel dataTypeLabel = new JLabel("Data Type");
		List dbTypes = getDatabaseTypes();
		Collections.sort(dbTypes);
		Vector dbTypesVector = new Vector();
		dbTypesVector.add("Choose Column Type");
		dbTypesVector.addAll(dbTypes);
		dataTypesComboBox = new JComboBox(dbTypesVector);
		dataTypesComboBox.setEditable(false);
		dataTypesComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				updateAddColumnButton();
			}
		});
		
		addColumnButton = new JButton("Add");
		addColumnButton.setEnabled(false);
		addColumnButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				columnsTableModel.addRow(columnNameField.getText(), (String) dataTypesComboBox.getSelectedItem());
//				columnsDisplayTable.repaint();
//				tableScrollPane.repaint();
				columnNameField.setText("");
				dataTypesComboBox.setSelectedIndex(0);
				updateAddColumnButton();
			}
		});
		
		removeColumnButton = new JButton("Remove");
		removeColumnButton.setEnabled(false);
		removeColumnButton.addActionListener(new ActionListener () {
			public void actionPerformed (ActionEvent event) {
				int selectedRow = columnsDisplayTable.getSelectedRow();
				columnsTableModel.removeRow(selectedRow);
//				columnsDisplayTable.repaint();
//				tableScrollPane.repaint();
			}
		});
		
		
		columnsDisplayTable = new JTable(columnsTableModel);
		columnsDisplayTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = columnsDisplayTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				updateRemoveColumnButton();
			}
		});
		JScrollPane tableScrollPane = new JScrollPane(columnsDisplayTable);
		columnsDisplayTable.setPreferredScrollableViewportSize(new Dimension(200, 200));
		

		columnSetupPanel.add(columnNameFieldLabel,new GridBagConstraints(
				0,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));
		columnSetupPanel.add(columnNameField,new GridBagConstraints(
				1,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));
				
		columnSetupPanel.add(dataTypeLabel,new GridBagConstraints(
				0,1,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));
		columnSetupPanel.add(dataTypesComboBox,new GridBagConstraints(
				1,1,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));
				
		columnSetupPanel.add(addColumnButton,new GridBagConstraints(
				1,2,1,1,1,0,
				GridBagConstraints.NORTHEAST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));

		columnSetupPanel.add(tableScrollPane,new GridBagConstraints(
				2,0,1,2,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.VERTICAL,
				new Insets(5, 5, 5, 5),
				2,2));

		columnSetupPanel.add(removeColumnButton,new GridBagConstraints(
				2,2,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5),
				2,2));
				
		return columnSetupPanel;
	}
	
	private JPanel buildButtonsPanel () {
		JPanel buttonsPanel = new JPanel();
		
		JButton okButton = new JButton("Import file data");
		okButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				importData();
				System.out.println("should be importing data into table here");
				dispose();
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener () {
			public void actionPerformed(ActionEvent event) {
				dispose();
			}
			
		});
		
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
		buttonsPanel.add(Box.createHorizontalGlue());
		buttonsPanel.add(okButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonsPanel.add(cancelButton);
		
		return buttonsPanel;		
	}

	private void setFieldSeparator (String separator) {
		this.fieldSeparator = separator;
	}
	
	private void updateAddColumnButton () {
		if ( (columnNameField.getText().length() > 0)  && 
					(dataTypesComboBox.getSelectedIndex() != 0) ){
			addColumnButton.setEnabled(true);						
		}
		else {
			addColumnButton.setEnabled(false);
		}	
	}

	private void updateRemoveColumnButton () {
		if (columnsDisplayTable.getSelectedRow() >= 0) {
			removeColumnButton.setEnabled(true);
		}
		else {
			removeColumnButton.setEnabled(false);
		}
	}
	
	private List getDatabaseTypes() {
		List res = new ArrayList();
		try {
			Collection typeNames = connection.getDatabaseSupportedTypeNames();
			Iterator it = typeNames.iterator();
			while (it.hasNext()) {
				SQLTypeInfo cur = (SQLTypeInfo) it.next();
				res.add(cur.getTypeName().toLowerCase());
			}
		}
		catch (DatabaseException e) {
			ErrorDialog.showError(this, e, "Error retrieving types supported by database");
		}
		return res;
	}
	
	private void importData () {
		String createTableStatement = "CREATE TEXT TABLE " + tableName + " {" + "\n";
		for (int i = 0; i < columnsTableModel.getRowCount(); i++ ) {
			createTableStatement = createTableStatement + columnsTableModel.getValueAt(i, 0) + 
									" " + columnsTableModel.getValueAt(i, 1);
			if (i != (columnsTableModel.getRowCount() - 1)) {
				createTableStatement = createTableStatement + "," + "\n";									
			}
		}
		createTableStatement += "};";
		
		String importDataStatement = "SET TABLE " + tableName + " SOURCE \"" + filename +
										";fs=" + this.fieldSeparator + "\"";

		//System.out.println("statement 1: \n" + createTableStatement);
		//System.out.println("statement 2: \n" + importDataStatement);										
		
	}
	
	private class DBTypesTableModel extends AbstractTableModel {
		
		final private String[] columnNames = {"Column Name","Data Type"};
		private List rows = new ArrayList();

		public int getRowCount() {
			return rows.size();
		}

		public int getColumnCount() {
			return columnNames.length;
		}
		
		public String getColumnName (int columnIndex) {
			return columnNames[columnIndex];
		}

		public Object getValueAt(int rowIndex, int colIndex) {
			List rowData = (List) rows.get(rowIndex);
			return rowData.get(colIndex);
		}
		
		public boolean isCellEditable(int rowIndex, int colIndex) {
			return true;
		}

//		public void setValueAt(Object value, int rowIndex, int colIndex) {
//			
//		}

		public void addRow (String newColNameStr, String colDataType) {
			List rowData = new ArrayList();
			rowData.add(0, newColNameStr);
			rowData.add(1, colDataType);
			rows.add(rowData);
			fireTableDataChanged();
		}
		
		public void removeRow (int rowIndex) {
			rows.remove(rowIndex);
			fireTableDataChanged();
		}
	}


}
