package net.sourceforge.toscanaj.view.dialogs;

import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.model.DatabaseInfo;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * This dialog asks the user for the information needed to connect to a
 * database.
 *
 * @TODO Add error handling -- this assumes the DBConnection class reports the
 *      errors.
 */
public class DatabaseChooser extends JDialog
{
    /**
     * This is the real dialog, inititialized using
     * initialize( Component, DatabaseInfo ).
     */
    private static DatabaseChooser _dialog;

    /**
     * The current database information object.
     */
    private static DatabaseInfo _databaseInfo;

    /**
     * The current database connection for querying table and column names.
     */
    private static DBConnection _connection;

    /**
     * The field for the data source name (DSN).
     */
    private JTextField _dsnField;

    /**
     * This is the list of available tables.
     */
    private JList _tableList;

    /**
     * This is the list of available keys.
     */
    private JList _keyList;

    /**
     * Sets up the dialog.
     */
    public static void initialize( Component comp, DatabaseInfo dbInfo )
    {
        Frame frame = JOptionPane.getFrameForComponent( comp );
        _dialog = new DatabaseChooser( frame, dbInfo );
    }

    /**
     * Show the initialized dialog.  The first argument should
     * be null if you want the dialog to come up in the center
     * of the screen.  Otherwise, the argument should be the
     * component on top of which the dialog should appear.
     */
    public static DatabaseInfo showDialog( Component comp )
    {
        if( _dialog != null )
        {
            _dialog.setLocationRelativeTo(comp);
            _dialog.setVisible(true);
        }
        else
        {
            System.err.println( "DatabaseChooser has to be initialize(..)d " +
                                "before showDialog(..) is called." );
        }
        return _databaseInfo;
    }

    /**
     * Creates a new database chooser by setting up components and layout.
     */
    private DatabaseChooser( Frame frame, DatabaseInfo dbInfo )
    {
        super( frame, "Choose Database", true );

        // store dbInfo first
        _databaseInfo = dbInfo;

        //buttons
        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DatabaseChooser._databaseInfo = null;
                DatabaseChooser._dialog.setVisible(false);
            }
        });

        final JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DatabaseChooser._databaseInfo = new DatabaseInfo();
                _databaseInfo.setUrl( "jdbc:odbc:" + _dsnField.getText() );
                _databaseInfo.setQuery( (String) _tableList.getSelectedValue(),
                                        (String) _keyList.getSelectedValue() );
                DatabaseChooser._dialog.setVisible(false);
            }
        });
        getRootPane().setDefaultButton(okButton);

        // the text field for the DSN -- return fille the list of tables
        _dsnField = new JTextField();
        _dsnField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillTableList();
            }
        });

        // the button for people not using return
        /// @TODO: redesing the whole UI -- it's crappy
        JButton updateButton = new JButton( "Update" );
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fillTableList();
            }
        });

        // the layout for text field/button
        JPanel dsnLeftPane = new JPanel();
        dsnLeftPane.setLayout( new BoxLayout( dsnLeftPane, BoxLayout.Y_AXIS ) );
        JPanel dsnPane = new JPanel();
        dsnPane.setLayout( new BoxLayout( dsnPane, BoxLayout.X_AXIS ) );
        JLabel dsnLabel = new JLabel( "Data Source Name (DSN):" );
        dsnLabel.setLabelFor( _dsnField );
        dsnLeftPane.add( dsnLabel );
        dsnLeftPane.add( _dsnField );
        dsnPane.add( dsnLeftPane );
        dsnPane.add( Box.createRigidArea( new Dimension(10,0) ) );
        dsnPane.add( updateButton );
        dsnPane.setBorder( BorderFactory.createEmptyBorder(10,10,10,10) );

        // the list for the table names
        _tableList = new JList();
        _tableList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        _tableList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    fillKeyList();
                }
            }
        });
        JScrollPane tableListScroller = new JScrollPane( _tableList );
        tableListScroller.setAlignmentX(LEFT_ALIGNMENT);

        // the layout
        JPanel tableListPane = new JPanel();
        tableListPane.setLayout( new BoxLayout( tableListPane, BoxLayout.Y_AXIS ) );
        JLabel tableListLabel = new JLabel( "Available Tables" );
        tableListLabel.setLabelFor( _tableList );
        tableListPane.add( tableListLabel );
        tableListPane.add( Box.createRigidArea( new Dimension(0,5) ) );
        tableListPane.add( tableListScroller );
        tableListPane.setBorder( BorderFactory.createEmptyBorder(10,10,10,10) );

        // the list for the available keys
        _keyList = new JList();
        _keyList.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        _keyList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    okButton.doClick();
                }
            }
        });
        JScrollPane keyListScroller = new JScrollPane( _keyList );
        keyListScroller.setAlignmentX(LEFT_ALIGNMENT);

        // the layout
        JPanel keyListPane = new JPanel();
        keyListPane.setLayout( new BoxLayout( keyListPane, BoxLayout.Y_AXIS ) );
        JLabel keyListLabel = new JLabel( "Available Keys" );
        keyListLabel.setLabelFor( _keyList );
        keyListPane.add( keyListLabel );
        keyListPane.add( Box.createRigidArea( new Dimension(0,5) ) );
        keyListPane.add( keyListScroller );
        keyListPane.setBorder( BorderFactory.createEmptyBorder(10,10,10,10) );

        //Lay out the buttons from left to right.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout( new BoxLayout( buttonPane, BoxLayout.X_AXIS) );
        buttonPane.setBorder( BorderFactory.createEmptyBorder(0, 10, 10, 10) );
        buttonPane.add( Box.createHorizontalGlue() );
        buttonPane.add( cancelButton );
        buttonPane.add( Box.createRigidArea( new Dimension(10, 0) ) );
        buttonPane.add( okButton );

        //Put everything together, using the content pane's BorderLayout.
        /// @Todo change (i.e. fix) this
        Container contentPane = getContentPane();
        contentPane.add( dsnPane, BorderLayout.NORTH );
        contentPane.add( tableListPane, BorderLayout.WEST );
        contentPane.add( keyListPane, BorderLayout.EAST );
        contentPane.add( buttonPane, BorderLayout.SOUTH );

        pack();
    }

    /**
     * Get the list of tables in the current data source and enlist them in
     * the listbox.
     */
    private void fillTableList()
    {
        try {
            _connection = new DBConnection( _dsnField.getText() );
            _tableList.setListData( _connection.getTableNames() );
        }
        catch(DatabaseException e) {
            e.printStackTrace();
            /// @TODO give feedback
        }
    }

    /**
     * Get the list of columns in the current table and enlist them in
     * the listbox.
     */
    private void fillKeyList()
    {
        try {
            _connection = new DBConnection( _dsnField.getText() );
            _keyList.setListData( _connection.getColumnNames(
                                        (String)_tableList.getSelectedValue() ) );
        }
        catch(DatabaseException e) {
            e.printStackTrace();
            /// @TODO give feedback
        }
    }

    /**
     * Some code to test the dialog.
     */
    public static void main(String[] args)
    {
        JFrame f = new JFrame( "Test DatabaseChooser" );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                 System.exit(0);
            }
        });

        JLabel intro = new JLabel("The current DatabaseInfo:");

        final JLabel name = new JLabel("not yet selected");
        intro.setLabelFor(name);
        name.setForeground(Color.black);

        JButton button = new JButton("Test dialog...");
        DatabaseChooser.initialize( f, new DatabaseInfo() );
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DatabaseInfo dbInfo = DatabaseChooser.showDialog( null );
                name.setText( dbInfo.toString() );
            }
        });

        JPanel contentPane = new JPanel();
        f.setContentPane(contentPane);
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        contentPane.add(intro);
        contentPane.add(name);
        contentPane.add(Box.createRigidArea(new Dimension(0,10)));
        contentPane.add(button);
        intro.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        name.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        button.setAlignmentX(JComponent.CENTER_ALIGNMENT);

        f.pack();
        f.setSize( 400, f.getHeight() );
        f.setVisible(true);
    }
}
