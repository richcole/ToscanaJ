package net.sourceforge.toscanaj.view.scales;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

import org.tockit.swing.preferences.ExtendedPreferences;

/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
public class AttributeListScaleGeneratorDialog extends JDialog {
	private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(AttributeListScaleGeneratorDialog.class);

	private static final int MINIMUM_WIDTH = 550;
	private static final int MINIMUM_HEIGHT = 400;
	private static final int DEFAULT_X_POS = 50;
	private static final int DEFAULT_Y_POS = 100;
	private static final int DEFAULT_NUM_OF_COLS = 2;
	private static final int DEFAULT_NUM_OF_ROWS = 1;

	private boolean result;
	private boolean useAllCombinations;
	
	private JTextField scaleTitleField;
	private JButton createButton, removeButton;
	private JPanel buttonsPane, titlePane, optionsPane, tableButtonsPane;
	private JScrollPane scrollpane;
	private JTable table;
	
	public AttributeListScaleGeneratorDialog(Frame owner) {
		super(owner,true);
		createView();
	}

	private void createView() {
		setTitle("Attribute List");
		preferences.restoreWindowPlacement(this, new Rectangle(DEFAULT_X_POS, DEFAULT_Y_POS, MINIMUM_WIDTH, MINIMUM_HEIGHT));
		
		// to enforce the minimum size during resizing of the JDialog
		addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				if (width < MINIMUM_WIDTH) width = MINIMUM_WIDTH;
				if (height < MINIMUM_HEIGHT) height = MINIMUM_HEIGHT;
				setSize(width, height);
			}
			public void componentShown(ComponentEvent e) {
				componentResized(e);
			}
		});
		
		createTitlePane();
		createTablePane();
		createTableButtonsPane();
		createOptionsPane();
		createButtonsPane();

		getContentPane().setLayout(new GridBagLayout());
		getContentPane().add(
			titlePane,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				0,
				0));
		getContentPane().add(
			scrollpane,
			new GridBagConstraints(
				0,
				1,
				1,
				1,
				1,
				1,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(5, 5, 0, 5),
				0,
				0));
		getContentPane().add(
			tableButtonsPane,
			new GridBagConstraints(
				0,
				2,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 5, 0, 5),
				0,
				0));				
		getContentPane().add(
			optionsPane,
			new GridBagConstraints(
				0,
				3,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(1, 5, 5, 5),
				0,
				0));
		getContentPane().add(
			buttonsPane,
			new GridBagConstraints(
				0,
				4,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(1, 5, 5, 5),
				0,
				0));
	}

	private void createTablePane() {
		this.table = new JTable(new AttributeListTableModel());
		((DefaultCellEditor)table.getDefaultEditor(String.class)).setClickCountToStart(1); 
		AttributeListTableModel model = (AttributeListTableModel)table.getModel();
		model.setTable(table);
		this.scrollpane = new JScrollPane(table);
 	    this.scrollpane.setAutoscrolls(true);
		getContentPane().add(scrollpane, BorderLayout.CENTER);
	}
	
	private void createOptionsPane(){
		optionsPane = new JPanel(new GridBagLayout());
		JRadioButton allCombiButton = new JRadioButton("Use all possible combinations");
		allCombiButton.setMnemonic(KeyEvent.VK_A);
		allCombiButton.setSelected(true);
		useAllCombinations = true; //set to true by default
		allCombiButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				useAllCombinations = true;
			}
		});
		JRadioButton existingCombiButton = new JRadioButton("Use only combinations existing in the database");
		existingCombiButton.setMnemonic(KeyEvent.VK_E);
		existingCombiButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				useAllCombinations = false;
			}
		});
		ButtonGroup group = new ButtonGroup();
		group.add(allCombiButton);
		group.add(existingCombiButton);
		
		optionsPane.setBorder(BorderFactory.createEtchedBorder());
		optionsPane.add(
		allCombiButton,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 0),
				0,
				0));
		optionsPane.add(
		existingCombiButton,
			new GridBagConstraints(
				0,
				1,
				1,
				1,
				1,
				1,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 5, 0, 0),
				0,
				0));

	}

	private void createTitlePane() {
		titlePane = new JPanel(new GridBagLayout());
		JLabel titleLabel = new JLabel("Scale Title:");
		this.scaleTitleField = new JTextField();
		scaleTitleField.addKeyListener( new KeyListener(){
			private void validateTextField(){
				if(scaleTitleField.getText().trim().equals("")){
					createButton.setEnabled(false);
				}else{
					createButton.setEnabled(true);
				}
			}
			public void keyTyped(KeyEvent e) {
				validateTextField();
				setCreateButtonStatus();
			}
			public void keyReleased(KeyEvent e) {
				validateTextField();
				setCreateButtonStatus();
			}
			public void keyPressed(KeyEvent e) {}			
		});
		
		titlePane.add(
			titleLabel,
			new GridBagConstraints(
				0,
				0,
				1,
				1,
				0,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				0,
				0));
		titlePane.add(
			scaleTitleField,
			new GridBagConstraints(
				1,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				0,
				0));
	}
	
	private void createTableButtonsPane(){
		tableButtonsPane = new JPanel();
		this.removeButton = new JButton("Remove selected row");
		removeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(table.getCellEditor()!=null){
					table.getCellEditor().stopCellEditing();
				}
				AttributeListTableModel model = (AttributeListTableModel)table.getModel();
				model.removeSelectedRow(table.getSelectedRow());
			}
		});
		tableButtonsPane.add(removeButton);		
	}
	
	private void createButtonsPane() {
		buttonsPane = new JPanel(new GridBagLayout());
		this.createButton = new JButton(" Create ");
		createButton.setEnabled((scaleTitleField.getText()!=null && 
						!scaleTitleField.getText().equals("")));
		JButton cancelButton = new JButton(" Cancel ");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TableCellEditor cellEditor = table.getCellEditor();
				if(cellEditor!=null){
					cellEditor.stopCellEditing();
				}
				closeDialog(true);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog(false);
			}
		});
		buttonsPane.add(
			createButton,
			new GridBagConstraints(
				1,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, (this.getWidth()/2), 0, 5),
				0,
				0));
		buttonsPane.add(
			cancelButton,
			new GridBagConstraints(
				2,
				0,
				1,
				1,
				1,
				0,
				GridBagConstraints.EAST,
				GridBagConstraints.BOTH,
				new Insets(0, 5, 0, 0),
				0,
				0));
	}
	
	private void closeDialog(boolean result) {
        preferences.storeWindowPlacement(this);
		this.result = result;
		setVisible(false);
	}
	public boolean execute() {
		show();
		return result;
	}
	
	public boolean getUseAllCombinations(){
		return this.useAllCombinations;
	}
    
	/*
	 * Checks against the context whether there are any attributes/SQL clause
	 * pair. If either one doesn't exist, disable the button.
	 */ 
	protected void setCreateButtonStatus(){
		AttributeListTableModel model = (AttributeListTableModel)table.getModel();
		if(model.isEmpty() || !model.checkAllRowsFilled()){
			createButton.setEnabled(false);
		}else{
			if(!scaleTitleField.getText().equals("")){
				createButton.setEnabled(true);
			}else{
				createButton.setEnabled(false);
			}
		}
	}
			
	public String getDiagramTitle() {
			return this.scaleTitleField.getText();
	}
	

	/*
	 * Doesn't return any row with null values (eg: last row) pair. 
	 */
	public Object[][] getData(){
		AttributeListTableModel model = (AttributeListTableModel) this.table.getModel();
		return model.getTableData();
	}

	class AttributeListTableModel extends AbstractTableModel{
			private JTable table;
			final String[] columnNames = {"Label Name", "SQL Clause"};
			Object[][] modelData =  new Object[DEFAULT_NUM_OF_ROWS][DEFAULT_NUM_OF_COLS];

			public int getColumnCount() {
				return columnNames.length;
			}
        
			public int getRowCount() {
				return modelData.length;
			}

			public String getColumnName(int col) {
				return columnNames[col];
			}

			public Object getValueAt(int row, int col) {
				return modelData[row][col];
			}

			public boolean isCellEditable(int row, int col) {
					return true;
			}

			public void setValueAt(Object value, int row, int col) {

				if (modelData[0][col] instanceof Integer && !(value instanceof Integer)) {                  
					try {
						modelData[row][col] = new Integer(value.toString());
						fireTableCellUpdated(row, col);
					} catch (NumberFormatException e) {
						JOptionPane.showMessageDialog(AttributeListScaleGeneratorDialog.this,
							"The \"" + getColumnName(col)
							+ "\" column accepts only integer values.");
					}
				} else {
					modelData[row][col] = value;
					fireTableCellUpdated(row, col);
				}
				if(row == this.getRowCount()-1){
					String cellContent = (String)table.getValueAt(row,col);
					if(cellContent!=null && !cellContent.trim().equals("")){
						createNewRow();	
					}
				}
				setCreateButtonStatus();
			}
			
			private void createNewRow(){
				Object[][] newData = new Object[this.getRowCount()+1][this.getColumnCount()];
				for(int row = 0; row< modelData.length; row++){
					for(int col = 0 ; col < modelData[row].length ; col++){
						newData[row][col] = modelData[row][col];
 					}
				}
				newData[this.getRowCount()][this.getColumnCount()-1] = null;
				modelData = newData;
			}
			
			protected void setTable(JTable table){
				this.table = table;
			}
						
			//removes last row of null values before returning
			protected Object[][] getTableData(){
				Object[][] returnData = new Object[getRowCount()-1][getColumnCount()]; 
				for(int row = 0; row < returnData.length; row++){
					for(int col = 0; col < returnData[row].length; col++){
						returnData[row][col] = this.modelData[row][col];
					}
				}
				return returnData;
			}
			
			//returns true if all the cells are empty
			protected boolean isEmpty(){
				boolean isEmpty = true;
				for(int row = 0; row< this.modelData.length; row++){
					for(int col = 0 ; col < this.modelData[row].length ; col++){
						if(modelData[row][col] != null){
							String cellValue = (String) this.modelData[row][col];
							if(!cellValue.trim().equals("")){
								return false;
							}
						}
					}
				}
				return isEmpty;		
			}
			
			/* 
			 * Checks whether the cells of the row at rowNumber has been filled
			 * with values. Returns true if it's populated with data.
			 */
			protected boolean isRowFilled(int rowNumber){
				boolean isFilled = true;
				for(int col =0; col<this.modelData[rowNumber].length; col++){
					String cellValue = (String) modelData[rowNumber][col];
					if(cellValue == null || cellValue.trim().equals("")){
						isFilled = false;
					}
				}
				return isFilled;
			}
			/*
			 * Validates whether the cells are filled with values, with the
			 * exception that the last row can be empty. If the last row has
			 * some cells filled, it returns false. returns true if it's valid
			 * to have its cell values to be converted to a lattice.
			 */
			protected boolean checkAllRowsFilled(){
				boolean allFilled = true;
				for(int row = 0; row<this.modelData.length; row++){
					if(row!= this.modelData.length-1){
						allFilled = isRowFilled(row);
						if(!allFilled){
							break;
						}
					}else{
						// check last row. not necessarily empty. 
						if(isRowFilled(this.modelData.length-1)){
							allFilled = false;
						}
					}
				}
				return allFilled;
			}
			
			protected void removeSelectedRow(int row){
				if(row==-1 || row == (this.modelData.length-1)){
					return;			
				}
				Object[][] newModel = new Object[getRowCount() - 1][getColumnCount()];
				for (int i = 0; i < newModel.length; i++) {
					if(i < row) {
						newModel[i] = this.modelData[i];
					} else {
						newModel[i] = this.modelData[i+1];
					}
				}
				this.modelData = newModel;
				fireTableRowsDeleted(row,row);
				setCreateButtonStatus();
			}
		}
}

