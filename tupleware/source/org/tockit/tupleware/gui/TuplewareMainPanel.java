/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.gui;

import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.TupleConceptInterpreter;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.gui.MainPanel;
import net.sourceforge.toscanaj.gui.action.ExportDiagramAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.CheckDuplicateFileChooser;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.Context;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.plugin.PluginLoader;
import org.tockit.relations.model.Relation;
import org.tockit.relations.model.Tuple;
import org.tockit.relations.operations.JoinOperation;
import org.tockit.relations.operations.PickColumnsOperation;
import org.tockit.swing.preferences.ExtendedPreferences;
import org.tockit.tupleware.scaling.TupleScaling;
import org.tockit.tupleware.source.TupleSource;
import org.tockit.tupleware.source.TupleSourceRegistry;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TuplewareMainPanel extends JFrame implements MainPanel, EventBrokerListener {
    private static final ExtendedPreferences preferences = 
        ExtendedPreferences.userNodeForClass(TuplewareMainPanel.class);

    private final class ModalContext implements Context {
        private String name;
		private Set objects;
		private Set attributes;        
        private BinaryRelation incidenceRelation;
        
        private ModalContext(int dim) {
            this.name = tuples.getDimensionNames()[dim];
            Set objectTuples = tuples.getTuples();
            this.objects = new HashSet();
            for (Iterator iter = objectTuples.iterator(); iter.hasNext();) {
                final Tuple tuple = (Tuple) iter.next();
				this.objects.add(new FCAElementImplementation(tuple));
            }
            Set attributeTuples = PickColumnsOperation.pickColumn(tuples, dim).getTuples();
            this.attributes = new HashSet();
            for (Iterator iterator = attributeTuples.iterator(); iterator.hasNext();){
                Tuple tuple = (Tuple) iterator.next();
				this.attributes.add(new FCAElementImplementation(tuple.getElement(0)));                
            }
			int[] allButThisDim = new int[tuples.getArity() - 1];
			for (int j = 0; j < allButThisDim.length; j++) {
				if(j < dim) {
					allButThisDim[j] = j;
				} else {
					allButThisDim[j] = j + 1;
				}
			}
            final Relation magicJoin = JoinOperation.join(tuples, allButThisDim, tuples, allButThisDim);
			incidenceRelation = new BinaryRelation() {
				public boolean contains(Object domainObject, Object rangeObject) {
					Tuple left = (Tuple) ((FCAElementImplementation)domainObject).getData();
					Object right = ((FCAElementImplementation)rangeObject).getData();
					Object[] fullData = new Object[left.getLength() + 1];
					for (int j = 0; j < left.getLength(); j++) {
						fullData[j] = left.getElement(j);
					}
					fullData[left.getLength()] = right;
					return magicJoin.isRelated(fullData);
				}
			};
		}
        public String getName() {
            return name;
        }
        public Set getObjects() {
        	return this.objects;
        }
        public Set getAttributes() {
            return this.attributes;
        }
        public BinaryRelation getRelation() {
            return this.incidenceRelation;
        }
    }
    private static final String CONFIGURATION_ENTRY_LAST_FILE = "lastFileRead";
    private static final String CONFIGURATION_ENTRY_DIVIDER = "diagramViewDivider";

	private static final String WINDOW_TITLE = "Tupleware";

    private int[] objectIndices;
    private JTable tupleTable;
    private EventBroker eventBroker;

    /**
     *  Model
     */
    private ConceptualSchema conceptualSchema;
    private Relation tuples;

    private DiagramExportSettings diagramExportSettings;
    private ExportDiagramAction exportDiagramAction;
    private PageFormat pageFormat = new PageFormat();

    /**
     * Controls
     */
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem printMenuItem;
    private JMenu editMenu;
    private JMenu helpMenu;
    private JToolBar toolBar;

    private File lastFileRead = null;

    /**
     * Views
     */
    private DiagramEditingView diagramEditingView;
    private XMLEditorDialog schemaDescriptionView;
	private SaveFileAction saveAsFileAction;
	private SaveConceptualSchemaActivity saveActivity;

    public TuplewareMainPanel() {
        super(WINDOW_TITLE);

        Iterator it = GraphicFormatRegistry.getIterator();
        if (it.hasNext()) {
            this.diagramExportSettings = new DiagramExportSettings();
        }

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);
        
        loadPlugins();

        createViews();

        createMenuBar();
        createToolBar();

        createLayout();

        preferences.restoreWindowPlacement(this,
                new Rectangle(10, 10, 900, 700));
        String lastFilePath = preferences.get(CONFIGURATION_ENTRY_LAST_FILE, null);
        
        if(lastFilePath != null) {                                                    
            this.lastFileRead = new File(lastFilePath);
        }

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
        tabPanel.addTab("Tuples", createTuplePanel());
        tabPanel.addTab("Diagrams", diagramEditingView);
        tabPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        return tabPanel;
    }
    
    private Component createTuplePanel() {
        tupleTable = new JTable();
        return new JScrollPane(tupleTable);
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

        JButton createModalSystemButton = new JButton("Create modal system");
        createModalSystemButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createModalSystem();
            }
        });
        toolBar.add(createModalSystemButton);
    }

    private void createNewDiagram() {
		IndexSelectionDialog dialog = new IndexSelectionDialog(this, "Select attribute set", this.tuples.getDimensionNames());
		dialog.show();
		int[] attributeIndices = dialog.getSelectedIndices();
		Diagram2D diagram = TupleScaling.scaleTuples(this.tuples, this.objectIndices, attributeIndices);
		this.conceptualSchema.addDiagram(diagram);
	}

	private void createModalSystem() {
		String[] dimensionNames = this.tuples.getDimensionNames();
		int[] nonObjectDims = new int[this.tuples.getArity() - this.objectIndices.length];
		int offset = 0;
		for (int i = 0; i < nonObjectDims.length; i++) {
            while(offset < this.objectIndices.length && i + offset == this.objectIndices[offset]) {
            	offset++;
            }
            nonObjectDims[i] = i + offset;
        }
        this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        TupleConceptInterpreter interpreter = new TupleConceptInterpreter(this.objectIndices);
        this.conceptualSchema.setConceptInterpreter(interpreter);
        for (int i = 0; i < nonObjectDims.length; i++) {
        	int dim = nonObjectDims[i];
			String dimensionName = dimensionNames[dim];
            Context context = new ModalContext(dim);
            Lattice lattice = new GantersAlgorithm().createLattice(context);
            Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, dimensionName, new DefaultDimensionStrategy());
            this.conceptualSchema.addDiagram(diagram);
        }
	}

    public void createViews() {
        diagramEditingView = new DiagramEditingView(this, conceptualSchema, eventBroker);
        diagramEditingView.setDividerLocation(preferences.getInt(CONFIGURATION_ENTRY_DIVIDER, 200));
        this.diagramEditingView.getDiagramView().getController().getEventBroker().subscribe(
                                        this, DisplayedDiagramChangedEvent.class, Object.class);

        schemaDescriptionView = new XMLEditorDialog(this, "Schema description");
    }


    public void createMenuBar() {
        final DiagramView diagramView = this.diagramEditingView.getDiagramView();
    	
	    // --- menu bar ---
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // --- file menu ---
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        
        this.saveActivity = new SaveConceptualSchemaActivity(this.conceptualSchema, this.eventBroker);
		
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

        // we add the export options only if we can export at all
        /// @todo reduce duplicate code with ToscanaJMainPanel
        if (this.diagramExportSettings != null) {
            Frame frame = JOptionPane.getFrameForComponent(this);
            exportDiagramAction =
                new ExportDiagramAction(
                    frame,
                    this.diagramExportSettings,
                    this.diagramEditingView.getDiagramView(),
                    KeyEvent.VK_E,
                    KeyStroke.getKeyStroke(
                        KeyEvent.VK_E,
                        ActionEvent.CTRL_MASK));
            fileMenu.add(exportDiagramAction);
            exportDiagramAction.setEnabled(false);
            fileMenu.addSeparator();
        }
        
        printMenuItem = new JMenuItem("Print...");
        printMenuItem.setMnemonic(KeyEvent.VK_P);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        printMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                printDiagram();
            }
        });
        printMenuItem.setEnabled(false);
        fileMenu.add(printMenuItem);

        JMenuItem printSetupMenuItem = new JMenuItem("Print Setup...");
        printSetupMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                pageFormat = PrinterJob.getPrinterJob().pageDialog(pageFormat);
                printDiagram();
            }
        });
        printSetupMenuItem.setEnabled(true);
        fileMenu.add(printSetupMenuItem);

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
		
		Iterator tupleSources = TupleSourceRegistry.getTupleSources().iterator();
		while (tupleSources.hasNext()) {
			TupleSource curSource = (TupleSource) tupleSources.next();
			addTupleSourceMenuItem(tuplesMenu, this, curSource);
		}
		
		tuplesMenu.addSeparator();
		JMenuItem saveTuplesItem = new JMenuItem("Save tuples...");
		saveTuplesItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	saveTuples();
            }
        });
        tuplesMenu.add(saveTuplesItem);

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

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        ButtonGroup fontSizeGroup = new ButtonGroup();
        JMenu setMinLabelSizeSubMenu = new JMenu("Set minimum label size");
        setMinLabelSizeSubMenu.setMnemonic(KeyEvent.VK_S);
        JMenuItem fontRangeMenuItem = new JRadioButtonMenuItem("None");
        fontSizeGroup.add(fontRangeMenuItem);
        fontRangeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JMenuItem source = (JMenuItem) e.getSource();
                diagramView.setMinimumFontSize(0);
                source.setSelected(true);
            }
        });
        fontRangeMenuItem.setSelected(true);
        setMinLabelSizeSubMenu.add(fontRangeMenuItem);
        int fontRange = 6; //min font size
        while (fontRange < 26) {
            fontRangeMenuItem = new JRadioButtonMenuItem(fontRange + "");
            fontSizeGroup.add(fontRangeMenuItem);
            fontRangeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JMenuItem source = (JMenuItem) e.getSource();
                    int newFontSize = Integer.parseInt(source.getText());
                    diagramView.setMinimumFontSize(newFontSize);
                    source.setSelected(true);
                }
            });
            if (diagramView.getMinimumFontSize() == fontRange) {
                fontRangeMenuItem.setSelected(true);
            }
            fontRange += 2;
            setMinLabelSizeSubMenu.add(fontRangeMenuItem);
        }
        viewMenu.add(setMinLabelSizeSubMenu);

        final JCheckBoxMenuItem showAttributeLabels =
            new JCheckBoxMenuItem("Show Attribute Labels");
        showAttributeLabels.setMnemonic(KeyEvent.VK_A);
        showAttributeLabels.setSelected(true);
        showAttributeLabels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean newState = !AttributeLabelView.allAreHidden();
                showAttributeLabels.setSelected(!newState);
                AttributeLabelView.setAllHidden(newState);
                diagramView.repaint();
            }
        });
        viewMenu.add(showAttributeLabels);

        final JCheckBoxMenuItem showObjectLabels =
            new JCheckBoxMenuItem("Show Object Labels");
        showObjectLabels.setMnemonic(KeyEvent.VK_O);
        showObjectLabels.setSelected(true);
        showObjectLabels.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean newState = !ObjectLabelView.allAreHidden();
                showObjectLabels.setSelected(!newState);
                ObjectLabelView.setAllHidden(newState);
                diagramView.repaint();
            }
        });
        viewMenu.add(showObjectLabels);

        menuBar.add(viewMenu);

        // --- help menu ---
        // create a help menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        final Component parent = this;
		JMenuItem aboutItem = new JMenuItem("About Tupleware...");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(parent, "This is a crusty early version of Tupleware, (c) DSTC Pty. Ltd,\n" +
                                                      "University of Queenland and Technical University Darmstadt");
			}
		});
		helpMenu.add(aboutItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
    }

    protected void printDiagram() {
        PrinterJob printJob = PrinterJob.getPrinterJob();
        if (printJob.printDialog()) {
            try {
                printJob.setPrintable(this.diagramEditingView.getDiagramView(), pageFormat);
                printJob.print();
            } catch (Exception PrintException) {
                PrintException.printStackTrace();
            }
        }
    }

    protected void saveTuples() {
		final JFileChooser saveDialog;
		ExtensionFileFilter fileFilter = new ExtensionFileFilter(new String[]{"tuples"},"Tuple Set File");
		ExtensionFileFilter[] filterArray = { fileFilter };
		if (this.lastFileRead != null) {
			saveDialog = new CheckDuplicateFileChooser(this.lastFileRead, filterArray);
	        
		} else {
			saveDialog = new CheckDuplicateFileChooser(new File(System.getProperty("user.dir")), filterArray);
		}
		
		if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				this.lastFileRead = saveDialog.getSelectedFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(this.lastFileRead));
				for (int i = 0; i < this.tuples.getDimensionNames().length; i++) {
                    String name = this.tuples.getDimensionNames()[i];
                    if(i!=0) {
                    	writer.write('\t');
                    }
                    writer.write(name);
                }
                writer.newLine();
                for (Iterator iter = this.tuples.getTuples().iterator(); iter.hasNext();) {
                    Object[] tuple = (Object[]) iter.next();
                    for (int i = 0; i < tuple.length; i++) {
                        Object object = tuple[i];
						if(i!=0) {
							writer.write('\t');
						}
                        writer.write(object.toString());
                    }
					writer.newLine();
                }
                writer.close();
			} catch (IOException e) {
				ErrorDialog.showError(this, e, "Failed to write file");
			}
		}
    }

    private void addTupleSourceMenuItem(JMenu tuplesMenu,
                                        final JFrame parent,
                                        final TupleSource source) {
        JMenuItem menuItem = new JMenuItem(source.getMenuName());
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                source.show(parent,lastFileRead);
                if(source.getSelectedFile() != null) {
                    lastFileRead = source.getSelectedFile();
                }
                if(source.getTuples() == null) {
                    return;
                }
                tuples = source.getTuples();
                objectIndices = source.getObjectIndices();
                fillTable();     
                conceptualSchema = new ConceptualSchema(eventBroker);   
            }
        });
        tuplesMenu.add(menuItem);
    }

	private void fillTable() {
        Object[][] data = new Object[this.tuples.getTuples().size()][this.tuples.getDimensionNames().length];
        int row = 0;
        for (Iterator iter = this.tuples.getTuples().iterator(); iter.hasNext();) {
            Tuple tuple = (Tuple) iter.next();
            for (int col = 0; col < tuple.getLength(); col++) {
                data[row][col] = tuple.getElement(col);
            }
            row ++;
        }
        this.tupleTable.setModel(new DefaultTableModel(data, this.tuples.getDimensionNames()));
    }

    public void closeMainPanel() {
        // store current position
        preferences.storeWindowPlacement(this);
        preferences.putInt(CONFIGURATION_ENTRY_DIVIDER, 
                                      diagramEditingView.getDividerLocation());
		if (this.lastFileRead != null) {                                      
		    preferences.put(CONFIGURATION_ENTRY_LAST_FILE, 
										  this.lastFileRead.getAbsolutePath());
		}
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

	private void loadPlugins() {
		/// @todo this should be read from config manager?...
		String pluginsDirName = "plugins";

		String pluginsBaseDir = System.getProperty("user.dir") + File.separator;
		
		
		try {
			PluginLoader.Error[] errors = PluginLoader.loadPlugins(new File(pluginsBaseDir + pluginsDirName));
			if (errors.length > 0) {
				String errorMsg = "";
				for (int i = 0; i < errors.length; i++) {
					PluginLoader.Error error = errors[i];
					errorMsg += "Plugin location:\n\t" + error.getPluginLocation().getAbsolutePath();
					errorMsg += "\n";
					errorMsg += "Error:\n\t" + error.getException().getMessage();
					errorMsg += "\n\n";
					error.getException().printStackTrace();
				}
				JOptionPane.showMessageDialog(this, "There were errors loading plugins: \n" + errorMsg,
											"Error loading plugins", 
											JOptionPane.WARNING_MESSAGE);
			}
		}
		catch (FileNotFoundException e) {
			/// @todo log this error in logger rather then printing stderr
			System.err.println("Error loading plugins: " + e.getMessage());
		}
	}
	
    public void processEvent(Event e) {
        boolean haveDiagram = (this.diagramEditingView.getDiagramView().getDiagram() != null);
        this.exportDiagramAction.setEnabled(haveDiagram && (this.diagramExportSettings != null));
        this.printMenuItem.setEnabled(haveDiagram);
    }
}
