/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.ToscanaJ;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.cernato.CernatoDimensionStrategy;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.CloseMainPanelActivity;
import net.sourceforge.toscanaj.gui.activity.SaveConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.TemporalMainDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.cernato.CernatoModel;
import net.sourceforge.toscanaj.model.cernato.View;
import net.sourceforge.toscanaj.model.cernato.ViewContext;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.parser.BurmeisterParser;
import net.sourceforge.toscanaj.parser.CernatoXMLParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.cernato.NDimDiagramEditingView;

import org.jdom.JDOMException;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

/// @todo check if the file we save to exists, warn if it does

public class SienaMainPanel extends JFrame implements MainPanel, EventBrokerListener {
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
    private TemporalMainDialog temporalControls;

    public SienaMainPanel() {
        super("Siena");

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);

        createViews();

        createMenuBar();
        
        ConfigurationManager.restorePlacement("SienaMainPanel", this,
                new Rectangle(100, 100, 500, 400));

		if(ConfigurationManager.fetchInt("SienaTemporalControls", "enabled", 0) == 1) {
		    temporalControls = new TemporalMainDialog(this, this.diagramView.getDiagramView(), eventBroker);
		    ConfigurationManager.restorePlacement("SienaTemporalControls", temporalControls, 
		    		new Rectangle(350,350,420,350));
		    temporalControls.show();
		}

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    public void createViews() {
        diagramView = new NDimDiagramEditingView(conceptualSchema, eventBroker);
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

        JMenuItem importBurmeisterItem = new JMenuItem("Import Burmeister Format...");
        importBurmeisterItem.setMnemonic(KeyEvent.VK_B);
        importBurmeisterItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importBurmeister();
            }
        });
        fileMenu.add(importBurmeisterItem);

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
        openDialog.setApproveButtonText("Import");
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        System.out.println(openDialog.getSelectedFile().getAbsolutePath());
        importCernatoXML(openDialog.getSelectedFile());
    }

	public void importCernatoXML(String fileLocation) {
		importCernatoXML(new File(fileLocation));
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
        } catch (JDOMException e) {
            ErrorDialog.showError(this, e, "Error parsing the file");
            return;
        }
        this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        this.conceptualSchema.setManyValuedContext(inputModel.getContext());
        addDiagrams(conceptualSchema, inputModel);
    }

    private void importBurmeister() {
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
        String newSchemaString = "Create new schema";
        String keepSchemaString = "Extend existing schema";
        Object retVal = JOptionPane.showInputDialog(this, "Do you want to keep the current schema?", "Keep Schema?",
                JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{keepSchemaString, newSchemaString},
                keepSchemaString);
        if (retVal == null) {
            return;
        }
        if (retVal == newSchemaString) {
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        }
        importBurmeister(openDialog.getSelectedFile());
    }

    private void importBurmeister(File file) {
        // store current file
        try {
            this.currentFile = file.getCanonicalPath();
        } catch (IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = file.getAbsolutePath();
            /// @todo what could be done here?
        }
		ContextImplementation context;
        try {
            context = BurmeisterParser.importBurmeisterFile(file);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
        addDiagram(conceptualSchema, context, context.getName(), new DefaultDimensionStrategy());
    }

    private void addDiagrams(ConceptualSchema schema, CernatoModel cernatoModel) {
        Vector views = cernatoModel.getViews();
        for (Iterator iterator = views.iterator(); iterator.hasNext();) {
            View view = (View) iterator.next();
            addDiagram(schema, new ViewContext(cernatoModel, view), view.getName(), new CernatoDimensionStrategy());
        }
    }

    private void addDiagram(ConceptualSchema schema, Context context, String name,
                            DimensionCreationStrategy dimensionStrategy) {
        LatticeGenerator lgen = new GantersAlgorithm();
        Lattice lattice = lgen.createLattice(context);
        Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, name, dimensionStrategy);
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
        if(temporalControls != null) {
        	ConfigurationManager.storePlacement("SienaTemporalControls", temporalControls);
        }
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
