package net.sourceforge.toscanaj.dbviewer;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Shows an object in a simple dialog.
 *
 * A definition for this viewer looks like this:
 * <viewer class="net.sourceforge.toscanaj.dbviewer.SimpleDatabaseViewer"
 *         name="Show object..."/>
 *     <parameter name="openDelimiter" value="!!"/>
 *     <parameter name="closeDelimiter" value="§§"/>
 *     <template>Name: !!name§§
 * Type: !!type§§
 * Size: !!size§§</template>
 * </viewer> 
 *
 * Here the three fields "name", "type" and "size" will be queried and the
 * template will be filled with the results and then displayed in a dialog.
 * Note that the whitespace will be copied, too -- if you format your XML in
 * a nice way you might get weird indentation in the dialog.
 *
 * @todo add a "template" parameter for giving the template as URL to a text file 
 * @todo JTextArea is not what we want -- at least not editable
 */
public class SimpleDatabaseViewer implements DatabaseViewer
{
    private class SimpleDatabaseViewerDialog extends JDialog
    {
        private DatabaseViewerManager viewerManager;
        
        private JTextArea textArea;
        
        private List textFragments = new LinkedList();
        
        private List fieldNames = new LinkedList();
            
        private void showObject(String objectKey)
        {
            try
            {
                List results = this.viewerManager.getConnection().executeQuery(fieldNames,
                                                                             viewerManager.getDatabaseInfo().getTableName(), 
                                                                             "WHERE " + viewerManager.getDatabaseInfo().getKey() + 
                                                                                        "='" + objectKey + "';");
                Vector fields = (Vector)results.get(0);
                Iterator itText = textFragments.iterator();
                Iterator itFields = fields.iterator();
                String output = "";
                while( itFields.hasNext() )
                { // we assume length(textFragements) = length(results) + 1
                    String text = (String) itText.next();
                    String result = (String) itFields.next();
                    output += text + result;
                }
                output += (String) itText.next();
                this.textArea.setText(output);
            }
            catch (DatabaseException e)
            {
                this.textArea.setText("Failed to query database:\n" + e.getMessage() + "\n" + e.getOriginal().getMessage());
            }
        }
    
        public SimpleDatabaseViewerDialog( Frame frame, DatabaseViewerManager viewerManager)
        {
            super( frame, "View Item", true );
    
            this.viewerManager = viewerManager;
            
            String openDelimiter = (String) viewerManager.getParameters().get("openDelimiter");
            String closeDelimiter = (String) viewerManager.getParameters().get("closeDelimiter");
            String template = viewerManager.getTemplateString();
            while( template.indexOf(openDelimiter) != -1 )
            {
                textFragments.add(template.substring(0, template.indexOf(openDelimiter)));
                template = template.substring(template.indexOf(openDelimiter)+openDelimiter.length());
                fieldNames.add(template.substring(0, template.indexOf(closeDelimiter)));
                template = template.substring(template.indexOf(closeDelimiter)+closeDelimiter.length());
            }
            textFragments.add(template);
    
            final JButton closeButton = new JButton("Close");
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ConfigurationManager.storePlacement("SimpleDatabaseViewerDialog", dialog);
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
            
            this.textArea = new JTextArea();
    
            //Put everything together, using the content pane's BorderLayout.
            Container contentPane = getContentPane();
            contentPane.add( textArea, BorderLayout.CENTER );
            contentPane.add( buttonPane, BorderLayout.SOUTH );
        }
    }
    
    private SimpleDatabaseViewerDialog dialog;
    
    public SimpleDatabaseViewer()
    {
        // initialization has to be done separately, so we can use the dynamic class loading mechanism
    }
    
    public void initialize(DatabaseViewerManager manager)
    {
        this.dialog = new SimpleDatabaseViewerDialog( manager.getParentWindow(), manager );
        ConfigurationManager.restorePlacement("SimpleDatabaseViewerDialog", dialog, new Rectangle(100,100,150,150));
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
            System.err.println( "SimpleDatabaseViewerDialog has to be initialize(..)d " +
                                "before showDialog(..) is called." );
        }
    }
}
