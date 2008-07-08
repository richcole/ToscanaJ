/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.diagram;

import org.jdom.Element;
import org.tockit.events.EventBroker;

/**
 * This class is an abstraction of all diagram related information.
 */

public interface WriteableDiagram2D<O, A> extends Diagram2D<O, A> {

    /**
     * Change the title of the diagram.
     */
    void setTitle(String title);

    /**
     * Gives the diagram a description.
     */
    void setDescription(Element descriptionXML);

    /**
     * Adds a point to the diagram (at the end of the list).
     */
    void addNode(DiagramNode<O, A> node);

    /**
     * Adds a line to the diagram (at the end of the list).
     * 
     * The from and to parameters are assumed to refer to some points already
     * existing in the points list.
     */
    void addLine(DiagramNode<O, A> from, DiagramNode<O, A> to);

    /**
     * Sets an event broker to send DiagramChangeEvents to.
     */
    void setEventBroker(EventBroker eventBroker);

    /**
     * Sends a notification about a change into the broker attached.
     * 
     * This is a null-operation if the broker is not set.
     */
    void sendChangeEvent();
}
