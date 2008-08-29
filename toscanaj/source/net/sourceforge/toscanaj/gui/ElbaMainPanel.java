/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.DataDump;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.db.DumpSqlScript;
import net.sourceforge.toscanaj.controller.db.WhereClauseGenerator;
import net.sourceforge.toscanaj.controller.diagram.SqlClauseEditingLabelViewPopupMenuHandler;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.MeetIrreducibleChainsDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.action.CheckContextConsistencyAction;
import net.sourceforge.toscanaj.gui.action.ExportDiagramAction;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.CloseMainPanelActivity;
import net.sourceforge.toscanaj.gui.activity.LoadConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.NewConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.SaveConceptualSchemaActivity;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.dialog.CheckDuplicateFileChooser;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExportStatisticalDataSettingsPanel;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.context.FCAElementImplementation;
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
import net.sourceforge.toscanaj.parser.CSCImport;
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
import net.sourceforge.toscanaj.view.scales.ScaleGenerator;

import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.context.model.Context;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

public class ElbaMainPanel extends JFrame implements MainPanel,
EventBrokerListener {
    private static final String WINDOW_TITLE = "Elba";
    private static final int MaxMruFiles = 8;

    private static final ExtendedPreferences preferences = ExtendedPreferences
    .userNodeForClass(ElbaMainPanel.class);

    /**
     * Main Controllers
     */
    private final EventBroker eventBroker;
    private final DatabaseConnection databaseConnection;

    /**
     * Model
     */
    private ConceptualSchema conceptualSchema;

    /**
     * Controls
     */
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu mruMenu;
    private JMenu helpMenu;

    private List<String> mruList = new LinkedList<String>();
    private File currentFile;

    /**
     * Views
     */
    private DiagramEditingView diagramEditingView;
    private DatabaseConnectionInformationView connectionInformationView;
    private XMLEditorDialog schemaDescriptionView;
    private JMenuItem dumpSQLMenuItem;
    private JMenuItem dumpStatisticalDataMenuItem;
    private JMenuItem createOptimizedSystemMenuItem;
    private SaveFileAction saveAsFileAction;
    private List<ScaleGenerator> scaleGenerators;
    private JButton newDiagramButton;
    private JToolBar toolbar;
    private SaveConceptualSchemaActivity saveActivity;
    private final DiagramExportSettings diagramExportSettings;
    private ExportDiagramAction exportDiagramAction;
    private File lastCSCFile;

    private File lastExportFile;
    private CheckContextConsistencyAction checkContextAction;

    public ElbaMainPanel() {
        super("Elba");

        this.eventBroker = new EventBroker();
        this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        this.databaseConnection = new DatabaseConnection(this.eventBroker);
        DatabaseConnection.setConnection(this.databaseConnection);

        this.diagramExportSettings = new DiagramExportSettings();

        this.eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class,
                Object.class);
        this.eventBroker.subscribe(this, DatabaseInfoChangedEvent.class,
                Object.class);

        fillScaleGeneratorList();

        createViews();
        // this has to happen before the menu gets created, since the menu uses
        // the information
        final DiagramView diagramView = this.diagramEditingView
        .getDiagramView();
        final double minLabelFontSize = preferences.getDouble(
                "minLabelFontSize", diagramView.getMinimumFontSize());
        diagramView.setMinimumFontSize(minLabelFontSize);

        this.mruList = preferences.getStringList("mruFiles");
        createMenuBar();

        this.lastCSCFile = new File(preferences.get("lastCSCFile", ""));
        this.lastExportFile = new File(preferences.get("lastExportFile", ""));

        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            final File schemaFile = new File(this.mruList.get(this.mruList
                    .size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }

        this.setVisible(true);
        preferences.restoreWindowPlacement(this, new Rectangle(10, 10, 1000,
                700));

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                closeMainPanel();
            }
        });
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    public void createViews() {
        final JPanel mainView = new JPanel(new BorderLayout());
        this.connectionInformationView = new DatabaseConnectionInformationView(
                this, this.conceptualSchema, this.eventBroker);
        this.schemaDescriptionView = new XMLEditorDialog(this,
        "System description");
        this.toolbar = new JToolBar();
        this.newDiagramButton = new JButton("New Diagram...");
        this.newDiagramButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                showScaleGeneratorMenu();
            }
        });

        final JButton schemaDescriptionButton = new JButton(
        "System Description...");
        schemaDescriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                ElbaMainPanel.this.schemaDescriptionView
                .setContent(ElbaMainPanel.this.conceptualSchema
                        .getDescription());
                ElbaMainPanel.this.schemaDescriptionView.setVisible(true);
                ElbaMainPanel.this.conceptualSchema
                .setDescription(ElbaMainPanel.this.schemaDescriptionView
                        .getContent());
            }
        });

        final JButton databaseConnectionButton = new JButton(
                "Database Connection...");
        databaseConnectionButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                showDatabaseConnectionDialog();
            }
        });

        this.toolbar.add(this.newDiagramButton);
        this.toolbar.add(schemaDescriptionButton);
        this.toolbar.add(databaseConnectionButton);

        this.diagramEditingView = new DiagramEditingView(this,
                this.conceptualSchema, this.eventBroker, true);
        this.diagramEditingView.setDividerLocation(preferences.getInt(
                "diagramViewDivider", 200));
        final DiagramView diagramView = this.diagramEditingView
        .getDiagramView();
        diagramView.setObjectLabelFactory(SqlClauseLabelView.getFactory());

        diagramView.getController().getEventBroker().subscribe(this,
                DisplayedDiagramChangedEvent.class, Object.class);

        diagramView.getController().getEventBroker().subscribe(
                new SqlClauseEditingLabelViewPopupMenuHandler(diagramView),
                CanvasItemContextMenuRequestEvent.class,
                SqlClauseLabelView.getFactory().getLabelClass());

        mainView.add(this.toolbar, BorderLayout.NORTH);
        mainView.add(this.diagramEditingView, BorderLayout.CENTER);
        setContentPane(mainView);
    }

    public void showScaleGeneratorMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem menuItem;
        final JFrame parent = this;

        final Iterator<ScaleGenerator> it = this.scaleGenerators.iterator();
        while (it.hasNext()) {
            final ScaleGenerator generator = it.next();
            menuItem = new JMenuItem(generator.getScaleName());
            menuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    try {
                        final Context context = generator.generateScale(
                                ElbaMainPanel.this.conceptualSchema,
                                ElbaMainPanel.this.databaseConnection);
                        Diagram2D newDiagram = null;
                        Lattice lattice = null;
                        if (context != null) {
                            final LatticeGenerator lgen = new GantersAlgorithm();
                            lattice = lgen.createLattice(context);
                            newDiagram = NDimLayoutOperations
                            .createDiagram(
                                    lattice,
                                    context.getName(),
                                    new MeetIrreducibleChainsDimensionStrategy());
                            if (null != newDiagram) {
                                Diagram2D diagramWithSameTitle = null;
                                int indexOfExistingDiagram = -1;
                                for (int i = 0; i < ElbaMainPanel.this.conceptualSchema
                                .getNumberOfDiagrams(); i++) {
                                    if (ElbaMainPanel.this.conceptualSchema
                                            .getDiagram(i).getTitle()
                                            .equalsIgnoreCase(
                                                    newDiagram.getTitle())) {
                                        diagramWithSameTitle = ElbaMainPanel.this.conceptualSchema
                                        .getDiagram(i);
                                        indexOfExistingDiagram = i;
                                    }
                                }
                                if (diagramWithSameTitle != null) {
                                    final int rv = showTitleExistsDialog(newDiagram);
                                    if (rv == JOptionPane.OK_OPTION) {
                                        replaceTitle(newDiagram,
                                                diagramWithSameTitle,
                                                indexOfExistingDiagram);
                                    } else if (rv == JOptionPane.CANCEL_OPTION) {
                                        renameTitle(newDiagram,
                                                diagramWithSameTitle);
                                    }
                                } else {
                                    ElbaMainPanel.this.conceptualSchema
                                    .addDiagram(newDiagram);
                                    ElbaMainPanel.this.diagramEditingView
                                    .getDiagramView().showDiagram(
                                            newDiagram);
                                }
                            }
                        }
                    } catch (final Exception exc) {
                        ErrorDialog.showError(parent, exc,
                                "Scale generation failed");
                    }
                }

                private void replaceTitle(final Diagram2D returnValue,
                        final Diagram2D diagramWithSameTitle,
                        final int indexOfExistingDiagram) {
                    ElbaMainPanel.this.conceptualSchema.addDiagram(returnValue);
                    if (indexOfExistingDiagram != -1) {
                        ElbaMainPanel.this.conceptualSchema.exchangeDiagrams(
                                (ElbaMainPanel.this.conceptualSchema
                                        .getNumberOfDiagrams() - 1),
                                        indexOfExistingDiagram);
                        ElbaMainPanel.this.conceptualSchema
                        .removeDiagram(diagramWithSameTitle);
                    } else {
                        ElbaMainPanel.this.conceptualSchema
                        .removeDiagram(diagramWithSameTitle);
                    }
                }

                private void renameTitle(final Diagram2D returnValue,
                        final Diagram2D diagramWithSameTitle) {
                    String inputValue = "";
                    String currentValue = returnValue.getTitle();
                    do {
                        inputValue = (String) JOptionPane.showInputDialog(null,
                                "Enter title: ", "Rename title",
                                JOptionPane.PLAIN_MESSAGE, null, null,
                                currentValue);
                        if (inputValue != null) {
                            inputValue = inputValue.trim();
                            currentValue = inputValue;
                        }
                    } while (inputValue != null
                            && (inputValue.equals("") || inputValue
                                    .equalsIgnoreCase(diagramWithSameTitle
                                            .getTitle().trim())));
                    // to set the edited title to the Diagram2D
                    final SimpleLineDiagram lineDiag = (SimpleLineDiagram) returnValue;
                    lineDiag.setTitle(inputValue);
                    ElbaMainPanel.this.conceptualSchema.addDiagram(lineDiag);
                }

                private int showTitleExistsDialog(final Diagram2D returnValue) {
                    Object[] options;
                    if (returnValue instanceof SimpleLineDiagram) {
                        options = new Object[] { "Replace Old Diagram",
                                "Discard New Diagram", "Rename New Diagram" };
                        return JOptionPane.showOptionDialog(parent,
                                "A diagram with the title '"
                                + returnValue.getTitle()
                                + "' already exists.", "Title exists",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.ERROR_MESSAGE, null, options,
                                options[2]);
                    } else {
                        options = new Object[] { "Replace Old Diagram",
                        "Discard New Diagram" };
                        return JOptionPane.showOptionDialog(parent,
                                "A diagram with the title '"
                                + returnValue.getTitle()
                                + "' already exists.", "Title exists",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.ERROR_MESSAGE, null, options, null);
                    }
                }
            });
            popupMenu.add(menuItem);
        }
        final int x = this.newDiagramButton.getLocationOnScreen().x
        - this.getX() + 20;
        final int y = this.newDiagramButton.getLocationOnScreen().y
        - this.getY() + 10;
        popupMenu.show(this, x, y);
    }

    protected void fillScaleGeneratorList() {
        this.scaleGenerators = new ArrayList<ScaleGenerator>();
        this.scaleGenerators.add(new AttributeListScaleGenerator(this));
        this.scaleGenerators.add(new ContextTableScaleGenerator(this,
                this.eventBroker));
        this.scaleGenerators.add(new NominalScaleGenerator(this));
        this.scaleGenerators.add(new OrdinalScaleGenerator(this));
        this.scaleGenerators.add(new CrossordinalScaleGenerator(this));
    }

    public void createMenuBar() {
        final DiagramView diagramView = this.diagramEditingView
        .getDiagramView();
        final JFrame parent = this;

        this.saveActivity = new SaveConceptualSchemaActivity(
                this.conceptualSchema, this.eventBroker);
        if (this.saveAsFileAction == null) {
            this.saveAsFileAction = new SaveFileAction(this, this.saveActivity,
                    KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_S,
                            ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        }
        this.saveAsFileAction.setPostSaveActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                setCurrentFile(ElbaMainPanel.this.saveAsFileAction
                        .getLastFileUsed());
                ElbaMainPanel.this.conceptualSchema.dataSaved();
                return true;
            }
        });
        this.saveAsFileAction.setPreSaveActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                return databaseWellDefined();
            }
        });

        // --- menu bar ---
        this.menuBar = new JMenuBar();
        setJMenuBar(this.menuBar);

        // --- file menu ---
        this.fileMenu = new JMenu("File");
        this.fileMenu.setMnemonic(KeyEvent.VK_F);
        this.menuBar.add(this.fileMenu);

        final SimpleActivity testSchemaSavedActivity = new SimpleActivity() {
            public boolean doActivity() throws Exception {
                return checkForMissingSave();
            }
        };
        final NewConceptualSchemaActivity newSchemaActivity = new NewConceptualSchemaActivity(
                this.eventBroker);
        newSchemaActivity.setTestNewOkActivity(testSchemaSavedActivity);
        newSchemaActivity.setPostNewActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                ElbaMainPanel.this.currentFile = null;
                updateWindowTitle();
                showDatabaseConnectionDialog();
                DatabaseViewerManager.resetRegistry();
                return ElbaMainPanel.this.databaseConnection.isConnected();
            }
        });
        final SimpleAction newAction = new SimpleAction(this,
                newSchemaActivity, "New", KeyEvent.VK_N, KeyStroke
                .getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

        final JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.setMnemonic(KeyEvent.VK_N);
        newMenuItem.addActionListener(newAction);
        this.fileMenu.add(newMenuItem);

        final LoadConceptualSchemaActivity loadSchemaActivity = new LoadConceptualSchemaActivity(
                this.eventBroker);
        loadSchemaActivity.setTestOpenOkActivity(testSchemaSavedActivity);
        final OpenFileAction openFileAction = new OpenFileAction(this,
                loadSchemaActivity, this.currentFile, KeyEvent.VK_O, KeyStroke
                .getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openFileAction.addPostOpenActivity(new SimpleActivity() {
            public boolean doActivity() throws Exception {
                updateWindowTitle();
                if (ElbaMainPanel.this.conceptualSchema.getDatabaseInfo() != null) {
                    return true;
                }
                showDatabaseConnectionDialog();
                return ElbaMainPanel.this.databaseConnection.isConnected();
            }
        });

        final JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,
                ActionEvent.CTRL_MASK));
        openMenuItem.addActionListener(openFileAction);
        this.fileMenu.add(openMenuItem);

        this.mruMenu = new JMenu("Reopen");
        this.mruMenu.setMnemonic(KeyEvent.VK_R);
        recreateMruMenu();
        this.fileMenu.add(this.mruMenu);

        this.fileMenu.addSeparator();

        final JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.setMnemonic(KeyEvent.VK_S);
        saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,
                ActionEvent.CTRL_MASK));
        saveMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                saveFile();
            }
        });
        this.fileMenu.add(saveMenuItem);

        final JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
        saveAsMenuItem.addActionListener(this.saveAsFileAction);
        this.fileMenu.add(saveAsMenuItem);

        this.fileMenu.addSeparator();

        final JMenuItem importCSCMenuItem = new JMenuItem("Import CSC File...");
        importCSCMenuItem.setMnemonic(KeyEvent.VK_I);
        importCSCMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                importCSC();
            }
        });
        this.fileMenu.add(importCSCMenuItem);

        // we add the export options only if we can export at all
        // / @todo reduce duplicate code with ToscanaJMainPanel
        if (this.diagramExportSettings != null) {
            final Frame frame = JOptionPane.getFrameForComponent(this);
            this.exportDiagramAction = new ExportDiagramAction(frame,
                    this.diagramExportSettings, this.diagramEditingView
                    .getDiagramView(), KeyEvent.VK_E, KeyStroke
                    .getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            this.fileMenu.add(this.exportDiagramAction);
            this.exportDiagramAction.setEnabled(false);

            this.fileMenu.addSeparator();
        }

        // --- file exit item ---
        JMenuItem exitMenuItem;
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setMnemonic(KeyEvent.VK_X);
        exitMenuItem.addActionListener(new SimpleAction(this,
                new CloseMainPanelActivity(this), "Exit", KeyEvent.VK_X,
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, ActionEvent.ALT_MASK)));
        this.fileMenu.add(exitMenuItem);

        final JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.add(diagramView.getUndoManager().getUndoAction());
        editMenu.add(diagramView.getUndoManager().getRedoAction());
        this.menuBar.add(editMenu);

        final JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        final ButtonGroup fontSizeGroup = new ButtonGroup();
        final JMenu setMinLabelSizeSubMenu = new JMenu("Set minimum label size");
        setMinLabelSizeSubMenu.setMnemonic(KeyEvent.VK_S);
        JMenuItem fontRangeMenuItem = new JRadioButtonMenuItem("None");
        fontSizeGroup.add(fontRangeMenuItem);
        fontRangeMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final JMenuItem source = (JMenuItem) e.getSource();
                diagramView.setMinimumFontSize(0);
                source.setSelected(true);
            }
        });
        fontRangeMenuItem.setSelected(true);
        setMinLabelSizeSubMenu.add(fontRangeMenuItem);
        int fontRange = 6; // min font size
        while (fontRange < 26) {
            fontRangeMenuItem = new JRadioButtonMenuItem(fontRange + "");
            fontSizeGroup.add(fontRangeMenuItem);
            fontRangeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    final JMenuItem source = (JMenuItem) e.getSource();
                    final int newFontSize = Integer.parseInt(source.getText());
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

        final JCheckBoxMenuItem showAttributeLabels = new JCheckBoxMenuItem(
        "Show Attribute Labels");
        showAttributeLabels.setMnemonic(KeyEvent.VK_A);
        showAttributeLabels.setSelected(true);
        showAttributeLabels.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean newState = !AttributeLabelView.allAreHidden();
                showAttributeLabels.setSelected(!newState);
                AttributeLabelView.setAllHidden(newState);
                diagramView.repaint();
            }
        });
        viewMenu.add(showAttributeLabels);

        final JCheckBoxMenuItem showObjectLabels = new JCheckBoxMenuItem(
        "Show Object Labels");
        showObjectLabels.setMnemonic(KeyEvent.VK_O);
        showObjectLabels.setSelected(true);
        showObjectLabels.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean newState = !SqlClauseLabelView.allAreHidden();
                showObjectLabels.setSelected(!newState);
                SqlClauseLabelView.setAllHidden(newState);
                diagramView.repaint();
            }
        });
        viewMenu.add(showObjectLabels);

        this.menuBar.add(viewMenu);

        final JMenu toolMenu = new JMenu("Tools");
        toolMenu.setMnemonic(KeyEvent.VK_T);
        this.dumpStatisticalDataMenuItem = new JMenuItem(
        "Export Realized Scales...");
        this.dumpStatisticalDataMenuItem.setMnemonic(KeyEvent.VK_S);
        this.dumpStatisticalDataMenuItem
        .addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                exportStatisticalData();
            }
        });
        this.dumpStatisticalDataMenuItem.setEnabled(false);
        toolMenu.add(this.dumpStatisticalDataMenuItem);
        this.dumpSQLMenuItem = new JMenuItem("Export Database as SQL...");
        this.dumpSQLMenuItem.setMnemonic(KeyEvent.VK_D);
        this.dumpSQLMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                exportSQLScript();
            }
        });
        this.dumpSQLMenuItem.setEnabled(false);
        toolMenu.add(this.dumpSQLMenuItem);
        this.createOptimizedSystemMenuItem = new JMenuItem(
        "Create Speed Optimized System...");
        this.createOptimizedSystemMenuItem.setMnemonic(KeyEvent.VK_O);
        this.createOptimizedSystemMenuItem
        .addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                checkForMissingSave();
                final int result = JOptionPane
                .showOptionDialog(
                        parent,
                        "Creating a speed optimized system will modify your database by adding a column for each diagram\n"
                        + "and your conceptual schema will be changed to query these new columns.\n\n"
                        + "The procedure can take some time on a large system. At the end Elba will offer you to save the\n"
                        + "new conceptual schema, since the changed made are irreversible.\n\n",
                        "Warning", JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE, null,
                        new String[] { "Cancel", "Continue" },
                "Cancel");
                if (result == 1) {
                    createSpeedOptimizedSystem();
                }
            }
        });
        this.createOptimizedSystemMenuItem.setEnabled(false);
        toolMenu.add(this.createOptimizedSystemMenuItem);

        toolMenu.addSeparator();

        this.checkContextAction = new CheckContextConsistencyAction(
                this.conceptualSchema, this.databaseConnection, this,
                this.eventBroker);
        toolMenu.add(this.checkContextAction);
        this.menuBar.add(toolMenu);

        // --- help menu ---
        // create a help menu
        this.helpMenu = new JMenu("Help");
        this.helpMenu.setMnemonic(KeyEvent.VK_H);

        final JMenuItem aboutItem = new JMenuItem("About Elba...");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                ToscanaJMainPanel.showAboutDialog(parent);
            }
        });
        this.helpMenu.add(aboutItem);

        this.menuBar.add(Box.createHorizontalGlue());
        this.menuBar.add(this.helpMenu);
    }

    private void updateWindowTitle() {
        // get the current filename without the extension and full path
        // we have to use '\\' instead of '\' although we're checking for the
        // occurrence of '\'.
        if (this.currentFile != null) {
            final String filename = this.currentFile.getName().substring(0,
                    this.currentFile.getName().length() - 4);
            setTitle(filename + " - " + WINDOW_TITLE);
        } else {
            setTitle(WINDOW_TITLE);
        }
    }

    private boolean databaseWellDefined() {
        if (this.databaseConnection.isConnected()) {
            return true;
        }
        final Object[] options = { "Drop database information",
                "Keep information", "Go back" };
        final int result = JOptionPane
        .showOptionDialog(
                this,
                "No database connection is established, which means the current database\n"
                + "information might be wrong. Storing a schema with broken database information\n"
                + "might disallow opening it in ToscanaJ, dropping the database information will\n"
                + "cause ToscanaJ to display the query clauses. What do you want to do?",
                "Database not connected",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[2]);
        if (result == 0) {
            this.conceptualSchema.setDatabaseInfo(null);
            return true;
        }
        if (result == 1) {
            return true; // save anyway
        }
        return false;
    }

    private void openSchemaFile(final File schemaFile) {
        try {
            this.conceptualSchema = CSXParser.parse(this.eventBroker,
                    schemaFile);
            setCurrentFile(schemaFile);
        } catch (final FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file", e
                    .getMessage());
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        } catch (final IOException e) {
            ErrorDialog.showError(this, e, "Could not open file", e
                    .getMessage());
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        } catch (final DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not read file", e
                    .getMessage());
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Could not open file", e
                    .getMessage());
            e.printStackTrace();
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
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
            final ListIterator<String> it = this.mruList
            .listIterator(this.mruList.size());
            while (it.hasPrevious()) {
                final String cur = it.previous();
                if (this.currentFile != null
                        && cur.equals(this.currentFile.getAbsolutePath())) {
                    // don't enlist the current file
                    continue;
                }
                empty = false;
                final JMenuItem mruItem = new JMenuItem(cur);
                mruItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        final JMenuItem menuItem = (JMenuItem) e.getSource();
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
        return this.eventBroker;
    }

    public void closeMainPanel() {
        final boolean closeOk = checkForMissingSave();
        if (!closeOk) {
            return;
        }
        // store current position
        preferences.storeWindowPlacement(this);
        preferences.putDouble("minLabelFontSize", this.diagramEditingView
                .getDiagramView().getMinimumFontSize());
        preferences.putStringList("mruFiles", this.mruList);
        preferences.putInt("diagramViewDivider", this.diagramEditingView
                .getDividerLocation());
        preferences.put("lastCSCFile", this.lastCSCFile.getAbsolutePath());
        preferences
        .put("lastExportFile", this.lastExportFile.getAbsolutePath());
        this.diagramEditingView.saveConfigurationSettings();
        System.exit(0);
    }

    protected boolean checkForMissingSave() throws HeadlessException {
        boolean closeOk;
        if (!this.conceptualSchema.isDataSaved()) {
            final int returnValue = showFileChangedDialog();
            if (returnValue == 0) {
                // save
                final boolean result = this.saveAsFileAction.saveFile();
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

    public void processEvent(final Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            final ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            final ConceptualSchema newConceptualSchema = schemaEvent
            .getConceptualSchema();
            if (newConceptualSchema != this.conceptualSchema) {
                this.conceptualSchema = newConceptualSchema;
                if (this.schemaDescriptionView != null) {
                    this.schemaDescriptionView.setContent(this.conceptualSchema
                            .getDescription());
                }
                connectDatabase();
            }
            if (e instanceof DatabaseInfoChangedEvent) {
                connectDatabase();
            }
        }
        if (e instanceof ConceptualSchemaLoadedEvent) {
            final ConceptualSchemaLoadedEvent loadEvent = (ConceptualSchemaLoadedEvent) e;
            setCurrentFile(loadEvent.getFile());
        } else if (e instanceof NewConceptualSchemaEvent) {
            setCurrentFile(null);
        }
        this.exportDiagramAction.setEnabled((this.diagramEditingView
                .getDiagramView().getDiagram() != null)
                && (this.diagramExportSettings != null));
    }

    protected void connectDatabase() {
        this.dumpStatisticalDataMenuItem.setEnabled(false);
        this.dumpSQLMenuItem.setEnabled(false);
        this.createOptimizedSystemMenuItem.setEnabled(false);
        disconnectDatabase();
        final DatabaseInfo databaseInformation = this.conceptualSchema
        .getDatabaseInfo();
        if (databaseInformation != null
                && databaseInformation.getDriverClass() != null
                && databaseInformation.getURL() != null) {
            try {
                DatabaseConnection.setConnection(this.databaseConnection);
                this.databaseConnection.connect(databaseInformation);
                final URL location = this.conceptualSchema.getDatabaseInfo()
                .getEmbeddedSQLLocation();
                if (location != null) {
                    this.databaseConnection.executeScript(location);
                }
            } catch (final DatabaseException ex) {
                ErrorDialog.showError(this, ex, "DB Connection failed",
                        "Can not connect to the database:\n" + ex.getMessage());
            }
            this.dumpStatisticalDataMenuItem.setEnabled(true);
            this.dumpSQLMenuItem.setEnabled(true);
            this.createOptimizedSystemMenuItem.setEnabled(true);
        }
    }

    protected void disconnectDatabase() {
        if (this.databaseConnection.isConnected()) {
            try {
                this.databaseConnection.disconnect();
            } catch (final DatabaseException ex) {
                ErrorDialog.showError(this, ex, "Closing database error",
                        "Some error closing the old database:\n"
                        + ex.getMessage());
            }
        }
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
        openDialog.setFileFilter(new ExtensionFileFilter(
                new String[] { "csc" }, "Conscript Files"));

        final JPanel optionsPanel = new JPanel(new BorderLayout());
        final JCheckBox openNewSchema = new JCheckBox("Create a new schema",
                true);
        optionsPanel.add(openNewSchema, BorderLayout.NORTH);
        optionsPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        openDialog.setAccessory(optionsPanel);

        final int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        try {
            if (openNewSchema.isSelected()) {
                if (!checkForMissingSave()) {
                    return;
                }
                // @todo is is possible to reuse NewConceptualSchemaAction
                // here?..
                new ConceptualSchema(this.eventBroker);
            }
            importCSC(openDialog.getSelectedFile());
            this.lastCSCFile = openDialog.getSelectedFile();
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Import failed");
        }
        if (openNewSchema.isSelected()) {
            this.currentFile = null;
            updateWindowTitle();
            if (this.conceptualSchema.getDatabaseInfo() == null) {
                showDatabaseConnectionDialog();
                DatabaseViewerManager.resetRegistry();
            }
        }
    }

    private void importCSC(final File file) {
        try {
            new CSCImport().importCSCFile(file, this.conceptualSchema);
        } catch (final org.tockit.conscript.parser.DataFormatException e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Could not parse file");
            return;
        } catch (final Error e) {
            ErrorDialog.showError(this, e, "Could not parse file",
            "Could not parse CSC file");
            return;
        }
    }

    private void exportStatisticalData() {
        final JFileChooser saveDialog;
        final ExportStatisticalDataSettingsPanel expSettingsPanel = new ExportStatisticalDataSettingsPanel();
        final String[] extension = { "xml" };
        final ExtensionFileFilter fileFilter = new ExtensionFileFilter(
                extension, "XML Files");
        final ExtensionFileFilter[] filterArray = { fileFilter };
        if (this.lastExportFile != null) {
            // use position of last file for dialog
            saveDialog = new CheckDuplicateFileChooser(this.lastExportFile,
                    filterArray);
        } else {
            saveDialog = new CheckDuplicateFileChooser(new File(System
                    .getProperty("user.dir")), filterArray);
        }
        saveDialog.setAccessory(expSettingsPanel);
        saveDialog.setApproveButtonText("Export");
        final int rv = saveDialog.showSaveDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        exportStatisticalData(saveDialog.getSelectedFile(), expSettingsPanel
                .getFilterClause(), expSettingsPanel
                .hasIncludeContingentListsSet(), expSettingsPanel
                .hasIncludeIntentExtentListsSet());
        this.lastExportFile = saveDialog.getSelectedFile();
    }

    private void exportStatisticalData(final File file,
            final String filterClause, final boolean includeContingentLists,
            final boolean includeIntentExtent) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            DataDump.dumpData(this.conceptualSchema,
                    new DatabaseConnectedConceptInterpreter(
                            this.conceptualSchema.getDatabaseInfo()),
                            outputStream, filterClause, includeContingentLists,
                            includeIntentExtent, null);
            outputStream.close();
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Could not export file");
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException ioe) {
                    // not much we can do here
                    e.printStackTrace();
                }
            }
            file.delete();
            return;
        }
    }

    private void exportSQLScript() {
        final JFileChooser saveDialog;
        final String[] extension = { "sql" };
        final ExtensionFileFilter fileFilter = new ExtensionFileFilter(
                extension, "SQL Scripts");
        final ExtensionFileFilter[] filterArray = { fileFilter };
        if (this.lastExportFile != null) {
            // use position of last file for dialog
            saveDialog = new CheckDuplicateFileChooser(this.lastExportFile,
                    filterArray);
        } else {
            saveDialog = new CheckDuplicateFileChooser(new File(System
                    .getProperty("user.dir")), filterArray);
        }
        saveDialog.setApproveButtonText("Export");
        final int rv = saveDialog.showSaveDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        exportSQLScript(saveDialog.getSelectedFile());
        this.lastExportFile = saveDialog.getSelectedFile();
    }

    private void exportSQLScript(final File file) {
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            DumpSqlScript.dumpSqlScript(this.databaseConnection, outputStream);
            outputStream.close();
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Could not export file");
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException ioe) {
                    // not much we can do here
                    e.printStackTrace();
                }
            }
            file.delete();
            return;
        }
    }

    /**
     * @todo this could be in its own function object -- with a test class for
     *       it
     * 
     * @todo at the moment this works on the same table. The reason for this is
     *       that we would have to adjust all WHERE expressions in the UPDATEs
     *       otherwise, since they would have to find the source table. This
     *       means understanding more SQL than we do at the moment, since all
     *       column identifiers would have to be found and replaced. Textual
     *       replacements would not be sufficient since column names could
     *       appear in other contexts, e.g. as values.
     * 
     * @todo we could check if we run on the embedded DB and offer to save the
     *       SQL script in case that is true.
     */
    private void createSpeedOptimizedSystem() {
        // / @todo do we want to call the consistency check here?

        final DatabaseInfo databaseInfo = this.conceptualSchema
        .getDatabaseInfo();
        final String tableName = databaseInfo.getTable().getSqlExpression();

        try {
            for (int i = 0; i < this.conceptualSchema.getNumberOfDiagrams(); i++) {
                final String columnName = "__diagram" + i + "__";
                try {
                    this.databaseConnection.executeUpdate("ALTER TABLE "
                            + tableName + " ADD COLUMN " + columnName
                            + " INTEGER;");
                    this.databaseConnection.executeUpdate("CREATE INDEX "
                            + columnName + "index ON " + tableName + "("
                            + columnName + ");");
                } catch (final DatabaseException e) {
                    // that is ok, we just had this column before
                }
                final Diagram2D diagram = this.conceptualSchema.getDiagram(i);
                final Iterator<DiagramNode> nodeIt = diagram.getNodes();
                int contingentCount = 0;
                while (nodeIt.hasNext()) {
                    final DiagramNode node = nodeIt.next();
                    final ConceptImplementation concept = (ConceptImplementation) node
                    .getConcept();
                    if (concept.getObjectContingentSize() != 0) {
                        final String oldWhereClause = WhereClauseGenerator
                        .createClause(concept
                                .getObjectContingentIterator());
                        final String newWhereClause = columnName + " = "
                        + contingentCount;
                        this.databaseConnection.executeUpdate("UPDATE "
                                + tableName + " SET " + newWhereClause
                                + " WHERE " + oldWhereClause + ";");
                        concept.removeObjectContingent();
                        concept.addObject(new FCAElementImplementation(
                                newWhereClause));
                        contingentCount++;
                    }
                }
            }
        } catch (final DatabaseException e) {
            ErrorDialog.showError(this, e, "Error updating table");
            return;
        }

        // / @todo it is not really obvious in the UI what is going on -- this
        // is just a hack to get things going
        this.saveAsFileAction.actionPerformed(null);
    }

    private void saveFile() {
        if (this.currentFile == null) {
            this.saveAsFileAction.saveFile();
        } else {
            try {
                this.saveActivity.processFile(this.currentFile);
                this.conceptualSchema.dataSaved();
            } catch (final Exception e) {
                ErrorDialog.showError(this, e, "Saving file failed");
            }
        }
    }

    private int showFileChangedDialog() {
        // return values
        // 0 : Save file
        // 1 : Discard current file
        // 2 : Go back (cancel save/open/close operation)
        final Object[] options = { "Save", "Discard", "Go back" };
        return JOptionPane
        .showOptionDialog(
                this,
                "The conceptual schema has been modified. Do you want to save the changes?",
                "Schema changed", JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE, null, options, options[2]);
    }

    protected void showDatabaseConnectionDialog() {
        disconnectDatabase();
        this.connectionInformationView.setVisible(true);
        if (!this.connectionInformationView.newConnectionWasSet()) {
            // reconnect to the old connection
            connectDatabase();
        }
    }

    private void setCurrentFile(final File newCurrentFile) {
        this.currentFile = newCurrentFile;
        if (newCurrentFile != null) {
            this.saveAsFileAction.setPreviousFile(newCurrentFile);
            final String filePath = newCurrentFile.getAbsolutePath();
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
}
