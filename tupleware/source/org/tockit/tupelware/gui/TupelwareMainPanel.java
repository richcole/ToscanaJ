/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.gui.MainPanel;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.tockit.events.EventBroker;
import org.tockit.tupelware.model.TupelSet;
import org.tockit.tupelware.scaling.TupelScaling;
import org.tockit.tupelware.source.TupelSource;
import org.tockit.tupelware.source.text.TextSource;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.Iterator;

public class TupelwareMainPanel extends JFrame implements MainPanel {
    private int[] objectIndices;
    private JTable tupelTable;
    private EventBroker eventBroker;
    static private final int MaxMruFiles = 8;
	private static final String WINDOW_TITLE = "Tupelware";

    /**
     *  Model
     */
    private ConceptualSchema conceptualSchema;
    private TupelSet tupels;

    /**
     * Controls
     */
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu helpMenu;
    private JToolBar toolBar;

    private File lastFileRead = null;

    /**
     * Views
     */
    private DiagramEditingView diagramView;
    private XMLEditorDialog schemaDescriptionView;
	private SaveFileAction saveAsFileAction;
	private SaveConceptualSchemaActivity saveActivity;

    public TupelwareMainPanel() {
        super(WINDOW_TITLE);

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);

        createViews();

        createMenuBar();
        createToolBar();

        createLayout();

        ConfigurationManager.restorePlacement("TupelwareMainPanel", this,
                new Rectangle(10, 10, 900, 700));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    private void createLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(createTabPanel(), BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private Component createTabPanel() {
        JTabbedPane tabPanel = new JTabbedPane();
        tabPanel.addTab("Tupels", createTupelPanel());
        tabPanel.addTab("Diagrams", diagramView);
        tabPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        return tabPanel;
    }
    
    private Component createTupelPanel() {
        tupelTable = new JTable();
        return new JScrollPane(tupelTable);
    }

    private void createToolBar() {
        toolBar = new JToolBar();
        JButton newDiagramButton = new JButton("New Diagram...");
        newDiagramButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createNewDiagram();
            }
        });
        toolBar.add(newDiagramButton);
    }

    private void createNewDiagram() {
        String result = JOptionPane.showInputDialog(this, "Please enter a name for the new diagram.",
                                                    "Enter name", JOptionPane.OK_CANCEL_OPTION);
        if(result != null) {
            IndexSelectionDialog dialog = new IndexSelectionDialog(this, "Select attribute set", this.tupels.getVariableNames());
            dialog.show();
            int[] attributeIndices = dialog.getSelectedIndices();
            Diagram2D diagram = TupelScaling.scaleTupels(this.tupels, this.objectIndices, attributeIndices);
            this.conceptualSchema.addDiagram(diagram);
        }
    }

    public void createViews() {
        diagramView = new DiagramEditingView(this, conceptualSchema, eventBroker);
        diagramView.setDividerLocation(ConfigurationManager.fetchInt("TupelwareMainPanel", "diagramViewDivider", 200));

        schemaDescriptionView = new XMLEditorDialog(this, "Schema description");
    }


    public void createMenuBar() {
    	
	    // --- menu bar ---
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // --- file menu ---
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
		
		this.saveAsFileAction =
			new SaveFileAction(
				this,
				saveActivity,
				KeyEvent.VK_A,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		this.saveAsFileAction.setPostSaveActivity(new SimpleActivity(){
			public boolean doActivity() throws Exception {
				conceptualSchema.dataSaved();
				return true;
			}
		});
		JMenuItem saveAsMenuItem = new JMenuItem("Save Schema As...");
		saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		saveAsMenuItem.addActionListener(saveAsFileAction);
		fileMenu.add(saveAsMenuItem);

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

		JMenu tuplesMenu = new JMenu("Tuples");
		tuplesMenu.setMnemonic('t');
		
		final TupelSource source = new TextSource();
		final JFrame parent = this;
		JMenuItem loadTuples = new JMenuItem("Load from tab-delimited data...");
		loadTuples.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	source.show(parent,lastFileRead);
            	tupels = source.getTupels();
            	objectIndices = source.getObjectIndices();
            	lastFileRead = source.getSelectedFile();
				fillTable();     
				conceptualSchema = new ConceptualSchema(eventBroker);   
            }
		});
		tuplesMenu.add(loadTuples);

		menuBar.add(tuplesMenu);

        editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        menuBar.add(editMenu);

        JMenuItem editSchemaDescriptionMenuItem = new JMenuItem("Schema Description...");
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

		JMenuItem aboutItem = new JMenuItem("About Tupelware...");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(parent, "This is a crusty early version of Tupelware, (c) DSTC Pty. Ltd,\n" +
                                                      "University of Queenland and Technical University Darmstadt");
			}
		});
		helpMenu.add(aboutItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
    }

	private void fillTable() {
        Object[][] data = new Object[this.tupels.getTupels().size()][this.tupels.getVariableNames().length];
        int row = 0;
        for (Iterator iter = this.tupels.getTupels().iterator(); iter.hasNext();) {
            Object[] tupel = (Object[]) iter.next();
            for (int col = 0; col < tupel.length; col++) {
                data[row][col] = tupel[col];
            }
            row ++;
        }
        this.tupelTable.setModel(new DefaultTableModel(data, this.tupels.getVariableNames()));
    }

    public void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("TupelwareMainPanel", this);
        ConfigurationManager.storeInt("TupelwareMainPanel", "diagramViewDivider",
                diagramView.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

	protected boolean checkForMissingSave() throws HeadlessException {
		boolean closeOk;
		if (!conceptualSchema.isDataSaved()) {
			int returnValue = showFileChangedDialog();
			if (returnValue == 0) {
				// save
				boolean result = this.saveAsFileAction.saveFile();
				if (result) {
					closeOk = true;
				} else {
					closeOk = false;
				}
			} else if (returnValue == 1) {
				// discard
				closeOk = true;
			} else {
				// go back
				closeOk = false;
			}
		} else {
			closeOk = true;
		}
		return closeOk;
	}
	
	private int showFileChangedDialog() {
		// return values
		// 0 : Save file
		// 1 : Discard current file
		// 2 : Go back (cancel save/open/close operation) 
		Object[] options = { "Save", "Discard", "Go back" };
		return JOptionPane.showOptionDialog(
			this,
			"The conceptual schema has been modified. Do you want to save the changes?",
			"Schema changed",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.WARNING_MESSAGE,
			null,
			options,
			options[2]);
	}
}
