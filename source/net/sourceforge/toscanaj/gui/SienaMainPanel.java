/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.gui;

import net.sourceforge.toscanaj.controller.ConfigurationManager;
import net.sourceforge.toscanaj.controller.cernato.CernatoDimensionStrategy;
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
import net.sourceforge.toscanaj.gui.dialog.DiagramExportSettingsDialog;
import net.sourceforge.toscanaj.gui.dialog.ErrorDialog;
import net.sourceforge.toscanaj.gui.dialog.ExtensionFileFilter;
import net.sourceforge.toscanaj.gui.dialog.TemporalMainDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.Context;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.DiagramExportSettings;
import net.sourceforge.toscanaj.model.cernato.CernatoModel;
import net.sourceforge.toscanaj.model.cernato.View;
import net.sourceforge.toscanaj.model.cernato.ViewContext;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaLoadedEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Lattice;
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

import org.jdom.JDOMException;
import org.tockit.canvas.imagewriter.GraphicFormatRegistry;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.List;
import java.util.ListIterator;

/// @todo check if the file we save to exists, warn if it does

public class SienaMainPanel extends JFrame implements MainPanel, EventBrokerListener {
	private static final String CONFIGURATION_SECTION_NAME = "SienaMainPanel";
	private static final String WINDOW_TITLE = "Siena";
	static private final int MaxMruFiles = 8;
	
    /**
     *  Main Controllers
     */
    private EventBroker eventBroker;

    /**
     *  Model
     */
    private ConceptualSchema conceptualSchema;

    /**
     * Controls
     */
    private JMenuBar menuBar;
    private JMenu helpMenu;
    private JMenu fileMenu;
	private JMenu mruMenu;

    /**
     * Views
     */
    private DiagramEditingView diagramEditingView;
	private List mruList = new LinkedList();
    private String currentFile = null;
    private TemporalMainDialog temporalControls;
    private DiagramExportSettings diagramExportSettings;
    private ExportDiagramAction exportDiagramAction;
    private File lastCSCFile;
	private SaveFileAction saveFileAction;
	private SaveConceptualSchemaActivity saveActivity;

    public SienaMainPanel() {
        super(WINDOW_TITLE);

        eventBroker = new EventBroker();
        conceptualSchema = new ConceptualSchema(eventBroker);

        // register all image writers we want to support
        try {
            org.tockit.canvas.imagewriter.BatikImageWriter.initialize();
        } catch (Throwable t) {
            // do nothing, we just don't support SVG
        }
        org.tockit.canvas.imagewriter.ImageIOImageWriter.initialize();

        Iterator it = GraphicFormatRegistry.getIterator();
        if (it.hasNext()) {
            this.diagramExportSettings = new DiagramExportSettings();
        }

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);

        createViews();

        createMenuBar();
		
		mruList = ConfigurationManager.fetchStringList(
				  CONFIGURATION_SECTION_NAME, "mruFiles",
				  MaxMruFiles);
		// if we have at least one MRU file try to open it
		if (this.mruList.size() > 0) {
			System.out.println("at least 1 mru file");
			File schemaFile =
				new File((String) mruList.get(mruList.size() - 1));
			if (schemaFile.canRead()) {
				openSchemaFile(schemaFile);
			}
		}
		
        ConfigurationManager.restorePlacement("SienaMainPanel", this,
                new Rectangle(10, 10, 900, 700));

		if(ConfigurationManager.fetchInt("SienaTemporalControls", "enabled", 0) == 1) {
		    temporalControls = new TemporalMainDialog(this, this.diagramEditingView.getDiagramView(), 
		    										  diagramExportSettings, eventBroker);
		    ConfigurationManager.restorePlacement("SienaTemporalControls", temporalControls, 
		    		new Rectangle(350,350,420,350));
		    temporalControls.show();
		}

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                closeMainPanel();
            }
        });
    }

    public void createViews() {
        diagramEditingView = new DiagramEditingView(conceptualSchema, eventBroker);
        diagramEditingView.getDiagramView().getController().getEventBroker().subscribe(
        								this, DisplayedDiagramChangedEvent.class, Object.class);
        diagramEditingView.setDividerLocation(ConfigurationManager.fetchInt("SienaMainPanel", "diagramViewDivider", 200));
        setContentPane(diagramEditingView);
    }


    public void createMenuBar() {

        // --- menu bar ---
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // --- file menu ---
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);

		SimpleActivity testSchemaSavedActivity = new SimpleActivity(){
			public boolean doActivity() throws Exception {
				return checkForMissingSave();
			}
		};

        NewConceptualSchemaActivity newSchemaActivity = new NewConceptualSchemaActivity(eventBroker);
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

        LoadConceptualSchemaActivity loadSchemaActivity = new LoadConceptualSchemaActivity(eventBroker);
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
		saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		saveMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
        saveActivity = new SaveConceptualSchemaActivity(conceptualSchema, eventBroker);
		this.saveFileAction =	 
				new SaveFileAction(
						this,
						saveActivity,
						KeyEvent.VK_S,
						KeyStroke.getKeyStroke(
								KeyEvent.VK_S,
								ActionEvent.CTRL_MASK
						)
				);
		saveFileAction.setPostSaveActivity(new SimpleActivity(){
			public boolean doActivity() throws Exception {
				currentFile = saveFileAction.getLastFileUsed().getPath();
				addFileToMRUList(saveFileAction.getLastFileUsed());
				conceptualSchema.dataSaved();
				updateWindowTitle();
				return true;
			}
		});
		
        fileMenu.add(saveMenuItem);

		JMenuItem saveAsMenuItem = new JMenuItem("Save As...");
		saveAsMenuItem.setMnemonic(KeyEvent.VK_A);
		saveAsMenuItem.addActionListener(saveFileAction);
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

        JMenuItem importBurmeisterItem = new JMenuItem("Import Burmeister Format...");
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
            exportDiagramAction = new ExportDiagramAction(frame, this.diagramExportSettings,
                                       this.diagramEditingView.getDiagramView(), KeyEvent.VK_E, 
                                       KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
            fileMenu.add(exportDiagramAction);
            exportDiagramAction.setEnabled(false);

            // create the export diagram save options submenu
            JMenuItem exportDiagramSetupMenuItem = new JMenuItem("Export Diagram Setup...");
            exportDiagramSetupMenuItem.setMnemonic(KeyEvent.VK_S);
            exportDiagramSetupMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));
            exportDiagramSetupMenuItem.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {
                    showImageExportOptions();
                }
            });
            fileMenu.add(exportDiagramSetupMenuItem);
            fileMenu.addSeparator();
        }

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
        
        final DiagramView diagramView = this.diagramEditingView.getDiagramView();

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

        final JCheckBoxMenuItem showAttributeLabels = new JCheckBoxMenuItem("Show Attribute Labels");
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

        final JCheckBoxMenuItem showObjectLabels = new JCheckBoxMenuItem("Show Object Labels");
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

        menuBar.add(viewMenu);

        // --- help menu ---
        // create a help menu
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);

		final JFrame parent = this;
		JMenuItem aboutItem = new JMenuItem("About Siena");
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

    protected void showImageExportOptions() {
    	DiagramView diagramView = this.diagramEditingView.getDiagramView();
        if (this.diagramExportSettings.usesAutoMode()) {
            this.diagramExportSettings.setImageSize(diagramView.getWidth(), diagramView.getHeight());
        }
        DiagramExportSettingsDialog.initialize(this, this.diagramExportSettings);
        boolean changesDone = DiagramExportSettingsDialog.showDialog(this);
        if (changesDone && this.diagramEditingView.getDiagramView().getDiagram() != null) {
            this.exportDiagramAction.exportImage();
        }
    }

    private void importCernatoXML() {
        final JFileChooser openDialog;
        if (this.currentFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.currentFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setFileFilter(
            new ExtensionFileFilter(new String[] { "xml" }, "Cernato XML Files"));
        openDialog.setApproveButtonText("Import");
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        importCernatoXML(openDialog.getSelectedFile());
    }

	public void importCernatoXML(String fileLocation) {
		importCernatoXML(new File(fileLocation));
	}
	
    private void importCernatoXML(File file) {
        // store current file
        try {
            this.currentFile = file.getCanonicalPath();
        } catch (IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = file.getAbsolutePath();
            /// @todo what could be done here?
        }
        CernatoModel inputModel;
        try {
            inputModel = CernatoXMLParser.importCernatoXMLFile(file);
        } catch (FileNotFoundException e) {
            ErrorDialog.showError(this, e, "Could not find file");
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
    }

    private void importBurmeister() {
        final JFileChooser openDialog;
        if (this.currentFile != null) {
            // use position of last file for dialog
            openDialog = new JFileChooser(this.currentFile);
        } else {
            openDialog = new JFileChooser(System.getProperty("user.dir"));
        }
        openDialog.setFileFilter(
            new ExtensionFileFilter(new String[] { "cxt" }, "Context Files"));
        openDialog.setApproveButtonText("Import");
        int rv = openDialog.showOpenDialog(this);
        if (rv != JFileChooser.APPROVE_OPTION) {
            return;
        }
        String newSchemaString = "Create new schema";
        String keepSchemaString = "Extend existing schema";
        Object retVal = JOptionPane.showInputDialog(this, "Do you want to keep the current schema?", "Keep Schema?",
                JOptionPane.QUESTION_MESSAGE, null,
                new Object[]{keepSchemaString, newSchemaString},
                keepSchemaString);
        if (retVal == null) {
            return;
        }
        if (retVal == newSchemaString) {
            this.conceptualSchema = new ConceptualSchema(this.eventBroker);
        }
        importBurmeister(openDialog.getSelectedFile());
    }

    private void importBurmeister(File file) {
        // store current file
        try {
            this.currentFile = file.getCanonicalPath();
        } catch (IOException e) { // could not resolve canonical path
            e.printStackTrace();
            this.currentFile = file.getAbsolutePath();
            /// @todo what could be done here?
        }
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
        addDiagram(conceptualSchema, context, context.getName(), new DefaultDimensionStrategy());
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
            this.lastCSCFile = openDialog.getSelectedFile();
        } catch (Exception e) {
            ErrorDialog.showError(this, e, "Import failed");
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
    }

    private void addDiagrams(ConceptualSchema schema, CernatoModel cernatoModel) {
        Vector views = cernatoModel.getViews();
        for (Iterator iterator = views.iterator(); iterator.hasNext();) {
            View view = (View) iterator.next();
            addDiagram(schema, new ViewContext(cernatoModel, view), view.getName(), new CernatoDimensionStrategy());
        }
    }

    private void addDiagram(ConceptualSchema schema, Context context, String name,
                            DimensionCreationStrategy dimensionStrategy) {
        LatticeGenerator lgen = new GantersAlgorithm();
        Lattice lattice = lgen.createLattice(context);
        Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, name, dimensionStrategy);
        schema.addDiagram(diagram);
    }

    public EventBroker getEventBroker() {
        return eventBroker;
    }

    public void closeMainPanel() {
        // store current position
        ConfigurationManager.storePlacement("SienaMainPanel", this);
		ConfigurationManager.storeStringList(CONFIGURATION_SECTION_NAME,"mruFiles",this.mruList);
        ConfigurationManager.storeInt("SienaMainPanel", "diagramViewDivider",
                diagramEditingView.getDividerLocation()
        );
        if(temporalControls != null) {
        	ConfigurationManager.storePlacement("SienaTemporalControls", temporalControls);
        }
        ConfigurationManager.saveConfiguration();
        System.exit(0);
    }

    public void processEvent(Event e) {
        if (e instanceof ConceptualSchemaChangeEvent) {
            ConceptualSchemaChangeEvent schemaEvent = (ConceptualSchemaChangeEvent) e;
            conceptualSchema = schemaEvent.getConceptualSchema();
        }
		if (e instanceof ConceptualSchemaLoadedEvent) {
			ConceptualSchemaLoadedEvent loadEvent =
				(ConceptualSchemaLoadedEvent) e;
			File schemaFile = loadEvent.getFile();
			addFileToMRUList(schemaFile);
		}
        this.exportDiagramAction.setEnabled(
                (this.diagramEditingView.getDiagramView().getDiagram() != null) &&
                (this.diagramExportSettings != null));
    }
    
	private void updateWindowTitle() {
		// get the current filename without the extension and full path
		// we have to use '\\' instead of '\' although we're checking for the occurrence of '\'.
		if(currentFile != null){
			System.out.println("currentfile NOT null. set title to "+currentFile+" Siena");
			String filename = currentFile.substring(currentFile.lastIndexOf("\\")+1,(currentFile.length()-4));
			setTitle(filename +" - "+WINDOW_TITLE);
		} else {
			System.out.println("currentfile is null. set title as 'Siena'");
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
			setTitle(schemaFile.getName().substring(0,((schemaFile.getName()).length()-4))+" - "+WINDOW_TITLE);		
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
				boolean result = this.saveFileAction.saveFile();
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
		if(this.currentFile == null) {
			this.saveFileAction.saveFile();	
		} else {
			try {
				saveActivity.processFile(new File(this.currentFile));
				this.conceptualSchema.dataSaved();
			} catch (Exception e) {
				ErrorDialog.showError(this,e,"Saving file failed");
			}			
		}
	}

}
