/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.scales;

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

public class InputTextDialog extends JDialog {

    private JOptionPane optionPane;
	private JTextField textField;


    public InputTextDialog(Frame aFrame, String title, String thingToAdd, String currentTextValue) {
        super(aFrame, true);
        setTitle(title);

        final String message = "Please input the name of the " + thingToAdd + ": ";
        
        textField = new JTextField(10);
        textField.setText(currentTextValue);
        
		Object[] array = {message, textField};

        final String enterButtonString = "Enter";
        final String cancelButtonString = "Cancel";
        Object[] options = {enterButtonString, cancelButtonString};

        optionPane = new JOptionPane(array, 
                                    JOptionPane.QUESTION_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);
        setContentPane(optionPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
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
        setLocationRelativeTo(aFrame);
        pack();
        show();
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
