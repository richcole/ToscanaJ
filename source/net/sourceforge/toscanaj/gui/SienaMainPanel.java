/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.cernato.LayoutOperations;
import net.sourceforge.toscanaj.events.*;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventListener;
import net.sourceforge.toscanaj.gui.action.*;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.cernato.CernatoModel;
import net.sourceforge.toscanaj.model.cernato.View;
import net.sourceforge.toscanaj.model.cernato.Criterion;
import net.sourceforge.toscanaj.model.cernato.PartialOrderNode;
import net.sourceforge.toscanaj.model.cernato.tests.TextDumps;
import net.sourceforge.toscanaj.model.events.*;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.ToscanaJ;
import net.sourceforge.toscanaj.parser.CernatoXMLParser;
import net.sourceforge.toscanaj.parser.DataFormatException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.Iterator;
import java.util.List;

/// @todo check if the file we save to exists, warn if it does
public class SienaMainPanel extends JFrame implements MainPanel, EventListener {
    /**
     *  Main Controllers
     */
    private EventBroker eventBroker;

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
    private DiagramEditingView diagramView;
    private String currentFile = null;

    public SienaMainPanel() {
        super("Siena");

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);

        createViews();

        createMenuBar();

        ConfigurationManager.restorePlacement("SienaMainPanel", this,
                new Rectangle(100, 100, 500, 400));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    public void createViews() {
        diagramView = new DiagramEditingView(this, conceptualSchema, eventBroker);
        diagramView.setDividerLocation(ConfigurationManager.fetchInt("SienaMainPanel", "diagramViewDivider", 200));
        setContentPane(diagramView);
    }


    public void createMenuBar() {

        // --- menu bar ---
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // --- file menu ---
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        JMenuItem importCernatoXMLItem = new JMenuItem("Import Cernato XML...");
        importCernatoXMLItem.setMnemonic(KeyEvent.VK_C);
        importCernatoXMLItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importCernatoXML();
            }
        });
        fileMenu.add(importCernatoXMLItem);

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

        JMenuItem aboutItem = new JMenuItem("About Siena");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
    }

    private void importCernatoXML() {
        final JFileChooser openDialog;
        if (this.currentFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.currentFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        importCernatoXML(openDialog.getSelectedFile());
    }

    private void importCernatoXML(File file) {
        // store current file
        try {
            this.currentFile = file.getCanonicalPath();
        } catch (IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = file.getAbsolutePath();
            /// @todo what could be done here?
        }
        CernatoModel inputModel;
        try {
            inputModel = CernatoXMLParser.importCernatoXMLFile(file);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
        this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        addDiagrams(conceptualSchema, inputModel);
    }

    private void addDiagrams(ConceptualSchema schema, CernatoModel cernatoModel) {
        /// @todo finish (create lattice generation, create layout algorithm)
        Vector views = cernatoModel.getViews();
        Vector dimensions = LayoutOperations.calculateDimensions(cernatoModel);
        for (Iterator iterator = views.iterator(); iterator.hasNext();) {
            View view = (View) iterator.next();
            TextDumps.dump(cernatoModel, view, System.out);
            addDiagram(schema, view, dimensions);
            System.out.println("============================================================");
            System.out.println();
        }
    }

    private void addDiagram(ConceptualSchema schema, View view, Vector dimensions) {
        SimpleLineDiagram diagram = new SimpleLineDiagram();
        diagram.setTitle(view.getName());
        List criteria = view.getCriteria();
        for (Iterator iterator = criteria.iterator(); iterator.hasNext();) {
            Criterion criterion = (Criterion) iterator.next();
            System.out.println("Criterion '" + criterion.getProperty().getName() + ": " +
                               criterion.getValueGroup().getName() + "' is assigned to dimensions:");
            int count = 0;
            for (Iterator iterator2 = dimensions.iterator(); iterator2.hasNext();) {
                net.sourceforge.toscanaj.model.cernato.Dimension dimension =
                                                (net.sourceforge.toscanaj.model.cernato.Dimension) iterator2.next();
                count++;
                Vector path = dimension.getPath();
                for (Iterator it3 = path.iterator(); it3.hasNext();) {
                    PartialOrderNode node = (PartialOrderNode) it3.next();
                    if(node.getValueGroup() == criterion.getValueGroup()) {
                        System.out.print(count + " - ");
                    }
                }
            }
            System.out.println("");
        }
        schema.addDiagram(diagram);
    }

    protected void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "This is Siena " + ToscanaJ.VersionString + ".\n\n" +
                "Copyright (c) DSTC Pty Ltd, Technische Universitaet Darmstadt and the\n" +
                "University of Queensland\n\n" +
                "See http://toscanaj.sourceforge.net for more information.",
                "About Siena",
                JOptionPane.PLAIN_MESSAGE);
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("SienaMainPanel", this);
        ConfigurationManager.storeInt("SienaMainPanel", "diagramViewDivider",
                diagramView.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            conceptualSchema = schemaEvent.getConceptualSchema();
        }
    }
}
