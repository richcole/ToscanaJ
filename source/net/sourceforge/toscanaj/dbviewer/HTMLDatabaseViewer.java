/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;

import javax.swing.*;
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
 * Shows a database view using HTML.
 *
 * A definition for this viewer looks like this:
 * <objectListView class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseReportGenerator"
 *         name="Show objects..."/>
 *     <template url="somedir/somefile.html"/>
 * </objectListView>
 *
 * The input file will be loaded and displayed as an HTML page.
 *
 * There can be a section in the HTML marked with <repeat>...</repeat>
 * which is the part that shall repeated for each item displayed. All
 * occurances of entries like <field name="givenName"/> within that will be
 * filled
 * with the content of the field for the object and changed into <span>s.
 *
 * Fields without the <repeat> section can be used for aggregates, e.g. you can
 * do sums or averages with them. If the whole template has no repeat, the viewer
 * can be used as viewer for single items using <objectView>.
 *
 * The <template> element can contain <html> directly instead of using the "url"
 * attribute.
 */
public class HTMLDatabaseViewer implements DatabaseViewer {
    private class HTMLDatabaseViewDialog extends JDialog {
        private DatabaseViewerManager viewerManager;

        private JEditorPane textArea;

        private Element repeatElement = null;

        private Element repetitionBlock = null;

        private List singleFieldElements = new LinkedList();

        private List singleFieldNames = new LinkedList();

        private List repeatedFieldElements = new LinkedList();

        private List repeatedFieldNames = new LinkedList();

        private Element template = null;

        public HTMLDatabaseViewDialog(Frame frame, DatabaseViewerManager viewerManager)
                throws DatabaseViewerInitializationException {
            super(frame, "View Item", true);

            this.viewerManager = viewerManager;

            this.template = viewerManager.getTemplate();

            // find <repeat> and non-repeat <field>s first
            List queue = new LinkedList();
            queue.add(this.template.getChild("html"));
            while (!queue.isEmpty()) {
                Element elem = (Element) queue.remove(0);
                queue.addAll(elem.getChildren());
                if (elem.getName().equals("repeat")) {
                    if (repeatElement != null) {
                        throw new DatabaseViewerInitializationException("Two repeat sections found in template.");
                    }
                    repeatElement = elem;
                    repetitionBlock = (Element) elem.clone();
                    /// @todo find some way to neutralize <repeat>
                }
                if (elem.getName().equals("field")) {
                    singleFieldElements.add(elem);
                    singleFieldNames.add(elem.getAttributeValue("content"));
                    elem.setName("span");
                    elem.removeAttribute("content");
                }
            }

            // find repeated fields
            if (repeatElement != null) {
                queue = new LinkedList();
                queue.add(repetitionBlock);
                while (!queue.isEmpty()) {
                    Element elem = (Element) queue.remove(0);
                    queue.addAll(elem.getChildren());
                    if (elem.getName().equals("field")) {
                        repeatedFieldElements.add(elem);
                        repeatedFieldNames.add(elem.getAttributeValue("content"));
                        elem.setName("span");
                        elem.removeAttribute("content");
                    }
                }
            }

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

            this.textArea = new JEditorPane();
            this.textArea.setContentType("text/html");
            this.textArea.setEditable(false);

            JScrollPane scrollview = new JScrollPane();
            scrollview.getViewport().add(this.textArea);

            //Put everything together, using the content pane's BorderLayout.
            Container contentPane = getContentPane();
            contentPane.add(scrollview, BorderLayout.CENTER);
            contentPane.add(buttonPane, BorderLayout.SOUTH);
        }

        protected void closeDialog() {
            ConfigurationManager.storePlacement("HTMLDatabaseViewDialog", this);
            this.dispose();
        }

        private void showView(String whereClause) {
            try {
                List results = this.viewerManager.getConnection().executeQuery(singleFieldNames,
                        viewerManager.getTableName(),
                        whereClause);
                Vector fields = (Vector) results.get(0);
                Iterator itFields = fields.iterator();
                Iterator itElems = singleFieldElements.iterator();
                while (itFields.hasNext()) {
                    String result = (String) itFields.next();
                    Element fieldElem = (Element) itElems.next();
                    if (result == null) {
                        // an empty <span> is displayed as greater than symbol in JEditPane
                        fieldElem.setText(" ");
                    } else {
                        fieldElem.setText(result);
                    }
                }
                if (repeatElement != null) {
                    results = this.viewerManager.getConnection().executeQuery(repeatedFieldNames,
                            viewerManager.getTableName(),
                            whereClause);
                    repeatElement.setContent(null);
                    Iterator it = results.iterator();
                    while (it.hasNext()) {
                        fields = (Vector) it.next();
                        itFields = fields.iterator();
                        itElems = repeatedFieldElements.iterator();
                        while (itFields.hasNext()) {
                            String result = (String) itFields.next();
                            Element fieldElem = (Element) itElems.next();
                            if (result == null) {
                                // an empty <span> is displayed as greater than symbol in JEditPane
                                fieldElem.setText(" ");
                            } else {
                                fieldElem.setText(result);
                            }
                        }
                        /// @todo only the content of repetitionBlock should be added (but _all_ content, not just elements)
                        repeatElement.addContent((Element) repetitionBlock.clone());
                    }
                }
                XMLOutputter outputter = new XMLOutputter();
                outputter.setOmitDeclaration(true);
                this.textArea.setText(outputter.outputString(this.template.getChild("html")));
                this.show();
            } catch (DatabaseException e) {
                this.textArea.setText("Failed to query database:\n" + e.getMessage() + "\n" + e.getOriginal().getMessage());
            }
        }
    }

    private HTMLDatabaseViewDialog dialog;

    public HTMLDatabaseViewer() {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager)
            throws DatabaseViewerInitializationException {
        this.dialog = new HTMLDatabaseViewDialog(manager.getParentWindow(), manager);
        ConfigurationManager.restorePlacement("HTMLDatabaseViewDialog", dialog, new Rectangle(100, 100, 350, 300));
    }

    public void showView(String whereClause) {
        if (this.dialog != null) {
            this.dialog.showView(whereClause);
        } else {
            System.err.println("HTMLDatabaseViewDialog has to be initialize(..)d " +
                    "before showDialog(..) is called.");
        }
    }
}
