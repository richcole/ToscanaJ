package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.activity.CloseMainPanelActivity;
import net.sourceforge.toscanaj.gui.activity.NewConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.activity.LoadConceptualSchemaActivity;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AnacondaJMainPanel extends JFrame implements MainPanel {
    private ConceptualSchema conceptualSchema = new ConceptualSchema();

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

    public class PrepareToSaveActivity implements SimpleActivity {

        public boolean doActivity() throws Exception {
            //return prepareToSave();
            return true;
        }
    }


    public AnacondaJMainPanel() {
        super("AnacondaJMainPanel");

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
        mainView.setDividerLocation(ConfigurationManager.fetchInt("AnacondaJMainPanel", "divider", 200));
        JPanel connectionInformationView = new DatabaseConnectionInformationView(this, conceptualSchema.getDatabaseInfo());
        JPanel tableView = new JPanel();
        tableView.setBackground(Color.black);
        JPanel scaleView = new JPanel();
        scaleView.setBackground(Color.green);
        JPanel diagramView = new JPanel();
        diagramView.setBackground(Color.red);
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

        // --- file new item ---
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(
                new SimpleAction(
                        this,
                        new NewConceptualSchemaActivity(conceptualSchema),
                        "New",
                        KeyEvent.VK_N,
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_N,
                                ActionEvent.CTRL_MASK
                        )
                )
        );
        fileMenu.add(newMenuItem);

        // --- file open item ---
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(
                new OpenFileAction(
                        this,
                        new LoadConceptualSchemaActivity(conceptualSchema),
                        KeyEvent.VK_O,
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_O,
                                ActionEvent.CTRL_MASK
                        )
                )
        );
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

    public void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("AnacondaJMainPanel", this);
        ConfigurationManager.storeInt("AnacondaJMainPanel", "divider",
                mainView.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }
}
