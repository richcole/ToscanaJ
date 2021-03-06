/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.database;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.Column;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
import net.sourceforge.toscanaj.model.database.Table;
import net.sourceforge.toscanaj.model.database.DatabaseInfo.Type;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;
import org.tockit.util.FileUtils;

/**
 * @todo at the moment we connect and then disconnect instead of passing the
 *       connection around. This means insourcing SQL scripts twice and some
 *       other overhead. Avoid.
 */
public class DatabaseConnectionInformationView extends JDialog implements
        EventBrokerListener {
    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(DatabaseConnectionInformationView.class);

    private static final String JDBC_ID_STRING = "JDBC";
    private static final String EMBEDDED_DBMS_ID_STRING = "Embedded DBMS";
    private static final String ODBC_ID_STRING = "ODBC Source";
    private static final String ACCESS_FILE_ID_STRING = "Access File";
    private static final String EXCEL_FILE_ID_STRING = "Excel File";

    private static final int MINIMUM_WIDTH = 400;
    private static final int MINIMUM_HEIGHT = 350;

    protected ConceptualSchema conceptualSchema;
    private final EventBroker internalBroker;
    private DatabaseInfo databaseInfo;
    private final DatabaseConnection connection;

    private JButton nextButton;
    private final JLabel stepLabel;
    private WizardPanel currentStep;
    private File openedDatabaseFile;

    private EmbeddedDbConnectionPanel embeddedDbPanel;
    private JdbcConnectionPanel jdbcDbPanel;
    private OdbcConnectionPanel odbcDbPanel;
    private AccessFileConnectionPanel accessDbPanel;
    private ExcelFileConnectionPanel excelDbPanel;
    private KeySelectPanel keySelectPanel;
    private DatabaseTypePanel dbTypePanel;

    boolean newConnectionSet;

    abstract class WizardPanel extends JPanel {
        WizardPanel() {
            super();
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }

        abstract void updateContents();

        abstract String getTitle();

        abstract String getNextButtonText();

        abstract boolean executeStep();

        abstract WizardPanel getNextPanel();
    }

    class DatabaseTypePanel extends WizardPanel {
        private final JRadioButton embDBMSRadioButton;
        private final JRadioButton jdbcRadioButton;
        private final JRadioButton odbcRadioButton;
        private final JRadioButton accessRadioButton;
        private final JRadioButton excelRadioButton;

        DatabaseTypePanel() {
            super();
            embDBMSRadioButton = new JRadioButton(EMBEDDED_DBMS_ID_STRING);
            jdbcRadioButton = new JRadioButton(JDBC_ID_STRING);
            odbcRadioButton = new JRadioButton(ODBC_ID_STRING);
            accessRadioButton = new JRadioButton(ACCESS_FILE_ID_STRING);
            excelRadioButton = new JRadioButton(EXCEL_FILE_ID_STRING);

            final ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(embDBMSRadioButton);
            buttonGroup.add(jdbcRadioButton);
            buttonGroup.add(odbcRadioButton);
            buttonGroup.add(accessRadioButton);
            buttonGroup.add(excelRadioButton);

            updateContents();

            this.setLayout(new GridBagLayout());
            this.add(embDBMSRadioButton,
                    new GridBagConstraints(0, 0, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));
            this.add(jdbcRadioButton,
                    new GridBagConstraints(0, 1, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));
            this.add(odbcRadioButton,
                    new GridBagConstraints(0, 2, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));
            this.add(accessRadioButton,
                    new GridBagConstraints(0, 3, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));
            this.add(excelRadioButton,
                    new GridBagConstraints(0, 4, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));
            this.add(new JPanel(), new GridBagConstraints(0, 4, 1, 1, 1, 1,
                    GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 2, 2));
        }

        @Override
        void updateContents() {
            if (databaseInfo == null) {
                embDBMSRadioButton.setSelected(true);
            } else {
                final DatabaseInfo.Type type = databaseInfo.getType();
                if (type == DatabaseInfo.EMBEDDED) {
                    embDBMSRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.UNDEFINED) {
                    embDBMSRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.JDBC) {
                    jdbcRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.ODBC) {
                    odbcRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.ACCESS_FILE) {
                    accessRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.EXCEL_FILE) {
                    excelRadioButton.setSelected(true);
                } else {
                    throw new RuntimeException("Unknown database type");
                }
            }
        }

        @Override
        String getTitle() {
            return "Database Type:";
        }

        @Override
        String getNextButtonText() {
            return "Use selected type >>";
        }

        @Override
        boolean executeStep() {
            // nothing to do here, we just return different panels
            return true;
        }

        @Override
        WizardPanel getNextPanel() {
            if (embDBMSRadioButton.isSelected()) {
                return embeddedDbPanel;
            } else if (jdbcRadioButton.isSelected()) {
                return jdbcDbPanel;
            } else if (odbcRadioButton.isSelected()) {
                return odbcDbPanel;
            } else if (accessRadioButton.isSelected()) {
                return accessDbPanel;
            } else if (excelRadioButton.isSelected()) {
                return excelDbPanel;
            }
            throw new RuntimeException("Something went really wrong");
        }
    }

    abstract class ConnectionPanel extends WizardPanel {
        @Override
        String getTitle() {
            return "Connection Details:";
        }

        @Override
        String getNextButtonText() {
            return "Connect >>";
        }

        @Override
        WizardPanel getNextPanel() {
            return keySelectPanel;
        }

        boolean connectDatabase() {
            try {
                conceptualSchema.setDatabaseSchema(new DatabaseSchema(
                        internalBroker));
                DatabaseConnection.setConnection(connection);
                connection.connect(databaseInfo);
            } catch (final DatabaseException e) {
                ErrorDialog.showError(this, e, "Connection failed");
            }
            return connection.isConnected();
        }

        protected void setPathInTextField(final JTextField textField,
                final String path) {
            if ("file".equals(conceptualSchema.getLocation().getProtocol())) {
                final File csxFile = new File(conceptualSchema.getLocation()
                        .getPath());
                final File dbFile = new File(path);
                final String dbFilePath = FileUtils.findRelativePath(csxFile,
                        dbFile);
                textField.setText(dbFilePath);
            } else {
                textField.setText(path);
            }
        }

        protected String createAbsoluteLocation(final String inputLocation) {
            if (inputLocation.startsWith("..")
                    || !inputLocation.contains(File.separator)) {
                // we assume the schema has a file URL, since we wouldn't have
                // the relative
                // path otherwise
                return new File(new File(conceptualSchema.getLocation()
                        .getPath()).getParent(), inputLocation).toString();
            } else {
                return new File(inputLocation).toString();
            }
        }
    }

    class EmbeddedDbConnectionPanel extends ConnectionPanel {
        private final JTextField scriptLocationField;

        EmbeddedDbConnectionPanel() {
            super();
            scriptLocationField = new JTextField();
            final JLabel sqlFileLabel = new JLabel("SQL File Location:");
            final JButton fileButton = new JButton("Browse...");
            fileButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    getFileURL(scriptLocationField, "sql",
                            "SQL Scripts (*.sql)");
                }
            });
            fileButton.setMnemonic('f');

            updateContents();
            this.setLayout(new GridBagLayout());

            this.add(sqlFileLabel,
                    new GridBagConstraints(0, 0, 2, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    5), 2, 2));

            this.add(scriptLocationField, new GridBagConstraints(0, 1, 1, 1, 1,
                    0, GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(5, 15, 5, 5), 2,
                    2));
            this.add(fileButton, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                    GridBagConstraints.NORTHEAST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 2, 2));

            this.add(new JPanel(), new GridBagConstraints(0, 3, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.BOTH,
                    new Insets(5, 5, 5, 5), 2, 2));

        }

        @Override
        void updateContents() {
            if (databaseInfo != null
                    && databaseInfo.getType() == DatabaseInfo.EMBEDDED) {
                setPathInTextField(this.scriptLocationField, databaseInfo
                        .getEmbeddedSQLLocation().getPath());
            } else {
                this.scriptLocationField.setText("");
            }
        }

        @Override
        boolean executeStep() {
            final DatabaseInfo embedInfo = DatabaseInfo
                    .getEmbeddedDatabaseInfo();
            databaseInfo.setUrl(embedInfo.getURL());
            databaseInfo.setUserName(embedInfo.getUserName());
            databaseInfo.setPassword(embedInfo.getPassword());
            databaseInfo.setDriverClass(embedInfo.getDriverClass());
            try {
                databaseInfo.setEmbeddedSQLLocation(new File(
                        createAbsoluteLocation(scriptLocationField.getText()))
                        .toURI().toURL());
            } catch (final MalformedURLException e) {
                ErrorDialog.showError(this, e, "Connection failed");
                return false;
            }
            final boolean connected = connectDatabase();
            if (!connected) {
                return false;
            }
            try {
                connection.executeScript(databaseInfo.getEmbeddedSQLLocation());
            } catch (final DatabaseException e) {
                ErrorDialog.showError(this, e, "Script error");
                return false;
            }
            return true;
        }
    }

    class JdbcConnectionPanel extends ConnectionPanel {
        private final JTextField urlField;
        private final JTextField userNameField;
        private final JPasswordField passwordField;
        private final JTextField driverField;

        JdbcConnectionPanel() {
            super();
            urlField = new JTextField();
            userNameField = new JTextField();
            passwordField = new JPasswordField();
            driverField = new JTextField();

            updateContents();

            this.setLayout(new GridBagLayout());
            this.add(new JLabel("URL: "),
                    new GridBagConstraints(0, 0, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));

            this.add(urlField, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JLabel("Driver: "),
                    new GridBagConstraints(0, 2, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));

            this.add(driverField, new GridBagConstraints(0, 3, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JLabel("Username: "),
                    new GridBagConstraints(0, 4, 1, 1, 1, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));

            this.add(userNameField, new GridBagConstraints(0, 5, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JLabel("Password: "),
                    new GridBagConstraints(0, 6, 1, 1, 0, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));

            this.add(passwordField, new GridBagConstraints(0, 7, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JPanel(), new GridBagConstraints(0, 8, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.BOTH,
                    new Insets(5, 5, 5, 0), 2, 2));
        }

        @Override
        void updateContents() {
            if (databaseInfo != null
                    && databaseInfo.getType() == DatabaseInfo.JDBC) {
                this.urlField.setText(databaseInfo.getURL());
                this.userNameField.setText(databaseInfo.getUserName());
                this.passwordField.setText(databaseInfo.getPassword());
                this.driverField.setText(databaseInfo.getDriverClass());
            } else {
                this.urlField.setText("");
                this.userNameField.setText("");
                this.passwordField.setText("");
                this.driverField.setText("");
            }
        }

        @Override
        boolean executeStep() {
            databaseInfo.setUrl(urlField.getText());
            databaseInfo.setUserName(userNameField.getText());
            databaseInfo.setPassword(new String(passwordField.getPassword()));
            databaseInfo.setDriverClass(driverField.getText());
            databaseInfo.setEmbeddedSQLLocation((String) null);
            return connectDatabase();
        }
    }

    class OdbcConnectionPanel extends ConnectionPanel {
        private final JTextField dataSourceNameField;
        private final JTextField userNameField;
        private final JPasswordField passwordField;

        OdbcConnectionPanel() {
            super();
            dataSourceNameField = new JTextField();
            userNameField = new JTextField();
            passwordField = new JPasswordField();

            updateContents();

            this.setLayout(new GridBagLayout());
            this.add(new JLabel("Data Source Name: "),
                    new GridBagConstraints(0, 0, 1, 1, 0, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));

            this.add(dataSourceNameField, new GridBagConstraints(0, 1, 1, 1, 1,
                    0, GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JLabel("Username: "),
                    new GridBagConstraints(0, 2, 1, 1, 0, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));

            this.add(userNameField, new GridBagConstraints(0, 3, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JLabel("Password: "),
                    new GridBagConstraints(0, 4, 1, 1, 0, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5,
                                    0), 2, 2));

            this.add(passwordField, new GridBagConstraints(0, 5, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));
            this.add(new JPanel(), new GridBagConstraints(0, 6, 1, 2, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                    new Insets(5, 5, 5, 0), 2, 2));
        }

        @Override
        void updateContents() {
            if (databaseInfo != null
                    && databaseInfo.getType() == DatabaseInfo.ODBC) {
                this.dataSourceNameField.setText(databaseInfo
                        .getOdbcDataSourceName());
                this.userNameField.setText(databaseInfo.getUserName());
                this.passwordField.setText(databaseInfo.getPassword());
            } else {
                this.dataSourceNameField.setText("");
                this.userNameField.setText("");
                this.passwordField.setText("");
            }
        }

        @Override
        boolean executeStep() {
            databaseInfo.setOdbcDataSource(dataSourceNameField.getText(),
                    userNameField.getText(), new String(passwordField
                            .getPassword()));
            return connectDatabase();
        }
    }

    class AccessFileConnectionPanel extends ConnectionPanel {
        private final JTextField fileUrlField;
        private final JTextField userNameField;
        private final JPasswordField passwordField;

        AccessFileConnectionPanel() {
            super();
            fileUrlField = new JTextField();
            userNameField = new JTextField();
            passwordField = new JPasswordField();

            final JButton fileButton = new JButton("Browse...");
            fileButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    getFileURL(fileUrlField, "mdb",
                            "Microsoft Access Databases (*.mdb)");
                }
            });
            fileButton.setMnemonic('f');

            updateContents();

            this.setLayout(new GridBagLayout());
            this.add(new JLabel("Access Database Location: "),
                    new GridBagConstraints(0, 0, 1, 1, 0, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 2,
                            2));

            this.add(fileUrlField, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 0), 2,
                    2));

            this.add(fileButton, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 2, 2));
            this.add(new JLabel("Username: "), new GridBagConstraints(0, 3, 1,
                    1, 0, 0, GridBagConstraints.NORTHWEST,
                    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));

            this.add(userNameField, new GridBagConstraints(0, 4, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JLabel("Password: "), new GridBagConstraints(0, 5, 1,
                    1, 0, 0, GridBagConstraints.NORTHWEST,
                    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));

            this.add(passwordField, new GridBagConstraints(0, 6, 1, 1, 10, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JPanel(), new GridBagConstraints(0, 7, 1, 1, 1, 1,
                    GridBagConstraints.WEST, GridBagConstraints.VERTICAL,
                    new Insets(5, 5, 5, 5), 2, 2));
        }

        @Override
        void updateContents() {
            if (databaseInfo != null
                    && databaseInfo.getType() == DatabaseInfo.ACCESS_FILE) {
                setPathInTextField(this.fileUrlField, databaseInfo
                        .getAccessFileUrl());
                this.userNameField.setText(databaseInfo.getUserName());
                this.passwordField.setText(databaseInfo.getPassword());
            } else {
                this.fileUrlField.setText("");
                this.userNameField.setText("");
                this.passwordField.setText("");
            }
        }

        @Override
        boolean executeStep() {
            databaseInfo.setAccessFileInfo(createAbsoluteLocation(fileUrlField
                    .getText()), userNameField.getText(), new String(
                    passwordField.getPassword()));
            return connectDatabase();
        }
    }

    class ExcelFileConnectionPanel extends ConnectionPanel {
        private final JTextField fileUrlField;
        private final JTextField userNameField;
        private final JPasswordField passwordField;

        ExcelFileConnectionPanel() {
            super();
            fileUrlField = new JTextField();
            userNameField = new JTextField();
            passwordField = new JPasswordField();

            final JButton fileButton = new JButton("Browse...");
            fileButton.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    getFileURL(fileUrlField, "xls",
                            "Microsoft Excel Workbooks (*.xls)");
                }
            });
            fileButton.setMnemonic('f');

            updateContents();

            this.setLayout(new GridBagLayout());
            this.add(new JLabel("Excel Workbook Location: "),
                    new GridBagConstraints(0, 0, 1, 1, 0, 0,
                            GridBagConstraints.NORTHWEST,
                            GridBagConstraints.NONE, new Insets(5, 5, 5, 0), 2,
                            2));

            this.add(fileUrlField, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 0), 2,
                    2));

            this.add(fileButton, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 2, 2));
            this.add(new JLabel("Username: "), new GridBagConstraints(0, 3, 1,
                    1, 0, 0, GridBagConstraints.NORTHWEST,
                    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));

            this.add(userNameField, new GridBagConstraints(0, 4, 1, 1, 1, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this.add(new JLabel("Password: "), new GridBagConstraints(0, 5, 1,
                    1, 0, 0, GridBagConstraints.NORTHWEST,
                    GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 2, 2));

            this.add(passwordField, new GridBagConstraints(0, 6, 1, 1, 10, 0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 15, 5, 5), 2,
                    2));

            this
                    .add(
                            new JLabel(
                                    "Note: this works only with Excel files having named ranges"),
                            new GridBagConstraints(0, 7, 1, 1, 1, 1,
                                    GridBagConstraints.SOUTHWEST,
                                    GridBagConstraints.VERTICAL, new Insets(5,
                                            5, 5, 5), 2, 2));
        }

        @Override
        void updateContents() {
            if (databaseInfo != null
                    && databaseInfo.getType() == DatabaseInfo.EXCEL_FILE) {
                setPathInTextField(this.fileUrlField, databaseInfo
                        .getExcelFileUrl());
                this.userNameField.setText(databaseInfo.getUserName());
                this.passwordField.setText(databaseInfo.getPassword());
            } else {
                this.fileUrlField.setText("");
                this.userNameField.setText("");
                this.passwordField.setText("");
            }
        }

        @Override
        boolean executeStep() {
            databaseInfo.setExcelFileInfo(createAbsoluteLocation(fileUrlField
                    .getText()), userNameField.getText(), new String(
                    passwordField.getPassword()));
            return connectDatabase();
        }
    }

    class KeySelectPanel extends WizardPanel {
        private final DatabaseSchemaView tableView;

        KeySelectPanel(final EventBroker broker) {
            super();
            this.setLayout(new BorderLayout());
            this.tableView = new DatabaseSchemaView(broker);
            this.add(tableView, BorderLayout.CENTER);
        }

        @Override
        String getTitle() {
            return "Select Key:";
        }

        @Override
        String getNextButtonText() {
            return "Done";
        }

        @Override
        boolean executeStep() {
            final Table sqlTable = this.tableView.getTable();
            final Column sqlKey = this.tableView.getKey();
            if (sqlTable == null || sqlKey == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select a table/column pair as primary key",
                        "No key selected", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            databaseInfo.setTable(sqlTable);
            databaseInfo.setKey(sqlKey);
            JOptionPane.showMessageDialog(this,
                    "Database connection established", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            return true;
        }

        @Override
        WizardPanel getNextPanel() {
            return null;
        }

        @Override
        void updateContents() {
            final Table table = databaseInfo.getTable();
            final Column key = databaseInfo.getKey();
            if (table != null && key != null) {
                final String tableName = table.getDisplayName();
                final String keyName = key.getDisplayName();
                tableView.setKey(tableName, keyName);
            }
        }
    }

    /**
     * Construct an instance of this view
     */
    public DatabaseConnectionInformationView(final JFrame frame,
            final ConceptualSchema conceptualSchema,
            final EventBroker eventBroker) {
        super(frame, "Database connection", true);
        this.conceptualSchema = conceptualSchema;
        this.internalBroker = new EventBroker();
        this.databaseInfo = new DatabaseInfo();
        new DatabaseSchema(internalBroker); // at the moment needed for event
        // processing
        this.connection = new DatabaseConnection(internalBroker);

        initializePanels();

        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new GridBagLayout());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                int width = getWidth();
                int height = getHeight();
                if (width < MINIMUM_WIDTH) {
                    width = MINIMUM_WIDTH;
                }
                if (height < MINIMUM_HEIGHT) {
                    height = MINIMUM_HEIGHT;
                }
                setSize(width, height);
            }

            @Override
            public void componentShown(final ComponentEvent e) {
                componentResized(e);
            }
        });

        this.stepLabel = new JLabel();

        final JPanel buttonPane = createButtonPanel();

        contentPane.add(stepLabel, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 10, 5, 5), 2, 2));
        contentPane.add(buttonPane, new GridBagConstraints(0, 2, 1, 1, 1, 0,
                GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL,
                new Insets(0, 5, 5, 5), 2, 2));

        setCurrentPanel(this.dbTypePanel);

        preferences.restoreWindowPlacement(this, new Rectangle(100, 100,
                MINIMUM_WIDTH, MINIMUM_WIDTH));

        eventBroker.subscribe(this, DatabaseInfoChangedEvent.class,
                Object.class);
        eventBroker.subscribe(this, NewConceptualSchemaEvent.class,
                Object.class);
    }

    protected void initializePanels() {
        dbTypePanel = new DatabaseTypePanel();
        this.embeddedDbPanel = new EmbeddedDbConnectionPanel();
        this.jdbcDbPanel = new JdbcConnectionPanel();
        this.odbcDbPanel = new OdbcConnectionPanel();
        this.accessDbPanel = new AccessFileConnectionPanel();
        this.excelDbPanel = new ExcelFileConnectionPanel();
        this.keySelectPanel = new KeySelectPanel(this.internalBroker);
    }

    private JPanel createButtonPanel() {
        nextButton = new JButton();
        nextButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean success = executeCurrentStep();
                if (success) {
                    gotoNextStep();
                }
            }
        });

        final JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                setActionCancelledFlags();
                setVisible(false);
            }
        });

        final JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.add(nextButton);
        buttonPane.add(cancelButton);
        return buttonPane;
    }

    private void setActionCancelledFlags() {
        this.newConnectionSet = false;
    }

    protected boolean executeCurrentStep() {
        try {
            return this.currentStep.executeStep();
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Step failed");
        }
        return false;
    }

    protected void gotoNextStep() {
        final WizardPanel nextPanel = this.currentStep.getNextPanel();
        if (nextPanel == null) {
            setVisible(false);
            this.conceptualSchema.setDatabaseInfo(this.databaseInfo);
            this.newConnectionSet = true;
        } else {
            setCurrentPanel(nextPanel);
        }
    }

    protected void setCurrentPanel(final WizardPanel panel) {
        final Container contentPane = this.getContentPane();
        if (this.currentStep != null) {
            contentPane.remove(this.currentStep);
        }
        this.currentStep = panel;
        this.stepLabel.setText(panel.getTitle());
        this.nextButton.setText(panel.getNextButtonText());
        try {
            panel.updateContents();
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Internal problem");
        }
        contentPane.add(panel, new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
                        0, 5, 5, 5), 2, 2));
        contentPane.invalidate();
        this.repaint();
    }

    private void getFileURL(final JTextField urlField, final String extension,
            final String description) {
        final JFileChooser openDialog = new JFileChooser(openedDatabaseFile);
        openDialog.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(final File f) {
                if (f.isDirectory()) {
                    return true;
                }
                String ext = "";
                final String s = f.getName();
                final int i = s.lastIndexOf('.');
                if (i > 0 && i < s.length() - 1) {
                    ext = s.substring(i + 1).toLowerCase();
                }
                return ext.equals(extension);
            }

            @Override
            public String getDescription() {
                return description;
            }
        });

        if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openedDatabaseFile = openDialog.getSelectedFile();
            final URL schemaURL = conceptualSchema.getLocation();
            String dbFilePath;
            if (schemaURL != null && "file".equals(schemaURL.getProtocol())) {
                final File csxFile = new File(schemaURL.getPath());
                dbFilePath = FileUtils.findRelativePath(csxFile,
                        openedDatabaseFile);
            } else {
                dbFilePath = openedDatabaseFile.getAbsolutePath();
            }
            urlField.setText(dbFilePath);
        }
    }

    public void processEvent(final Event event) {
        final ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        this.conceptualSchema = changeEvent.getConceptualSchema();
        final DatabaseInfo existingInfo = this.conceptualSchema
                .getDatabaseInfo();
        if (existingInfo != null) {
            this.databaseInfo = existingInfo;
        } else {
            this.databaseInfo = new DatabaseInfo();
        }
        if (this.currentStep != null) {
            try {
                this.currentStep.updateContents();
            } catch (final Exception e) {
                ErrorDialog.showError(this, e, "Internal problem");
            }
        }
    }

    @Override
    public void setVisible(final boolean visible) {
        if (visible) {
            this.newConnectionSet = false;
            super.setVisible(true);
        } else {
            preferences.storeWindowPlacement(this);
            super.setVisible(false);
            if (this.connection.isConnected()) {
                try {
                    this.connection.disconnect();
                } catch (final DatabaseException e) {
                    ErrorDialog.showError(this, e,
                            "Database not closed properly");
                }
            }
            setCurrentPanel(this.dbTypePanel);
        }
    }

    public boolean newConnectionWasSet() {
        return this.newConnectionSet;
    }
}
