package net.sourceforge.toscanaj.view.diagram;

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

import net.sourceforge.toscanaj.canvas.CanvasItem;
import net.sourceforge.toscanaj.canvas.DrawingCanvas;
import net.sourceforge.toscanaj.model.diagram.DiagramNode;
import net.sourceforge.toscanaj.model.diagram.SimpleLineDiagram;
import net.sourceforge.toscanaj.model.diagram.DiagramLine;
import net.sourceforge.toscanaj.model.diagram.Diagram2D;
import net.sourceforge.toscanaj.model.diagram.LabelInfo;
import net.sourceforge.toscanaj.view.diagram.LabelView;
import net.sourceforge.toscanaj.view.diagram.ToscanajGraphics2D;


/**
 * This class paints a diagram defined by the SimpleLineDiagram class.
 */

public class DiagramView extends DrawingCanvas implements DiagramObserver
{
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
    private Diagram2D _diagram = null;

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
    * method to notify observer that a change has been made
    */
    public void diagramChanged(){
      repaint();
    }

    /**
    * Method called by LabelView to update all observers
    */
    public void updateAllObservers() {
      ((SimpleLineDiagram)_diagram).emitChangeSignal();
    }

    /**
     * Paints the diagram on the screen.
     */
    public void paintComponent( Graphics g )
    {
        if( _diagram == null ) {
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
        g2d.drawString( _diagram.getTitle(), x - MARGIN/2, y - MARGIN/2 );

        // get the dimensions of the diagram
        Rectangle2D diagBounds = _diagram.getBounds();

        // check if the y-coordinate in the file is denoted in math-style, i.e.
        // up is positive
        // invY is one unless this is the case, then invY will be minus one
        // invY has to be used wherever Y coordinates from the diagram are used
        // in combination with absolute Y coordinates like e.g. the predefined
        // RADIUS of the points
        int invY = 1;
        if( _diagram.getNumberOfNodes() > 0 ) {
            if( diagBounds.getY() < _diagram.getNode(0).getPosition().getY() ) {
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
        if( diagBounds.getHeight() != 0 && _diagram.getNumberOfNodes() != 0 )
        {
            yScale = h / diagBounds.getHeight() * invY;
            yOrigin = y - _diagram.getNode(0).getPosition().getY() * yScale;
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
        _diagram = diagram;
        ((SimpleLineDiagram)_diagram).addObserver(this);
         newCanvasItemsList();
        // add all lines to the canvas
        for( int i = 0; i < _diagram.getNumberOfLines(); i++ ) {
            DiagramLine dl = _diagram.getLine(i);
            addCanvasItem( new LineView(dl) );
        }
        // add all points and labels to the canvas
        for( int i = 0; i < _diagram.getNumberOfNodes(); i++ ) {
            DiagramNode node = _diagram.getNode(i);
            NodeView nodeView = new NodeView(node);
            addCanvasItem( nodeView );
            LabelInfo attrLabelInfo = _diagram.getAttributeLabel( i );
            if( attrLabelInfo != null ) {
                addCanvasItem( new LabelView( this, LabelView.ABOVE, attrLabelInfo ) );
            }
            LabelInfo objLabelInfo = _diagram.getObjectLabel( i );
            if( objLabelInfo != null ) {
                addCanvasItem( new LabelView( this, LabelView.BELOW, objLabelInfo ) );
            }
        }
        repaint();
    }
}