/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

/** 
 * @todo this class is too big in many senses, most noticably in the fact that it knows about
 * way too much stuff
 */ 
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.JTableHeader;

import net.sourceforge.toscanaj.controller.cernato.CernatoDimensionStrategy;
import net.sourceforge.toscanaj.controller.diagram.ObjectEditingLabelViewPopupMenuHandler;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DiagramToContextConverter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.gui.action.ExportDiagramAction;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.CloseMainPanelActivity;
import net.sourceforge.toscanaj.gui.activity.LoadConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.NewConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.SaveConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.temporal.TemporalControlsPanel;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAElement;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAElement;
import net.sourceforge.toscanaj.model.database.AggregateQuery;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttributeImplementation;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContextImplementation;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;
import net.sourceforge.toscanaj.parser.BurmeisterParser;
import net.sourceforge.toscanaj.parser.CSCParser;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.parser.ObjectAttributeListParser;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;
import net.sourceforge.toscanaj.view.manyvaluedcontext.ManyValuedAttributeDialog;
import net.sourceforge.toscanaj.view.manyvaluedcontext.ObjectDialog;
import net.sourceforge.toscanaj.view.manyvaluedcontext.RowHeader;
import net.sourceforge.toscanaj.view.manyvaluedcontext.TableRowHeaderResizer;
import net.sourceforge.toscanaj.view.manyvaluedcontext.TableView;

import org.jdom.JDOMException;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.cernatoXML.model.CernatoModel;
import org.tockit.cernatoXML.model.CernatoObject;
import org.tockit.cernatoXML.model.CernatoTable;
import org.tockit.cernatoXML.model.Criterion;
import org.tockit.cernatoXML.model.NumericalType;
import org.tockit.cernatoXML.model.NumericalValue;
import org.tockit.cernatoXML.model.Property;
import org.tockit.cernatoXML.model.PropertyType;
import org.tockit.cernatoXML.model.TextualType;
import org.tockit.cernatoXML.model.TextualValue;
import org.tockit.cernatoXML.model.View;
import org.tockit.cernatoXML.model.ViewContext;
import org.tockit.cernatoXML.parser.CernatoXMLParser;
import org.tockit.context.model.BinaryRelation;
import org.tockit.context.model.BinaryRelationImplementation;
import org.tockit.context.model.Context;
import org.tockit.datatype.Datatype;
import org.tockit.datatype.Value;
import org.tockit.datatype.xsd.DecimalType;
import org.tockit.datatype.xsd.DecimalValue;
import org.tockit.datatype.xsd.StringType;
import org.tockit.datatype.xsd.StringValue;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * @todo make sure all changes to the context will propagate to make the schema dirty.
 */
public class SienaMainPanel extends JFrame implements MainPanel, EventBrokerListener {
    private static final String WINDOW_TITLE = "Siena";
    private static final int MaxMruFiles = 8;

    private static final ExtendedPreferences preferences = ExtendedPreferences.userNodeForClass(SienaMainPanel.class);

    /**
     *  Main Controllers
     */
    private EventBroker eventBroker;

    /**
     *  Model
     */
    private ConceptualSchema conceptualSchema;
    
    private JMenuBar menuBar;
    private JMenu helpMenu;
    private JMenu fileMenu;
    private JMenu mruMenu;

    private DiagramEditingView diagramEditingView;
    private TemporalControlsPanel temporalControls;
    private List<String> mruList = new LinkedList<String>();
    private File currentFile;
    private DiagramExportSettings diagramExportSettings;
    private ExportDiagramAction exportDiagramAction;
    private File lastCSCFile;
    private File lastBurmeisterFile;
    private File lastOALFile;
    private File lastCernatoFile;
    private SaveFileAction saveAsFileAction;
    private SaveConceptualSchemaActivity saveActivity;
    
    /**
     * The last setup for page format given by the user.
     */
    private PageFormat pageFormat = new PageFormat();
    
	private TableView tableView;
	private RowHeader rowHeader;
    private JLabel temporalControlsLabel;
    private JRadioButtonMenuItem showExactMenuItem;
    private JRadioButtonMenuItem showAllMenuItem;
    private JMenuItem printMenuItem;
    private JMenuItem printSetupMenuItem;
    
	public SienaMainPanel(boolean loadLastFile) {
        super(WINDOW_TITLE);

        this.eventBroker = new EventBroker();
        this.conceptualSchema = new ConceptualSchema(this.eventBroker);

        this.diagramExportSettings = new DiagramExportSettings();

        this.eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        this.eventBroker.subscribe(this, ConceptualSchemaLoadedEvent.class, Object.class);
            
        setDefaultManyValuedContext();

        createViews();

        this.mruList = preferences.getStringList("mruFiles");
        createMenuBar();
        
        // if we have at least one MRU file try to open it
        if (loadLastFile && this.mruList.size() > 0) {
            File schemaFile =
                new File(this.mruList.get(this.mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }
        
        this.lastCernatoFile = new File(preferences.get("lastCernatoImport", ""));
        this.lastCSCFile = new File(preferences.get("lastCSCImport",""));
        this.lastBurmeisterFile = new File(preferences.get("lastBurmeisterImport",""));
        this.lastOALFile = new File(preferences.get("lastOALImport",""));

		this.setVisible(true);
        preferences.restoreWindowPlacement(this, new Rectangle(10, 10, 900, 700));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void setDefaultManyValuedContext() {
        WritableManyValuedContext manyValuedContext = new ManyValuedContextImplementation();
        manyValuedContext.add(StringType.createEnumerationRestrictedType("Single Valued", new String[]{"X"}));
        manyValuedContext.add(StringType.createEnumerationRestrictedType("Boolean", new String[]{"true", "false"}));
        manyValuedContext.add(StringType.createUnrestrictedType("String"));
        manyValuedContext.add(DecimalType.createUnrestrictedType("Number"));
        manyValuedContext.add(DecimalType.createDecimalType("Integer", null, false, null, false, new Integer(0)));
        manyValuedContext.add(DecimalType.createDecimalType("Non-Negative Integer", new Double(0), true, null, false, new Integer(0)));
        manyValuedContext.add(DecimalType.createDecimalType("Positive Integer", new Double(0), false, null, false, new Integer(0)));
        this.conceptualSchema.setManyValuedContext(manyValuedContext);
	}

	protected void createViews() {
		createDiagramEditingView();

		JTabbedPane mainPanel = new JTabbedPane();
		mainPanel.addTab("Context", createContextEditingView());
		mainPanel.setSelectedIndex(0);
        mainPanel.addTab("Diagrams", this.diagramEditingView);

		getContentPane().add(mainPanel);
	}

	protected void createDiagramEditingView() {
        this.diagramEditingView = new DiagramEditingView(this, this.conceptualSchema, this.eventBroker, false);
        this.temporalControlsLabel = new JLabel("Temporal Concept Analysis:");
        this.temporalControls = new TemporalControlsPanel(
                                            this.diagramEditingView.getDiagramView(),
                                            this.diagramExportSettings,
                                            this.eventBroker);
        boolean temporalControlsEnabled = preferences.getBoolean("temporalControlsEnabled", false); 
        this.temporalControlsLabel.setVisible(temporalControlsEnabled);
        this.temporalControls.setVisible(temporalControlsEnabled);                                    
        this.diagramEditingView.addAccessory(this.temporalControlsLabel);
        this.diagramEditingView.addAccessory(this.temporalControls);
        this.diagramEditingView.setExtraContextMenuActions(new DiagramEditingView.DiagramAction[] {
                new DiagramEditingView.DiagramAction() {
                    public void actionPerformed(ActionEvent e, Diagram2D diagram) {
                        insertDiagramIntoView(diagram, true);
                    }

                    public Object getLabel() {
                        return "Nest diagram";
                    }
                    
                    public boolean isEnabled() {
                        return SienaMainPanel.this.diagramEditingView.getDiagramView().getDiagram() != null;
                    }
                }
        });
		this.diagramEditingView.getDiagramView().getController().getEventBroker().subscribe(
										this, DisplayedDiagramChangedEvent.class, Object.class);
		DiagramView diagramView = this.diagramEditingView.getDiagramView();
		diagramView.getController().getEventBroker().subscribe(
			new ObjectEditingLabelViewPopupMenuHandler(
				diagramView),
			CanvasItemContextMenuRequestEvent.class,
			ObjectLabelView.getFactory().getLabelClass());
		this.diagramEditingView.setDividerLocation(preferences.getInt("diagramViewDivider", 200));
	}

    private void insertDiagramIntoView(final Diagram2D diagram, final boolean nestDiagram) {
        Diagram2D oldDiagram = this.diagramEditingView.getDiagramView().getDiagram();
        Diagram2D newDiagram = diagram;
        DiagramHistory diagramHistory = new DiagramHistory();

        if(nestDiagram && oldDiagram != null) {
            // before nesting make sure apposition is ok by synchronizing object sets to their join
            Iterator oldObjectSetIterator = oldDiagram.getTopConcept().getExtentIterator();
            Set oldObjects = new HashSet();
            while (oldObjectSetIterator.hasNext()) {
                oldObjects.add(oldObjectSetIterator.next());
            }
            Iterator newObjectSetIterator = diagram.getTopConcept().getExtentIterator();
            while (newObjectSetIterator.hasNext()) {
                Object object = newObjectSetIterator.next();
                if(oldObjects.contains(object)) {
                    // remove the common ones from the old set
                    oldObjects.remove(object);
                } else {
                    // add the ones that are only in new to the top node of the old diagram if its intent is empty
                    // if the intent is not empty, the diagram needs to be recreated to have a matching concept.
                    // Since the new diagram will have a concept with empty intent, this should happen only once.
                    // note that if there is intent which would match, the object should be in both diagrams in the
                    // first place
                    if(oldDiagram.getTopConcept().getIntentSize() == 0) {
                        ((ConceptImplementation)oldDiagram.getTopConcept()).addObject(object);
                    } else {
                        oldDiagram = extendDiagram(oldDiagram, object);
                    }
                }
            }
            // now add the ones that are in the old diagram but not found in the new one to
            // the new top concept
            // this is again only happening if there is no intent attached to the top node, else
            // we have to create a new diagram
            for (Iterator iter = oldObjects.iterator(); iter.hasNext();) {
                if (newDiagram.getTopConcept().getIntentSize() == 0) {
                    ((ConceptImplementation) newDiagram.getTopConcept()).addObject(iter.next());
                } else {
                    newDiagram = extendDiagram(newDiagram, iter.next());
                }
            }
            assert oldDiagram.getTopConcept().getExtentSize() == diagram.getTopConcept().getExtentSize();
            // nest the results
            newDiagram = new NestedLineDiagram(oldDiagram, newDiagram);
            diagramHistory.addDiagram(oldDiagram);
            diagramHistory.addDiagram(newDiagram);
            diagramHistory.setNestingLevel(1);
        } else {
            diagramHistory.addDiagram(newDiagram);
            diagramHistory.setNestingLevel(0);
        }
        this.diagramEditingView.getDiagramView().showDiagram(newDiagram);
        ConceptInterpretationContext context = new ConceptInterpretationContext(diagramHistory, new EventBroker());
        this.diagramEditingView.getDiagramView().setConceptInterpretationContext(context);
    }

    private Diagram2D extendDiagram(Diagram2D oldDiagram, Object newObject) {
        ContextImplementation context = (ContextImplementation) DiagramToContextConverter.getContext(oldDiagram);
        context.getObjects().add(newObject);
        LatticeGenerator lgen = new GantersAlgorithm();
        return NDimLayoutOperations.createDiagram(lgen.createLattice(context),
                                                  oldDiagram.getTitle(),
                                                  new DefaultDimensionStrategy());
    }

	/**
	 * @todo this method is inconsistent with createDiagramEditingView() in terms of return value.
	 */
	protected Component createContextEditingView() {
		this.rowHeader = new RowHeader(this.conceptualSchema.getManyValuedContext());
		this.tableView = new TableView(this.conceptualSchema.getManyValuedContext());
				
		JScrollPane scrollPane = new JScrollPane(this.tableView);
		scrollPane.setRowHeaderView(this.rowHeader);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JTableHeader corner = this.rowHeader.getTableHeader();
        corner.setReorderingAllowed(false);
        corner.setResizingAllowed(false);

        scrollPane.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, corner);

        new TableRowHeaderResizer(scrollPane).setEnabled(true);
        
		JPanel retVal = new JPanel(new BorderLayout());
		retVal.add(createContextToolbar(), BorderLayout.NORTH);
		retVal.add(scrollPane, BorderLayout.CENTER);
		return retVal;
	}

	private void editObject(int row) {
		Frame tFrame = JOptionPane.getFrameForComponent(this.tableView);
		List objectList = this.conceptualSchema.getManyValuedContext().getObjects();
		WritableFCAElement object = (WritableFCAElement) objectList.get(row);
		new ObjectDialog(tFrame, object);
		this.conceptualSchema.getManyValuedContext().update();
		this.tableView.updateModel();
		this.rowHeader.updateModel();
	}

	private void editAttribute(int column) {
		Frame tFrame = JOptionPane.getFrameForComponent(this.tableView);
		List manyValuedAttributeList = this.conceptualSchema.getManyValuedContext().getAttributes();
		WritableManyValuedAttribute attribute = (WritableManyValuedAttribute) manyValuedAttributeList.get(column);
		new ManyValuedAttributeDialog(tFrame, attribute, this.conceptualSchema.getManyValuedContext());
		this.conceptualSchema.getManyValuedContext().update();
		this.tableView.updateModel();
	}

	private JToolBar createContextToolbar() {
		JToolBar retVal = new JToolBar();
		final JButton addObjectButton = new JButton("Add object...");
		addObjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	WritableManyValuedContext manyValuedContext = SienaMainPanel.this.conceptualSchema.getManyValuedContext();
                manyValuedContext.add(new FCAElementImplementation(""));
            	editObject(manyValuedContext.getObjects().size() - 1);
            }
        });

		final JButton addAttributeButton = new JButton("Add attribute...");
		addAttributeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WritableManyValuedContext manyValuedContext = SienaMainPanel.this.conceptualSchema.getManyValuedContext();
				Datatype firstType;
				if (manyValuedContext.getTypes().isEmpty()) {
					firstType = null;
				} else {
					firstType = (Datatype) manyValuedContext.getTypes().iterator().next();
				}
				manyValuedContext.add(new ManyValuedAttributeImplementation(firstType,""));
				editAttribute(manyValuedContext.getAttributes().size() - 1);
			}
		});

		final JButton createDiagramButton = new JButton("Create Diagram...");
		createDiagramButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                /// @todo
			}
		});
		
		retVal.add(addObjectButton);
		retVal.add(addAttributeButton);
		retVal.add(createDiagramButton);

        return retVal;
    }

    public void createMenuBar() {
        if (this.menuBar == null) {
        	this.menuBar = new JMenuBar();
            setJMenuBar(this.menuBar);
        } else {
        	this.menuBar.removeAll();
        }
        
        // --- file menu ---
        this.fileMenu = new JMenu("File");
        this.fileMenu.setMnemonic(KeyEvent.VK_F);
        this.menuBar.add(this.fileMenu);

        SimpleActivity testSchemaSavedActivity = new SimpleActivity() {
            public boolean doActivity() throws Exception {
                return checkForMissingSave();
            }
        };
        NewConceptualSchemaActivity newSchemaActivity =
            new NewConceptualSchemaActivity(this.eventBroker);
        newSchemaActivity.setPostNewActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
            	SienaMainPanel.this.currentFile = null;
                updateWindowTitle();
                setDefaultManyValuedContext();
                SienaMainPanel.this.rowHeader.setManyValuedContext(SienaMainPanel.this.conceptualSchema.getManyValuedContext());
                SienaMainPanel.this.tableView.setManyValuedContext(SienaMainPanel.this.conceptualSchema.getManyValuedContext());
                SienaMainPanel.this.conceptualSchema.dataSaved();
                return true;
            }
        });
        SimpleAction newAction =
            new SimpleAction(
                this,
                newSchemaActivity,
                "New",
                KeyEvent.VK_N,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.addActionListener(newAction);
        this.fileMenu.add(newMenuItem);

        // @todo check why this code doesn't use openSchema(File)
        LoadConceptualSchemaActivity loadSchemaActivity =
            new LoadConceptualSchemaActivity(this.eventBroker);
        loadSchemaActivity.setTestOpenOkActivity(testSchemaSavedActivity);
        OpenFileAction openFileAction =
            new OpenFileAction(
                this,
                loadSchemaActivity,
                this.currentFile,
                KeyEvent.VK_O,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openFileAction.addPostOpenActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                updateWindowTitle();
                SienaMainPanel.this.rowHeader.setManyValuedContext(SienaMainPanel.this.conceptualSchema.getManyValuedContext());
                SienaMainPanel.this.tableView.setManyValuedContext(SienaMainPanel.this.conceptualSchema.getManyValuedContext());
                return true;
            }
        });
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(openFileAction);
        this.fileMenu.add(openMenuItem);

        this.mruMenu = new JMenu("Reopen");
        this.mruMenu.setMnemonic(KeyEvent.VK_R);
        this.fileMenu.add(this.mruMenu);

        JMenuItem saveMenuItem = new JMenuItem("Save...");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        this.saveActivity =
            new SaveConceptualSchemaActivity(this.conceptualSchema, this.eventBroker);
        if(this.saveAsFileAction == null) {
            this.saveAsFileAction =
                new SaveFileAction(
                    this,
                    this.saveActivity,
                    KeyEvent.VK_S,
                    KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        }
        this.saveAsFileAction.setPostSaveActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                setCurrentFile(SienaMainPanel.this.saveAsFileAction.getLastFileUsed());
                SienaMainPanel.this.conceptualSchema.dataSaved();
                recreateMruMenu();
                return true;
            }
        });

        this.fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
        saveAsMenuItem.addActionListener(this.saveAsFileAction);
        this.fileMenu.add(saveAsMenuItem);

        this.fileMenu.addSeparator();

        JMenuItem importCernatoXMLItem = new JMenuItem("Import Cernato XML...");
        importCernatoXMLItem.setMnemonic(KeyEvent.VK_C);
        importCernatoXMLItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    importCernatoXML();
                } catch (IOException e1) {
                    ErrorDialog.showError(SienaMainPanel.this,e1,"Cernato XML import failed");
                }
            }
        });
        this.fileMenu.add(importCernatoXMLItem);

        JMenuItem importBurmeisterItem =
            new JMenuItem("Import Burmeister Format...");
        importBurmeisterItem.setMnemonic(KeyEvent.VK_B);
        importBurmeisterItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importBurmeister();
            }
        });
        this.fileMenu.add(importBurmeisterItem);

        JMenuItem importOALItem =
            new JMenuItem("Import Object Attribute List...");
        importOALItem.setMnemonic(KeyEvent.VK_A);
        importOALItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importObjectAttributeList();
            }
        });
        this.fileMenu.add(importOALItem);

        JMenuItem importCSCMenuItem = new JMenuItem("Import CSC File...");
        importCSCMenuItem.setMnemonic(KeyEvent.VK_I);
        importCSCMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importCSC();
            }
        });
        this.fileMenu.add(importCSCMenuItem);

        this.fileMenu.addSeparator();

        // we add the export options only if we can export at all
        /// @todo reduce duplicate code with ToscanaJMainPanel
        if (this.diagramExportSettings != null) {
            Frame frame = JOptionPane.getFrameForComponent(this);
            this.exportDiagramAction =
                new ExportDiagramAction(
                    frame,
                    this.diagramExportSettings,
                    this.diagramEditingView.getDiagramView(),
                    KeyEvent.VK_E,
                    KeyStroke.getKeyStroke(
                        KeyEvent.VK_E,
                        ActionEvent.CTRL_MASK));
            this.fileMenu.add(this.exportDiagramAction);
            this.exportDiagramAction.setEnabled(false);
            this.fileMenu.addSeparator();
        }

        // menu item PRINT
        this.printMenuItem = new JMenuItem("Print...");
        this.printMenuItem.setMnemonic(KeyEvent.VK_P);
        this.printMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        this.printMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                printDiagram();
            }
        });
        this.printMenuItem.setEnabled(false);
        this.fileMenu.add(this.printMenuItem);

        // menu item PRINT SETUP
        this.printSetupMenuItem = new JMenuItem("Print Setup...");
        this.printSetupMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	SienaMainPanel.this.pageFormat = PrinterJob.getPrinterJob().pageDialog(SienaMainPanel.this.pageFormat);
                printDiagram();
            }
        });
        this.printSetupMenuItem.setEnabled(true);
        this.fileMenu.add(this.printSetupMenuItem);
        
        this.fileMenu.addSeparator();

        // --- file exit item ---
        JMenuItem exitMenuItem;
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(
            new SimpleAction(
                this,
                new CloseMainPanelActivity(this),
                "Exit",
                KeyEvent.VK_X,
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK)));
        this.fileMenu.add(exitMenuItem);

        final DiagramView diagramView =
            this.diagramEditingView.getDiagramView();

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.add(diagramView.getUndoManager().getUndoAction());
        editMenu.add(diagramView.getUndoManager().getRedoAction());
        this.menuBar.add(editMenu);
        
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);

        ButtonGroup documentsDisplayGroup = new ButtonGroup();
        this.showExactMenuItem = new JRadioButtonMenuItem("Show only exact matches");
        this.showExactMenuItem.setMnemonic(KeyEvent.VK_X);
        this.showExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        this.showExactMenuItem.setSelected(true);
        this.showExactMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	SienaMainPanel.this.diagramEditingView.getDiagramView().setDisplayType(true);
            }
        }); 
        documentsDisplayGroup.add(this.showExactMenuItem);
        viewMenu.add(this.showExactMenuItem);

        this.showAllMenuItem = new JRadioButtonMenuItem("Show all matches");
        this.showAllMenuItem.setMnemonic(KeyEvent.VK_A);
        this.showAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        this.showAllMenuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	SienaMainPanel.this.diagramEditingView.getDiagramView().setDisplayType(false);
            }
        }); 
        documentsDisplayGroup.add(this.showAllMenuItem);
        viewMenu.add(this.showAllMenuItem);
        
        viewMenu.addSeparator();

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

        ButtonGroup labelContentGroup = new ButtonGroup();
        viewMenu.addSeparator();
        addQueryMenuItem(AggregateQuery.COUNT_QUERY, viewMenu, labelContentGroup, KeyEvent.VK_C);
        addQueryMenuItem(ListQuery.KEY_LIST_QUERY, viewMenu, labelContentGroup, KeyEvent.VK_L);
        addQueryMenuItem(AggregateQuery.PERCENT_QUERY, viewMenu, labelContentGroup, KeyEvent.VK_D);
        
        viewMenu.addSeparator();
        
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

        JMenu colorModeMenu = new JMenu("Color schema");
        ButtonGroup colorModeGroup = new ButtonGroup();
        
        Collection<DiagramSchema> colorSchemas = DiagramSchema.getSchemas();
        for (Iterator<DiagramSchema> iter = colorSchemas.iterator(); iter.hasNext(); ) {
            final DiagramSchema schema = iter.next();
            JRadioButtonMenuItem colorSchemaItem = new JRadioButtonMenuItem(schema.getName());
            colorSchemaItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    schema.setAsCurrent();
                    SienaMainPanel.this.diagramEditingView.getDiagramView().setDiagramSchema(schema);
                }
            });
            if(schema == DiagramSchema.getCurrentSchema()) {
                colorSchemaItem.setSelected(true);
            }
            colorModeGroup.add(colorSchemaItem);
            colorModeMenu.add(colorSchemaItem);
        }
        viewMenu.add(colorModeMenu);
        
        viewMenu.addSeparator();

        final JCheckBoxMenuItem showTemporalControls =
            new JCheckBoxMenuItem("Show Temporal Controls");
        showTemporalControls.setMnemonic(KeyEvent.VK_T);
        showTemporalControls.setSelected(preferences.getBoolean("temporalControlsEnabled", false));
        showTemporalControls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean newState = !SienaMainPanel.this.temporalControls.isVisible();
                SienaMainPanel.this.temporalControlsLabel.setVisible(newState);
                SienaMainPanel.this.temporalControls.setVisible(newState);
            }
        });
        viewMenu.add(showTemporalControls);

        this.menuBar.add(viewMenu);

        // --- help menu ---
        // create a help menu
        this.helpMenu = new JMenu("Help");
        this.helpMenu.setMnemonic(KeyEvent.VK_H);

        final JFrame parent = this;
        JMenuItem aboutItem = new JMenuItem("About Siena...");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ToscanaJMainPanel.showAboutDialog(parent);
            }
        });
        this.helpMenu.add(aboutItem);

        this.menuBar.add(Box.createHorizontalGlue());
        this.menuBar.add(this.helpMenu);
    }
    private void addQueryMenuItem(final Query query, JMenu viewMenu, ButtonGroup labelContentGroup, int mnemonic) {
        String name = query.getName();
        JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name);
        menuItem.setMnemonic(mnemonic);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	SienaMainPanel.this.diagramEditingView.getDiagramView().setQuery(query);
            }
        });
        if(labelContentGroup.getSelection() == null) {
            menuItem.setSelected(true);
            this.diagramEditingView.getDiagramView().setQuery(query);
        }
        labelContentGroup.add(menuItem);
        viewMenu.add(menuItem);
    }

    private void importCernatoXML() throws IOException {
        final JFileChooser openDialog;
        if (this.lastCernatoFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.lastCernatoFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setFileFilter(
            new ExtensionFileFilter(
                new String[] { "xml" },
                "Cernato XML Files"));
        openDialog.setApproveButtonText("Import");
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        importCernatoXML(openDialog.getSelectedFile());
    }

    public void importCernatoXML(File file) throws IOException {
        this.lastCernatoFile = file;
        CernatoModel inputModel;
        try {
            inputModel = CernatoXMLParser.importCernatoXMLFile(file);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (JDOMException e) {
            ErrorDialog.showError(this, e, "Error parsing the file");
            return;
        } catch (org.tockit.cernatoXML.parser.DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
        this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        WritableManyValuedContext model = createManyValuedContext(inputModel);
        this.conceptualSchema.setManyValuedContext(model);
        addDiagrams(this.conceptualSchema, inputModel);
		this.tableView.setManyValuedContext(model);
		this.rowHeader.setManyValuedContext(model);
        
        this.currentFile = null;
        String filename =
            file.getName().substring(0, file.getName().lastIndexOf('.'));
        setTitle(filename + " (Cernato import, unsaved) - " + WINDOW_TITLE);
		validate();
		repaint();
    }

    private WritableManyValuedContext createManyValuedContext(CernatoModel cernatoModel) {
        ManyValuedContextImplementation result = new ManyValuedContextImplementation();
        
        CernatoTable cernatoContext = cernatoModel.getContext();
        Vector types = cernatoModel.getTypes();
        Map<PropertyType, Datatype> typeMap = new Hashtable<PropertyType, Datatype>();
        for (Iterator it = types.iterator(); it.hasNext();) {
            PropertyType cernatoType = (PropertyType) it.next();
            org.tockit.cernatoXML.model.Value[] valueRange = 
                cernatoType.getValueRange();

            Datatype targetType;
            if(cernatoType instanceof NumericalType) {
                NumericalType numType = (NumericalType) cernatoType;
                NumericalValue min = (NumericalValue) valueRange[0];
                NumericalValue max = (NumericalValue) valueRange[1];
                
                /// @todo we lack support for the number of decimals on the Cernato side
                DecimalType decType = DecimalType.createDecimalType(
                        numType.getName(), min.getValue(), max.getValue(),
                        numType.getNumberOfDecimals());
                targetType = decType;
            } else if(cernatoType instanceof TextualType) {
                String[] stringValues = new String[valueRange.length];
                TextualType textType = new TextualType(cernatoType.getName());
                for (int i = 0; i < valueRange.length; i++) {
                    TextualValue textualValue = (TextualValue) valueRange[i];
                    stringValues[i] = textualValue.getDisplayString();
                }
                targetType = StringType.createEnumerationRestrictedType(
                        textType.getName(), stringValues);
            } else {
                throw new RuntimeException("Unknown Cernato type");
            }
            result.add(targetType);
            typeMap.put(cernatoType, targetType);
        }
        Set properties = cernatoContext.getProperties();
        Map<Property, ManyValuedAttributeImplementation> attributeMap = new Hashtable<Property, ManyValuedAttributeImplementation>();
        for (Iterator it = properties.iterator(); it.hasNext();) {
            Property property = (Property) it.next();
            Datatype attributeType = typeMap.get(property.getType());
            ManyValuedAttributeImplementation attribute = 
                new ManyValuedAttributeImplementation(attributeType, property.getName());
            result.add(attribute);
            attributeMap.put(property, attribute);
        }
        Set objects = cernatoContext.getObjects();
        Map<CernatoObject, FCAElementImplementation> objectMap = new Hashtable<CernatoObject, FCAElementImplementation>();
        for (Iterator it = objects.iterator(); it.hasNext();) {
            CernatoObject cernatoObject = (CernatoObject) it.next();
            FCAElementImplementation targetObject = new FCAElementImplementation(cernatoObject.getName());
            /// @todo the memo fields could be mapped into descriptions
            result.add(targetObject);
            objectMap.put(cernatoObject, targetObject);
        }
        for (Iterator objIt = objects.iterator(); objIt.hasNext();) {
            CernatoObject cernatoObject = (CernatoObject) objIt.next();
            for (Iterator propIt = properties.iterator(); propIt.hasNext();) {
                Property property = (Property) propIt.next();
                org.tockit.cernatoXML.model.Value value = 
                    cernatoContext.getRelationship(cernatoObject, property);

                FCAElement targetObject = objectMap.get(cernatoObject);
                ManyValuedAttribute attribute = attributeMap.get(property);
                if(value instanceof NumericalValue) {
                    NumericalValue numValue = (NumericalValue) value;
                    result.setRelationship(targetObject, attribute, new DecimalValue(numValue.getValue()));
                } else if(value instanceof TextualValue) {
                    TextualValue textValue = (TextualValue) value;
                    result.setRelationship(targetObject, attribute, new StringValue(textValue.getDisplayString()));
                } else if(value != null) {
                    throw new RuntimeException("Unknown Cernato value for relationship between " +
                    		"object '" + cernatoObject.getName() + "' and " +
                    		"property '" + property.getName() + "'");
                }
            }
        }
        return result;
    }

    private void importBurmeister() {
        final JFileChooser openDialog;
        if (this.lastBurmeisterFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.lastBurmeisterFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setMultiSelectionEnabled(true);
        // create the options panel to be used in the file chooser 
        JRadioButton keepSchemaButton =
            new JRadioButton("Extend existing schema");
        keepSchemaButton.setSelected(true);
        JRadioButton newSchemaButton = new JRadioButton("Create new schema");
        ButtonGroup schemaOptionGroup = new ButtonGroup();
        schemaOptionGroup.add(keepSchemaButton);
        schemaOptionGroup.add(newSchemaButton);
        JPanel schemaOptionPanel = new JPanel(new GridBagLayout());
        schemaOptionPanel.add(
            keepSchemaButton,
            new GridBagConstraints(
                0,
                0,
                1,
                1,
                1,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5),
                2,
                2));
        schemaOptionPanel.add(
            newSchemaButton,
            new GridBagConstraints(
                0,
                1,
                1,
                1,
                1,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5),
                2,
                2));
        schemaOptionPanel.add(
            new JPanel(),
            new GridBagConstraints(
                0,
                2,
                1,
                1,
                1,
                1,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5),
                2,
                2));

        openDialog.setAccessory(schemaOptionPanel);
        openDialog.setFileFilter(
            new ExtensionFileFilter(new String[] { "cxt" }, "Context Files"));
        openDialog.setApproveButtonText("Import");
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        if (newSchemaButton.isSelected()) {
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
            this.currentFile = null;
            updateWindowTitle();
        }
        File[] files = openDialog.getSelectedFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            importBurmeister(file);
        }
    }

    private void importBurmeister(File file) {
        this.lastBurmeisterFile = file;
        Context context;
        try {
            context = BurmeisterParser.importBurmeisterFile(file);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
        addDiagram(
        	this.conceptualSchema,
            context,
            context.getName(),
            new DefaultDimensionStrategy());
    }

    private void importObjectAttributeList() {
        final JFileChooser openDialog;
        if (this.lastOALFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.lastOALFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setMultiSelectionEnabled(true);
        // create the options panel to be used in the file chooser 
        JRadioButton keepSchemaButton =
            new JRadioButton("Extend existing schema");
        keepSchemaButton.setSelected(true);
        JRadioButton newSchemaButton = new JRadioButton("Create new schema");
        ButtonGroup schemaOptionGroup = new ButtonGroup();
        schemaOptionGroup.add(keepSchemaButton);
        schemaOptionGroup.add(newSchemaButton);
        JPanel schemaOptionPanel = new JPanel(new GridBagLayout());
        schemaOptionPanel.add(
            keepSchemaButton,
            new GridBagConstraints(
                0,
                0,
                1,
                1,
                1,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(5, 5, 5, 5),
                2,
                2));
        schemaOptionPanel.add(
            newSchemaButton,
            new GridBagConstraints(
                0,
                1,
                1,
                1,
                1,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(0, 5, 5, 5),
                2,
                2));
        schemaOptionPanel.add(
            new JPanel(),
            new GridBagConstraints(
                0,
                2,
                1,
                1,
                1,
                1,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.BOTH,
                new Insets(0, 5, 5, 5),
                2,
                2));

        openDialog.setAccessory(schemaOptionPanel);
        openDialog.setFileFilter(
            new ExtensionFileFilter(new String[] { "oal" }, "Object Attribute Lists"));
        openDialog.setApproveButtonText("Import");
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        if (newSchemaButton.isSelected()) {
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
            this.currentFile = null;
            updateWindowTitle();
        }
        File[] files = openDialog.getSelectedFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            importObjectAttributeList(file);
        }
    }

    private void importObjectAttributeList(File file) {
        this.lastOALFile = file;
        ContextImplementation context;
        try {
            context = ObjectAttributeListParser.importOALFile(file);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
        addDiagram(
        	this.conceptualSchema,
            context,
            context.getName(),
            new DefaultDimensionStrategy());
    }

    private void importCSC() {
        final JFileChooser openDialog;
        if (this.lastCSCFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.lastCSCFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setApproveButtonText("Import");
        openDialog.setFileFilter(
            new ExtensionFileFilter(new String[] { "csc" }, "Conscript Files"));
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            importCSC(openDialog.getSelectedFile());
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Import failed");
        }
    }

    private void importCSC(File file) {
        this.lastCSCFile = file;
        try {
            new CSCParser().importCSCFile(file, this.conceptualSchema);
        } catch (org.tockit.conscript.parser.DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
        catch (Exception e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        }
        catch (Error e) {
            ErrorDialog.showError(this, e, "Could not parse file", "Could not parse CSC file");
            return;
        }
    }

    private void addDiagrams(ConceptualSchema schema, CernatoModel cernatoModel) {
        Vector views = cernatoModel.getViews();
        for (Iterator iterator = views.iterator(); iterator.hasNext();) {
            View view = (View) iterator.next();
            ViewContext viewContext = new ViewContext(cernatoModel.getContext(), view);
            Context mappedContext = new ContextImplementation();
            BinaryRelationImplementation mappedRelation = 
                (BinaryRelationImplementation) mappedContext.getRelation();
            Map<CernatoObject, FCAElement> objectMap = new Hashtable<CernatoObject, FCAElement>();
            for (Iterator objIt = viewContext.getObjects().iterator(); objIt.hasNext();) {
                CernatoObject object = (CernatoObject) objIt.next();
                FCAElement newObject = new FCAElementImplementation(object.getName());
                mappedContext.getObjects().add(newObject);
                objectMap.put(object, newObject);
            }
            for (Iterator attrIt = viewContext.getAttributes().iterator(); attrIt.hasNext();) {
                Criterion criterion = (Criterion) attrIt.next();
                FCAElement newAttribute = new FCAElementImplementation(criterion);
                mappedContext.getAttributes().add(newAttribute);
                for (Iterator objIt = viewContext.getObjects().iterator(); objIt.hasNext();) {
                    CernatoObject object = (CernatoObject) objIt.next();
                    if(viewContext.getRelation().contains(object, criterion)) {
                        mappedRelation.insert(objectMap.get(object), newAttribute);
                    }
                }
            }
            addDiagram(
                schema,
                mappedContext,
                view.getName(),
                new CernatoDimensionStrategy());
        }
    }

    private void addDiagram(ConceptualSchema schema, Context context, String name,
                            DimensionCreationStrategy dimensionStrategy) {
        try {
	        LatticeGenerator lgen = new GantersAlgorithm();
            Lattice lattice = lgen.createLattice(context);
            Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, name, dimensionStrategy);
            schema.addDiagram(diagram);
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Diagram creation failed", 
                                    "Could not create diagram \"" + name + "\"");
        }
    }

    public EventBroker getEventBroker() {
        return this.eventBroker;
    }

    public void closeMainPanel() {
    	/// @todo this is copy and paste from Elba. A lot of this stuff should go into a better MainPanel framework.
		boolean closeOk = checkForMissingSave();
		if (!closeOk) {
			return;
		}
        // store file locations
        preferences.putStringList("mruFiles", this.mruList);
        if(this.lastCernatoFile != null) {
            preferences.put("lastCernatoImport", 
                            this.lastCernatoFile.getAbsolutePath());
        }
        if(this.lastBurmeisterFile!= null) {
            preferences.put("lastBurmeisterImport", 
                            this.lastBurmeisterFile.getAbsolutePath());
        }
        if(this.lastOALFile!= null) {
            preferences.put("lastOALImport", 
                            this.lastOALFile.getAbsolutePath());
        }
        if(this.lastCSCFile!= null) {
            preferences.put("lastCSCImport", 
                            this.lastCSCFile.getAbsolutePath());
        }
        preferences.putBoolean("temporalControlsEnabled", 
                               this.temporalControls.isVisible());
        // store current position
        preferences.storeWindowPlacement(this);
        preferences.putInt("diagramViewDivider",
        		this.diagramEditingView.getDividerLocation());
        
        System.exit(0);
    }

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent schemaEvent =
                (ConceptualSchemaChangeEvent) e;
            this.conceptualSchema = schemaEvent.getConceptualSchema();
        }
        if (e instanceof ConceptualSchemaLoadedEvent) {
            ConceptualSchemaLoadedEvent loadEvent =
                (ConceptualSchemaLoadedEvent) e;
            if(this.conceptualSchema.getManyValuedContext() == null) {
                this.conceptualSchema.setManyValuedContext(createManyValuedContextFromDiagrams());
            }
            this.rowHeader.setManyValuedContext(this.conceptualSchema.getManyValuedContext());
            this.tableView.setManyValuedContext(this.conceptualSchema.getManyValuedContext());
            setCurrentFile(loadEvent.getFile());
        } else if (e instanceof NewConceptualSchemaEvent) {
            setCurrentFile(null);
        }
        this.exportDiagramAction.setEnabled(
            (this.diagramEditingView.getDiagramView().getDiagram() != null)
                && (this.diagramExportSettings != null));
        this.printMenuItem.setEnabled(this.diagramEditingView.getDiagramView().getDiagram() != null);
    }

	/**
	 * @todo this has to be moved into the base class we still don't have.
	 */
    private void updateWindowTitle() {
        // get the current filename without the extension and full path
        if (this.currentFile != null) {
            String filename =
                this.currentFile.getName().substring(0,this.currentFile.getName().length() - 4);
            setTitle(filename + " - " + WINDOW_TITLE);
        } else {
            setTitle(WINDOW_TITLE);
        }
    }

    private void recreateMruMenu() {
        if (this.mruMenu == null) { // no menu yet
            return;
        }
        this.mruMenu.removeAll();
        boolean empty = true;
        // will be used to check if we have at least one entry
        if (this.mruList.size() > 0) {
            ListIterator<String> it = this.mruList.listIterator(this.mruList.size());
            while (it.hasPrevious()) {
                String cur = it.previous();
                if (this.currentFile != null && 
                        cur.equals(this.currentFile.getAbsolutePath())) {
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

    private void openSchemaFile(File schemaFile) {
        try {
            this.conceptualSchema = CSXParser.parse(this.eventBroker, schemaFile);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not find file",
                e.getMessage());
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        } catch (IOException e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not open file",
                e.getMessage());
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        } catch (DataFormatException e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not read file",
                e.getMessage());
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        } catch (Exception e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not open file",
                e.getMessage());
            e.printStackTrace();
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        }
    }

    private WritableManyValuedContext createManyValuedContextFromDiagrams() {
        ManyValuedContextImplementation mvContext = new ManyValuedContextImplementation();
        StringType type = StringType.createEnumerationRestrictedType("single valued", new String[]{"X"});
        mvContext.add(type);
        for (Iterator<Diagram2D> iter = this.conceptualSchema.getDiagramsIterator(); iter.hasNext(); ) {
            Diagram2D diagram = iter.next();
            Context svContext = DiagramToContextConverter.getContext(diagram);
            BinaryRelation relation = svContext.getRelation();
            HashMap<FCAElement, ManyValuedAttribute> attributeMap = new HashMap<FCAElement, ManyValuedAttribute>(); 
            for (Iterator<Object> attrIt = svContext.getAttributes().iterator(); attrIt.hasNext(); ) {
                FCAElement attribute = (FCAElement) attrIt.next();
                ManyValuedAttribute mvAttribute = new ManyValuedAttributeImplementation(type,attribute.getData().toString());
                mvContext.add(mvAttribute);
                attributeMap.put(attribute, mvAttribute);
            }
            for (Iterator<Object> objIt = svContext.getObjects().iterator(); objIt.hasNext(); ) {
                FCAElement object = (FCAElement) objIt.next();
                mvContext.add(object);
                for (Iterator<Object> attrIt = svContext.getAttributes().iterator(); attrIt.hasNext(); ) {
                    FCAElement attribute = (FCAElement) attrIt.next();
                    if(relation.contains(object, attribute)) {
                        mvContext.setRelationship(object, attributeMap.get(attribute), new StringValue("X"));
                    } else {
                        mvContext.setRelationship(object, attributeMap.get(attribute), null);
                    }
                }
            }
        }
        return mvContext;
    }

    protected boolean checkForMissingSave() throws HeadlessException {
        boolean closeOk;
        if (!this.conceptualSchema.isDataSaved()) {
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

    private void saveFile() {
    	ensureObjectSetConsistency();
    	 
        if (this.currentFile == null) {
            this.saveAsFileAction.saveFile();
        } else {
            try {
            	this.saveActivity.processFile(this.currentFile);
                this.conceptualSchema.dataSaved();
            } catch (Exception e) {
                ErrorDialog.showError(this, e, "Saving file failed");
            }
        }
    }
	
	/**
	 * @todo this is only an intermediate hack, it will be superflous once everything gets mapped 
	 *       into the main context
	 */
	private void ensureObjectSetConsistency() {
		Set<Object> allObjects = new HashSet<Object>();
		for (Iterator<Diagram2D> diagIt = this.conceptualSchema.getDiagramsIterator(); diagIt.hasNext();) {
            Diagram2D diagram = diagIt.next();
            Concept concept = diagram.getTopConcept();
            for (Iterator concIt = concept.getExtentIterator(); concIt.hasNext();) {
                Object object = concIt.next();
                allObjects.add(object);
            }
        }
        
		for (Iterator<Diagram2D> diagIt = this.conceptualSchema.getDiagramsIterator(); diagIt.hasNext();) {
			NDimDiagram diagram = (NDimDiagram) diagIt.next();
			
			ConceptImplementation concept = (ConceptImplementation) diagram.getTopConcept();
			if(concept.getExtentSize() == allObjects.size()) {
				continue;			
			}
			
			Set<Object> difference = new HashSet<Object>(allObjects);
			for (Iterator extIt = concept.getExtentIterator(); extIt.hasNext();) {
                Object object = extIt.next();
                difference.remove(object);
            }
            
			if(concept.getAttributeContingentSize() != 0) {
				DiagramNode topNode = diagram.getNodeForConcept(concept);
				concept = new ConceptImplementation();
				ConceptImplementation oldTopConcept = (ConceptImplementation) topNode.getConcept();
                concept.addSubConcept(oldTopConcept);
				oldTopConcept.addSuperConcept(concept);
				concept.buildClosures();
				
				diagram.getBase().add(new Point2D.Double(0,diagram.getBounds().getHeight()/10));
				DiagramNode newTop = new NDimDiagramNode(diagram,"new top", new double[diagram.getBase().size()], concept, 
				                                         null, new LabelInfo(),null);
				                                         
				for (Iterator<DiagramNode> nodeIt = diagram.getNodes(); nodeIt.hasNext();) {
					NDimDiagramNode node = (NDimDiagramNode) nodeIt.next();
                    double[] newPos = new double[node.getNdimVector().length + 1];
                    for (int i = 0; i < node.getNdimVector().length; i++) {
                        newPos[i] = node.getNdimVector()[i];
                    }
                    newPos[newPos.length - 1] = 1;
                    node.setNdimVector(newPos);
                    ConceptImplementation curConcept = (ConceptImplementation) node.getConcept(); 
                    curConcept.buildClosures();
                }
                
				diagram.addNode(newTop);
				diagram.addLine(newTop, topNode);
			}
			
			for (Iterator<Object> diffIt = difference.iterator(); diffIt.hasNext();) {
                FCAElement object = (FCAElement) diffIt.next();
                concept.addObject(object);
            }
		}
		DiagramView diagramView = this.diagramEditingView.getDiagramView();
        diagramView.showDiagram(diagramView.getDiagram());
    }

    protected void showNumericInputDialog(WritableManyValuedAttribute attribute,
												WritableFCAElement obj) {
		WritableManyValuedContext context = this.conceptualSchema.getManyValuedContext();
        Value relationship = context.getRelationship(obj,attribute);
        String content;
        if(relationship != null) {
			content = relationship.toString();
        } else {
        	content = "";
        }
		String value = (String) JOptionPane.showInputDialog(this,"Enter Value","Edit Value",
																JOptionPane.PLAIN_MESSAGE,null,null,
																content);
		if(value!=null){
			try{
				double val = Double.parseDouble(value);
				DecimalValue decimalValue = new DecimalValue(val);
				context.setRelationship(obj,attribute,decimalValue);
				validate();
			}catch(NumberFormatException e){
				JOptionPane.showMessageDialog(this,
							"Enter numbers only.",
							"Warning",
							JOptionPane.WARNING_MESSAGE);
				showNumericInputDialog(attribute,obj);
			}
		}
	}
	
    private void setCurrentFile(File newCurrentFile) {
        this.currentFile = newCurrentFile;
        if(newCurrentFile != null) {
            this.saveAsFileAction.setPreviousFile(newCurrentFile);
            String filePath = this.currentFile.getAbsolutePath();
            if (this.mruList.contains(filePath)) {
                // if it is already in, just remove it and add it at the end
                this.mruList.remove(filePath);
            }
            this.mruList.add(filePath);
            if (this.mruList.size() > MaxMruFiles) {
                this.mruList.remove(0);
            }
        }
        recreateMruMenu();
        updateWindowTitle();
    }

    /**
     * Prints the diagram using the current settings.
     *
     * If we don't have a diagram at the moment we just return.
     */
    protected void printDiagram() {
        if (this.diagramEditingView.getDiagramView().getDiagram() != null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob.printDialog()) {
                try {
                    printJob.setPrintable(this.diagramEditingView.getDiagramView(), this.pageFormat);
                    printJob.print();
                } catch (Exception e) {
                    ErrorDialog.showError(this, e, "Printing failed");
                }
            }
        }
    }
}
