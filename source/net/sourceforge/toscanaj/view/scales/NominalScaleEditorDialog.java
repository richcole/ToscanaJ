/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Types;
import java.util.Iterator;
import java.util.List;

public class NominalScaleEditorDialog extends JDialog {
    private boolean result;

    private Column column;
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
    
	private static final String CONFIGURATION_SECTION_NAME = "NominalScaleEditorDialog";
	private static final int MINIMUM_WIDTH = 500;
	private static final int MINIMUM_HEIGHT = 300;
	private static final int DEFAULT_X_POS = 10;
	private static final int DEFAULT_Y_POS = 10;
	
    public interface SqlFragment {
    	String getAttributeLabel();
    	String getSqlClause();
    	String getClosedAttributeLabel();
    }
    
    private static class TableColumnValueTriple implements SqlFragment{
    	private TableColumnPair tableColumnPair;
    	private String value;
    	public TableColumnValueTriple(TableColumnPair tableColumnPair, String value) {
    		this.tableColumnPair = tableColumnPair;
    		this.value = value;
    	}
        public TableColumnPair getTableColumnPair() {
            return tableColumnPair;
        }
        public String getValue() {
            return value;
        }
        public String toString() {
        	return this.getSqlClause();
        }
        public String getAttributeLabel() {
            return this.value;
        }
        public String getSqlClause() {
        	if(tableColumnPair.getColumn().getType() == Types.VARCHAR) {
            	return this.tableColumnPair.toString() + " = '" + this.value + "'";
        	} else {
				return this.tableColumnPair.toString() + " = " + this.value;
        	}
        }
        public String getClosedAttributeLabel() {
            return this.getAttributeLabel();
        }
    }
    
    private static class Disjunction implements SqlFragment {
        private SqlFragment firstPart;
        private SqlFragment secondPart;
        public Disjunction(SqlFragment firstPart, SqlFragment secondPart) {
            this.firstPart = firstPart;
            this.secondPart = secondPart;
        }
        public String getAttributeLabel() {
            return this.firstPart.getClosedAttributeLabel() + " or " + this.secondPart.getClosedAttributeLabel();
        }
        public String getSqlClause() {
            return "(" + this.firstPart.getSqlClause() + ") OR (" + this.secondPart.getSqlClause() + ")";
        }
        public String toString() {
        	return getSqlClause();
        }
        public String getClosedAttributeLabel() {
            return "(" + this.getAttributeLabel() + ")";
        }
    }

    private static class Conjunction implements SqlFragment {
        private SqlFragment firstPart;
        private SqlFragment secondPart;
        public Conjunction(SqlFragment firstPart, SqlFragment secondPart) {
            this.firstPart = firstPart;
            this.secondPart = secondPart;
        }
        public String getAttributeLabel() {
            return this.firstPart.getClosedAttributeLabel() + " and " + this.secondPart.getClosedAttributeLabel();
        }
        public String getSqlClause() {
            return "(" + this.firstPart.getSqlClause() + ") AND (" + this.secondPart.getSqlClause() + ")";
        }
        public String toString() {
            return getSqlClause();
        }
        public String getClosedAttributeLabel() {
            return "(" + this.getAttributeLabel() + ")";
        }
    }

    public NominalScaleEditorDialog(Frame owner, DatabaseConnection databaseConnection,
                                     DatabaseSchema databaseSchema) {
        super(owner);
        this.databaseConnection = databaseConnection;
        this.databaseSchema = databaseSchema;

		ConfigurationManager.restorePlacement(CONFIGURATION_SECTION_NAME, 
		this, new Rectangle(DEFAULT_X_POS, DEFAULT_Y_POS, MINIMUM_WIDTH, MINIMUM_HEIGHT));
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
        		if(column != cb.getSelectedItem()) {
        			column = ((TableColumnPair) cb.getSelectedItem()).getColumn();
        			fillAvailableValueList();
        		}
            }
        });

        JPanel tablePane = new JPanel(new GridBagLayout());
        this.columnValuesListModel = new DefaultListModel();
        this.columnValuesListView = new JList(columnValuesListModel);
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
                boolean combinationPossible = attributeListView.getSelectedIndices().length == 2;
                andButton.setEnabled(combinationPossible);
                orButton.setEnabled(combinationPossible);
            }
		});
        pack();
    }
	private void closeDialog(boolean res) {
		ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME,this);
		result = res;
		hide();
	}
    private void addValuesToSelection() {
        for (int i = this.columnValuesListView.getSelectedValues().length - 1; i >= 0; i--) {
            String value = (String)this.columnValuesListView.getSelectedValues()[i];
            TableColumnPair tabCol = (TableColumnPair) this.columnChooser.getSelectedItem();
            this.attributeListModel.addElement(new TableColumnValueTriple(tabCol, value));
        }
        fillAvailableValueList();
    }

    private void removeValuesFromSelection() {
        for (int i = this.attributeListView.getSelectedValues().length - 1; i >= 0; i--) {
            Object selectedItem = this.attributeListView.getSelectedValues()[i];
            this.attributeListModel.removeElement(selectedItem);
        }
    }

    private void fillControls() {
        this.columnChooser.removeAllItems();
        List tables = this.databaseSchema.getTables();
        for (Iterator tableIterator = tables.iterator(); tableIterator.hasNext();) {
            Table table = (Table) tableIterator.next();
            List columns = table.getColumns();
            for (Iterator columnsIterator = columns.iterator(); columnsIterator.hasNext();) {
                Column column = (Column) columnsIterator.next();
                this.columnChooser.addItem(new TableColumnPair(table, column));
            }
            fillAvailableValueList();
        }
    }

	/**
	 * @todo we need caching here
	 */
    private void fillAvailableValueList() {
        this.columnValuesListModel.clear();
        if(column == null) {
        	return;
        }
        TableColumnPair tabCol = (TableColumnPair) this.columnChooser.getSelectedItem();
        List resultSet = null;
        try {
            String query = "SELECT DISTINCT " + column.getSqlExpression() + " FROM " +
                    column.getTable().getName() + ";";
            resultSet = databaseConnection.queryColumn(query, 1);
            for (Iterator it = resultSet.iterator(); it.hasNext();) {
            	String value = (String) it.next();
            	if(!valueSelected(tabCol, value)) {
                	this.columnValuesListModel.addElement(value);
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
        Object[] selectedValues = this.attributeListView.getSelectedValues();
        SqlFragment firstPart = (SqlFragment) selectedValues[0];
        SqlFragment secondPart = (SqlFragment) selectedValues[1];
        int pos = this.attributeListView.getSelectedIndices()[0];
        this.attributeListModel.removeElement(firstPart);
        this.attributeListModel.removeElement(secondPart);
        this.attributeListModel.add(pos, new Conjunction(firstPart,secondPart));
    }

    private void createDisjunction() {
        SqlFragment firstPart = (SqlFragment) this.attributeListView.getSelectedValues()[0];
        SqlFragment secondPart = (SqlFragment) this.attributeListView.getSelectedValues()[1];
        int pos = this.attributeListView.getSelectedIndices()[0];
        this.attributeListModel.removeElement(firstPart);
        this.attributeListModel.removeElement(secondPart);
        this.attributeListModel.add(pos, new Disjunction(firstPart,secondPart));
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
