/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com). Please read licence.txt in
 * the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import org.jdom.Element;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.toscanaj.model.XML_Serializable;

/**
 * Interface for getting diagram related information.
 */

public interface Diagram2D extends XML_Serializable {
    static final String DIAGRAM_ELEMENT_NAME = "diagram";
    static final String TITLE_ATTRIBUTE_NAME = "title";
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
}