package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.diagram.DiagramObservable;
import net.sourceforge.toscanaj.view.diagram.DiagramObserver;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

/**
 * This class is an abstraction of all diagram related information.
 */

public class SimpleLineDiagram implements DiagramObservable, Diagram2D
{
    /**
     * The list of DiagramObserver implementations currently observing changes.
     */
    private List diagramObserver = null;

    /**
     * The title used for this diagram.
     */
    private String title;

    /**
     * The list of nodes in the diagram.
     */
    private List nodes;

    /**
     * The list of lines in the diagram.
     */
    private List lines;

    /**
     * The default constructor creates a diagram with just nothing in it at all.
     */
    public SimpleLineDiagram() {
        title = "";
        nodes = new LinkedList();
        lines = new LinkedList();
        diagramObserver = new LinkedList();
    }

    /**
     * Method to add observer
     */
    public void addObserver(DiagramObserver observer) {
        this.diagramObserver.add(observer);
    }

    /**
     * Send to all obvservers that a change has been made
     */
    public void emitChangeSignal() {
        Iterator iterator = diagramObserver.iterator();
        while(iterator.hasNext()){
            ((DiagramObserver)iterator.next()).diagramChanged();
        }
    }

    /**
     * Returns the title of the diagram.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Change the title of the diagram.
     */
    public void setTitle( String title ) {
        this.title = title;
    }

    /**
     * Returns the number of nodes in the diagram.
     */
    public int getNumberOfNodes() {
        return this.nodes.size();
    }

    /**
     * Returns the number of lines in the diagram.
     */
    public int getNumberOfLines() {
        return this.lines.size();
    }

    /**
     * Calculates a rectangle that includes all points.
     */
    public Rectangle2D getBounds() {
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for( int i = 0; i < this.nodes.size(); i++ ) {
            Point2D p = ((DiagramNode)this.nodes.get( i )).getPosition();
            double x = p.getX();
            double y = p.getY();

            if( x < minX ) {
                minX = x;
            }
            if( x > maxX ) {
                maxX = x;
            }
            if( y < minY ) {
                minY = y;
            }
            if( y > maxY ) {
                maxY = y;
            }
        }
        return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
    }

    /**
     * Returns a node in the diagram.
     *
     * Numbers start with zero.
     */
    public DiagramNode getNode( int nodeNumber ) {
        return (DiagramNode)this.nodes.get(nodeNumber);
    }

    /**
     * Implements Diagram2D.getLine(int).
     */
    public DiagramLine getLine( int lineNumber ) {
        return (DiagramLine)this.lines.get(lineNumber);
    }

    /**
     * Adds a node to the diagram (at the end of the list).
     */
    public void addNode(DiagramNode node) {
        this.nodes.add(node);
    }

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPosition( int lineNumber ) {
        DiagramLine line = (DiagramLine)this.lines.get(lineNumber);
        return line.getFromPosition();
    }

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPosition( int lineNumber ) {
        DiagramLine line = (DiagramLine)this.lines.get(lineNumber);
        return line.getToPosition();
    }

    /**
     * Adds a line to the diagram (at the end of the list).
     */
    public void addLine( DiagramNode from, DiagramNode to ) {
        this.lines.add( new DiagramLine( from, to ) );
    }

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel( int nodeNumber ) {
        return ((DiagramNode)this.nodes.get(nodeNumber)).getObjectLabelInfo();
    }

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel( int nodeNumber ) {
        return ((DiagramNode)this.nodes.get(nodeNumber)).getAttributeLabelInfo();
    }
}