/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au). 
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.Canvas;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.controller.fca.DiagramHistory;
import net.sourceforge.toscanaj.controller.fca.ConceptInterpreter;
import net.sourceforge.toscanaj.model.database.Query;
import net.sourceforge.toscanaj.model.diagram.*;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObserver;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
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

    class ResizeListener extends ComponentAdapter {
        public void componentResized(ComponentEvent e) {
            requestScreenTransformUpdate();
            repaint();
        }
    }

    /**
     * Creates a new view displaying an empty digram (i.e. nothing at all).
     */
    public DiagramView(ConceptInterpreter conceptInterpreter) {
        super();
        this.conceptInterpreter = conceptInterpreter;
        addComponentListener(new ResizeListener());
        /// @todo this is not yet used since the title is not a canvas item yet
        getBackgroundItem().setPaint(DiagramSchema.getDiagramSchema().getBackgroundColor());
    }

    public void setConceptInterpreter(ConceptInterpreter conceptInterpreter) {
        this.conceptInterpreter = conceptInterpreter;
        /// @todo propagate change to parts, redraw
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
        clearCanvas();
        if (diagram == null) {
            repaint();
            return;
        }
        addDiagram(diagram, conceptInterpreter);
        requestScreenTransformUpdate();
        repaint();
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
    private void addDiagram(Diagram2D diagram, ConceptInterpreter conceptInterpreter) {
        addDiagramLinesToCanvas(diagram);
        addDiagramNodesToCanvas(diagram, conceptInterpreter);
        addDiagramLabelsToCanvas(diagram, conceptInterpreter);
    }

    private void addDiagramLabelsToCanvas(Diagram2D diagram, ConceptInterpreter conceptInterpreter) {
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            LabelInfo attrLabelInfo = diagram.getAttributeLabel(i);
            if (attrLabelInfo != null) {
                LabelView labelView = new AttributeLabelView(this, attrLabelInfo, conceptInterpreter);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
            LabelInfo objLabelInfo = diagram.getObjectLabel(i);
            if (objLabelInfo != null) {
                LabelView labelView = new ObjectLabelView(this, objLabelInfo, conceptInterpreter);
                addCanvasItem(labelView);
                labelView.addObserver(this);
            }
        }
    }

    private void addDiagramLinesToCanvas(Diagram2D diagram) {
        for (int i = 0; i < diagram.getNumberOfLines(); i++) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem(new LineView(dl));
        }
    }

    private void addDiagramNodesToCanvas(Diagram2D diagram, ConceptInterpreter conceptInterpreter) {
        for (int i = 0; i < diagram.getNumberOfNodes(); i++) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this, conceptInterpreter);
            addCanvasItem(nodeView);
            if (node instanceof NestedDiagramNode) {
                NestedDiagramNode ndNode = (NestedDiagramNode) node;
                addDiagram(ndNode.getInnerDiagram(), conceptInterpreter);
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
        requestScreenTransformUpdate();
        repaint();
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
            if (cur instanceof LineView) {
                LineView lv = (LineView) cur;
                lv.setSelectedConcepts(concepts);
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
        requestScreenTransformUpdate();
        repaint();
        // set the value for new ones
        ObjectLabelView.setDefaultQuery(query);
    }
}
