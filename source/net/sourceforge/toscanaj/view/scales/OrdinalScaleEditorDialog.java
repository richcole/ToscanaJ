/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

public class OrdinalScaleEditorDialog extends JDialog {
    boolean result;
    private JTextField titleEditor = new JTextField();

    private JButton addButton;
    private NumberField addField;
    private JList dividersList;
    private int scaleType;
    private JButton okButton; 

    public static final int INTEGER = 0;
    public static final int FLOAT = 1;
    public static final int UNSUPPORTED = -1;

    public OrdinalScaleEditorDialog(Frame owner, String scaleName, int scaleType) {
        super(owner);
      	setSize(400,600);
      	setLocation(200,100);
        this.scaleType = scaleType;
        layoutDialog(scaleName);
        pack();
    }

    public boolean execute() {
        result = false;
        show();
        return result;
    }

    private void layoutDialog(String scaleName) {
        setModal(true);
        setTitle("Ordinal scale editor");
        JPanel mainPane = new JPanel(new GridBagLayout());
        
		JPanel titlePane = new JPanel(new GridBagLayout());
        titleEditor.setText(scaleName + " (ordinal)");
		this.titleEditor.addKeyListener(new KeyListener(){
			private void validateTextField(){
				if(titleEditor.getText().equals("") || dividersList.getModel().getSize()==0){
					okButton.setEnabled(false);
				}else{
					okButton.setEnabled(true);
				}
			}
			public void keyTyped(KeyEvent e) {
				validateTextField();
			}
			public void keyReleased(KeyEvent e) {
				validateTextField();
			}
			public void keyPressed(KeyEvent e) {}		
		});
        JLabel title = new JLabel("Title");
        titlePane.add(title, new GridBagConstraints(
        					0,0,1,1,0,0,
        					GridBagConstraints.NORTHWEST,
        					GridBagConstraints.NONE,
        					new Insets(2,2,2,2),
        					2,2      
        ));
        
		titlePane.add(titleEditor, new GridBagConstraints(
							 0,1,1,1,1.0,0,
							 GridBagConstraints.WEST,
							 GridBagConstraints.HORIZONTAL,
							 new Insets(2,2,2,2),
							 2,2      
		 ));
        
        mainPane.add(titlePane,new GridBagConstraints(
					0,0,2,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		mainPane.add(makeCenterPane(), new GridBagConstraints(
					0,1,1,1,1.0,1.0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints .BOTH,
					new Insets(2,2,2,0),
					2,2
		));
		
		
		mainPane.add(makeButtonsPane(), new GridBagConstraints(
					0,3,2,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints .HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		setContentPane(mainPane);

    }

	protected JPanel makeCenterPane() {
		JPanel dividerPane = new JPanel(new GridBagLayout());
		dividersModel = new DefaultListModel();
		
		dividersList = new JList(dividersModel);
		dividersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		
		dividerPane.add(new JScrollPane(dividersList),new GridBagConstraints(
							0,1,1,1,1.0,1.0,
							GridBagConstraints.WEST,
							GridBagConstraints.BOTH,
							new Insets(2,2,2,0),
							2,2
		));
		
		final JButton removeButton = new JButton("Remove");
		removeButton.setEnabled(hasSelectedDivider());
		
		dividersList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			   public void valueChanged(ListSelectionEvent e) {
				   removeButton.setEnabled(e.getFirstIndex() != -1);
			   }
		   });
		   dividersModel.addListDataListener(new ListDataListener() {
			   private void updateRemoveButton() {
				   removeButton.setEnabled(dividersModel.getSize() > 0 && hasSelectedDivider());
			   }
		
			   public void intervalAdded(ListDataEvent e) {
				   updateRemoveButton();
			   }
		
			   public void intervalRemoved(ListDataEvent e) {
				   updateRemoveButton();
			   }
		
			   public void contentsChanged(ListDataEvent e) {
				   updateRemoveButton();
			   }
		   });
		
		   JPanel removeButtonPane = new JPanel(new GridBagLayout());
		   
		
		   removeButton.addActionListener(new ActionListener() {
			   public void actionPerformed(ActionEvent e) {
				   int[] selected = dividersList.getSelectedIndices();
				   for (int i = selected.length; --i >= 0;) {
					   removeDivider(selected[i]);
				   }
			   }
		   });
		
			final JButton removeAllButton = new JButton("Remove All");
			removeAllButton.setEnabled(false);
		    dividersModel.addListDataListener(new ListDataListener() {
			  private void updateRemoveButton() {
				  removeAllButton.setEnabled(dividersModel.size() != 0);
			  }

			  public void intervalAdded(ListDataEvent e) {
				  updateRemoveButton();
			  }

			  public void intervalRemoved(ListDataEvent e) {
				  updateRemoveButton();
			  }

			  public void contentsChanged(ListDataEvent e) {
				  updateRemoveButton();
			  }
		  });
				removeAllButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						removeAllDividers();
				}
			});
			
		removeButtonPane.add(removeButton, new GridBagConstraints(
					0,0,1,1,1.0,0,
					GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		removeButtonPane.add(removeAllButton, new GridBagConstraints(
					1,0,1,1,1.0,0,
					GridBagConstraints.WEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		JPanel centerPane = new JPanel(new GridBagLayout());
		
		
		
		centerPane.add(makeAddDividerPanel(), new GridBagConstraints(
					0,0,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints .HORIZONTAL,
					new Insets(2,0,2,0),
					2,2
		));
		centerPane.add(dividerPane, new GridBagConstraints(
					1,0,1,1,1.0,1.0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,0),
					2,2
		)); 
		
		centerPane.add(new JPanel(), new GridBagConstraints(
					0,1,1,1,1.0,1.0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,0),
					2,2
		));
		centerPane.add(removeButtonPane, new GridBagConstraints(
					1,1,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Divider");
		titledBorder.setTitleJustification(TitledBorder.RIGHT);
		centerPane.setBorder(titledBorder);
		return centerPane;
	}

    public void removeDivider(int i) {
        dividersModel.removeElementAt(i);
    }

    private boolean hasSelectedDivider() {
        return dividersList.getSelectedIndex() != -1;
    }

    public void removeAllDividers() {
        dividersModel.removeAllElements();
    }

    private JButton makeActionOnCorrectScaleButton(final String label) {
        JButton actionButton = new JButton(label);
        dividersModel.addListDataListener(new UpdateButtonForCorrectModelStateListDataListener(actionButton));
        actionButton.setEnabled(isScaleCorrect());
        return actionButton;
    }

    private JPanel makeAddDividerPanel() {
        JPanel addPanel = new JPanel();
        JLabel enterValue = new JLabel("Enter Value:");
        addPanel.setLayout(new GridBagLayout());
        if (scaleType == FLOAT) {
            addField = new NumberField(10, NumberField.FLOAT);
        } else {
            addField = new NumberField(10, NumberField.INTEGER);
        }
        addButton = new JButton("Add");
        addButton.setEnabled(false);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDelimiter();
            }
        });

        addField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                processDocumentEvent(e);
            }

            public void removeUpdate(DocumentEvent e) {
                processDocumentEvent(e);
            }

            public void changedUpdate(DocumentEvent e) {
                processDocumentEvent(e);
            }

            private void processDocumentEvent(DocumentEvent e) {
                addButton.setEnabled(addField.isValid());
            }
        });

        addField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addDelimiter();
            }
        });
        
		addPanel.add(enterValue, new GridBagConstraints(
					0,0,1,1,0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE,
					new Insets(0,2,0,2),
					2,2
		));

        addPanel.add(addField, new GridBagConstraints(
        			0,1,1,1,0,0,
        			GridBagConstraints.NORTHWEST,
        			GridBagConstraints.VERTICAL,
        			new Insets(0,2,0,2),
        			2,2
        ));
        addPanel.add(addButton,new GridBagConstraints(
					1,1,1,1,0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.NONE,
					new Insets(0,2,0,2),
					2,2
		));
        return addPanel;
    }

    private void addDelimiter() {
        if (scaleType == FLOAT) {
            addDelimiter(addField.getDoubleValue());
        } else {
            addDelimiter(addField.getIntegerValue());
        }
    }

    private DefaultListModel dividersModel;

    public void addDelimiter(double value) {
        int i;
        for (i = 0; i < dividersModel.size(); i++) {
            final double currDivider = ((Double) dividersModel.elementAt(i)).doubleValue();
            if (value == currDivider) {
                return;
            }
            if (value < currDivider) {
                break;
            }
        }
        dividersModel.insertElementAt(new Double(value), i);
        addField.setText("");
    }

    public void addDelimiter(int value) {
        int i;
        for (i = 0; i < dividersModel.size(); i++) {
            final int currDivider = ((Integer) dividersModel.elementAt(i)).intValue();
            if (value == currDivider) {
                return;
            }
            if (value < currDivider) {
                break;
            }
        }
        dividersModel.insertElementAt(new Integer(value), i);
        addField.setText("");
    }

    public java.util.List getDividers() {
        return Arrays.asList(dividersModel.toArray());
    }

    public String getDiagramTitle() {
        return titleEditor.getText();
    }

    private JPanel makeButtonsPane() {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

        okButton = makeActionOnCorrectScaleButton("Create");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                result = true;
            }
        });
        

        buttonPane.add(okButton);

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                result = false;
            }
        });
        buttonPane.add(cancelButton);
        return buttonPane;
    }

    private boolean isScaleCorrect() {
        return dividersModel.getSize() > 0 && !titleEditor.getText().equals("");
    }

    private class UpdateButtonForCorrectModelStateListDataListener implements ListDataListener {
        private final JButton actionButton;

        public UpdateButtonForCorrectModelStateListDataListener(JButton button) {
            this.actionButton = button;
        }

        private void updateStateOfOkButton() {
            actionButton.setEnabled(isScaleCorrect());
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
