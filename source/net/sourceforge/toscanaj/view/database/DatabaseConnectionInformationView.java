/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectEvent;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.EmitEventActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventListener;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @todo the buttons are stupid, there should be a cancel button and an ok button, maybe "apply" as third
 */
public class DatabaseConnectionInformationView extends JDialog implements EventListener {
    protected DatabaseInfo info;

    private DatabaseConnectEvent databaseConnectEvent;

    private File openedDatabaseFile = null;

    private JTextField urlField;
    private JTextField userField;
    private JTextField passwordField;
    private JTextField driverField;

    class SaveControlActivity implements SimpleActivity {
        public boolean doActivity() throws Exception {
            copyFromControls(info);
            return true;
        }
    }

    /**
     * Construct an instance of this view
     */
    public DatabaseConnectionInformationView(JFrame frame, DatabaseInfo databaseInfo, EventBroker eventBroker) {
        super(frame, "Database connection");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        if (databaseInfo != null) {
            this.info = databaseInfo;
        } else {
            this.info = new DatabaseInfo();
        }

        JLabel urlLabel = new JLabel("Url:");
        JLabel driverLabel = new JLabel("Driver:");
        JLabel userLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        urlField = new JTextField();
        driverField = new JTextField();
        userField = new JTextField();
        passwordField = new JTextField();

        JButton fileButton = new JButton("File...");
        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getFileURL();
            }
        });

        JButton connectButton = new JButton();
        databaseConnectEvent = new DatabaseConnectEvent(this, this.info);
        SimpleAction action = new SimpleAction(frame, "Connect");
        action.add(new SaveControlActivity());
        action.add(new EmitEventActivity(eventBroker, databaseConnectEvent));
        connectButton.setAction(action);

        JButton closeButton = new JButton("Close Dialog");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hide();
            }
        });

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel pane = new JPanel(new GridBagLayout());

        pane.add(urlLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2,2,2,2),
                                                  0, 0));
        pane.add(urlField, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(2,2,2,2),
                                                  0, 0));
        pane.add(fileButton, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                                                  GridBagConstraints.EAST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2,2,2,2),
                                                  0, 0));
        pane.add(driverLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2,2,2,2),
                                                  0, 0));
        pane.add(driverField, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(2,2,2,2),
                                                  0, 5));
        pane.add(userLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2,2,2,2),
                                                  0, 0));
        pane.add(userField, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(2,2,2,2),
                                                  0, 5));
        pane.add(passwordLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0,
                                                  GridBagConstraints.WEST,
                                                  GridBagConstraints.NONE,
                                                  new Insets(2,2,2,2),
                                                  0, 0));
        pane.add(passwordField, new GridBagConstraints(1, 3, 1, 1, 1, 0,
                                                  GridBagConstraints.CENTER,
                                                  GridBagConstraints.BOTH,
                                                  new Insets(2,2,2,2),
                                                  0, 5));

        buttonPane.add(connectButton);
        buttonPane.add(closeButton);

        contentPane.add(pane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        ConfigurationManager.restorePlacement("DatabaseConnectionInformationView", this,
                new Rectangle(100, 100, 300, 200));

        eventBroker.subscribe(this, DatabaseInfoChangedEvent.class, Object.class);
    }

    private void getFileURL() {
        JFileChooser openDialog;
        if (openedDatabaseFile != null) {
            openDialog = new JFileChooser(openedDatabaseFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                if( f.isDirectory() ) {
                    return true;
                }

                String ext = "";
                String s = f.getName();
                int i = s.lastIndexOf('.');
                if( i > 0 && i < s.length() - 1 ) {
                    ext = s.substring(i+1).toLowerCase();
                }
                return ext.equals("mdb");
            }

            public String getDescription() {
                return "Microsoft Access Databases";
            }
        });

        if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openedDatabaseFile = openDialog.getSelectedFile();
            // the following string is based on this: http://www.artima.com/legacy/answers/Feb2002/messages/141.html
            String connectionURL = "jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb); " +
                                   "DBQ=" + openedDatabaseFile.getAbsolutePath() + "; " +
                                   "UserCommitSync=Yes; " +
                                   "Threads=3; " +
                                   "SafeTransactions=0; " +
                                   "PageTimeout=5; " +
                                   "MaxScanRows=8; " +
                                   "MaxBufferSize=2048; " +
                                   "DriverId=281";
            urlField.setText(connectionURL);
            driverField.setText("sun.jdbc.odbc.JdbcOdbcDriver");
        }
    }

    public boolean areControlsChanged() {
        if (!urlField.getText().equals(info.getURL())) {
            return true;
        }
        if (!userField.getText().equals(info.getUserName())) {
            return true;
        }
        if (!passwordField.getText().equals(info.getPassword())) {
            return true;
        }
        if (!driverField.getText().equals(info.getDriverClass())) {
            return true;
        }
        return false;
    }

    public void copyFromControls(DatabaseInfo info) {
        info.setUrl(urlField.getText());
        info.setUserName(userField.getText());
        info.setPassword(passwordField.getText());
        info.setDriverClass(driverField.getText());
    }

    public void copyToControls(DatabaseInfo info) {
        this.info = info;
        urlField.setText(info.getURL());
        driverField.setText(info.getDriverClass());
        passwordField.setText(info.getPassword());
        userField.setText(info.getUserName());
    }

    public void processEvent(Event event) {
        ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        copyToControls(changeEvent.getConceptualSchema().getDatabaseInfo());
        databaseConnectEvent.setInfo(changeEvent.getConceptualSchema().getDatabaseInfo());
    }

    public void setInfo(DatabaseInfo info) {
        copyToControls(info);
    }

    public void hide() {
        super.hide();
        ConfigurationManager.storePlacement("DatabaseConnectionInformationView", this);
    }
}
