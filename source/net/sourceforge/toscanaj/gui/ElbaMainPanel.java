/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.DataDump;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.CheckDuplicateFileChooser;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExportStatisticalDataSettingsDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.parser.CSCParser;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.SqlClauseLabelView;
import net.sourceforge.toscanaj.view.scales.ScaleEditingViewDialog;

import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ElbaMainPanel extends JFrame implements MainPanel, EventBrokerListener {
    private static final String CONFIGURATION_SECTION_NAME = "ElbaMainPanel";
    static private final int MaxMruFiles = 8;
    
    /**
     *  Main Controllers
     */
    private EventBroker eventBroker;
    private DatabaseConnection databaseConnection;

    /**
     *  Model
     */
    private ConceptualSchema conceptualSchema;

    /**
     * Controls
     */
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu mruMenu;
    private JMenu editMenu;
    private JMenu helpMenu;

    private List mruList = new LinkedList();
    private String currentFile = null;

    /**
     * Views
     */
    private ScaleEditingViewDialog scaleEditingViewDialog;
    private DiagramEditingView diagramEditingView;
    private DatabaseConnectionInformationView connectionInformationView;
    private XMLEditorDialog schemaDescriptionView;
    private JMenuItem dumpSQLMenuItem;
    private JMenuItem dumpStatisticalDataMenuItem;

    public ElbaMainPanel() {
        super("Elba");

        this.eventBroker = new EventBroker();
        this.conceptualSchema = new ConceptualSchema(eventBroker);
        this.databaseConnection = new DatabaseConnection(eventBroker);
        DatabaseConnection.setConnection(this.databaseConnection);

        this.eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class);
        this.eventBroker.subscribe(this, DatabaseInfoChangedEvent.class, Object.class);

        createViews();
        // this has to happen before the menu gets created, since the menu uses the information
        DiagramView diagramView = this.diagramEditingView.getDiagramView();
        float minLabelFontSize = ConfigurationManager.fetchFloat(CONFIGURATION_SECTION_NAME, "minLabelFontSize",
                                                               (float)diagramView.getMinimumFontSize());
        diagramView.setMinimumFontSize(minLabelFontSize);

        createMenuBar();
		                                                    
        mruList = ConfigurationManager.fetchStringList(CONFIGURATION_SECTION_NAME, "mruFiles", MaxMruFiles);
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile = new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }

        ConfigurationManager.restorePlacement(CONFIGURATION_SECTION_NAME, this,
                new Rectangle(100, 100, 500, 400));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    public void createViews() {
		final JFrame frame = this;
		JPanel mainView = new JPanel(new GridBagLayout());
		scaleEditingViewDialog= new ScaleEditingViewDialog(frame, conceptualSchema , eventBroker,
						databaseConnection );
		connectionInformationView =
						new DatabaseConnectionInformationView(this, conceptualSchema, eventBroker);
		schemaDescriptionView = new XMLEditorDialog(this, "Schema description");
		JPanel buttonPane = new JPanel(new GridBagLayout());
		JButton newDiagramButton = new JButton("New Diagram...");
		newDiagramButton.addActionListener(new ActionListener (){
			public void actionPerformed(ActionEvent e){
				scaleEditingViewDialog.show();
			}
		});
		
		JButton schemaDescriptionButton = new JButton("Schema Description");
		schemaDescriptionButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				schemaDescriptionView.setContent(conceptualSchema.getDescription());
				schemaDescriptionView.show();
				conceptualSchema.setDescription(schemaDescriptionView.getContent());
			}
		}		);
		
		JButton databaseConnectionButton = new JButton("Database Connection");
		databaseConnectionButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				connectionInformationView.show();
			}
		});
		
		buttonPane.add(newDiagramButton, new GridBagConstraints(
						0,0,1,1,0,0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(5,1,1,1),
						2,2
		));
		
		buttonPane.add(schemaDescriptionButton, new GridBagConstraints(
						1,0,1,1,0,0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(5,1,1,1),
						2,2
		));
		
		buttonPane.add(databaseConnectionButton, new GridBagConstraints(
						2,0,1,1,0,0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.NONE,
						new Insets(5,1,1,1),
						2,2
		));
		buttonPane.add(new JPanel(), new GridBagConstraints(
						3,0,1,1,1,1,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(1,1,1,1),
						2,2
			));
		
		
        
      
        
        diagramEditingView = new DiagramEditingView(conceptualSchema, eventBroker);
        diagramEditingView.setDividerLocation(ConfigurationManager.fetchInt(CONFIGURATION_SECTION_NAME, "diagramViewDivider", 200));
        diagramEditingView.getDiagramView().setObjectLabelFactory(SqlClauseLabelView.getFactory());
		
		
		mainView.add(buttonPane, new GridBagConstraints(
						0,0,1,1,1.0,0,
						GridBagConstraints.NORTHWEST,
						GridBagConstraints.HORIZONTAL,
						new Insets(2,2,2,2),
						2,2)
		);
		mainView.add(diagramEditingView, new GridBagConstraints(
						0,1,1,1,1,1,
						GridBagConstraints.WEST,
						GridBagConstraints.BOTH,
						new Insets(2,2,2,2),
						2,2)
		);
        setContentPane(mainView);
    }


    public void createMenuBar() {
    	final DiagramView diagramView = diagramEditingView.getDiagramView();

        // --- menu bar ---
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // --- file menu ---
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        SimpleAction newAction = new SimpleAction(
                this,
                new NewConceptualSchemaActivity(eventBroker),
                "New",
                KeyEvent.VK_N,
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_N,
                        ActionEvent.CTRL_MASK
                )
        );

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(newAction);
        fileMenu.add(newMenuItem);

        OpenFileAction openFileAction = new OpenFileAction(
                this,
                new LoadConceptualSchemaActivity(eventBroker, databaseConnection),
                currentFile,
                KeyEvent.VK_O,
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_O,
                        ActionEvent.CTRL_MASK
                )
        );

        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(openFileAction);
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save...");
        SaveConceptualSchemaActivity saveActivity =
                new SaveConceptualSchemaActivity(conceptualSchema, eventBroker);
        saveMenuItem.addActionListener(
                new SaveFileAction(
                        this,
                        saveActivity,
                        KeyEvent.VK_S,
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_S,
                                ActionEvent.CTRL_MASK
                        )
                )
        );
        fileMenu.add(saveMenuItem);

        JMenuItem importCSCMenuItem = new JMenuItem("Import CSC File...");
        importCSCMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                importCSC();
            }
        });
        fileMenu.add(importCSCMenuItem);

        mruMenu = new JMenu("Reopen");
        recreateMruMenu();
        fileMenu.add(mruMenu);

        fileMenu.addSeparator();

        // --- file exit item ---
        JMenuItem exitMenuItem;
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(
                new SimpleAction(
                        this,
                        new CloseMainPanelActivity(this),
                        "Exit", KeyEvent.VK_X,
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_F4, ActionEvent.ALT_MASK
                        )
                )
        );
        fileMenu.add(exitMenuItem);
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        ButtonGroup fontSizeGroup = new ButtonGroup();
        JMenu setMinLabelSizeSubMenu = new JMenu("Set minimum label size");
        JMenuItem fontRangeMenuItem = new JRadioButtonMenuItem("None");
        fontSizeGroup.add(fontRangeMenuItem);
        fontRangeMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                JMenuItem source = (JMenuItem) e.getSource();
                diagramView.setMinimumFontSize(0);
                source.setSelected(true);
            }
        });
        fontRangeMenuItem.setSelected(true);
        setMinLabelSizeSubMenu.add(fontRangeMenuItem);
        int fontRange = 6; //min font size
        while(fontRange<26){
            fontRangeMenuItem = new JRadioButtonMenuItem(fontRange+"");
            fontSizeGroup.add(fontRangeMenuItem);
            fontRangeMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    JMenuItem source = (JMenuItem) e.getSource();
                    int newFontSize = Integer.parseInt(source.getText());
                    diagramView.setMinimumFontSize(newFontSize);
                    source.setSelected(true);
                }
            });
            if(diagramView.getMinimumFontSize() == fontRange){
                fontRangeMenuItem.setSelected(true);
            }
            fontRange+=2;
            setMinLabelSizeSubMenu.add(fontRangeMenuItem);
        }
        viewMenu.add(setMinLabelSizeSubMenu);
        menuBar.add(viewMenu);

        JMenu toolMenu = new JMenu("Tools");
        toolMenu.setMnemonic(KeyEvent.VK_T);
        dumpStatisticalDataMenuItem = new JMenuItem("Export Statistical Data...");
        dumpStatisticalDataMenuItem.setMnemonic(KeyEvent.VK_S);
        dumpStatisticalDataMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                exportStatisticalData();
            }
        });
        toolMenu.add(dumpStatisticalDataMenuItem);
        dumpSQLMenuItem = new JMenuItem("Export Database as SQL...");
        dumpSQLMenuItem.setMnemonic(KeyEvent.VK_D);
        dumpSQLMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                exportSQLScript();
            }
        });
        toolMenu.add(dumpSQLMenuItem);
		menuBar.add(toolMenu);

        // --- help menu ---
        // create a help menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
    }

    private void openSchemaFile(File schemaFile) {
        try {
            conceptualSchema = CSXParser.parse(eventBroker, schemaFile);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file", e.getMessage());
            conceptualSchema = new ConceptualSchema(eventBroker);
        } catch (IOException e) {
            ErrorDialog.showError(this, e, "Could not open file", e.getMessage());
            conceptualSchema = new ConceptualSchema(eventBroker);
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not read file", e.getMessage());
            conceptualSchema = new ConceptualSchema(eventBroker);
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Could not open file", e.getMessage());
            e.printStackTrace();
            conceptualSchema = new ConceptualSchema(eventBroker);
        }
    }

    private void recreateMruMenu() {
        if (mruMenu == null) { // no menu yet
            return;
        }
        this.mruMenu.removeAll();
        boolean empty = true; // will be used to check if we have at least one entry
        if (this.mruList.size() > 0) {
            ListIterator it = mruList.listIterator(mruList.size() - 1);
            while (it.hasPrevious()) {
                String cur = (String) it.previous();
                if (cur.equals(currentFile)) {
                    // don't enlist the current file
                    continue;
                }
                empty = false;
                JMenuItem mruItem = new JMenuItem(cur);
                mruItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        JMenuItem menuItem = (JMenuItem) e.getSource();
                        openSchemaFile(new File(menuItem.getText()));
                    }
                });
                this.mruMenu.add(mruItem);
            }
        }
        // we have now at least one file
        this.mruMenu.setEnabled(!empty);
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public void closeMainPanel() {
        // store current position

        ConfigurationManager.storeFloat(CONFIGURATION_SECTION_NAME, "minLabelFontSize", 
        							    (float)this.diagramEditingView.getDiagramView().getMinimumFontSize());
        ConfigurationManager.storeStringList(CONFIGURATION_SECTION_NAME, "mruFiles", this.mruList);
        ConfigurationManager.storeInt(CONFIGURATION_SECTION_NAME, "diagramViewDivider",
                diagramEditingView.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            // set schema in any change event, sometimes the order of the events is wrong
            /// @todo make sure the events come in the proper order
            ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            conceptualSchema = schemaEvent.getConceptualSchema();
        }
		if (e instanceof NewConceptualSchemaEvent) {
            DatabaseViewerManager.resetRegistry();
        }
        if (e instanceof ConceptualSchemaLoadedEvent) {
            ConceptualSchemaLoadedEvent loadEvent = (ConceptualSchemaLoadedEvent) e;
            File schemaFile = loadEvent.getFile();
            addFileToMRUList(schemaFile);
            if(schemaDescriptionView != null) {
            	schemaDescriptionView.setContent(conceptualSchema.getDescription());
            }
        }
        if (e instanceof DatabaseInfoChangedEvent || e instanceof NewConceptualSchemaEvent ||
            e instanceof ConceptualSchemaLoadedEvent) {
            if (databaseConnection.isConnected()) {
                try {
                    databaseConnection.disconnect();
                } catch (DatabaseException ex) {
                    ErrorDialog.showError(this, ex, "Closing database error",
                            "Some error closing the old database:\n" + ex.getMessage());
                    return;
                }
            }
            DatabaseInfo databaseInformation = conceptualSchema.getDatabaseInfo();
            if( databaseInformation != null &&
            	databaseInformation.getDriverClass() != null && 
                databaseInformation.getURL() != null) {
                try {
                    databaseConnection.connect(databaseInformation);
                    URL location = conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
                    if (location != null) {
                        databaseConnection.executeScript(location);
                    }
                } catch (DatabaseException ex) {
                    ErrorDialog.showError(this, ex, "DB Connection failed",
                            "Can not connect to the database:\n" + ex.getMessage());
                }
            }
        }
    }

    private void addFileToMRUList(File file) {
        try {
            this.currentFile = file.getCanonicalPath();
        } catch (IOException ex) { // could not resolve canonical path
            ex.printStackTrace();
            this.currentFile = file.getAbsolutePath();
            /// @todo what could be done here?
        }
        if (this.mruList.contains(this.currentFile)) {
            // if it is already in, just remove it and add it at the end
            this.mruList.remove(this.currentFile);
        }
        this.mruList.add(this.currentFile);
        if (this.mruList.size() > MaxMruFiles) {
            this.mruList.remove(0);
        }
        recreateMruMenu();
    }

    private void importCSC() {
        final JFileChooser openDialog;
        if (this.currentFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.currentFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setApproveButtonText("Import");
        openDialog.setFileFilter(new ExtensionFileFilter(new String[]{"csc"}, "Conscript Files"));
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
        	importCSC(openDialog.getSelectedFile());
        } catch(Exception e) {
        	ErrorDialog.showError(this, e, "Import failed");
        }
    }

    private void importCSC(File file) {
        // store current file
        try {
            this.currentFile = file.getCanonicalPath();
        } catch (IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = file.getAbsolutePath();
            /// @todo what could be done here?
        }
        try {
            new CSCParser().importCSCFile(file, this.conceptualSchema);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
    }

    private void exportStatisticalData() {
        ExportStatisticalDataSettingsDialog expSettingsDialog = new ExportStatisticalDataSettingsDialog(this);
        expSettingsDialog.show();
        if(!expSettingsDialog.hasPositiveResult()) {
            return;
        }

        final JFileChooser saveDialog;
		String[] extension = {"xml"};
		ExtensionFileFilter fileFilter = new ExtensionFileFilter(extension,"XML Files");
		ExtensionFileFilter[] filterArray = { fileFilter };
        if (this.currentFile != null) {
            // use position of last file for dialog
            saveDialog = new CheckDuplicateFileChooser(new File(this.currentFile), filterArray);
        } else {
            saveDialog = new CheckDuplicateFileChooser(new File(System.getProperty("user.dir")), filterArray);
        }
        saveDialog.setApproveButtonText("Export");
        int rv = saveDialog.showSaveDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        exportStatisticalData(saveDialog.getSelectedFile(),
                                    expSettingsDialog.getFilterClause(),
                                    expSettingsDialog.hasIncludeContingentListsSet(),
                                    expSettingsDialog.hasIncludeIntentExtentListsSet());
    }

    private void exportStatisticalData(File file, String filterClause,
                                        boolean includeContingentLists, boolean includeIntentExtent) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            DataDump.dumpData(this.conceptualSchema, outputStream, filterClause, includeContingentLists, includeIntentExtent);
            outputStream.close();
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Could not export file");
            return;
        }
    }

    private void exportSQLScript() {
        final JFileChooser saveDialog;
		String[] extension = {"sql"};
		ExtensionFileFilter fileFilter = new ExtensionFileFilter(extension,"SQL Scripts");
		ExtensionFileFilter[] filterArray = { fileFilter };
        if (this.currentFile != null) {
            // use position of last file for dialog
            saveDialog = new CheckDuplicateFileChooser(new File(this.currentFile), filterArray);
        } else {
            saveDialog = new CheckDuplicateFileChooser(new File(System.getProperty("user.dir")), filterArray);
        }
        saveDialog.setApproveButtonText("Export");
        int rv = saveDialog.showSaveDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        exportSQLScript(saveDialog.getSelectedFile());
    }

    private void exportSQLScript(File file) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.close();
        } catch (NullPointerException e) {
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(this,"An internal error occured", "Internal error", JOptionPane.ERROR);
            return;
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Could not export file");
            return;
        }
    }
}
