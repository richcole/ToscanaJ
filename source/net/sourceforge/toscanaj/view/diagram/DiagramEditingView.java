/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

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
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.ContextImplementation;
import net.sourceforge.toscanaj.model.database.ListQuery;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.lattice.Lattice;
import net.sourceforge.toscanaj.view.scales.ContextTableScaleEditorDialog;

import org.tockit.canvas.events.CanvasItemDraggedEvent;
import org.tockit.events.Event;
import org.tockit.events.EventBroker;
import org.tockit.events.EventBrokerListener;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.Iterator;

public class DiagramEditingView extends JPanel implements EventBrokerListener {
    private ConceptualSchema conceptualSchema;
    private DefaultListModel diagramListModel;
    private JSplitPane splitPane;
    protected DiagramView diagramView;
    protected NodeMovementEventListener nodeMovementEventListener = new NodeMovementEventListener();
    protected SetMovementEventListener idealMovementEventListener = new IdealMovementEventListener();
    protected FilterMovementEventListener filterMovementEventListener = new FilterMovementEventListener();
    private static final double ZOOM_FACTOR = 1.1;
    private JButton editContextButton;
    private DatabaseConnection databaseConnection = null;

    /**
     * Construct an instance of this view
     */
    public DiagramEditingView(ConceptualSchema conceptualSchema, EventBroker eventBroker) {
        super();
        setLayout(new BorderLayout());
        this.conceptualSchema = conceptualSchema;

        setName("DiagramEditingView");

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, makeDiagramListView(), makeDiagramViewPanel());
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class);
        eventBroker.subscribe(this, DatabaseConnectedEvent.class, Object.class);
        this.diagramView.getController().getEventBroker().subscribe(this, DisplayedDiagramChangedEvent.class, Object.class);
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
        new LabelDragEventHandler(canvasEventBroker);
        new LabelClickEventHandler(canvasEventBroker);

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        createMovementManipulators(toolPanel);

        toolPanel.add(new JLabel("Zoom:"));
        JButton zoomInButton = new JButton("+");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomIntoDiagram();
            }
        });
        toolPanel.add(zoomInButton);
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOutOfDiagram();
            }
        });
        toolPanel.add(zoomOutButton);

        editContextButton = new JButton("Edit Context...");
        editContextButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editContext();
            }
        });
        editContextButton.setEnabled(false);
        toolPanel.add(editContextButton);

        diagramViewPanel.add(toolPanel, BorderLayout.NORTH);
        diagramViewPanel.add(diagramView, BorderLayout.CENTER);
        return diagramViewPanel;
    }

    protected void createMovementManipulators(JPanel toolPanel) {
        toolPanel.add(new JLabel("Movement:"));
        final EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        canvasEventBroker.subscribe(
                nodeMovementEventListener,
                CanvasItemDraggedEvent.class,
                NodeView.class
        );
        String[] movementNames = {"Node", "Ideal", "Filter"};
        JComboBox movementChooser = new JComboBox(movementNames);
        toolPanel.add(movementChooser);
        movementChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox combobox = (JComboBox) e.getSource();
                String selection = combobox.getSelectedItem().toString();
                if (selection.equals("Node")) {
                    canvasEventBroker.removeSubscriptions(idealMovementEventListener);
                    canvasEventBroker.removeSubscriptions(filterMovementEventListener);
                    canvasEventBroker.subscribe(
                            nodeMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                } else if (selection.equals("Ideal")) {
                    canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
                    canvasEventBroker.removeSubscriptions(filterMovementEventListener);
                    canvasEventBroker.subscribe(
                            idealMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                } else if (selection.equals("Filter")) {
                    canvasEventBroker.removeSubscriptions(idealMovementEventListener);
                    canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
                    canvasEventBroker.subscribe(
                            filterMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                }
            }
        });
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
    	Frame frame = JOptionPane.getFrameForComponent(this);
    	ContextTableScaleEditorDialog dialog = 
    					new ContextTableScaleEditorDialog(frame, this.conceptualSchema, this.databaseConnection, context);
    	if(dialog.execute()) {
    		/// @todo check for duplicate names
    		LatticeGenerator lgen = new GantersAlgorithm();
    		context = dialog.getContext();
    		Lattice lattice = lgen.createLattice(context);
    		Diagram2D diagram = NDimLayoutOperations.createDiagram(lattice, context.getName(), new DefaultDimensionStrategy());
    		this.conceptualSchema.replaceDiagram(this.diagramView.getDiagram(), diagram);
    		this.diagramView.showDiagram(diagram);
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
							GridBagConstraints.CENTER,
							GridBagConstraints.HORIZONTAL,
							new Insets(1, 1, 1, 1),
							2, 2)
		);
		upDownButtonPane.add(downButton, new GridBagConstraints(
							1, 0, 1, 1, 1.0, 0,
							GridBagConstraints.CENTER,
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
        return new LabeledScrollPaneView("Diagrams:", listView, buttonsPane);
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
    	    updateButtons();
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
	        updateButtons();
    	}
    }
    
    private void updateButtons() {
    	this.editContextButton.setEnabled(this.diagramView.getDiagram() != null);
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
