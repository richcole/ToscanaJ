/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net
    .sourceforge
    .toscanaj
    .controller
    .cernato
    .NDimNodeMovementEventListener;
import net.sourceforge.toscanaj.controller.db.DatabaseConnection;
import net.sourceforge.toscanaj.controller.diagram.*;
import net.sourceforge.toscanaj.controller.events.DatabaseConnectedEvent;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.DiagramToContextConverter;
import net.sourceforge.toscanaj.controller.fca.DirectConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.GantersAlgorithm;
import net.sourceforge.toscanaj.controller.fca.LatticeGenerator;
import net.sourceforge.toscanaj.controller.ndimlayout.DefaultDimensionStrategy;
import net.sourceforge.toscanaj.controller.ndimlayout.NDimLayoutOperations;
import net.sourceforge.toscanaj.gui.LabeledPanel;
import net.sourceforge.toscanaj.gui.dialog.XMLEditorDialog;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.WriteableDiagram2D;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.model.ndimdiagram.NDimDiagram;
import net.sourceforge.toscanaj.view.scales.ContextTableScaleEditorDialog;

import org.tockit.canvas.events.CanvasItemContextMenuRequestEvent;
import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;

public class DiagramEditingView extends JPanel implements EventBrokerListener {
    private static final int DEFAULT_GRID_SIZE = 15;
    private static final String[] FULL_MOVEMENT_OPTION_NAMES = {"NDim", "Node", "Ideal", "Filter"};
    private static final String[] SIMPLE_MOVEMENT_OPTION_NAMES = {"Node", "Ideal", "Filter"};
    private ConceptualSchema conceptualSchema;
    private DefaultListModel diagramListModel;
    private JSplitPane splitPane;
    protected DiagramView diagramView;
    protected NodeMovementEventListener nodeMovementEventListener = new NodeMovementEventListener();
    protected SetMovementEventListener idealMovementEventListener = new IdealMovementEventListener();
    protected FilterMovementEventListener filterMovementEventListener = new FilterMovementEventListener();
    protected NDimNodeMovementEventListener ndimMovementEventListener = new NDimNodeMovementEventListener();
    private static final double ZOOM_FACTOR = 1.1;
    private JButton editContextButton;
    private DatabaseConnection databaseConnection = null;
	private JButton zoomInButton;
	private JButton zoomOutButton;
    private JComboBox movementChooser;
    private JButton editDiagramDescButton;
    protected ContextTableScaleEditorDialog contextEditingDialog;
    private JButton gridIncreaseButton;
    private JButton gridDecreaseButton;
    private JCheckBox gridEnabledCheckBox;

    /**
     * Construct an instance of this view
     */
    public DiagramEditingView(ConceptualSchema conceptualSchema, EventBroker eventBroker) {
        super();
        setLayout(new BorderLayout());
        this.conceptualSchema = conceptualSchema;

        setName("DiagramEditingView");

        JComponent diagramListView = makeDiagramListView();
        JPanel mainDiagramView = makeDiagramViewPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, diagramListView, mainDiagramView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);
        
        Frame frame = JOptionPane.getFrameForComponent(this);
        this.contextEditingDialog = new ContextTableScaleEditorDialog(frame, this.conceptualSchema, this.databaseConnection, eventBroker);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class);
        eventBroker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
        this.diagramView.getController().getEventBroker().subscribe(this, DisplayedDiagramChangedEvent.class, Object.class);
		this.diagramView.getController().getEventBroker().subscribe( 
							new AttributeEditingLabelViewPopupMenuHandler(diagramView, eventBroker),
							CanvasItemContextMenuRequestEvent.class, AttributeLabelView.class);
    }

    protected JPanel makeDiagramViewPanel() {
        JPanel diagramViewPanel = new JPanel(new BorderLayout());

        diagramView = new DiagramView();
        diagramView.setQuery(ListQuery.KEY_LIST_QUERY);
        EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        DirectConceptInterpreter interpreter = new DirectConceptInterpreter();
        ConceptInterpretationContext interpretationContext =
                new ConceptInterpretationContext(new DiagramHistory(), canvasEventBroker);
        diagramView.setConceptInterpreter(interpreter);
        diagramView.setConceptInterpretationContext(interpretationContext);
        diagramView.setGrid(DEFAULT_GRID_SIZE,DEFAULT_GRID_SIZE);
        diagramView.setGridEnabled(false);
        new LabelDragEventHandler(canvasEventBroker);
        new LabelClickEventHandler(canvasEventBroker);
        diagramView.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createMovementManipulators(toolPanel);

        toolPanel.add(new JLabel("Zoom:"));
        zoomInButton = new JButton("+");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomIntoDiagram();
            }
        });
        toolPanel.add(zoomInButton);
        zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOutOfDiagram();
            }
        });
        toolPanel.add(zoomOutButton);

        toolPanel.add(new JLabel("Grid:"));
        gridIncreaseButton = new JButton("+");
        gridIncreaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagramView.increaseGridSize();
            }
        });
        gridIncreaseButton.setEnabled(false);
        toolPanel.add(gridIncreaseButton);
        gridDecreaseButton = new JButton("-");
        gridDecreaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                diagramView.decreaseGridSize();
            }
        });
        gridDecreaseButton.setEnabled(false);
        toolPanel.add(gridDecreaseButton);
        gridEnabledCheckBox = new JCheckBox("use");
        gridEnabledCheckBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		boolean enabled = gridEnabledCheckBox.isSelected();
        		gridIncreaseButton.setEnabled(enabled);
        		gridDecreaseButton.setEnabled(enabled);
        		diagramView.setGridEnabled(enabled);	
            }
        });
        gridEnabledCheckBox.setSelected(false);
        gridEnabledCheckBox.setEnabled(false);
        toolPanel.add(gridEnabledCheckBox);

        editContextButton = new JButton("Edit Context...");
        editContextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editContext();
            }
        });
        editContextButton.setEnabled(false);
        toolPanel.add(editContextButton);
		
		editDiagramDescButton = new JButton("Edit Description...");
		editDiagramDescButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				editDiagramDescription();
			}
		});
		editDiagramDescButton.setEnabled(false);
		toolPanel.add(editDiagramDescButton);
		
        diagramViewPanel.add(toolPanel, BorderLayout.NORTH);
        diagramViewPanel.add(diagramView, BorderLayout.CENTER);
        return diagramViewPanel;
    }

    protected void createMovementManipulators(JPanel toolPanel) {
        toolPanel.add(new JLabel("Movement:"));
        movementChooser = new JComboBox();
        setMovementManipulators();
        toolPanel.add(movementChooser);
        movementChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox combobox = (JComboBox) e.getSource();
                String selection = combobox.getSelectedItem().toString();
                if (selection.equals("NDim")) {
                    setNDimManipulator();
                } else if (selection.equals("Node")) {
                	setNodeManipulator();
                } else if (selection.equals("Ideal")) {
                	setIdealManipulator();
                } else if (selection.equals("Filter")) {
                	setFilterManipulator();
                }
            }
        });
    }

    protected void setMovementManipulators() {
        Diagram2D diagram = this.diagramView.getDiagram();
        if(diagram == null) {
        	movementChooser.setEnabled(false);
        } else {
        	movementChooser.setEnabled(true);
        	if (diagram instanceof NDimDiagram) {
        		if(movementChooser.getModel().getSize() != FULL_MOVEMENT_OPTION_NAMES.length) {
		        	movementChooser.setModel(new DefaultComboBoxModel(FULL_MOVEMENT_OPTION_NAMES));
		        	setNDimManipulator();
        		}
	        } else {
        	    if(movementChooser.getModel().getSize() != SIMPLE_MOVEMENT_OPTION_NAMES.length) {
		            movementChooser.setModel(new DefaultComboBoxModel(SIMPLE_MOVEMENT_OPTION_NAMES));
		            setNodeManipulator();
        	    }
	        }
        }
    }

    protected void setNDimManipulator() {
        EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
        canvasEventBroker.removeSubscriptions(idealMovementEventListener);
        canvasEventBroker.removeSubscriptions(filterMovementEventListener);
        canvasEventBroker.subscribe(
                ndimMovementEventListener,
                CanvasItemDraggedEvent.class,
                NodeView.class
        );
    }

    protected void setNodeManipulator() {
        EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        canvasEventBroker.removeSubscriptions(ndimMovementEventListener);
        canvasEventBroker.removeSubscriptions(idealMovementEventListener);
        canvasEventBroker.removeSubscriptions(filterMovementEventListener);
        canvasEventBroker.subscribe(
                nodeMovementEventListener,
                CanvasItemDraggedEvent.class,
                NodeView.class
        );
    }

    protected void setFilterManipulator() {
        EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        canvasEventBroker.removeSubscriptions(ndimMovementEventListener);
        canvasEventBroker.removeSubscriptions(idealMovementEventListener);
        canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
        canvasEventBroker.subscribe(
                filterMovementEventListener,
                CanvasItemDraggedEvent.class,
                NodeView.class
        );
    }

    protected void setIdealManipulator() {
        EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        canvasEventBroker.removeSubscriptions(ndimMovementEventListener);
        canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
        canvasEventBroker.removeSubscriptions(filterMovementEventListener);
        canvasEventBroker.subscribe(
                idealMovementEventListener,
                CanvasItemDraggedEvent.class,
                NodeView.class
        );
    }

    private void zoomIntoDiagram() {
        changeZoom(1 / ZOOM_FACTOR);
    }

    private void zoomOutOfDiagram() {
        changeZoom(ZOOM_FACTOR);
    }

    private void changeZoom(double zoomFactor) {
        Diagram2D diagram = this.diagramView.getDiagram();
        Iterator nodesIt = diagram.getNodes();
        while (nodesIt.hasNext()) {
            DiagramNode diagramNode = (DiagramNode) nodesIt.next();
            Point2D pos = diagramNode.getPosition();
            diagramNode.setPosition(new Point2D.Double(pos.getX() * zoomFactor, pos.getY() * zoomFactor));
        }
        this.diagramView.requestScreenTransformUpdate();
        this.diagramView.repaint();
    }
    
    protected void editContext() {
        ContextImplementation context = (ContextImplementation) DiagramToContextConverter.getContext(this.diagramView.getDiagram());
        contextEditingDialog.setContext(context);
    	if(contextEditingDialog.execute()) {
    		/// @todo check for duplicate names
    		LatticeGenerator lgen = new GantersAlgorithm();
    		context = contextEditingDialog.getContext();
    		Lattice lattice = lgen.createLattice(context);
    		Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, context.getName(), new DefaultDimensionStrategy());
    		this.conceptualSchema.replaceDiagram(this.diagramView.getDiagram(), diagram);
    		this.diagramView.showDiagram(diagram);
    	}
    }
	
	protected void editDiagramDescription(){
		WriteableDiagram2D currentDiagram = (WriteableDiagram2D) this.diagramView.getDiagram();
		if(currentDiagram!=null){
			XMLEditorDialog diagramDescriptionEditor = new XMLEditorDialog(null,"Diagram description editor");
			diagramDescriptionEditor.setContent(currentDiagram.getDescription());
			diagramDescriptionEditor.show();
			currentDiagram.setDescription(diagramDescriptionEditor.getContent());
		}
	}
	
    protected JComponent makeDiagramListView() {
        diagramListModel = new DefaultListModel();
        final JList listView = new JList(diagramListModel);
		final JButton upButton=new JButton("Up");
		final JButton downButton=new JButton("Down");
        final JButton removeButton = new JButton("Remove");
        JPanel buttonsPane = new JPanel(new GridBagLayout());
        JPanel upDownButtonPane = new JPanel(new GridBagLayout());
        upDownButtonPane.add(upButton, new GridBagConstraints(
					        0, 0, 1, 1, 1.0, 0,
							GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 1, 1),
							2, 2)
		);
		upDownButtonPane.add(downButton, new GridBagConstraints(
							1, 0, 1, 1, 1.0, 0,
							GridBagConstraints.WEST,
							GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 1, 1),
							2, 2)
		);
		
		
		buttonsPane.add(upDownButtonPane,new GridBagConstraints(
							0, 0, 1, 1, 1.0, 0,
							GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 1, 1),
							2, 2)
		);
		buttonsPane.add(removeButton,new GridBagConstraints(
							0, 1, 1, 1, 1.0, 0,
							GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 1, 1),
							2, 2)
		);
		
		
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        removeButton.setEnabled(false);
        listView.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fillDiagramListView();
        listView.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                	if(listView.getSelectedIndex()==0 ){
                		upButton.setEnabled(false);
						downButton.setEnabled(listView.getSelectedIndex() != -1);						
                	}
                	else if(listView.getSelectedIndex()+1 == listView.getModel().getSize()){
						downButton.setEnabled(false);
						upButton.setEnabled(listView.getSelectedIndex() != -1);
                	}
					else{
						upButton.setEnabled(listView.getSelectedIndex() != -1);
						downButton.setEnabled(listView.getSelectedIndex() != -1);
					}
					removeButton.setEnabled(listView.getSelectedIndex() != -1);
                    int[] selections = listView.getSelectedIndices();
                    if (selections.length == 1) {
                        diagramView.showDiagram(conceptualSchema.getDiagram(selections[0]));
                    } else {
                        diagramView.showDiagram(null);
                    }
                }
            }
        });
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int[] selections = listView.getSelectedIndices();
                for (int i = selections.length - 1; i >= 0; i--) {
                    int selection = selections[i];
                    conceptualSchema.removeDiagram(selection);
                }
            }
        });
        
		upButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
			 	int index = listView.getSelectedIndex();
				 if(index!=-1){
				 	conceptualSchema.exchangeDiagrams(index , index - 1);
				 	listView.setSelectedIndex(index-1);
				 }
			 }
		 });
			 
		downButton.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				int index = listView.getSelectedIndex();
				if(index!=-1){
				   conceptualSchema.exchangeDiagrams(index , index + 1);
				   listView.setSelectedIndex(index+1);
				}
			 }
		 });
        return new LabeledPanel("Diagrams:", listView, buttonsPane);
    }

    protected void fillDiagramListView() {
        diagramListModel.clear();
        for (int i = 0; i < conceptualSchema.getNumberOfDiagrams(); i++) {
            SimpleLineDiagram diagram = (SimpleLineDiagram) conceptualSchema.getDiagram(i);
            diagramListModel.addElement(diagram.getTitle());
        }
    }

    public void processEvent(Event event) {
    	if(event instanceof DisplayedDiagramChangedEvent) {
    	    updateToolbar();
    	    return;
    	}
    	if(event instanceof DatabaseConnectedEvent) {
    		DatabaseConnectedEvent dbConEv = (DatabaseConnectedEvent) event;
    		this.databaseConnection = dbConEv.getConnection();
    	}
    	if(event instanceof ConceptualSchemaChangeEvent) {
	        ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
	        if (event instanceof NewConceptualSchemaEvent) {
	            conceptualSchema = changeEvent.getConceptualSchema();
	        }
	        fillDiagramListView();
	        updateToolbar();
    	}
    }
    
    private void updateToolbar() {
    	boolean diagramAvailable = this.diagramView.getDiagram() != null;
		this.zoomInButton.setEnabled(diagramAvailable);
		this.zoomOutButton.setEnabled(diagramAvailable);
        this.gridIncreaseButton.setEnabled(diagramAvailable && this.gridEnabledCheckBox.isSelected());
        this.gridDecreaseButton.setEnabled(diagramAvailable && this.gridEnabledCheckBox.isSelected());
        this.gridEnabledCheckBox.setEnabled(diagramAvailable);
		this.editContextButton.setEnabled(diagramAvailable);
		this.editDiagramDescButton.setEnabled(diagramAvailable);
		if(movementChooser != null) {
			setMovementManipulators();
		}
    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }

    public DiagramView getDiagramView() {
        return this.diagramView;
    }
}
