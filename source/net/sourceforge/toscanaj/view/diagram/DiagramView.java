package net.sourceforge.toscanaj.view.diagram;

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.DrawingCanvas;
import net.sourceforge.toscanaj.controller.fca.DiagramController;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.observer.ChangeObservable;
import net.sourceforge.toscanaj.observer.ChangeObserver;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;

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
     * The minimum size for drawing.
     *
     * If this size for a diagram is not reached (in width or height), the digram
     * will not be drawn at all.
     */
    private final int MINDIAGRAMSIZE=50;

    /**
     * This is a generic margin used for all four edges.
     *
     * The margin should be big enough to allow a RADIUS to lap over.
     */
    private final int MARGIN = 80;

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

        // calculate paintable area
        Insets insets = getInsets();
        int x = getX() + insets.left + MARGIN;
        int y = getY() + insets.top + MARGIN;
        int h = getHeight() - insets.left - insets.right - 2 * MARGIN;
        int w = getWidth() - insets.top - insets.bottom - 2 * MARGIN;

        // check if there is enough left to paint on, otherwise abort
        if( ( h < MINDIAGRAMSIZE ) || ( w < MINDIAGRAMSIZE ) ) {
            return;
        }

        // draw diagram title in the top left corner
        g2d.drawString( diagram.getTitle(), x - MARGIN/2, y - MARGIN/2 );

        // get the dimensions of the diagram
        Rectangle2D diagBounds = diagram.getBounds();

        // check if the y-coordinate in the file is denoted in math-style, i.e.
        // up is positive
        // invY is one unless this is the case, then invY will be minus one
        // invY has to be used wherever Y coordinates from the diagram are used
        // in combination with absolute Y coordinates like e.g. the predefined
        // RADIUS of the points
        int invY = 1;
        if( diagram.getNumberOfNodes() > 0 ) {
            if( diagBounds.getY() < diagram.getNode(0).getPosition().getY() ) {
                invY = -1;
            }
        }
        // we need some values to do the projection -- the initial values are
        // centered and no size change. This is useful if the diagram has no
        // extent in a direction
        double xOrigin = x + w/2;
        double yOrigin = y + h/2;
        double xScale = 1;
        double yScale = 1;

        // adjust the scaling values if the diagram has extent in a dimension
        // and move the top/left edge of the diagram to the top/left edge of
        // the painting area. If the diagram has no extent in one direction
        // there will be no scaling and it will be placed centered
        /** @TODO change this to calculate the lable sizes/offsets into the
         *  scaling */
        if( diagBounds.getWidth() != 0 )
        {
            xScale = w / diagBounds.getWidth();
            xOrigin = x - diagBounds.getX() * xScale;
        }
        if( diagBounds.getHeight() != 0 && diagram.getNumberOfNodes() != 0 )
        {
            yScale = h / diagBounds.getHeight() * invY;
            yOrigin = y - diagram.getNode(0).getPosition().getY() * yScale;
        }
        //store updated ToscanajGraphics2D
        graphics = new ToscanajGraphics2D(g2d, new Point2D.Double( xOrigin, yOrigin ), xScale, yScale );
        // paint all items on canvas
        paintCanvasItems(graphics);
    }

    /**
     * Sets the given diagram as new diagram to display.
     *
     * This will automatically cause a repaint of the view.
     */
    public void showDiagram(SimpleLineDiagram diagram ) {
        this.diagram = diagram;
        clearCanvas();
        if(diagram == null) {
            repaint();
            return;
        }
        // add all lines to the canvas
        for( int i = 0; i < diagram.getNumberOfLines(); i++ ) {
            DiagramLine dl = diagram.getLine(i);
            addCanvasItem( new LineView(dl) );
        }
        // add all nodes to the canvas
        for( int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node);
            addCanvasItem( nodeView );
        }
        // add all labels to the canvas
        for( int i = 0; i < diagram.getNumberOfNodes(); i++ ) {
            DiagramNode node = diagram.getNode(i);
            NodeView nodeView = new NodeView(node);
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
        repaint();
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
}