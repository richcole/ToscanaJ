/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.AttributeSelectionDialog;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.lattice.LatticeImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.cernato.NDimDiagramEditingView;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/// @todo check if the file we save to exists, warn if it does

public class LuccaMainPanel extends JFrame implements MainPanel, EventBrokerListener {
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
    private JToolBar toolBar;

    private List mruList = new LinkedList();
    private String currentFile = null;

    /**
     * Views
     */
    private DiagramEditingView diagramView;
    private DatabaseConnectionInformationView connectionInformationView;
    private XMLEditorDialog schemaDescriptionView;
    private AttributeSelectionDialog columnChooserDialog;
    private static final DimensionCreationStrategy DimensionStrategy = new DefaultDimensionStrategy();

    public LuccaMainPanel() {
        super("Lucca");

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);
        databaseConnection = new DatabaseConnection(eventBroker);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DatabaseInfoChangedEvent.class, Object.class);

        createViews();

        mruList = ConfigurationManager.fetchStringList("LuccaMainPanel", "mruFiles", MaxMruFiles);
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile = new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }

        createMenuBar();
        createToolBar();

        createLayout();

        ConfigurationManager.restorePlacement("LuccaMainPanel", this,
                new Rectangle(100, 100, 500, 400));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    private void createLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(diagramView, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private void createToolBar() {
        toolBar = new JToolBar();
        JButton newDiagramButton = new JButton("New Diagram");
        newDiagramButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewDiagram();
            }
        });
        toolBar.add(newDiagramButton);

        JButton insertAttributeButton = new JButton("Insert Attribute");
        insertAttributeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                insertAttribute();
            }
        });
        toolBar.add(insertAttributeButton);
    }

    private void insertAttribute() {
    	columnChooserDialog.show();
    }

    private void createNewDiagram() {
        String result = JOptionPane.showInputDialog(this, "Please enter a name for the new diagram.",
                                                    "Enter name", JOptionPane.OK_CANCEL_OPTION);
        if(result != null) {
            ConceptImplementation concept = new ConceptImplementation();
            LatticeImplementation lattice = new LatticeImplementation();
            lattice.addConcept(concept);
            addDiagram(lattice, result);
        }
    }

    private void addDiagram(Lattice lattice, String name) {
        Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, name, DimensionStrategy);
        conceptualSchema.addDiagram(diagram);
    }

    public void createViews() {
        diagramView = new NDimDiagramEditingView(conceptualSchema, eventBroker);
        diagramView.setDividerLocation(ConfigurationManager.fetchInt("LuccaMainPanel", "diagramViewDivider", 200));
        connectionInformationView =
                new DatabaseConnectionInformationView(this, conceptualSchema.getDatabaseInfo(), eventBroker);

        schemaDescriptionView = new XMLEditorDialog(this, "Schema description");
        columnChooserDialog = new AttributeSelectionDialog(this, "Choose Column", this.databaseConnection, eventBroker);
    }


    public void createMenuBar() {

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
//        saveActivity.setPrepareActivity(new PrepareToSaveActivity());
        fileMenu.add(saveMenuItem);

        JMenuItem dbConnectionMenuItem = new JMenuItem("Database connection...");
        dbConnectionMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connectionInformationView.show();
            }
        });
        fileMenu.add(dbConnectionMenuItem);

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

        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        JMenuItem editSchemaDescriptionMenuItem = new JMenuItem("Schema Description");
        editSchemaDescriptionMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                schemaDescriptionView.setContent(conceptualSchema.getDescription());
                schemaDescriptionView.show();
                conceptualSchema.setDescription(schemaDescriptionView.getContent());
            }
        });
        editMenu.add(editSchemaDescriptionMenuItem);

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
        ConfigurationManager.storePlacement("LuccaMainPanel", this);
        ConfigurationManager.storeStringList("LuccaMainPanel", "mruFiles", this.mruList);
        ConfigurationManager.storeInt("LuccaMainPanel", "diagramViewDivider",
                diagramView.getDividerLocation()
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
        if (e instanceof ConceptualSchemaLoadedEvent) {
            ConceptualSchemaLoadedEvent loadEvent = (ConceptualSchemaLoadedEvent) e;
            File schemaFile = loadEvent.getFile();
            addFileToMRUList(schemaFile);
            schemaDescriptionView.setContent(conceptualSchema.getDescription());
        }
        if (e instanceof DatabaseInfoChangedEvent) {
            DatabaseInfo databaseInformation = conceptualSchema.getDatabaseInfo();
            if (databaseInformation.getDriverClass() != null && databaseInformation.getURL() != null) {
                if (databaseConnection.isConnected()) {
                    try {
                        databaseConnection.disconnect();
                    } catch (DatabaseException ex) {
                        ErrorDialog.showError(this, ex, "Closing database error",
                                "Some error closing the old database:\n" + ex.getMessage());
                        return;
                    }
                }
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
}
