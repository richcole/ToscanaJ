/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.tockit.swing.preferences.ExtendedPreferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

public class NominalScaleEditorDialog extends JDialog {
    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(NominalScaleEditorDialog.class);
    
    /**
     * This is used to figure out if multiple columns are involved and we need
     * to add the column names to the attribute labels.
     * 
     * This is a hack to get more things going, but in the end it is just another
     * sign that the nominal scale generator as it is is no good. 
     */
    private static boolean multipleColumnsUsed;
    private Column columnUsed;
    
    private boolean result;

    private TableColumnPair selectedTableColumnPair;
    private DatabaseConnection databaseConnection;

    private JList columnValuesListView;
    private JList attributeListView;
    private DefaultListModel columnValuesListModel;
    private DefaultListModel attributeListModel;
	private JButton cancelButton;
	private JButton createButton;

    private JTextField scaleTitleField;
    private JComboBox columnChooser;
    private DatabaseSchema databaseSchema;
    private JButton andButton;
    private JButton orButton;
    private JButton notButton;
    
	private static final int MINIMUM_WIDTH = 500;
	private static final int MINIMUM_HEIGHT = 300;
	private static final Rectangle DEFAULT_PLACEMENT = new Rectangle(10,10,MINIMUM_WIDTH, MINIMUM_HEIGHT);
	
    public interface SqlFragment {
    	String getAttributeLabel();
    	String getSqlClause();
    	String getClosedAttributeLabel();
    }
    
    private interface ColumnValue {
    	String getValue();
    	String getSqlClause(Column column);
    	Font deriveFont(Font f);
    }
    
    private class OrdinaryColumnValue implements ColumnValue {
    	private String value;
    	public OrdinaryColumnValue (String value) {
    		this.value = value;
    	}
		public String getValue() {
			return this.value;
		}
		public String getSqlClause(Column column) {
			if(column.getType() == Types.VARCHAR) {
				return " = '" + this.value + "'";
			} else {
				return " = " + this.value;
			}
		}
		public Font deriveFont(Font f) {
			return f;
		}
    }    

	private class NullColumnValue implements ColumnValue {
		private String value;
		public NullColumnValue (String value) {
			this.value = value;
		}
		public String getValue() {
			return this.value;
		}
		public String getSqlClause(Column column) {
			return " IS " + this.value;
		}
		public Font deriveFont(Font f) {
			Font resFont = f.deriveFont(Font.ITALIC);
			return resFont;
		}
	}

    private static class TableColumnValueTriple implements SqlFragment{
    	private TableColumnPair tableColumnPair;
    	private ColumnValue colValue;
    	public TableColumnValueTriple(TableColumnPair tableColumnPair, ColumnValue colVallue) {
    		this.tableColumnPair = tableColumnPair;
    		this.colValue = colVallue;
    	}
        public TableColumnPair getTableColumnPair() {
            return tableColumnPair;
        }
        public String getValue() {
            return colValue.getValue();
        }
        public String toString() {
        	return this.getSqlClause();
        }
        public String getAttributeLabel() {
            if(multipleColumnsUsed) {
                return this.tableColumnPair.getColumn().getDisplayName() + ": " + this.colValue.getValue();
            } else {
                return this.colValue.getValue();
            }
        }
        public String getSqlClause() {
			return this.tableColumnPair.getSqlExpression() + this.colValue.getSqlClause(tableColumnPair.getColumn());
        }
        public String getClosedAttributeLabel() {
            return this.getAttributeLabel();
        }
    }
        
    private static class Disjunction implements SqlFragment {
		private SqlFragment [] sqlFragments;

		public Disjunction(SqlFragment[] sqlFragments) {
			this.sqlFragments = sqlFragments;
		}
        
        public String getAttributeLabel() {
			String res = "";
			for (int i = 0; i < this.sqlFragments.length; i++) {
				SqlFragment curFragment = sqlFragments[i];
				if (i == 0) {
					res = curFragment.getClosedAttributeLabel();
				}
				else {
					res = res + " or " + curFragment.getClosedAttributeLabel();
				}
			}
			return res;
        }

        public String getSqlClause() {
			String res = "";
			for (int i = 0; i < this.sqlFragments.length; i++) {
				SqlFragment curFragment = sqlFragments[i];
				if (i == 0) {
					res = "(" + curFragment.getSqlClause() + ")";
				}
				else {
					res = res + " OR (" + curFragment.getSqlClause() + ")";
				}
			}
			return res;
        }

        public String toString() {
        	return getSqlClause();
        }

        public String getClosedAttributeLabel() {
            return "(" + this.getAttributeLabel() + ")";
        }
    }

    private static class Conjunction implements SqlFragment {
        private SqlFragment [] sqlFragments;
		public Conjunction(SqlFragment[] sqlFragments) {
			this.sqlFragments = sqlFragments;
		}

        public String getAttributeLabel() {
			String res = "";
			for (int i = 0; i < this.sqlFragments.length; i++) {
				SqlFragment curFragment = sqlFragments[i];
				if (i == 0) {
					res = curFragment.getClosedAttributeLabel();
				}
				else {
					res = res + " and " + curFragment.getClosedAttributeLabel();
				}
			}
            return res;
        }

        public String getSqlClause() {
			String res = "";
			for (int i = 0; i < this.sqlFragments.length; i++) {
				SqlFragment curSqlFragment = sqlFragments[i];
				if (i == 0) {
					res = "(" + curSqlFragment.getSqlClause() + ")";
				}
				else {
					res = res + " AND (" + curSqlFragment.getSqlClause() + ")";
				}
			}
			return res;
        }

        public String toString() {
            return getSqlClause();
        }

        public String getClosedAttributeLabel() {
            return "(" + this.getAttributeLabel() + ")";
        }
    }

	private static class Negation implements SqlFragment {
		private SqlFragment sqlFragment;
		public Negation(SqlFragment sqlFragment) {
			this.sqlFragment = sqlFragment;
		}

		public String getAttributeLabel() {
			return "NOT " + this.sqlFragment.getClosedAttributeLabel();
		}

		public String getSqlClause() {
			return "NOT (" + this.sqlFragment.getSqlClause() + ")";
		}

		public String toString() {
			return getSqlClause();
		}

		public String getClosedAttributeLabel() {
			return "(" + this.getAttributeLabel() + ")";
		}
	}
	
	private class ColumnValuesListRenderer extends DefaultListCellRenderer {
		public Component getListCellRendererComponent(JList list, Object value, 
										int index,	boolean isSelected, 
										boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			ColumnValue colValue = (ColumnValue) value;
			setText(colValue.getValue());
			setFont(colValue.deriveFont(list.getFont()));
			return this;
		}
		
	}

    public NominalScaleEditorDialog(Frame owner, DatabaseConnection databaseConnection,
                                     DatabaseSchema databaseSchema) {
        super(owner);
        this.databaseConnection = databaseConnection;
        this.databaseSchema = databaseSchema;
        
        multipleColumnsUsed = false;
        this.columnUsed = null;

        preferences.restoreWindowPlacement(this, DEFAULT_PLACEMENT);
		//	to enforce the minimum size during resizing of the JDialog
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

        createControls();
        fillControls();

        result = false;
    }

    private void createControls() {
        setModal(true);
        setTitle("Nominal Scale Generator");
        getContentPane().setLayout(new GridBagLayout());

        // -- title pane ---
        this.scaleTitleField = new JTextField();
        this.scaleTitleField.addKeyListener(new KeyListener(){
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
        JPanel titlePane = new JPanel(new GridBagLayout());
        titlePane.add(new Label("Scale Title: "), new GridBagConstraints(
        				0,0,1,1,0,0,
        				GridBagConstraints.NORTHWEST,
        				GridBagConstraints.NONE,
        				new Insets(2,2,2,2),
        				2,2
        ));
        
        titlePane.add(scaleTitleField, new GridBagConstraints(
        				1,0,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(2,2,2,2),
						2,2
		));
		
		
        getContentPane().add(
                titlePane,
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );

        this.columnChooser = new JComboBox();
        this.columnChooser.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		JComboBox cb = (JComboBox) e.getSource();
        		Object newTableColumnPair = cb.getSelectedItem();
                if(newTableColumnPair != selectedTableColumnPair && newTableColumnPair instanceof TableColumnPair) {
        			selectedTableColumnPair = (TableColumnPair) newTableColumnPair;
        			Object firstItem = cb.getItemAt(0);
        			if(! (firstItem instanceof TableColumnPair) ) {
        				cb.removeItemAt(0);
        			}
        		} else {
        			selectedTableColumnPair = null;
        		}
				fillAvailableValueList();
            }
        });

        JPanel tablePane = new JPanel(new GridBagLayout());
        this.columnValuesListModel = new DefaultListModel();
        this.columnValuesListView = new JList(columnValuesListModel);
		this.columnValuesListView.setCellRenderer(new ColumnValuesListRenderer());
        this.columnValuesListView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    addValuesToSelection();
                }
            }
        });
        
        JPanel moveButtonPane = new JPanel(new GridLayout(2, 1));
        JButton addButton = new JButton(">");
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addValuesToSelection();
            }
        });
        JButton removeButton = new JButton("<");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeValuesFromSelection();
            }
        });
        moveButtonPane.add(addButton);
        moveButtonPane.add(removeButton);
        this.attributeListModel = new DefaultListModel();
        this.attributeListView = new JList(attributeListModel);
        this.attributeListView.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 2) {
                    removeValuesFromSelection();
                }
            }
        });
        
        JPanel combinationButtonPanel = new JPanel(new GridBagLayout());
        this.andButton = new JButton("AND");
        this.andButton.setEnabled(false);
        this.andButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	createConjunction();
            }
        });
        
        this.orButton = new JButton("OR");
        this.orButton.setEnabled(false);
        this.orButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                createDisjunction();
            }
        });
        
        this.notButton = new JButton("NOT");
        this.notButton.setEnabled(false);
        this.notButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createNegation();
			}
        });

        
        combinationButtonPanel.add(this.andButton,
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        combinationButtonPanel.add(this.orButton,
                new GridBagConstraints(
                        1, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
		combinationButtonPanel.add(this.notButton,
				new GridBagConstraints(
						2, 0, 1, 1, 1, 0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL,
						new Insets(5, 5, 5, 5),
						0, 0
				)
		);
        
        tablePane.add(new LabeledPanel("Columns", this.columnChooser, false),
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        tablePane.add(new LabeledPanel("Available Values", this.columnValuesListView),
                new GridBagConstraints(
                        0, 1, 1, 2, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        tablePane.add(moveButtonPane,
                new GridBagConstraints(
                        1, 0, 1, 3, 0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        
        JPanel selectedClausePane = new JPanel(new BorderLayout());
        selectedClausePane.add(new LabeledPanel("Selected Clauses",this.attributeListView),BorderLayout.CENTER);
        selectedClausePane.add(combinationButtonPanel, BorderLayout.SOUTH);
        
        tablePane.add(selectedClausePane,
                new GridBagConstraints(
                        2, 0 , 1, 3, 1, 1,
                        GridBagConstraints.NORTHWEST,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        tablePane.setBorder(BorderFactory.createEtchedBorder());
        
        getContentPane().add(
                tablePane,
                new GridBagConstraints(
                        0, 1, 1, 1, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                closeDialog(false);
            }
        });
        createButton = new JButton("Create");
        createButton.setEnabled(!scaleTitleField.getText().equals("") && 
        attributeListView.getModel().getSize()!=0);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
				closeDialog(true);
            }
        });
	

        
		buttonPane.add(createButton);
        buttonPane.add(cancelButton);
        getContentPane().add(
                buttonPane,
                new GridBagConstraints(
                        0, 2, 1, 1, 1, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        
		attributeListView.getModel().addListDataListener(new UpdateButtonForCorrectModelStateListDataListener(createButton));
		attributeListView.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
				boolean combinationPossible = attributeListView.getSelectedIndices().length >= 2;
                andButton.setEnabled(combinationPossible);
                orButton.setEnabled(combinationPossible);
                boolean onlyOneItemSelected = attributeListView.getSelectedIndices().length == 1;
                notButton.setEnabled(onlyOneItemSelected);
            }
		});
        pack();
    }
    
	private void closeDialog(boolean res) {
        preferences.storeWindowPlacement(this);
		result = res;
		hide();
	}
	
    private void addValuesToSelection() {
        for (int i = this.columnValuesListView.getSelectedValues().length - 1; i >= 0; i--) {
			ColumnValue value = (ColumnValue)this.columnValuesListView.getSelectedValues()[i];
            this.attributeListModel.addElement(new TableColumnValueTriple(this.selectedTableColumnPair, value));
            if(this.columnUsed == null) {
                this.columnUsed = this.selectedTableColumnPair.getColumn();
            } else if (this.columnUsed != this.selectedTableColumnPair.getColumn()) {
                multipleColumnsUsed = true;
            }
        }
        fillAvailableValueList();
    }

    private void removeValuesFromSelection() {
        for (int i = this.attributeListView.getSelectedValues().length - 1; i >= 0; i--) {
            Object selectedItem = this.attributeListView.getSelectedValues()[i];
            this.attributeListModel.removeElement(selectedItem);
        }
        fillAvailableValueList();
    }

    private void fillControls() {
        this.columnChooser.removeAllItems();
        this.columnChooser.addItem("<Please select a column>");
        List tables = this.databaseSchema.getTables();
        for (Iterator tableIterator = tables.iterator(); tableIterator.hasNext();) {
            Table table = (Table) tableIterator.next();
            List columns = table.getColumns();
            for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext();) {
                Column column = (Column) columnsIterator.next();
                this.columnChooser.addItem(new TableColumnPair(table, column));
            }
        }
    }

	/**
	 * @todo we need caching here
	 */
    private void fillAvailableValueList() {
        this.columnValuesListModel.clear();
        if(selectedTableColumnPair == null) {
        	return;
        }
        List resultSet = null;
        try {
            String query = "SELECT DISTINCT " + selectedTableColumnPair.getSqlExpression() + " FROM " +
                    selectedTableColumnPair.getTable().getSqlExpression() + ";";
            resultSet = databaseConnection.queryColumn(query, 1);
			Iterator it = resultSet.iterator();
			if (it.hasNext()) {
				if (checkIfColumnAllowsNullValues()) {
					this.columnValuesListModel.addElement(new NullColumnValue("NULL"));
				}
			}
            while ( it.hasNext()) {
            	String value = (String) it.next();
            	if(!valueSelected(selectedTableColumnPair, value)) {
                	this.columnValuesListModel.addElement(new OrdinaryColumnValue(value));
            	}
            }
        } catch (DatabaseException e) {
            ErrorDialog.showError(this, e, "Database query failed");
        }
    }

    
    private boolean valueSelected(TableColumnPair tabCol, String value) {
    	for(int i = 0; i < this.attributeListModel.size(); i++) {
    		if(this.attributeListModel.get(i) instanceof TableColumnValueTriple) {
	    		TableColumnValueTriple tcv = (TableColumnValueTriple) this.attributeListModel.get(i);
	    		if( tcv.getTableColumnPair().equals(tabCol) && tcv.getValue().equals(value)) {
	    			return true;
	    		}
    		}
    	}
        return false;
    }

    public boolean execute() {
        result = false;
        show();
        return result;
    }

    public Object[] getValues() {
        return this.attributeListModel.toArray();
    }

    public String getDiagramTitle() {
        return this.scaleTitleField.getText();
    }
    
	protected void setCreateButtonStatus(){
		if(scaleTitleField.getText().equals("") && attributeListView.getModel().getSize()==0){
			createButton.setEnabled(false);
		}else{
			if(!scaleTitleField.getText().equals("") && attributeListView.getModel().getSize()!=0){
				createButton.setEnabled(true);
			}else{
				createButton.setEnabled(false);
			}
		}
	}    
	
	private boolean isScaleCorrect() {
		  return attributeListView.getModel().getSize() > 0;
	}
	
    private void createConjunction() {
		int pos = this.attributeListView.getSelectedIndices()[0];
    	Object[] selectedValues = this.attributeListView.getSelectedValues();
		SqlFragment[] selectedFragments = new SqlFragment[selectedValues.length];
		for (int i = 0; i < selectedValues.length; i++) {
			Object curValue = selectedValues[i];
			SqlFragment fragment = (SqlFragment) curValue;
			selectedFragments[i] = fragment;
			this.attributeListModel.removeElement(fragment);
		}
		this.attributeListModel.add(pos, new Conjunction(selectedFragments));
		fillAvailableValueList();
    }

    private void createDisjunction() {
		int pos = this.attributeListView.getSelectedIndices()[0];
		Object[] selectedValues = this.attributeListView.getSelectedValues();
        SqlFragment[] selectedFragments = new SqlFragment[selectedValues.length];
        for (int i = 0; i < selectedValues.length; i++) {
			Object curValue = selectedValues[i];
			SqlFragment fragment = (SqlFragment) curValue;
			selectedFragments[i] = fragment;
			this.attributeListModel.removeElement(fragment);
		}
		this.attributeListModel.add(pos, new Disjunction(selectedFragments));
		fillAvailableValueList();
    }

	private void createNegation() {
		int pos = this.attributeListView.getSelectedIndex();
		SqlFragment sqlFragment = (SqlFragment) this.attributeListView.getSelectedValue();
		this.attributeListModel.removeElement(sqlFragment);
		this.attributeListModel.add(pos, new Negation(sqlFragment));
		fillAvailableValueList();
	}

	private boolean checkIfColumnAllowsNullValues()
												throws DatabaseException {
		boolean columnAllowsNulls = true;
		DatabaseMetaData dbMetadata = databaseConnection.getDatabaseMetaData();
		try {
			String tableName = selectedTableColumnPair.getTable().getDisplayName();
			String colName = selectedTableColumnPair.getColumn().getDisplayName(); 
			ResultSet rs = dbMetadata.getColumns(null, null, tableName, colName);
			while (rs.next()) {
				ResultSetMetaData cur = rs.getMetaData();
				String curTableName = "";
				String curColName = "";
				String curIsNullable = "";
				for (int i = 1; i <= cur.getColumnCount(); i++) {
					if (cur.getColumnName(i).equals("TABLE_NAME")) {
						curTableName = rs.getString(i);									
					}
					if (cur.getColumnName(i).equals("COLUMN_NAME")) {
						curColName = rs.getString(i);
					}
					if (cur.getColumnName(i).equals("IS_NULLABLE")){
						curIsNullable = rs.getString(i);
					}
				}
				if ( (curTableName.equals(tableName)) && (curColName.equals(colName)) ) {
					// check now if column allows NULL values. This is tricky
					// as JDBC doesn't return very good values, here is a sniplet from
					// javadoc:
					// "NO" means column definitely does not allow NULL values; 
					// "YES" means the column might allow NULL values. 
					// An empty string means nobody knows.
					if (curIsNullable.equals("NO")) {
						columnAllowsNulls = false;
					}
				}
			}
		} catch (SQLException e1) {
			ErrorDialog.showError(this, e1, "Database query failed");
		}
		return columnAllowsNulls;
	}

	private class UpdateButtonForCorrectModelStateListDataListener implements ListDataListener {
			private final JButton actionButton;

			public UpdateButtonForCorrectModelStateListDataListener(JButton button) {
				this.actionButton = button;
			}

			private void updateStateOfButtons() {
				actionButton.setEnabled(isScaleCorrect() && !scaleTitleField.getText().equals(""));
			}

			public void contentsChanged(ListDataEvent e) {
				updateStateOfButtons();
			}

			public void intervalAdded(ListDataEvent e) {
				updateStateOfButtons();
			}

			public void intervalRemoved(ListDataEvent e) {
				updateStateOfButtons();
			}
		}
    
}
