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
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.db.DatabaseException;
import net.sourceforge.toscanaj.controller.diagram.*;
import net.sourceforge.toscanaj.controller.fca.*;
import net.sourceforge.toscanaj.dbviewer.DatabaseViewerManager;
import net.sourceforge.toscanaj.gui.dialog.DescriptionViewer;
import net.sourceforge.toscanaj.gui.dialog.DiagramContextDescriptionDialog;
import net.sourceforge.toscanaj.gui.dialog.DiagramExportSettingsDialog;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.diagram.*;
import org.jdom.Element;
import org.tockit.canvas.CanvasBackground;
import org.tockit.canvas.events.CanvasItemActivatedEvent;
import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.events.CanvasItemSelectedEvent;
import org.tockit.canvas.imagewriter.DiagramExportSettings;
import org.tockit.canvas.imagewriter.GraphicFormat;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.canvas.imagewriter.ImageGenerationException;
import org.tockit.events.EventBroker;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;


/**
 *  This class provides the main GUI panel with menus and a toolbar
 *  for ToscanaJ.
 *
 * @todo store view settings (contingent/extent labels/gradient) in session management
 */
public class ToscanaJMainPanel extends JFrame implements ChangeObserver, ClipboardOwner {

    /**
     * The central event broker for the main panel
     */
    private EventBroker broker;

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
    private Action exportDiagramAction;
    private Action goBackAction;

    // buttons list
    private JButton diagramDescriptionButton = null;
    private JButton schemaDescriptionButton = null;
    private JButton diagramContextDescriptionButton = null;

    // menu items list
    // FILE menu
    private JMenuItem exportDiagramSetupMenuItem = null;
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
    private JRadioButtonMenuItem showAllMenuItem = null;
    private JRadioButtonMenuItem showExactMenuItem = null;

    /**
     * The main model member.
     */
    private ConceptualSchema conceptualSchema;

    /**
     * The diagram viewing area.
     */
    private DiagramView diagramView;

    /**
     * The pane for selecting the diagrams.
     */
    private DiagramOrganiser diagramOrganiser;

    /**
     * Flag to indicate if the save icon and menu options should be
     * enabled
     */
    private boolean fileIsOpen = false;

    /**
     * Keeps a list of most recently files.
     */
    private List mruList = new LinkedList();

    /**
     * Stores the file name of the currently open file.
     */
    private String currentFile = null;

    /**
     * Stores the last file where an image was exported to.
     */
    private File lastImageExportFile = null;

    /**
     * The last setup for page format given by the user.
     */
    private PageFormat pageFormat = new PageFormat();

    /**
     * The current settings for diagram export.
     */
    private DiagramExportSettings diagramExportSettings = null;

	/**
	 * Simple initialisation constructor.
	 */
    public ToscanaJMainPanel() {
        super("ToscanaJ");
        broker = new EventBroker();
        conceptualSchema = new ConceptualSchema(broker);
        DatabaseConnection.initialize(broker);

        // register all image writers we want to support
        try {
			org.tockit.canvas.imagewriter.BatikImageWriter.initialize();
        } catch (Throwable t) {
        	// do nothing, we just don't support SVG
        }
        // the next one is part of JDK 1.4, so it should give us JPG and PNG all the time
       	org.tockit.canvas.imagewriter.ImageIOImageWriter.initialize();

        // set the default diagram export options: auto mode, no format defined yet, no size
        // if there is no format, we don't set the settings, which causes the menu items to be unavailable 
        Iterator it = GraphicFormatRegistry.getIterator();
        if (it.hasNext()) {
            this.diagramExportSettings = new DiagramExportSettings(null, 0, 0, true);
        }

        // then build the panel (order is important for checking if we want export options)
        buildPanel();
        // listen to changes on DiagramController
        DiagramController.getController().addObserver(this);
        // we are the parent window for anything database viewers / report generators want to display
        DatabaseViewerManager.setParentComponent(this);
        // restore the old MRU list
        mruList = ConfigurationManager.fetchStringList("ToscanaJMainPanel", "mruFiles", MaxMruFiles);
        // set up the menu for the MRU files
        recreateMruMenu();
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile = new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                openSchemaFile(schemaFile);
            }
        }
        // restore the last image export position
        String lastImage = ConfigurationManager.fetchString("ToscanaJMainPanel", "lastImageExport", null);
        if (lastImage != null) {
            this.lastImageExportFile = new File(lastImage);
        }

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    /**
     * This constructor opens the file given as url in the parameter.
     *
     * Used when opening ToscanaJ with a file name on the command line.
     */
    public ToscanaJMainPanel(String schemaFileURL) {
        // do the normal initialisation first
        this();
        // open the file
        openSchemaFile(new File(schemaFileURL));
    }

    /**
     * Build the GUI.
     */
    private void buildPanel() {
        diagramView = new DiagramView();

        createActions();
        buildMenuBar();
        setJMenuBar(menubar);
        buildToolBar();

        //Lay out the content pane.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        DiagramController controller = DiagramController.getController();
        /// @todo move the subscriptions into the handlers
        EventBroker diagramEventBroker = diagramView.getController().getEventBroker();
        diagramEventBroker.subscribe(
                new FilterOperationEventListener(controller),
                CanvasItemActivatedEvent.class,
                NodeView.class
        );
        diagramEventBroker.subscribe(
                new HighlightingOperationEventListener(diagramView),
                CanvasItemSelectedEvent.class,
                NodeView.class
        );
        diagramEventBroker.subscribe(
                new HighlightRemovalOperationEventListener(diagramView),
                CanvasItemSelectedEvent.class,
                CanvasBackground.class
        );
        diagramEventBroker.subscribe(
                new ObjectLabelViewPopupMenuHandler(diagramView, this.broker),
                CanvasItemContextMenuRequestEvent.class,
                ObjectLabelView.class
        );
        diagramEventBroker.subscribe(
                new ObjectLabelViewOpenDisplayHandler(),
                CanvasItemActivatedEvent.class,
                ObjectLabelView.class
        );
        diagramEventBroker.subscribe(
                new AttributeLabelViewPopupMenuHandler(diagramView, this.broker),
                CanvasItemContextMenuRequestEvent.class,
                AttributeLabelView.class
        );
		diagramEventBroker.subscribe(
				new NodeViewPopupMenuHandler(diagramView),
				CanvasItemContextMenuRequestEvent.class,
				NodeView.class
		);
		new LabelClickEventHandler(diagramEventBroker);
        new LabelDragEventHandler(diagramEventBroker);

        diagramOrganiser = new DiagramOrganiser(this.conceptualSchema);



        //Create a split pane with the two scroll panes in it.

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,

                diagramOrganiser, diagramView);

        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(50, 100);
        diagramView.setMinimumSize(minimumSize);
        diagramOrganiser.setMinimumSize(minimumSize);
        contentPane.add(this.toolbar, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);
        setContentPane(contentPane);
        // restore old position
        ConfigurationManager.restorePlacement("ToscanaJMainPanel", this, new Rectangle(10, 10, 600, 450));
        int div = ConfigurationManager.fetchInt("ToscanaJMainPanel", "divider", 200);
        splitPane.setDividerLocation(div);
    }

    private void createActions() {
        this.openFileAction = new AbstractAction("Open...") {
            public void actionPerformed(ActionEvent e) {
                openSchema();
            }
        };
        this.openFileAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
        this.openFileAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        this.exportDiagramAction = new AbstractAction("Export Diagram...") {
            public void actionPerformed(ActionEvent e) {
                exportImage();
            }
        };
        this.exportDiagramAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_E));
        this.exportDiagramAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
        this.exportDiagramAction.setEnabled(false);
        this.goBackAction = new AbstractAction("Go Back one Diagram") {
            public void actionPerformed(ActionEvent e) {
                DiagramController.getController().back();
            }
        };
        this.goBackAction.putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_B));
        this.goBackAction.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, ActionEvent.ALT_MASK));
        this.goBackAction.setEnabled(false);
        /// @todo Change all the other actions into Actions.
    }

    /**
     *  build the MenuBar
     */
    private void buildMenuBar() {
        if (menubar == null) {
            // create menu bar
            menubar = new JMenuBar();
        } else {
            menubar.removeAll();
        }

        // create the FILE menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menubar.add(fileMenu);
        fileMenu.add(this.openFileAction);

        // we add the export options only if we can export at all
        if (this.diagramExportSettings != null) {
            fileMenu.add(exportDiagramAction);

            // create the export diagram save options submenu
            this.exportDiagramSetupMenuItem = new JMenuItem("Export Diagram Setup...");
            this.exportDiagramSetupMenuItem.setMnemonic(KeyEvent.VK_S);
            this.exportDiagramSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
            this.exportDiagramSetupMenuItem.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					showImageExportOptions();
				}
            });
            fileMenu.add(exportDiagramSetupMenuItem);
        }

        // separator
        fileMenu.addSeparator();

        // menu item PRINT
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

        // menu item PRINT SETUP
        printSetupMenuItem = new JMenuItem("Print Setup...");
        printSetupMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				pageFormat = PrinterJob.getPrinterJob().pageDialog(pageFormat);
		  		printDiagram();
			}
		});
        printSetupMenuItem.setEnabled(true);
        fileMenu.add(printSetupMenuItem);

        // separator
        fileMenu.addSeparator();

        // recent edited files will be in this menu
        mruMenu = new JMenu("Reopen");
        mruMenu.setMnemonic(KeyEvent.VK_R);
        fileMenu.add(mruMenu);

        // separator
        fileMenu.addSeparator();

        // menu item EXIT
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        exitMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				closeMainPanel();
			}
		});
        fileMenu.add(exitMenuItem);

        // create the DIAGRAM menu
        JMenu diagrMenu = new JMenu("Diagram");
        diagrMenu.setMnemonic(KeyEvent.VK_D);
        menubar.add(diagrMenu);
        diagrMenu.add(goBackAction);
        diagrMenu.addSeparator();

        // menu radio buttons group:
        ButtonGroup documentsFilterGroup = new ButtonGroup();
        this.filterExactMenuItem = new JRadioButtonMenuItem("Filter: use only exact matches");
        this.filterExactMenuItem.setMnemonic(KeyEvent.VK_X);
        this.filterExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        this.filterExactMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				diagramView.setFilterMode(ConceptInterpretationContext.CONTINGENT);
				updateLabelViews();
			}
		});
        documentsFilterGroup.add(this.filterExactMenuItem);
        diagrMenu.add(this.filterExactMenuItem);
        this.filterAllMenuItem = new JRadioButtonMenuItem("Filter: use all matches");
        this.filterAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_F, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
        this.filterAllMenuItem.setMnemonic(KeyEvent.VK_A);
        this.filterAllMenuItem.setSelected(true);
        this.filterAllMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				diagramView.setFilterMode(ConceptInterpretationContext.EXTENT);
		  		updateLabelViews();
			}
		});
        documentsFilterGroup.add(this.filterAllMenuItem);
        diagrMenu.add(this.filterAllMenuItem);

        // separator
        diagrMenu.addSeparator();

        // create the nesting submenu
        ButtonGroup nestingGroup = new ButtonGroup();
        this.noNestingMenuItem = new JRadioButtonMenuItem("Flat Diagram");
        this.noNestingMenuItem.setMnemonic(KeyEvent.VK_F);
        this.noNestingMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        this.noNestingMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				DiagramController.getController().setNestingLevel(0);
			}
		});        
        this.noNestingMenuItem.setSelected(true);
        nestingGroup.add(noNestingMenuItem);
        diagrMenu.add(noNestingMenuItem);
        this.nestingLevel1MenuItem = new JRadioButtonMenuItem("Nested Diagram");
        this.nestingLevel1MenuItem.setMnemonic(KeyEvent.VK_N);
        this.nestingLevel1MenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        this.nestingLevel1MenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				DiagramController.getController().setNestingLevel(1);
			}
		}); 
        nestingGroup.add(nestingLevel1MenuItem);
        diagrMenu.add(nestingLevel1MenuItem);

        // create the view menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        menubar.add(viewMenu);

        ButtonGroup documentsDisplayGroup = new ButtonGroup();
        this.showExactMenuItem = new JRadioButtonMenuItem("Show only exact matches");
        this.showExactMenuItem.setMnemonic(KeyEvent.VK_X);
        this.showExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        this.showExactMenuItem.setSelected(true);
        this.showExactMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				updateLabelViews();
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
				updateLabelViews();
			}
		}); 
        documentsDisplayGroup.add(this.showAllMenuItem);
        viewMenu.add(this.showAllMenuItem);

        if (ConfigurationManager.fetchInt("ToscanaJMainPanel", "offerGradientOptions", 0) == 1) {
            viewMenu.addSeparator();
            ButtonGroup colorGradientGroup = new ButtonGroup();
            JRadioButtonMenuItem showExactMenuItem = new JRadioButtonMenuItem("Use colors for exact matches");
            showExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_G, ActionEvent.CTRL_MASK));
            showExactMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    diagramView.getDiagramSchema().setGradientType(DiagramSchema.GRADIENT_TYPE_CONTINGENT);
                    diagramView.update(this);
                }
            });
            colorGradientGroup.add(showExactMenuItem);
            viewMenu.add(showExactMenuItem);

            JRadioButtonMenuItem showAllMenuItem = new JRadioButtonMenuItem("Use colors for all matches");
            showAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_G, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
            showAllMenuItem.setSelected(true);
            showAllMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    diagramView.getDiagramSchema().setGradientType(DiagramSchema.GRADIENT_TYPE_EXTENT);
                    diagramView.update(this);
                }
            });
            colorGradientGroup.add(showAllMenuItem);
            viewMenu.add(showAllMenuItem);
        }

        if (ConfigurationManager.fetchInt("ToscanaJMainPanel", "offerNodeSizeScalingOptions", 0) == 1) {
            viewMenu.addSeparator();
            ButtonGroup nodeSizeScalingGroup = new ButtonGroup();
            JRadioButtonMenuItem nodeSizeExactMenuItem = new JRadioButtonMenuItem("Change node sizes with number of exact matches");
            nodeSizeExactMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_N, ActionEvent.CTRL_MASK));
            nodeSizeExactMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    diagramView.getDiagramSchema().setNodeSizeScalingType(DiagramSchema.NODE_SIZE_SCALING_CONTINGENT);
                    diagramView.update(this);
                }
            });
            nodeSizeScalingGroup.add(nodeSizeExactMenuItem);
            viewMenu.add(nodeSizeExactMenuItem);

            JRadioButtonMenuItem nodeSizeAllMenuItem = new JRadioButtonMenuItem("Change node sizes with number of all matches");
            nodeSizeAllMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_N, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
            nodeSizeAllMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    diagramView.getDiagramSchema().setNodeSizeScalingType(DiagramSchema.NODE_SIZE_SCALING_EXTENT);
                    diagramView.update(this);
                }
            });
            nodeSizeScalingGroup.add(nodeSizeAllMenuItem);
            viewMenu.add(nodeSizeAllMenuItem);

            JRadioButtonMenuItem nodeSizeFixedMenuItem = new JRadioButtonMenuItem("Fixed node sizes");
            nodeSizeFixedMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    diagramView.getDiagramSchema().setNodeSizeScalingType(DiagramSchema.NODE_SIZE_SCALING_NONE);
                    diagramView.update(this);
                }
            });
            nodeSizeScalingGroup.add(nodeSizeFixedMenuItem);
            viewMenu.add(nodeSizeFixedMenuItem);
            
            switch (diagramView.getDiagramSchema().getNodeSizeScalingType()) {
                case DiagramSchema.NODE_SIZE_SCALING_CONTINGENT:
                    nodeSizeExactMenuItem.setSelected(true);
                    break;
                case DiagramSchema.NODE_SIZE_SCALING_EXTENT:
                    nodeSizeAllMenuItem.setSelected(true);
                    break;
                case DiagramSchema.NODE_SIZE_SCALING_NONE:
                    nodeSizeFixedMenuItem.setSelected(true);
                    break;
                default:
                	System.err.println("Unknown case for DiagramSchema.getNodeSizeScalingType() encountered in ToscanaJMainPanel");
            }
        }

        // menu radio buttons group:
        ButtonGroup labelContentGroup = new ButtonGroup();

        /**
         * @todo doing arithmetics on the KeyEvent constants is probably not the proper thing to do,
         *       though I could not find another way to get this. Try again...
         */
        if (this.conceptualSchema != null) {
            Iterator it = this.conceptualSchema.getQueries().iterator();
            if (it.hasNext()) {
                viewMenu.addSeparator();
                boolean first = true;
                String allowedChars = "abcdefghijklmnopqrstuvwxyz";
                String usedChars = "ax";
                int count = 0;
                while (it.hasNext()) {
                    final Query query = (Query) it.next();
                    count++;
                    String name = query.getName();
                    JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(name);
                    for (int i = 0; i < name.length(); i++) {
                        char c = name.toLowerCase().charAt(i);
                        if ((allowedChars.indexOf(c) != -1) && (usedChars.indexOf(c) == -1)) {
                            menuItem.setMnemonic(KeyEvent.VK_A + allowedChars.indexOf(c));
                            usedChars += c;
                            break;
                        }
                    }
                    if (count < 10) { // first ones get their number (starting with 1)
                        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                                KeyEvent.VK_0 + count, ActionEvent.ALT_MASK));
                    }
                    if (count == 10) { // tenth gets the zero
                        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                                KeyEvent.VK_0, ActionEvent.ALT_MASK));
                    } // others don't get an accelerator
                    menuItem.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            diagramView.setQuery(query);
                        }
                    });
                    labelContentGroup.add(menuItem);
                    viewMenu.add(menuItem);
                    if (first == true) {
                        first = false;
                        menuItem.setSelected(true);
                        diagramView.setQuery(query);
                    }
                }
            }
        }

        // create a help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        menubar.add(Box.createHorizontalGlue());
        menubar.add(helpMenu);

        // add description entries if available
        if (this.conceptualSchema != null) {
            boolean entriesAdded = false;
            Element description = this.conceptualSchema.getDescription();
            if (description != null) {
                JMenuItem descItem = new JMenuItem("Schema Description");
                descItem.setMnemonic(KeyEvent.VK_S);
                descItem.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_F1, 0));
                descItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showSchemaDescription();
                    }
                });
                helpMenu.add(descItem);
                entriesAdded = true;
            }
            if (this.conceptualSchema.hasDiagramDescription()) {
                diagramDescriptionMenuItem = new JMenuItem("Diagram Description");
                diagramDescriptionMenuItem.setMnemonic(KeyEvent.VK_D);
                diagramDescriptionMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                        KeyEvent.VK_F1, ActionEvent.SHIFT_MASK));
                diagramDescriptionMenuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        showDiagramDescription();
                    }
                });
                diagramDescriptionMenuItem.setEnabled(false);
                helpMenu.add(diagramDescriptionMenuItem);
                entriesAdded = true;
            }
            if (entriesAdded) {
                helpMenu.addSeparator();
            }
        }
        JMenuItem aboutItem = new JMenuItem("About ToscanaJ");
        aboutItem.setMnemonic(KeyEvent.VK_A);
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aboutItem);
        this.menubar.updateUI();
    }

    /**
     *  Build the ToolBar.
     */
    private void buildToolBar() {
        toolbar = new JToolBar();
        toolbar.setFloatable(true);
        toolbar.add(this.openFileAction);
        toolbar.add(this.goBackAction);
        toolbar.add(Box.createHorizontalGlue());
        diagramContextDescriptionButton = new JButton(" Diagram Context Description... ");
		diagramContextDescriptionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showDiagramContextDescription();
			}
		});
		diagramContextDescriptionButton.setVisible(true);
		diagramContextDescriptionButton.setEnabled(false);
		toolbar.add(diagramContextDescriptionButton);
		schemaDescriptionButton = new JButton(" About Schema... ");
        schemaDescriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showSchemaDescription();
            }
        });
        schemaDescriptionButton.setVisible(false);
        toolbar.add(schemaDescriptionButton);
        diagramDescriptionButton = new JButton(" About Diagram... ");
        diagramDescriptionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showDiagramDescription();
            }
        });
        diagramDescriptionButton.setVisible(false);
        diagramDescriptionButton.setEnabled(false);
        toolbar.add(diagramDescriptionButton);
    }

    /**
     * Enable or disable relevant buttons and menus depending
     * on boolean isOpen (referring to the face if any file/s is
     * open ).
     */
    protected void resetButtons(boolean isOpen) {
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
    public void update(Object source) {
        DiagramController diagContr = DiagramController.getController();
        this.printMenuItem.setEnabled(diagContr.getDiagramHistory().getSize() != 0);
        this.exportDiagramAction.setEnabled(
                (diagContr.getDiagramHistory().getSize() != 0) &&
                (this.diagramExportSettings != null));
        this.goBackAction.setEnabled(diagContr.undoIsPossible());
        if ((this.diagramDescriptionButton != null) && (this.diagramDescriptionMenuItem != null)) {
            Diagram2D curDiag = diagContr.getCurrentDiagram();
            if (curDiag != null) {
            	boolean showAboutDiagramComponents;
            	if( diagContr.getDiagramHistory().getNumberOfCurrentDiagrams() == 1) {
            		Element diagDesc = curDiag.getDescription();
            		showAboutDiagramComponents = diagDesc != null;
            	} else {
            	    Diagram2D outerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(0);
            		Element outerDiagDesc = outerDiagram.getDescription();
            		Diagram2D innerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(1);
                    Element innerDiagDesc = innerDiagram.getDescription();
            		showAboutDiagramComponents = (outerDiagDesc != null) || (innerDiagDesc != null); 
            	}
                this.diagramDescriptionButton.setEnabled(showAboutDiagramComponents);
                this.diagramDescriptionMenuItem.setEnabled(showAboutDiagramComponents);
            } else {
                this.diagramDescriptionButton.setEnabled(false);
                this.diagramDescriptionMenuItem.setEnabled(false);
            }
        }
        if((this.diagramContextDescriptionButton != null)) {
        	boolean diagramOpened = false;
        	if( diagContr.getDiagramHistory().getNumberOfCurrentDiagrams() != 0) {
				diagramOpened = true;
        	}
			this.diagramContextDescriptionButton.setEnabled(diagramOpened);
        }
    }

    /**
     * Close Main Window (Exit the program).
     */
    private void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("ToscanaJMainPanel", this);
        ConfigurationManager.storeInt("ToscanaJMainPanel", "divider", splitPane.getDividerLocation());
        // save the MRU list
        ConfigurationManager.storeStringList("ToscanaJMainPanel", "mruFiles", this.mruList);
        // store last image export position
        if (this.lastImageExportFile != null) {
            ConfigurationManager.storeString("ToscanaJMainPanel", "lastImageExport", this.lastImageExportFile.getPath());
        }
        // and save the whole configuration
        ConfigurationManager.saveConfiguration();

        if (DatabaseConnection.getConnection().isConnected()) {
            try {
                DatabaseConnection.getConnection().disconnect();
            } catch (DatabaseException e) {
                ErrorDialog.showError(this, e, "Closing database error", "Some error closing the old database:\n" + e.getMessage());
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
    	ExtensionFileFilter csxFilter = new ExtensionFileFilter(new String[]{"csx"},"Conceptual Schema");
    	openDialog.setFileFilter(csxFilter);
    	openDialog.addChoosableFileFilter(csxFilter);
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        openSchemaFile(openDialog.getSelectedFile());
    }

    /**
     * Open a file and parse it to create ConceptualSchema.
     */
    protected void openSchemaFile(File schemaFile) {
        // store current file
        try {
            this.currentFile = schemaFile.getCanonicalPath();
        } catch (IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = schemaFile.getAbsolutePath();
            /// @todo what could be done here?
        }
        DatabaseViewerManager.resetRegistry();
        if (DatabaseConnection.getConnection().isConnected()) {
            try {
                DatabaseConnection.getConnection().disconnect();
            } catch (DatabaseException e) {
                ErrorDialog.showError(this, e, "Closing database error", "Some error closing the old database:\n" + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
        DatabaseInfo databaseInfo;
        try {
            conceptualSchema = CSXParser.parse(broker, schemaFile);
            databaseInfo = conceptualSchema.getDatabaseInfo();
            if (databaseInfo != null) {
                DatabaseConnection.getConnection().connect(databaseInfo);
                URL location = databaseInfo.getEmbeddedSQLLocation();
                if (location != null) {
                    DatabaseConnection.getConnection().executeScript(location);
                }
            }
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "File access error", e.getMessage());
            return;
        } catch (IOException e) {
            ErrorDialog.showError(this, e, "Parsing the file error", "Some error happened when parsing the file:\n" + e.getMessage());
            return;
        } catch (DataFormatException e) {
            ErrorDialog.showError(this, e, "Parsing the file error", "Some error happened when parsing the file:\n" + e.getMessage());
            return;
        } catch (DatabaseException e) {
            ErrorDialog.showError(this, e, "Error initializing database connection", "Error report:\n" + e.getMessage());
            e.printStackTrace();
            return;
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Parsing the file error", "Some error happened when parsing the file:\n" + e.getMessage());
            e.printStackTrace();
            return;
        }
        diagramView.showDiagram(null);
        DiagramController controller = DiagramController.getController();
        ConceptInterpreter interpreter = null;
        if (databaseInfo != null) {
            interpreter = new DatabaseConnectedConceptInterpreter(databaseInfo);
        } else {
            interpreter = new DirectConceptInterpreter();
        }
        ConceptInterpretationContext interpretationContext = new ConceptInterpretationContext(controller.getDiagramHistory(),
                broker);
        diagramView.setConceptInterpreter(interpreter);
        diagramView.setConceptInterpretationContext(interpretationContext);
        updateLabelViews();
        diagramOrganiser.setConceptualSchema(conceptualSchema);
        DiagramController.getController().reset();
        DiagramController.getController().addObserver(this.diagramView);

        // enable relevant buttons and menus
        fileIsOpen = true;
        resetButtons(fileIsOpen);
        if (conceptualSchema.getDescription() != null) {
            schemaDescriptionButton.setVisible(true);
        } else {
            schemaDescriptionButton.setVisible(false);
        }
        if (conceptualSchema.hasDiagramDescription()) {
            diagramDescriptionButton.setVisible(true);
        } else {
            diagramDescriptionButton.setVisible(false);
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
     * Recreates the menu of most recently used files and enables it if it is not
     * empty.
     */
    private void recreateMruMenu() {
        this.mruMenu.removeAll();
        boolean empty = true; // will be used to check if we have at least one entry
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

    /**
     * Ask the user for a file name and then exports the current diagram as graphic to it.
     *
     * If there is no diagram to print we will just return.
     *
     * @see #showImageExportOptions()
     */
	protected void exportImage() {
		if (DiagramController.getController().getDiagramHistory().getSize() != 0) {
			if(this.diagramExportSettings.usesAutoMode()) {
				exportImageWithAutoMode();
			} else {
				exportImageWithManualMode();
			}
		}
    }

	protected void exportImageWithAutoMode() {
		if (this.lastImageExportFile == null) {
			this.lastImageExportFile =
				new File(System.getProperty("user.dir"));
		}
		final GraphicFormat graphicFormat = this.diagramExportSettings.getGraphicFormat();
		if(graphicFormat==null){}
		final DiagramExportSettings exportSettings= this.diagramExportSettings;
		final JFileChooser saveDialog =
			new JFileChooser(this.lastImageExportFile) {
			public void approveSelection() {
				File selectedFile = getSelectedFile();
				if(selectedFile.getName().indexOf('.') == -1) { // check for extension
					// add default
					FileFilter filter = getFileFilter();
					if(filter instanceof ExtensionFileFilter) {
						ExtensionFileFilter extFileFilter = (ExtensionFileFilter) filter;
						String[] extensions = extFileFilter.getExtensions();
						selectedFile = new File(selectedFile.getAbsolutePath() + "."+extensions[0]);
						setSelectedFile(selectedFile);
					}	
				}
				if (selectedFile != null && selectedFile.exists()) {
					String warningMessage = "The image file '"	+ selectedFile.getName() + "' already exists.\nDo you want to overwrite the existing file?";
					if(exportSettings.getSaveCommentsToFile()==true) {
						File textFile = new File(selectedFile.getAbsoluteFile()+".txt");
						if(textFile.exists()) {
						warningMessage = "The files '"	+ selectedFile.getName() + "' and '" + textFile.getName()+ "' already exist.\nDo you want to overwrite the existing files?";
						}
					}
					int response =
						JOptionPane.showOptionDialog(
							this,
							warningMessage,
							"File Export Warning: File exists",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,null,new Object[] {"Yes", "No"}, "No");
					if (response != JOptionPane.YES_OPTION) {
						return;
					}
				}
				if(selectedFile!=null && !selectedFile.exists() && exportSettings.getSaveCommentsToFile()==true){
					File textFile = new File(selectedFile.getAbsoluteFile()+".txt");
					if(textFile.exists()) {
						int response =
							JOptionPane.showOptionDialog(
								this,
								"The text file '" + textFile.getName()
								+ "' already exists.\n"
								+"Do you want to overwrite the existing file?",
								"File Export Warning: File exists",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE,null,new Object[] {"Yes", "No"}, "No");
							if (response != JOptionPane.YES_OPTION) {
								return;
							}
					}
					
				}
				super.approveSelection();
			}
		};
		FileFilter defaultFilter = saveDialog.getFileFilter();
		Iterator formatIterator = GraphicFormatRegistry.getIterator();
		while (formatIterator.hasNext()) {
			GraphicFormat format = (GraphicFormat) formatIterator.next();
			ExtensionFileFilter fileFilter = new ExtensionFileFilter(format.getExtensions(),format.getName());
			saveDialog.addChoosableFileFilter(fileFilter);
			if(format == this.diagramExportSettings.getGraphicFormat()) {
				defaultFilter = fileFilter;
			}
		}
		saveDialog.setFileFilter(defaultFilter);
		boolean formatDefined;
		do {
			formatDefined = true;
			int rv = saveDialog.showSaveDialog(this);
			if (rv == JFileChooser.APPROVE_OPTION) {
				File selectedFile = saveDialog.getSelectedFile();
				FileFilter fileFilter = saveDialog.getFileFilter();
				if(fileFilter instanceof ExtensionFileFilter) {
					ExtensionFileFilter extFileFilter = (ExtensionFileFilter) fileFilter;
					String[] extensions = extFileFilter.getExtensions();
					if (selectedFile.getName().indexOf('.') == -1) {
						selectedFile = new File(selectedFile.getAbsolutePath() + "." + extensions[0]);
					}
				}
				GraphicFormat format =
					GraphicFormatRegistry.getTypeByExtension(
						selectedFile);
				if (format != null) {
					this.diagramExportSettings.setGraphicFormat(format);
				} else {
					if(selectedFile.getName().indexOf('.') != -1) {
						JOptionPane.showMessageDialog(
							this,
							"Sorry, no type with this extension known.\n"
								+ "Please use either another extension or try\n"
								+ "manual settings.",
							"Export failed",
							JOptionPane.ERROR_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(
							this,
							"No extension given.\n" +
							"Please give an extension or pick a file type\n" +
							"from the options.",
							"Export failed",
							JOptionPane.ERROR_MESSAGE);
					}	
					formatDefined = false;
				}

				if (formatDefined) {
					exportImage(selectedFile);
				}
			}
		} while (formatDefined == false);
	}

	protected void exportImage(File selectedFile){
		try {
			// Get title of the current diagram
			String title = "";
			String lineSeparator = System.getProperty("line.separator");
			DiagramHistory diagramHistory = DiagramController.getController().getDiagramHistory(); 
			int numCurDiag = diagramHistory.getNumberOfCurrentDiagrams();
			int firstCurrentPos = diagramHistory.getFirstCurrentDiagramPosition();
			for(int i=0; i<numCurDiag; i++) { 
				title += diagramHistory.getElementAt(i+firstCurrentPos).toString();
				if( i < numCurDiag-1 ){
				title += " / ";
				}
				if ( (i == numCurDiag-1) && numCurDiag >1) {
					title += " ( Outer diagram / Inner diagram )";
				}
			}			
			
			// Get description of the diagrams
			String description = DiagramController.getController().getDiagramHistory().getTextualDescription();
			
			// Put title and desc in properties
			Properties metadata = new Properties();
			metadata.setProperty("title", title);
			metadata.setProperty("description", description.trim());
			this
				.diagramExportSettings
				.getGraphicFormat()
				.getWriter()
				.exportGraphic(
				this.diagramView,
				this.diagramExportSettings,
				selectedFile,
				metadata);
				if(this.diagramExportSettings.getSaveCommentsToFile()==true){
					try{
						PrintWriter out = new PrintWriter(new FileWriter(new File(selectedFile.getAbsolutePath()+".txt")));
						out.println("The diagram(s) you have viewed for the resulting image: "+System.getProperty("line.separator")+selectedFile.getAbsolutePath());
						DateFormat dateFormatter = DateFormat.getDateTimeInstance();
						out.println("as at "+dateFormatter.format(new Date(System.currentTimeMillis()))+" is(are): ");
						out.println();
						out.println(DiagramController.getController().getDiagramHistory().getTextualDescription());
						out.close();
					}catch(IOException e){
						ErrorDialog.showError(this, e, "Exporting text file error");
					}
				}
				if(this.diagramExportSettings.getSaveCommentToClipboard()==true){
					DateFormat dateFormatter = DateFormat.getDateTimeInstance();
					String header ="The diagram(s) you have viewed for the resulting image:\n"
					+selectedFile.getAbsolutePath()+"\n"
					+"as at "+dateFormatter.format(new Date(System.currentTimeMillis()))+" is(are): \n";
					StringSelection comments = new StringSelection(header+"\n"+DiagramController.getController().getDiagramHistory().getTextualDescription());
					Clipboard systemClipboard = getToolkit().getSystemClipboard();
					systemClipboard.setContents(comments,ToscanaJMainPanel.this);
				}
		} catch (ImageGenerationException e) {
			ErrorDialog.showError(this, e, "Exporting image error");
		} catch (OutOfMemoryError e) {
			ErrorDialog.showError(
				this,
				"Out of memory",
				"Not enough memory available to export\n"
					+ "the diagram in this size");
		}
		this.lastImageExportFile = selectedFile;
	}
 
	protected void exportImageWithManualMode() {
		if (this.lastImageExportFile == null) {
			this.lastImageExportFile =
				new File(System.getProperty("user.dir"));
		}
		final GraphicFormat format = this.diagramExportSettings.getGraphicFormat();
		final DiagramExportSettings exportSettings = this.diagramExportSettings;
		final JFileChooser saveDialog =
			new JFileChooser(this.lastImageExportFile) {
				public void approveSelection() {
					File selectedFile = getSelectedFile();
					if(selectedFile.getName().indexOf('.') == -1) { // check for extension
						// add default
						String[] extensions = format.getExtensions();
						selectedFile = new File(selectedFile.getAbsolutePath() + "."+extensions[0]);
						setSelectedFile(selectedFile);
					}
					if (selectedFile != null && selectedFile.exists()) {
						String warningMessage = "The image file '"	+ selectedFile.getName() + "' already exists.\nDo you want to overwrite the existing file?";
						if(exportSettings.getSaveCommentsToFile()==true) {
							File textFile = new File(selectedFile.getAbsoluteFile()+".txt");
							if(textFile.exists()) {
								warningMessage = "The files '"	+ selectedFile.getName() + "' and '" + textFile.getName()+ "' already exist.\nDo you want to overwrite the existing files?";
							}
						}
						int response =
							JOptionPane.showOptionDialog(
							this,
							warningMessage,
							"File Export Warning: File exists",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE,null,new Object[] {"Yes", "No"}, "No");
						if (response != JOptionPane.YES_OPTION) {
							return;
						}
					}
					if(selectedFile!=null && !selectedFile.exists() && exportSettings.getSaveCommentsToFile()==true){
						File textFile = new File(selectedFile.getAbsoluteFile()+".txt");
						if(textFile.exists()) {
							int response =
								JOptionPane.showOptionDialog(
								this,
								"The text file '" + textFile.getName()
								+ "' already exists.\n"
								+"Do you want to overwrite the existing file?",
								"File Export Warning: File exists",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.WARNING_MESSAGE,null,new Object[] {"Yes", "No"}, "No");
							if (response != JOptionPane.YES_OPTION) {
								return;
							}
						}
					}
					super.approveSelection();
				}
			};
		GraphicFormat currentFormat = diagramExportSettings.getGraphicFormat();
		ExtensionFileFilter manualFileFilter = new ExtensionFileFilter(currentFormat.getExtensions(),currentFormat.getName());
		saveDialog.addChoosableFileFilter(manualFileFilter);
		int rv = saveDialog.showSaveDialog(this);
		if (rv == JFileChooser.APPROVE_OPTION) {
			File selectedFile = saveDialog.getSelectedFile();
			exportImage(selectedFile);
		}
	}
 
    /**
     * Shows the dialog to change the image export options.
     *
     * If the dialog is closed by pressing ok, the settings will be stored and an
     * export will be initiated.
     */
    protected void showImageExportOptions() {
        if (this.diagramExportSettings.usesAutoMode()) {
            this.diagramExportSettings.setImageSize(this.diagramView.getWidth(), this.diagramView.getHeight());
        }
        DiagramExportSettingsDialog.initialize(this, this.diagramExportSettings);
        DiagramExportSettings rv = DiagramExportSettingsDialog.showDialog(this);
        if (rv != null) {
            this.diagramExportSettings = rv;
            // probably the user wants to export now -- just give him the chance if possible
            exportImage();
        }
    }

    /**
     * Prints the diagram using the current settings.
     *
     * If we don't have a diagram at the moment we just return.
     */
    protected void printDiagram() {
        if (DiagramController.getController().getDiagramHistory().getSize() != 0) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob.printDialog()) {
                try {
                    printJob.setPrintable(this.diagramView, pageFormat);
                    printJob.print();
                } catch (Exception PrintException) {
                    PrintException.printStackTrace();
                }
            }
        }
    }

	protected void showDiagramContextDescription() {
		if(DiagramController.getController().getDiagramHistory().getNumberOfCurrentDiagrams() != 0){
			DiagramContextDescriptionDialog dialog = new DiagramContextDescriptionDialog(this, 
								this.diagramView.getController().getEventBroker());
			dialog.showDescription();
		}
   }
	   
    protected void showSchemaDescription() {
        DescriptionViewer.show(this, this.conceptualSchema.getDescription());
    }

    protected void showDiagramDescription() {
    	if(DiagramController.getController().getDiagramHistory().getNestingLevel() == 0) {
    	    DescriptionViewer.show(this, DiagramController.getController().getCurrentDiagram().getDescription());
    	} else { // we assume we have a nesting level of two
		    JPopupMenu popupMenu = new JPopupMenu();
		    popupMenu.setLabel("Choose diagram");
		    JMenuItem menuItem;
		    final JFrame window = this;
		    DiagramController diagContr = DiagramController.getController();
    	    Diagram2D outerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(0);
    	    final Element outerDiagDesc = outerDiagram.getDescription();
    	    if(outerDiagDesc != null) {
	    	    menuItem = new JMenuItem("Outer Diagram");
	    	    menuItem.addActionListener(new ActionListener() {
	    	        public void actionPerformed(ActionEvent e) {
	    	            DescriptionViewer.show(window, outerDiagDesc);
	    	        }
	    	    });
	    	    popupMenu.add(menuItem);
    	    }
    	    Diagram2D innerDiagram = diagContr.getDiagramHistory().getCurrentDiagram(1);
    	    final Element innerDiagDesc = innerDiagram.getDescription();
    	    if(innerDiagDesc != null) {
	    	    menuItem = new JMenuItem("Inner Diagram");
	    	    menuItem.addActionListener(new ActionListener() {
	    	        public void actionPerformed(ActionEvent e) {
	    	            DescriptionViewer.show(window, innerDiagDesc);
	    	        }
	    	    });
	    	    popupMenu.add(menuItem);
    	    }
		    popupMenu.show(this, -22222, -22222); // show it somewhere where it is not seen
		    // we need to show it to get its width afterwards (observed on JDK 1.4.0_01/WinXP)
		    // we can't get a really good position, since it can be invoked either by the toolbar button,
		    // the help menu entry or a keyboard shortcut, so we just put it somewhere around the area
		    // of the toolbar button and the menu entry
		    popupMenu.setLocation(this.getX() + this.diagramView.getX() + this.diagramView.getWidth() - popupMenu.getWidth(),
		    			          this.getY() + this.diagramView.getY() + this.menubar.getHeight() + this.toolbar.getHeight() );
    	}
    }

    protected void showAboutDialog() {
        JOptionPane.showMessageDialog(this,
                "This is ToscanaJ " + ToscanaJ.VersionString + ".\n\n" +
                "Copyright (c) DSTC Pty Ltd\n\n" +
                "See http://toscanaj.sourceforge.net for more information.",
                "About ToscanaJ",
                JOptionPane.PLAIN_MESSAGE);
    }
    
    public void lostOwnership(Clipboard clipboard, Transferable comments) {
    //mandatory method to implement for the copy to systemClipboard function
    //don't have to do anything
    //see exportImage(File selectedFile) method
    }
}

