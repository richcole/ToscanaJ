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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @todo the buttons are stupid, there should be a cancel button and an ok button, maybe "apply" as third
 */
public class DatabaseConnectionInformationView extends JDialog implements EventListener {
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
        super(frame, "Database connection");
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.info = databaseInfo;

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

        JButton closeButton = new JButton("Close Dialog");
        closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hide();
            }
        });

        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JPanel pane = new JPanel(new GridLayout(0, 2));

        pane.add(urlLabel);
        pane.add(urlField);
        pane.add(driverLabel);
        pane.add(driverField);
        pane.add(userLabel);
        pane.add(userField);
        pane.add(passwordLabel);
        pane.add(passwordField);

        buttonPane.add(connectButton);
        buttonPane.add(closeButton);

        contentPane.add(pane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        ConfigurationManager.restorePlacement("DatabaseConnectionInformationView", this,
                new Rectangle(100, 100, 300, 200));

        eventBroker.subscribe(this, DatabaseInfoChangedEvent.class, Object.class);
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
