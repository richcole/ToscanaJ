/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.tupleware.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.gui.MainPanel;
import net.sourceforge.toscanaj.gui.action.SaveFileAction;
import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.*;
import net.sourceforge.toscanaj.gui.dialog.CheckDuplicateFileChooser;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.view.diagram.DiagramEditingView;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.tockit.events.EventBroker;
import org.tockit.tupleware.model.TupleSet;
import org.tockit.tupleware.scaling.TupleScaling;
import org.tockit.tupleware.source.TupleSource;
import org.tockit.tupleware.source.text.TextSource;
import org.tockit.tupleware.source.rdql.RdqlQueryEngine;
import org.tockit.tupleware.source.sql.SqlQueryEngine;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

public class TuplewareMainPanel extends JFrame implements MainPanel {
    private static final String CONFIGURATION_SECTION = "TuplewareMainPanel";
	private static final String WINDOW_TITLE = "Tupleware";

    private int[] objectIndices;
    private JTable tupleTable;
    private EventBroker eventBroker;

    /**
     *  Model
     */
    private ConceptualSchema conceptualSchema;
    private TupleSet tuples;

    /**
     * Controls
     */
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu editMenu;
    private JMenu helpMenu;
    private JToolBar toolBar;

    private File lastFileRead = null;

    /**
     * Views
     */
    private DiagramEditingView diagramView;
    private XMLEditorDialog schemaDescriptionView;
	private SaveFileAction saveAsFileAction;
	private SaveConceptualSchemaActivity saveActivity;

    public TuplewareMainPanel() {
        super(WINDOW_TITLE);

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);

        createViews();

        createMenuBar();
        createToolBar();

        createLayout();

        ConfigurationManager.restorePlacement(CONFIGURATION_SECTION, this,
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
        tabPanel.addTab("Tuples", createTuplePanel());
        tabPanel.addTab("Diagrams", diagramView);
        tabPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        return tabPanel;
    }
    
    private Component createTuplePanel() {
        tupleTable = new JTable();
        return new JScrollPane(tupleTable);
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
            IndexSelectionDialog dialog = new IndexSelectionDialog(this, "Select attribute set", this.tuples.getVariableNames());
            dialog.show();
            int[] attributeIndices = dialog.getSelectedIndices();
            Diagram2D diagram = TupleScaling.scaleTuples(this.tuples, this.objectIndices, attributeIndices);
            this.conceptualSchema.addDiagram(diagram);
        }
    }

    public void createViews() {
        diagramView = new DiagramEditingView(this, conceptualSchema, eventBroker);
        diagramView.setDividerLocation(ConfigurationManager.fetchInt(CONFIGURATION_SECTION, "diagramViewDivider", 200));

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
		
		this.saveAsFileAction =
			new SaveFileAction(
				this,
				saveActivity,
				KeyEvent.VK_A,
				KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		this.saveAsFileAction.setPostSaveActivity(new SimpleActivity(){
			public boolean doActivity() throws Exception {
				conceptualSchema.dataSaved();
				return true;
			}
		});
		JMenuItem saveAsMenuItem = new JMenuItem("Save Schema As...");
		saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		saveAsMenuItem.addActionListener(saveAsFileAction);
		fileMenu.add(saveAsMenuItem);

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

		JMenu tuplesMenu = new JMenu("Tuples");
		tuplesMenu.setMnemonic('t');
		
        addTupleSourceMenuItem(tuplesMenu, this, new TextSource());
        addTupleSourceMenuItem(tuplesMenu, this, new SqlQueryEngine());
		addTupleSourceMenuItem(tuplesMenu, this, new RdqlQueryEngine());
		
		tuplesMenu.addSeparator();
		JMenuItem saveTuplesItem = new JMenuItem("Save tuples...");
		saveTuplesItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
            	saveTuples();
            }
        });
        tuplesMenu.add(saveTuplesItem);

		menuBar.add(tuplesMenu);

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

        final Component parent = this;
		JMenuItem aboutItem = new JMenuItem("About Tupleware...");
		aboutItem.setMnemonic(KeyEvent.VK_A);
		aboutItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(parent, "This is a crusty early version of Tupleware, (c) DSTC Pty. Ltd,\n" +
                                                      "University of Queenland and Technical University Darmstadt");
			}
		});
		helpMenu.add(aboutItem);

        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(helpMenu);
    }

    protected void saveTuples() {
		final JFileChooser saveDialog;
		String[] csxExtension = {"csx"};
		ExtensionFileFilter fileFilter = new ExtensionFileFilter(new String[]{"tuples"},"Tuple Set File");
		ExtensionFileFilter[] filterArray = { fileFilter };
		if (this.lastFileRead != null) {
			saveDialog = new CheckDuplicateFileChooser(this.lastFileRead, filterArray);
	        
		} else {
			saveDialog = new CheckDuplicateFileChooser(new File(System.getProperty("user.dir")), filterArray);
		}
		
		if (saveDialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			try {
				this.lastFileRead = saveDialog.getSelectedFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(this.lastFileRead));
				for (int i = 0; i < this.tuples.getVariableNames().length; i++) {
                    String name = this.tuples.getVariableNames()[i];
                    if(i!=0) {
                    	writer.write('\t');
                    }
                    writer.write(name);
                }
                writer.newLine();
                for (Iterator iter = this.tuples.getTuples().iterator(); iter.hasNext();) {
                    Object[] tuple = (Object[]) iter.next();
                    for (int i = 0; i < tuple.length; i++) {
                        Object object = tuple[i];
						if(i!=0) {
							writer.write('\t');
						}
                        writer.write(object.toString());
                    }
					writer.newLine();
                }
                writer.close();
			} catch (IOException e) {
				ErrorDialog.showError(this, e, "Failed to write file");
			}
		}
    }

    private void addTupleSourceMenuItem(JMenu tuplesMenu,
                                        final JFrame parent,
                                        final TupleSource source) {
        JMenuItem menuItem = new JMenuItem(source.getMenuName());
        menuItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                source.show(parent,lastFileRead);
                if(source.getTuples() == null) {
                    return;
                }
                tuples = source.getTuples();
                objectIndices = source.getObjectIndices();
                if(source.getSelectedFile() != null) {
                    lastFileRead = source.getSelectedFile();
                }
                fillTable();     
                conceptualSchema = new ConceptualSchema(eventBroker);   
            }
        });
        tuplesMenu.add(menuItem);
    }

	private void fillTable() {
        Object[][] data = new Object[this.tuples.getTuples().size()][this.tuples.getVariableNames().length];
        int row = 0;
        for (Iterator iter = this.tuples.getTuples().iterator(); iter.hasNext();) {
            Object[] tuple = (Object[]) iter.next();
            for (int col = 0; col < tuple.length; col++) {
                data[row][col] = tuple[col];
            }
            row ++;
        }
        this.tupleTable.setModel(new DefaultTableModel(data, this.tuples.getVariableNames()));
    }

    public void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement(CONFIGURATION_SECTION, this);
        ConfigurationManager.storeInt(CONFIGURATION_SECTION, "diagramViewDivider",
                diagramView.getDividerLocation()
        );
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
