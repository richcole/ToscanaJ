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
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.tockit.events.EventBroker;
import org.tockit.tupelware.model.TupelSet;
import org.tockit.tupelware.parser.TupelParser;
import org.tockit.tupelware.scaling.TupelScaling;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/// @todo check if the file we save to exists, warn if it does

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
    private JMenu mruMenu;
    private JMenu editMenu;
    private JMenu helpMenu;
    private JToolBar toolBar;

    private List mruList = new LinkedList();
    private String currentFile = null;

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

        mruList = ConfigurationManager.fetchStringList("TupelwareMainPanel", "mruFiles", MaxMruFiles);
        // if we have at least one MRU file try to open it
        if (this.mruList.size() > 0) {
            File schemaFile = new File((String) mruList.get(mruList.size() - 1));
            if (schemaFile.canRead()) {
                loadTupels(schemaFile);
            }
        }

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
		
        FileActivity loadTupelsActivity = new FileActivity(){
            public void processFile(File file) throws Exception {
                loadTupels(file);
            }
            public boolean prepareToProcess() throws Exception {
                return checkForMissingSave();
            }
            public String[] getExtensions() {
                return new String[]{"tupels"};
            }
            public String getDescription() {
                return "Tupel Sets";
            }
        };
        OpenFileAction openFileAction = new OpenFileAction(
                this,
                loadTupelsActivity,
                currentFile,
                KeyEvent.VK_L,
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_L,
                        ActionEvent.CTRL_MASK
                )
        );
		openFileAction.addPostOpenActivity(new SimpleActivity() {
			public boolean doActivity() throws Exception {
				updateWindowTitle();
				return true;
			}
		});

        JMenuItem openMenuItem = new JMenuItem("Load tupels...");
        openMenuItem.addActionListener(openFileAction);
        fileMenu.add(openMenuItem);

		this.saveAsFileAction =
			new SaveFileAction(
				this,
				saveActivity,
				KeyEvent.VK_A,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		this.saveAsFileAction.setPostSaveActivity(new SimpleActivity(){
			public boolean doActivity() throws Exception {
				currentFile = saveAsFileAction.getLastFileUsed().getPath();
				addFileToMRUList(saveAsFileAction.getLastFileUsed());
				conceptualSchema.dataSaved();
				updateWindowTitle();
				return true;
			}
		});
		JMenuItem saveAsMenuItem = new JMenuItem("Save Schema As...");
		saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		saveAsMenuItem.addActionListener(saveAsFileAction);
		fileMenu.add(saveAsMenuItem);

        mruMenu = new JMenu("Reload tuples");
        recreateMruMenu();
        fileMenu.add(mruMenu);

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

		final JFrame parent = this;
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

    protected void loadTupels(File file) {
        Reader reader;
        try {
            reader = new FileReader(file);
            this.tupels = TupelParser.parseTabDelimitedTupels(reader);
            IndexSelectionDialog dialog = new IndexSelectionDialog(this, "Select object set", this.tupels.getVariableNames());
            dialog.show();
            this.objectIndices = dialog.getSelectedIndices();
            fillTable();     
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);   
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Could not read file");
        }
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

    private void updateWindowTitle() {
	// get the current filename without the extension and full path
	// we have to use '\\' instead of '\' although we're checking for the occurrence of '\'.
		if(currentFile != null){
			String filename = currentFile.substring(currentFile.lastIndexOf("\\")+1,(currentFile.length()-4));
			setTitle(filename +" - "+WINDOW_TITLE);
		} else {
			setTitle(WINDOW_TITLE);
		}
	}

    private void recreateMruMenu() {
        if (mruMenu == null) { // no menu yet
            return;
        }
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
                        loadTupels(new File(menuItem.getText()));
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
        // store current position
        ConfigurationManager.storePlacement("TupelwareMainPanel", this);
        ConfigurationManager.storeStringList("TupelwareMainPanel", "mruFiles", this.mruList);
        ConfigurationManager.storeInt("TupelwareMainPanel", "diagramViewDivider",
                diagramView.getDividerLocation()
        );
        ConfigurationManager.saveConfiguration();
        System.exit(0);
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
}
