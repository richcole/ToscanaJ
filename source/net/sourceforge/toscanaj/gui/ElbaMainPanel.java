/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.DataDump;
import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.DumpSqlScript;
import net.sourceforge.toscanaj.controller.db.WhereClauseGenerator;
import net.sourceforge.toscanaj.controller.diagram.SqlClauseEditingLabelViewPopupMenuHandler;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.action.CheckContextConsistencyAction;
import net.sourceforge.toscanaj.gui.action.ExportDiagramAction;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.CheckDuplicateFileChooser;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExportStatisticalDataSettingsPanel;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.DiagramExportSettings;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.DatabaseInfoChangedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.parser.CSCParser;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.database.DatabaseConnectionInformationView;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.DisplayedDiagramChangedEvent;
import net.sourceforge.toscanaj.view.diagram.SqlClauseLabelView;
import net.sourceforge.toscanaj.view.scales.AttributeListScaleGenerator;
import net.sourceforge.toscanaj.view.scales.ContextTableScaleGenerator;
import net.sourceforge.toscanaj.view.scales.CrossordinalScaleGenerator;
import net.sourceforge.toscanaj.view.scales.NominalScaleGenerator;
import net.sourceforge.toscanaj.view.scales.OrdinalScaleGenerator;
import net.sourceforge.toscanaj.view.scales.ScaleEditingViewDialog;
import net.sourceforge.toscanaj.view.scales.ScaleGenerator;

import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ElbaMainPanel
    extends JFrame
    implements MainPanel, EventBrokerListener {
    private static final String CONFIGURATION_SECTION_NAME = "ElbaMainPanel";
    private static final String WINDOW_TITLE = "Elba";
    static private final int MaxMruFiles = 8;

    /**
     *  Main Controllers
     */
    private EventBroker eventBroker;
    private DatabaseConnection databaseConnection;

    /**
     *  Model
     */
    private ConceptualSchema conceptualSchema;

    /**
     * Controls
     */
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu mruMenu;
    private JMenu helpMenu;

    private List mruList = new LinkedList();
    private String currentFile = null;

    /**
     * Views
     */
    private ScaleEditingViewDialog scaleEditingViewDialog;
    private DiagramEditingView diagramEditingView;
    private DatabaseConnectionInformationView connectionInformationView;
    private XMLEditorDialog schemaDescriptionView;
    private JMenuItem dumpSQLMenuItem;
	private JMenuItem dumpStatisticalDataMenuItem;
	private JMenuItem createOptimizedSystemMenuItem;
    private SaveFileAction saveAsFileAction;
    private List scaleGenerators;
    private JButton newDiagramButton;
    private JPanel toolbar;
    private SaveConceptualSchemaActivity saveActivity;
    private DiagramExportSettings diagramExportSettings;
    private ExportDiagramAction exportDiagramAction;
    private File lastCSCFile;

    private File lastExportFile;
    private CheckContextConsistencyAction checkContextAction;
    
	public ElbaMainPanel() {
        super("Elba");

        this.eventBroker = new EventBroker();
        this.conceptualSchema = new ConceptualSchema(eventBroker);
        this.databaseConnection = new DatabaseConnection(eventBroker);
        DatabaseConnection.setConnection(this.databaseConnection);

        // register all image writers we want to support
		ToscanaJMainPanel.registerImageWriters();

        Iterator it = GraphicFormatRegistry.getIterator();
        if (it.hasNext()) {
            this.diagramExportSettings = new DiagramExportSettings();
        }

        this.eventBroker.subscribe(
            this,
            ConceptualSchemaChangeEvent.class,
            Object.class);
        this.eventBroker.subscribe(
            this,
            DatabaseInfoChangedEvent.class,
            Object.class);

        fillScaleGeneratorList();

        createViews();
        // this has to happen before the menu gets created, since the menu uses the information
        DiagramView diagramView = this.diagramEditingView.getDiagramView();
        float minLabelFontSize =
            ConfigurationManager.fetchFloat(
                CONFIGURATION_SECTION_NAME,
                "minLabelFontSize",
                (float) diagramView.getMinimumFontSize());
        diagramView.setMinimumFontSize(minLabelFontSize);

        createMenuBar();

        mruList =
            ConfigurationManager.fetchStringList(
                CONFIGURATION_SECTION_NAME,
                "mruFiles",
                MaxMruFiles);
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile =
                new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }

        ConfigurationManager.restorePlacement(
            CONFIGURATION_SECTION_NAME,
            this,
            new Rectangle(10, 10, 1000, 700));

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

	/**
	 * @todo wondering if it would be simpler to use JToolBar instead of JPanel with
	 * GridBagLayout....
	 */
    public void createViews() {
        final JFrame frame = this;
        JPanel mainView = new JPanel(new GridBagLayout());
        scaleEditingViewDialog =
            new ScaleEditingViewDialog(
                frame,
                conceptualSchema,
                eventBroker,
                databaseConnection);
        connectionInformationView =
            new DatabaseConnectionInformationView(
                this,
                conceptualSchema,
                eventBroker);
        schemaDescriptionView = new XMLEditorDialog(this, "Schema description");
        toolbar = new JPanel(new GridBagLayout());
        newDiagramButton = new JButton("New Diagram...");
        newDiagramButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showScaleGeneratorMenu();
            }
        });

        JButton schemaDescriptionButton = new JButton("Schema Description...");
        schemaDescriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                schemaDescriptionView.setContent(
                    conceptualSchema.getDescription());
                schemaDescriptionView.show();
                conceptualSchema.setDescription(
                    schemaDescriptionView.getContent());
            }
        });

        JButton databaseConnectionButton =
            new JButton("Database Connection...");
        databaseConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDatabaseConnectionDialog();
            }
        });

        toolbar.add(
            newDiagramButton,
            new GridBagConstraints(
                0,
                0,
                1,
                1,
                0,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(5, 1, 1, 1),
                2,
                2));

        toolbar.add(
            schemaDescriptionButton,
            new GridBagConstraints(
                1,
                0,
                1,
                1,
                0,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(5, 1, 1, 1),
                2,
                2));

        toolbar.add(
            databaseConnectionButton,
            new GridBagConstraints(
                2,
                0,
                1,
                1,
                0,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.NONE,
                new Insets(5, 1, 1, 1),
                2,
                2));
        toolbar.add(
            new JPanel(),
            new GridBagConstraints(
                3,
                0,
                1,
                1,
                1,
                1,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(1, 1, 1, 1),
                2,
                2));

        diagramEditingView =
            new DiagramEditingView(conceptualSchema, eventBroker);
        diagramEditingView.setDividerLocation(
            ConfigurationManager.fetchInt(
                CONFIGURATION_SECTION_NAME,
                "diagramViewDivider",
                200));
        DiagramView diagramView = diagramEditingView.getDiagramView();
        diagramView.setObjectLabelFactory(SqlClauseLabelView.getFactory());

        diagramView.getController().getEventBroker().subscribe(
            this,
            DisplayedDiagramChangedEvent.class,
            Object.class);

        diagramView.getController().getEventBroker().subscribe(
            new SqlClauseEditingLabelViewPopupMenuHandler(
                diagramView,
                eventBroker),
            CanvasItemContextMenuRequestEvent.class,
            SqlClauseLabelView.getFactory().getLabelClass());

        mainView.add(
            toolbar,
            new GridBagConstraints(
                0,
                0,
                1,
                1,
                1.0,
                0,
                GridBagConstraints.NORTHWEST,
                GridBagConstraints.HORIZONTAL,
                new Insets(2, 2, 2, 2),
                2,
                2));
        mainView.add(
            diagramEditingView,
            new GridBagConstraints(
                0,
                1,
                1,
                1,
                1,
                1,
                GridBagConstraints.WEST,
                GridBagConstraints.BOTH,
                new Insets(2, 2, 2, 2),
                2,
                2));
        setContentPane(mainView);
    }

    public void showScaleGeneratorMenu() {
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem;
        final JFrame parent = this;

        Iterator it = this.scaleGenerators.iterator();
        while (it.hasNext()) {
            final ScaleGenerator generator = (ScaleGenerator) it.next();
            menuItem = new JMenuItem(generator.getScaleName());
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        Context context =
                            generator.generateScale(
                                conceptualSchema,
                                databaseConnection);
                        Diagram2D newDiagram = null;
                        Lattice lattice = null;
                        if (context != null) {
                            LatticeGenerator lgen = new GantersAlgorithm();
                            lattice = lgen.createLattice(context);
                            newDiagram =
                                NDimLayoutOperations.createDiagram(
                                    lattice,
                                    context.getName(),
                                    new DefaultDimensionStrategy());
                            if (null != newDiagram) {
                                Diagram2D diagramWithSameTitle = null;
                                int indexOfExistingDiagram = -1;
                                for (int i = 0;
                                    i < conceptualSchema.getNumberOfDiagrams();
                                    i++) {
                                    if (conceptualSchema
                                        .getDiagram(i)
                                        .getTitle()
                                        .equalsIgnoreCase(
                                            newDiagram.getTitle())) {
                                        diagramWithSameTitle =
                                            conceptualSchema.getDiagram(i);
                                        indexOfExistingDiagram = i;
                                    }
                                }
                                if (diagramWithSameTitle != null) {
                                    int rv = showTitleExistsDialog(newDiagram);
                                    if (rv == JOptionPane.OK_OPTION) {
                                        replaceTitle(
                                            newDiagram,
                                            diagramWithSameTitle,
                                            indexOfExistingDiagram);
                                    } else if (
                                        rv == JOptionPane.CANCEL_OPTION) {
                                        renameTitle(
                                            newDiagram,
                                            diagramWithSameTitle);
                                    }
                                } else {
                                    conceptualSchema.addDiagram(newDiagram);
                                    diagramEditingView
                                        .getDiagramView()
                                        .showDiagram(
                                        newDiagram);
                                }
                            }
                        }
                    } catch (Exception exc) {
                        ErrorDialog.showError(
                            parent,
                            exc,
                            "Scale generation failed");
                    }
                }
                private void replaceTitle(
                    Diagram2D returnValue,
                    Diagram2D diagramWithSameTitle,
                    int indexOfExistingDiagram) {
                    conceptualSchema.addDiagram(returnValue);
                    if (indexOfExistingDiagram != -1) {
                        conceptualSchema.exchangeDiagrams(
                            (conceptualSchema.getNumberOfDiagrams() - 1),
                            indexOfExistingDiagram);
                        conceptualSchema.removeDiagram(diagramWithSameTitle);
                    } else {
                        conceptualSchema.removeDiagram(diagramWithSameTitle);
                    }
                }
                private void renameTitle(
                    Diagram2D returnValue,
                    Diagram2D diagramWithSameTitle) {
                    String inputValue = "";
                    String currentValue = returnValue.getTitle();
                    do {
                        inputValue =
                            (String) JOptionPane.showInputDialog(
                                null,
                                "Enter title: ",
                                "Rename title",
                                JOptionPane.PLAIN_MESSAGE,
                                null,
                                null,
                                currentValue);
                        if (inputValue != null) {
                            inputValue = inputValue.trim();
                            currentValue = inputValue;
                        }
                    } while (
                        inputValue != null
                            && (inputValue.equals("")
                                || inputValue.equalsIgnoreCase(
                                    diagramWithSameTitle.getTitle().trim())));
                    //to set the edited title to the Diagram2D
                    SimpleLineDiagram lineDiag =
                        (SimpleLineDiagram) returnValue;
                    lineDiag.setTitle(inputValue);
                    conceptualSchema.addDiagram(lineDiag);
                }

                private int showTitleExistsDialog(Diagram2D returnValue) {
                    Object[] options;
                    if (returnValue instanceof SimpleLineDiagram) {
                        options =
                            new Object[] {
                                "Replace Old Diagram",
                                "Discard New Diagram",
                                "Rename New Diagram" };
                        return JOptionPane.showOptionDialog(
                            parent,
                            "A diagram with the title '"
                                + returnValue.getTitle()
                                + "' already exists.",
                            "Title exists",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            options,
                            options[2]);
                    } else {
                        options =
                            new Object[] {
                                "Replace Old Diagram",
                                "Discard New Diagram" };
                        return JOptionPane.showOptionDialog(
                            parent,
                            "A diagram with the title '"
                                + returnValue.getTitle()
                                + "' already exists.",
                            "Title exists",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.ERROR_MESSAGE,
                            null,
                            options,
                            null);
                    }
                }
            });
            popupMenu.add(menuItem);
        }
        int x =
            this.newDiagramButton.getLocationOnScreen().x - this.getX() + 20;
        int y =
            this.newDiagramButton.getLocationOnScreen().y - this.getY() + 10;
        popupMenu.show(this, x, y);
    }

    protected void fillScaleGeneratorList() {
        this.scaleGenerators = new ArrayList();
        scaleGenerators.add(new AttributeListScaleGenerator(this));
        scaleGenerators.add(
            new ContextTableScaleGenerator(this, this.eventBroker));
        scaleGenerators.add(new NominalScaleGenerator(this));
        scaleGenerators.add(new OrdinalScaleGenerator(this));
        scaleGenerators.add(new CrossordinalScaleGenerator(this));
    }

    public void createMenuBar() {
        final DiagramView diagramView = diagramEditingView.getDiagramView();

        saveActivity =
            new SaveConceptualSchemaActivity(conceptualSchema, eventBroker);
        this.saveAsFileAction =
            new SaveFileAction(
                this,
                saveActivity,
                KeyEvent.VK_A,
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_S,
                    ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        this.saveAsFileAction.setPostSaveActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                currentFile = saveAsFileAction.getLastFileUsed().getPath();
                addFileToMRUList(saveAsFileAction.getLastFileUsed());
                conceptualSchema.dataSaved();
                updateWindowTitle();
                return true;
            }
        });
        this.saveAsFileAction.setPreSaveActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                return databaseWellDefined();
            }
        });

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
        newSchemaActivity.setTestNewOkActivity(testSchemaSavedActivity);
        newSchemaActivity.setPostNewActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                currentFile = null;
                updateWindowTitle();
                showDatabaseConnectionDialog();
                DatabaseViewerManager.resetRegistry();
                return databaseConnection.isConnected();
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
                if (conceptualSchema.getDatabaseInfo() != null) {
                    return true;
                }
                showDatabaseConnectionDialog();
                return databaseConnection.isConnected();
            }
        });

        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openMenuItem.addActionListener(openFileAction);
        fileMenu.add(openMenuItem);

        mruMenu = new JMenu("Reopen");
        mruMenu.setMnemonic(KeyEvent.VK_R);
        recreateMruMenu();
        fileMenu.add(mruMenu);

        fileMenu.addSeparator();

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }
        });
        fileMenu.add(saveMenuItem);

        JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
        saveAsMenuItem.addActionListener(saveAsFileAction);
        fileMenu.add(saveAsMenuItem);

        fileMenu.addSeparator();

        JMenuItem importCSCMenuItem = new JMenuItem("Import CSC File...");
        importCSCMenuItem.setMnemonic(KeyEvent.VK_I);
        importCSCMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                importCSC();
            }
        });
        fileMenu.add(importCSCMenuItem);

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
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(
            new SimpleAction(
                this,
                new CloseMainPanelActivity(this),
                "Exit",
                KeyEvent.VK_X,
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK)));
        fileMenu.add(exitMenuItem);

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
                boolean newState = !SqlClauseLabelView.allAreHidden();
                showObjectLabels.setSelected(!newState);
                SqlClauseLabelView.setAllHidden(newState);
                diagramView.repaint();
            }
        });
        viewMenu.add(showObjectLabels);

        menuBar.add(viewMenu);

        JMenu toolMenu = new JMenu("Tools");
        toolMenu.setMnemonic(KeyEvent.VK_T);
        dumpStatisticalDataMenuItem =
            new JMenuItem("Export Realized Scales...");
        dumpStatisticalDataMenuItem.setMnemonic(KeyEvent.VK_S);
        dumpStatisticalDataMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportStatisticalData();
            }
        });
        dumpStatisticalDataMenuItem.setEnabled(false);
		toolMenu.add(dumpStatisticalDataMenuItem);
		dumpSQLMenuItem = new JMenuItem("Export Database as SQL...");
		dumpSQLMenuItem.setMnemonic(KeyEvent.VK_D);
		dumpSQLMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportSQLScript();
			}
		});
		dumpSQLMenuItem.setEnabled(false);
		toolMenu.add(dumpSQLMenuItem);
		createOptimizedSystemMenuItem = new JMenuItem("Create Speed Optimized System...");
		createOptimizedSystemMenuItem.setMnemonic(KeyEvent.VK_O);
		createOptimizedSystemMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createSpeedOptimizedSystem();
			}
		});
		createOptimizedSystemMenuItem.setEnabled(false);
        toolMenu.add(createOptimizedSystemMenuItem);
        
        toolMenu.addSeparator();
        
		this.checkContextAction = new CheckContextConsistencyAction(this.conceptualSchema,
    													this.databaseConnection, 
    													this, this.eventBroker);
        toolMenu.add(checkContextAction);
        menuBar.add(toolMenu);

        // --- help menu ---
        // create a help menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

        final JFrame parent = this;
        JMenuItem aboutItem = new JMenuItem("About Elba...");
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

    private void updateWindowTitle() {
        // get the current filename without the extension and full path
        // we have to use '\\' instead of '\' although we're checking for the occurrence of '\'.
        if (currentFile != null) {
            String filename =
                currentFile.substring(
                    currentFile.lastIndexOf("\\") + 1,
                    (currentFile.length() - 4));
            setTitle(filename + " - " + WINDOW_TITLE);
        } else {
            setTitle(WINDOW_TITLE);
        }
    }

    private boolean databaseWellDefined() {
        if (this.databaseConnection.isConnected()) {
            return true;
        }
        Object[] options =
            { "Drop database information", "Keep information", "Go back" };
        int result =
            JOptionPane.showOptionDialog(
                this,
                "No database connection is established, which means the current database\n"
                    + "information might be wrong. Storing a schema with broken database information\n"
                    + "might disallow opening it in ToscanaJ, dropping the database information will\n"
                    + "cause ToscanaJ to display the query clauses. What do you want to do?",
                "Database not connected",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[2]);
        if (result == 0) {
            this.conceptualSchema.setDatabaseInfo(null);
            return true;
        }
        if (result == 1) {
            return true; // save anyway
        }
        return false;
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

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public void closeMainPanel() {
        boolean closeOk = checkForMissingSave();
        if (!closeOk) {
            return;
        }
        // store current position
        ConfigurationManager.storePlacement(CONFIGURATION_SECTION_NAME, this);
        ConfigurationManager.storeFloat(
            CONFIGURATION_SECTION_NAME,
            "minLabelFontSize",
            (float) this
                .diagramEditingView
                .getDiagramView()
                .getMinimumFontSize());
        ConfigurationManager.storeStringList(
            CONFIGURATION_SECTION_NAME,
            "mruFiles",
            this.mruList);
        ConfigurationManager.storeInt(
            CONFIGURATION_SECTION_NAME,
            "diagramViewDivider",
            diagramEditingView.getDividerLocation());
        this.diagramEditingView.saveConfigurationSettings();
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

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent schemaEvent =
                (ConceptualSchemaChangeEvent) e;
            ConceptualSchema newConceptualSchema =
                schemaEvent.getConceptualSchema();
            if (newConceptualSchema != this.conceptualSchema
                || e instanceof NewConceptualSchemaEvent) {
                this.conceptualSchema = newConceptualSchema;
                if (schemaDescriptionView != null) {
                    schemaDescriptionView.setContent(
                        conceptualSchema.getDescription());
                }
                connectDatabase();
            }
            if (e instanceof DatabaseInfoChangedEvent) {
                connectDatabase();
            }
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

    protected void connectDatabase() {
        this.dumpStatisticalDataMenuItem.setEnabled(false);
        this.dumpSQLMenuItem.setEnabled(false);
        this.createOptimizedSystemMenuItem.setEnabled(false);
        disconnectDatabase();
        DatabaseInfo databaseInformation = conceptualSchema.getDatabaseInfo();
        if (databaseInformation != null
            && databaseInformation.getDriverClass() != null
            && databaseInformation.getURL() != null) {
            try {
                DatabaseConnection.setConnection(databaseConnection);
                databaseConnection.connect(databaseInformation);
                URL location =
                    conceptualSchema.getDatabaseInfo().getEmbeddedSQLLocation();
                if (location != null) {
                    databaseConnection.executeScript(location);
                }
            } catch (DatabaseException ex) {
                ErrorDialog.showError(
                    this,
                    ex,
                    "DB Connection failed",
                    "Can not connect to the database:\n" + ex.getMessage());
            }
            this.dumpStatisticalDataMenuItem.setEnabled(true);
            this.dumpSQLMenuItem.setEnabled(true);
			this.createOptimizedSystemMenuItem.setEnabled(true);
        }
    }

    protected void disconnectDatabase() {
        if (databaseConnection.isConnected()) {
            try {
                databaseConnection.disconnect();
            } catch (DatabaseException ex) {
                ErrorDialog.showError(
                    this,
                    ex,
                    "Closing database error",
                    "Some error closing the old database:\n" + ex.getMessage());
            }
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
            
		JPanel optionsPanel = new JPanel(new BorderLayout());
		JCheckBox openNewSchema = new JCheckBox("Create a new schema with this file", true);
		optionsPanel.add(openNewSchema, BorderLayout.CENTER);
		optionsPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		openDialog.setAccessory(optionsPanel);

        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
        	if (openNewSchema.isSelected()) {
				if (checkForMissingSave()) {
					// @todo is is possible to reuse NewConceptualSchemaAction here?..
					new ConceptualSchema(this.eventBroker);
				}
        	}
            importCSC(openDialog.getSelectedFile());
            this.lastCSCFile = openDialog.getSelectedFile();
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Import failed");
        }
        if (openNewSchema.isSelected()) {
			currentFile = null;
			updateWindowTitle();
			showDatabaseConnectionDialog();
			DatabaseViewerManager.resetRegistry();
        }
    }

    private void importCSC(File file) {
        try {
            new CSCParser().importCSCFile(file, this.conceptualSchema);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
            return;
        } catch (DataFormatException e) {
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

    private void exportStatisticalData() {
        final JFileChooser saveDialog;
        ExportStatisticalDataSettingsPanel expSettingsPanel =
            new ExportStatisticalDataSettingsPanel();
        String[] extension = { "xml" };
        ExtensionFileFilter fileFilter =
            new ExtensionFileFilter(extension, "XML Files");
        ExtensionFileFilter[] filterArray = { fileFilter };
        if (this.lastExportFile != null) {
            // use position of last file for dialog
            saveDialog =
                new CheckDuplicateFileChooser(this.lastExportFile, filterArray);
        } else {
            saveDialog =
                new CheckDuplicateFileChooser(
                    new File(System.getProperty("user.dir")),
                    filterArray);
        }
        saveDialog.setAccessory((JComponent) expSettingsPanel);
        saveDialog.setApproveButtonText("Export");
        int rv = saveDialog.showSaveDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        exportStatisticalData(
            saveDialog.getSelectedFile(),
            expSettingsPanel.getFilterClause(),
            expSettingsPanel.hasIncludeContingentListsSet(),
            expSettingsPanel.hasIncludeIntentExtentListsSet());
        this.lastExportFile = saveDialog.getSelectedFile();
    }

    private void exportStatisticalData(
        File file,
        String filterClause,
        boolean includeContingentLists,
        boolean includeIntentExtent) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            DataDump.dumpData(
                this.conceptualSchema,
                outputStream,
                filterClause,
                includeContingentLists,
                includeIntentExtent);
            outputStream.close();
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Could not export file");
            try {
                outputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            file.delete();
            return;
        }
    }

    private void exportSQLScript() {
        final JFileChooser saveDialog;
        String[] extension = { "sql" };
        ExtensionFileFilter fileFilter =
            new ExtensionFileFilter(extension, "SQL Scripts");
        ExtensionFileFilter[] filterArray = { fileFilter };
        if (this.lastExportFile != null) {
            // use position of last file for dialog
            saveDialog =
                new CheckDuplicateFileChooser(this.lastExportFile, filterArray);
        } else {
            saveDialog =
                new CheckDuplicateFileChooser(
                    new File(System.getProperty("user.dir")),
                    filterArray);
        }
        saveDialog.setApproveButtonText("Export");
        int rv = saveDialog.showSaveDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        exportSQLScript(saveDialog.getSelectedFile());
        this.lastExportFile = saveDialog.getSelectedFile();
    }

    private void exportSQLScript(File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            DumpSqlScript.dumpSqlScript(this.databaseConnection, outputStream);
            outputStream.close();
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Could not export file");
            try {
                outputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
            file.delete();
            return;
        }
    }
    
    /**
     * @todo this could be in its own function object -- with a test class for it
     * 
     * @todo at the moment this works on the same table. The reason for this is that we would have to
     *       adjust all WHERE expressions in the UPDATEs otherwise, since they would have to find the
     *       source table. This means understanding more SQL than we do at the moment, since all column
     *       identifiers would have to be found and replaced. Textual replacements would not be sufficient
     *       since column names could appear in other contexts, e.g. as values. 
     * 
     * @todo we could check if we run on the embedded DB and offer to save the SQL script in case that
     *       is true.
     */
    private void createSpeedOptimizedSystem() {
    	/// @todo do we want to call the consistency check here?
    	
    	DatabaseInfo databaseInfo = this.conceptualSchema.getDatabaseInfo();
		String tableName = databaseInfo.getTable().getSqlExpression();

        try {
			for (int i = 0; i < this.conceptualSchema.getNumberOfDiagrams(); i++) {
				String columnName = "__diagram" + i + "__";
				try {
					databaseConnection.executeUpdate("ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " INTEGER;");
					databaseConnection.executeUpdate("CREATE INDEX " + columnName + "index ON " + tableName + "("+ columnName + ");");
				} catch (DatabaseException e) {
					// that is ok, we just had this column before
				}
				Diagram2D diagram = this.conceptualSchema.getDiagram(i);
				Iterator nodeIt = diagram.getNodes();
				int contingentCount = 0;
				while (nodeIt.hasNext()) {
	                DiagramNode node = (DiagramNode) nodeIt.next();
	                ConceptImplementation concept = (ConceptImplementation) node.getConcept();
	                if(concept.getObjectContingentSize() != 0) {
						String oldWhereClause = WhereClauseGenerator.createClause(concept.getObjectContingentIterator());
						String newWhereClause = columnName + " = " + contingentCount;
                        databaseConnection.executeUpdate("UPDATE " + tableName + 
														" SET " + newWhereClause +
														" WHERE " + oldWhereClause + ";");
						concept.removeObjectContingent();
						concept.addObject(newWhereClause);
						contingentCount ++;
	                }
	            }
			}
		} catch (DatabaseException e) {
			ErrorDialog.showError(this, e, "Error updating table");
			return;
		}
    	
    	/// @todo it is not really obvious in the UI what is going on -- this is just a hack to get things going
    	saveAsFileAction.actionPerformed(null);
    }

    private void saveFile() {
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

    protected void showDatabaseConnectionDialog() {
        disconnectDatabase();
        connectionInformationView.show();
        if (!connectionInformationView.newConnectionWasSet()) {
            // reconnect to the old connection
            connectDatabase();
        }
    }    
}
