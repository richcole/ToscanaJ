package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.model.AnacondaModel;

import net.sourceforge.toscanaj.controller.ConfigurationManager;

import net.sourceforge.toscanaj.gui.action.*;

import net.sourceforge.toscanaj.view.AnacondaModelView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AnacondaMainPanel extends JFrame
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
    JSplitPane          splitPane;
    AnacondaModelView   modelView;
    JPanel              rightPane;

    public AnacondaMainPanel() {
        super("AnacondaJ");

        createViews();
        createMenuBar();

        ConfigurationManager.restorePlacement("AnacondaMainPanel", this,
                new Rectangle(100, 100, 500, 400));
    }

    public void createViews()
    {
        rightPane = new JPanel();
        rightPane.setLayout(new CardLayout());

        modelView  = new AnacondaModelView(this, model, rightPane);

        splitPane  = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, modelView, rightPane
        );
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        splitPane.setDividerLocation(
                ConfigurationManager.fetchInt("AnacondaMainPanel", "divider", 200)
        );

        setContentPane(splitPane);
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
        saveMenuItem.addActionListener(
                new SaveFileAction(
                        this,
                        new AnacondaSaveFileActivity(model, this),
                        KeyEvent.VK_O,
                        KeyStroke.getKeyStroke(
                                KeyEvent.VK_O,
                                ActionEvent.CTRL_MASK
                        )
                )
        );
        fileMenu.add(saveMenuItem);

        // --- file exit item ---
        JMenuItem exitMenuItem;
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(
                new SimpleAction(
                        this,
                        new AnacondaQuitActivity(this),
                        KeyEvent.VK_O,
                        KeyStroke.getKeyStroke(
                            KeyEvent.VK_F4, ActionEvent.ALT_MASK
                        )
                )
        );
        fileMenu.add(exitMenuItem);
    }

    public void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("AnacondaMainPanel", this);
        ConfigurationManager.storeInt("AnacondaMainPanel", "divider",
                splitPane.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

    /**
     *  Main method for running the program
     */
    public static void main(String[] args) {
        final AnacondaMainPanel mainWindow;
        mainWindow = new AnacondaMainPanel();

        mainWindow.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                mainWindow.closeMainPanel();
            }
        });

        mainWindow.setVisible(true);
    }


}
