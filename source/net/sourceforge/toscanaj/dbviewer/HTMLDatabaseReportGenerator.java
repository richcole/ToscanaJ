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
 * Shows a report using HTML.
 *
 * A definition for this report generator looks like this:
 * <report class="net.sourceforge.toscanaj.dbviewer.HTMLDatabaseReportGenerator"
 *         name="Show objects..."/>
 *     <parameter name="template" value="/some/file/somewhere.html"/>
 * </report>
 *
 * The input file will be loaded and displayed as an HTML page. 
 *
 * There has to be a section in the HTML marked with <repeat>...</repeat>
 * which is the part that shall repeated for each item displayed. All
 * occurances of entries like <field name="givenName"/> within that will be 
 * filled
 * with the content of the field for the object and changed into <span>s.
 */
public class HTMLDatabaseReportGenerator implements DatabaseReportGenerator
{
    private class HTMLDatabaseReportDialog extends JDialog
    {
        private DatabaseReportGeneratorManager viewerManager;

        private JEditorPane textArea;

        private Element repeatElement = null;
        
        private Element repetitionBlock = null;
        
        private List fieldElements = new LinkedList();

        private List fieldNames = new LinkedList();

        private Document document;

        public HTMLDatabaseReportDialog( Frame frame, DatabaseReportGeneratorManager viewerManager)
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
                if(elem.getName().equals("repeat"))
                {
                    repeatElement = elem;
                    repetitionBlock = (Element)elem.clone();
                    /// @todo find some way to neutralize <repeat>
                }
            }

            queue = new LinkedList();
            queue.add(repetitionBlock);
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
                    ConfigurationManager.storePlacement("HTMLDatabaseReportDialog", dialog);
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

        private void showReport(String whereClause)
        {
            try
            {
                List results = this.viewerManager.getConnection().executeQuery(fieldNames,
                                                                             viewerManager.getDatabaseInfo().getTableName(),
                                                                             whereClause);
                /// @todo remove all content, not just children
                repeatElement.removeChildren();
                Iterator it = results.iterator();
                while(it.hasNext()) 
                {
                    Vector fields = (Vector)it.next();
                    Iterator itFields = fields.iterator();
                    Iterator itElems = fieldElements.iterator();
                    while( itFields.hasNext() )
                    {
                        String result = (String) itFields.next();
                        Element fieldElem = (Element) itElems.next();
                        fieldElem.setText(result);
                    }
                    /// @todo only the content of repetitionBlock should be added (but _all_ content, not just elements)
                    repeatElement.addContent((Element)repetitionBlock.clone());
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

    private HTMLDatabaseReportDialog dialog;

    public HTMLDatabaseReportGenerator()
    {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }

    public void initialize(DatabaseReportGeneratorManager manager)
        throws DatabaseViewerInitializationException
    {
        this.dialog = new HTMLDatabaseReportDialog( manager.getParentWindow(), manager );
        ConfigurationManager.restorePlacement("HTMLDatabaseReportDialog", dialog, new Rectangle(100,100,150,150));
    }

    public void showReport(String whereClause)
    {
        if( this.dialog != null )
        {
            this.dialog.showReport(whereClause);
            this.dialog.setVisible(true);
        }
        else
        {
            System.err.println( "HTMLDatabaseReportDialog has to be initialize(..)d " +
                                "before showDialog(..) is called." );
        }
    }
}
