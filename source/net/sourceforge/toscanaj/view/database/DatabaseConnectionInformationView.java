package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Database;
import net.sourceforge.toscanaj.model.Model;
import net.sourceforge.toscanaj.model.AnacondaModel;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.action.ConnectDatabaseActivity;
import net.sourceforge.toscanaj.gui.action.SimpleActivity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Observer;
import java.util.Observable;

/**
 * A dialog for editing the database info parameters.
 *
 * Use the static method showDialog() to get a modal dialog. If this returns
 * true the user clicked ok (otherwise cancel) and the information can be
 * retrieved with the methods getFormat(), getWidth() and getHeight().  
 *
 */
public class DatabaseConnectionInformationView extends JPanel implements Observer
{
    protected AnacondaModel model;
    protected DatabaseInfo info;

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

    public boolean areControlsChanged() {
        if ( ! urlField.getText().equals(info.url) ) {
            return true;
        }
        if ( ! userField.getText().equals(info.user) ) {
            return true;
        }
        if ( ! passwordField.getText().equals(info.password) ) {
            return true;
        }
        if ( ! driverField.getText().equals(info.driver) ) {
            return true;
        }
        return false;
    }

    public void copyFromControls(DatabaseInfo info)
    {
        info.url      = urlField.getText();
        info.user     = userField.getText();
        info.password = passwordField.getText();
        info.driver   = driverField.getText();
    }

    public void copyToControls(DatabaseInfo info)
    {
        this.info = info;
        urlField.setText(info.url);
        driverField.setText(info.driver);
        passwordField.setText(info.password);
        userField.setText(info.user);
    }

    public void update(Observable o, Object arg)
    {
        if (o instanceof DatabaseInfo) {
            copyToControls((DatabaseInfo)o);
        }
        else if ( o instanceof Database ) {
            Database database = (Database) o;
            copyToControls(database.getInfo());
            database.getInfo().addObserver(this);
        }
    };

    /**
     * Construct an instance of this view
     */
    public DatabaseConnectionInformationView(JFrame frame, DatabaseInfo databaseInfo)
    {
        super();
        setLayout(new BorderLayout());
        this.info = databaseInfo;

        setName("DatabaseConnectionInformationView");
        info.addObserver(this);

        JLabel urlLabel      = new JLabel("Url:");
        JLabel driverLabel   = new JLabel("Driver:");
        JLabel userLabel     = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");

        urlField      = new JTextField();
        driverField   = new JTextField();
        userField     = new JTextField();
        passwordField = new JTextField();

        JButton connectButton = new JButton("Connect");
        SimpleAction action = new SimpleAction(frame);
        action.add(new SaveControlActivity());
        action.add(new ConnectDatabaseActivity(model));
        connectButton.setAction(action);

        JPanel buttonPane = new JPanel(new BorderLayout());
        JPanel pane       = new JPanel(new GridLayout(0,2));

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
    };

    public void setInfo(DatabaseInfo info)
    {
        copyToControls(info);
    };
}
