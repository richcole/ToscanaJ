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
 * A basic dialog to input text.
 * 
 * The constructors have following parameters:
 * <dl>
 * <dt>thingToAdd</dt>
 * <dd>The string of the element to be added, either an "object" or "attribute"
 * during renaming or the title during creation.</dd>
 * <dt>currentTextValue</dt>
 * <dd>The value of the current string. To be used in the formatting of the text
 * message prompt in the JDialog</dd>
 * <dt>withCancelButton</dt>
 * <dd>Allows the user to specify whether or not they want dialog to have a
 * "Cancel" button. By default the dialog is built with a cancel button.</dd>
 * </dl>
 */
public class InputTextDialog extends JDialog {

    private static final boolean isModal = true;

    private JOptionPane optionPane;
    private JTextField textField;

    private boolean withCancelButton = true;

    public InputTextDialog(final Dialog aDialog, final String title,
            final String thingToAdd, final String currentTextValue) {
        super(aDialog, isModal);
        buildDialog(aDialog, title, thingToAdd, currentTextValue);
    }

    public InputTextDialog(final Frame aFrame, final String title,
            final String thingToAdd, final String currentTextValue) {
        super(aFrame, isModal);
        buildDialog(aFrame, title, thingToAdd, currentTextValue);
    }

    public InputTextDialog(final Frame aFrame, final String title,
            final String thingToAdd, final String currentTextValue,
            final boolean withCancelButton) {
        super(aFrame, isModal);
        this.withCancelButton = withCancelButton;
        buildDialog(aFrame, title, thingToAdd, currentTextValue);
    }

    private void buildDialog(final Component parent, final String title,
            final String thingToAdd, final String currentTextValue) {
        setTitle(title);

        final String message = "Please input the name of the " + thingToAdd
                + ": ";

        this.textField = new JTextField(10);
        this.textField.setText(currentTextValue);

        final Object[] array = { message, this.textField };

        final String enterButtonString = "Enter";
        final String cancelButtonString = "Cancel";

        if (this.withCancelButton) {
            final Object[] options = { enterButtonString, cancelButtonString };
            this.optionPane = new JOptionPane(array,
                    JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
                    null, options, options[0]);
        } else {
            final Object[] options = { enterButtonString };
            this.optionPane = new JOptionPane(array,
                    JOptionPane.QUESTION_MESSAGE, JOptionPane.YES_NO_OPTION,
                    null, options);
        }

        setContentPane(this.optionPane);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent we) {
                if (InputTextDialog.this.withCancelButton) {
                    /*
                     * Instead of directly closing the window, we're going to
                     * change the JOptionPane's value property.
                     */
                    InputTextDialog.this.optionPane.setValue(JOptionPane.CLOSED_OPTION);
                }
            }
        });

        this.textField.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                InputTextDialog.this.optionPane.setValue(enterButtonString);
            }
        });

        this.optionPane.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent e) {
                final String prop = e.getPropertyName();

                if (isVisible()
                        && (e.getSource() == InputTextDialog.this.optionPane)
                        && (prop.equals(JOptionPane.VALUE_PROPERTY) || prop
                                .equals(JOptionPane.INPUT_VALUE_PROPERTY))) {
                    final Object value = InputTextDialog.this.optionPane
                            .getValue();

                    if (value == JOptionPane.UNINITIALIZED_VALUE) {
                        return;
                    }

                    if (value.equals(enterButtonString)) {
                        final String typedText = InputTextDialog.this.textField
                                .getText();
                        if ((typedText == null) || (typedText.trim().equals(""))) {
                            setVisible(true);
                            InputTextDialog.this.optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                        } else {
                            InputTextDialog.this.optionPane.setInputValue(typedText);
                            setVisible(false);
                        }
                    } else {
                        setVisible(false);
                        InputTextDialog.this.optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);
                    }
                }
            }
        });
        setLocationRelativeTo(parent);
        pack();
        setVisible(true);
    }

    public String getInput() {
        return (String) this.optionPane.getInputValue();
    }

    public boolean isCancelled() {
        if (this.optionPane.getValue() == JOptionPane.UNINITIALIZED_VALUE) {
            return true;
        }
        return false;
    }
}
