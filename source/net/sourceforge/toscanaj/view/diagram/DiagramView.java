/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.controller.diagram.SelectionChangedEvent;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpretationContext;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import org.tockit.canvas.Canvas;
import org.tockit.events.EventBroker;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * This class paints a diagram defined by the SimpleLineDiagram class.
 *
 * @todo get rid of ChangeObserver, use EventBroker.
 */
public class DiagramView extends Canvas implements ChangeObserver {
    /**
     * The diagram to display.
     */
    private Diagram2D diagram = null;

    private ConceptInterpreter conceptInterpreter;

    private ConceptInterpretationContext conceptInterpretationContext;
    /**
     * Currently we don't use selection.
     */
    static final public int NO_SELECTION = -1;
    /**
     * Node displays nothing selected
     */
    static final public int NOT_SELECTED = 0;
    /**
     * Node displays the currently selected concept.
     */
    static final public int SELECTED_DIRECTLY = 1;
    /**
     * Node displays a concept in the filter of the currently selected concept.
     */
    static final public int SELECTED_FILTER = 2;
    /**
     * Node displays a concept in the ideal of the currently selected concept.
     */
    static final public int SELECTED_IDEAL = 4;

    private DiagramSchema diagramSchema;

    private class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            requestScreenTransformUpdate();
            repaint();
        }
    }

    /**
     * Creates a new view displaying an empty digram (i.e. nothing at all).
     */
    public DiagramView() {
        super(new EventBroker());
        this.conceptInterpreter = null;
        this.conceptInterpretationContext = null;
        this.diagramSchema = new DiagramSchema();
        addComponentListener(new ResizeListener());
        getBackgroundItem().setPaint(diagramSchema.getBackgroundColor());
    }

    public ConceptInterpreter getConceptInterpreter() {
        return conceptInterpreter;
    }

    public void setConceptInterpreter(ConceptInterpreter conceptInterpreter) {
        this.conceptInterpreter = conceptInterpreter;
        /// @todo update parts, redraw
    }

    public void setConceptInterpretationContext(ConceptInterpretationContext conceptInterpretationContext) {
        this.conceptInterpretationContext = conceptInterpretationContext;
        /// @todo update parts, redraw
    }

    public void setFilterMode(boolean filterMode) {
        Iterator it = this.getCanvasItemsByType(NodeView.class).iterator();
        while (it.hasNext()) {
            NodeView cur = (NodeView) it.next();
            cur.getConceptInterpretationContext().setFilterMode(filterMode);
        }
    }

    protected void dragFinished(MouseEvent e) {
        requestScreenTransformUpdate();
    }

    public void requestScreenTransformUpdate() {
        screenTransformDirty = true;
    }

    /**
     * Implements ChangeObserver.update(Object) by repainting the diagram.
     */
    public void update(Object source) {
        if (source instanceof DiagramController ||
                source instanceof DiagramHistory) {
            showDiagram(DiagramController.getController().getCurrentDiagram());
        } else {
            requestScreenTransformUpdate();
            repaint();
        }
    }

    private boolean screenTransformDirty = false;

    protected boolean isScreenTransformDirty() {
        return screenTransformDirty;
    }

    /**
     * Paints the diagram on the screen.
     */
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        AffineTransform oldTransform = g2d.getTransform();

        // fill the background (without transform)
        g2d.setPaint(diagramSchema.getBackgroundColor());
        g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));

        if (diagram == null) {
            return;
        }

        int margin = diagramSchema.getMargin();
        if (screenTransformDirty) {
            // find current bounds
            Rectangle2D bounds = new Rectangle2D.Double(margin, margin,
                    getWidth() - 2 * margin, getHeight() - 2 * margin);
            this.setScreenTransform(this.scaleToFit(g2d, bounds));
            makeScreenTransformClear();
        }
        g2d.transform(getScreenTransform());

        // paint all items on canvas
        paintCanvas(g2d);
        // draw diagram title in the top left corner
        g2d.setPaint(diagramSchema.getForegroundColor());
        g2d.setTransform(oldTransform);
        g2d.drawString(diagram.getTitle(), margin, margin);
    }

    protected void makeScreenTransformClear() {
        screenTransformDirty = false;
    }

    /**
     * Sets the given diagram as new diagram to display.
     *
     * This will automatically cause a repaint of the view.
     */
    public void showDiagram(Diagram2D diagram) {
        this.diagram = diagram;
        removeSubscriptions();
        clearCanvas();
        if (diagram == null) {
            repaint();
			this.getController().getEventBroker().processEvent(new DisplayedDiagramChangedEvent(this));
            return;
        }
        addLayer("lines-0");
        addLayer("nodes-0");
        addLayer("lines-1");
        addLayer("nodes-1");
        addLayer("labels");
        addDiagram(diagram, conceptInterpretationContext, 0);
        requestScreenTransformUpdate();
        repaint();
        this.getController().getEventBroker().processEvent(new DisplayedDiagramChangedEvent(this));
    }

    private void removeSubscriptions() {
        for (Iterator iterator = this.getCanvasItemsByType(LabelView.class).iterator(); iterator.hasNext();) {
            LabelView lv = (LabelView) iterator.next();
            this.getController().getEventBroker().removeSubscriptions(lv);
        }
    }

    public Diagram2D getDiagram() {
        return diagram;
    }

    /**
     * Adds a line diagram to the canvas.
     *
     * If the filter concept is non-null all nodes created will use this for
     * filter operations.
     */
    private void addDiagram(Diagram2D diagram, ConceptInterpretationContext context, int layer) {
        String lineLayerName = "lines-" + layer;
        String nodeLayerName = "nodes-" + layer;
        String labelLayerName = "labels";
        Hashtable nodeMap = new Hashtable();
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this, context);
            nodeMap.put(node, nodeView);
            addCanvasItem(nodeView, nodeLayerName);
            if (node instanceof NestedDiagramNode) {
                Concept concept = node.getConcept();
                if (conceptInterpreter.isRealized(concept, context)) {
                    NestedDiagramNode ndNode = (NestedDiagramNode) node;
                    addDiagram(ndNode.getInnerDiagram(), context.createNestedContext(concept), layer + 1);
                }
            } else {
                /**
                 * Inititalize the contingent sizes to allow contingent gradients to work properly on first draw.
                 *
                 * @todo this is not a nice place to do this, find something better
                 */
                conceptInterpreter.getObjectCount(node.getConcept(), context);
            }
            LabelInfo attrLabelInfo = diagram.getAttributeLabel(i);
            if (attrLabelInfo != null) {
                LabelView labelView = new AttributeLabelView(this, nodeView, attrLabelInfo);
                addCanvasItem(labelView, labelLayerName);
                labelView.addObserver(this);
            }
            LabelInfo objLabelInfo = diagram.getObjectLabel(i);
            if (objLabelInfo != null) {
                LabelView labelView = new ObjectLabelView(this, nodeView, objLabelInfo);
                addCanvasItem(labelView, labelLayerName);
                labelView.addObserver(this);
            }
        }
        for (int i = 0; i < diagram.getNumberOfLines(); i++) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem(new LineView(dl, (NodeView) nodeMap.get(dl.getFromNode()), (NodeView) nodeMap.get(dl.getToNode())), lineLayerName);
        }
    }

    public void setDisplayType(boolean contingentOnly) {
        Iterator it = this.getCanvasItemsByType(NodeView.class).iterator();
        while (it.hasNext()) {
            NodeView nv = (NodeView) it.next();
            nv.getConceptInterpretationContext().setObjectDisplayMode(contingentOnly);
        }
        updateLabelEntries();
        requestScreenTransformUpdate();
        repaint();
    }

    private void updateLabelEntries() {
        Iterator it = this.getCanvasItemsByType(LabelView.class).iterator();
        while (it.hasNext()) {
            LabelView lv = (LabelView) it.next();
            lv.updateEntries();
        }
    }

    public void setSelectedConcepts(List concepts) {
        // notify all nodes and lines
        Iterator it = this.getCanvasItemsByType(NodeView.class).iterator();
        while (it.hasNext()) {
            NodeView nv = (NodeView) it.next();
            nv.setSelectedConcepts(concepts);
        }
        getController().getEventBroker().processEvent(new SelectionChangedEvent(this));
        repaint();
    }

    /**
     * Sets all object label views to use this query when asking their concepts.
     */
    public void setQuery(Query query) {
        // update the current labels
        Iterator it = this.getCanvasItemsByType(ObjectLabelView.class).iterator();
        while (it.hasNext()) {
            ObjectLabelView lv = (ObjectLabelView) it.next();
            lv.setQuery(query);
        }
        requestScreenTransformUpdate();
        repaint();
        // set the value for new ones
        ObjectLabelView.setDefaultQuery(query);
    }

    public DiagramSchema getDiagramSchema() {
        return diagramSchema;
    }
}

