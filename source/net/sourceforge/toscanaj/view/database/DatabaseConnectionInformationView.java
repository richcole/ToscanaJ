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
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseSchema;
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
import java.net.URL;

/**
 * @todo at the moment we connect and then disconnect instead of passing the
 * connection around. This means insourcing SQL scripts twice and some other
 * overhead. Avoid.
 */
public class DatabaseConnectionInformationView extends JDialog
												implements EventBrokerListener {

	private static final String ODBC_PREFIX = "jdbc:odbc:";
	private static final String ACCESS_FILE_URL_PREFIX = 
				"jdbc:odbc:DRIVER=Microsoft Access Driver (*.mdb); DBQ=";
	private static final String ACCESS_FILE_URL_END = 
				";UserCommitSync=Yes;Threads=3;SafeTransactions=0;PageTimeout=5;" +
				"MaxScanRows=8;MaxBufferSize=2048;DriverId=281";

	private static final String JDBC_ODBC_BRIDGE_DRIVER = 
				"sun.jdbc.odbc.JdbcOdbcDriver";
			
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
    
    abstract class WizardPanel extends JPanel {
    	WizardPanel() {
    		super();
    	    setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
    	}
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
            embDBMSRadioButton.setSelected(true);

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
                connection.connect(databaseInfo);
            } catch (DatabaseException e) {
            	ErrorDialog.showError(this,e,"Connection failed");
            }
        	return connection.isConnected();
        }
    }
    
    class EmbeddedDbConnectionPanel extends ConnectionPanel {
        private JTextField sqlScriptLocationField;

    	EmbeddedDbConnectionPanel() {
    		super();
    	    sqlScriptLocationField = new JTextField();
    	    JLabel sqlFileLabel = new JLabel("SQL File Location:");
    	    JButton fileButton = new JButton("Browse...");
    	    fileButton.addActionListener(new ActionListener() {
    	        public void actionPerformed(ActionEvent e) {
    	            getFileURL(sqlScriptLocationField, "sql", "SQL Scripts (*.sql)");
    	        }
    	    });
    	    fileButton.setMnemonic('f');
    	    
    	    this.setLayout(new GridBagLayout());
    	    this.add(sqlFileLabel,new GridBagConstraints(
    	            0,0,2,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 5),
    	            2,2));

    	    this.add(sqlScriptLocationField,new GridBagConstraints(
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
    	
        boolean executeStep() {
            DatabaseInfo embedInfo = DatabaseInfo.getEmbeddedDatabaseInfo();
            databaseInfo.setUrl(embedInfo.getURL());
            databaseInfo.setUserName(embedInfo.getUserName());
            databaseInfo.setPassword(embedInfo.getPassword());
            databaseInfo.setDriverClass(embedInfo.getDriverClass());
            try {
                databaseInfo.setEmbeddedSQLLocation(new URL("file:\\" + sqlScriptLocationField.getText()));
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
            return true;
        }
    }

    class JdbcConnectionPanel extends ConnectionPanel {
        private JTextField jdbcUrlField;
        private JTextField jdbcUserField;
        private JTextField jdbcPasswordField;
        private JTextField jdbcDriverField;

    	JdbcConnectionPanel() {
            super();
    	    jdbcUrlField = new JTextField();
    	    jdbcUserField = new JTextField();
    	    jdbcPasswordField = new JTextField();
    	    jdbcDriverField = new JTextField();
    	    this.setLayout(new GridBagLayout());

    	    this.add(new JLabel("URL: "),new GridBagConstraints(
    	            0,0,1,1,1,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(jdbcUrlField,new GridBagConstraints(
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

    	    this.add(jdbcDriverField,new GridBagConstraints(
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

    	    this.add(jdbcUserField,new GridBagConstraints(
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

    	    this.add(jdbcPasswordField,new GridBagConstraints(
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

        boolean executeStep() {
			databaseInfo.setUrl(jdbcUrlField.getText());
            databaseInfo.setUserName(jdbcUserField.getText());
            databaseInfo.setPassword(jdbcPasswordField.getText());
            databaseInfo.setDriverClass(jdbcDriverField.getText());
            databaseInfo.setEmbeddedSQLLocation((String) null);
            return connectDatabase();
        }
    }

    class OdbcConnectionPanel extends ConnectionPanel {
        private JTextField odbcDataSourceNameField;
        private JTextField odbcUserField;
        private JTextField odbcPasswordField;

    	OdbcConnectionPanel() {
            super();
    	    odbcDataSourceNameField = new JTextField();
    	    odbcUserField = new JTextField();
    	    odbcPasswordField = new JTextField();
    	    this.setLayout(new GridBagLayout());

    	    this.add(new JLabel("Data Name Source: "),new GridBagConstraints(
    	            0,0,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.HORIZONTAL,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(odbcDataSourceNameField,new GridBagConstraints(
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

    	    this.add(odbcUserField,new GridBagConstraints(
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

    	    this.add(odbcPasswordField,new GridBagConstraints(
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
        boolean executeStep() {
            databaseInfo.setDriverClass(JDBC_ODBC_BRIDGE_DRIVER);
            databaseInfo.setUrl(ODBC_PREFIX + odbcDataSourceNameField.getText());
            databaseInfo.setUserName(odbcUserField.getText());
            databaseInfo.setPassword(odbcPasswordField.getText());
            databaseInfo.setEmbeddedSQLLocation((String) null);
            return connectDatabase();
        }
    }

    class AccessFileConnectionPanel extends ConnectionPanel {
        private JTextField accessUrlField;
        private JTextField accessUserField;
        private JTextField accessPasswordField;

    	AccessFileConnectionPanel() {
            super();
    	    accessUrlField = new JTextField();
    	    accessUserField = new JTextField();
    	    accessPasswordField = new JTextField();
    	    this.setLayout(new GridBagLayout());
    	    
    	    JButton fileButton = new JButton("Browse...");
    	    fileButton.addActionListener(new ActionListener() {
    	        public void actionPerformed(ActionEvent e) {
    	            getFileURL(accessUrlField, "mdb", "Microsoft Access Databases (*.mdb)");
    	        }
    	    });
    	    fileButton.setMnemonic('f');

    	    this.add(new JLabel("File Access Location: "),new GridBagConstraints(
    	            0,0,1,1,0,0,
    	            GridBagConstraints.NORTHWEST,
    	            GridBagConstraints.NONE,
    	            new Insets(5, 5, 5, 0),
    	            2,2));

    	    this.add(accessUrlField,new GridBagConstraints(
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

    	    this.add(accessUserField,new GridBagConstraints(
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

    	    this.add(accessPasswordField,new GridBagConstraints(
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
        boolean executeStep() {
            databaseInfo.setDriverClass(JDBC_ODBC_BRIDGE_DRIVER);
            databaseInfo.setUrl(createAccessFileURL(accessUrlField.getText()));
            databaseInfo.setUserName(accessUserField.getText());
            databaseInfo.setPassword(accessPasswordField.getText());
            databaseInfo.setEmbeddedSQLLocation((String) null);
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
            String sqlTableName = this.tableView.getSqlTableName();
            String sqlKeyName = this.tableView.getSqlKeyName();
            if(sqlTableName == null || sqlKeyName == null) {
        		JOptionPane.showMessageDialog(this, "Please select a table/column pair as primary key", "No key selected", 
        									  JOptionPane.ERROR_MESSAGE);
        		return false;
        	}
        	databaseInfo.setTableName(sqlTableName);
        	databaseInfo.setKey(sqlKeyName);
        	return true;
        }
        WizardPanel getNextPanel() {
            return null;
        }
    }

	/**
	 * Construct an instance of this view
	 */
	public DatabaseConnectionInformationView(JFrame frame,
												ConceptualSchema conceptualSchema,
												EventBroker eventBroker) {
		super(frame, "Database connection");
		this.conceptualSchema = conceptualSchema;
		this.internalBroker = new EventBroker();
		this.databaseInfo = new DatabaseInfo();
		this.connection = new DatabaseConnection(internalBroker);
        this.databaseSchema = new DatabaseSchema(internalBroker);
		
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
   		return this.currentStep.executeStep();
    }

    protected void gotoNextStep() {
		WizardPanel nextPanel = this.currentStep.getNextPanel();
        if(nextPanel == null) {
            hide();
            this.conceptualSchema.setDatabaseInfo(this.databaseInfo);
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
        contentPane.add(panel,new GridBagConstraints(
                0,1,1,1,1,1,
                GridBagConstraints.CENTER,
                GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5),
                2,2));
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

	private String createAccessFileURL(String fileLocation) {
		return ACCESS_FILE_URL_PREFIX + fileLocation + ACCESS_FILE_URL_END;
	}

//	public void copyToControls(DatabaseInfo info) {
//		if( info == null || info.getURL() == null ) {
//		    this.sqlScriptLocationField.setText("");
//		    raisePanel(EMBEDDED_DBMS_ID_STRING);
//		    return;
//		}
//		if(info.getURL().equals(DatabaseInfo.getEmbeddedDatabaseInfo().getURL())) {
//			copyEmbeddedDataToControls(info);
//			raisePanel(EMBEDDED_DBMS_ID_STRING);
//		} else if(info.getDriverClass().equals(JDBC_ODBC_BRIDGE_DRIVER)) {
//			if(info.getURL().indexOf(';') == -1){ // a semicolon is not allowed in DSN names
//				copyODBCDataToControls(info);
//				raisePanel(ODBC_ID_STRING);
//			} 
//			else { // but always in the access file URLs
//				copyAccessDataToControls(info);
//				raisePanel(ACCESS_FILE_ID_STRING);
//			}
//		} 
//		else {
//			copyJDBCDataToControls(info);
//			raisePanel(JDBC_ID_STRING);
//		}
//	}
//
//	private void copyAccessDataToControls(DatabaseInfo info) {
//		int start = ACCESS_FILE_URL_PREFIX.length();
//		int end = info.getURL().length() - ACCESS_FILE_URL_END.length();
//		this.accessUrlField.setText(info.getURL().substring(start, end));
//	}
//
//	private void copyODBCDataToControls(DatabaseInfo info) {
//		int start = ODBC_PREFIX.length();
//		this.odbcDataSourceNameField.setText(info.getURL().substring(start));
//		this.odbcPasswordField.setText(info.getPassword());
//		this.odbcUserField.setText(info.getUserName());
//	}
//
	public void processEvent(Event event) {
		ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        this.conceptualSchema = changeEvent.getConceptualSchema();
        DatabaseInfo databaseInfo = this.conceptualSchema.getDatabaseInfo();
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
}
