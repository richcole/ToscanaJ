/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;
import net.sourceforge.toscanaj.controller.diagram.*;
import net.sourceforge.toscanaj.controller.fca.*;
import net.sourceforge.toscanaj.events.*;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.gui.LabeledScrollPaneView;
import net.sourceforge.toscanaj.model.DiagramCollection;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.events.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

public class DiagramEditingView extends JPanel implements EventListener {
    private DiagramCollection conceptualSchema;
    private DefaultListModel diagramListModel;
    private JSplitPane splitPane;
    protected DiagramView diagramView;
    protected NodeMovementEventListener nodeMovementEventListener = new NodeMovementEventListener();
    protected SetMovementEventListener idealMovementEventListener = new IdealMovementEventListener();
    protected FilterMovementEventListener filterMovementEventListener = new FilterMovementEventListener();
    private static final double ZOOM_FACTOR = 1.1;

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

        eventBroker.subscribe(this, NewConceptualSchemaEvent.class, Object.class);
        eventBroker.subscribe(this, DiagramListChangeEvent.class, Object.class);
    }

    private JPanel makeDiagramViewPanel() {
        JPanel diagramViewPanel = new JPanel(new BorderLayout());

        diagramView = new DiagramView();
        DirectConceptInterpreter interpreter = new DirectConceptInterpreter();
        ConceptInterpretationContext interpretationContext =
                    new ConceptInterpretationContext(new DiagramHistory(),diagramView.getController().getEventBroker());
        diagramView.setConceptInterpreter(interpreter);
        diagramView.setConceptInterpretationContext(interpretationContext);

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
        changeZoom(ZOOM_FACTOR);
    }

    private void zoomOutOfDiagram() {
        changeZoom(1/ZOOM_FACTOR);
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

    private JComponent makeDiagramListView() {
        diagramListModel = new DefaultListModel();
        final JList listView = new JList(diagramListModel);
        final JButton removeButton = new JButton("Remove");
        removeButton.setEnabled(false);
        listView.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fillDiagramListView();
        listView.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
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
        if (event instanceof NewConceptualSchemaEvent) {
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
