/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.events.NewConceptualSchemaEvent;
import net.sourceforge.toscanaj.model.events.DiagramListChangeEvent;
import net.sourceforge.toscanaj.model.database.DatabaseInfo;
import net.sourceforge.toscanaj.model.DiagramCollection;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.diagram.NodeMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.IdealMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.FilterMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.SetMovementEventListener;
import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class DiagramEditingView extends JPanel implements BrokerEventListener {
    private DiagramCollection conceptualSchema;
    private DefaultListModel diagramListModel;
    private JSplitPane splitPane;
    private DiagramView diagramView;
    private NodeMovementEventListener nodeMovementEventListener = new NodeMovementEventListener();
    private SetMovementEventListener idealMovementEventListener = new IdealMovementEventListener();
    private FilterMovementEventListener filterMovementEventListener = new FilterMovementEventListener();

    /**
     * Construct an instance of this view
     */
    public DiagramEditingView(JFrame frame, DiagramCollection conceptualSchema, EventBroker eventBroker) {
        super();
        setLayout(new BorderLayout());
        this.conceptualSchema = conceptualSchema;

        setName("DiagramEditingView");

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, makeDiagramListView(), makeDiagramViewPanel());
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class );
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class );
    }

    private JPanel makeDiagramViewPanel() {
        JPanel diagramViewPanel = new JPanel(new BorderLayout());

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPanel.add(new JLabel("Movement:"));
        String[] movementNames = { "Node", "Ideal", "Filter" };
        JComboBox movementChooser = new JComboBox(movementNames);
        toolPanel.add(movementChooser);

        DiagramController controller = DiagramController.getController();
        diagramView = new DiagramView(controller.getDefaultInterpreter(null),
                                      new ConceptInterpretationContext(controller.getDiagramHistory(),
                                                                       ConceptInterpretationContext.CONTINGENT,
                                                                       ConceptInterpretationContext.EXTENT));
        final EventBroker canvasEventBroker = diagramView.getController().getEventBroker();
        canvasEventBroker.subscribe(
                nodeMovementEventListener,
                CanvasItemDraggedEvent.class,
                NodeView.class
        );
        movementChooser.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JComboBox combobox = (JComboBox) e.getSource();
                String selection = combobox.getSelectedItem().toString();
                if(selection.equals("Node")) {
                    canvasEventBroker.removeSubscriptions(idealMovementEventListener);
                    canvasEventBroker.removeSubscriptions(filterMovementEventListener);
                    canvasEventBroker.subscribe(
                            nodeMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                } else if(selection.equals("Ideal")) {
                    canvasEventBroker.removeSubscriptions(nodeMovementEventListener);
                    canvasEventBroker.removeSubscriptions(filterMovementEventListener);
                    canvasEventBroker.subscribe(
                            idealMovementEventListener,
                            CanvasItemDraggedEvent.class,
                            NodeView.class
                    );
                } else if(selection.equals("Filter")) {
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

        diagramViewPanel.add(toolPanel, BorderLayout.NORTH);
        diagramViewPanel.add(diagramView, BorderLayout.CENTER);
        return diagramViewPanel;
    }

    private JComponent makeDiagramListView() {
        diagramListModel = new DefaultListModel();
        final JList listView = new JList(diagramListModel);
        final JButton removeButton = new JButton("Remove");
        removeButton.setEnabled(false);
        listView.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fillDiagramListView();
        listView.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if(!e.getValueIsAdjusting()) {
                    removeButton.setEnabled(listView.getSelectedIndex() != -1);
                    int[] selections = listView.getSelectedIndices();
                    if(selections.length == 1) {
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

        return new LabeledScrollPaneView("Diagrams:", listView, removeButton);
    }

    protected void fillDiagramListView() {
        diagramListModel.clear();
        for (int i = 0; i < conceptualSchema.getNumberOfDiagrams(); i++) {
            SimpleLineDiagram diagram = (SimpleLineDiagram) conceptualSchema.getDiagram(i);
            diagramListModel.addElement(diagram.getTitle());
        }
    }

    public void processEvent(Event event) {
        ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        if ( event instanceof NewConceptualSchemaEvent ) {
            conceptualSchema = changeEvent.getConceptualSchema();
        }
        fillDiagramListView();
    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }
}
