package net.sourceforge.toscanaj.view.database;
/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */

import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.activity.EmitEventActivity;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectEvent;

import javax.swing.*;
import java.awt.*;

public class DatabaseConnectionInformationView extends JPanel implements BrokerEventListener {
    protected DatabaseInfo info;

    private DatabaseConnectEvent databaseConnectEvent;

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
        super();
        setLayout(new BorderLayout());
        this.info = databaseInfo;

        setName("DatabaseConnectionInformationView");

        JLabel urlLabel = new JLabel("Url:");
        JLabel driverLabel = new JLabel("Driver:");
        JLabel userLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        urlField = new JTextField();
        driverField = new JTextField();
        userField = new JTextField();
        passwordField = new JTextField();

        JButton connectButton = new JButton();
        databaseConnectEvent = new DatabaseConnectEvent(this, databaseInfo);
        SimpleAction action = new SimpleAction(frame, "Connect");
        action.add(new SaveControlActivity());
        action.add(new EmitEventActivity(eventBroker, databaseConnectEvent));
        connectButton.setAction(action);

        JPanel buttonPane = new JPanel(new BorderLayout());
        JPanel pane = new JPanel(new GridLayout(0, 2));

        pane.add(urlLabel);
        pane.add(urlField);
        pane.add(driverLabel);
        pane.add(driverField);
        pane.add(userLabel);
        pane.add(userField);
        pane.add(passwordLabel);
        pane.add(passwordField);

        buttonPane.add(connectButton, BorderLayout.EAST);

        add(pane, BorderLayout.CENTER);
        add(buttonPane, BorderLayout.SOUTH);

        eventBroker.subscribe(this, DatabaseInfoChangedEvent.class, Object.class );
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
}
