/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupelware.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.ndimlayout.DimensionCreationStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.gui.MainPanel;
import net.sourceforge.toscanaj.gui.action.OpenFileAction;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.lattice.ConceptImplementation;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.lattice.LatticeImplementation;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;

import javax.swing.*;

import org.tockit.events.EventBroker;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/// @todo check if the file we save to exists, warn if it does

public class TupelwareMainPanel extends JFrame implements MainPanel {
    private EventBroker eventBroker;
    static private final int MaxMruFiles = 8;
	private static final String WINDOW_TITLE = "Tupelware";

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
    private static final DimensionCreationStrategy DimensionStrategy = new DefaultDimensionStrategy();
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

    public TupelwareMainPanel(String string) {
    }

    private void createLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(toolBar, BorderLayout.NORTH);
        mainPanel.add(diagramView, BorderLayout.CENTER);
        setContentPane(mainPanel);
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
            ConceptImplementation concept = new ConceptImplementation();
            LatticeImplementation lattice = new LatticeImplementation();
            lattice.addConcept(concept);
            addDiagram(lattice, result);
        }
    }

    private void addDiagram(Lattice lattice, String name) {
        Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, name, DimensionStrategy);
        conceptualSchema.addDiagram(diagram);
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
		
        OpenFileAction openFileAction = new OpenFileAction(
                this,
                new LoadConceptualSchemaActivity(eventBroker),
                currentFile,
                KeyEvent.VK_O,
                KeyStroke.getKeyStroke(
                        KeyEvent.VK_O,
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

    private void loadTupels(File schemaFile) {
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
