package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.action.SimpleActivity;
import net.sourceforge.toscanaj.model.DatabaseInfo;

import javax.swing.*;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class DatabaseConnectionInformationView extends JPanel implements Observer {
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

    public void update(Observable o, Object arg) {
        //copyToControls((DatabaseInfo) o);
    };

    /**
     * Construct an instance of this view
     */
    public DatabaseConnectionInformationView(JFrame frame, DatabaseInfo databaseInfo) {
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
        SimpleAction action = new SimpleAction(frame, "Connect");
        action.add(new SaveControlActivity());
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
    };

    public void setInfo(DatabaseInfo info) {
        copyToControls(info);
    };
}
