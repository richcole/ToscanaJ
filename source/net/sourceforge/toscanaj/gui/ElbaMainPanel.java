/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import org.tockit.events.*;
import org.tockit.events.Event;
import org.tockit.events.EventListener;
import net.sourceforge.toscanaj.gui.action.*;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.events.*;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;
import net.sourceforge.toscanaj.view.database.DatabaseSchemaView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.scales.ScaleEditingView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;

/// @todo check if the file we save to exists, warn if it does
public class ElbaMainPanel extends JFrame implements MainPanel, EventListener {
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
    private JMenu helpMenu;
    private JMenu fileMenu;
    private JMenu mruMenu;

    private List mruList = new LinkedList();
    private String currentFile = null;

    /**
     * Views
     */
    private PanelStackView mainView;
    private DatabaseSchemaView databaseSchemaView;
    private ScaleEditingView scaleView;
    private DiagramEditingView diagramView;
    private DatabaseConnectionInformationView connectionInformationView;

    public class PrepareToSaveActivity implements SimpleActivity {

        public boolean doActivity() throws Exception {
            //return prepareToSave();
            return true;
        }
    }

    public ElbaMainPanel() {
        super("Elba");

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);
        databaseConnection = new DatabaseConnection(eventBroker);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DatabaseInfoChangedEvent.class, Object.class);

        createViews();

        mruList = ConfigurationManager.fetchStringList("ElbaMainPanel", "mruFiles", MaxMruFiles);
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile = new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }

        createMenuBar();

        ConfigurationManager.restorePlacement("ElbaMainPanel", this,
                new Rectangle(100, 100, 500, 400));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    public void createViews() {
        mainView = new PanelStackView(this);
        mainView.setDividerLocation(ConfigurationManager.fetchInt("ElbaMainPanel", "mainPanelDivider", 200));

        connectionInformationView =
                new DatabaseConnectionInformationView(this, conceptualSchema.getDatabaseInfo(), eventBroker);

        databaseSchemaView = new DatabaseSchemaView(this, eventBroker);
        databaseSchemaView.setHorizontalDividerLocation(
                ConfigurationManager.fetchInt("ElbaMainPanel", "databaseSchemaViewHorizontalDivider", 200));
        databaseSchemaView.setVerticalDividerLocation(
                ConfigurationManager.fetchInt("ElbaMainPanel", "databaseSchemaViewVerticalDivider", 300));

        scaleView = new ScaleEditingView(this, conceptualSchema, eventBroker, databaseConnection);
        scaleView.setHorizontalDividerLocation(
                ConfigurationManager.fetchInt("ElbaMainPanel", "scaleViewHorizontalDivider", 200));
        scaleView.setVerticalDividerLocation(
                ConfigurationManager.fetchInt("ElbaMainPanel", "scaleViewVerticalDivider", 300));

        diagramView = new DiagramEditingView(this, conceptualSchema, eventBroker);
        diagramView.setDividerLocation(ConfigurationManager.fetchInt("ElbaMainPanel", "diagramViewDivider", 200));

        mainView.addView("Tables", databaseSchemaView);
        mainView.addView("Scales", scaleView);
        mainView.addView("Diagrams", diagramView);
        setContentPane(mainView);
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
        ConfigurationManager.storePlacement("ElbaMainPanel", this);
        ConfigurationManager.storeInt("ElbaMainPanel", "mainPanelDivider",
                mainView.getDividerLocation()
        );
        ConfigurationManager.storeStringList("ElbaMainPanel", "mruFiles", this.mruList);
        ConfigurationManager.storeInt("ElbaMainPanel", "databaseSchemaViewHorizontalDivider",
                databaseSchemaView.getHorizontalDividerLocation()
        );
        ConfigurationManager.storeInt("ElbaMainPanel", "databaseSchemaViewVerticalDivider",
                databaseSchemaView.getVerticalDividerLocation()
        );
        ConfigurationManager.storeInt("ElbaMainPanel", "scaleViewHorizontalDivider",
                scaleView.getHorizontalDividerLocation()
        );
        ConfigurationManager.storeInt("ElbaMainPanel", "scaleViewVerticalDivider",
                scaleView.getVerticalDividerLocation()
        );
        ConfigurationManager.storeInt("ElbaMainPanel", "diagramViewDivider",
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