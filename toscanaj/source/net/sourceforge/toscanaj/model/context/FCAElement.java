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
 * Models an object or attribute in a formal context.
 * 
 * Objects and attributes are only distinguished by their role, they do not
 * have separate classes.
 * 
 * We do not use plain Java objects for two reasons:
 * <ol>
 * <li>we want to store the order of the objects intrinsically;</li>
 * <li>we want to attach descriptions.</li>
 * </ol>
 * 
 * FCAElements are considered equal, if the data stored is equal.
 * 
 * @see WritableFCAElement
 */
public interface FCAElement {
    /**
     * Returns the data stored in this FCAElement.
     * 
     * This is not allowed to be null.
     */
    Object getData();
    
    /**
     * Returns the description of the FCAElement.
     * 
     * The return value of this method can be null.
     */
    Element getDescription();
    
    /**
     * Return the position in a context if available.
     * 
     * The return value is either the position in the according
     * context (i.e. in the list of objects or attributes, depending
     * on the usage of this element), or -1 if no position in a 
     * context is known.
     */
    int getContextPosition();
}
    
