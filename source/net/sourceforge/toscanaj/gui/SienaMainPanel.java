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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.*;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.controller.cernato.CernatoDimensionStrategy;
import net.sourceforge.toscanaj.controller.diagram.ObjectEditingLabelViewPopupMenuHandler;
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
import net.sourceforge.toscanaj.model.DiagramExportSettings;
import net.sourceforge.toscanaj.model.cernato.CernatoModel;
import net.sourceforge.toscanaj.model.context.Context;
import net.sourceforge.toscanaj.model.context.ContextImplementation;
import net.sourceforge.toscanaj.model.context.FCAObject;
import net.sourceforge.toscanaj.model.context.FCAObjectImplementation;
import net.sourceforge.toscanaj.model.context.WritableFCAObject;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.AttributeValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedAttributeImplementation;
import net.sourceforge.toscanaj.model.manyvaluedcontext.ManyValuedContextImplementation;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedAttribute;
import net.sourceforge.toscanaj.model.manyvaluedcontext.WritableManyValuedContext;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.NumericalValue;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.TextualType;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.View;
import net.sourceforge.toscanaj.model.manyvaluedcontext.types.ViewContext;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagramNode;
import net.sourceforge.toscanaj.parser.BurmeisterParser;
import net.sourceforge.toscanaj.parser.CSCParser;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.CernatoXMLParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;
import net.sourceforge.toscanaj.view.manyvaluedcontext.ColumnHeader;
import net.sourceforge.toscanaj.view.manyvaluedcontext.ObjectDialog;
import net.sourceforge.toscanaj.view.manyvaluedcontext.RowHeader;
import net.sourceforge.toscanaj.view.manyvaluedcontext.TableView;

import org.jdom.JDOMException;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.ExtendedPreferences;

/**
 * @todo make sure all changes to the context will propagate to make the schema dirty.
 */
public class SienaMainPanel extends JFrame implements MainPanel, EventBrokerListener {
    private static final int NUMBER_OF_VALUE_POPUP_MENU_ROWS = 15;
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
    private List mruList = new LinkedList();
    private String currentFile = null;
    private DiagramExportSettings diagramExportSettings;
    private ExportDiagramAction exportDiagramAction;
    private File lastCSCFile;
    private File lastBurmeisterFile;
    private File lastCernatoFile;
    private SaveFileAction saveAsFileAction;
    private SaveConceptualSchemaActivity saveActivity;

	private TableView tableView;
	private RowHeader rowHeader;
	private ColumnHeader colHeader;
    private JLabel temporalControlsLabel;
    /**
	 * @todo this class is superflous, it should be replaced by putting the calculation into the
	 * TableView class.
	 */
	protected class Point{
		
		private int row;
		private int col;
		
		public Point(double x, double y){
			row = (int) x / TableView.CELL_WIDTH;
			col = (int) y / TableView.CELL_HEIGHT;
		}
		
		public int getRow(){
			return row;
		}
		public int getCol(){
			return col;
		}
	}
	
	public SienaMainPanel() {
        super(WINDOW_TITLE);

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);

        // register all image writers we want to support
        ToscanaJMainPanel.registerImageWriters();

        Iterator it = GraphicFormatRegistry.getIterator();
        if (it.hasNext()) {
            this.diagramExportSettings = new DiagramExportSettings();
        }

        eventBroker.subscribe(
            this,
            NewConceptualSchemaEvent.class,
            Object.class);
            
        initializeModel();

        createViews();

        createMenuBar();

        mruList = preferences.getStringList("mruFiles");

        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile =
                new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }
        
        this.lastCernatoFile = new File(preferences.get("lastCernatoImport", ""));
        this.lastCSCFile = new File(preferences.get("lastCSCImport",""));
        this.lastBurmeisterFile = new File(preferences.get("lastBurmeisterImport",""));

		this.setVisible(true);
        preferences.restoreWindowPlacement(this, new Rectangle(10, 10, 900, 700));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	}

	private void initializeModel() {
		this.conceptualSchema.setManyValuedContext(new ManyValuedContextImplementation());
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
        this.diagramEditingView = new DiagramEditingView(this, conceptualSchema, eventBroker);
        this.temporalControlsLabel = new JLabel("Temporal Concept Analysis:");
        this.temporalControls = new TemporalControlsPanel(
                                            this.diagramEditingView.getDiagramView(),
                                            diagramExportSettings,
                                            eventBroker);
        boolean temporalControlsEnabled = preferences.getBoolean("temporalControlsEnabled", false); 
        this.temporalControlsLabel.setVisible(temporalControlsEnabled);
        this.temporalControls.setVisible(temporalControlsEnabled);                                    
        this.diagramEditingView.addAccessory(temporalControlsLabel);
        this.diagramEditingView.addAccessory(temporalControls);
		this.diagramEditingView.getDiagramView().getController().getEventBroker().subscribe(
										this, DisplayedDiagramChangedEvent.class, Object.class);
		DiagramView diagramView = diagramEditingView.getDiagramView();
		diagramView.getController().getEventBroker().subscribe(
			new ObjectEditingLabelViewPopupMenuHandler(
				diagramView,
				eventBroker),
			CanvasItemContextMenuRequestEvent.class,
			ObjectLabelView.getFactory().getLabelClass());
		this.diagramEditingView.setDividerLocation(preferences.getInt("diagramViewDivider", 200));
	}

	/**
	 * @todo this method is inconsistent with createDiagramEditingView() in terms of return value.
	 */
	protected Component createContextEditingView() {
		rowHeader = new RowHeader(this.conceptualSchema.getManyValuedContext());
		colHeader = new ColumnHeader(this.conceptualSchema.getManyValuedContext());
		tableView = new TableView(this.conceptualSchema.getManyValuedContext());
		tableView.addMouseListener(getTableViewMouseListener());
				
		colHeader.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					double x = e.getPoint().getX();
					double y = e.getPoint().getY();
					int row = new Point(x,y).getRow();
                    editAttribute(row);
				}
			}
		});

		rowHeader.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
					double x = e.getPoint().getX();
					double y = e.getPoint().getY();
					Point p = new Point(x,y);
					int col = p.getCol();
                    editObject(col);
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(tableView);
		scrollPane.setColumnHeaderView(colHeader);
		scrollPane.setRowHeaderView(rowHeader);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel retVal = new JPanel(new BorderLayout());
		retVal.add(createContextToolbar(), BorderLayout.NORTH);
		retVal.add(scrollPane, BorderLayout.CENTER);
		return retVal;
	}

	private void editObject(int row) {
		Frame tFrame = JOptionPane.getFrameForComponent(tableView);
		ArrayList objectList = (ArrayList) conceptualSchema.getManyValuedContext().getObjects();
		WritableFCAObject object = (WritableFCAObject) objectList.get(row);
		ObjectDialog objectDialog = new ObjectDialog(tFrame, object);
		objectDialog.show();
		this.tableView.updateSize();
		this.rowHeader.updateSize();
	}

	private void editAttribute(int column) {
		this.tableView.updateSize();
		this.colHeader.updateSize();
	}

	private Component createContextToolbar() {
		JPanel retVal = new JPanel(new FlowLayout(FlowLayout.LEFT));
		final JButton addObjectButton = new JButton("Add object...");
		addObjectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	WritableManyValuedContext manyValuedContext = conceptualSchema.getManyValuedContext();
                manyValuedContext.add(new FCAObjectImplementation(""));
            	editObject(manyValuedContext.getObjects().size() - 1);
            }
        });

		final JButton addAttributeButton = new JButton("Add attribute...");
		addAttributeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WritableManyValuedContext manyValuedContext = conceptualSchema.getManyValuedContext();
				AttributeType firstType = (AttributeType) manyValuedContext.getTypes().iterator().next();
				manyValuedContext.add(new ManyValuedAttributeImplementation(firstType,""));
				editAttribute(manyValuedContext.getAttributes().size() - 1);
			}
		});

		final JButton createDiagramButton = new JButton("Create Diagram...");
		createDiagramButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(createDiagramButton,"Not yet implemented");
			}
		});
		
		retVal.add(addObjectButton);
		retVal.add(addAttributeButton);
		retVal.add(createDiagramButton);

        return retVal;
    }

    protected MouseListener getTableViewMouseListener() {
		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				double x = e.getPoint().getX();
				double y = e.getPoint().getY();
				Point p = new Point(x,y);
				tableView.setSelectedCell(new TableView.SelectedCell(p.getCol(), p.getRow()));
				
				if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1){
					WritableManyValuedContext context = conceptualSchema.getManyValuedContext();
                    ArrayList propertyList = (ArrayList)context.getAttributes();
					WritableManyValuedAttribute attribute = (WritableManyValuedAttribute)
															propertyList.get(p.getRow());
					ArrayList objectList = (ArrayList) context.getObjects();
					WritableFCAObject obj = (WritableFCAObject)objectList.get(p.getCol());
					
					if(attribute.getType() instanceof TextualType){
						showPopupMenu(x + tableView.getX(), y + tableView.getY(), attribute, obj);
					}
					else {
						showNumericInputDialog(attribute, obj);
					}
					tableView.repaint();
				}
			}
		};
		return mouseListener;
	}

    public void createMenuBar() {

        // --- menu bar ---
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // --- file menu ---
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

        SimpleActivity testSchemaSavedActivity = new SimpleActivity() {
            public boolean doActivity() throws Exception {
                return checkForMissingSave();
            }
        };

        NewConceptualSchemaActivity newSchemaActivity =
            new NewConceptualSchemaActivity(eventBroker);
        newSchemaActivity.setPostNewActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                currentFile = null;
                updateWindowTitle();
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
        fileMenu.add(newMenuItem);

        LoadConceptualSchemaActivity loadSchemaActivity =
            new LoadConceptualSchemaActivity(eventBroker);
        /// @todo add dirty flag support as Elba has
        loadSchemaActivity.setTestOpenOkActivity(testSchemaSavedActivity);
        OpenFileAction openFileAction =
            new OpenFileAction(
                this,
                loadSchemaActivity,
                currentFile,
                KeyEvent.VK_O,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openFileAction.addPostOpenActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                updateWindowTitle();
                return true;
            }
        });
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(openFileAction);
        fileMenu.add(openMenuItem);

        mruMenu = new JMenu("Reopen");
        mruMenu.setMnemonic(KeyEvent.VK_R);
        recreateMruMenu();
        fileMenu.add(mruMenu);

        JMenuItem saveMenuItem = new JMenuItem("Save...");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        saveActivity =
            new SaveConceptualSchemaActivity(conceptualSchema, eventBroker);
        this.saveAsFileAction =
            new SaveFileAction(
                this,
                saveActivity,
                KeyEvent.VK_S,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveAsFileAction.setPostSaveActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                currentFile = saveAsFileAction.getLastFileUsed().getPath();
                addFileToMRUList(saveAsFileAction.getLastFileUsed());
                conceptualSchema.dataSaved();
                updateWindowTitle();
                return true;
            }
        });

        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
        saveAsMenuItem.addActionListener(saveAsFileAction);
        fileMenu.add(saveAsMenuItem);

        fileMenu.addSeparator();

        JMenuItem importCernatoXMLItem = new JMenuItem("Import Cernato XML...");
        importCernatoXMLItem.setMnemonic(KeyEvent.VK_C);
        importCernatoXMLItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importCernatoXML();
            }
        });
        fileMenu.add(importCernatoXMLItem);

        JMenuItem importBurmeisterItem =
            new JMenuItem("Import Burmeister Format...");
        importBurmeisterItem.setMnemonic(KeyEvent.VK_B);
        importBurmeisterItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importBurmeister();
            }
        });
        fileMenu.add(importBurmeisterItem);

        JMenuItem importCSCMenuItem = new JMenuItem("Import CSC File...");
        importCSCMenuItem.setMnemonic(KeyEvent.VK_I);
        importCSCMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importCSC();
            }
        });
        fileMenu.add(importCSCMenuItem);

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
        fileMenu.add(exitMenuItem);

        final DiagramView diagramView =
            this.diagramEditingView.getDiagramView();

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

        viewMenu.addSeparator();
        
        final JCheckBoxMenuItem showTemporalControls =
            new JCheckBoxMenuItem("Show Temporal Controls");
        showTemporalControls.setMnemonic(KeyEvent.VK_T);
        showTemporalControls.setSelected(preferences.getBoolean("temporalControlsEnabled", false));
        showTemporalControls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean newState = !temporalControls.isVisible();
                temporalControlsLabel.setVisible(newState);
                temporalControls.setVisible(newState);
            }
        });
        viewMenu.add(showTemporalControls);

        menuBar.add(viewMenu);

        // --- help menu ---
        // create a help menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        final JFrame parent = this;
        JMenuItem aboutItem = new JMenuItem("About Siena...");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ToscanaJMainPanel.showAboutDialog(parent);
            }
        });
        helpMenu.add(aboutItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
    }
    private void importCernatoXML() {
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

    public void importCernatoXML(File file) {
        this.lastCernatoFile = file;
        CernatoModel inputModel;
        try {
            inputModel = CernatoXMLParser.importCernatoXMLFile(file);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (IOException e) {
            ErrorDialog.showError(this, e, "Could not read file");
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
		this.tableView.setManyValuedContext(inputModel.getContext());
		this.rowHeader.setManyValuedContext(inputModel.getContext());
		this.colHeader.setManyValuedContext(inputModel.getContext());
        
        this.currentFile = null;
        String filename =
            file.getName().substring(0, file.getName().lastIndexOf('.'));
        setTitle(filename + " (Cernato import, unsaved) - " + WINDOW_TITLE);
		validate();
		repaint();
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
			currentFile = null;
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
        addDiagram(
            conceptualSchema,
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
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file '" + file.getAbsolutePath() + "'");
            return;
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file '" + file.getAbsolutePath() + "'");
            return;
        }
    }

    private void addDiagrams(
        ConceptualSchema schema,
        CernatoModel cernatoModel) {
        Vector views = cernatoModel.getViews();
        for (Iterator iterator = views.iterator(); iterator.hasNext();) {
            View view = (View) iterator.next();
            addDiagram(
                schema,
                new ViewContext(cernatoModel.getContext(), view),
                view.getName(),
                new CernatoDimensionStrategy());
        }
    }

    private void addDiagram(
        ConceptualSchema schema,
        Context context,
        String name,
        DimensionCreationStrategy dimensionStrategy) {
        LatticeGenerator lgen = new GantersAlgorithm();
        Lattice lattice = lgen.createLattice(context);
        Diagram2D diagram =
            NDimLayoutOperations.createDiagram(
                lattice,
                name,
                dimensionStrategy);
        schema.addDiagram(diagram);
    }

    public EventBroker getEventBroker() {
        return eventBroker;
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
        if(this.lastCSCFile!= null) {
            preferences.put("lastCSCImport", 
                            this.lastCSCFile.getAbsolutePath());
        }
        preferences.putBoolean("temporalControlsEnabled", 
                               this.temporalControls.isVisible());
        // store current position
        preferences.storeWindowPlacement(this);
        preferences.putInt("diagramViewDivider",
                           diagramEditingView.getDividerLocation());
        System.exit(0);
    }

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent schemaEvent =
                (ConceptualSchemaChangeEvent) e;
            conceptualSchema = schemaEvent.getConceptualSchema();
        }
        if (e instanceof ConceptualSchemaLoadedEvent) {
            ConceptualSchemaLoadedEvent loadEvent =
                (ConceptualSchemaLoadedEvent) e;
            File schemaFile = loadEvent.getFile();
            addFileToMRUList(schemaFile);
        }
        this.exportDiagramAction.setEnabled(
            (this.diagramEditingView.getDiagramView().getDiagram() != null)
                && (this.diagramExportSettings != null));
    }

	/**
	 * @todo this has to be moved into the base class we still don't have.
	 */
    private void updateWindowTitle() {
        // get the current filename without the extension and full path
        if (currentFile != null) {
            String filename =
                currentFile.substring(
                    currentFile.lastIndexOf(File.separator) + 1,
                    (currentFile.length() - 4));
            setTitle(filename + " - " + WINDOW_TITLE);
        } else {
            setTitle(WINDOW_TITLE);
        }
    }

    private void recreateMruMenu() {
        if (mruMenu == null) { // no menu yet
            return;
        }
        this.mruMenu.removeAll();
        boolean empty = true;
        // will be used to check if we have at least one entry
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

    private void openSchemaFile(File schemaFile) {
        try {
            conceptualSchema = CSXParser.parse(eventBroker, schemaFile);
            setTitle(
                schemaFile.getName().substring(
                    0,
                    ((schemaFile.getName()).length() - 4))
                    + " - "
                    + WINDOW_TITLE);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not find file",
                e.getMessage());
            conceptualSchema = new ConceptualSchema(eventBroker);
        } catch (IOException e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not open file",
                e.getMessage());
            conceptualSchema = new ConceptualSchema(eventBroker);
        } catch (DataFormatException e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not read file",
                e.getMessage());
            conceptualSchema = new ConceptualSchema(eventBroker);
        } catch (Exception e) {
            ErrorDialog.showError(
                this,
                e,
                "Could not open file",
                e.getMessage());
            e.printStackTrace();
            conceptualSchema = new ConceptualSchema(eventBroker);
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

    private void saveFile() {
    	ensureObjectSetConsistency();
    	 
        if (this.currentFile == null) {
            this.saveAsFileAction.saveFile();
        } else {
            try {
                saveActivity.processFile(new File(this.currentFile));
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
		Set allObjects = new HashSet();
		for (Iterator diagIt = this.conceptualSchema.getDiagramsIterator(); diagIt.hasNext();) {
            Diagram2D diagram = (Diagram2D) diagIt.next();
            Concept concept = diagram.getTopConcept();
            for (Iterator concIt = concept.getExtentIterator(); concIt.hasNext();) {
                Object object = concIt.next();
                allObjects.add(object);
            }
        }
        
		for (Iterator diagIt = this.conceptualSchema.getDiagramsIterator(); diagIt.hasNext();) {
			NDimDiagram diagram = (NDimDiagram) diagIt.next();
			
			ConceptImplementation concept = (ConceptImplementation) diagram.getTopConcept();
			if(concept.getExtentSize() == allObjects.size()) {
				continue;			
			}
			
			Set difference = new HashSet(allObjects);
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
				                                         
				for (Iterator nodeIt = diagram.getNodes(); nodeIt.hasNext();) {
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
			
			for (Iterator diffIt = difference.iterator(); diffIt.hasNext();) {
                Object object = (Object) diffIt.next();
                concept.addObject(object);
            }
		}
		DiagramView diagramView = this.diagramEditingView.getDiagramView();
        diagramView.showDiagram(diagramView.getDiagram());
    }

    protected void showNumericInputDialog(WritableManyValuedAttribute attribute,
												WritableFCAObject obj) {
		WritableManyValuedContext context = this.conceptualSchema.getManyValuedContext();
        AttributeValue relationship = context.getRelationship(obj,attribute);
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
				NumericalValue numericalValue = new NumericalValue(val);
				context.setRelationship(obj,attribute,numericalValue);
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
	
	protected void showPopupMenu(double xPos, double yPos, ManyValuedAttribute 
										property, FCAObject obj) {
		TextualType attributeType = (TextualType)property.getType();
		AttributeValue[] textualValueList = attributeType.getValueRange();
		JPopupMenu menu = new JPopupMenu();
		
		if(textualValueList.length<=NUMBER_OF_VALUE_POPUP_MENU_ROWS){
			menu = createPopupMenu(1,textualValueList.length,textualValueList,
														property,obj);
		} else {
			menu = createPopupMenu(textualValueList.length/NUMBER_OF_VALUE_POPUP_MENU_ROWS,NUMBER_OF_VALUE_POPUP_MENU_ROWS,
											textualValueList,property,obj );
		}
		menu.show(this,(int)xPos,(int)yPos);
	}

	protected JPopupMenu createPopupMenu(int numOfCol,int numOfRows, AttributeValue[] textualValueList,
											final ManyValuedAttribute property, 
												final FCAObject obj) {
		final WritableManyValuedContext context = this.conceptualSchema.getManyValuedContext();
		JPopupMenu menu = new JPopupMenu();
		menu.setLayout(new GridLayout(numOfRows ,numOfCol));
		for(int i = 0 ; i < textualValueList.length ; i++){
			final AttributeValue textualValue = (AttributeValue) textualValueList[i];
			JMenuItem menuItem = new JMenuItem(textualValue.getDisplayString());
			menuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					context.setRelationship(obj,property,textualValue);
					validate();
				}
			});
			menu.add(menuItem);
		}
		return menu;
	}
}
