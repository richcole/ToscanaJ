/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
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

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.MalformedURLException;



/**
 * @todo at the moment we connect and then disconnect instead of passing the
 * connection around. This means insourcing SQL scripts twice and some other
 * overhead. Avoid.
 */
public class DatabaseConnectionInformationView extends JDialog
												implements EventBrokerListener {
	private static final String JDBC_ID_STRING = "JDBC";
	private static final String EMBEDDED_DBMS_ID_STRING = "Embedded DBMS";
	private static final String ODBC_ID_STRING = "ODBC Source";
	private static final String ACCESS_FILE_ID_STRING = "Access File";
	
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 350;
	
	protected ConceptualSchema conceptualSchema;
	private EventBroker internalBroker;
	private DatabaseInfo databaseInfo;
	private DatabaseConnection connection;

    private JButton nextButton;
    private JLabel stepLabel;
    private WizardPanel currentStep;
    private File openedDatabaseFile;

	private EmbeddedDbConnectionPanel embeddedDbPanel;
    private JdbcConnectionPanel jdbcDbPanel;
    private OdbcConnectionPanel odbcDbPanel;
    private AccessFileConnectionPanel accessDbPanel;
	private KeySelectPanel keySelectPanel;
    private DatabaseTypePanel dbTypePanel;
    private DatabaseSchema databaseSchema;
    private boolean newConnectionSet;
    
    private Frame owner;
    
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
        private JRadioButton embDBMSRadioButton;
        private JRadioButton jdbcRadioButton;
        private JRadioButton accessRadioButton;
        private JRadioButton odbcRadioButton;
        DatabaseTypePanel() {
            super();
            embDBMSRadioButton = new JRadioButton(EMBEDDED_DBMS_ID_STRING);
            jdbcRadioButton = new JRadioButton(JDBC_ID_STRING);
            accessRadioButton = new JRadioButton(ACCESS_FILE_ID_STRING);
            odbcRadioButton = new JRadioButton(ODBC_ID_STRING);
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(embDBMSRadioButton);
            buttonGroup.add(jdbcRadioButton);
            buttonGroup.add(accessRadioButton);
            buttonGroup.add(odbcRadioButton);

            updateContents();

			this.setLayout(new GridBagLayout());
            this.add(embDBMSRadioButton,new GridBagConstraints(
                    0,0,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0),
                    2,2));
            this.add(jdbcRadioButton,new GridBagConstraints(
                    0,1,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0),
                    2,2));
            this.add(odbcRadioButton,new GridBagConstraints(
                    0,2,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0),
                    2,2));
            this.add(accessRadioButton,new GridBagConstraints(
                    0,3,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0),
                    2,2));
            this.add(new JPanel(),new GridBagConstraints(
                    0,4,1,1,1,1,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0),
                    2,2));
        }
        void updateContents() {
            if(databaseInfo == null) {
                embDBMSRadioButton.setSelected(true);
            } else {
            	Type type = databaseInfo.getType();
            	if(type == DatabaseInfo.EMBEDDED) {
                    embDBMSRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.UNDEFINED) {
                    embDBMSRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.JDBC) {
            	    jdbcRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.ODBC) {
            	    odbcRadioButton.setSelected(true);
                } else if (type == DatabaseInfo.ACCESS_FILE) {
            	    accessRadioButton.setSelected(true);
            	} else {
            		throw new RuntimeException("Unknown database type");
            	}
            }
        }
        String getTitle() {
            return "Database Type:";
        }
        String getNextButtonText() {
            return "Use selected type >>";
        }
        boolean executeStep() {
        	// nothing to do here, we just return different panels
        	return true;
        }
        WizardPanel getNextPanel() {
            if(embDBMSRadioButton.isSelected()) {
                return embeddedDbPanel;
            } else if(jdbcRadioButton.isSelected()) {
                return jdbcDbPanel;
            } else if(odbcRadioButton.isSelected()) {
                return odbcDbPanel;
            } else if(accessRadioButton.isSelected()) {
                return accessDbPanel;
            }
            throw new RuntimeException("Something went really wrong");
        }
    }

    abstract class ConnectionPanel extends WizardPanel {
        String getTitle() {
            return "Connection Details:";
        }
        String getNextButtonText() {
            return "Connect >>";
        }
        WizardPanel getNextPanel() {
            return keySelectPanel;
        }
        boolean connectDatabase() {
        	try {
				DatabaseConnection.setConnection(connection);
                connection.connect(databaseInfo);
            } catch (DatabaseException e) {
            	ErrorDialog.showError(this,e,"Connection failed");
            }
        	return connection.isConnected();
        }
    }
    
    class EmbeddedDbConnectionPanel extends ConnectionPanel {
        private JTextField scriptLocationField;
		private JTextField csvFileLocationField;

    	EmbeddedDbConnectionPanel() {
    		super();
    	    scriptLocationField = new JTextField();
    	    JLabel sqlFileLabel = new JLabel("SQL File Location:");
    	    JButton fileButton = new JButton("Browse...");
    	    fileButton.addActionListener(new ActionListener() {
    	        public void actionPerformed(ActionEvent e) {
    	            getFileURL(scriptLocationField, "sql", "SQL Scripts (*.sql)");
    	        }
    	    });
    	    fileButton.setMnemonic('f');

			JLabel csvFileLabel = new JLabel("CSV File Location:");
			csvFileLocationField = new JTextField();
			JButton csvFileButton = new JButton("Browse...");
			csvFileButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					getFileURL(csvFileLocationField, "csv", "Comma Separated Files (*.csv)");
				}
			});
            updateContents();
    	    this.setLayout(new GridBagLayout());

    	    this.add(sqlFileLabel,new GridBagConstraints(
    	            0,0,2,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 5),
    	            2,2));

    	    this.add(scriptLocationField,new GridBagConstraints(
    	            0,1,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets( 5, 15, 5, 5),
    	            2,2));
    	    this.add(fileButton,new GridBagConstraints(
    	            0,2,1,1,0,0,
    	            GridBagConstraints.NORTHEAST,
    	            GridBagConstraints.NONE,
    	            new Insets(5, 5, 5, 5),
    	            2,2));

		   this.add(new JPanel(),new GridBagConstraints(
				   0,3,1,1,1,1,
				   GridBagConstraints.WEST,
				   GridBagConstraints.BOTH,
				   new Insets(5, 5, 5, 5),
				   2,2));
			
    	}

        void updateContents() {
            if(databaseInfo != null && databaseInfo.getType() == DatabaseInfo.EMBEDDED) {
            	this.scriptLocationField.setText(databaseInfo.getEmbeddedSQLLocation().getPath());
            } else {
            	this.scriptLocationField.setText("");
            }
        }
    	
        boolean executeStep() {
            DatabaseInfo embedInfo = DatabaseInfo.getEmbeddedDatabaseInfo();
            databaseInfo.setUrl(embedInfo.getURL());
            databaseInfo.setUserName(embedInfo.getUserName());
            databaseInfo.setPassword(embedInfo.getPassword());
            databaseInfo.setDriverClass(embedInfo.getDriverClass());
            try {
                databaseInfo.setEmbeddedSQLLocation(new File(scriptLocationField.getText()).toURL());
            } catch (MalformedURLException e) {
            	ErrorDialog.showError(this,e,"URL invalid");
            	return false;
            }
            boolean connected = connectDatabase();
            if(!connected) {
            	return false;
            }
            try {
                connection.executeScript(databaseInfo.getEmbeddedSQLLocation());
            } catch (DatabaseException e) {
            	ErrorDialog.showError(this, e, "Script error");
            	return false;
            }
            if (csvFileLocationField.getText().length() > 0) {
            	System.out.println("connected = " + connected);
				CSVImportDetailsDialog csvImportDialog = new CSVImportDetailsDialog(owner, csvFileLocationField.getText(), connection);
            }
            return true;
        }
    }

    class JdbcConnectionPanel extends ConnectionPanel {
        private JTextField urlField;
        private JTextField userNameField;
        private JPasswordField passwordField;
        private JTextField driverField;

    	JdbcConnectionPanel() {
            super();
    	    urlField = new JTextField();
    	    userNameField = new JTextField();
    	    passwordField = new JPasswordField();
    	    driverField = new JTextField();

            updateContents();

    	    this.setLayout(new GridBagLayout());
    	    this.add(new JLabel("URL: "),new GridBagConstraints(
    	            0,0,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(urlField,new GridBagConstraints(
    	            0,1,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JLabel("Driver: "),new GridBagConstraints(
    	            0,2,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(driverField,new GridBagConstraints(
    	            0,3,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JLabel("Username: "),new GridBagConstraints(
    	            0,4,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(userNameField,new GridBagConstraints(
    	            0,5,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JLabel("Password: "),new GridBagConstraints(
    	            0,6,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(passwordField,new GridBagConstraints(
    	            0,7,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JPanel(),new GridBagConstraints(
    	            0,8,1,1,1,1,
    	            GridBagConstraints.WEST,
    	            GridBagConstraints.BOTH,
    	            new Insets(5, 5, 5, 0),
    	            2,2));
        }

        void updateContents() {
            if(databaseInfo != null && databaseInfo.getType() == DatabaseInfo.JDBC) {
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
        private JTextField dataSourceNameField;
        private JTextField userNameField;
        private JPasswordField passwordField;

    	OdbcConnectionPanel() {
            super();
    	    dataSourceNameField = new JTextField();
    	    userNameField = new JTextField();
    	    passwordField = new JPasswordField();

            updateContents();

    	    this.setLayout(new GridBagLayout());
    	    this.add(new JLabel("Data Source Name: "),new GridBagConstraints(
    	            0,0,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(dataSourceNameField,new GridBagConstraints(
    	            0,1,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JLabel("Username: "),new GridBagConstraints(
    	            0,2,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(userNameField,new GridBagConstraints(
    	            0,3,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JLabel("Password: "),new GridBagConstraints(
    	            0,4,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(passwordField,new GridBagConstraints(
    	            0,5,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));
    	    this.add(new JPanel(),new GridBagConstraints(
    	            0,6,1,2,1,1,
    	            GridBagConstraints.WEST,
    	            GridBagConstraints.VERTICAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));
        }
        void updateContents() {
            if(databaseInfo != null && databaseInfo.getType() == DatabaseInfo.ODBC) {
                this.dataSourceNameField.setText(databaseInfo.getOdbcDataSourceName());
                this.userNameField.setText(databaseInfo.getUserName());
                this.passwordField.setText(databaseInfo.getPassword());
            } else {
                this.dataSourceNameField.setText("");
                this.userNameField.setText("");
                this.passwordField.setText("");
            }
        }
        boolean executeStep() {
            databaseInfo.setOdbcDataSource(dataSourceNameField.getText(), userNameField.getText(), new String(passwordField.getPassword()));
            return connectDatabase();
        }
    }

    class AccessFileConnectionPanel extends ConnectionPanel {
        private JTextField fileUrlField;
        private JTextField userNameField;
        private JPasswordField passwordField;

    	AccessFileConnectionPanel() {
            super();
    	    fileUrlField = new JTextField();
    	    userNameField = new JTextField();
    	    passwordField = new JPasswordField();

    	    JButton fileButton = new JButton("Browse...");
    	    fileButton.addActionListener(new ActionListener() {
    	        public void actionPerformed(ActionEvent e) {
    	            getFileURL(fileUrlField, "mdb", "Microsoft Access Databases (*.mdb)");
    	        }
    	    });
    	    fileButton.setMnemonic('f');

            updateContents();

    	    this.setLayout(new GridBagLayout());
    	    this.add(new JLabel("File Access Location: "),new GridBagConstraints(
    	            0,0,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.NONE,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(fileUrlField,new GridBagConstraints(
    	            0,1,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 0),
    	            2,2));

    	    this.add(fileButton,new GridBagConstraints(
    	            0,2,1,1,0,0,
    	            GridBagConstraints.EAST,
    	            GridBagConstraints.NONE,
    	            new Insets(5, 5, 5, 5),
    	            2,2));
    	    this.add(new JLabel("Username: "),new GridBagConstraints(
    	            0,3,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.NONE,
    	            new Insets(5, 5, 5, 5),
    	            2,2));

    	    this.add(userNameField,new GridBagConstraints(
    	            0,4,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JLabel("Password: "),new GridBagConstraints(
    	            0,5,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.NONE,
    	            new Insets(5, 5, 5, 5),
    	            2,2));

    	    this.add(passwordField,new GridBagConstraints(
    	            0,6,1,1,10,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(0, 15, 5, 5),
    	            2,2));

    	    this.add(new JPanel(),new GridBagConstraints(
    	            0,7,1,1,1,1,
    	            GridBagConstraints.WEST,
    	            GridBagConstraints.VERTICAL,
    	            new Insets(5, 5, 5, 5),
    	            2,2));
        }
        void updateContents() {
            if(databaseInfo != null && databaseInfo.getType() == DatabaseInfo.ACCESS_FILE) {
            	this.fileUrlField.setText(databaseInfo.getAccessFileUrl());
                this.userNameField.setText(databaseInfo.getUserName());
                this.passwordField.setText(databaseInfo.getPassword());
            } else {
                this.fileUrlField.setText("");
                this.userNameField.setText("");
                this.passwordField.setText("");
            }
        }
        boolean executeStep() {
            databaseInfo.setAccessFileInfo(fileUrlField.getText(), userNameField.getText(), new String(passwordField.getPassword()));
            return connectDatabase();
        }
    }

    class KeySelectPanel extends WizardPanel {
        private DatabaseSchemaView tableView;

        KeySelectPanel(EventBroker broker) {
        	super();
        	this.setLayout(new BorderLayout());
        	this.tableView = new DatabaseSchemaView(broker);
        	this.add(tableView, BorderLayout.CENTER);
        }
        String getTitle() {
            return "Select Key:";
        }
        String getNextButtonText() {
            return "Done";
        }
        boolean executeStep() {
            Table sqlTable = this.tableView.getTable();
            Column sqlKey = this.tableView.getKey();
            if(sqlTable == null || sqlKey == null) {
        		JOptionPane.showMessageDialog(this, "Please select a table/column pair as primary key", "No key selected", 
        									  JOptionPane.ERROR_MESSAGE);
        		return false;
        	}
        	databaseInfo.setTable(sqlTable);
        	databaseInfo.setKey(sqlKey);
        	return true;
        }
        WizardPanel getNextPanel() {
            return null;
        }
        void updateContents() {
            Table table = databaseInfo.getTable();
            Column key = databaseInfo.getKey();
            if(table != null && key != null) {
				String tableName = table.getDisplayName();
				String keyName = key.getDisplayName();
        		tableView.setKey(tableName, keyName);
        	}
        }
    }

	/**
	 * Construct an instance of this view
	 */
	public DatabaseConnectionInformationView(JFrame frame,
												ConceptualSchema conceptualSchema,
												EventBroker eventBroker) {
		super(frame, "Database connection", true);
		this.conceptualSchema = conceptualSchema;
		this.internalBroker = new EventBroker();
		this.databaseInfo = new DatabaseInfo();
		this.connection = new DatabaseConnection(internalBroker);
        this.databaseSchema = new DatabaseSchema(internalBroker);
        this.owner = frame;
		
        initializePanels();

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		
		addComponentListener( new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				if (width < MINIMUM_WIDTH) width = MINIMUM_WIDTH;
				if (height < MINIMUM_HEIGHT) height = MINIMUM_HEIGHT;
				setSize(width, height);
			}
			public void componentShown(ComponentEvent e) {
				componentResized(e);
			}
		});
		
        this.stepLabel = new JLabel();
				
        JPanel buttonPane = createButtonPanel(frame);
		
	    contentPane.add(stepLabel,new GridBagConstraints(
	            0,0,1,1,1,0,
	            GridBagConstraints.WEST,
	            GridBagConstraints.HORIZONTAL,
	            new Insets(0, 10, 5, 5),
	            2,2));
		contentPane.add(buttonPane, new GridBagConstraints(
				0,2,1,1,1,0,
				GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 5, 5),
				2,2));
				
		setCurrentPanel(this.dbTypePanel);
				
		ConfigurationManager.restorePlacement(
			"DatabaseConnectionInformationView",
			this,
			new Rectangle(100, 100, MINIMUM_WIDTH, MINIMUM_WIDTH));

	    eventBroker.subscribe(
	        this,
	        DatabaseInfoChangedEvent.class,
	        Object.class);
	    eventBroker.subscribe(
	        this,
	        NewConceptualSchemaEvent.class,
	        Object.class);
	}

    protected void initializePanels() {
        dbTypePanel = new DatabaseTypePanel();
        this.embeddedDbPanel = new EmbeddedDbConnectionPanel();
        this.jdbcDbPanel = new JdbcConnectionPanel();
        this.odbcDbPanel = new OdbcConnectionPanel();
        this.accessDbPanel = new AccessFileConnectionPanel();
        this.keySelectPanel = new KeySelectPanel(this.internalBroker);
    }

    public JPanel createButtonPanel(JFrame frame) {
        nextButton = new JButton();
        nextButton.addActionListener(new ActionListener(){
        	public void actionPerformed(ActionEvent e) {
        		boolean success = executeCurrentStep();
        		if(success) {
        			gotoNextStep();
        		}
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		hide();
        	}
        });
        
        JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPane.add(nextButton);
        buttonPane.add(cancelButton);
        return buttonPane;
    }
    
    protected boolean executeCurrentStep() {
    	try {
   			return this.currentStep.executeStep();
    	} catch(Exception e) {
    		ErrorDialog.showError(this,e,"Step failed");
    	}
    	return false;
    }

    protected void gotoNextStep() {
		WizardPanel nextPanel = this.currentStep.getNextPanel();
        if(nextPanel == null) {
            hide();
            this.conceptualSchema.setDatabaseInfo(this.databaseInfo);
            this.newConnectionSet = true;
        } else {
        	setCurrentPanel(nextPanel);
        }
    }

    protected void setCurrentPanel(WizardPanel panel) {
        Container contentPane = this.getContentPane();
        if(this.currentStep != null) {
            contentPane.remove(this.currentStep);
        }
        this.currentStep = panel;
        this.stepLabel.setText(panel.getTitle());
        this.nextButton.setText(panel.getNextButtonText());
        try {
	        panel.updateContents();
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Internal problem");
        }
        contentPane.add(panel,new GridBagConstraints(
                0,1,1,1,1,1,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5),
                2,2));
        contentPane.invalidate();
        this.repaint();
    }

	private void getFileURL(JTextField urlField, final String extension, final String description) {
		JFileChooser openDialog;
		if (openedDatabaseFile != null) {
			openDialog = new JFileChooser(openedDatabaseFile);
		} else {
			openDialog = new JFileChooser(System.getProperty("user.dir"));
		}
		openDialog.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}
				String ext = "";
				String s = f.getName();
				int i = s.lastIndexOf('.');
				if (i > 0 && i < s.length() - 1) {
					ext = s.substring(i + 1).toLowerCase();
				}
				return ext.equals(extension);
			}
			public String getDescription() {
				return description;
			}
		});

		if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			openedDatabaseFile = openDialog.getSelectedFile();
			String fileURL = openedDatabaseFile.getAbsolutePath();
			urlField.setText(fileURL);
		}
	}

	public void processEvent(Event event) {
		ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        this.conceptualSchema = changeEvent.getConceptualSchema();
        DatabaseInfo existingInfo = this.conceptualSchema.getDatabaseInfo();
        if(existingInfo != null) {
        	this.databaseInfo = existingInfo;
        } else {
        	this.databaseInfo = new DatabaseInfo();
        }
        if(this.currentStep != null) {
        	try {
        		this.currentStep.updateContents();
        	} catch (Exception e) {
        		ErrorDialog.showError(this, e, "Internal problem");
        	}
        }
	}
	
	public void hide() {
		super.hide();
		if(this.connection.isConnected()) {
			try {
                this.connection.disconnect();
            } catch (DatabaseException e) {
            	ErrorDialog.showError(this,e,"Database not closed properly");
            }
		}
	    setCurrentPanel(this.dbTypePanel);
		ConfigurationManager.storePlacement(
			"DatabaseConnectionInformationView",
			this);
	}
	
	public void show() {
		this.newConnectionSet = false;
		super.show();
	}
	
	public boolean newConnectionWasSet() {
		return this.newConnectionSet;
	}
}
