package net.sourceforge.toscanaj.model.diagram;

import java.awt.geom.Point2D;


/**
 * This class is an abstraction of all diagram related information.
 */

public interface WriteableDiagram2D extends Diagram2D
{

   /**
     * Change the title of the diagram.
     */
    public void setTitle( String title );

    /**
     * Adds a point to the diagram (at the end of the list).
     */
    public void addPoint( Point2D point );

    /**
     * Adds a line to the diagram (at the end of the list).
     *
     * The from and to parameters are assumed to refer to some points already
     * existing in the points list.
     */
    public void addLine( int from, int to );
}
