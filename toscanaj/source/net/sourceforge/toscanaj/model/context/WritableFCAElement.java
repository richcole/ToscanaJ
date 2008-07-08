/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.context;

import org.jdom.Element;

/**
 * This interface is a writable version of FCAElement.
 */
public interface WritableFCAElement extends FCAElement {
    /**
     * Changes the data stored in this FCAElement.
     * 
     * Note that this most likely changes the identity of the object.
     * 
     * @param data
     *            The new data, must not be null.
     */
    void setData(Object data);

    /**
     * Sets a description for the FCAElement.
     * 
     * @param description
     *            The new description, can be null.
     */
    void setDescription(Element description);

    /**
     * Sets an index within a context.
     * 
     * This method can be used to add an order to the FCAElements in a context.
     * 
     * @param contextPosition
     *            The position of the object or attribute in the context or -1
     *            if there is no such order.
     */
    void setContextPosition(int contextPosition);
}
