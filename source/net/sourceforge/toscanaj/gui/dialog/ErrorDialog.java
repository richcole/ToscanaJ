/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

/**
 * This is a generic class to handle error messages.
 *
 * ToscanaJ Exceptions will give the user the option to see a simple error
 * message or thay can view more detailed error message.
 * 
 * @todo break messages that are too long into multiple lines
 */

public class ErrorDialog extends JDialog implements ClipboardOwner{
	private JButton closeButton, copyToClipboardButton;
	private static final String CONFIGURATION_SECTION_NAME = "ErrorDialog";
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 500;
	private static final int DEFAULT_X_POS = 50;
	private static final int DEFAULT_Y_POS = 50;

    /**
     * Constructor to show a simple error message
     */
    private ErrorDialog(Component component, String title, String msg) {
		super(JOptionPane.getFrameForComponent(component),title,true);
		JPanel mainPanel = new JPanel(new BorderLayout());
		ConfigurationManager.restorePlacement(CONFIGURATION_SECTION_NAME, 
		this, new Rectangle(DEFAULT_X_POS, DEFAULT_Y_POS, MINIMUM_WIDTH, MINIMUM_HEIGHT));
		mainPanel.add(createErrorLogScrollPane(msg),BorderLayout.CENTER);
		mainPanel.add(createButtonsPanel(msg), BorderLayout.SOUTH);
		setContentPane(mainPanel);
		setVisible(true);
    }
    
	private JScrollPane createErrorLogScrollPane(String msg){
		JTextArea textArea = new JTextArea(msg);
		textArea.setEditable(false);
		JScrollPane scrollPane = new JScrollPane(textArea);
		return scrollPane;
	}
	
	private JPanel createButtonsPanel(String msg) {
		JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final String errorMsg = msg;
		closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeDialog();			
			}		
		});
		copyToClipboardButton = new JButton("Copy to Clipboard");
		copyToClipboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection comments = new StringSelection(errorMsg);
				Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				systemClipboard.setContents(comments,null);
				closeDialog();
			}		
		});
		buttonsPanel.add(closeButton);
		buttonsPanel.add(copyToClipboardButton);
		return buttonsPanel;
	}
	
	private void closeDialog(){
		ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME,this);
		dispose();
	}
	
    /**
     * Constructor to show an error message based on exception thrown.
     *
     * The user has the choice to view more detail of the error
     * based on the original exception thrown.
     */
    private ErrorDialog(Component component, Throwable e, String title) {
        showDetailedErrorMessage(component, e, title);
    }

    /**
     * Constructor to show a simple error message.
     *
     * The user has the choice to view more detail of the error
     * based on the original exception thrown.
     */
    private ErrorDialog(Component component, Throwable e, String title, String errorMsg) {
        showDetailedErrorMsg(component, e, title, errorMsg);
    }
    
    private void showDetailedErrorMessage(Component component, Throwable e, String title) {
        //original one
		Throwable original = e.getCause();
		if (original == null) {
			showLastErrorMessage(component, e, title);
			return;
		}        
        Object[] options = {"OK", "Details"};
        int n = JOptionPane.showOptionDialog(component,
                e.getMessage(),
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
        if (n == 1) {
            ErrorDialog.showError(component, original, title, original.getMessage());
        }
    }

    private void showLastErrorMessage(Component component, Throwable e, String title) {
        Object[] options = {"OK", "Details"};
        int n = JOptionPane.showOptionDialog(component,
                e.getMessage(),
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
        if (n == 1) {
        	StringWriter stackTraceWriter = new StringWriter();
        	e.printStackTrace(new PrintWriter(stackTraceWriter));
            ErrorDialog.showError(component, title, stackTraceWriter.toString());
        }
    }

    private void showDetailedErrorMsg(Component component, Throwable e, String title, String extraMessage) {
        Object[] options = {"OK", "Details"};
        int n = JOptionPane.showOptionDialog(component,
                extraMessage,
                title,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
        if (n == 1) {
            ErrorDialog.showError(component, e, title);
        }
    }

    /**
     * Show a simple error message.
     */
    public static void showError(Component component, String title, String errorMsg) {
        new ErrorDialog(component, title, errorMsg);
    }

    /**
     * Show an error message using exception thrown
     *
     * User can see a more detail error message
     * based on original exception thrown
     */
    public static void showError(Component component, Throwable e, String title) {
        new ErrorDialog(component, e, title);
    }

    /**
     * Show a simple error message
     *
     * User can see a more detail error message
     * based on original exception thrown
     */
    public static void showError(Component component, Throwable e, String title, String errorMsg) {
        new ErrorDialog(component, e, title, errorMsg);
    }
	/**
	 * Implement for Clipboard feature. Don't have to do anything
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}
}
