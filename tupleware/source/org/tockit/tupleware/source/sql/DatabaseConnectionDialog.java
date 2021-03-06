/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.source.sql;

import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.DatabaseInfo.Type;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;

import org.tockit.events.EventBroker;
import org.tockit.relations.model.Relation;
import org.tockit.relations.model.RelationImplementation;
import org.tockit.swing.preferences.ExtendedPreferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * @todo this is just copy & paste code from ToscanaJ's DatabaseConnectionInformationView for now.
 */
@SuppressWarnings("serial")
public class DatabaseConnectionDialog extends JDialog {
    private static final ExtendedPreferences preferences = 
        ExtendedPreferences.userNodeForClass(DatabaseConnectionDialog.class);

    private static final String JDBC_ID_STRING = "JDBC";
	private static final String ODBC_ID_STRING = "ODBC Source";
	private static final String ACCESS_FILE_ID_STRING = "Access File";
	
	private static final int MINIMUM_WIDTH = 400;
	private static final int MINIMUM_HEIGHT = 350;
	
	private DatabaseInfo databaseInfo;
	private DatabaseConnection connection;

    private JButton nextButton;
    private JLabel stepLabel;
    private WizardPanel currentStep;
    private File lastFile;

    private DatabaseTypePanel dbTypePanel;
    private JdbcConnectionPanel jdbcDbPanel;
    private OdbcConnectionPanel odbcDbPanel;
    private AccessFileConnectionPanel accessDbPanel;
    private SqlQueryPanel sqlQueryPanel;

    private Relation<Object> tuples;

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
        private JRadioButton jdbcRadioButton;
        private JRadioButton accessRadioButton;
        private JRadioButton odbcRadioButton;
        DatabaseTypePanel() {
            super();
            
            jdbcRadioButton = new JRadioButton(JDBC_ID_STRING);
            accessRadioButton = new JRadioButton(ACCESS_FILE_ID_STRING);
            odbcRadioButton = new JRadioButton(ODBC_ID_STRING);
            
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add(jdbcRadioButton);
            buttonGroup.add(accessRadioButton);
            buttonGroup.add(odbcRadioButton);

            updateContents();

			this.setLayout(new GridBagLayout());
						
            this.add(jdbcRadioButton,new GridBagConstraints(
                    0,0,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0),
                    2,2));
            this.add(odbcRadioButton,new GridBagConstraints(
                    0,1,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0),
                    2,2));
            this.add(accessRadioButton,new GridBagConstraints(
                    0,2,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 0),
                    2,2));
            this.add(new JPanel(),new GridBagConstraints(
                    0,3,1,1,1,1,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0),
                    2,2));
        }
        @Override
		void updateContents() {
        	net.sourceforge.toscanaj.model.database.DatabaseInfo.Type type = databaseInfo.getType();
			if (type == DatabaseInfo.UNDEFINED) {
				jdbcRadioButton.setSelected(true);
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
            if(jdbcRadioButton.isSelected()) {
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
            return sqlQueryPanel;
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

        @Override
		void updateContents() {
            if(databaseInfo != null && databaseInfo.getType() == DatabaseInfo.EMBEDDED) {
            	this.scriptLocationField.setText(databaseInfo.getEmbeddedSQLLocation().getPath());
            } else {
            	this.scriptLocationField.setText("");
            }
        }
    	
        @Override
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

        @Override
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
        @Override
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
        @Override
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
        @Override
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
        @Override
		boolean executeStep() {
            databaseInfo.setAccessFileInfo(fileUrlField.getText(), userNameField.getText(), new String(passwordField.getPassword()));
            return connectDatabase();
        }
    }

    class SqlQueryPanel extends WizardPanel {
        private JTextArea sqlQueryArea;

        SqlQueryPanel() {
            super();
            sqlQueryArea = new JTextArea();
            sqlQueryArea.setBorder(BorderFactory.createLoweredBevelBorder());

            this.setLayout(new GridBagLayout());
            this.add(new JLabel("SQL Query:"),new GridBagConstraints(
                    0,0,1,1,1,0,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.HORIZONTAL,
                    new Insets(5, 5, 5, 5),
                    0,0));

            this.add(sqlQueryArea,new GridBagConstraints(
                    0,1,1,1,1,1,
                    GridBagConstraints.NORTHWEST,
                    GridBagConstraints.BOTH,
                    new Insets( 5, 15, 5, 5),
                    0,0));
        }

        @Override
		void updateContents() {
        	// nothing to do
        }
        
        @Override
		boolean executeStep() {
            try {
				Statement stmt = connection.getJdbcConnection().createStatement();
				ResultSet resultSet = stmt.executeQuery(this.sqlQueryArea.getText());
				ResultSetMetaData metaData = resultSet.getMetaData();
                int numberColumns = metaData.getColumnCount();
                String[] names = new String[numberColumns];
				for (int i = 0; i < numberColumns; i++) {
                    names[i] = metaData.getColumnLabel(i + 1);
                }
				tuples = new RelationImplementation<Object>(names);
				while (resultSet.next()) {
					Object[] tuple = new Object[numberColumns];
					for (int i = 0; i < numberColumns; i++) {
						tuple[i] = resultSet.getObject(i + 1);
					}
					tuples.addTuple(tuple);
				}
				connection.disconnect();
                return true;
			} catch (SQLException se) {
				ErrorDialog.showError(owner, se, "An error occured while querying the database.");
            } catch (DatabaseException e) {
				ErrorDialog.showError(owner, e, "An error occured while closing the database.");
            }
            return false;
        }

        @Override
		String getTitle() {
            return "Enter SQL Query";
        }

        @Override
		String getNextButtonText() {
            return "Query";
        }

        @Override
		WizardPanel getNextPanel() {
            return null;
        }
    }

	/**
	 * Construct an instance of this view
	 */
	public DatabaseConnectionDialog(JFrame parent, File lastFile) {
		super(parent, "Database connection", true);
		
		this.databaseInfo = new DatabaseInfo();
		this.connection = new DatabaseConnection(new EventBroker());
        this.owner = parent;
        this.lastFile = lastFile;
		
        initializePanels();

		Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		
		addComponentListener( new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int width = getWidth();
				int height = getHeight();
				if (width < MINIMUM_WIDTH) width = MINIMUM_WIDTH;
				if (height < MINIMUM_HEIGHT) height = MINIMUM_HEIGHT;
				setSize(width, height);
			}
			@Override
			public void componentShown(ComponentEvent e) {
				componentResized(e);
			}
		});
		
        this.stepLabel = new JLabel();
				
        JPanel buttonPane = createButtonPanel();
		
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
				
		preferences.restoreWindowPlacement(
			this,
			new Rectangle(100, 100, MINIMUM_WIDTH, MINIMUM_WIDTH));
	}

    protected void initializePanels() {
        dbTypePanel = new DatabaseTypePanel();
        this.jdbcDbPanel = new JdbcConnectionPanel();
        this.odbcDbPanel = new OdbcConnectionPanel();
        this.accessDbPanel = new AccessFileConnectionPanel();
        this.sqlQueryPanel = new SqlQueryPanel();
    }

    public JPanel createButtonPanel() {
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
                setVisible(false);
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
            setVisible(false);
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
		if (lastFile != null) {
			openDialog = new JFileChooser(lastFile);
		} else {
			openDialog = new JFileChooser(System.getProperty("user.dir"));
		}
		openDialog.setFileFilter(new FileFilter() {
			@Override
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
			@Override
			public String getDescription() {
				return description;
			}
		});

		if (openDialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			lastFile = openDialog.getSelectedFile();
			String fileURL = lastFile.getAbsolutePath();
			urlField.setText(fileURL);
		}
	}
	

    @Override
	public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(!visible) {
    	    setCurrentPanel(this.dbTypePanel);
    		preferences.storeWindowPlacement(this);
        }
    }

    public Relation<Object> getTuples() {
        return this.tuples;
    }
    
    public File getLastFile() {
    	return this.lastFile;
    }
}
