package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.event.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.adapters.DOMAdapter;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;

/**
 * Shows an object using HTML.
 *
 * A definition for this viewer looks like this:
 * <viewer class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseViewer"
 *         name="Show object..."/>
 *     <parameter name="template" value="/some/file/somewhere.html"/>
 * </viewer>
 *
 * The input file will be loaded and displayed as an HTML page. All
 * occurances of entries like <field name="givenName"/> will be filled
 * with the content of the field for the object and changed into <span>s.
 */
public class HTMLDatabaseViewer implements DatabaseViewer
{
    private class HTMLDatabaseViewerDialog extends JDialog
    {
        private DatabaseViewerManager viewerManager;

        private JEditorPane textArea;

        private List fieldElements = new LinkedList();

        private List fieldNames = new LinkedList();

        private Document document;

        public HTMLDatabaseViewerDialog( Frame frame, DatabaseViewerManager viewerManager)
                throws DatabaseViewerInitializationException
        {
            super( frame, "View Item", true );

            this.viewerManager = viewerManager;

            String templateFile = (String) viewerManager.getParameters().get("template");
            
            if( templateFile == null )
            {
                throw new DatabaseViewerInitializationException("No template parameter given.");
            }
            InputStream in;
            try{
                in = new FileInputStream( templateFile );
            }
            catch (Exception e)
            {
                throw new DatabaseViewerInitializationException("Could not open file '" + templateFile + "'", e);
            }
            try
            {
                DOMAdapter domAdapter = new org.jdom.adapters.XercesDOMAdapter();
                org.w3c.dom.Document w3cdoc = domAdapter.getDocument( in, false );

                DOMBuilder builder =
                                new DOMBuilder( "org.jdom.adapters.XercesDOMAdapter" );
                this.document = builder.build( w3cdoc );
            }
            catch (Exception e)
            {
                throw new DatabaseViewerInitializationException("Could not parse template.", e);
            }

            List queue = new LinkedList();
            queue.add(this.document.getRootElement());
            while(!queue.isEmpty())
            {
                Element elem = (Element) queue.remove(0);
                queue.addAll(elem.getChildren());
                if(elem.getName().equals("field"))
                {
                    fieldElements.add(elem);
                    fieldNames.add(elem.getAttributeValue("name"));
                    elem.setName("span");
                    elem.removeAttribute("name");
                }
            }

            final JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ConfigurationManager.storePlacement("HTMLDatabaseViewerDialog", dialog);
                    dialog.setVisible(false);
                }
            });
            getRootPane().setDefaultButton(closeButton);

            //Lay out the buttons from left to right.
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout( new BoxLayout( buttonPane, BoxLayout.X_AXIS) );
            buttonPane.setBorder( BorderFactory.createEmptyBorder(0, 10, 10, 10) );
            buttonPane.add( Box.createHorizontalGlue() );
            buttonPane.add( closeButton );

            this.textArea = new JEditorPane();
            this.textArea.setContentType("text/html");
            this.textArea.setEditable(false);

            JScrollPane scrollview = new JScrollPane();
            scrollview.getViewport().add(this.textArea);

            //Put everything together, using the content pane's BorderLayout.
            Container contentPane = getContentPane();
            contentPane.add( scrollview, BorderLayout.CENTER );
            contentPane.add( buttonPane, BorderLayout.SOUTH );
        }

        private void showObject(String objectKey)
        {
            try
            {
                List results = this.viewerManager.getConnection().executeQuery(fieldNames,
                                                                             viewerManager.getTableName(),
                                                                             "WHERE " + viewerManager.getKeyName() +
                                                                                        "='" + objectKey + "';");
                Vector fields = (Vector)results.get(0);
                Iterator itFields = fields.iterator();
                Iterator itElems = fieldElements.iterator();
                while( itFields.hasNext() )
                {
                    String result = (String) itFields.next();
                    Element fieldElem = (Element) itElems.next();
                    fieldElem.setText(result);
                }
                XMLOutputter outputter = new XMLOutputter();
                outputter.setOmitDeclaration(true);
                this.textArea.setText(outputter.outputString(this.document));
            }
            catch (DatabaseException e)
            {
                this.textArea.setText("Failed to query database:\n" + e.getMessage() + "\n" + e.getOriginal().getMessage());
            }
        }
    }

    private HTMLDatabaseViewerDialog dialog;

    public HTMLDatabaseViewer()
    {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseViewerManager manager)
        throws DatabaseViewerInitializationException
    {
        this.dialog = new HTMLDatabaseViewerDialog( manager.getParentWindow(), manager );
        ConfigurationManager.restorePlacement("HTMLDatabaseViewerDialog", dialog, new Rectangle(100,100,150,150));
    }

    public void showObject(String objectKey)
    {
        if( this.dialog != null )
        {
            this.dialog.showObject(objectKey);
            this.dialog.setVisible(true);
        }
        else
        {
            System.err.println( "HTMLDatabaseViewerDialog has to be initialize(..)d " +
                                "before showDialog(..) is called." );
        }
    }
}
