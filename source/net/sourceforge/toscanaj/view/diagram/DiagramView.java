package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.DrawingCanvas;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.model.Query;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * This class paints a diagram defined by the SimpleLineDiagram class.
 */
public class DiagramView extends DrawingCanvas implements ChangeObserver {
    /**
     * This is a generic margin used for all four edges.
     */
    private final int MARGIN = 20;

    /**
     * The diagram to display.
     */
    private Diagram2D diagram = null;

    class ResizeListener extends ComponentAdapter{
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
        addComponentListener(new ResizeListener());
    }

    protected void dragFinished(MouseEvent e) {
        requestScreenTransformUpdate();
    }

    private void requestScreenTransformUpdate() {
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
            repaint();
        }
    }




    private boolean screenTransformDirty = false;

    protected boolean isScreenTransformDirty(){
        return screenTransformDirty;
    }

    /**
     * Paints the diagram on the screen.
     */
    public void paintComponent(Graphics g) {
        if (diagram == null) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;

        // fill the background
        g2d.setPaint(DiagramSchema.getDiagramSchema().getBackground());
        g2d.fill(this.getBounds());

        // draw diagram title in the top left corner

        g2d.setPaint(DiagramSchema.getDiagramSchema().getForeground());
        // title is not scaled on purpose
        g2d.drawString(diagram.getTitle(), MARGIN, MARGIN);

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
    }

    protected void makeScreenTransformClear() {
        screenTransformDirty = false;
    }

    /**
     * Sets the given diagram as new diagram to display.
     *
     * This will automatically cause a repaint of the view.
     */
    public void showDiagram(SimpleLineDiagram diagram) {
        this.diagram = diagram;
        clearCanvas();
        if (diagram == null) {
            repaint();
            return;
        }
        if (diagram instanceof NestedLineDiagram) {
            addDiagram((NestedLineDiagram) diagram);
        } else {
            addDiagram(diagram, null);
        }
        requestScreenTransformUpdate();
        repaint();
    }

    /**
     * Adds a simple non-nested line diagram to the canvas.
     *
     * If the filter concept is non-null all nodes created will use this for
     * filter operations.
     */
    private void addDiagram(SimpleLineDiagram diagram, Concept filterConcept) {
        // add all lines to the canvas
        for (int i = 0; i < diagram.getNumberOfLines(); i++) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem(new LineView(dl));
        }
        // add all nodes to the canvas
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView;
            if (filterConcept == null) {
                nodeView = new NodeView(node, this);
            } else {
                nodeView = new NodeView(node, this, filterConcept);
            }
            addCanvasItem(nodeView);
        }
        // add all labels to the canvas
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            LabelInfo attrLabelInfo = diagram.getAttributeLabel(i);
            if (attrLabelInfo != null) {
                LabelView labelView = new AttributeLabelView(this, attrLabelInfo);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
            LabelInfo objLabelInfo = diagram.getObjectLabel(i);
            if (objLabelInfo != null) {
                LabelView labelView = new ObjectLabelView(this, objLabelInfo);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
        }
    }

    /**
     * Adds a nested line diagram to the canvas.
     */
    private void addDiagram(NestedLineDiagram diagram) {
        // add all outer lines to the canvas
        for (int i = 0; i < diagram.getNumberOfLines(); i++) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem(new LineView(dl));
        }
        // add all outer nodes to the canvas
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this);
            addCanvasItem(nodeView);
        }
        // recurse for the inner diagrams
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            NestedDiagramNode node = (NestedDiagramNode) diagram.getNode(i);
            addDiagram((SimpleLineDiagram) node.getInnerDiagram(), node.getConcept());
        }
        // add all outer labels to the canvas
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            LabelInfo attrLabelInfo = diagram.getAttributeLabel(i);
            if (attrLabelInfo != null) {
                LabelView labelView = new AttributeLabelView(this, attrLabelInfo);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
            LabelInfo objLabelInfo = diagram.getObjectLabel(i);
            if (objLabelInfo != null) {
                LabelView labelView = new ObjectLabelView(this, objLabelInfo);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
        }
    }

    /**
     * @see LabelView.setDisplayType(boolean)
     */
    public void setDisplayType(boolean contingentOnly) {
        // change existing labels
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof ObjectLabelView) {
                ObjectLabelView lv = (ObjectLabelView) cur;
                lv.setDisplayType(contingentOnly);
            } else if (cur instanceof AttributeLabelView) {
                AttributeLabelView lv = (AttributeLabelView) cur;
                lv.setDisplayType(true);
            }
        }
        // set new default
        ObjectLabelView.setDefaultDisplayType(contingentOnly);
    }

    /**
     * Sets the selected concept.
     *
     * The selected concept and its filter and ideal will be highlighted on
     * drawing. If the parameter is set to null highlighting will be dropped.
     */
    public void setSelectedConcept(Concept concept) {
        // notify all nodes and lines
        Iterator it = this.canvasItems.iterator();
        while (it.hasNext()) {
            CanvasItem cur = (CanvasItem) it.next();
            if (cur instanceof NodeView) {
                NodeView nv = (NodeView) cur;
                nv.setSelectedConcept(concept);
            }
            if (cur instanceof LineView) {
                LineView lv = (LineView) cur;
                lv.setSelectedConcept(concept);
            }
        }
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
        repaint();
        // set the value for new ones
        ObjectLabelView.setDefaultQuery(query);
    }

    /**
     * Overwrites DrawingCanvas.backgroundClicked(Point2D) to erase the highlighting.
     */
    protected void backgroundClicked(Point2D point) {
        this.setSelectedConcept(null);
    }
}