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

import org.tockit.context.model.Context;
import org.tockit.swing.preferences.ExtendedPreferences;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class OrdinalScaleEditorDialog extends JDialog {
    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(OrdinalScaleEditorDialog.class);
	
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 600;
	private static final Rectangle DEFAULT_PLACEMENT = new Rectangle(200,100,MINIMUM_WIDTH, MINIMUM_HEIGHT);
    
    private JTextField titleEditor = new JTextField();
    private JButton okButton; 
	private OrdinalScaleGeneratorPanel scalePanel;

    private DatabaseSchema databaseSchema;
    private DatabaseConnection connection;

    private boolean result;
      
    public OrdinalScaleEditorDialog(Frame owner, DatabaseSchema databaseSchema, DatabaseConnection connection) {
        super(owner);
       	this.databaseSchema = databaseSchema;
      	this.connection = connection;
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
        layoutDialog();
        pack();
    }

    public boolean execute() {
        result = false;
        show();
        return result;
    }

    private void layoutDialog() {
        setModal(true);
        setTitle("Ordinal scale editor");
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
        		if(titleEditor.getText().equals("") || scalePanel.getDividersList().getModel().getSize()==0){
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
        return new LabeledPanel("Title:", this.titleEditor, false);
    }

    private JPanel makeSelectionPane(DatabaseSchema databaseSchema, DatabaseConnection connection) {
        this.scalePanel = new OrdinalScaleGeneratorPanel(databaseSchema, connection);
        this.scalePanel.setVisible(true);
        return scalePanel;
    }

    private JButton makeActionOnCorrectScaleButton(final String label) {
        JButton actionButton = new JButton(label);
        scalePanel.getDividersModel().addListDataListener(new UpdateButtonForCorrectModelStateListDataListener(actionButton));
        actionButton.setEnabled(isScaleCorrect());
        return actionButton;
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
				closeDialog(true);
            }
        });

        buttonPane.add(okButton);

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               closeDialog(false);
            }
        });
        buttonPane.add(cancelButton);
        return buttonPane;
    }
	
	private void closeDialog(boolean res) {
        preferences.storeWindowPlacement(this);
		dispose();
		this.result = res;
	}

    private boolean isScaleCorrect() {
        return scalePanel.getDividersModel().getSize() > 0 && !titleEditor.getText().equals("");
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

    public Context createContext() {
        return this.scalePanel.createContext(this.getDiagramTitle());
    }
}
