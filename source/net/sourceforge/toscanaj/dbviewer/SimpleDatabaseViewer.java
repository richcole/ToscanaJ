/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Shows an object in a simple dialog.
 *
 * A definition for this viewer looks like this:
 * <objectView class="net.sourceforge.toscanaj.dbviewer.SimpleDatabaseViewer"
 *         name="Show object..."/>
 *     <parameter name="openDelimiter" value="!!"/>
 *     <parameter name="closeDelimiter" value="§§"/>
 *     <template>Name: !!name§§
 * Type: !!type§§
 * Size: !!size§§</template>
 * </objectView>
 *
 * Here the three fields "name", "type" and "size" will be queried and the
 * template will be filled with the results and then displayed in a dialog.
 * Note that the whitespace will be copied, too -- if you format your XML in
 * a nice way you might get weird indentation in the dialog.
 *
 * Only one item will be displayed, any but the first item will be ignored.
 *
 * @todo Add something to handle multiple results.
 */
public class SimpleDatabaseViewer implements DatabaseViewer {
    private class SimpleDatabaseViewerDialog extends JDialog {
        private DatabaseViewerManager viewerManager;

        private JTextArea textArea;

        private List textFragments = new LinkedList();

        private List fieldNames = new LinkedList();

        private void showView(String whereClause) {
            try {
                List results = this.viewerManager.getConnection().executeQuery(fieldNames,
                        viewerManager.getTableName(),
                        whereClause);
                Vector fields = (Vector) results.get(0);
                Iterator itText = textFragments.iterator();
                Iterator itFields = fields.iterator();
                String output = "";
                while (itFields.hasNext()) { // we assume length(textFragements) = length(results) + 1
                    String text = (String) itText.next();
                    String result = (String) itFields.next();
                    output += text + result;
                }
                output += (String) itText.next();
                this.textArea.setText(output);
            } catch (DatabaseException e) {
                this.textArea.setText("Failed to query database:\n" + e.getMessage() + "\n" + e.getOriginal().getMessage());
            }
        }

        public SimpleDatabaseViewerDialog(Frame frame, DatabaseViewerManager viewerManager)
                throws DatabaseViewerInitializationException {
            super(frame, "View Item", true);

            this.viewerManager = viewerManager;

            String openDelimiter = (String) viewerManager.getParameters().get("openDelimiter");
            if (openDelimiter == null) {
                throw new DatabaseViewerInitializationException("Open delimiter not defined");
            }
            String closeDelimiter = (String) viewerManager.getParameters().get("closeDelimiter");
            if (closeDelimiter == null) {
                throw new DatabaseViewerInitializationException("Close delimiter not defined");
            }
            String template = viewerManager.getTemplateString();
            if (template == null) {
                throw new DatabaseViewerInitializationException("No template found");
            }
            while (template.indexOf(openDelimiter) != -1) {
                textFragments.add(template.substring(0, template.indexOf(openDelimiter)));
                template = template.substring(template.indexOf(openDelimiter) + openDelimiter.length());
                fieldNames.add(template.substring(0, template.indexOf(closeDelimiter)));
                template = template.substring(template.indexOf(closeDelimiter) + closeDelimiter.length());
            }
            textFragments.add(template);

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
            getRootPane().setDefaultButton(closeButton);

            //Lay out the buttons from left to right.
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
            buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            buttonPane.add(Box.createHorizontalGlue());
            buttonPane.add(closeButton);

            this.textArea = new JTextArea();
            this.textArea.setEditable(false);

            //Put everything together, using the content pane's BorderLayout.
            Container contentPane = getContentPane();
            contentPane.add(textArea, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);
        }

        protected void closeDialog() {
            ConfigurationManager.storePlacement("SimpleDatabaseViewerDialog", dialog);
            this.dispose();
        }
    }

    private SimpleDatabaseViewerDialog dialog;

    public SimpleDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager)
            throws DatabaseViewerInitializationException {
        this.dialog = new SimpleDatabaseViewerDialog(manager.getParentWindow(), manager);
        ConfigurationManager.restorePlacement("SimpleDatabaseViewerDialog", dialog, new Rectangle(100, 100, 350, 300));
    }

    public void showView(String whereClause) {
        if (this.dialog != null) {
            this.dialog.showView(whereClause);
            this.dialog.setVisible(true);
        } else {
            System.err.println("SimpleDatabaseViewerDialog has to be initialize(..)d " +
                    "before showView(..) is called.");
        }
    }
}
