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

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.lattice.Attribute;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;

public class CrossordinalScaleEditorDialog extends JDialog {
	
	boolean result;
	private JTextField titleEditor = new JTextField();
	private JButton createButton; 

	private OrdinalScaleGeneratorPanel leftPanel, rightPanel;
		
	public static final int INTEGER = 0;
	public static final int FLOAT = 1;
	public static final int UNSUPPORTED = -1;
	private static final String CONFIGURATION_SECTION_NAME = "BiordinalScaleEditorDialog";
	private static final int MINIMUM_WIDTH = 800;
	private static final int MINIMUM_HEIGHT = 500;
	private static final int DEFAULT_X_POS = 10;
	private static final int DEFAULT_Y_POS = 10;

	public CrossordinalScaleEditorDialog(Frame owner, DatabaseSchema databaseSchema, DatabaseConnection connection) {
		super(owner);
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
		
		layoutDialog(databaseSchema, connection);
	}

	public boolean execute() {
		result = false;
		show();
		return result;
	}

	private void layoutDialog(DatabaseSchema databaseSchema, DatabaseConnection connection) {
		setModal(true);
		setTitle("Cross-ordinal scale editor");
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
			public void keyTyped(KeyEvent e) {
				setCreateButtonState();
			}
			public void keyReleased(KeyEvent e) {
				setCreateButtonState();
			}
			public void keyPressed(KeyEvent e) {}		
		});
		return new LabeledPanel("Title:", this.titleEditor, false);
	}
	
	protected void setCreateButtonState() {
		createButton.setEnabled(!titleEditor.getText().equals("") && 
								leftPanel.getDividersList().getModel().getSize()!=0 && 
								rightPanel.getDividersList().getModel().getSize()!=0);
	}
  
	private JPanel makeSelectionPane(DatabaseSchema databaseSchema, DatabaseConnection connection) {
		JPanel selectionPane = new JPanel();
		selectionPane.setLayout(new GridBagLayout());

		this.leftPanel = new OrdinalScaleGeneratorPanel(databaseSchema, connection);
		this.rightPanel = new OrdinalScaleGeneratorPanel(databaseSchema, connection);
		
		ListDataListener listener = new ListDataListener() {
			public void contentsChanged(ListDataEvent e) {
				setCreateButtonState();
			}
			public void intervalAdded(ListDataEvent e) {
				setCreateButtonState();
			}
			public void intervalRemoved(ListDataEvent e) {
				setCreateButtonState();
			}
		};
		
		this.leftPanel.addDividerListListener(listener);
		this.rightPanel.addDividerListListener(listener);
				
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
				closeDialog(true);
			}
		});

		buttonPane.add(createButton);

		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog(false);
			}
		});
		buttonPane.add(cancelButton);
		return buttonPane;
	}
	
	private void closeDialog(boolean result) {
		ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME,this);
		dispose();
		this.result = result;
	}
	
	private JButton makeActionOnCorrectScaleButton(final String label) {
		JButton actionButton = new JButton(label);
		leftPanel.getDividersModel().addListDataListener(new UpdateButtonForCorrectModelStateListDataListener(actionButton));
		actionButton.setEnabled(isScaleCorrect());
		return actionButton;
	}

	public Context createContext() {
		ContextImplementation firstContext = 
						(ContextImplementation) this.leftPanel.createContext("left");
		extendAttributeNames(firstContext.getAttributes(), leftPanel.getColumn().getName());
		Context secondContext = this.rightPanel.createContext("right");
		extendAttributeNames(secondContext.getAttributes(), rightPanel.getColumn().getName());
		return firstContext.createProduct(secondContext, this.titleEditor.getText());
	}
	
	private void extendAttributeNames(Collection attributes, String colName) {
		Iterator it = attributes.iterator();
		while (it.hasNext()) {
			Attribute attribute = (Attribute) it.next();
			attribute.setData(colName + " " + attribute.toString());
		}
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