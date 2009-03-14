/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
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
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.AbstractAction;
import javax.swing.Action;
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
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

import net.sourceforge.toscanaj.ToscanaJ;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.diagram.AttributeLabelViewPopupMenuHandler;
import net.sourceforge.toscanaj.controller.diagram.BackgroundPopupMenuHandler;
import net.sourceforge.toscanaj.controller.diagram.FilterOperationEventListener;
import net.sourceforge.toscanaj.controller.diagram.HighlightRemovalOperationEventListener;
import net.sourceforge.toscanaj.controller.diagram.HighlightingOperationEventListener;
import net.sourceforge.toscanaj.controller.diagram.LabelClickEventHandler;
import net.sourceforge.toscanaj.controller.diagram.LabelDragEventHandler;
import net.sourceforge.toscanaj.controller.diagram.LabelScrollEventHandler;
import net.sourceforge.toscanaj.controller.diagram.NodeViewPopupMenuHandler;
import net.sourceforge.toscanaj.controller.diagram.ObjectLabelViewOpenDisplayHandler;
import net.sourceforge.toscanaj.controller.diagram.ObjectLabelViewPopupMenuHandler;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DatabaseConnectedConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DiagramReference;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter.IntervalType;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.action.ExportDiagramAction;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.gui.dialog.DiagramContextDescriptionDialog;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.ReadingHelpDialog;
import net.sourceforge.toscanaj.gui.dialog.ToscanaJPreferences;
import net.sourceforge.toscanaj.gui.events.DiagramClickedEvent;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.util.gradients.CombinedGradient;
import net.sourceforge.toscanaj.util.gradients.Gradient;
import net.sourceforge.toscanaj.util.gradients.LinearGradient;
import net.sourceforge.toscanaj.view.diagram.AttributeLabelView;
import net.sourceforge.toscanaj.view.diagram.DiagramSchema;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.diagram.ObjectLabelView;
import net.sourceforge.toscanaj.view.diagram.SignificanceLegend;

import org.jdom.Element;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.events.CanvasItemEvent;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.manipulators.ItemMovementManipulator;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;
import org.tockit.swing.preferences.ExtendedPreferences;

/**
 * This class provides the main GUI panel with menus and a toolbar for ToscanaJ.
 * 
 * @todo store view settings (contingent/extent labels/gradient) in session
 *       management
 */
public class ToscanaJMainPanel extends JFrame implements ChangeObserver,
        ClipboardOwner {
    private ItemMovementManipulator legendMoveManipulator;

    private static final String WINDOW_TITLE = "ToscanaJ";

    private static final ExtendedPreferences preferences = ExtendedPreferences
            .userNodeForClass(ToscanaJMainPanel.class);

    /**
     * The central event broker for the main panel
     */
    private final EventBroker broker;

    /**
     * The maximum number of files in the most recently used files list.
     */
    static private final int MaxMruFiles = 8;

    /**
     * Our toolbar.
     */
    private JToolBar toolbar = null;

    /**
     * The main menu.
     */
    private JMenuBar menubar = null;

    /**
     * The split main in the main area.
     * 
     * This is stored for storing the splitter position when closing.
     */
    private JSplitPane splitPane = null;

    // the actions used in the UI
    private Action openFileAction;
    private ExportDiagramAction exportDiagramAction;
    private Action goBackAction;

    // buttons list
    private JButton diagramDescriptionButton = null;
    private JButton schemaDescriptionButton = null;
    private JButton diagramContextDescriptionButton = null;

    // menu items list
    // FILE menu
    private JMenuItem printMenuItem = null;
    private JMenuItem printSetupMenuItem = null;
    private JMenu mruMenu = null;
    private JMenuItem exitMenuItem = null;
    private JMenuItem diagramDescriptionMenuItem = null;

    // DIAGRAM menu
    private JRadioButtonMenuItem filterAllMenuItem = null;
    private JRadioButtonMenuItem filterExactMenuItem = null;

    // nesting submenu
    private JRadioButtonMenuItem noNestingMenuItem = null;
    private JRadioButtonMenuItem nestingLevel1MenuItem = null;

    // view menu
    private JRadioButtonMenuItem showAllMenuItem;
    private JRadioButtonMenuItem showExactMenuItem;

    /**
     * The main model member.
     */
    private ConceptualSchema conceptualSchema;

    /**
     * The diagram viewing area.
     */
    private DiagramView diagramView;

    private DiagramView diagramPreview;

    private ReadingHelpDialog readingHelpDialog;

    /**
     * The pane for selecting the diagrams.
     */
    private DiagramOrganiser diagramOrganiser;

    /**
     * Flag to indicate if the save icon and menu options should be enabled
     */
    private boolean fileIsOpen = false;

    /**
     * Keeps a list of most recently files.
     */
    private List<String> mruList = new LinkedList<String>();

    /**
     * Stores the file name of the currently open file.
     */
    private String currentFile = null;

    /**
     * The last setup for page format given by the user.
     */
    private PageFormat pageFormat = new PageFormat();

    /**
     * The current settings for diagram export.
     */
    private DiagramExportSettings diagramExportSettings = null;

    private JSplitPane leftHandPane;

    /**
     * Simple initialisation constructor.
     */
    public ToscanaJMainPanel() {
        super(WINDOW_TITLE);
        this.broker = new EventBroker();
        this.conceptualSchema = new ConceptualSchema(this.broker);
        DatabaseConnection.initialize(this.broker);

        this.diagramExportSettings = new DiagramExportSettings();

        // then build the panel (order is important for checking if we want
        // export options)
        buildUI();
        // listen to changes on DiagramController
        DiagramController.getController().addObserver(this);
        // we are the parent window for anything database viewers / report
        // generators want to display
        DatabaseViewerManager.setParentComponent(this);
        // restore the old MRU list
        this.mruList = preferences.getStringList("mruFiles");
        // set up the menu for the MRU files
        recreateMruMenu();
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            final File schemaFile = new File(this.mruList.get(this.mruList
                    .size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    /**
     * This constructor opens the file given as url in the parameter.
     * 
     * Used when opening ToscanaJ with a file name on the command line.
     */
    public ToscanaJMainPanel(final String schemaFileURL) {
        // do the normal initialisation first
        this();
        // open the file
        openSchemaFile(new File(schemaFileURL));
    }

    /**
     * Build the GUI.
     */
    private void buildUI() {
        this.diagramView = new DiagramView();
        // set the minimum font size of the label into this.diagramView from the
        // properties file
        // this has to happen before the menu gets created, since the menu uses
        // the information
        final double minLabelFontSize = preferences.getDouble(
                "minLabelFontSize", this.diagramView.getMinimumFontSize());
        this.diagramView.setMinimumFontSize(minLabelFontSize);

        createActions();
        buildToolBar();

        // Lay out the content pane.
        final JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        final DiagramController controller = DiagramController.getController();
        // / @todo move the subscriptions into the handlers
        final EventBroker diagramEventBroker = this.diagramView.getController()
                .getEventBroker();
        new FilterOperationEventListener(controller, diagramEventBroker);
        new HighlightingOperationEventListener(this.diagramView,
                diagramEventBroker);
        new HighlightRemovalOperationEventListener(this.diagramView,
                diagramEventBroker);
        diagramEventBroker.subscribe(new ObjectLabelViewPopupMenuHandler(
                this.diagramView, this.broker),
                CanvasItemContextMenuRequestEvent.class, ObjectLabelView.class);
        new ObjectLabelViewOpenDisplayHandler(diagramEventBroker);
        diagramEventBroker.subscribe(new AttributeLabelViewPopupMenuHandler(
                this.diagramView), CanvasItemContextMenuRequestEvent.class,
                AttributeLabelView.class);
        new NodeViewPopupMenuHandler(this.diagramView, diagramEventBroker);
        new BackgroundPopupMenuHandler(this.diagramView, diagramEventBroker,
                this);

        new LabelClickEventHandler(diagramEventBroker);
        new LabelDragEventHandler(diagramEventBroker);
        new LabelScrollEventHandler(diagramEventBroker);

        final Dimension minimumSize = new Dimension(50, 50);

        this.diagramOrganiser = new DiagramOrganiser(this.conceptualSchema,
                this.broker);
        if (preferences.getBoolean("showDiagramPreview", true)) {
            // set preference in case it is not yet there -- otherwise the
            // options dialog
            // will show the wrong setting
            preferences.putBoolean("showDiagramPreview", true);
            this.diagramPreview = new DiagramView();
            this.diagramPreview
                    .setConceptInterpreter(new DirectConceptInterpreter());
            this.diagramPreview
                    .setConceptInterpretationContext(new ConceptInterpretationContext(
                            new DiagramHistory(), new EventBroker()));
            this.diagramPreview.setObjectLabelFactory(null);
            this.diagramPreview.setMinimumFontSize(8.0);
            this.diagramPreview.setMinimumSize(minimumSize);
            // / @todo clean/restructure/outsource some of this if we keep it
            this.broker.subscribe(new EventBrokerListener() {
                class FilterChangeHandler implements EventBrokerListener {
                    private final DiagramReference diagramReference;

                    FilterChangeHandler(final DiagramReference diagramReference) {
                        this.diagramReference = diagramReference;
                    }

                    public void processEvent(final Event e) {
                        CanvasItemEvent itemEvent = null;
                        try {
                            itemEvent = (CanvasItemEvent) e;
                        } catch (final ClassCastException e1) {
                            throw new RuntimeException(
                                    getClass().getName()
                                            + " has to be subscribed to CanvasItemEvents only");
                        }
                        NodeView nodeView = null;
                        try {
                            nodeView = (NodeView) itemEvent.getSubject();
                        } catch (final ClassCastException e1) {
                            throw new RuntimeException(
                                    getClass().getName()
                                            + " has to be subscribed to events from NodeViews only");
                        }
                        ToscanaJMainPanel.this.diagramPreview
                                .setSelectedConcepts(nodeView.getDiagramNode()
                                        .getConceptNestingList());
                        this.diagramReference.setFilterConcept(nodeView
                                .getDiagramNode().getConcept());
                        // / @todo evil hack, creates weird dependencies
                        if (ToscanaJMainPanel.this.diagramView
                                .getConceptInterpreter() instanceof DatabaseConnectedConceptInterpreter) {
                            final DatabaseConnectedConceptInterpreter dbint = (DatabaseConnectedConceptInterpreter) ToscanaJMainPanel.this.diagramView
                                    .getConceptInterpreter();
                            dbint.clearCache();
                        }
                        ToscanaJMainPanel.this.diagramView
                                .showDiagram(ToscanaJMainPanel.this.diagramView
                                        .getDiagram());
                    }
                }

                FilterChangeHandler selectionListener;

                public void processEvent(final Event e) {
                    final DiagramReference diagramReference = ((DiagramClickedEvent) e)
                            .getDiagramReference();
                    ToscanaJMainPanel.this.diagramPreview
                            .showDiagram(diagramReference.getDiagram());
                    final Concept zoomedConcept = diagramReference
                            .getFilterConcept();
                    final EventBroker canvasBroker = ToscanaJMainPanel.this.diagramPreview
                            .getController().getEventBroker();
                    if (this.selectionListener != null) {
                        canvasBroker
                                .removeSubscriptions(this.selectionListener);
                    }
                    if (zoomedConcept != null) {
                        ToscanaJMainPanel.this.diagramPreview
                                .setSelectedConcepts(new Concept[] { zoomedConcept });
                        this.selectionListener = new FilterChangeHandler(
                                diagramReference);
                        canvasBroker.subscribe(this.selectionListener,
                                CanvasItemSelectedEvent.class, NodeView.class);
                    }
                }
            }, DiagramClickedEvent.class, DiagramReference.class);

            this.leftHandPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                    this.diagramOrganiser, this.diagramPreview);
            this.leftHandPane.setOneTouchExpandable(true);
            this.leftHandPane.setResizeWeight(0);

            this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    this.leftHandPane, this.diagramView);
        } else {
            this.splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                    this.diagramOrganiser, this.diagramView);
        }

        this.splitPane.setOneTouchExpandable(true);
        this.splitPane.setResizeWeight(0);

        this.diagramView.setMinimumSize(minimumSize);
        this.diagramOrganiser.setMinimumSize(minimumSize);
        contentPane.add(this.toolbar, BorderLayout.NORTH);
        contentPane.add(this.splitPane, BorderLayout.CENTER);
        setContentPane(contentPane);
        // restore old position
        this.setVisible(true);
        preferences.restoreWindowPlacement(this,
                new Rectangle(10, 10, 900, 700));
        final int mainDividerPos = preferences.getInt("mainDivider", 200);
        this.splitPane.setDividerLocation(mainDividerPos);
        final int secondaryDividerPos = preferences.getInt("secondaryDivider",
                420);
        if (this.leftHandPane != null) {
            this.leftHandPane.setDividerLocation(secondaryDividerPos);
        }

        buildMenuBar();
        setJMenuBar(this.menubar);

        this.readingHelpDialog = new ReadingHelpDialog(this, diagramEventBroker);
    }

    private void createActions() {
        this.openFileAction = new AbstractAction("Open...") {
            public void actionPerformed(final ActionEvent e) {
                openSchema();
                updateWindowTitle();
            }
        };
        this.openFileAction.putValue(Action.MNEMONIC_KEY, new Integer(
                KeyEvent.VK_O));
        this.openFileAction.putValue(Action.ACCELERATOR_KEY, KeyStroke
                .getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

        this.exportDiagramAction = new ExportDiagramAction(this,
                this.diagramExportSettings, this.diagramView, KeyEvent.VK_E,
                KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        this.exportDiagramAction.setEnabled(false);
        this.goBackAction = new AbstractAction("Go Back one Diagram") {
            public void actionPerformed(final ActionEvent e) {
                DiagramController.getController().back();
            }
        };
        this.goBackAction.putValue(Action.MNEMONIC_KEY, new Integer(
                KeyEvent.VK_B));
        this.goBackAction.putValue(Action.ACCELERATOR_KEY, KeyStroke
                .getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
        this.goBackAction.setEnabled(false);
        // / @todo Change all the other actions into Actions.
    }

    /**
     * build the MenuBar
     */
    private void buildMenuBar() {
        if (this.menubar == null) {
            // create menu bar
            this.menubar = new JMenuBar();
        } else {
            this.menubar.removeAll();
        }

        // create the FILE menu
        final JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        this.menubar.add(fileMenu);
        fileMenu.add(this.openFileAction);

        // we add the export options only if we can export at all
        if (this.diagramExportSettings != null) {
            fileMenu.add(this.exportDiagramAction);
        }

        // separator
        fileMenu.addSeparator();

        // menu item PRINT
        this.printMenuItem = new JMenuItem("Print...");
        this.printMenuItem.setMnemonic(KeyEvent.VK_P);
        this.printMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,
                ActionEvent.CTRL_MASK));
        this.printMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                printDiagram();
            }
        });
        this.printMenuItem.setEnabled(false);
        fileMenu.add(this.printMenuItem);

        // menu item PRINT SETUP
        this.printSetupMenuItem = new JMenuItem("Print Setup...");
        this.printSetupMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                ToscanaJMainPanel.this.pageFormat = PrinterJob.getPrinterJob()
                        .pageDialog(ToscanaJMainPanel.this.pageFormat);
                printDiagram();
            }
        });
        this.printSetupMenuItem.setEnabled(true);
        fileMenu.add(this.printSetupMenuItem);

        // separator
        fileMenu.addSeparator();

        // recent edited files will be in this menu
        this.mruMenu = new JMenu("Reopen");
        this.mruMenu.setMnemonic(KeyEvent.VK_R);
        fileMenu.add(this.mruMenu);

        // separator
        fileMenu.addSeparator();

        // menu item EXIT
        this.exitMenuItem = new JMenuItem("Exit");
        this.exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                ActionEvent.ALT_MASK));
        this.exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                closeMainPanel();
            }
        });
        fileMenu.add(this.exitMenuItem);

        // create the DIAGRAM menu
        final JMenu diagrMenu = new JMenu("Diagram");
        diagrMenu.setMnemonic(KeyEvent.VK_D);
        this.menubar.add(diagrMenu);
        diagrMenu.add(this.goBackAction);
        diagrMenu.addSeparator();

        // menu radio buttons group:
        final ButtonGroup documentsFilterGroup = new ButtonGroup();
        this.filterExactMenuItem = new JRadioButtonMenuItem(
                "Filter: use only exact matches");
        this.filterExactMenuItem.setMnemonic(KeyEvent.VK_X);
        this.filterExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        this.filterExactMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                ToscanaJMainPanel.this.diagramView
                        .setFilterMode(ConceptInterpretationContext.CONTINGENT);
                updateLabelViews();
            }
        });
        documentsFilterGroup.add(this.filterExactMenuItem);
        diagrMenu.add(this.filterExactMenuItem);
        this.filterAllMenuItem = new JRadioButtonMenuItem(
                "Filter: use all matches");
        this.filterAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        this.filterAllMenuItem.setMnemonic(KeyEvent.VK_A);
        this.filterAllMenuItem.setSelected(true);
        this.filterAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                ToscanaJMainPanel.this.diagramView
                        .setFilterMode(ConceptInterpretationContext.EXTENT);
                updateLabelViews();
            }
        });
        documentsFilterGroup.add(this.filterAllMenuItem);
        diagrMenu.add(this.filterAllMenuItem);

        // separator
        diagrMenu.addSeparator();

        // create the nesting submenu
        final ButtonGroup nestingGroup = new ButtonGroup();
        this.noNestingMenuItem = new JRadioButtonMenuItem("Flat Diagram");
        this.noNestingMenuItem.setMnemonic(KeyEvent.VK_F);
        this.noNestingMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        this.noNestingMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                DiagramController.getController().setNestingLevel(0);
            }
        });
        this.noNestingMenuItem.setSelected(true);
        nestingGroup.add(this.noNestingMenuItem);
        diagrMenu.add(this.noNestingMenuItem);
        this.nestingLevel1MenuItem = new JRadioButtonMenuItem("Nested Diagram");
        this.nestingLevel1MenuItem.setMnemonic(KeyEvent.VK_N);
        this.nestingLevel1MenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        this.nestingLevel1MenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                DiagramController.getController().setNestingLevel(1);
            }
        });
        nestingGroup.add(this.nestingLevel1MenuItem);
        diagrMenu.add(this.nestingLevel1MenuItem);

        // create the view menu
        final JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        this.menubar.add(viewMenu);

        final ButtonGroup documentsDisplayGroup = new ButtonGroup();
        this.showExactMenuItem = new JRadioButtonMenuItem(
                "Show only exact matches");
        this.showExactMenuItem.setMnemonic(KeyEvent.VK_X);
        this.showExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        this.showExactMenuItem.setSelected(true);
        this.showExactMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                updateLabelViews();
            }
        });
        documentsDisplayGroup.add(this.showExactMenuItem);
        viewMenu.add(this.showExactMenuItem);

        this.showAllMenuItem = new JRadioButtonMenuItem("Show all matches");
        this.showAllMenuItem.setMnemonic(KeyEvent.VK_A);
        this.showAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        this.showAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                updateLabelViews();
            }
        });
        documentsDisplayGroup.add(this.showAllMenuItem);
        viewMenu.add(this.showAllMenuItem);

        viewMenu.addSeparator();

        final JCheckBoxMenuItem showAttributeLabels = new JCheckBoxMenuItem(
                "Show Attribute Labels");
        showAttributeLabels.setMnemonic(KeyEvent.VK_A);
        showAttributeLabels.setSelected(true);
        showAttributeLabels.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean newState = !AttributeLabelView.allAreHidden();
                showAttributeLabels.setSelected(!newState);
                AttributeLabelView.setAllHidden(newState);
                ToscanaJMainPanel.this.diagramView.repaint();
            }
        });
        viewMenu.add(showAttributeLabels);

        final JCheckBoxMenuItem showObjectLabels = new JCheckBoxMenuItem(
                "Show Object Labels");
        showObjectLabels.setMnemonic(KeyEvent.VK_O);
        showObjectLabels.setSelected(true);
        showObjectLabels.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                final boolean newState = !ObjectLabelView.allAreHidden();
                showObjectLabels.setSelected(!newState);
                ObjectLabelView.setAllHidden(newState);
                ToscanaJMainPanel.this.diagramView.repaint();
            }
        });
        viewMenu.add(showObjectLabels);

        viewMenu.addSeparator();

        final JMenuItem showInfoViewItem = new JMenuItem(
                "Show Concept Information...");
        showInfoViewItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                ToscanaJMainPanel.this.readingHelpDialog.setVisible(true);
            }
        });

        viewMenu.add(showInfoViewItem);

        // @todo rename property entry
        // @todo disable gradient options while deviation is analyzed
        if (preferences.getBoolean("offerOrthogonalityGradient", false)) {
            viewMenu.addSeparator();
            final CombinedGradient redGreenGradient = new CombinedGradient(
                    new LinearGradient(new Color(180, 0, 0), Color.WHITE), 1);
            redGreenGradient.addGradientPart(new LinearGradient(Color.WHITE,
                    new Color(0, 130, 0)), 1);
            final SignificanceLegend legendItem = new SignificanceLegend(
                    new Font("sans-serif", Font.PLAIN, 12), new Point2D.Double(
                            0, 0), redGreenGradient);
            if (this.legendMoveManipulator == null) {
                this.legendMoveManipulator = new ItemMovementManipulator(
                        this.diagramView, SignificanceLegend.class,
                        this.diagramView.getController().getEventBroker());
            }
            final JCheckBoxMenuItem showOrthogonalityMenuItem = new JCheckBoxMenuItem(
                    "Analyze orthogonality");
            showOrthogonalityMenuItem.addActionListener(new ActionListener() {
                private IntervalType lastIntervalType;

                public void actionPerformed(final ActionEvent e) {
                    final DiagramSchema diagramSchema = ToscanaJMainPanel.this.diagramView
                            .getDiagramSchema();
                    if (showOrthogonalityMenuItem.isSelected()) {
                        this.lastIntervalType = diagramSchema.getGradientType();
                        setDiagramGradient(redGreenGradient,
                                ConceptInterpreter.INTERVAL_TYPE_ORTHOGONALTIY);
                        ToscanaJMainPanel.this.diagramView
                                .getConceptInterpreter().showDeviation(true);
                    } else {
                        setDiagramGradient(diagramSchema.getDefaultGradient(),
                                this.lastIntervalType);
                        ToscanaJMainPanel.this.diagramView
                                .getConceptInterpreter().showDeviation(false);
                    }
                    ToscanaJMainPanel.this.diagramView.updateLabelEntries();
                }
            });
            viewMenu.add(showOrthogonalityMenuItem);

            final JMenuItem showSignificanceLegendMenuItem = new JMenuItem(
                    "Show Significance Legend");
            showSignificanceLegendMenuItem
                    .addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent e) {
                            ToscanaJMainPanel.this.diagramView
                                    .addCanvasItem(legendItem);
                            ToscanaJMainPanel.this.diagramView.repaint();
                        }
                    });
            viewMenu.add(showSignificanceLegendMenuItem);
        }

        if (preferences.getBoolean("offerGradientOptions", false)) {
            viewMenu.addSeparator();
            final DiagramSchema diagramSchema = this.diagramView
                    .getDiagramSchema();
            final ButtonGroup colorGradientGroup = new ButtonGroup();
            final JRadioButtonMenuItem showExactGradientMenuItem = new JRadioButtonMenuItem(
                    "Use colors for exact matches");
            showExactGradientMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_G, ActionEvent.CTRL_MASK));
            showExactGradientMenuItem
                    .setSelected(diagramSchema.getGradientType() == ConceptInterpreter.INTERVAL_TYPE_CONTINGENT);
            showExactGradientMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setDiagramGradient(diagramSchema.getDefaultGradient(),
                            ConceptInterpreter.INTERVAL_TYPE_CONTINGENT);
                }
            });
            colorGradientGroup.add(showExactGradientMenuItem);
            viewMenu.add(showExactGradientMenuItem);

            final JRadioButtonMenuItem showAllGradientMenuItem = new JRadioButtonMenuItem(
                    "Use colors for all matches");
            showAllGradientMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_G, ActionEvent.CTRL_MASK
                            + ActionEvent.SHIFT_MASK));
            showAllGradientMenuItem
                    .setSelected(diagramSchema.getGradientType() == ConceptInterpreter.INTERVAL_TYPE_EXTENT);
            showAllGradientMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    setDiagramGradient(diagramSchema.getDefaultGradient(),
                            ConceptInterpreter.INTERVAL_TYPE_EXTENT);
                }
            });
            colorGradientGroup.add(showAllGradientMenuItem);
            viewMenu.add(showAllGradientMenuItem);

        }

        if (preferences.getBoolean("offerNodeSizeScalingOptions", false)) {
            viewMenu.addSeparator();
            final ButtonGroup nodeSizeScalingGroup = new ButtonGroup();
            final JRadioButtonMenuItem nodeSizeExactMenuItem = new JRadioButtonMenuItem(
                    "Change node sizes with number of exact matches");
            nodeSizeExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            nodeSizeExactMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    ToscanaJMainPanel.this.diagramView
                            .getDiagramSchema()
                            .setNodeSizeScalingType(
                                    ConceptInterpreter.INTERVAL_TYPE_CONTINGENT);
                    ToscanaJMainPanel.this.diagramView.update(this);
                }
            });
            nodeSizeScalingGroup.add(nodeSizeExactMenuItem);
            viewMenu.add(nodeSizeExactMenuItem);

            final JRadioButtonMenuItem nodeSizeAllMenuItem = new JRadioButtonMenuItem(
                    "Change node sizes with number of all matches");
            nodeSizeAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_N, ActionEvent.CTRL_MASK
                            + ActionEvent.SHIFT_MASK));
            nodeSizeAllMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    ToscanaJMainPanel.this.diagramView.getDiagramSchema()
                            .setNodeSizeScalingType(
                                    ConceptInterpreter.INTERVAL_TYPE_EXTENT);
                    ToscanaJMainPanel.this.diagramView.update(this);
                }
            });
            nodeSizeScalingGroup.add(nodeSizeAllMenuItem);
            viewMenu.add(nodeSizeAllMenuItem);

            final JRadioButtonMenuItem nodeSizeFixedMenuItem = new JRadioButtonMenuItem(
                    "Fixed node sizes");
            nodeSizeFixedMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    ToscanaJMainPanel.this.diagramView.getDiagramSchema()
                            .setNodeSizeScalingType(
                                    ConceptInterpreter.INTERVAL_TYPE_FIXED);
                    ToscanaJMainPanel.this.diagramView.update(this);
                }
            });
            nodeSizeScalingGroup.add(nodeSizeFixedMenuItem);
            viewMenu.add(nodeSizeFixedMenuItem);

            final ConceptInterpreter.IntervalType nodeSizeScaling = this.diagramView
                    .getDiagramSchema().getNodeSizeScalingType();
            if (nodeSizeScaling == ConceptInterpreter.INTERVAL_TYPE_CONTINGENT) {
                nodeSizeExactMenuItem.setSelected(true);
            } else if (nodeSizeScaling == ConceptInterpreter.INTERVAL_TYPE_EXTENT) {
                nodeSizeAllMenuItem.setSelected(true);
            } else if (nodeSizeScaling == ConceptInterpreter.INTERVAL_TYPE_FIXED) {
                nodeSizeFixedMenuItem.setSelected(true);
            }
        }

        // menu radio buttons group:
        final ButtonGroup labelContentGroup = new ButtonGroup();

        /**
         * @todo doing arithmetics on the KeyEvent constants is probably not the
         *       proper thing to do, though I could not find another way to get
         *       this. Try again...
         */
        if (this.conceptualSchema != null) {
            final Iterator<Query> it = this.conceptualSchema.getQueries()
                    .iterator();
            if (it.hasNext()) {
                viewMenu.addSeparator();
                boolean first = true;
                final String allowedChars = "abcdefghijklmnopqrstuvwxyz";
                String usedChars = "ax";
                int count = 0;
                while (it.hasNext()) {
                    final Query query = it.next();
                    count++;
                    final String name = query.getName();
                    final JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(
                            name);
                    for (int i = 0; i < name.length(); i++) {
                        final char c = name.toLowerCase().charAt(i);
                        if ((allowedChars.indexOf(c) != -1)
                                && (usedChars.indexOf(c) == -1)) {
                            menuItem.setMnemonic(KeyEvent.VK_A
                                    + allowedChars.indexOf(c));
                            usedChars += c;
                            break;
                        }
                    }
                    if (count < 10) { // first ones get their number (starting
                        // with 1)
                        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                                KeyEvent.VK_0 + count, ActionEvent.ALT_MASK));
                    }
                    if (count == 10) { // tenth gets the zero
                        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                                KeyEvent.VK_0, ActionEvent.ALT_MASK));
                    } // others don't get an accelerator
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(final ActionEvent e) {
                            ToscanaJMainPanel.this.diagramView.setQuery(query);
                        }
                    });
                    labelContentGroup.add(menuItem);
                    viewMenu.add(menuItem);
                    if (first == true) {
                        first = false;
                        menuItem.setSelected(true);
                        this.diagramView.setQuery(query);
                    }
                }
            }

            viewMenu.addSeparator();

            final ButtonGroup fontSizeGroup = new ButtonGroup();
            final JMenu setMinLabelSizeSubMenu = new JMenu(
                    "Set minimum label size");
            JMenuItem fontRangeMenuItem = new JRadioButtonMenuItem("None");
            fontSizeGroup.add(fontRangeMenuItem);
            fontRangeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    final JMenuItem source = (JMenuItem) e.getSource();
                    ToscanaJMainPanel.this.diagramView.setMinimumFontSize(0);
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
                        final int newFontSize = Integer.parseInt(source
                                .getText());
                        ToscanaJMainPanel.this.diagramView
                                .setMinimumFontSize(newFontSize);
                        source.setSelected(true);
                    }
                });
                if (this.diagramView.getMinimumFontSize() == fontRange) {
                    fontRangeMenuItem.setSelected(true);
                }
                fontRange += 2;
                setMinLabelSizeSubMenu.add(fontRangeMenuItem);
            }
            viewMenu.add(setMinLabelSizeSubMenu);
        }

        final JMenu colorModeMenu = new JMenu("Color schema");
        final ButtonGroup colorModeGroup = new ButtonGroup();

        final Collection<DiagramSchema> colorSchemas = DiagramSchema
                .getSchemas();
        for (final DiagramSchema schema : colorSchemas) {
            final JRadioButtonMenuItem colorSchemaItem = new JRadioButtonMenuItem(
                    schema.getName());
            colorSchemaItem.addActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    schema.setAsCurrent();
                    setDiagramSchema(schema);
                }
            });
            if (schema == DiagramSchema.getCurrentSchema()) {
                colorSchemaItem.setSelected(true);
            }
            colorModeGroup.add(colorSchemaItem);
            colorModeMenu.add(colorSchemaItem);
        }
        viewMenu.add(colorModeMenu);

        viewMenu.addSeparator();

        final JFrame parent = this;
        final JMenuItem preferencesMenuItem = new JMenuItem("Preferences...");
        preferencesMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                if (!preferences.getBoolean("hidePreferencesWarning", false)) {
                    final Object[] options = { "Return", "Change Preferences",
                            "Disable Warning" };
                    final int retVal = JOptionPane
                            .showOptionDialog(
                                    parent,
                                    "Changing the preferences might affect the behaviour of the"
                                            + "program in unexpected ways.\n"
                                            + "You can restore the original"
                                            + "settings by running ToscanaJ with the \"-reset\" option.",
                                    "Entering preferences",
                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                    JOptionPane.WARNING_MESSAGE, null, options,
                                    options[0]);
                    if (retVal == 0) {
                        return;
                    }
                    if (retVal == 2) {
                        preferences.putBoolean("hidePreferencesWarning", true);
                    }
                }
                final boolean okClicked = ToscanaJPreferences
                        .showPreferences(parent);
                if (okClicked) {
                    setDiagramSchema(DiagramSchema.getCurrentSchema());
                    // at least the line grouping for the equivalence classes
                    // need
                    // a full update of the diagram
                    ToscanaJMainPanel.this.diagramView
                            .showDiagram(ToscanaJMainPanel.this.diagramView
                                    .getDiagram());
                    if (ToscanaJMainPanel.this.diagramPreview != null) {
                        ToscanaJMainPanel.this.diagramPreview
                                .showDiagram(ToscanaJMainPanel.this.diagramPreview
                                        .getDiagram());
                    }
                    buildMenuBar();
                }
            }
        });
        viewMenu.add(preferencesMenuItem);

        // create a help menu
        final JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        this.menubar.add(Box.createHorizontalGlue());
        this.menubar.add(helpMenu);

        // add description entries if available
        if (this.conceptualSchema != null) {
            boolean entriesAdded = false;
            final Element description = this.conceptualSchema.getDescription();
            if (description != null) {
                final JMenuItem descItem = new JMenuItem(
                        "System Description...");
                descItem.setMnemonic(KeyEvent.VK_S);
                descItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1,
                        0));
                descItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        showSchemaDescription();
                    }
                });
                helpMenu.add(descItem);
                entriesAdded = true;
            }
            if (this.conceptualSchema.hasDiagramDescription()) {
                this.diagramDescriptionMenuItem = new JMenuItem(
                        "Diagram Description...");
                this.diagramDescriptionMenuItem.setMnemonic(KeyEvent.VK_D);
                this.diagramDescriptionMenuItem.setAccelerator(KeyStroke
                        .getKeyStroke(KeyEvent.VK_F1, ActionEvent.SHIFT_MASK));
                this.diagramDescriptionMenuItem
                        .addActionListener(new ActionListener() {
                            public void actionPerformed(final ActionEvent e) {
                                showDiagramDescription();
                            }
                        });
                this.diagramDescriptionMenuItem.setEnabled(false);
                helpMenu.add(this.diagramDescriptionMenuItem);
                entriesAdded = true;
            }
            if (entriesAdded) {
                helpMenu.addSeparator();
            }
        }

        final JMenuItem aboutItem = new JMenuItem("About ToscanaJ...");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                showAboutDialog(parent);
            }
        });
        helpMenu.add(aboutItem);
        this.menubar.updateUI();
    }

    private void setDiagramGradient(final Gradient gradient,
            final IntervalType intervalType) {
        final DiagramSchema diagramSchema = this.diagramView.getDiagramSchema();
        diagramSchema.setGradientType(intervalType);
        diagramSchema.setGradient(gradient);
        this.diagramView.update(this);
        if (this.diagramPreview != null) {
            this.diagramPreview.update(this);
        }
    }

    protected void setDiagramSchema(final DiagramSchema schema) {
        this.diagramView.setDiagramSchema(schema);
        if (this.diagramPreview != null) {
            this.diagramPreview.setDiagramSchema(schema);
        }
    }

    /**
     * Build the ToolBar.
     */
    private void buildToolBar() {
        this.toolbar = new JToolBar();
        this.toolbar.setFloatable(true);
        this.toolbar.add(this.openFileAction);
        this.toolbar.add(this.goBackAction);
        this.toolbar.add(Box.createHorizontalGlue());
        this.diagramContextDescriptionButton = new JButton(
                "Analysis History...");
        this.diagramContextDescriptionButton
                .addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        showDiagramContextDescription();
                    }
                });
        this.diagramContextDescriptionButton.setVisible(true);
        this.diagramContextDescriptionButton.setEnabled(false);
        this.toolbar.add(this.diagramContextDescriptionButton);
        this.schemaDescriptionButton = new JButton("About System...");
        this.schemaDescriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                showSchemaDescription();
            }
        });
        this.schemaDescriptionButton.setVisible(false);
        this.toolbar.add(this.schemaDescriptionButton);
        this.diagramDescriptionButton = new JButton("About Diagram...");
        this.diagramDescriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                showDiagramDescription();
            }
        });
        this.diagramDescriptionButton.setVisible(false);
        this.diagramDescriptionButton.setEnabled(false);
        this.toolbar.add(this.diagramDescriptionButton);
    }

    /**
     * Enable or disable relevant buttons and menus depending on boolean isOpen
     * (referring to the face if any file/s is open ).
     */
    protected void resetButtons(final boolean isOpen) {
        // menues
        this.showAllMenuItem.setEnabled(isOpen);
        this.showExactMenuItem.setEnabled(isOpen);
        this.filterExactMenuItem.setEnabled(isOpen);
        this.filterAllMenuItem.setEnabled(isOpen);
    }

    /**
     * Callback for listening to changes on DiagramController.
     * 
     * Updates the buttons / menu entries.
     */
    public void update(final Object source) {
        final DiagramController diagContr = DiagramController.getController();
        this.printMenuItem
                .setEnabled(diagContr.getDiagramHistory().getSize() != 0);
        this.exportDiagramAction.setEnabled((diagContr.getDiagramHistory()
                .getSize() != 0)
                && (this.diagramExportSettings != null));
        this.goBackAction.setEnabled(diagContr.undoIsPossible());
        if ((this.diagramDescriptionButton != null)
                && (this.diagramDescriptionMenuItem != null)) {
            final Diagram2D curDiag = diagContr.getCurrentDiagram();
            if (curDiag != null) {
                boolean showAboutDiagramComponents;
                if (diagContr.getDiagramHistory().getNumberOfCurrentDiagrams() == 1) {
                    final Element diagDesc = curDiag.getDescription();
                    showAboutDiagramComponents = diagDesc != null;
                } else {
                    final Diagram2D outerDiagram = diagContr
                            .getDiagramHistory().getCurrentDiagram(0);
                    final Element outerDiagDesc = outerDiagram.getDescription();
                    final Diagram2D innerDiagram = diagContr
                            .getDiagramHistory().getCurrentDiagram(1);
                    final Element innerDiagDesc = innerDiagram.getDescription();
                    showAboutDiagramComponents = (outerDiagDesc != null)
                            || (innerDiagDesc != null);
                }
                this.diagramDescriptionButton
                        .setEnabled(showAboutDiagramComponents);
                this.diagramDescriptionMenuItem
                        .setEnabled(showAboutDiagramComponents);
            } else {
                this.diagramDescriptionButton.setEnabled(false);
                this.diagramDescriptionMenuItem.setEnabled(false);
            }
        }
        if ((this.diagramContextDescriptionButton != null)) {
            boolean diagramOpened = false;
            if (diagContr.getDiagramHistory().getNumberOfCurrentDiagrams() != 0) {
                diagramOpened = true;
            }
            this.diagramContextDescriptionButton.setEnabled(diagramOpened);
        }
    }

    /**
     * Close Main Window (Exit the program).
     */
    private void closeMainPanel() {
        // close dialogs to make sure their settings are stored properly
        this.readingHelpDialog.closeDialog();

        // store current position
        preferences.storeWindowPlacement(this);
        preferences.putInt("mainDivider", this.splitPane.getDividerLocation());
        if (this.leftHandPane != null) {
            preferences.putInt("secondaryDivider", this.leftHandPane
                    .getDividerLocation());
        }
        // save the MRU list
        preferences.putStringList("mruFiles", this.mruList);
        // store the minimum label size
        preferences.putDouble("minLabelFontSize", this.diagramView
                .getMinimumFontSize());

        if (DatabaseConnection.getConnection().isConnected()) {
            try {
                DatabaseConnection.getConnection().disconnect();
            } catch (final DatabaseException e) {
                ErrorDialog.showError(this, e, "Closing database error",
                        "Some error closing the old database:\n"
                                + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        System.exit(0);
    }

    /**
     * Open a schema using the file open dialog.
     */
    protected void openSchema() {
        final JFileChooser openDialog;
        if (this.currentFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.currentFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        final ExtensionFileFilter csxFilter = new ExtensionFileFilter(
                new String[] { "csx" }, "Conceptual Schema");
        openDialog.setFileFilter(csxFilter);
        openDialog.addChoosableFileFilter(csxFilter);
        final int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        openSchemaFile(openDialog.getSelectedFile());
    }

    /**
     * Open a file and parse it to create ConceptualSchema.
     */
    protected void openSchemaFile(final File schemaFile) {
        // store current file
        try {
            setTitle(schemaFile.getName().substring(0,
                    ((schemaFile.getName()).length() - 4))
                    + " - " + WINDOW_TITLE);
            this.currentFile = schemaFile.getCanonicalPath();
        } catch (final IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = schemaFile.getAbsolutePath();
            // / @todo what could be done here?
        }
        DatabaseViewerManager.resetRegistry();
        if (DatabaseConnection.getConnection().isConnected()) {
            try {
                DatabaseConnection.getConnection().disconnect();
            } catch (final DatabaseException e) {
                ErrorDialog.showError(this, e, "Closing database error",
                        "Some error closing the old database:\n"
                                + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        DatabaseInfo databaseInfo;
        try {
            this.conceptualSchema = CSXParser.parse(this.broker, schemaFile);
            databaseInfo = this.conceptualSchema.getDatabaseInfo();
            if (databaseInfo != null) {
                DatabaseConnection.getConnection().connect(databaseInfo);
                final URL location = databaseInfo.getEmbeddedSQLLocation();
                if (location != null) {
                    DatabaseConnection.getConnection().executeScript(location);
                }
            }
        } catch (final FileNotFoundException e) {
            ErrorDialog.showError(this, e, "File access error", e.getMessage());
            return;
        } catch (final IOException e) {
            ErrorDialog.showError(this, e, "Parsing the file error",
                    "Some error happened when parsing the file:\n"
                            + e.getMessage());
            return;
        } catch (final DataFormatException e) {
            ErrorDialog.showError(this, e, "Parsing the file error",
                    "Some error happened when parsing the file:\n"
                            + e.getMessage());
            return;
        } catch (final DatabaseException e) {
            ErrorDialog.showError(this, e,
                    "Error initializing database connection", "Error report:\n"
                            + e.getMessage());
            e.printStackTrace();
            return;
        } catch (final Exception e) {
            ErrorDialog.showError(this, e, "Parsing the file error",
                    "Some error happened when parsing the file:\n"
                            + e.getMessage());
            e.printStackTrace();
            return;
        }
        this.diagramView.showDiagram(null);
        if (this.diagramPreview != null) {
            this.diagramPreview.showDiagram(null);
        }
        final DiagramController controller = DiagramController.getController();
        final ConceptInterpretationContext interpretationContext = new ConceptInterpretationContext(
                controller.getDiagramHistory(), this.broker);
        this.diagramView.setConceptInterpreter(this.conceptualSchema
                .getConceptInterpreter());
        this.diagramView.setConceptInterpretationContext(interpretationContext);
        updateLabelViews();
        this.diagramOrganiser.setConceptualSchema(this.conceptualSchema);
        DiagramController.getController().reset();
        DiagramController.getController().addObserver(this.diagramView);

        // enable relevant buttons and menus
        this.fileIsOpen = true;
        resetButtons(this.fileIsOpen);
        if (this.conceptualSchema.getDescription() != null) {
            this.schemaDescriptionButton.setVisible(true);
        } else {
            this.schemaDescriptionButton.setVisible(false);
        }
        if (this.conceptualSchema.hasDiagramDescription()) {
            this.diagramDescriptionButton.setVisible(true);
        } else {
            this.diagramDescriptionButton.setVisible(false);
        }

        // update MRU list
        if (this.mruList.contains(this.currentFile)) {
            // if it is already in, just remove it and add it at the end
            this.mruList.remove(this.currentFile);
        }
        this.mruList.add(this.currentFile);
        if (this.mruList.size() > MaxMruFiles) {
            this.mruList.remove(0);
        }

        // tell the viewer about it (so relative links can be resolved)
        DescriptionViewer.setBaseLocation(this.currentFile);

        // recreate the menus
        buildMenuBar();
        recreateMruMenu();
    }

    /**
     * Sets all labels to the display options currently selected.
     */
    private void updateLabelViews() {
        this.diagramView.setDisplayType(this.showExactMenuItem.isSelected());
    }

    /**
     * Recreates the menu of most recently used files and enables it if it is
     * not empty.
     */
    private void recreateMruMenu() {
        this.mruMenu.removeAll();
        boolean empty = true; // will be used to check if we have at least one
        // entry
        if (this.mruList.size() > 0) {
            final ListIterator<String> it = this.mruList
                    .listIterator(this.mruList.size() - 1);
            while (it.hasPrevious()) {
                final String cur = it.previous();
                if (cur.equals(this.currentFile)) {
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

    /**
     * Prints the diagram using the current settings.
     * 
     * If we don't have a diagram at the moment we just return.
     */
    protected void printDiagram() {
        if (DiagramController.getController().getDiagramHistory().getSize() != 0) {
            final PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob.printDialog()) {
                try {
                    printJob.setPrintable(this.diagramView, this.pageFormat);
                    printJob.print();
                } catch (final Exception e) {
                    ErrorDialog.showError(this, e, "Printing failed");
                }
            }
        }
    }

    public void showDiagramContextDescription() {
        if (DiagramController.getController().getDiagramHistory()
                .getNumberOfCurrentDiagrams() != 0) {
            final DiagramContextDescriptionDialog dialog = new DiagramContextDescriptionDialog(
                    this, this.diagramView.getController().getEventBroker());
            dialog.showDescription();
        }
    }

    protected void showSchemaDescription() {
        DescriptionViewer.show(this, this.conceptualSchema.getDescription());
    }

    public void showDiagramDescription() {
        if (DiagramController.getController().getDiagramHistory()
                .getNestingLevel() == 0) {
            DescriptionViewer.show(this, DiagramController.getController()
                    .getCurrentDiagram().getDescription());
        } else { // we assume we have a nesting level of two
            final JPopupMenu popupMenu = new JPopupMenu();
            popupMenu.setLabel("Choose diagram");
            JMenuItem menuItem;
            final JFrame window = this;
            final DiagramController diagContr = DiagramController
                    .getController();
            final Diagram2D outerDiagram = diagContr.getDiagramHistory()
                    .getCurrentDiagram(0);
            final Element outerDiagDesc = outerDiagram.getDescription();
            if (outerDiagDesc != null) {
                menuItem = new JMenuItem("Outer Diagram");
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        DescriptionViewer.show(window, outerDiagDesc);
                    }
                });
                popupMenu.add(menuItem);
            }
            final Diagram2D innerDiagram = diagContr.getDiagramHistory()
                    .getCurrentDiagram(1);
            final Element innerDiagDesc = innerDiagram.getDescription();
            if (innerDiagDesc != null) {
                menuItem = new JMenuItem("Inner Diagram");
                menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        DescriptionViewer.show(window, innerDiagDesc);
                    }
                });
                popupMenu.add(menuItem);
            }
            popupMenu.show(this, -22222, -22222); // show it somewhere where it
            // is not seen
            // we need to show it to get its width afterwards (observed on JDK
            // 1.4.0_01/WinXP)
            // we can't get a really good position, since it can be invoked
            // either by the toolbar button,
            // the help menu entry or a keyboard shortcut, so we just put it
            // somewhere around the area
            // of the toolbar button and the menu entry
            popupMenu.setLocation(this.getX() + this.diagramView.getX()
                    + this.diagramView.getWidth() - popupMenu.getWidth(), this
                    .getY()
                    + this.diagramView.getY()
                    + this.menubar.getHeight()
                    + this.toolbar.getHeight());
        }
    }

    public static void showAboutDialog(final JFrame parent) {
        JOptionPane
                .showMessageDialog(
                        parent,
                        "This program is part of ToscanaJ "
                                + ToscanaJ.VersionString
                                + ".\n\n"
                                + "Copyright (c) DSTC Pty Ltd, Technische Universit�t Darmstadt and the\n"
                                + "University of Queensland\n\n"
                                + "This product includes software developed by the "
                                + "Apache Software Foundation (http://www.apache.org/).\n\n"
                                + "See http://toscanaj.sourceforge.net for more information.",
                        "About this program", JOptionPane.PLAIN_MESSAGE);
    }

    public void lostOwnership(final Clipboard clipboard,
            final Transferable comments) {
        // mandatory method to implement for the copy to systemClipboard
        // function
        // don't have to do anything
        // see exportImage(File selectedFile) method
    }

    public ConceptualSchema getConceptualSchema() {
        return this.conceptualSchema;
    }

    private void updateWindowTitle() {
        // get the current filename without the extension and full path
        // we have to use '\\' instead of '\' although we're checking for the
        // occurrence of '\'.
        if (this.currentFile != null) {
            final String filename = this.currentFile.substring(this.currentFile
                    .lastIndexOf("\\") + 1, (this.currentFile.length() - 4));
            setTitle(filename + " - " + WINDOW_TITLE);
        } else {
            setTitle(WINDOW_TITLE);
        }
    }
}
