/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;

import javax.swing.*;

import org.tockit.swing.ExtendedPreferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * This abstract class gives a framework for implementing database viewers with
 * paging buttons.
 * 
 * If a specific database viewer can handle only one item at a time, this class
 * can be used to add a set of buttons to navigate through the items given.
 * These buttons implement going to the first, previous, next and last item. In
 * addition a label shows the current position and the total number of items.
 * 
 * To use this class derive from it and implement the two abstract methods. The
 * getPanel() method has to return the Swing component used in your viewer,
 * while the showItem(String) should cause your viewer to show the item for the
 * specific object key given. See the documentation of DatabaseViewer about
 * getting the table and object key information.
 */
abstract public class PagingDatabaseViewer implements DatabaseViewer {
    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(PagingDatabaseViewer.class);
    
	protected interface PageViewPanel {
		void showItem(String keyValue);
		Component getComponent() throws DatabaseViewerInitializationException;
	}
	
    private DatabaseViewerManager viewerManager;

    private class PagingDatabaseViewerDialog extends JDialog {
        private List fieldNames;
        private String[] keyValues;
        private int position;
        private JButton navStartButton;
        private JButton navPrevButton;
        private JButton navNextButton;
        private JButton navEndButton;
        private JLabel infoLabel;
        private PageViewPanel viewPanel;

        protected void showView(String whereClause) {
            try {
                fieldNames = new LinkedList();
                fieldNames.add(viewerManager.getKeyName());
                List results = viewerManager.getConnection().executeQuery(fieldNames,
                        viewerManager.getTableName(),
                        whereClause);
                keyValues = new String[results.size()];
                int i = 0;
                for (Iterator iterator = results.iterator(); iterator.hasNext();) {
                    Vector vector = (Vector) iterator.next();
                    keyValues[i] = (String) vector.get(0);
                    i++;
                }
                position = 0;
                enableButtons();
                showCurrentItem();
                show();
            } catch (DatabaseException e) {
                JOptionPane.showMessageDialog(this,
                        "Failed to query database:\n" + e.getMessage() + "\n" + e.getCause().getMessage(),
                        "Database connection failed",
                        JOptionPane.ERROR_MESSAGE);

            }
        }

        public PagingDatabaseViewerDialog(Frame frame)
                throws DatabaseViewerInitializationException {
            super(frame, "View Items", true);
			this.setModal(false);
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    closeDialog();
                }
            });

            final JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    closeDialog();
                }
            });
            
            this.viewPanel = createPanel();
            
            getRootPane().setDefaultButton(closeButton);
            navStartButton = new JButton("<<");
            navStartButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    start();
                }
            });
            navPrevButton = new JButton("<");
            navPrevButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    prev();
                }
            });
            navNextButton = new JButton(">");
            navNextButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    next();
                }
            });
            navEndButton = new JButton(">>");
            navEndButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    end();
                }
            });

            infoLabel = new JLabel("");

            //Lay out the buttons from left to right.
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEtchedBorder());//(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            buttonPane.add(navStartButton);
            buttonPane.add(navPrevButton);
            buttonPane.add(navNextButton);
            buttonPane.add(navEndButton);
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(infoLabel);
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(closeButton);

            //Put everything together, using the content pane's BorderLayout.
            Container contentPane = getContentPane();
            contentPane.add(this.viewPanel.getComponent(), BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);
        }


        protected void closeDialog() {
            preferences.storeWindowPlacement(this);
            this.dispose();
        }

        private void enableButtons() {
            int last = this.keyValues.length - 1;
            this.navStartButton.setEnabled(position != 0);
            this.navEndButton.setEnabled(position != last);
            this.navPrevButton.setEnabled(position != 0);
            this.navNextButton.setEnabled(position != last);
        }

        private void next() {
            position++;
            showCurrentItem();
            enableButtons();
        }

        private void prev() {
            position--;
            showCurrentItem();
            enableButtons();
        }

        private void showCurrentItem() {
            this.viewPanel.showItem(keyValues[position]);
            infoLabel.setText((position + 1) + "/" + keyValues.length);
        }

        private void start() {
            position = 0;
            showCurrentItem();
            enableButtons();
        }

        private void end() {
            position = keyValues.length - 1;
            showCurrentItem();
            enableButtons();
        }
    }

    protected abstract PageViewPanel createPanel() throws DatabaseViewerInitializationException;

    protected DatabaseViewerManager getManager() {
        return this.viewerManager;
    }

    public PagingDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager)
            throws DatabaseViewerInitializationException {
        this.viewerManager = manager;
    }

    public void showView(String whereClause) {
		Frame parentWindow = DatabaseViewerManager.getParentWindow();
		PagingDatabaseViewerDialog dialog;
		try {
			dialog = new PagingDatabaseViewerDialog(parentWindow);
            preferences.restoreWindowPlacement(dialog, new Rectangle(100, 100, 350, 300));
			dialog.showView(whereClause);
		} catch (DatabaseViewerInitializationException e) {
			ErrorDialog.showError(parentWindow,e,"Viewer could not be initialized");
		}
    }
}
