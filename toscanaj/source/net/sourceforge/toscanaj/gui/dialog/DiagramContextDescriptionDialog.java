/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id: DiagramContextDescriptionDialog.java,v 1.6 2003/1/24 02:46:20
 * acme15 Exp $
 */
package net.sourceforge.toscanaj.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

public class DiagramContextDescriptionDialog extends JDialog implements
        ClipboardOwner, EventBrokerListener {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(DiagramContextDescriptionDialog.class);

    private final JTextArea contentTextArea;

    /**
     * Construct the layout
     */
    public DiagramContextDescriptionDialog(final JFrame parent,
            final EventBroker broker) {
        super(parent, "Analysis History");
        final DiagramContextDescriptionDialog dialog = this;

        broker
                .subscribe(this, DisplayedDiagramChangedEvent.class,
                        Object.class);

        // set the content area
        this.contentTextArea = new JTextArea();
        this.contentTextArea.setEditable(false);
        final JScrollPane scrollview = new JScrollPane();
        scrollview.getViewport().add(this.contentTextArea);

        // set the buttons area
        final JPanel buttonsPanel = new JPanel(new FlowLayout());
        JButton closeButton, copyToClipboardButton;
        closeButton = new JButton(" Close ");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                close();
            }
        });
        copyToClipboardButton = new JButton(" Copy to Clipboard ");
        copyToClipboardButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final StringSelection comments = new StringSelection(
                        DiagramContextDescriptionDialog.this.contentTextArea
                                .getText());
                final Clipboard systemClipboard = getToolkit()
                        .getSystemClipboard();
                systemClipboard.setContents(comments, dialog);
                close();
            }
        });
        // add buttons to the button Panel
        buttonsPanel.add(copyToClipboardButton);
        buttonsPanel.add(closeButton);

        // Put everything together, using the content pane's BorderLayout.
        final Container contentPane = getContentPane();
        contentPane.add(scrollview, BorderLayout.CENTER);
        contentPane.add(buttonsPanel, BorderLayout.SOUTH);
        setVisible(false);
        setBounds(150, 150, 350, 350);
    }

    public void lostOwnership(final Clipboard clipboard,
            final Transferable trans) {
        // this method is for implementing the abstract ClipboardOwner class
        // we don't need to do anything here
    }

    public void showDescription() {
        preferences.restoreWindowPlacement(this, new Rectangle(150, 150, 350,
                350));
        this.contentTextArea.setText(DiagramController.getController()
                .getDiagramHistory().getTextualDescription());
        this.setVisible(true);
    }

    public void close() {
        final DiagramContextDescriptionDialog dialog = this;
        preferences.storeWindowPlacement(dialog);
        this.setVisible(false);
    }

    public void processEvent(final Event e) {
        final DiagramHistory diagHistory = DiagramController.getController()
                .getDiagramHistory();
        if (diagHistory.getNumberOfCurrentDiagrams() > 0) {
            this.contentTextArea.setText(diagHistory.getTextualDescription());
        } else {
            close();
        }
    }
}
