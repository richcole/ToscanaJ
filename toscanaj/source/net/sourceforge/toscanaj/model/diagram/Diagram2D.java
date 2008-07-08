/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import net.sourceforge.toscanaj.model.lattice.Concept;
import net.sourceforge.toscanaj.util.xmlize.XMLizable;

import org.jdom.Element;
import org.tockit.events.EventBroker;

/**
 * Interface for getting diagram related information.
 * 
 * @todo remove index based access, even though this means giving write access
 *       in some way
 * @todo make interface smaller
 * @todo remove constants from interface
 */

public interface Diagram2D<O, A> extends XMLizable {
    static final String DIAGRAM_ELEMENT_NAME = "diagram";
    static final String TITLE_ATTRIBUTE_NAME = "title";
    static final String DESCRIPTION_ELEMENT_NAME = "description";
    static final String EXTRA_CANVAS_ITEMS_ELEMENT_NAME = "extraCanvasItems";

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
    DiagramNode<O, A> getNode(int nodeNumber);

    DiagramNode<O, A> getNode(String identifier);

    /**
     * Finds the node representing the concept given.
     * 
     * Returns null if no such node exists in this diagram.
     */
    DiagramNode<O, A> getNodeForConcept(Concept<O, A> concept);

    /**
     * Returns the list of all nodes in the diagram.
     */
    Iterator<DiagramNode<O, A>> getNodes();

    Iterator<DiagramLine<O, A>> getLines();

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
    DiagramLine<O, A> getLine(int lineNumber);

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
     * Returns the XML Element describing the diagram (if available, null
     * otherwise).
     */
    Element getDescription();

    /**
     * Returns true if the diagram is layed out as Hasse diagram.
     * 
     * This means every line is pointing downwards or in other terms: every node
     * is located above all nodes that represent a concept lesser than its
     * concept.
     */
    boolean isHasseDiagram();

    /**
     * Finds the top concept in the underlying lattice.
     * 
     * @todo check where this might be used instead of doing the same thing
     *       multiple times
     * 
     * @throws IllegalStateException
     *             if the diagram has no nodes or is not a lattice.
     */
    Concept<O, A> getTopConcept();

    /**
     * Finds the bottom concept in the underlying lattice.
     * 
     * @todo check where this might be used instead of doing the same thing
     *       multiple times
     * 
     * @throws IllegalStateException
     *             if the diagram has no nodes or is not a lattice.
     */
    Concept<O, A> getBottomConcept();

    /**
     * Returns the event broker this diagram sends change events to.
     * 
     * @return The target event broker for the diagram or null if none is
     *         available.
     */
    EventBroker getEventBroker();
}
