/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package net.sourceforge.toscanaj.model.context;

import org.jdom.Element;

public interface FCAElement {
    Object getData();
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
    
