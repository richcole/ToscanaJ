package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.model.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.events.EventBroker;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.diagram.NodeMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.IdealMovementEventListener;
import net.sourceforge.toscanaj.controller.diagram.FilterMovementEventListener;
import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

public class DiagramEditingView extends JPanel implements BrokerEventListener {
    private ConceptualSchema conceptualSchema;
    private DefaultListModel diagramListModel;
    private JSplitPane splitPane;
    private DiagramView diagramView;
    private NodeMovementEventListener nodeMovementEventListener = new NodeMovementEventListener();
    private IdealMovementEventListener idealMovementEventListener = new IdealMovementEventListener();
    private FilterMovementEventListener filterMovementEventListener = new FilterMovementEventListener();

    /**
     * Construct an instance of this view
     */
    public DiagramEditingView(JFrame frame, ConceptualSchema conceptualSchema, EventBroker eventBroker) {
        super();
        setLayout(new BorderLayout());
        this.conceptualSchema = conceptualSchema;

        setName("DiagramEditingView");

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, makeDiagramListView(), makeDiagramViewPanel());
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);

        eventBroker.subscribe(this, ConceptualSchemaChangeEvent.class, Object.class );
    }

    private JPanel makeDiagramViewPanel() {
        JPanel diagramViewPanel = new JPanel(new BorderLayout());

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolPanel.add(new JLabel("Movement:"));
        String[] movementNames = { "Node", "Ideal", "Filter" };
        JComboBox movementChooser = new JComboBox(movementNames);
        toolPanel.add(movementChooser);

        diagramView = new DiagramView();
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
        fillDiagramListView();
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = listView.locationToIndex(e.getPoint());
                    showDiagram(index);
                }
            }
        };
        listView.addMouseListener(mouseListener);

        JPanel listViewPane = new JPanel();
        listViewPane.setLayout(new GridBagLayout());
        listViewPane.add(new JLabel("Diagrams:"),
                new GridBagConstraints(
                        0, 0, 1, 1, 1.0, 0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );
        listViewPane.add(new JScrollPane(listView),
                new GridBagConstraints(
                        0, 1, 1, 1, 1.0, 1.0,
                        GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH,
                        new Insets(5, 5, 5, 5),
                        5, 5)
        );

        return listViewPane;
    }

    private void showDiagram(int index) {
        diagramView.showDiagram(conceptualSchema.getDiagram(index));
    }

    protected void fillDiagramListView() {
        for (int i = 0; i < conceptualSchema.getNumberOfDiagrams(); i++) {
            SimpleLineDiagram diagram = (SimpleLineDiagram) conceptualSchema.getDiagram(i);
            diagramListModel.addElement(diagram.getTitle());
        }
    }

    public void processEvent(Event event) {
        ConceptualSchemaChangeEvent changeEvent = (ConceptualSchemaChangeEvent) event;
        conceptualSchema = changeEvent.getConceptualSchema();
        diagramListModel.clear();
        fillDiagramListView();
        diagramView.showDiagram(null);
    }

    public void setDividerLocation(int location) {
        splitPane.setDividerLocation(location);
    }

    public int getDividerLocation() {
        return splitPane.getDividerLocation();
    }
}