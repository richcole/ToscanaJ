package net.sourceforge.toscanaj;

import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.parser.CSXParser;
import net.sourceforge.toscanaj.parser.DataFormatException;
import net.sourceforge.toscanaj.view.DiagramOrganiser;
import net.sourceforge.toscanaj.view.diagram.DiagramView;
import net.sourceforge.toscanaj.view.dialogs.DatabaseChooser;

import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JSplitPane;

/**
 *  This class provides the main GUI panel with menus and a toolbar
 *  for ToscanaJ.
 */
public class MainPanel extends JFrame implements ActionListener {

  private JToolBar toolbar = null;
  private JMenuBar menubar = null;
  /**
   * switches debug mode
   */
  public static boolean debug = false;

      // buttons list
  private JButton openButton     = null;


    // menu items list
    // FILE menu
  private JMenuItem openMenuItem		= null;
  private JMenuItem printMenuItem		= null;
  private JMenuItem printSetupMenuItem= null;
  private JMenuItem exitMenuItem		= null;

  // DIAGRAM menu
  private ButtonGroup documentsDisplayGroup = null;
  private JRadioButtonMenuItem showAllMenuItem		= null;
  private JRadioButtonMenuItem showExactMenuItem		= null;

  private ButtonGroup documentsFilterGroup = null;
  private JRadioButtonMenuItem filterAllMenuItem		= null;
  private JRadioButtonMenuItem filterExactMenuItem		= null;

  private ButtonGroup labelContentGroup = null;
  private JMenuItem numDocMenuItem = null;
  private JMenuItem percDistMenuItem = null;
  private JMenuItem listDocMenuItem = null;

  private int currentSelectedIndex;

  // icon images
  private static final String OPEN_ICON          = "open.gif";
  private static final String CONTENTS_ICON      = "contents.gif";
  private static final String CLEAR_ICON          = "clear.gif";

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

  // specify the location of icon images - this is platform safe
  // because the String is converted into a URL so the "/" is OK for
  // all platforms.
  private static final String IMAGE_PATH         = "resource/icons/";

  // flag to indicate if the save icon and menu options should be
  // enabled
  public boolean fileIsOpen = false;

    /**
     * Simple initialisation constructor.
     */
    public MainPanel() {
        super("ToscanaJ");
        buildPanel();
        // try to set Windows LnF
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        }
        catch( Exception e ) {
            // we don't really care if it fails -- just print message on stderr
            System.err.println("Warning: could not set Windows Look and Feel");
        }
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

        // buildToolBar();

        //Lay out the content pane.
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        diagramView = new DiagramView();
        diagramOrganiser = new DiagramOrganiser(this.conceptualSchema);

        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   diagramView, diagramOrganiser);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(1);

        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 50);
        diagramView.setMinimumSize(minimumSize);
        diagramOrganiser.setMinimumSize(minimumSize);

        contentPane.add(splitPane, BorderLayout.CENTER);

        setContentPane( contentPane );
        contentPane.setPreferredSize(new Dimension(550, 400));
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

        // separator
        fileMenu.addSeparator();

        // menu item PRINT
        printMenuItem = new JMenuItem("Print");
        printMenuItem.setMnemonic(KeyEvent.VK_P);
        printMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                 KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        printMenuItem.addActionListener(this);
        printMenuItem.setEnabled(fileIsOpen);
        fileMenu.add(printMenuItem);

        // menu item PRINT SETUP
        printSetupMenuItem = new JMenuItem("Print Setup");
        printSetupMenuItem.addActionListener(this);
        printSetupMenuItem.setEnabled(fileIsOpen);
        fileMenu.add(printSetupMenuItem);

        // separator
        fileMenu.addSeparator();

        // recent edited files should come here
        JMenuItem dummyMenu = new JMenuItem("recent files here");
        dummyMenu.setEnabled(false);
        fileMenu.add(dummyMenu);

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

        // menu radio buttons group:
        this.documentsDisplayGroup = new ButtonGroup();

        this.showExactMenuItem = new JRadioButtonMenuItem("Show only exact matches");
        this.showExactMenuItem.setSelected(true);
        this.showExactMenuItem.addActionListener(this);
        documentsDisplayGroup.add(this.showExactMenuItem);
        diagrMenu.add(this.showExactMenuItem);

        this.showAllMenuItem = new JRadioButtonMenuItem("Show all matches");
        this.showAllMenuItem.addActionListener(this);
        documentsDisplayGroup.add(this.showAllMenuItem);
        diagrMenu.add(this.showAllMenuItem);

        // separator
        diagrMenu.addSeparator();

        // menu radio buttons group:
        this.documentsFilterGroup = new ButtonGroup();

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

        // menu radio buttons group:
        this.labelContentGroup = new ButtonGroup();

        // radio button menu item NUMBER OF DOCUMENTS
        numDocMenuItem = new JRadioButtonMenuItem("Number Of Documents");
        numDocMenuItem.setSelected(true);
        numDocMenuItem.addActionListener(this);
        labelContentGroup.add(numDocMenuItem);
        diagrMenu.add(numDocMenuItem);

        // radio button menu item PERCENTUAL DISTRIBUTION
        percDistMenuItem = new JRadioButtonMenuItem("Percentual Distribution");
        percDistMenuItem.addActionListener(this);
        labelContentGroup.add(percDistMenuItem);
        diagrMenu.add(percDistMenuItem);

        // radio button menu item LIST OF DOCUMENTS
        listDocMenuItem = new JRadioButtonMenuItem("List Of Documents");
        listDocMenuItem.addActionListener(this);
        labelContentGroup.add(listDocMenuItem);
        diagrMenu.add(listDocMenuItem);
    }


    /**
     *  build the ToolBar
     */

    private void buildToolBar() {
        toolbar = new JToolBar();
        toolbar.setFloatable(true);

        // Open button
        openButton = new JButton(new ImageIcon(IMAGE_PATH + OPEN_ICON));
        openButton.setToolTipText("Open");
        openButton.addActionListener(this);
        toolbar.add(openButton);
    }

    /**
     * Enable or disable relevant buttons and menus depending
     * on boolean isOpen (referring to the face if any file/s is
     * open ).
     */
    public void resetButtons(boolean isOpen) {
        // menues
        printMenuItem.setEnabled (isOpen);
        printSetupMenuItem.setEnabled (isOpen);

        this.showAllMenuItem.setEnabled (isOpen);
        this.showAllMenuItem.setEnabled (isOpen);
        this.filterExactMenuItem.setEnabled (isOpen);
        this.filterAllMenuItem.setEnabled (isOpen);
        numDocMenuItem.setEnabled (isOpen);
        percDistMenuItem.setEnabled (isOpen);
        listDocMenuItem.setEnabled (isOpen);
    }

    /**
     * Close Main Window (Exit the program).
     */
    private void closeMainPanel () {
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
        if (actionSource == printMenuItem) {
            System.out.println("Action for menu item: Print "); // stub
        }
        if (actionSource == printSetupMenuItem) {
            System.out.println("Action for menu item: Print Setup "); // stub
        }
        if (actionSource == exitMenuItem) {
            System.out.println("Action for menu item: exit ");
            closeMainPanel();
        }

        // menu DIAGRAM
        if (actionSource == this.showExactMenuItem) {
        }
        if (actionSource == this.showAllMenuItem) {
        }
        if (actionSource == this.filterExactMenuItem) {
        }
        if (actionSource == this.filterAllMenuItem) {
        }
        if (actionSource == numDocMenuItem) {
            System.out.println("Acton for menu group: Number Of Documents ");
        }
        if (actionSource == percDistMenuItem) {
            System.out.println("Acton for menu group: Percentual Distribution");
        }
        if (actionSource == listDocMenuItem) {
            System.out.println("Acton for menu group: List Of Documents ");
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
        diagramOrganiser.setConceptualSchema(conceptualSchema);
        DiagramHistory.getDiagramHistory().clear();
        DiagramHistory.getDiagramHistory().addObserver(this.diagramView);

        // enable relevant buttons and menus
        fileIsOpen = true;
        resetButtons(fileIsOpen);
    }

    /**
     *  Main method for running the program
     */
    public static void main(String [] args) {
        MainPanel test;
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
                System.exit(0);
            }
        });

        test.setSize(600,450);
        test.pack();
        test.setVisible(true);
    }

}
