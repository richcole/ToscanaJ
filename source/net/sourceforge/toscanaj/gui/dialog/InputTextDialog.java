/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

/**
 *  Input dialog, constructors have following parameters:
 *  - thingToAdd The string of the element to be added, either an
 * "object" or "attribute" during renaming or the title during creation.
 *  - currentTextValue The value of the current string.
 * To be used in the formatting of the text message prompt in the JDialog
 *  - withCancelButton allows user to specify whether or not they want 
 * dialog to have a "Cancel" button. By default dialog is built with Cancel button.
 */
public class InputTextDialog extends JDialog {
	
	private static final boolean isModal = true;

    private JOptionPane optionPane;
	private JTextField textField;
	
	private boolean withCancelButton = true;
	
	public InputTextDialog(Dialog aDialog, String title, String thingToAdd, String currentTextValue) {
		super(aDialog, isModal);
		buildDialog(aDialog, title, thingToAdd, currentTextValue);
	}

    public InputTextDialog(Frame aFrame, String title, String thingToAdd, String currentTextValue) {
        super(aFrame, isModal);
		buildDialog(aFrame, title, thingToAdd, currentTextValue);
    }

	public InputTextDialog(Frame aFrame, String title, String thingToAdd, String currentTextValue, boolean withCancelButton) {
		super(aFrame, isModal);
		this.withCancelButton = withCancelButton;
		buildDialog(aFrame, title, thingToAdd, currentTextValue);
	}

	private void buildDialog(Component parent, String title, String thingToAdd, String currentTextValue) {
		setTitle(title);
		
		final String message = "Please input the name of the " + thingToAdd + ": ";
		
		textField = new JTextField(10);
		textField.setText(currentTextValue);
		
		Object[] array = {message, textField};
		
		final String enterButtonString = "Enter";
		final String cancelButtonString = "Cancel";
		
		if (this.withCancelButton) {
			Object[] options = {enterButtonString, cancelButtonString};
			optionPane = new JOptionPane(array, 
										JOptionPane.QUESTION_MESSAGE,
										JOptionPane.YES_NO_OPTION,
										null,
										options,
										options[0]);
		}
		else {
			Object[] options = {enterButtonString};
			optionPane = new JOptionPane(array, 
										JOptionPane.QUESTION_MESSAGE,
										JOptionPane.YES_NO_OPTION,
										null,
										options);
		}
		
		setContentPane(optionPane);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
		        public void windowClosing(WindowEvent we) {
		        	if (withCancelButton) {
						/*
						 * Instead of directly closing the window,
						 * we're going to change the JOptionPane's
						 * value property.
						 */
						optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
		        	}
		    }
		});
		
		textField.addActionListener(new ActionListener() {
		    public void actionPerformed(ActionEvent e) {
		        optionPane.setValue(enterButtonString);
		    }
		});
		
		optionPane.addPropertyChangeListener(new PropertyChangeListener() {
		    public void propertyChange(PropertyChangeEvent e) {
		        String prop = e.getPropertyName();
		
		        if (isVisible() 
				             && (e.getSource() == optionPane)
				             && (prop.equals(JOptionPane.VALUE_PROPERTY) ||
				                 prop.equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
		            Object value = optionPane.getValue();
		
		            if (value == JOptionPane.UNINITIALIZED_VALUE) {
		                return;
		            }
		
		            if (value.equals(enterButtonString)) {
		                String typedText = textField.getText();
		                if ((typedText.equals(null)) || (typedText.trim().equals(""))) {
		                	setVisible(true);
							optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
		                }
		                else {
		                	optionPane.setInputValue(typedText);
		                	setVisible(false);
		                }
		            }
		            else {
		            	setVisible(false);
						optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
		            }
		        }
		    }
		});
		setLocationRelativeTo(parent);
		pack();
		setVisible(true);
	}
    
    public String getInput () {
    	return (String) optionPane.getInputValue();
    }
    
    public boolean isCancelled () {
    	if (optionPane.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
    		return true;
    	}
    	return false;
    }
}
