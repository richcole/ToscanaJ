package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.DrawingCanvas;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.model.diagram.NestedDiagramNode;
import net.sourceforge.toscanaj.model.diagram.NestedLineDiagram;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.view.diagram.LabelView;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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

    /**
     * Flag to prevent label from being moved when just clicked on
     */
    private boolean dragMode = false;

    /**
     * Distance that label has to be moved to enable dragMode
     */
    private int dragMin = 5;

    /**
     * The position where the mouse was when the last event came.
     */
    private Point2D lastMousePos = null;

    /**
     * Creates a new vew displaying an empty digram (i.e. nothing at all).
     */
    public DiagramView()
    {
        addMouseListener(this);
        addMouseMotionListener(this);
   }

    /**
     * Implements ChangeObserver.update(Object) by repainting the diagram.
     */
    public void update(Object source){
        if(source instanceof DiagramController) {
            showDiagram((SimpleLineDiagram)DiagramController.getController().getCurrentDiagram());
        }
        else {
            repaint();
        }
    }

    /**
     * Paints the diagram on the screen.
     */
    public void paintComponent( Graphics g )
    {
        if( diagram == null ) {
            return;
        }
        Graphics2D g2d = (Graphics2D) g;

        // draw diagram title in the top left corner
        g2d.drawString( diagram.getTitle(), this.getX() + MARGIN, this.getY() + MARGIN );

        Rectangle2D bounds = new Rectangle2D.Double( getX() + MARGIN, getY() + MARGIN,
                                                     getWidth() - 2*MARGIN, getHeight() - 2*MARGIN );

        this.scaleToFit(g2d, bounds);

        // paint all items on canvas
        paintCanvas(g2d);
    }

    /**
     * Sets the given diagram as new diagram to display.
     *
     * This will automatically cause a repaint of the view.
     */
    public void showDiagram(SimpleLineDiagram diagram) {
        this.diagram = diagram;
        clearCanvas();
        if(diagram == null) {
            repaint();
            return;
        }
        if(diagram instanceof NestedLineDiagram) {
            addDiagram((NestedLineDiagram)diagram);
        }
        else {
            addDiagram(diagram);
        }
        repaint();
    }

    /**
     * Adds a simple non-nested line diagram to the canvas.
     */
    private void addDiagram(SimpleLineDiagram diagram) {
        // add all lines to the canvas
        for( int i = 0; i < diagram.getNumberOfLines(); i++ ) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem( new LineView(dl) );
        }
        // add all nodes to the canvas
        for( int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this);
            addCanvasItem( nodeView );
        }
        // add all labels to the canvas
        for( int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this);
            LabelInfo attrLabelInfo = diagram.getAttributeLabel( i );
            if( attrLabelInfo != null ) {
                LabelView labelView = new AttributeLabelView( this, attrLabelInfo );
                addCanvasItem( labelView );
                labelView.addObserver(this);
            }
            LabelInfo objLabelInfo = diagram.getObjectLabel( i );
            if( objLabelInfo != null ) {
                LabelView labelView = new ObjectLabelView( this, objLabelInfo );
                addCanvasItem( labelView );
                labelView.addObserver(this);
            }
        }
    }

    /**
     * Adds a nested line diagram to the canvas.
     */
    private void addDiagram(NestedLineDiagram diagram) {
        // add all outer lines to the canvas
        for( int i = 0; i < diagram.getNumberOfLines(); i++ ) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem( new LineView(dl) );
        }
        // add all outer nodes to the canvas
        for( int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this);
            addCanvasItem( nodeView );
        }
        // add all outer labels to the canvas
        for( int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node, this);
            LabelInfo attrLabelInfo = diagram.getAttributeLabel( i );
            if( attrLabelInfo != null ) {
                LabelView labelView = new AttributeLabelView( this, attrLabelInfo );
                addCanvasItem( labelView );
                labelView.addObserver(this);
            }
            LabelInfo objLabelInfo = diagram.getObjectLabel( i );
            if( objLabelInfo != null ) {
                LabelView labelView = new ObjectLabelView( this, objLabelInfo );
                addCanvasItem( labelView );
                labelView.addObserver(this);
            }
        }
        // recurse for the inner diagrams
        for( int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            NestedDiagramNode node = (NestedDiagramNode)diagram.getNode(i);
            addDiagram((SimpleLineDiagram)node.getInnerDiagram());
        }
    }

    /**
     * Sets the display type on all object labels in the diagram.
     *
     * @see LabelView.setDisplayType(int, boolean)
     */
    public void setDisplayType(int type, boolean contingentOnly) {
        // change existing labels
        Iterator it = this.canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem) it.next();
            if(cur instanceof ObjectLabelView) {
                ObjectLabelView lv = (ObjectLabelView) cur;
                lv.setDisplayType(type, contingentOnly);
            }
        }
        // set new default
        ObjectLabelView.setDefaultDisplayType(type, contingentOnly);
    }

    /**
     * Toggles if object labels should display percentual distribution.
     *
     * If off only absolute numbers will be displayed when displaying numbers,
     * otherwise the percentage of the full object set will be added in
     * parentheses.
     *
     * @see LabelView.setShowPercentage(boolean)
     */
    public void setShowPercentage(boolean toggle) {
        // change existing labels
        Iterator it = this.canvasItems.iterator();
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem) it.next();
            if(cur instanceof ObjectLabelView) {
                ObjectLabelView lv = (ObjectLabelView) cur;
                lv.setShowPercentage(toggle);
            }
        }
        // set new default
        ObjectLabelView.setDefaultShowPercentage(toggle);
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
        while( it.hasNext() ) {
            CanvasItem cur = (CanvasItem) it.next();
            if(cur instanceof NodeView) {
                NodeView nv = (NodeView) cur;
                nv.setSelectedConcept(concept);
            }
            if(cur instanceof LineView) {
                LineView lv = (LineView) cur;
                lv.setSelectedConcept(concept);
            }
        }
        repaint();
    }

    /**
     * Overwrites DrawingCanvas.backgroundClicked(Point2D) to erase the highlighting.
     */
    protected void backgroundClicked(Point2D point) {
        this.setSelectedConcept(null);
    }
}