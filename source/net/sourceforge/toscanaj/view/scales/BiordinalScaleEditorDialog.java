/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

import javax.swing.*;
import javax.swing.event.*;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class BiordinalScaleEditorDialog extends JDialog {
	
	boolean result;
	private JTextField titleEditor = new JTextField();
	private JButton createButton; 

	private OrdinalScaleGeneratorPanel leftPanel, rightPanel;
		
	public static final int INTEGER = 0;
	public static final int FLOAT = 1;
	public static final int UNSUPPORTED = -1;
	


	public BiordinalScaleEditorDialog(Frame owner, DatabaseSchema databaseSchema, DatabaseConnection connection) {
		super(owner);
		setSize(800,500);
		setLocation(10,10);
		layoutDialog(databaseSchema, connection);
	}

	public boolean execute() {
		result = false;
		show();
		return result;
	}

	private void layoutDialog(DatabaseSchema databaseSchema, DatabaseConnection connection) {
		setModal(true);
		setTitle("Biordinal scale editor");
		JPanel mainPane = new JPanel(new GridBagLayout());
        
		mainPane.add(makeTitlePane(),new GridBagConstraints(
					0,0,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
				
		mainPane.add(makeSelectionPane(databaseSchema, connection), new GridBagConstraints(
					0,1,1,1,1,1,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,2),
					2,2
		));
		
		mainPane.add(makeButtonsPane(), new GridBagConstraints(
					0,2,1,1,1.0,0,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints .HORIZONTAL,
					new Insets(2,2,2,2),
					2,2
		));
		
		setContentPane(mainPane);

	}

	private JPanel makeTitlePane() {
		this.titleEditor.addKeyListener(new KeyListener(){
			private void validateTextField(){
				if(titleEditor.getText().equals("") || leftPanel.getDividersList().getModel().getSize()==0 || rightPanel.getDividersList().getModel().getSize()==0){
					createButton.setEnabled(false);
				}else{
					createButton.setEnabled(true);
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
		return new LabeledPanel("Title:", this.titleEditor, false);
	}
  
	private JPanel makeSelectionPane(DatabaseSchema databaseSchema, DatabaseConnection connection) {
		JPanel selectionPane = new JPanel();
		selectionPane.setLayout(new GridBagLayout());

		this.leftPanel = new OrdinalScaleGeneratorPanel(databaseSchema, connection);
		this.rightPanel = new OrdinalScaleGeneratorPanel(databaseSchema, connection);
				
		selectionPane.add(leftPanel,new GridBagConstraints(
					0,0,1,1,1,1,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,2),
					2,2
		));
				
		selectionPane.add(rightPanel, new GridBagConstraints(
					1,0,1,1,1,1,
					GridBagConstraints.NORTHWEST,
					GridBagConstraints.BOTH,
					new Insets(2,2,2,2),
					2,2
		));
		
		return selectionPane;
		
	}

	public String getDiagramTitle() {
		return titleEditor.getText();
	}

	private JPanel makeButtonsPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));

		createButton = makeActionOnCorrectScaleButton("Create");
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				result = true;
			}
		});

		buttonPane.add(createButton);

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
	
	private JButton makeActionOnCorrectScaleButton(final String label) {
		JButton actionButton = new JButton(label);
		leftPanel.getDividersModel().addListDataListener(new UpdateButtonForCorrectModelStateListDataListener(actionButton));
		actionButton.setEnabled(isScaleCorrect());
		return actionButton;
	}

	public Context createContext() {
		return new ContextImplementation("Not yet implemented");
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
	
	private boolean isScaleCorrect() {
		DefaultListModel leftPanelListModel = leftPanel.getDividersModel();
		DefaultListModel rightPanelListModel = rightPanel.getDividersModel();
		return leftPanelListModel.getSize() > 0 && rightPanelListModel.getSize() > 0 && !titleEditor.getText().equals("");
	}

}
