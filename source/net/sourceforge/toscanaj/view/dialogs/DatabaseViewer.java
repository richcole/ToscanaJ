package net.sourceforge.toscanaj.view.dialogs;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.model.DatabaseViewerSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;

public class DatabaseViewer extends JDialog
{
    /**
     * This is the real dialog, inititialized using
     * initialize( Component, DatabaseInfo ).
     */
    private static DatabaseViewer dialog;

    private DatabaseViewerSetup viewerSetup;
    
    private JTextArea textArea;
    
    private List textFragments = new LinkedList();
    
    private List fieldNames = new LinkedList();
    
    /**
     * The current database connection for querying table and column names.
     */
    private static DBConnection _connection;

    /**
     * Sets up the dialog.
     */
    public static void initialize( Component comp, DatabaseViewerSetup viewerSetup )
    {
        Frame frame = JOptionPane.getFrameForComponent( comp );
        dialog = new DatabaseViewer( frame, viewerSetup );
        ConfigurationManager.restorePlacement("DatabaseViewer", dialog, new Rectangle(100,100,150,150));
    }

    /**
     * Show the initialized dialog.  The first argument shouldpa
     * be null if you want the dialog to come up in the center
     * of the screen.  Otherwise, the argument should be the
     * component on top of which the dialog should appear.
     */
    public static void showDialog( String objectKey )
    {
        if( dialog != null )
        {
            dialog.setContent(objectKey);
            dialog.setVisible(true);
        }
        else
        {
            System.err.println( "DatabaseChooser has to be initialize(..)d " +
                                "before showDialog(..) is called." );
        }
    }

    private void setContent(String objectKey)
    {
        try
        {
            List results = this.viewerSetup.getConnection().executeQuery(fieldNames,
                                                                         viewerSetup.getDatabaseInfo().getTableName(), 
                                                                         "WHERE " + viewerSetup.getDatabaseInfo().getKey() + 
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

    private DatabaseViewer( Frame frame, DatabaseViewerSetup viewerSetup)
    {
        super( frame, "View Item", true );

        this.viewerSetup = viewerSetup;
        
        String openDelimiter = (String) viewerSetup.getParameters().get("openDelimiter");
        String closeDelimiter = (String) viewerSetup.getParameters().get("closeDelimiter");
        String template = viewerSetup.getTemplateString();
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
                ConfigurationManager.storePlacement("DatabaseViewer", dialog);
                DatabaseViewer.dialog.setVisible(false);
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
