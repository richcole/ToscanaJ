package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.model.diagram.DiagramNode;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Interface for getting diagram related information.
 */

public interface Diagram2D
{
    /**
     * Returns the title of the diagram.
     */
    public String getTitle();

    /**
     * Returns the number of nodes in the diagram.
     */
    public int getNumberOfNodes();

    /**
     * Returns the number of lines in the diagram.
     */
    public int getNumberOfLines();

    /**
     * Calculates a rectangle that includes all points.
     */
    public Rectangle2D getBounds();

    /**
     * Returns a node in the diagram.
     *
     * Numbers start with zero.
     */
    public DiagramNode getNode( int nodeNumber );

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getFromPosition( int lineNumber );

    /**
     * Returns a line in the diagram.
     *
     * Numbers start with zero.
     */
    public DiagramLine getLine( int lineNumber );

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    public Point2D getToPosition( int lineNumber );

    /**
     * Returns the information on the object label of the diagram.
     */
    public LabelInfo getObjectLabel( int pointNumber );

    /**
     * Returns the information on the attribute label of the diagram.
     */
    public LabelInfo getAttributeLabel( int pointNumber );
}