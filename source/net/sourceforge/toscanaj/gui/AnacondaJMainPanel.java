package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;
import net.sourceforge.toscanaj.view.database.DatabaseSchemaView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.scales.ScaleEditingView;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;

public class AnacondaJMainPanel extends JFrame implements MainPanel, BrokerEventListener {
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

    public class PrepareToSaveActivity implements SimpleActivity {

        public boolean doActivity() throws Exception {
            //return prepareToSave();
            return true;
        }
    }

    public AnacondaJMainPanel() {
        super("AnacondaJMainPanel");

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);
        databaseConnection = new DatabaseConnection(eventBroker);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class );
        eventBroker.subscribe(this, DatabaseInfoChangedEvent.class, Object.class );

        createViews();

        mruList = ConfigurationManager.fetchStringList("AnacondaJMainPanel", "mruFiles", MaxMruFiles);
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile = new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }

        createMenuBar();

        ConfigurationManager.restorePlacement("AnacondaJMainPanel", this,
                new Rectangle(100, 100, 500, 400));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    public void createViews() {
        mainView = new PanelStackView(this);
        mainView.setDividerLocation(ConfigurationManager.fetchInt("AnacondaJMainPanel", "mainPanelDivider", 200));

        DatabaseConnectionInformationView connectionInformationView =
            new DatabaseConnectionInformationView(this, conceptualSchema.getDatabaseInfo(), eventBroker);

        databaseSchemaView = new DatabaseSchemaView(this, eventBroker);
        databaseSchemaView.setHorizontalDividerLocation(
                            ConfigurationManager.fetchInt("AnacondaJMainPanel", "databaseSchemaViewHorizontalDivider", 200));
        databaseSchemaView.setVerticalDividerLocation(
                            ConfigurationManager.fetchInt("AnacondaJMainPanel", "databaseSchemaViewVerticalDivider", 300));

        scaleView = new ScaleEditingView(this, conceptualSchema, eventBroker);
        scaleView.setHorizontalDividerLocation(
                            ConfigurationManager.fetchInt("AnacondaJMainPanel", "scaleViewHorizontalDivider", 200));
        scaleView.setVerticalDividerLocation(
                            ConfigurationManager.fetchInt("AnacondaJMainPanel", "scaleViewVerticalDivider", 300));

        diagramView = new DiagramEditingView(this, conceptualSchema, eventBroker);
        diagramView.setDividerLocation(ConfigurationManager.fetchInt("AnacondaJMainPanel", "diagramViewDivider", 200));

        mainView.addView("Connection", connectionInformationView);
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

        // --- help menu ---
        // create a help menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);

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
    }

    private void openSchemaFile(File schemaFile) {
        try {
            conceptualSchema = CSXParser.parse(eventBroker, schemaFile, databaseConnection);
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
        if(mruMenu == null) { // no menu yet
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
        ConfigurationManager.storePlacement("AnacondaJMainPanel", this);
        ConfigurationManager.storeInt("AnacondaJMainPanel", "mainPanelDivider",
                mainView.getDividerLocation()
        );
        ConfigurationManager.storeStringList("AnacondaJMainPanel", "mruFiles", this.mruList);
        ConfigurationManager.storeInt("AnacondaJMainPanel", "databaseSchemaViewHorizontalDivider",
                databaseSchemaView.getHorizontalDividerLocation()
        );
        ConfigurationManager.storeInt("AnacondaJMainPanel", "databaseSchemaViewVerticalDivider",
                databaseSchemaView.getVerticalDividerLocation()
        );
        ConfigurationManager.storeInt("AnacondaJMainPanel", "scaleViewHorizontalDivider",
                scaleView.getHorizontalDividerLocation()
        );
        ConfigurationManager.storeInt("AnacondaJMainPanel", "scaleViewVerticalDivider",
                scaleView.getVerticalDividerLocation()
        );
        ConfigurationManager.storeInt("AnacondaJMainPanel", "diagramViewDivider",
                diagramView.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

    public void processEvent(Event e) {
        if ( e instanceof ConceptualSchemaChangeEvent ) {
            // set schema in any change event, sometimes the order of the events is wrong
            /// @todo make sure the events come in the proper order
            ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            conceptualSchema = schemaEvent.getConceptualSchema();
        }
        if ( e instanceof ConceptualSchemaLoadedEvent ) {
            ConceptualSchemaLoadedEvent loadEvent = (ConceptualSchemaLoadedEvent) e;
            File schemaFile = loadEvent.getFile();
            addFileToMRUList(schemaFile);
        }
        if ( e instanceof DatabaseInfoChangedEvent ) {
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
                    ErrorDialog.showError(this, ex,  "DB Connection failed",
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
