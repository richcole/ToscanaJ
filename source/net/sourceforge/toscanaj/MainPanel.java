package net.sourceforge.toscanaj;

import net.sourceforge.toscanaj.canvas.ImageGenerationException;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.fca.DiagramController;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;

import net.sourceforge.toscanaj.observer.ChangeObserver;

import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;

import net.sourceforge.toscanaj.view.DiagramOrganiser;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.NodeView;
import net.sourceforge.toscanaj.view.dialogs.DatabaseChooser;
import net.sourceforge.toscanaj.view.dialogs.DiagramExportDialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;

/**
 *  This class provides the main GUI panel with menus and a toolbar
 *  for ToscanaJ.
 */
public class MainPanel extends JFrame implements ActionListener, ChangeObserver {
    /**
     * The maximum number of files in the most recently used files list.
     */
    static private final int MaxMruFiles = 6;

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

    /**
     * switches debug mode
     */
    public static boolean debug = false;

    // buttons list
    private JButton openButton = null;
    private JButton backButton = null;

    // menu items list
    // FILE menu
    private JMenuItem openMenuItem = null;
    private JMenuItem exportDiagramMenuItem = null;
    private JMenuItem printMenuItem = null;
    private JMenuItem printSetupMenuItem = null;
    private JMenu mruMenu = null;
    private JMenuItem exitMenuItem = null;

    //file->auto/manual
    private JMenu setupDiagramMenuItem = null;
    private JRadioButtonMenuItem autoExportDiagramMenuItem = null;
    private JRadioButtonMenuItem manualExportDiagramMenuItem = null;

    // DIAGRAM menu
    private JMenuItem backMenuItem = null;

    private JRadioButtonMenuItem filterAllMenuItem = null;
    private JRadioButtonMenuItem filterExactMenuItem = null;

    // nesting submenu
    private JRadioButtonMenuItem noNestingMenuItem = null;
    private JRadioButtonMenuItem nestingLevel1MenuItem = null;
    private JRadioButtonMenuItem nestingLevel2MenuItem = null;

    // view menu
    private JRadioButtonMenuItem showAllMenuItem = null;
    private JRadioButtonMenuItem showExactMenuItem = null;

    private JRadioButtonMenuItem numDocMenuItem = null;
    private JRadioButtonMenuItem listDocMenuItem = null;

    private JCheckBoxMenuItem percDistMenuItem = null;

    // view->color menu
    private JMenuItem circleColorMenuItem = null;
    private JMenuItem topColorMenuItem = null;
    private JMenuItem bottomColorMenuItem = null;

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
    DiagramOrganiser diagramOrganiser;

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
     * The last setup for page format given by the user.
     */
    private PageFormat pageFormat = new PageFormat();

    /**
     * Simple initialisation constructor.
     */
    public MainPanel() {
        super("ToscanaJ");
        buildPanel();
        // listen to changes on DiagramController
        DiagramController.getController().addObserver(this);
        // restore the old MRU list
        mruList = ConfigurationManager.fetchStringList("mainPanel", "mruFiles", MaxMruFiles);
    }

    /**
     * This constructor opens the file given as url in the parameter.
     *
     * Used when opening ToscanaJ with a file name on the command line.
     */
    public MainPanel( String schemaFileURL ) {
        // do the normal initialisation first
        this();
        // open the file
        openSchemaFile( new File(schemaFileURL) );
    }


    /**
     * Build the GUI.
     */
    private void buildPanel() {
        buildMenuBar();
        setJMenuBar(menubar);

        buildToolBar();

        //Lay out the content pane.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        diagramView = new DiagramView();
        diagramOrganiser = new DiagramOrganiser(this.conceptualSchema);

        //Create a split pane with the two scroll panes in it.
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   diagramView, diagramOrganiser);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        diagramView.setMinimumSize(minimumSize);
        diagramOrganiser.setMinimumSize(minimumSize);

        contentPane.add(this.toolbar, BorderLayout.NORTH);
        contentPane.add(splitPane, BorderLayout.CENTER);

        setContentPane( contentPane );
        // restore old position
        ConfigurationManager.restorePlacement("mainWindow",this, new Rectangle(10,10,600,450));
        int div = ConfigurationManager.fetchInt("mainPanel","divider",400);
        splitPane.setDividerLocation(div);
    }

    /**
     *  build the MenuBar
     */
    private void buildMenuBar() {
        // create menu bar

        menubar = new JMenuBar();

        // create the FILE menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menubar.add(fileMenu);

        // menu item OPEN
        openMenuItem = new JMenuItem("Open...");
                        //new ImageIcon(IMAGE_PATH + OPEN_ICON));
        openMenuItem.setMnemonic(KeyEvent.VK_O);
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);

        // menu item export diagram
        exportDiagramMenuItem = new JMenuItem("Export Diagram...");
        exportDiagramMenuItem.setMnemonic(KeyEvent.VK_E);
        exportDiagramMenuItem.addActionListener(this);
        exportDiagramMenuItem.setEnabled(false);
        fileMenu.add(exportDiagramMenuItem);

        // create the export diagram save options submenu
        this.setupDiagramMenuItem = new JMenu("Export Diagram Setup");
        this.setupDiagramMenuItem.setMnemonic(KeyEvent.VK_N);
        this.setupDiagramMenuItem.setEnabled(false);
        fileMenu.add(setupDiagramMenuItem);
        // export diagram button group
        ButtonGroup exportDiagramGroup = new ButtonGroup();
        // auto export
        this.autoExportDiagramMenuItem = new JRadioButtonMenuItem("Auto");
        this.autoExportDiagramMenuItem.setSelected(true);
        this.autoExportDiagramMenuItem.setMnemonic(KeyEvent.VK_E);
        this.autoExportDiagramMenuItem.addActionListener(this);
        this.setupDiagramMenuItem.add(autoExportDiagramMenuItem);
        exportDiagramGroup.add(this.autoExportDiagramMenuItem);
        //manual export
        this.manualExportDiagramMenuItem = new JRadioButtonMenuItem("Manual");
        this.manualExportDiagramMenuItem.setMnemonic(KeyEvent.VK_E);
        this.manualExportDiagramMenuItem.addActionListener(this);
        this.setupDiagramMenuItem.add(manualExportDiagramMenuItem);
        exportDiagramGroup.add(this.manualExportDiagramMenuItem);

        // separator
        fileMenu.addSeparator();

        // menu item PRINT
        printMenuItem = new JMenuItem("Print");
        printMenuItem.setMnemonic(KeyEvent.VK_P);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        printMenuItem.addActionListener(this);
        printMenuItem.setEnabled(false);
        fileMenu.add(printMenuItem);

        // menu item PRINT SETUP
        printSetupMenuItem = new JMenuItem("Print Setup");
        printSetupMenuItem.addActionListener(this);
        printSetupMenuItem.setEnabled(true);
        fileMenu.add(printSetupMenuItem);

        // separator
        fileMenu.addSeparator();

        // recent edited files will be in this menu
        mruMenu = new JMenu("Reopen");
        mruMenu.setMnemonic(KeyEvent.VK_R);
        mruMenu.setEnabled(false);
        fileMenu.add(mruMenu);

        // separator
        fileMenu.addSeparator();

        // menu item EXIT
        exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_F4, ActionEvent.ALT_MASK));
        exitMenuItem.setMnemonic(KeyEvent.VK_E);
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);

        // create the DIAGRAM menu
        JMenu diagrMenu = new JMenu("Diagram");
        diagrMenu.setMnemonic(KeyEvent.VK_D);
        menubar.add(diagrMenu);

        this.backMenuItem = new JMenuItem("Go Back one Diagram");
        this.backMenuItem.addActionListener(this);
        this.backMenuItem.setEnabled(false);
        diagrMenu.add(backMenuItem);

        // separator
        diagrMenu.addSeparator();

        // menu radio buttons group:
        ButtonGroup documentsFilterGroup = new ButtonGroup();

        this.filterExactMenuItem = new JRadioButtonMenuItem("Filter: use only exact matches");
        this.filterExactMenuItem.addActionListener(this);
        documentsFilterGroup.add(this.filterExactMenuItem);
        diagrMenu.add(this.filterExactMenuItem);

        this.filterAllMenuItem = new JRadioButtonMenuItem("Filter: use all matches");
        this.filterAllMenuItem.setSelected(true);
        this.filterAllMenuItem.addActionListener(this);
        documentsFilterGroup.add(this.filterAllMenuItem);
        diagrMenu.add(this.filterAllMenuItem);

        // separator
        diagrMenu.addSeparator();

        // create the nesting submenu
        JMenu nestingMenu = new JMenu("Nesting");
        nestingMenu.setMnemonic(KeyEvent.VK_N);
        diagrMenu.add(nestingMenu);

        ButtonGroup nestingGroup = new ButtonGroup();

        this.noNestingMenuItem = new JRadioButtonMenuItem("No Nesting");
        this.noNestingMenuItem.addActionListener(this);
        this.noNestingMenuItem.setSelected(true);
        nestingGroup.add(noNestingMenuItem);
        nestingMenu.add(noNestingMenuItem);

        this.nestingLevel1MenuItem = new JRadioButtonMenuItem("One level");
        this.nestingLevel1MenuItem.addActionListener(this);
        nestingGroup.add(nestingLevel1MenuItem);
        nestingMenu.add(nestingLevel1MenuItem);

        this.nestingLevel2MenuItem = new JRadioButtonMenuItem("Two levels");
        this.nestingLevel2MenuItem.addActionListener(this);
        nestingGroup.add(nestingLevel2MenuItem);
        nestingMenu.add(nestingLevel2MenuItem);

        // create the view menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        menubar.add(viewMenu);

        // menu radio buttons group:
        ButtonGroup labelContentGroup = new ButtonGroup();

        // menu radio buttons group:
        ButtonGroup documentsDisplayGroup = new ButtonGroup();

        this.showExactMenuItem = new JRadioButtonMenuItem("Show only exact matches");
        this.showExactMenuItem.setSelected(true);
        this.showExactMenuItem.addActionListener(this);
        documentsDisplayGroup.add(this.showExactMenuItem);
        viewMenu.add(this.showExactMenuItem);

        this.showAllMenuItem = new JRadioButtonMenuItem("Show all matches");
        this.showAllMenuItem.addActionListener(this);
        documentsDisplayGroup.add(this.showAllMenuItem);
        viewMenu.add(this.showAllMenuItem);

        // separator
        viewMenu.addSeparator();

        // radio button menu item NUMBER OF DOCUMENTS
        numDocMenuItem = new JRadioButtonMenuItem("Number Of Documents");
        numDocMenuItem.setSelected(true);
        numDocMenuItem.addActionListener(this);
        labelContentGroup.add(numDocMenuItem);
        viewMenu.add(numDocMenuItem);

        // radio button menu item LIST OF DOCUMENTS
        listDocMenuItem = new JRadioButtonMenuItem("List Of Documents");
        listDocMenuItem.addActionListener(this);
        labelContentGroup.add(listDocMenuItem);
        viewMenu.add(listDocMenuItem);

        // separator
        viewMenu.addSeparator();

        // menu item PERCENTUAL DISTRIBUTION
        percDistMenuItem = new JCheckBoxMenuItem("Percentual Distribution");
        percDistMenuItem.addActionListener(this);
        percDistMenuItem.setState(false);
        viewMenu.add(percDistMenuItem);

        // separator
        viewMenu.addSeparator();

        // create the Color submenu
        JMenu colorMenu = new JMenu("Color");
        colorMenu.setMnemonic(KeyEvent.VK_V);
        viewMenu.add(colorMenu);

        this.circleColorMenuItem = new JMenuItem("Circles...");
        this.circleColorMenuItem.addActionListener(this);
        colorMenu.add(circleColorMenuItem);

        this.topColorMenuItem = new JMenuItem("Top...");
        this.topColorMenuItem.addActionListener(this);
        colorMenu.add(topColorMenuItem);

        this.bottomColorMenuItem = new JMenuItem("Bottom...");
        this.bottomColorMenuItem.addActionListener(this);
        colorMenu.add(bottomColorMenuItem);
    }


    /**
     *  Build the ToolBar.
     */
    private void buildToolBar() {
        toolbar = new JToolBar();
        toolbar.setFloatable(true);

        openButton = new JButton(" Open ");
        openButton.addActionListener(this);
        toolbar.add(openButton);

        backButton = new JButton(" Back ");
        backButton.addActionListener(this);
        backButton.setEnabled(false);
        toolbar.add(backButton);
    }

    /**
     * Enable or disable relevant buttons and menus depending
     * on boolean isOpen (referring to the face if any file/s is
     * open ).
     */
    public void resetButtons(boolean isOpen) {
        // menues
        this.showAllMenuItem.setEnabled (isOpen);
        this.showExactMenuItem.setEnabled (isOpen);
        this.filterExactMenuItem.setEnabled (isOpen);
        this.filterAllMenuItem.setEnabled (isOpen);
        this.numDocMenuItem.setEnabled (isOpen);
        this.listDocMenuItem.setEnabled (isOpen);
        this.percDistMenuItem.setEnabled (isOpen);
    }

    /**
     * Callback for listening to changes on DiagramController.
     *
     * Updates the buttons / menu entries.
     */
    public void update(Object source) {
        this.printMenuItem.setEnabled(DiagramController.getController().getDiagramHistory().getSize()!=0);
        this.exportDiagramMenuItem.setEnabled(DiagramController.getController().getDiagramHistory().getSize()!=0);
        this.setupDiagramMenuItem.setEnabled(DiagramController.getController().getDiagramHistory().getSize()!=0);
        this.backMenuItem.setEnabled(DiagramController.getController().undoIsPossible());
        this.backButton.setEnabled(DiagramController.getController().undoIsPossible());
    }

    /**
     * Close Main Window (Exit the program).
     */
    private void closeMainPanel () {
        // store current position
        ConfigurationManager.storePlacement("mainWindow",this);
        ConfigurationManager.storeInt("mainPanel","divider",splitPane.getDividerLocation());
        // save the MRU list
        ConfigurationManager.storeStringList("mainPanel","mruFiles",this.mruList);
        // and save the whole configuration
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

    public void actionPerformed (ActionEvent ae) {
        Object actionSource = ae.getSource();
        //System.out.println ("actionSource = " + actionSource);
        //System.out.println ("action command = " + ae.getActionCommand());

        // Button actions
        if (actionSource == openButton) {
            openSchema();
        }

        // Menus actions

        // menu FILE
        if (actionSource == openMenuItem) {
            openSchema();
        }
        if(actionSource == this.autoExportDiagramMenuItem) {
            this.diagramExportSettings = new DiagramExportSettings(  1,
                                                                      this.diagramView.getWidth(),
                                                                      this.diagramView.getHeight() );
        }
        if (actionSource == exportDiagramMenuItem) {
            //manualy export diagram is the delault setting
            if(this.autoExportDiagramMenuItem.isSelected()){
                autoExportDiagram();
            } else {
                manualExportDiagram();
            }
        }
        if (actionSource == manualExportDiagramMenuItem) {
            showDiagramExportDialog();
        }
        if (actionSource == printMenuItem) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob.printDialog()) {
                try {
                    printJob.setPrintable(this.diagramView,pageFormat);
                    printJob.print();
                }
                catch (Exception PrintException) {
                    PrintException.printStackTrace();
                }
            }
        }
        if (actionSource == printSetupMenuItem) {
            pageFormat = PrinterJob.getPrinterJob().pageDialog(pageFormat);
        }
        if (actionSource == exitMenuItem) {
            closeMainPanel();
        }

        // diagram view
        if (actionSource == this.filterExactMenuItem) {
            DiagramController.getController().setFilterMethod(DiagramController.FILTER_CONTINGENT);
        }
        if (actionSource == this.filterAllMenuItem) {
            DiagramController.getController().setFilterMethod(DiagramController.FILTER_EXTENT);
        }
        // the back button/menu entry
        if( (actionSource == this.backButton) ||
            (actionSource == this.backMenuItem) )
        {
            DiagramController.getController().back();
        }
        // nesting
        if (actionSource == this.noNestingMenuItem) {
            DiagramController.getController().setNestingLevel(0);
        }
        if (actionSource == this.nestingLevel1MenuItem) {
            DiagramController.getController().setNestingLevel(1);
        }
        if (actionSource == this.nestingLevel2MenuItem) {
            DiagramController.getController().setNestingLevel(2);
        }

        // view menu
        if( (actionSource == this.showExactMenuItem) ||
            (actionSource == this.showAllMenuItem) ||
            (actionSource == this.numDocMenuItem) ||
            (actionSource == this.listDocMenuItem) )
        {
            updateLabelViews();
        }
        if (actionSource == this.percDistMenuItem) {
            this.diagramView.setShowPercentage(this.percDistMenuItem.getState());
        }

        // the color entries
        if( actionSource == this.circleColorMenuItem ) {
            Color newColor = JColorChooser.showDialog(this, "Change circle color", NodeView.getCircleColor());
            if(newColor != null) {
                NodeView.setCircleColor(newColor);
            }
            repaint();
        }
        if( actionSource == this.topColorMenuItem ) {
            Color newColor = JColorChooser.showDialog(this, "Change gradient color", NodeView.getTopColor());
            if(newColor != null) {
                NodeView.setTopColor(newColor);
            }
            repaint();
        }
        if( actionSource == this.bottomColorMenuItem ) {
            Color newColor = JColorChooser.showDialog(this, "Change gradient color", NodeView.getBottomColor());
            if(newColor != null) {
                NodeView.setBottomColor(newColor);
            }
            repaint();
        }
    }

    // temp member to hold the current export diagram setting
    static private DiagramExportSettings diagramExportSettings = null;

    /**
     * store the latest file path
     */
    private String imagePath = null;


    // DiagramExportSettings stores the default image setting

    private class DiagramExportSettings {
        private int format;
        private int width;
        private int height;
        public DiagramExportSettings(int format, int width, int height) {
            this.format = format;
            this.width = width;
            this.height = height;
        }
        /**
         * get the current image format
         */
        public int getImageFormat(){ return this.format; }

        /**
         * get the current image width
         */
        public int getImageWidth() { return this.width; }

        /**
         * get the current image height
         */
        public int getImageHeight() { return this.height; }

    }

    private boolean showDiagramExportDialog() {
        if(diagramExportSettings != null) {
            DiagramExportDialog.initialize(this, diagramExportSettings.getImageWidth(), diagramExportSettings.getImageHeight());
        } else {
            DiagramExportDialog.initialize(this, diagramView.getWidth(), diagramView.getHeight());
        }
        if(DiagramExportDialog.showDialog(this)) {
            this.diagramExportSettings = new DiagramExportSettings(  DiagramExportDialog.getImageFormat(),
                                                                      DiagramExportDialog.getImageWidth(),
                                                                      DiagramExportDialog.getImageHeight() );
            return true;
        }
        return false;
    }

    /**
     * DiagramExportDialog called to manualy export a diagram
     *
     */
    private void manualExportDiagram() {
        if( diagramExportSettings == null) {
            if(showDiagramExportDialog() == false) {
                return;
            }
        }
        final JFileChooser saveDialog =
                        new JFileChooser( (imagePath == null)? "user.dir" : imagePath );
        int rv = saveDialog.showSaveDialog( this );
        if( rv == JFileChooser.APPROVE_OPTION ) {
            try {
                imagePath = saveDialog.getSelectedFile().getPath();
                this.diagramView.exportGraphic( diagramExportSettings.getImageFormat(),
                                                diagramExportSettings.getImageWidth(),
                                                diagramExportSettings.getImageHeight(),
                                                imagePath );
            }
            catch(ImageGenerationException e) {
                /// @TODO give some feedback here
            }
        }
    }

    /**
     * method automatically export a diagram
     *
     */
    private void autoExportDiagram() {
        final JFileChooser saveDialog =
                        new JFileChooser( (imagePath == null)? "user.dir" : imagePath );
        int rv = saveDialog.showSaveDialog( this );
        if( rv == JFileChooser.APPROVE_OPTION ) {
            try {
                imagePath = saveDialog.getSelectedFile().getPath();
                System.out.println(imagePath);
                this.diagramView.exportGraphic( 1,
                                                this.diagramView.getWidth(),
                                                this.diagramView.getHeight(),
                                                imagePath );
            }
            catch(ImageGenerationException e) {
                /// @TODO give some feedback here
            }
        }
    }

    /**
     * Open a schema using the file open dialog.
     */
    protected void openSchema() {
        final JFileChooser openDialog =
                        new JFileChooser( System.getProperty( "user.dir" ) );
        int rv=openDialog.showOpenDialog( this );
        if( rv != JFileChooser.APPROVE_OPTION )
        {
            return;
        }
        openSchemaFile( openDialog.getSelectedFile() );
    }

    /**
     * Open a file and parse it to create ConceptualSchema.
     */
    protected void openSchemaFile(File schemaFile) {
        // parse it
        try {
            conceptualSchema = CSXParser.parse(schemaFile);
        }
        catch( FileNotFoundException e) {
            JOptionPane.showMessageDialog( this,
                    "Couldn't access the file.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE );
            System.err.println( e.getMessage() );
            return;
        }
        catch( IOException e) {
            JOptionPane.showMessageDialog( this,
                    "Some error happened when parsing the file:\n" +
                        e.getMessage(),
                    "File/XML error",
                    JOptionPane.ERROR_MESSAGE );
            System.err.println( e.getMessage() );
            return;
        }
        catch( DataFormatException e) {
            JOptionPane.showMessageDialog( this,
                    "Some error happened when parsing the file:\n" +
                        e.getMessage(),
                    "CSX error",
                    JOptionPane.ERROR_MESSAGE );
            System.err.println( e.getMessage() );
            return;
        }

        // if database should be used, but is not given --> ask user
        if( conceptualSchema.usesDatabase() &&
          ( conceptualSchema.getDatabaseInfo() == null ) ) {
            DatabaseChooser.initialize( this, new DatabaseInfo() );
            DatabaseInfo dbInfo = DatabaseChooser.showDialog( this );
            if( dbInfo == null )
            {
                return;
            }
            else
            {
                conceptualSchema.setDatabaseInformation( dbInfo );
            }
        }
        diagramView.showDiagram(null);
        updateLabelViews();
        diagramView.setShowPercentage(this.percDistMenuItem.isSelected());
        diagramOrganiser.setConceptualSchema(conceptualSchema);
        DiagramController.getController().reset();
        DiagramController.getController().addObserver(this.diagramView);

        // enable relevant buttons and menus
        fileIsOpen = true;
        resetButtons(fileIsOpen);

        // update MRU list
        if(!this.mruList.contains(schemaFile.getPath())) {
            this.mruList.add(schemaFile.getPath());
        }
        if(this.mruList.size() > MaxMruFiles) {
            this.mruList.remove(0);
        }

        // recreate MRU menu
        this.mruMenu.removeAll();
        Iterator it = mruList.iterator();
        while(it.hasNext()) {
            String cur = (String) it.next();
            if(cur.equals(schemaFile.getPath())) {
                // don't enlist the current file
                continue;
            }
            JMenuItem mruItem = new JMenuItem(cur);
            mruItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JMenuItem menuItem = (JMenuItem) e.getSource();
                    openSchemaFile(new File(menuItem.getText()));
                }
            });
            this.mruMenu.add(mruItem);
        }
        // we have now at least one file
        this.mruMenu.setEnabled(this.mruList.size() > 1);
    }

    /**
     * Sets all labels to the display options currently selected.
     */
    private void updateLabelViews(){
        if(this.numDocMenuItem.isSelected()) {
            this.diagramView.setDisplayType(LabelView.DISPLAY_NUMBER, this.showExactMenuItem.isSelected());
        }
        else {
            this.diagramView.setDisplayType(LabelView.DISPLAY_LIST, this.showExactMenuItem.isSelected());
        }
    }

    /**
     *  Main method for running the program
     */
    public static void main(String [] args) {
        final MainPanel test;
        if(args.length == 1) {
          test = new MainPanel(args[0]);
        } else if(args.length == 2) {
          if(args[1].equals("-debug")) {
            test = new MainPanel(args[0]);
            debug = true;
          } else {
            System.err.println("\nCommand line arguments: <schemaFile> <-debug>");
            return;
          }
        } else {
          test = new MainPanel();
        }

        test.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                test.closeMainPanel();
            }
        });

        test.setVisible(true);
    }
}