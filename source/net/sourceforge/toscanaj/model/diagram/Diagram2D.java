/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import net.sourceforge.toscanaj.util.xmlize.XMLizable;
import org.jdom.Element;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

/**
 * Interface for getting diagram related information.
 *
 * @todo remove index based access, even though this means giving write access in some way
 */

public interface Diagram2D extends XMLizable {
    static final String DIAGRAM_ELEMENT_NAME = "diagram";
    static final String TITLE_ATTRIBUTE_NAME = "title";
    static final String DESCRIPTION_ELEMENT_NAME = "description";
    
    static final double MINIMUM_STEEPNESS = 0.1;

    /**
     * Returns the title of the diagram.
     */
    String getTitle();

    /**
     * Returns the number of nodes in the diagram.
     */
    int getNumberOfNodes();

    /**
     * Returns the number of lines in the diagram.
     */
    int getNumberOfLines();

    /**
     * Calculates a rectangle that includes all points.
     */
    Rectangle2D getBounds();

    /**
     * Returns a node in the diagram.
     *
     * Numbers start with zero.
     */
    DiagramNode getNode(int nodeNumber);

    DiagramNode getNode(String identifier);

    /**
     * Returns the list of all nodes in the diagram.
     */
    Iterator getNodes();

    Iterator getLines();

    /**
     * Returns the coordinates of a starting point of a line.
     *
     * Numbers start with zero.
     */
    Point2D getFromPosition(int lineNumber);

    /**
     * Returns a line in the diagram.
     *
     * Numbers start with zero.
     */
    DiagramLine getLine(int lineNumber);

    /**
     * Returns the coordinates of an end point of a line.
     *
     * Numbers start with zero.
     */
    Point2D getToPosition(int lineNumber);

    /**
     * Returns the information on the object label of the diagram.
     */
    LabelInfo getObjectLabel(int pointNumber);

    /**
     * Returns the information on the attribute label of the diagram.
     */
    LabelInfo getAttributeLabel(int pointNumber);

    /**
     * Returns the XML Element describing the diagram (if available, null otherwise).
     */
    Element getDescription();
    
    /**
     * Returns true if the diagram is layouted as Hasse diagram.
     * 
     * This means every line is pointing downwards or in other terms: every node
     * is located above all nodes that represent a concept lesser than its
     * concept.
     */
    boolean isHasseDiagram();
}
