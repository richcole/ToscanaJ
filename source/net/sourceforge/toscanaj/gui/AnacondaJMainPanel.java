package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.model.AnacondaModel;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

import net.sourceforge.toscanaj.gui.action.*;

import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AnacondaJMainPanel extends JFrame
{
    /**
     * Stores the anaconda model, initially this is an empty model.
     */
    AnacondaModel model = new AnacondaModel();

    /**
     * Controls
     */
    JMenuBar menuBar;
    JMenu    helpMenu;
    JMenu    fileMenu;

    /**
     * Views
     */
    PanelStackView mainView;

    public class PrepareToSaveActivity implements SimpleActivity
    {

        public boolean doActivity() throws Exception
        {
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

    public void createViews()
    {
        mainView = new PanelStackView(this);
        mainView.setDividerLocation(ConfigurationManager.fetchInt("AnacondaJMainPanel", "divider", 200));
        JPanel connectionInformationView = new DatabaseConnectionInformationView(this, this.model.getDatabase().getInfo());
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
        /*
        JMenuItem newMenuItem = new JMenuItem("New...");
        newMenuItem.addActionListener(
                new SimpleAction(
                        this,
                        new AnacondaNewModelActivity(model, rightPane),
                        KeyEvent.VK_N,
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_N,
                                ActionEvent.CTRL_MASK
                        )
                )
        );
        fileMenu.add(newMenuItem);
        */

        // --- file open item ---
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(
                new OpenFileAction(
                        this,
                        new AnacondaOpenFileActivity(model, this),
                        KeyEvent.VK_O,
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_O,
                                ActionEvent.CTRL_MASK
                        )
                )
        );
        fileMenu.add(openMenuItem);

        JMenuItem saveMenuItem = new JMenuItem("Save...");
        AnacondaSaveFileActivity saveActivity =
            new AnacondaSaveFileActivity(model, this);
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

        // --- file exit item ---
        JMenuItem exitMenuItem;
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(
                new SimpleAction(
                        this,
                        new AnacondaQuitActivity(this),
                        KeyEvent.VK_Q,
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
