package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DBConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.CloseMainPanelActivity;
import net.sourceforge.toscanaj.gui.activity.LoadConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.NewConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;
import net.sourceforge.toscanaj.view.database.SchemeView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

public class AnacondaJMainPanel extends JFrame implements MainPanel, BrokerEventListener {

    /**
     *  Main Controllers
     */
    private EventBroker eventBroker;
    private DBConnection databaseConnection;

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

    /**
     * Views
     */
    private PanelStackView mainView;
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
        databaseConnection = new DBConnection(eventBroker);

        eventBroker.subscribe(this,
                              ConceptualSchemaChangeEvent.class,
                              Object.class );

        createViews();
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
            new DatabaseConnectionInformationView(this, conceptualSchema.getDatabaseInfo());
        eventBroker.subscribe(connectionInformationView,
                              ConceptualSchemaChangeEvent.class,
                              Object.class );

        JPanel tableView = new SchemeView(this);
//        JPanel tableView = new JPanel();
//        tableView.setBackground(Color.black);

        JPanel scaleView = new JPanel();
        scaleView.setBackground(Color.green);

        diagramView = new DiagramEditingView(this, conceptualSchema);
        diagramView.setDividerLocation(ConfigurationManager.fetchInt("AnacondaJMainPanel", "diagramViewDivider", 200));
        eventBroker.subscribe(diagramView,
                              ConceptualSchemaChangeEvent.class,
                              Object.class );

        mainView.addView("Connection", connectionInformationView);
        mainView.addView("Tables", tableView);
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
                KeyEvent.VK_O,
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_O,
                        ActionEvent.CTRL_MASK
                )
        );

        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(openFileAction);
        fileMenu.add(openMenuItem);

        /*
        JMenuItem saveMenuItem = new JMenuItem("Save...");
        AnacondaSaveFileActivity saveActivity =
            new AnacondaSaveFileActivity(conceptualSchema, this);
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
        saveActivity.setPrepareActivity(new PrepareToSaveActivity());
        fileMenu.add(saveMenuItem);
        */

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

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("AnacondaJMainPanel", this);
        ConfigurationManager.storeInt("AnacondaJMainPanel", "mainPanelDivider",
                mainView.getDividerLocation()
        );
        ConfigurationManager.storeInt("AnacondaJMainPanel", "diagramViewDivider",
                diagramView.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

    public void processEvent(Event e) {
        if ( e instanceof ConceptualSchemaChangeEvent ) {
            ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            conceptualSchema = schemaEvent.getConceptualSchema();
            if (databaseConnection.isConnected()) {
                try {
                    databaseConnection.disconnect();
                } catch (DatabaseException ex) {
                    ErrorDialog.showError(this, ex, "Closing database error", "Some error closing the old database:\n" + ex.getMessage());
                    ex.printStackTrace();
                    return;
                }
            }
            try {
                databaseConnection.connect(conceptualSchema.getDatabaseInfo());
                URL location = conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
                if (location != null) {
                    databaseConnection.executeScript(location);
                }
            } catch (DatabaseException ex) {
                ErrorDialog.showError(this, ex,  "DB Connection failed", "Can not connect to the database");
            }
        }
    }
}
