/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.database;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class DatabaseConnectionInformationView
	extends JDialog
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
	private static final int MINIMUM_HEIGHT = 500;
	
	protected ConceptualSchema conceptualSchema;

	private File openedDatabaseFile = null;

	private JTextField jdbcUrlField;
	private JTextField jdbcUserField;
	private JTextField jdbcPasswordField;
	private JTextField jdbcDriverField;

	private JTextField odbcDataSourceNameField;
	private JTextField odbcUserField;
	private JTextField odbcPasswordField;

	private JTextField accessUrlField;
	private JTextField accessUserField;
	private JTextField accessPasswordField;
	private JTextField sqlScriptLocationField;
	private String activePanel = "";

	private JPanel inputPanel;
	private JRadioButton embDBMSRadioButton;
	private JRadioButton jdbcRadioButton;
	private JRadioButton accessRadioButton;
	private JRadioButton odbcRadioButton;
	
	class SaveControlActivity implements SimpleActivity {
		public boolean doActivity() throws Exception {
			copyFromControls();
			return true;
		}
	}

	/**
	 * Construct an instance of this view
	 */
	public DatabaseConnectionInformationView(
											JFrame frame,
											ConceptualSchema conceptualSchema,
											final EventBroker eventBroker) {
		super(frame, "Database connection");
		this.conceptualSchema = conceptualSchema;
		
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
		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel connection = createConnectionPanel();
		JPanel tableView = new DatabaseSchemaView(eventBroker);
		tabbedPane.addTab(
			"Step 1: Select Connection",
			null,
			connection,
			"Database Connections");
		tabbedPane.addTab("Step 2: Select Key", null, tableView, "Tables View");
		
		JButton connectButton = new JButton();
		SimpleAction action = new SimpleAction(frame, "Connect");
		action.add(new SaveControlActivity());
		connectButton.setAction(action);

		JButton closeButton = new JButton("Close Dialog");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hide();
			}
		});

		JPanel buttonPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		buttonPane.add(connectButton);
		buttonPane.add(closeButton);
		
		contentPane.add(tabbedPane,new GridBagConstraints(
				0,0,1,1,1,1,
				GridBagConstraints.CENTER,
				GridBagConstraints.BOTH,
				new Insets(0, 5, 5, 5),
				2,2));
		contentPane.add(buttonPane, new GridBagConstraints(
				0,1,1,1,1,0,
				GridBagConstraints.EAST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 5, 5),
				2,2));
				
		ConfigurationManager.restorePlacement(
			"DatabaseConnectionInformationView",
			this,
			new Rectangle(100, 100, 300, 600));

	    eventBroker.subscribe(
	        this,
	        DatabaseInfoChangedEvent.class,
	        Object.class);
	}

	public JPanel createConnectionPanel() {
		JPanel connection = new JPanel(new GridBagLayout());
		embDBMSRadioButton = new JRadioButton(EMBEDDED_DBMS_ID_STRING);
		jdbcRadioButton = new JRadioButton(JDBC_ID_STRING);
		accessRadioButton = new JRadioButton(ACCESS_FILE_ID_STRING);
		odbcRadioButton = new JRadioButton(ODBC_ID_STRING);

		inputPanel = new JPanel();
		
		inputPanel.setLayout(new CardLayout());
		inputPanel.add("blankPanel", new JPanel());
		inputPanel.add(EMBEDDED_DBMS_ID_STRING, createSQLPane());
		inputPanel.add(JDBC_ID_STRING, createJDBCPane());
		inputPanel.add(ODBC_ID_STRING, createODBCPane());
		inputPanel.add(ACCESS_FILE_ID_STRING, createAccessPane());
		
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(embDBMSRadioButton);
		buttonGroup.add(jdbcRadioButton);
		buttonGroup.add(accessRadioButton);
		buttonGroup.add(odbcRadioButton);
		
		connection.add(inputPanel,new GridBagConstraints(
				1,0,1,5,1,1,
				GridBagConstraints.NORTH,
				GridBagConstraints.BOTH,
				new Insets(5, 0, 5, 5),
				2,2));

		connection.add(embDBMSRadioButton,new GridBagConstraints(
				0,0,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0),
				2,2));
				
		connection.add(jdbcRadioButton,new GridBagConstraints(
				0,1,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0),
				2,2));
				
		connection.add(odbcRadioButton,new GridBagConstraints(
				0,2,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0),
				2,2));

		connection.add(accessRadioButton,new GridBagConstraints(
				0,3,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0),
				2,2));
				
		connection.add(new JPanel(),new GridBagConstraints(
				0,4,1,1,0,1,
				GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL,
				new Insets(0, 0, 0, 0),
				1,1));
				
		embDBMSRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				raisePanel(EMBEDDED_DBMS_ID_STRING);
			}
		});

		jdbcRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				raisePanel(JDBC_ID_STRING);
			}
		});

		odbcRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				raisePanel(ODBC_ID_STRING);
			}
		});

		accessRadioButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				raisePanel(ACCESS_FILE_ID_STRING);
			}
		});
		return connection;
	}

	protected void raisePanel(String panelId) {
		TitledBorder border = BorderFactory.createTitledBorder(panelId);
		inputPanel.setBorder(border);
		CardLayout cl = (CardLayout) (inputPanel.getLayout());
		cl.show(inputPanel, panelId);
		activePanel = panelId;
	}

	protected JPanel createSQLPane() {
		sqlScriptLocationField = new JTextField();
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JPanel panel = new JPanel(new GridBagLayout());
		JPanel mainPanel = new JPanel(new GridBagLayout());
		JLabel sqlFile = new JLabel("SQL File:");
		JButton fileButton = new JButton("File...");
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getFileURL(sqlScriptLocationField, "sql", "SQL Scripts (*.sql)");
			}
		});
		fileButton.setMnemonic('f');
		fileButton.setSize(100, 100);
		panel.add(sqlFile,new GridBagConstraints(
				0,0,1,1,1.0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		panel.add(sqlScriptLocationField,new GridBagConstraints(
				0,1,1,1,1.0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets( 5, 2, 0, 5),
				2,2));

		buttonPanel.add(fileButton);

		mainPanel.add(panel,new GridBagConstraints(
				0,0,1,1,1.0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 5, 0, 0),
				2,2));
				
		mainPanel.add(buttonPanel,new GridBagConstraints(
				0,1,1,1,1.0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 2, 0),
				2,2));
				
		mainPanel.add(new JPanel(),new GridBagConstraints(
				0,2,1,1,0,1,
				GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL,
				new Insets(5, 5, 5, 0),
				2,2));
				
		return mainPanel;
	}

	protected JPanel createJDBCPane() {
		jdbcUrlField = new JTextField();
		jdbcUserField = new JTextField();
		jdbcPasswordField = new JTextField();
		jdbcDriverField = new JTextField();
		JPanel pane = new JPanel(new GridBagLayout());

		pane.add(new JLabel("URL: "),new GridBagConstraints(
				0,0,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(jdbcUrlField,new GridBagConstraints(
				1,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));

		pane.add(new JLabel("Driver: "),new GridBagConstraints(
				0,1,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(jdbcDriverField,new GridBagConstraints(
				1,1,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));

		pane.add(new JLabel("Username: "),new GridBagConstraints(
				0,2,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(jdbcUserField,new GridBagConstraints(
				1,2,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));

		pane.add(new JLabel("Password: "),new GridBagConstraints(
				0,3,1,0,0,
				0,GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(jdbcPasswordField,new GridBagConstraints(
				1,3,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));
				
		pane.add(new JPanel(),new GridBagConstraints(
				0,4,1,2,0,1,
				GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL,
				new Insets(5, 5, 5, 0),
				2,2));

		return pane;
	}

	protected JPanel createODBCPane() {
		odbcDataSourceNameField = new JTextField();
		odbcUserField = new JTextField();
		odbcPasswordField = new JTextField();
		JPanel pane = new JPanel(new GridBagLayout());

		pane.add(new JLabel("Data Name Source: "),new GridBagConstraints(
				0,0,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(odbcDataSourceNameField,new GridBagConstraints(
				1,0,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));

		pane.add(new JLabel("Username: "),new GridBagConstraints(
				0,1,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(odbcUserField,new GridBagConstraints(
				1,1,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));

		pane.add(new JLabel("Password: "),new GridBagConstraints(
				0,2,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(odbcPasswordField,new GridBagConstraints(
				1,2,1,1,1,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5),
				2,2));
		pane.add(new JPanel(),new GridBagConstraints(
				1,3,1,2,1,1,
				GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL,
				new Insets(5, 5, 5, 0),
				2,2));
		

		return pane;

	}

	protected JPanel createAccessPane() {

		accessUrlField = new JTextField();
		accessUserField = new JTextField();
		accessPasswordField = new JTextField();
		JPanel pane = new JPanel(new GridBagLayout());
		JButton fileButton = new JButton("File...");
		fileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getFileURL(accessUrlField, "mdb", "Microsoft Access Databases (*.mdb)");
			}
		});
		fileButton.setMnemonic('f');

		pane.add(new JLabel("File Access Location: "),new GridBagConstraints(
				0,0,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 2, 0),
				2,2));

		pane.add(accessUrlField,new GridBagConstraints(
				1,0,1,1,10,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 2, 0),
				2,2));

		pane.add(fileButton,new GridBagConstraints(
				2,0,1,1,0,0,
				GridBagConstraints.CENTER,
				GridBagConstraints.NONE,
				new Insets(5, 5, 2, 0),
				2,2));
		pane.add(new JLabel("Username: "),new GridBagConstraints(
				0,1,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(2, 5, 5, 0),
				2,2));

		pane.add(accessUserField,new GridBagConstraints(
				1,1,1,1,10,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(new JLabel("Password: "),new GridBagConstraints(
				0,2,1,1,0,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE,
				new Insets(5, 5, 5, 0),
				2,2));

		pane.add(accessPasswordField,new GridBagConstraints(
				1,2,1,1,10,0,
				GridBagConstraints.NORTHWEST,
				GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 0),
				2,2));
				
		pane.add(new JPanel(),new GridBagConstraints(
				2,0,1,3,1,1,
				GridBagConstraints.WEST,
				GridBagConstraints.VERTICAL,
				new Insets(5, 5, 5, 0),
				2,2));
		return pane;
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

	public boolean areControlsChanged() {
		DatabaseInfo info = conceptualSchema.getDatabaseInfo();
		if (activePanel.equals(JDBC_ID_STRING)) {
			if (!jdbcUrlField.getText().equals(info.getURL())) {
				return true;
			}
			else if (!jdbcUserField.getText().equals(info.getUserName())) {
				return true;
			}
			else if (!jdbcPasswordField.getText().equals(info.getPassword())) {
				return true;
			}
			else if (!jdbcDriverField.getText().equals(info.getDriverClass())) {
				return true;
			}
			return false;
		} 
		
		if (activePanel.equals(ODBC_ID_STRING)) {
			// use JDBC URL for ODBC DSN ("jdbc:odbc:DSN")
			String odbcDataSourceName = ODBC_PREFIX + odbcDataSourceNameField.getText();
			if (!odbcDataSourceName.equals(info.getURL())) {
				return true;
			}
			else if (!info.getDriverClass().equals(JDBC_ODBC_BRIDGE_DRIVER)) {
				return true;
			}
			else if (!odbcUserField.getText().equals(info.getUserName())) {
				return true;
			}
			else if (!odbcPasswordField.getText().equals(info.getPassword())) {
				return true;
			}
			return false;
		} 
		
		if (activePanel.equals(EMBEDDED_DBMS_ID_STRING)) {
			if (!sqlScriptLocationField.getText().equals(info.getEmbeddedSQLLocation())) {
				return true;
			}
			DatabaseInfo embedInfo = DatabaseInfo.getEmbeddedDatabaseInfo();
			
			if (!info.getDriverClass().equals(embedInfo.getDriverClass())){
				
				return true;
			}
			
			else if (!info.getPassword().equals(embedInfo.getPassword())){
				return true;
			}
			
			else if (!info.getUserName().equals( embedInfo.getUserName())){
				return true;
			}
			
			else if(!info.getURL().equals(embedInfo.getURL())){
				return true;
			}
			
			return false;
		} 
		
		if (activePanel.equals(ACCESS_FILE_ID_STRING)) {
			if (!createAccessFileURL(accessUrlField.getText()).equals(info.getURL())) {
				return true;
			}
			else if (!info.getDriverClass().equals(JDBC_ODBC_BRIDGE_DRIVER)) {
				return true;
			}
			else if (!info.getPassword().equals(accessPasswordField.getText())){
				return true;
			}
			else if (!info.getUserName().equals(accessUserField.getText())){
				return true;
			}
			return false;
		}

		return false;
	}

	public void copyFromControls() throws MalformedURLException {
		DatabaseInfo info = new DatabaseInfo();
		if (activePanel.equals(JDBC_ID_STRING)) {
			info.setUrl(jdbcUrlField.getText());
			info.setUserName(jdbcUserField.getText());
			info.setPassword(jdbcPasswordField.getText());
			info.setDriverClass(jdbcDriverField.getText());
			info.setEmbeddedSQLLocation((String) null);
		} 

		else if (activePanel.equals(EMBEDDED_DBMS_ID_STRING)) {
			DatabaseInfo embedInfo = DatabaseInfo.getEmbeddedDatabaseInfo();
			info.setUrl(embedInfo.getURL());
			info.setUserName(embedInfo.getUserName());
			info.setPassword(embedInfo.getPassword());
			info.setDriverClass(embedInfo.getDriverClass());
            info.setEmbeddedSQLLocation(new URL("file:\\" + sqlScriptLocationField.getText()));
		} 

		else if (activePanel.equals(ACCESS_FILE_ID_STRING)) {
			info.setDriverClass(JDBC_ODBC_BRIDGE_DRIVER);
			info.setUrl(createAccessFileURL(accessUrlField.getText()));
			info.setUserName(accessUserField.getText());
			info.setPassword(accessPasswordField.getText());
			info.setEmbeddedSQLLocation((String) null);
		} 

		else if (activePanel.equals(ODBC_ID_STRING)) {
			info.setDriverClass(JDBC_ODBC_BRIDGE_DRIVER);
			info.setUrl(ODBC_PREFIX + odbcDataSourceNameField.getText());
			info.setUserName(odbcUserField.getText());
			info.setPassword(odbcPasswordField.getText());
			info.setEmbeddedSQLLocation((String) null);
		}
		this.conceptualSchema.setDatabaseInfo(info);
	}

	public void copyToControls(DatabaseInfo info) {
		if( info == null || info.getURL() == null ) {
		    this.sqlScriptLocationField.setText("");
		    raisePanel(EMBEDDED_DBMS_ID_STRING);
		    return;
		}
		if(info.getURL().equals(DatabaseInfo.getEmbeddedDatabaseInfo().getURL())) {
			copyEmbeddedDataToControls(info);
			this.embDBMSRadioButton.setSelected(true);
			raisePanel(EMBEDDED_DBMS_ID_STRING);
		} else if(info.getDriverClass().equals(JDBC_ODBC_BRIDGE_DRIVER)) {
			if(info.getURL().indexOf(';') == -1){ // a semicolon is not allowed in DSN names
				copyODBCDataToControls(info);
				this.odbcRadioButton.setSelected(true);
				raisePanel(ODBC_ID_STRING);
			} 
			else { // but always in the access file URLs
				copyAccessDataToControls(info);
				this.accessRadioButton.setSelected(true);
				raisePanel(ACCESS_FILE_ID_STRING);
			}
		} 
		else {
			copyJDBCDataToControls(info);
			this.jdbcRadioButton.setSelected(true);
			raisePanel(JDBC_ID_STRING);
		}
	}

	private void copyJDBCDataToControls(DatabaseInfo info) {
		this.jdbcUrlField.setText(info.getURL());
		this.jdbcDriverField.setText(info.getDriverClass());
		this.jdbcUserField.setText(info.getUserName());
		this.jdbcPasswordField.setText(info.getPassword());
	}

	private void copyAccessDataToControls(DatabaseInfo info) {
		int start = ACCESS_FILE_URL_PREFIX.length();
		int end = info.getURL().length() - ACCESS_FILE_URL_PREFIX.length();
		this.accessUrlField.setText(info.getURL().substring(start, end));
	}

	private void copyODBCDataToControls(DatabaseInfo info) {
		int start = ODBC_PREFIX.length();
		this.odbcDataSourceNameField.setText(info.getURL().substring(start));
		this.odbcPasswordField.setText(info.getPassword());
		this.odbcUserField.setText(info.getUserName());
	}

	private void copyEmbeddedDataToControls(DatabaseInfo info) {
		this.sqlScriptLocationField.setText(info.getEmbeddedSQLLocation().getPath());
	}

	public void processEvent(Event event) {
		ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        ConceptualSchema conceptualSchema = changeEvent.getConceptualSchema();
        DatabaseInfo databaseInfo = conceptualSchema.getDatabaseInfo();
        copyToControls(databaseInfo);
	}
	
	public void hide() {
		super.hide();
		ConfigurationManager.storePlacement(
			"DatabaseConnectionInformationView",
			this);
	}

}
