/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.Canvas;
import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.controller.diagram.SelectionChangedEvent;
import net.sourceforge.toscanaj.controller.fca.*;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

/**
 * This class paints a diagram defined by the SimpleLineDiagram class.
 */
public class DiagramView extends Canvas implements ChangeObserver {
    /**
     * This is a generic margin used for all four edges.
     */
    private final int MARGIN = 20;

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

    class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            requestScreenTransformUpdate();
            repaint();
        }
    }

    /**
     * Creates a new view displaying an empty digram (i.e. nothing at all).
     */
    public DiagramView() {
        super();
        this.conceptInterpreter = null;
        this.conceptInterpretationContext = null;
        addComponentListener(new ResizeListener());
        getBackgroundItem().setPaint(DiagramSchema.getDiagramSchema().getBackgroundColor());
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
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof NodeView) {
                NodeView nv = (NodeView) cur;
                nv.getConceptInterpretationContext().setFilterMode(filterMode);
            }
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
            showDiagram((SimpleLineDiagram) DiagramController.getController().getCurrentDiagram());
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
        AffineTransform oldTransform = g2d.getTransform();

        // fill the background (without transform)
        g2d.setPaint(DiagramSchema.getDiagramSchema().getBackgroundColor());
        g2d.fill(new Rectangle2D.Double(0, 0, getWidth(), getHeight()));

        if (diagram == null) {
            return;
        }

        if (screenTransformDirty) {
            // find current bounds
            Rectangle2D bounds = new Rectangle2D.Double(MARGIN, MARGIN,
                    getWidth() - 2 * MARGIN, getHeight() - 2 * MARGIN);
            this.setScreenTransform(this.scaleToFit(g2d, bounds));
            makeScreenTransformClear();
        }
        g2d.transform(getScreenTransform());

        // paint all items on canvas
        paintCanvas(g2d);
        // draw diagram title in the top left corner
        g2d.setPaint(DiagramSchema.getDiagramSchema().getForegroundColor());
        g2d.setTransform(oldTransform);
        g2d.drawString(diagram.getTitle(), MARGIN, MARGIN);
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
            return;
        }
        addDiagram(diagram, conceptInterpretationContext);
        requestScreenTransformUpdate();
        repaint();
    }

    private void removeSubscriptions() {
        for (Iterator iterator = canvasItems.iterator(); iterator.hasNext();) {
            CanvasItem canvasItem = (CanvasItem) iterator.next();
            if (canvasItem instanceof LabelView) {
                LabelView lv = (LabelView) canvasItem;
                this.getController().getEventBroker().removeSubscriptions(lv);
            }
        }
    }

    public Diagram2D getDiagram() {
        return diagram;
    }

    /**
     * Adds a simple non-nested line diagram to the canvas.
     *
     * If the filter concept is non-null all nodes created will use this for
     * filter operations.
     */
    private void addDiagram(Diagram2D diagram, ConceptInterpretationContext context) {
        Hashtable nodeMap = createNodeMap(diagram, context);
        addLinesToDiagram(diagram, nodeMap);
        addNodesToDiagram(diagram, nodeMap, context);
        addLabelsToDiagram(diagram, nodeMap);
    }

    private void addLabelsToDiagram(Diagram2D diagram, Hashtable nodeMap) {
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = (NodeView) nodeMap.get(node);
            LabelInfo attrLabelInfo = diagram.getAttributeLabel(i);
            if (attrLabelInfo != null) {
                LabelView labelView = new AttributeLabelView(this, nodeView, attrLabelInfo);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
            LabelInfo objLabelInfo = diagram.getObjectLabel(i);
            if (objLabelInfo != null) {
                LabelView labelView = new ObjectLabelView(this, nodeView, objLabelInfo);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
        }
    }

    private void addNodesToDiagram(Diagram2D diagram, Hashtable nodeMap, ConceptInterpretationContext context) {
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = (NodeView) nodeMap.get(node);
            addCanvasItem(nodeView);
            if (node instanceof NestedDiagramNode) {
                Concept concept = node.getConcept();
                if (conceptInterpreter.isRealized(concept, context)) {
                    NestedDiagramNode ndNode = (NestedDiagramNode) node;
                    addDiagram(ndNode.getInnerDiagram(), context.getNestedContext(concept));
                }
            }
        }
    }

    private void addLinesToDiagram(Diagram2D diagram, Hashtable nodeMap) {
        for (int i = 0; i < diagram.getNumberOfLines(); i++) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem(new LineView(dl, (NodeView) nodeMap.get(dl.getFromNode()), (NodeView) nodeMap.get(dl.getToNode())));
        }
    }

    private Hashtable createNodeMap(Diagram2D diagram, ConceptInterpretationContext context) {
        Hashtable nodeMap = new Hashtable();
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this, context);
            nodeMap.put(node, nodeView);
        }
        return nodeMap;
    }

    public void setDisplayType(boolean contingentOnly) {
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof NodeView) {
                NodeView nv = (NodeView) cur;
                nv.getConceptInterpretationContext().setObjectDisplayMode(contingentOnly);
            }
        }
        updateLabelEntries();
        requestScreenTransformUpdate();
        repaint();
    }

    private void updateLabelEntries() {
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof LabelView) {
                LabelView lv = (LabelView) cur;
                lv.updateEntries();
            }
        }
    }

    public void setSelectedConcepts(List concepts) {
        // notify all nodes and lines
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof NodeView) {
                NodeView nv = (NodeView) cur;
                nv.setSelectedConcepts(concepts);
            }
        }
        getController().getEventBroker().processEvent(new SelectionChangedEvent(this));
        repaint();
    }

    /**
     * Sets all object label views to use this query when asking their concepts.
     */
    public void setQuery(Query query) {
        // update the current labels
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof ObjectLabelView) {
                ObjectLabelView lv = (ObjectLabelView) cur;
                lv.setQuery(query);
            }
        }
        requestScreenTransformUpdate();
        repaint();
        // set the value for new ones
        ObjectLabelView.setDefaultQuery(query);
    }
}
