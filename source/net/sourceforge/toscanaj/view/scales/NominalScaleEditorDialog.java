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
import net.sourceforge.toscanaj.model.database.Column;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Iterator;

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

    public NominalScaleEditorDialog(Frame owner, Column column, DatabaseConnection databaseConnection) {
        super(owner);
        this.column = column;
        this.databaseConnection = databaseConnection;

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
        scaleTitleField.setText(column.getName() + " (nominal)");
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
        titlePane.add(new Label("Scale Title"), new GridBagConstraints(
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
        tablePane.add(new LabeledPanel("Available Values", this.columnValuesListView),
                new GridBagConstraints(
                        0, 0, 1, 1, 1, 1,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        tablePane.add(moveButtonPane,
                new GridBagConstraints(
                        1, 0, 1, 1, 0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.NONE,
                        new Insets(5, 5, 5, 5),
                        0, 0
                )
        );
        tablePane.add(new LabeledPanel("Selected Attributes", this.attributeListView),
                new GridBagConstraints(
                        2, 0, 1, 1, 1, 1,
                        GridBagConstraints.CENTER,
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
                result = false;
                hide();
            }
        });
        createButton = new JButton("Create");
        createButton.setEnabled(!scaleTitleField.getText().equals("") && 
        attributeListView.getModel().getSize()!=0);
        createButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                result = true;
                hide();
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
        pack();
    }

    private void addValuesToSelection() {
        for (int i = this.columnValuesListView.getSelectedValues().length - 1; i >= 0; i--) {
            Object o = this.columnValuesListView.getSelectedValues()[i];
            this.columnValuesListModel.removeElement(o);
            this.attributeListModel.addElement(o);
        }
    }

    private void removeValuesFromSelection() {
        for (int i = this.attributeListView.getSelectedValues().length - 1; i >= 0; i--) {
            Object o = this.attributeListView.getSelectedValues()[i];
            this.attributeListModel.removeElement(o);
            this.columnValuesListModel.addElement(o);
        }
    }

    private void fillControls() {
        // --- get a list of the values in column
        java.util.List resultSet = null;
        try {
            String query = "SELECT DISTINCT " + column.getName() + " FROM " +
                    column.getTable().getName() + ";";
            resultSet = databaseConnection.queryColumn(query, 1);
        } catch (DatabaseException e) {
        }

        for (Iterator it = resultSet.iterator(); it.hasNext();) {
            this.columnValuesListModel.addElement((String) it.next());
        }
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
	private class UpdateButtonForCorrectModelStateListDataListener implements ListDataListener {
			private final JButton actionButton;

			public UpdateButtonForCorrectModelStateListDataListener(JButton button) {
				this.actionButton = button;
			}

			private void updateStateOfOkButton() {
				actionButton.setEnabled(isScaleCorrect() && !scaleTitleField.getText().equals(""));
			}

			public void contentsChanged(ListDataEvent e) {
				updateStateOfOkButton();
			}

			public void intervalAdded(ListDataEvent e) {
				updateStateOfOkButton();
			}

			public void intervalRemoved(ListDataEvent e) {
				updateStateOfOkButton();
			}
		}
    
}
