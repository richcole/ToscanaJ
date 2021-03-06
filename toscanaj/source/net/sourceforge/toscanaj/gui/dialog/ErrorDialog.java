/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * This is a generic class to handle error messages.
 * 
 * ToscanaJ Exceptions will give the user the option to see a simple error
 * message or thay can view more detailed error message.
 * 
 * @todo break messages that are too long into multiple lines
 */

public class ErrorDialog extends JDialog {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(ErrorDialog.class);
    private static final Rectangle DEFAULT_PLACEMENT = new Rectangle(250, 150,
            400, 300);

    private static final String ERROR_MESSAGE_INDENT = "    ";

    private boolean isDetailedExceptionsShown, onFirstExecute;

    /**
     * Constructor to show a simple error message.
     * 
     * The user has the choice to view more detail of the error based on the
     * original exception thrown.
     */
    private ErrorDialog(final Component component, final Throwable e,
            final String title, final String errorMsg) {
        super(JOptionPane.getFrameForComponent(component), title, true);
        preferences.restoreWindowPlacement(this, DEFAULT_PLACEMENT);

        this.isDetailedExceptionsShown = false;
        this.onFirstExecute = true;
        this.getContentPane().setLayout(new GridBagLayout());

        final Component detailedPanel = createErrorLogPanel(e);
        this.getContentPane().add(
                createErrorMsgPanel(e, errorMsg),
                new GridBagConstraints(0, 0, 1, 1, 1, 0,
                        GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 0, 5), 0, 0));
        this.getContentPane().add(
                createButtonsPanel(detailedPanel),
                new GridBagConstraints(0, 1, 1, 1, 1, 0,
                        GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                        new Insets(0, 5, 0, 5), 0, 0));
        this.getContentPane().add(
                detailedPanel,
                new GridBagConstraints(0, 2, 1, 1, 1, 1,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5), 0, 0));
        pack();
        setVisible(true);
    }

    private JPanel createErrorMsgPanel(final Throwable e,
            final String extraMessage) {
        final String message = extraMessage != null ? extraMessage : e
                .getMessage();

        final JLabel simpleErrorLabel = new JLabel(message, UIManager
                .getIcon("OptionPane.errorIcon"), SwingConstants.LEFT);
        final JPanel simpleErrorMsgPanel = new JPanel(new GridBagLayout());

        simpleErrorMsgPanel.add(simpleErrorLabel, new GridBagConstraints(0, 0,
                1, 1, 1, 0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));

        return simpleErrorMsgPanel;
    }

    private JPanel createButtonsPanel(final Component detailedPanel) {
        final JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        final JButton okButton = new JButton("OK");
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setFocusable(true);
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                closeDialog();
            }
        });

        final JButton detailButton = new JButton("Details >>>");
        final JDialog errorDialog = this;
        detailButton.setFocusable(false);
        detailButton.setMnemonic(KeyEvent.VK_D);
        detailButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final int width = errorDialog.getWidth();
                updateDetailButton(detailButton);
                detailedPanel
                        .setVisible(!ErrorDialog.this.isDetailedExceptionsShown);
                errorDialog.pack();
                errorDialog.setSize(new Dimension(width, errorDialog
                        .getHeight()));
                errorDialog.validate();
            }
        });
        buttonsPanel.add(detailButton);
        buttonsPanel.add(okButton);
        buttonsPanel.setSize(400, 50);

        return buttonsPanel;
    }

    private String createErrorLog(final Throwable e) {
        Throwable currentException = e;
        final String lineBreak = "\n";
        String stackTrace = "==============" + lineBreak + "== Stack Trace =="
                + lineBreak + "==============" + lineBreak;
        String causedBy = "";
        final String exceptionName = e.getClass().getName();
        final String exceptionMsg = e.getMessage();

        while (currentException.getCause() != null) {
            currentException = currentException.getCause();
            causedBy += "Caused By:" + lineBreak + "-----------------"
                    + lineBreak + ERROR_MESSAGE_INDENT
                    + currentException.getMessage() + lineBreak
                    + ERROR_MESSAGE_INDENT + ERROR_MESSAGE_INDENT + "("
                    + currentException.getClass().getName() + ")" + lineBreak
                    + lineBreak;
        }

        // stack trace
        final StringWriter stackTraceWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTraceWriter));
        stackTrace += stackTraceWriter.toString();

        return "Error Message:" + lineBreak + "---------------------"
                + lineBreak + ERROR_MESSAGE_INDENT + exceptionMsg + lineBreak
                + ERROR_MESSAGE_INDENT + ERROR_MESSAGE_INDENT + "("
                + exceptionName + ")" + lineBreak + lineBreak + causedBy
                + lineBreak + stackTrace;
    }

    private Component createErrorLogPanel(final Throwable e) {
        final JTextArea textArea = new JTextArea(createErrorLog(e), 20, 50);
        textArea.setEditable(false);
        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setVisible(this.isDetailedExceptionsShown);
        return scrollPane;
    }

    private void updateDetailButton(final JButton detailButton) {
        final String closeDetailString = "<<< Details";
        final String openDetailString = "Details >>>";
        if (this.isDetailedExceptionsShown) {
            detailButton.setText(closeDetailString);
            this.isDetailedExceptionsShown = false;
        } else if (this.onFirstExecute == true) {
            detailButton.setText(closeDetailString);
            this.isDetailedExceptionsShown = false;
            this.onFirstExecute = false;
        } else {
            detailButton.setText(openDetailString);
            this.isDetailedExceptionsShown = true;
        }
    }

    /**
     * Show an error message using exception thrown
     * 
     * User can see a more detail error message based on original exception
     * thrown
     */
    public static void showError(final Component component, final Throwable e,
            final String title) {
        new ErrorDialog(component, e, title, null);
    }

    /**
     * Show a simple error message
     * 
     * User can see a more detail error message based on original exception
     * thrown
     */
    public static void showError(final Component component, final Throwable e,
            final String title, final String errorMsg) {
        e.printStackTrace(); // for use in IDEs and with similar tools
        new ErrorDialog(component, e, title, errorMsg);
    }

    private void closeDialog() {
        preferences.storeWindowPlacement(this);
        dispose();
    }
}
