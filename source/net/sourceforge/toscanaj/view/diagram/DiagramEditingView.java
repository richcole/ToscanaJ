package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.gui.action.SimpleAction;
import net.sourceforge.toscanaj.gui.activity.SimpleActivity;
import net.sourceforge.toscanaj.gui.events.ConceptualSchemaChangeEvent;
import net.sourceforge.toscanaj.model.DatabaseInfo;
import net.sourceforge.toscanaj.model.ConceptualSchema;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.events.BrokerEventListener;
import net.sourceforge.toscanaj.events.Event;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.diagram.NodeMovementEventListener;
import net.sourceforge.toscanaj.canvas.events.CanvasItemDraggedEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;
import java.util.Observer;

public class DiagramEditingView extends JPanel implements BrokerEventListener {
    private ConceptualSchema conceptualSchema;
    private DefaultListModel diagramListModel;
    private JSplitPane splitPane;
    private DiagramView diagramView;

    /**
     * Construct an instance of this view
     */
    public DiagramEditingView(JFrame frame, ConceptualSchema conceptualSchema) {
        super();
        setLayout(new BorderLayout());
        this.conceptualSchema = conceptualSchema;

        setName("DiagramEditingView");

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

        diagramView = new DiagramView();
        diagramView.getController().getEventBroker().subscribe(
                new NodeMovementEventListener(),
                CanvasItemDraggedEvent.class,
                NodeView.class
        );

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listView, diagramView);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0);
        add(splitPane);
    }

    private void showDiagram(int index) {
        diagramView.showDiagram(conceptualSchema.getDiagram(index));
    }
    ;

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
